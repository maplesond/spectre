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
package uk.ac.uea.cmp.phygen.core.io.nexus;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.io.FileUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;
import uk.ac.uea.cmp.phygen.core.io.AbstractPhygenReader;
import uk.ac.uea.cmp.phygen.core.io.PhygenDataType;
import uk.ac.uea.cmp.phygen.core.io.nexus.parser.NexusFileLexer;
import uk.ac.uea.cmp.phygen.core.io.nexus.parser.NexusFileParser;
import uk.ac.uea.cmp.phygen.core.io.nexus.parser.NexusFilePopulator;
import uk.ac.uea.cmp.phygen.core.util.DefaultParsingErrorListener;
import uk.ac.uea.cmp.phygen.core.util.DefaultParsingErrorStrategy;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Used to handle streaming of Nexus format files into memory, and convertion of
 * the data into a SplitSystem object.
 *
 * @author Dan
 */
@MetaInfServices(uk.ac.uea.cmp.phygen.core.io.PhygenReader.class)
public class NexusReader extends AbstractPhygenReader {


    public Nexus parse(File file) throws IOException {

        // Convert source into a character stream
        CharStream in = new ANTLRInputStream(new FileInputStream(file));

        // Setup lexer
        NexusFileLexer lexer = new NexusFileLexer(in);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new DefaultParsingErrorListener());

        // Do the lexing
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // The results of parsing go in here
        Nexus nexus = new Nexus();

        // Setup parser
        NexusFileParser parser = new NexusFileParser(tokens);
        parser.removeParseListeners();
        parser.removeErrorListeners();
        parser.addParseListener(new NexusFilePopulator(nexus, true));
        parser.addErrorListener(new DefaultParsingErrorListener());
        parser.setErrorHandler(new DefaultParsingErrorStrategy());

        // Do the parsing
        try {
            parser.parse();
        }
        catch(RuntimeException e) {
            throw new IOException(e);
        }

        // Return the populated Nexus object
        return nexus;
    }

    /**
     * Reads the file specified by this reader and converts the data into a set
     * of taxa and the distances between taxa.
     *
     * @return The distance matrix, with associated taxa set.
     * @throws IOException    Thrown if there were any problems accessing the file.
     * @throws ParseException Thrown if there were any syntax issues when
     *                        parsing the file.
     */
    public DistanceMatrix readDistanceMatrix(File file) throws IOException {

        if (file == null) {
            throw new NullPointerException("Must specify a nexus file to read");
        }

        if (!file.exists()) {
            throw new IOException("Nexus file does not exist: " + file.getAbsolutePath());
        }

        if (!file.canRead()) {
            throw new IOException("Nexus file cannot be read: " + file.getAbsolutePath());
        }

        List<String> inLines = FileUtils.readLines(file);

        DistanceMatrix distanceMatrix = null;
        boolean isTriangle = false; //true if triangular formatted dist matrix
        String taxaNumberString = "";
        int taxaIndex = 1;
        int offset = 0; //used if Exception is generated
        boolean taxaBloc = false;
        boolean distanceBloc = false;
        boolean matrix = false;
        String aLine = "";
        int p = 0;

        //Evaluation of file content (if there is a distance and a taxabloc)
        while (taxaBloc == false && distanceBloc == false) {
            aLine = inLines.get(p++);
            if (aLine.trim().toUpperCase().contains("BEGIN TAXA")) {
                taxaBloc = true;
            } else if (aLine.trim().toUpperCase().contains("BEGIN DISTANCES")) {
                distanceBloc = true;
            }
        }

        //If no taxa bloc, read distanceMatrix & tax labels from distance bloc
        if (distanceBloc == true) {

            int s = 0;

            while (matrix == false) {
                aLine = inLines.get(p++);
                // Try to find nbTaxa in properties
                if (this.isPropertyGroup(aLine, "DIMENSIONS")) {
                    String ntaxString = this.getValueFromPropertyInLine(aLine, "NTAX");
                    int n = Integer.parseInt(ntaxString);
                    distanceMatrix = new DistanceMatrix(n);
                }

                if (aLine.trim().toUpperCase().startsWith("MATRIX")) {
                    matrix = true;
                }
                taxaNumberString = Integer.toString(taxaIndex);
            }
            aLine = inLines.get(p);

            List<String> matrixLines = new ArrayList<>();
            do {
                aLine = aLine.trim();
                if (!aLine.isEmpty()) {
                    distanceMatrix.setTaxa(s++, getIdentifierFromMatrixLine(aLine));
                    matrixLines.add(aLine);
                }
                aLine = inLines.get(++p);
            } while (aLine.trim().toUpperCase().contains(";") == false);

            TriangleFormat tf = fillDistanceMatrix(matrixLines, distanceMatrix);
        }


        //If taxa bloc is included, read in taxa names from taxa bloc
        if (taxaBloc == true) {
            p--;
            do {
                // Try to find nbTaxa in properties
                if (this.isPropertyGroup(aLine, "DIMENSIONS")) {
                    String ntaxString = this.getValueFromPropertyInLine(aLine, "NTAX");
                    int n = Integer.parseInt(ntaxString);
                    distanceMatrix = new DistanceMatrix(n);
                }

                aLine = inLines.get(++p);
            } while (aLine.toUpperCase().contains("TAXLABELS") == false);


            aLine = inLines.get(++p);

            do {
                if (aLine.isEmpty() == false && aLine.equals(";") == false) {
                    int beginIdx = aLine.indexOf("'") + 1;
                    int endIdx = aLine.lastIndexOf("'");

                    try {
                        String help = aLine.substring(beginIdx, endIdx);
                        help = help.replace(' ', '_');

                        distanceMatrix.setTaxa(taxaIndex - 1, help);
                        taxaIndex++;
                    } catch (StringIndexOutOfBoundsException e) {
                        throw new IOException("Error at offset " + offset + " in " + file.getPath());
                    }
                    taxaNumberString = Integer.toString(taxaIndex);
                }

                aLine = inLines.get(++p);
            } while (aLine.trim().toUpperCase().contains("END") == false);


            int row = 0;
            List<String> matrixLines = new ArrayList<>();
            do {
                if (distanceBloc && matrix) {

                    if (!aLine.isEmpty() && !aLine.equals(";")) {
                        matrixLines.add(aLine.trim());
                    }
                }

                if (aLine.trim().toUpperCase().contains("BEGIN DISTANCES")) {
                    distanceBloc = true;
                }
                if (aLine.trim().toUpperCase().contains("MATRIX")) {
                    matrix = true;
                }
                if (aLine.trim().toUpperCase().contains("FORMAT")) {
                    // Nothing to do really...
                }
                aLine = inLines.get(++p);
            } while (aLine.trim().toUpperCase().contains("END") == false);

            TriangleFormat tf = fillDistanceMatrix(matrixLines, distanceMatrix);
        }

        return distanceMatrix;
    }

    @Override
    public List<NewickTree> readTrees(File input, double weight) throws IOException {
        throw new UnsupportedOperationException("Haven't got around to implementing this yet");
    }

    @Override
    public String[] commonFileExtensions() {
        return new String[]{"nex", "nxs", "nexus"};
    }

    @Override
    public String getIdentifier() {
        return "NEXUS";
    }

    @Override
    public boolean acceptsDataType(PhygenDataType phygenDataType) {

        if (phygenDataType == PhygenDataType.DISTANCE_MATRIX)
            return true;
        else if (phygenDataType == PhygenDataType.TREE)
            return true;

        return false;
    }




    private String getIdentifierFromMatrixLine(String aLine) {
        int endIdx = aLine.indexOf(" ");
        String identifier = aLine.substring(0, endIdx);
        aLine = aLine.substring(endIdx, aLine.length()).trim();
        if (identifier.startsWith("[")) {
            if (identifier.endsWith("]")) {
                endIdx = aLine.indexOf(" ");
                identifier = aLine.substring(0, endIdx - 1);
            }
        }


        if (identifier.startsWith("'")) {
            identifier = identifier.substring(1);
        }
        if (identifier.endsWith("'")) {
            identifier = identifier.substring(0, endIdx - 2);
        }

        return identifier;
    }

    private TriangleFormat fillDistanceMatrix(List<String> lines, DistanceMatrix distanceMatrix) throws IOException {

        TriangleFormat tf = TriangleFormat.BOTH;

        for (int i = 0; i < lines.size(); i++) {

            List<String> matrixLineElements = getMatrixLineElements(lines.get(i));

            // We have an lower triangular matrix
            if (i == 0 && matrixLineElements.size() != distanceMatrix.size()) {
                tf = TriangleFormat.LOWER;
            }
            // Not sure yet, either BOTH or UPPER, assume both for now
            else if (i == 0 && matrixLineElements.size() == distanceMatrix.size()) {
                tf = TriangleFormat.BOTH;
            }
            // Actually this is an upper triangular matrix
            else if (i == 1 && matrixLineElements.size() != distanceMatrix.size() && tf == TriangleFormat.BOTH) {
                tf = TriangleFormat.UPPER;
            }

            // We should have covered all the bases here and ensured tf is non-null;
            tf.fillRow(i, matrixLineElements, distanceMatrix);
        }

        // We didn't know it was upper triangular format before the second line so computer the first column now
        if (tf == TriangleFormat.UPPER) {
            for (int i = 1; i < distanceMatrix.size(); i++) {
                distanceMatrix.setDistance(i, 0, distanceMatrix.getDistance(0, i));
            }
        }

        // Return triangle format type just for info
        return tf;
    }

    private static List<String> getMatrixLineElements(String matrixLine) {

        // Split line on any whitespace
        String[] words = matrixLine.trim().split("\\s+");
        List<String> elements = new ArrayList<>();

        for (String s : words) {

            // Don't add index or label if present
            if (!s.startsWith("[") && !s.startsWith("'")) {
                elements.add(s);
            }
        }

        return elements;
    }

    private enum TriangleFormat {

        BOTH {
            @Override
            public void fillRow(int row, List<String> elements, DistanceMatrix distanceMatrix) {
                for (int j = 0; j < elements.size(); j++) {
                    distanceMatrix.setDistance(row, j, Double.parseDouble(elements.get(j)));
                }
            }
        },
        LOWER {
            @Override
            public void fillRow(int row, List<String> elements, DistanceMatrix distanceMatrix) {
                for (int j = 0; j < elements.size(); j++) {
                    distanceMatrix.setDistance(row, j, Double.parseDouble(elements.get(j)));
                    distanceMatrix.setDistance(j, row, Double.parseDouble(elements.get(j)));
                }
            }
        },
        UPPER {
            @Override
            public void fillRow(int row, List<String> elements, DistanceMatrix distanceMatrix) {
                for (int j = 0; j < elements.size(); j++) {
                    distanceMatrix.setDistance(row, j + row, Double.parseDouble(elements.get(j)));
                    distanceMatrix.setDistance(j + row, row, Double.parseDouble(elements.get(j)));
                }
            }
        };


        public abstract void fillRow(int row, List<String> elements, DistanceMatrix distanceMatrix);
    }

    public CircularOrdering extractCircOrdering(File file) throws IOException {

        List<String> inLines = FileUtils.readLines(file);

        String circOrd = "";
        int n = 0;

        boolean taxaFound = false;
        String aLine = inLines.get(0);

        //Find circular ordering in document
        for (int i = 1; i < inLines.size(); i++) {
            aLine = inLines.get(i);

            // Try to find nbTaxa in properties
            if (this.isPropertyGroup(aLine, "DIMENSIONS") && taxaFound == false) {
                String ntaxString = this.getValueFromPropertyInLine(aLine, "NTAX");
                n = Integer.parseInt(ntaxString);
                taxaFound = true;
            }

            if (aLine.toUpperCase().contains("CYCLE")) {
                int startidx = aLine.indexOf(" ");
                int endidx = aLine.indexOf(";");
                circOrd = aLine.substring(startidx, endidx).trim();
            }

        }


        //modify ordering (each entry -1) and fill array
        int[] circOrdering = new int[n];
        String[] help = circOrd.split(" ");

        for (int i = 0; i < help.length; i++) {
            circOrdering[i] = Integer.valueOf(help[i]) - 1;
        }


        return new CircularOrdering(circOrdering);
    }

    public Nexus readNexusData(File inFile) throws IOException {

        Taxa taxonNames = new Taxa();
        List<SplitBlock> splits = new LinkedList<>();
        List<Double> weights = new LinkedList<>();
        List<Integer> cycle = new LinkedList<>();

        int N = 0;

        BufferedReader fileInput = new BufferedReader(new FileReader(inFile));

        /**
         * Keep on reading and tokenizing until... a token is found
         * beginning with "ntax=" parse its remainder for the number of
         * taxa.
         *
         * Keep on reading until a token is found "TAXLABELS". Then read N
         * lines which will be the taxon names. We assume there are n choose
         * 4 quartets.
         *
         * Keep on reading and tokenizing until "st_splits;" is found. Then
         * proceed to "MATRIX". Then read the quartet lines until a line
         * starts with ";".
         */
        boolean readingState = true;

        while (readingState) {

            String aLine = fileInput.readLine();
            StringTokenizer sT = new StringTokenizer(aLine);

            while (sT.hasMoreTokens()) {

                String tT = sT.nextToken();

                if (tT.startsWith("ntax=")) {

                    N = Integer.parseInt(tT.substring(5, tT.length() - 1));

                    for (int n = 0; n < N; n++) {
                        taxonNames.add(new Taxon(""));
                    }

                    readingState = false;
                }
            }
        }

        readingState = true;

        while (readingState) {

            String aLine = fileInput.readLine();
            StringTokenizer sT = new StringTokenizer(aLine);

            while (sT.hasMoreTokens()) {

                String tT = sT.nextToken();

                if (tT.startsWith("TAXLABELS")) {

                    for (int n = 0; n < N; n++) {

                        String aS = fileInput.readLine();
                        taxonNames.set(n, new Taxon(aS));
                    }

                    readingState = false;
                }
            }
        }

        readingState = true;

        while (readingState) {

            String aLine = fileInput.readLine();
            StringTokenizer sT = new StringTokenizer(aLine);

            while (sT.hasMoreTokens()) {

                String tT = sT.nextToken();

                if (tT.startsWith("st_splits;")) {

                    readingState = false;
                }
            }
        }

        readingState = true;

        while (readingState) {

            String aLine = fileInput.readLine();
            StringTokenizer sT = new StringTokenizer(aLine);

            while (sT.hasMoreTokens()) {

                String tT = sT.nextToken();

                if (tT.startsWith("CYCLE")) {

                    String[] cycleParts = aLine.split(" ");

                    for (String cyclePart : cycleParts) {

                        String trimmed = cyclePart.trim();

                        if (!trimmed.isEmpty()) {
                            cycle.add(Integer.parseInt(trimmed));
                        }
                    }
                    readingState = false;
                }
            }
        }

        readingState = true;

        while (readingState) {

            String aLine = fileInput.readLine();
            StringTokenizer sT = new StringTokenizer(aLine);

            while (sT.hasMoreTokens()) {

                String tT = sT.nextToken();

                if (tT.startsWith("MATRIX")) {

                    boolean splitState = true;

                    while (splitState) {

                        String bLine = fileInput.readLine().trim();

                        if (bLine.startsWith(";")) {

                            splitState = false;

                        } else {

                            // BEGIN reading a split line

                            if (bLine.endsWith(",")) {

                                bLine = bLine.substring(0, bLine.length() - 1);

                            }

                            StringTokenizer bT = new StringTokenizer(bLine);

                            int id = Integer.parseInt(bT.nextToken());

                            double w = Double.parseDouble(bT.nextToken());

                            List<Integer> setA = new LinkedList<>();

                            while (bT.hasMoreTokens()) {
                                setA.add(new Integer(Integer.parseInt(bT.nextToken())));
                            }

                            splits.add(new SplitBlock(setA));
                            weights.add(new Double(w));

                            // END reading a split line
                        }
                    }

                    readingState = false;
                }
            }
        }

        Nexus nexus = new Nexus();
        nexus.setTaxa(taxonNames);
        nexus.setDistanceMatrix(null);
        nexus.setCycle(cycle);
        nexus.setSplits(splits);
        nexus.setWeights(weights);

        return nexus;
    }

    protected boolean isPropertyGroup(String line, String propertyGroup) {
        return line.trim().toUpperCase().startsWith(propertyGroup);
    }

    protected String getValueFromPropertyInLine(String line, String property) throws IOException {

        String[] properties = line.trim().toUpperCase().split(" ");

        // Ignore the first part because it will be the group name
        for (int i = 1; i < properties.length; i++) {

            String[] propertyParts = properties[i].split("=");

            if (propertyParts.length != 2)
                throw new IOException("Property group line is ill formed: " + line);

            String key = propertyParts[0];
            String value = propertyParts[1].endsWith(";") ?
                    propertyParts[1].substring(0, propertyParts[1].length() - 1) :
                    propertyParts[1];

            if (key.equalsIgnoreCase(property))
                return value;
        }

        return null;
    }
}
