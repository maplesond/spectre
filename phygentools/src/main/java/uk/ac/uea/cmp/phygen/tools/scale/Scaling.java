/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.uea.cmp.phygen.tools.scale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetNetwork;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.core.math.tuple.Key;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;
import uk.ac.uea.cmp.phygen.tools.chopper.Chopper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@MetaInfServices
public class Scaling extends PhygenTool {

    private static Logger log = LoggerFactory.getLogger(Scaling.class);

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_PREFIX = "output";
    private static final String OPT_MODE = "mode";
    private static final String OPT_OPTIMISER = "optimiser";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The output prefix path, which will be used for all output files").create("o"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT_FILE).hasArg()
                .withDescription("The input file containing the tree to be scaled").create("i"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_MODE).hasArg()
                .withDescription("The output file type: " + Mode.listTypes()).create("t"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription("The optimiser to use: " + OptimiserFactory.getInstance().listOperationalOptimisers()).create("p"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        // All options are required
        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT_FILE));
        File outputPrefix = new File(commandLine.getOptionValue(OPT_OUTPUT_PREFIX));
        Mode mode = Mode.valueOf(commandLine.getOptionValue(OPT_MODE).toUpperCase());
        Optimiser optimiser = null;

        try {
            optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(
                            commandLine.getOptionValue(OPT_OPTIMISER), Objective.ObjectiveType.QUADRATIC) :
                    null;
        } catch (OptimiserException oe) {
            throw new IOException(oe);
        }

        try {
            this.execute(inputFile, outputPrefix, mode, optimiser);
        } catch (OptimiserException oe) {
            throw new IOException(oe);
        }
    }

    @Override
    public String getName() {
        return "scaler";
    }

    @Override
    public String getDescription() {
        return "Scales input trees to a given range based on their quartet weights (uses \"chopper\" to get the quartet weights from the trees)";
    }

    public void execute(File inputFile, File outputPrefix, Mode mode, Optimiser optimiser) throws OptimiserException, IOException {

        // Split each tree found in the input into a separate quartet network and save network to file
        int ntrees = this.createQuartetFiles(inputFile, outputPrefix, mode);

        // Computes the matrix of coefficients
        double[][] h = computerMatrix(outputPrefix.getPath(), ntrees);

        // Create the problem from the coefficients and run the solver to get the optimal solution
        Solution solution = this.optimise(optimiser, h);

        // Updates quartet weights and writes them into a file for each input tree one quartet file is generated
        updateQuartetWeights(outputPrefix.getParent(), outputPrefix.getName(), solution.getVariableValues());
    }

    private Solution optimise(Optimiser optimiser, double[][] h) throws OptimiserException {

        List<Variable> variables = this.createVariables(h.length);
        List<Constraint> constraints = this.createConstraints(variables, h);
        Objective objective = this.createObjective(variables, h);

        Problem problem = new Problem("scaling", variables, constraints, objective);

        // Run the solver on the problem and return the result
        return optimiser.optimise(problem);
    }

    private Objective createObjective(List<Variable> variables, double[][] h) {

        Expression expr = new Expression();

        for (int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                expr.addTerm(h[j][i], variables.get(j), variables.get(i));
            }
        }

        for(int i = 0; i < variables.size(); i++) {
            expr.addTerm(1.0, variables.get(i));
        }

        return new Objective("scaling", Objective.ObjectiveDirection.MINIMISE, expr);
    }

    private List<Constraint> createConstraints(List<Variable> variables, double[][] h) {

        List<Constraint> constraints = new ArrayList<>(variables.size());

        for (Variable var : variables) {
            Expression expr = new Expression().addTerm(1.0, var);
            constraints.add(new Constraint("c0", expr, Constraint.Relation.EQUAL, 0.0));
        }

        return constraints;
    }

    protected List<Variable> createVariables(final int size) {

        List<Variable> variables = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            variables.add(new Variable(
                    "x" + i,                                    // Name
                    new Bounds(0.0, Bounds.BoundType.LOWER),    // Bounds
                    Variable.VariableType.CONTINUOUS            // Type
            ));
        }

        return variables;
    }


    public static void run(File inputFile, File outputPrefix, Mode mode, Optimiser optimiser) throws OptimiserException, IOException {
        new Scaling().execute(inputFile, outputPrefix, mode, optimiser);
    }

    /**
     * Generating the quartet files
     * @param inputFile
     * @param outputPrefix
     * @param mode
     * @return Number of trees.
     * @throws IOException
     */
    protected int createQuartetFiles(File inputFile, File outputPrefix, Mode mode) throws IOException {

        //number of trees to be determined
        int ntrees = 0;

        // Get all the input from the file
        List<String> lines = FileUtils.readLines(inputFile);

        // Turn trees into collections of quartets
        for (int i = 0; i < ntrees; i++) {

            String line = lines.get(i).trim();

            if (!line.isEmpty()) {

                ntrees++;

                if (mode == Mode.NEWICK) {

                    NewickTree tree = new NewickTree(lines.get(i));

                    QuartetNetwork quartetNetwork = new Chopper().execute(tree).create();

                    quartetNetwork.saveQuartets(new File(outputPrefix.getParentFile(), outputPrefix.getName() + ntrees + ".qua"));
                }
                else if (mode == Mode.SCRIPT) {

                    String[] typeAndName = line.split(" ");
                    String type = typeAndName[0];
                    String name = typeAndName[1];

                    Chopper.run(
                            new File(inputFile.getParentFile(), name),
                            type,
                            outputPrefix.getParentFile(),
                            outputPrefix.getName() + ntrees + ".qua");
                }
            }
        }

        return ntrees;
    }




    /**
     * ********************************************
     * Code for computing the matrix of coefficients
     * *********************************************
     */

    /**
     * Extract the three weights from a line in the quartet file
     * @param line string containing the line in the quartet file
     * @return The quartet weights.
     */
    protected QuartetWeights extractWeights(String line) {

        //auxiliary variable used to store indices in the input string
        int indx = 0;

        //subtring of input line
        String part = null;

        indx = line.indexOf(';');
        part = line.substring(34, indx);

        //part now contains the part with the three weights
        //System.out.println("Part: " + part);
        indx = part.indexOf(' ');
        double w1 = Double.parseDouble((part.substring(0, indx)).trim());
        part = (part.substring(indx)).trim();
        //System.out.println("Part: " + part);
        indx = part.indexOf(' ');
        double w2 = Double.parseDouble((part.substring(0, indx)).trim());
        part = (part.substring(indx)).trim();
        double w3 = Double.parseDouble(part);

        return new QuartetWeights(w1, w2, w3);
    }

    /**
     * Store the quartets associated with a tree in a HashMap
     * @param lnr file handle
     * @param ntaxa number of taxa in tree
     * @return A hashmap of Keys to Quartet Weights.
     */
    protected HashMap<Key, QuartetWeights> getQuartets(LineNumberReader lnr, int ntaxa) {
        //upper bound on the number of different 4-subsets
        //used to set initial capacity of HashMap
        int size = ntaxa * (ntaxa - 1) * (ntaxa - 2) * (ntaxa - 3) / (3 * 4);

        //new TreeSet for the quartets
        HashMap<Key, QuartetWeights> quart = new HashMap<>(size);

        //loop variables
        int t1 = 0;
        int t2 = 0;
        int t3 = 0;
        int t4 = 0;

        //auxiliary variable used to store the
        //currently read line
        String line = null;

        //Now loop over the taxa so that we get all
        //4-subsets lexicographically ordered
        for (t1 = 0; t1 < (ntaxa - 3); t1++) {
            for (t2 = t1 + 1; t2 < (ntaxa - 2); t2++) {
                for (t3 = t2 + 1; t3 < (ntaxa - 1); t3++) {
                    for (t4 = t3 + 1; t4 < ntaxa; t4++) {
                        try {
                            line = lnr.readLine();
                        } catch (IOException exception) {
                            log.error("Error while reading from file");
                        }
                        quart.put(new Key(t1, t2, t3, t4), extractWeights(line));
                    }
                }
            }
        }
        return quart;
    }

    /**
     * Computes an array in which at position k we find the index of taxon k in tree i
     * @param taxalisti array with taxanames for tree i sorted increasingly by indices of taxa
     * @param taxalistj array with taxanames for tree j sorted increasingly by indices of taxa
     * @return Translated taxa indicies
     */
    protected int[] translateIndices(String[] taxalisti, String[] taxalistj) {
        //loop variables
        int k = 0;
        int l = 0;

        //array in which the translation is stored
        int[] trans = new int[taxalistj.length];

        //loop throuph lists of taxa
        for (k = 0; k < taxalistj.length; k++) {
            //assume at the beginning that the taxon
            //with index k in tree j does not occur in
            //tree i
            trans[k] = -1;
            for (l = 0; l < taxalisti.length; l++) {
                if (taxalistj[k].compareTo(taxalisti[l]) == 0) {
                    trans[k] = l;
                    break;
                }
            }
        }
        return trans;
    }


    /**
     * Read the list of taxa from a quartet file.  Recall that the taxa are written in separate lines (line 2 up to line
     * (ntaxa+1))
     * @param lnr file handle
     * @param ntaxa number of taxa
     * @return List of taxa
     */
    protected String[] getTaxaList(LineNumberReader lnr, int ntaxa) {
        //array to store taxanames
        String[] taxalist = new String[ntaxa];

        //auxiliary variable to store a line from the quartet file
        String line = null;

        //skip two intermediate lines in quartet file
        try {
            line = lnr.readLine();
            line = lnr.readLine();
        } catch (IOException exception) {
            log.error("Error while reading from file");
        }

        //loop variable
        int i = 0;

        //loop through lines 2 up to (ntaxa+1) of quartet file
        for (i = 0; i < ntaxa; i++) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                log.error("Error while reading from file");
            }
            int indx = line.indexOf(';');
            taxalist[i] = line.substring(21, indx);
        }
        return taxalist;
    }

    protected int getNTaxa(LineNumberReader lnr) {
        String line = null;

        try {
            line = lnr.readLine();
        } catch (IOException exception) {
            log.error("Error while reading from file");
        }

        int indx = line.indexOf(';');
        return Integer.parseInt(line.substring(12, indx));
    }

    /**
     * Computes the contribution of tree number j to the diagonal element at position (i,i)
     * @param prefix common prefix of filenames with input
     * @param itree index of tree for which we compute the diagonal element
     * @param ntaxai number of taxa in tree with with index i
     * @param taxalisti
     * @param quarti
     * @param jtree the other tree
     * @return Sum of the diagonals
     * @throws IOException
     */
    protected double sumUpDiagonal(String prefix, int itree, int ntaxai,
                                        String[] taxalisti, HashMap quarti, int jtree) throws IOException {
        //auxilliary variable to store intermediate
        //results when summing up the values for the
        //coefficient
        double part = 0.0;

        //open file for the tree with index j
        LineNumberReader lnrj = new LineNumberReader(new FileReader(prefix + (jtree + 1) + ".qua"));

        //read out the number of taxa in tree with index j
        int ntaxaj = getNTaxa(lnrj);
        //System.out.println("ntaxaj: " + ntaxaj);

        //read list of taxanames for tree with index j
        String[] taxalistj = getTaxaList(lnrj, ntaxaj);
        //printStringArray(taxalistj);

        //get an array that translates the indices of taxa
        //in tree j to the indeices of those taxa in tree
        //number i
        int[] transind = translateIndices(taxalisti, taxalistj);
        //printIntArray(transind);

        //find common quartets and sum up squares of weights

        //loop variables
        int t1 = 0;
        int t2 = 0;
        int t3 = 0;
        int t4 = 0;

        //auxiliary variable used to store the
        //currently read line
        String line = null;

        //auxiliary variables used to store the weights of the
        //quartets associated with the current line and, if
        //it exists, in tree i
        QuartetWeights wvj = null;
        QuartetWeights wvi = null;

        //Now loop over the taxa so that we get all
        //4-subsets lexicographically ordered
        for (t1 = 0; t1 < (ntaxaj - 3); t1++) {
            for (t2 = t1 + 1; t2 < (ntaxaj - 2); t2++) {
                for (t3 = t2 + 1; t3 < (ntaxaj - 1); t3++) {
                    for (t4 = t3 + 1; t4 < ntaxaj; t4++) {
                        try {
                            line = lnrj.readLine();
                        } catch (IOException exception) {
                            log.error("Error while reading from file");
                        }
                        //System.out.println("quartets on: " + (t1+1) + " " + (t2+1) + " " + (t3+1) + " " + (t4+1));
                        wvj = extractWeights(line);
                        //wvj.printWeights();
                        //System.out.println("other quartets on: " + (transind[t1]+1) + " " + (transind[t2]+1) + " " + (transind[t3]+1) + " " + (transind[t4]+1));
                        //need to sort indices after translation, done in
                        //constructor of Key
                        wvi = (QuartetWeights) quarti.get(new Key(transind[t1], transind[t2], transind[t3], transind[t4]));
                        if (wvi != null) {
                            //wvi.printWeights();
                            wvi.permute(transind[t1], transind[t2], transind[t3], transind[t4]);
                            //wvi.printWeights();
                            if (wvi.scalarProduct(wvj) != 0.0) {
                                //System.out.println("common quartet found");
                                part = part + wvi.squaredLength();
                            }
                        }
                    }
                }
            }
        }

        //close the file for the tree with index j
        lnrj.close();

        return part;
    }

    /**
     * Computes the diagonal elements of the coefficient matrix
     * @param prefix common prefix of filenames with input
     * @param itree index of tree for which we compute the diagonal element
     * @param ntrees total number of trees in input
     * @return Value of the diagonal element
     * @throws IOException
     */
    protected double computeDiagonalElement(String prefix, int itree, int ntrees) throws IOException {
        //loop variable
        int j = 0;

        //auxilliary variable to store intermediate
        //results when summing up the values for the
        //coefficient
        double coeff = 0.0;

        //open file for the tree with index i
        LineNumberReader lnri = new LineNumberReader(new FileReader(prefix + (itree + 1) + ".qua"));

        //read out the number of taxa in tree with index i
        int ntaxai = getNTaxa(lnri);
        //System.out.println("ntaxai: " + ntaxai);

        //read list of taxa and store them in an array
        String[] taxalisti = getTaxaList(lnri, ntaxai);
        //printStringArray(taxalisti);

        //create a hash map describing the set of
        //quartets for tree i
        HashMap quarti = getQuartets(lnri, ntaxai);
        //System.out.println("hash map constructed. size: " + quarti.size());

        //close file for the tree with index i
        lnri.close();

        //loop through the trees with index j distinct from i
        for (j = 0; j < ntrees; j++) {
            if (j != itree) {
                coeff = coeff + sumUpDiagonal(prefix, itree, ntaxai, taxalisti, quarti, j);
            }
        }

        return coeff;
    }

    /**
     * Computes entry at position (i,j), i<j
     * @param prefix common prefix of file names with input
     * @param itree index of first tree
     * @param jtree index of second tree
     * @return Value of the off diagonal element
     * @throws IOException
     */
    protected double computeOffDiagonalElement(String prefix, int itree, int jtree) throws IOException {

        //auxiliary variable used to store intermediate results
        double coeff = 0.0;

        //open file for each tree
        LineNumberReader lnri = new LineNumberReader(new FileReader(prefix + (itree + 1) + ".qua"));
        LineNumberReader lnrj = new LineNumberReader(new FileReader(prefix + (jtree + 1) + ".qua"));

        //read out the number of taxa in each tree
        int ntaxai = getNTaxa(lnri);
        int ntaxaj = getNTaxa(lnrj);

        //read list of taxa and store them in an array
        String[] taxalisti = getTaxaList(lnri, ntaxai);
        String[] taxalistj = getTaxaList(lnrj, ntaxaj);

        //create a hash map describing the set of
        //quartets for tree i
        HashMap quarti = getQuartets(lnri, ntaxai);

        //close file for the tree with index i
        lnri.close();

        //get an array that translates the indices of taxa
        //in tree j to the indeices of those taxa in tree
        //number i
        int[] transind = translateIndices(taxalisti, taxalistj);

        //find common quartets and sum up the products of weights

        //auxiliary variable used to store the
        //currently read line
        String line = null;

        //auxiliary variables used to store the weights of the
        //quartets associated with the current line and, if
        //it exists, in tree i
        QuartetWeights wvj = null;
        QuartetWeights wvi = null;

        //Now loop over the taxa so that we get all
        //4-subsets lexicographically ordered
        for (int t1 = 0; t1 < (ntaxaj - 3); t1++) {
            for (int t2 = t1 + 1; t2 < (ntaxaj - 2); t2++) {
                for (int t3 = t2 + 1; t3 < (ntaxaj - 1); t3++) {
                    for (int t4 = t3 + 1; t4 < ntaxaj; t4++) {
                        try {
                            line = lnrj.readLine();
                        } catch (IOException exception) {
                            log.error("Error while reading from file");
                        }

                        wvj = extractWeights(line);

                        //need to sort indices after translation, best in
                        //constructor of Key
                        wvi = (QuartetWeights) quarti.get(new Key(transind[t1], transind[t2], transind[t3], transind[t4]));
                        if (wvi != null) {
                            //wvi.printWeights();
                            wvi.permute(transind[t1], transind[t2], transind[t3], transind[t4]);
                            //wvi.printWeights();
                            coeff = coeff + wvi.scalarProduct(wvj);
                        }
                    }
                }
            }
        }

        //close the file for the tree with index j
        lnrj.close();

        return (-coeff);
    }

    /**
     * Compute the matrix of coefficients
     * @param prefix
     * @param ntrees
     * @return A matrix of coefficients.
     * @throws IOException
     */
    protected double[][] computerMatrix(String prefix, int ntrees) throws IOException {

        // Matrix of coefficients to be computed
        double[][] h = new double[ntrees][ntrees];

        // Compute diagonal elements of coefficient matrix
        for (int i = 0; i < ntrees; i++) {
            h[i][i] = computeDiagonalElement(prefix, i, ntrees);
        }

        // Compute non-diagonal elements of coefficient matrix
        for (int i = 0; i < (ntrees - 1); i++) {
            for (int j = i + 1; j < ntrees; j++) {
                h[i][j] = computeOffDiagonalElement(prefix, i, j);
                h[j][i] = h[i][j];
            }
        }

        return h;
    }

    protected String getNewInput(String input) {
        String path = "";
//        String filename = "";
        String prefix = "";
        if (input.contains("/")) {
            int cutIdx = input.lastIndexOf("/");
            path = input.substring(0, cutIdx + 1);
//            filename = input.substring(cutIdx + 1);
            int cutIdx2 = input.lastIndexOf(".");
            prefix = "scaled" + input.substring(cutIdx + 1, cutIdx2);
        } else {
//            filename = input;
            int cutIdx2 = input.lastIndexOf(".");
            prefix = "scaled" + input.substring(0, cutIdx2);
        }

        input = path + prefix + ".script";
        return input;
    }

    public static enum Mode {

        SCRIPT,
        NEWICK;

        public static String listTypes() {
            List<String> typeStrings = new ArrayList<String>();

            for (Mode m : Mode.values()) {
                typeStrings.add(m.name());
            }

            return "[" + StringUtils.join(typeStrings, ", ") + "]";
        }
    }


    /**
     * Updates the quartet weights in the new input files (scales the weights before they are processed by Chopper)
     * @param path
     * @param prefix
     * @param w
     * @throws IOException
     */
    protected void updateQuartetWeights(String path, String prefix, double[] w) throws IOException {

        //open Quartet file
        for (int i = 0; i < w.length; i++) {

            //Opens a quartet file
            List<String> lnr = FileUtils.readLines(new File(path + prefix + (i + 1) + ".qua"));

            //ArrayLists to store the weights and quartets from the quartet file
            ArrayList<double[]> weights = new ArrayList<>();
            ArrayList<String> quartets = new ArrayList<>();
            int t = 0;

            //Read every line of file to extract quartets and quartet weights
            for (int p = 0; p < lnr.size(); p++) {
                String aLine = lnr.get(p);
                if (aLine.isEmpty() == false) {

                    if (aLine.startsWith("quartet:")) {
                        int wIdx = aLine.indexOf("weights:");
                        String quartet = aLine.substring(8, wIdx).trim();
                        quartets.add(quartet);
                        String weight = aLine.substring(wIdx + 8, aLine.lastIndexOf(";")).trim();
                        double[] qweights = new double[3];
                        String[] help = new String[3];
                        help = weight.split(" ");
                        for (int h = 0; h < 3; h++) {
                            qweights[h] = Double.parseDouble(help[h]) * w[i];
                        }
                        weights.add(qweights);
                        t++;
                    }
                }
            }


            StringBuffer buffer = new StringBuffer();
            for (int s = 0; s < (lnr.size() - t); s++) {
                buffer.append(lnr.get(s) + "\n");
            }
            for (int s = 0; s < weights.size(); s++) {
                double[] help = weights.get(s);
                buffer.append("quartet: " + quartets.get(s) + " weights:");
                for (int z = 0; z < 3; z++) {
                    buffer.append(" " + help[z]);
                }
                buffer.append(";\n");
            }
            FileUtils.writeStringToFile(new File(path + prefix + (i + 1) + ".qua"), buffer.toString());
        }

        makeScript(path, prefix, w);
    }

    /**
     * Creates script for the scaled/updated quartet file as new input for chopper
     * @param path
     * @param prefix
     * @param w
     * @throws IOException
     */
    protected void makeScript(String path, String prefix, double[] w) throws IOException {

        String content = "";
        for (int i = 0; i < w.length; i++) {
            content += "qweights " + prefix + (i + 1) + ".qua\n";
        }

        FileUtils.writeStringToFile(new File(path + prefix + ".script"), content);
    }

    public static void main(String[] args) {

        try {
            new Scaling().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
