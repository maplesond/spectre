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
package uk.ac.uea.cmp.phygen.superq.chopper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.*;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 20:03:21 To
 * change this template use Options | File Templates.
 */
public class Chopper {

    private static Logger log = LoggerFactory.getLogger(Chopper.class);

    public static enum Type {

        SCRIPT {

            public Source getLoader() {
                throw new UnsupportedOperationException("Can't get a loader from SCIPT mode.");
            }

            public String getDescriptiveName() {
                return "script";
            }
        },
        NEWICK {

            public Source getLoader() {
                return new TreeLoader();
            }

            public String getDescriptiveName() {
                return "newick";
            }
        },
    
    
        Q_WEIGHTS {

            public Source getLoader() {
                return new QWeightLoader();
            }

            public String getDescriptiveName() {
                return "qweights";
            }
        },
        NEXUS_ST_SPLITS {

            public Source getLoader() {
                return new NexusSplitsLoader();
            }

            public String getDescriptiveName() {
                return "nexus:st_splits";
            }
        },
        NEXUS_ST_QUARTETS {

            public Source getLoader() {
                return new NexusQuartetLoader();
            }

            public String getDescriptiveName() {
                return "nexus:st_quartets";
            }
        },
        NEXUS_TREES {

            public Source getLoader() {
                return new TreeFileLoader();
            }

            public String getDescriptiveName() {
                return "nexus:trees";
            }
        },
        NEXUS_DISTANCES {

            public Source getLoader() {
                return new NexusDistancesLoader();
            }

            public String getDescriptiveName() {
                return "nexus:distances";
            }
        };

        public abstract Source getLoader();

        public abstract String getDescriptiveName();

        public static Type valueOfDescriptiveName(String name) {
            for (Type t : Type.values()) {
                if (t.getDescriptiveName().equalsIgnoreCase(name)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Unknown type");
        }
    }
    private File inFile;
    private File outFile;
    private Type type;

    public Chopper(final Type type, final File inFile, final File outFile) {
        if (type == null || inFile == null || outFile == null) {
            throw new NullPointerException("All arguments must be non-null");
        }

        this.type = type;
        this.inFile = inFile;
        this.outFile = outFile;
    }

    public File getInFile() {
        return inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public Type getType() {
        return type;
    }

    public void run() throws Exception {
        //Script file
        if (this.type == Type.SCRIPT) {
            doScript();
        } else {

            LinkedList taxonNames = new LinkedList();
            QuartetWeights qW = new QuartetWeights();
            QuartetWeights summer = new QuartetWeights();

            Source loader = this.type.getLoader();

            double weight = 1.0;

            // I'm sure it should be possible to tidy this up further.
            loader.load(this.inFile.getPath(), weight);
            LinkedList taxonNamesOld = (LinkedList) taxonNames.clone();
            loader.harvestNames(taxonNames);
            loader.translate(taxonNames);
            qW = qW.translate(taxonNamesOld, taxonNames);
            summer = summer.translate(taxonNamesOld, taxonNames);


            // crucial, drop now the list stuff

            while (loader.hasMoreSets()) {


                double aW = loader.getNextWeight();
                QuartetWeights aQW = loader.getNextQuartetWeights();
                Combiner.add(qW, aQW, aW);

                System.gc();
            }

            Combiner.sum(summer, taxonNames, loader);

            //System.out.println ("done.");

            // divide

            Combiner.divide(qW, summer);

            // if requested, print information file

            printInformation(this.outFile.getPath(), summer, taxonNames);

            // then we print the result

            printQuartets(this.outFile.getPath(), qW, taxonNames);

            //System.out.println ("QNet.Chopper: Quartets written to file.");

        }

    }

    private void doScript() throws Exception {

        LinkedList taxonNames = new LinkedList();
        QuartetWeights qW = new QuartetWeights();
        QuartetWeights summer = new QuartetWeights();

        BufferedReader in = new BufferedReader(new FileReader(this.inFile));

        String aLine;
        while ((aLine = in.readLine()) != null) {

            StringTokenizer sT = new StringTokenizer(aLine);

            if (sT.hasMoreTokens()) {

                Type t;
                Source loader;
                try {
                    t = Type.valueOfDescriptiveName(sT.nextToken());
                    loader = t.getLoader();
                } catch (IllegalArgumentException e) {
                    // Just try the next line if the type wasn't recognised.
                    continue;
                }

                // line may be:
                // (at each step, load (with dummy weight if need be))

                // newick file, set of trees: if weight is given, multiply with newick weights

                if (sT.hasMoreTokens()) {

                    String sourceFileName = sT.nextToken();

                    if (!(new File(sourceFileName)).isAbsolute()) {

                        // if no path, give it the path of the script file

                        if (this.inFile.getParent() != null) {

                            // a path was given to the script file

                            sourceFileName = this.inFile.getParent() + File.separator + sourceFileName;

                        }

                    }

                    double weight;

                    if (sT.hasMoreTokens()) {

                        weight = Double.parseDouble(sT.nextToken());

                    } else {

                        weight = 1.0;

                    }

                    //System.out.print ("QNet.Chopper: Loading file " + sourceFileName + " of type " + type + " with weight " + weight + "... ");

                    loader.load(sourceFileName, weight);
                    LinkedList taxonNamesOld = (LinkedList) taxonNames.clone();
                    loader.harvestNames(taxonNames);
                    loader.translate(taxonNames);
                    qW = qW.translate(taxonNamesOld, taxonNames);
                    summer = summer.translate(taxonNamesOld, taxonNames);

                    // crucial, drop now the list stuff

                    while (loader.hasMoreSets()) {

                        double aW = loader.getNextWeight();
                        QuartetWeights aQW = loader.getNextQuartetWeights();
                        Combiner.add(qW, aQW, aW);

                        System.gc();

                    }

                    Combiner.sum(summer, taxonNames, loader);

                    //System.out.println ("done.");
                    //Runtime rt = Runtime.getRuntime();
                    //System.out.println("free memory: " + rt.freeMemory());
                } else {

                    log.warn("QNet.Chopper: Script line lacking file name!");

                }

            } else {
            }

            System.gc();
        }

        in.close();

        // divide

        Combiner.divide(qW, summer);

        // if requested, print information file

        printInformation(this.outFile.getPath(), summer, taxonNames);

        // then we print the result

        printQuartets(this.outFile.getPath(), qW, taxonNames);

        //Iterator it = taxonNames.iterator();
        //while(it.hasNext())
        //{
        //    System.out.println(it.next());
        //}

        //System.out.println ("QNet.Chopper: Quartets written to file.");


    }

    public static Chopper parseArgs(String[] args) {

        if (args.length < 3) {

            throw new IllegalArgumentException("QNet.Chopper: Type, input file name and output file name must be supplied!");
        }

        // Ensures that requests to uk.ac.uea.cmp.phygen.superq.chopper are upper case and have colons replaced
        // with underscores to reflect enum Type.
        Type t = Type.valueOf(args[0].toUpperCase().replace(':', '_'));
        File in = new File(args[1]);
        File out = new File(args[2]);

        if (in == null || !in.exists()) {
            throw new IllegalArgumentException("inFile must specify a real file to process.");
        }

        Chopper chopper = new Chopper(t, in, out);
        return chopper;
    }

    // this class chops forests down into little
    // pieces of woods
    // input is a file of newick trees
    // output is a quartet weights file
    public static void main(String args[]) {

        try {
            Chopper chopper = parseArgs(args);
            chopper.run();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.err.println("STACKTRACE: " + getStackTrace(e.getStackTrace()));
        }
    }
    
    private static String getStackTrace(StackTraceElement[] callStack)
    {
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement ste : callStack)
        {
            sb.append(ste.toString()).append("\n");
        }
        
        return sb.toString();
    }

    public static void printQuartets(String fileName, QuartetWeights qW, LinkedList taxonNames) throws IOException {

        int N = taxonNames.size();

        FileWriter out = new FileWriter(fileName);

        out.write("taxanumber: " + N + ";\ndescription: supernetwork quartets generated by uk.ac.uea.cmp.phygen.superq.chopper;\nsense: max;\n");

        NumberFormat nF = NumberFormat.getIntegerInstance();
        nF.setMinimumIntegerDigits(3);
        nF.setMaximumIntegerDigits(3);

        for (int n = 0; n < N; n++) {

            out.write("taxon:   " + nF.format(n + 1) + "   name: " + ((String) taxonNames.get(n)) + ";\n");

        }

        for (int iA = 0; iA < N - 3; iA++) {

            for (int iB = iA + 1; iB < N - 2; iB++) {

                for (int iC = iB + 1; iC < N - 1; iC++) {

                    for (int iD = iC + 1; iD < N; iD++) {

                        int a = iA + 1;
                        int b = iB + 1;
                        int c = iC + 1;
                        int d = iD + 1;

                        out.write("quartet: " + nF.format(a) + " " + nF.format(b) + " " + nF.format(c) + " " + nF.format(d)
                                  + " weights: "
                                  + qW.getWeight(a, b, c, d) + " " + +qW.getWeight(a, c, b, d) + " " + qW.getWeight(a, d, b, c) + ";\n");

                    }

                }

            }

        }

        out.close();

    }

    public static void printInformation(String fileName, QuartetWeights summer, LinkedList taxonNames) throws IOException {

        int N = taxonNames.size();

                FileWriter out = new FileWriter(fileName + ".info");

        out.write("taxanumber: " + N + ";\ndescription: supernetwork quartets presences generated by uk.ac.uea.cmp.phygen.superq.chopper;\nsense: max;\n");

        NumberFormat nF = NumberFormat.getIntegerInstance();
        nF.setMinimumIntegerDigits(3);
        nF.setMaximumIntegerDigits(3);

        for (int n = 0; n < N; n++) {

            out.write("taxon:   " + nF.format(n + 1) + "   name: " + ((String) taxonNames.get(n)) + ";\n");

        }

        for (int iA = 0; iA < N - 3; iA++) {

            for (int iB = iA + 1; iB < N - 2; iB++) {

                for (int iC = iB + 1; iC < N - 1; iC++) {

                    for (int iD = iC + 1; iD < N; iD++) {

                        int a = iA + 1;
                        int b = iB + 1;
                        int c = iC + 1;
                        int d = iD + 1;

                        if (summer.getWeight(a, b, c, d) > 0.0) {

                            out.write("quartet: " + nF.format(a) + " " + nF.format(b) + " " + nF.format(c) + " " + nF.format(d)
                                    + " weights: "
                                    + "1 1 1;\n");

                        } else {

                            out.write("quartet: " + nF.format(a) + " " + nF.format(b) + " " + nF.format(c) + " " + nF.format(d)
                                    + " weights: "
                                    + "0 0 0;\n");

                        }

                    }

                }

            }

        }

        out.close();

    }
}
