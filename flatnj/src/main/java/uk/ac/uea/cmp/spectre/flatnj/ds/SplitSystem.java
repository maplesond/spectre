/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.flatnj.ds;

/*
 * This class implements methods to handle a weighted split system
 */

import uk.ac.uea.cmp.spectre.core.ds.Alignment;
import uk.ac.uea.cmp.spectre.flatnj.tools.Utilities;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SplitSystem {
    //number of taxa

    protected int nTaxa = 0;
    protected int[] taxaMap;
    protected String[] taxaNames;
    protected int[] cycle;
    //The taxa are always indexed as 0,1,...,nTaxa-1.
    //This is also the ordering in which they are used
    //in the matrix that stores the split system below.
    //number of splits
    protected int nSplits = 0;
    //2-dimensional 0/1-array encoding the splits.
    //Rows correspond to splits, columns correspond to taxa.
    //The ordering of taxa is 0,1,...,nTaxa-1.
    protected boolean[][] splits = null;
    //weights of the splits
    //The ordering is the same as in the rows in the 0/1 matrix
    //that stores the splits
    protected double[] weights = null;
    protected double[] trivialWeights;

    //value of the fit function from gurobi solver
    protected double fit;

    protected boolean[] active;

    //This method checks whether the quadruple split number nr 
    //is in the restriction of the split system to {a,b,c,d}.
    //It is assumed that a, b, c and d are pairwise distinct.
    //The possible values for nr are:
    //0 --> a|bcd
    //1 --> b|acd
    //2 --> c|abd
    //3 --> d|abc
    //4 --> ab|cd
    //5 --> ac|bd
    //6 --> ad|bc
    public boolean restrictionExists(int a, int b, int c, int d, int nr) {
        boolean exists = false;
        int i = 0;

        if (nr == 0) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][b] == splits[i][c]) && (splits[i][c] == splits[i][d])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 1) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][c]) && (splits[i][c] == splits[i][d])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 2) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][b]) && (splits[i][b] == splits[i][d])) && (splits[i][a] != splits[i][c])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 3) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][b]) && (splits[i][b] == splits[i][c])) && (splits[i][a] != splits[i][d])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 4) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][b]) && (splits[i][c] == splits[i][d])) && (splits[i][a] != splits[i][c])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 5) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][c]) && (splits[i][b] == splits[i][d])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 6) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][d]) && (splits[i][b] == splits[i][c])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }

    //This method checks whether the quartet split number nrQ 
    //is in the restriction of the split number nrS to {a,b,c,d}.
    //It is assumed that a, b, c and d are pairwise distinct.
    //The possible values for nrQ are:
    //0 --> a|bcd
    //1 --> b|acd
    //2 --> c|abd
    //3 --> d|abc
    //4 --> ab|cd
    //5 --> ac|bd
    //6 --> ad|bc
    public boolean singleRestrictionExists(int a, int b, int c, int d, int nrQ, int nrS) {
        boolean exists = false;

        if (nrQ == 0) {
            if (((splits[nrS][b] == splits[nrS][c]) && (splits[nrS][c] == splits[nrS][d])) && (splits[nrS][a] != splits[nrS][b])) {
                exists = true;
            }
        } else if (nrQ == 1) {
            if (((splits[nrS][a] == splits[nrS][c]) && (splits[nrS][c] == splits[nrS][d])) && (splits[nrS][a] != splits[nrS][b])) {
                exists = true;
            }
        } else if (nrQ == 2) {
            if (((splits[nrS][a] == splits[nrS][b]) && (splits[nrS][b] == splits[nrS][d])) && (splits[nrS][a] != splits[nrS][c])) {
                exists = true;
            }
        } else if (nrQ == 3) {
            if (((splits[nrS][a] == splits[nrS][b]) && (splits[nrS][b] == splits[nrS][c])) && (splits[nrS][a] != splits[nrS][d])) {
                exists = true;
            }
        } else if (nrQ == 4) {
            if (((splits[nrS][a] == splits[nrS][b]) && (splits[nrS][c] == splits[nrS][d])) && (splits[nrS][a] != splits[nrS][c])) {
                exists = true;
            }
        } else if (nrQ == 5) {
            if (((splits[nrS][a] == splits[nrS][c]) && (splits[nrS][b] == splits[nrS][d])) && (splits[nrS][a] != splits[nrS][b])) {
                exists = true;
            }
        } else if (nrQ == 6) {
            if (((splits[nrS][a] == splits[nrS][d]) && (splits[nrS][b] == splits[nrS][c])) && (splits[nrS][a] != splits[nrS][b])) {
                exists = true;
            }
        }
        return exists;
    }

    //This method prints information about the split system on the screen
    public void printSplits() {
        int i = 0;
        int j = 0;

        double[] trivial = new double[nTaxa];

        System.out.println("Number of taxa: " + nTaxa + " *");
        System.out.println("Number of splits: " + nSplits + " *");

        System.out.println("List of splits:");
        System.out.println("Not trivial:");

        for (i = 0; i < nSplits; i++) {
            if (!isTrivial(splits[i])) {
                System.out.print(weights[i] + " : ");
                for (j = 0; j < nTaxa; j++) {
                    int x = (splits[i][j]) ? 1 : 0;
                    System.out.print(x);
                    if (j < nTaxa - 1) {
                        System.out.print("-");
                    }
                }
                System.out.println(" *");
            } else {
                trivial[trivialTaxa(splits[i])] = weights[i];
            }
        }
        System.out.println("Trivial:");
        for (i = 0; i < nTaxa; i++) {
            if (trivial[i] + trivialWeights[i] > 0) {
                System.out.print((trivial[i] + trivialWeights[i]) + " : ");
                for (j = 0; j < nTaxa; j++) {
                    int x = (i == j) ? 1 : 0;
                    System.out.print(x);
                    if (j < nTaxa - 1) {
                        System.out.print("-");
                    }
                }
                System.out.println(" *");
            }
        }
    }

    //This method prints information about the splits that have positive length on the screen
    public void printPositiveSplits() {
        int i = 0;
        int j = 0;

        double[] trivial = new double[nTaxa];

        System.out.println("Number of taxa: " + nTaxa + " *");
        System.out.println("Number of splits: " + (nonTrivialSplits() + nTaxa) + " *");

        System.out.println("List of splits:");
        System.out.println("Not trivial:");

        for (i = 0; i < nSplits; i++) {
            if (weights[i] > 0) {
                if (!isTrivial(splits[i])) {
                    System.out.print(weights[i] + " : ");
                    for (j = 0; j < nTaxa; j++) {
                        int x = (splits[i][j]) ? 1 : 0;
                        System.out.print(x);
                        if (j < nTaxa - 1) {
                            System.out.print("-");
                        }
                    }
                    System.out.println(" *");
                } else {
                    trivial[trivialTaxa(splits[i])] = weights[i];
                }
            }
        }
        System.out.println("Trivial:");
        for (i = 0; i < nTaxa; i++) {
            if (trivial[i] + trivialWeights[i] > 0) {
                System.out.print((trivial[i] + trivialWeights[i]) + " : ");
                for (j = 0; j < nTaxa; j++) {
                    int x = (i == j) ? 1 : 0;
                    System.out.print(x);
                    if (j < nTaxa - 1) {
                        System.out.print("-");
                    }
                }
                System.out.println(" *");
            }
        }
    }

    public void printSplitsNonTrivial() {
        int i = 0;
        int j = 0;


        System.out.println("Number of taxa: " + nTaxa + " *");
        System.out.println("Number of splits: " + nSplits + " *");

        System.out.println("List of splits:");
        System.out.println("Not trivial:");

        for (i = 0; i < nSplits; i++) {
            System.out.print(weights[i] + " : ");
            for (j = 0; j < nTaxa; j++) {
                int x = (splits[i][j]) ? 1 : 0;
                System.out.print(x);
                if (j < nTaxa - 1) {
                    System.out.print("-");
                }
            }
            System.out.println(" *");
        }
    }

    //This constructor reads a weighted split system from a file.
    //The file must contain the list of splits in the same
    //format as in a nexus file.
    public SplitSystem(String fileName) {

        int mode = 0;
        String line = null;
        LineNumberReader ln_reader = null;
        Scanner scanner = null;
        String matched = null;
        boolean weightsPresent = false;

        try {
            ln_reader = new LineNumberReader(new FileReader(fileName));
            line = ln_reader.readLine();
        } catch (FileNotFoundException fnfe) {
            System.out.println("File '" + fileName + "' not found.");
        } catch (IOException ioe) {
            System.out.println("Error reading splits file.");
        }

        while (line != null) {
            line = (mode != 1) ? line.toLowerCase() : line;
            scanner = new Scanner(line);
            if (scanner.findInLine("end;") != null || scanner.findInLine("END;") != null) {
                mode = 0;
            } else if (mode == 0) {
                matched = scanner.findInLine("begin\\s+taxa;");
                if (matched != null) {
                    mode = 1;
                } else {
                    matched = scanner.findInLine("begin\\s+splits;");
                    mode = (matched != null) ? 2 : mode;
                }
            } else if (mode == 1) {
                if (taxaNames == null) {
                    matched = scanner.findInLine("ntax\\s*=\\s*(\\d+)");
                    if (matched != null) {
                        taxaNames = new String[Integer.parseInt((scanner.match()).group(1))];
                    }
                } else {
                    matched = scanner.findInLine("\\[(\\d+)\\]\\s+'(.+)'");
                    if (matched != null) {
                        taxaNames[Integer.parseInt((scanner.match()).group(1)) - 1] = scanner.match().group(2);
                    }
                }
            } else if (mode == 2) {
                if (scanner.findInLine("weights\\s*=\\s*yes") != null) {
                    weightsPresent = true;
                    weights = new double[nSplits];
                } else if (scanner.findInLine("cycle") != null) {
                    line = line.replace("cycle", "");
                    line = line.replace(";", "");
                    line = line.trim();
                    String[] cycleString = line.split("\\s+");
                    cycle = new int[cycleString.length];
                    for (int i = 0; i < cycle.length; i++) {
                        cycle[i] = Integer.parseInt(cycleString[i]) - 1;
                    }
                } else if (splits == null) {
                    matched = scanner.findInLine("ntax\\s*=\\s*(\\d+)\\s+nsplits\\s*=\\s*(\\d+)");
                    if (matched != null) {
                        nSplits = Integer.parseInt((scanner.match()).group(2));
                        nTaxa = Integer.parseInt((scanner.match()).group(1));
                    }
                    splits = new boolean[nSplits][nTaxa];
                } else {
                    int id = -1;
                    String thisSplit = null;
                    if (weightsPresent) {
                        matched = scanner.findInLine("\\[(\\d+).*\\]\\s+([\\d\\.eE\\-]+)\\s+(.+),");
                        if (matched != null) {
                            id = Integer.parseInt(scanner.match().group(1)) - 1;
                            double weight = Double.parseDouble(scanner.match().group(2));
                            weights[id] = weight;
                            thisSplit = scanner.match().group(3);
                        }
                    } else {
                        matched = scanner.findInLine("\\[(\\d+).*\\]\\s+(.+),");
                        if (matched != null) {
                            id = Integer.parseInt(scanner.match().group(1)) - 1;
                            thisSplit = scanner.match().group(2);
                        }
                    }
                    if (id > -1 && thisSplit != null) {
                        String[] ids = thisSplit.split("\\s+");
                        for (int i = 0; i < ids.length; i++) {
                            splits[id][Integer.parseInt(ids[i]) - 1] = true;
                        }
                    }
                }
            }

            try {
                line = ln_reader.readLine();
            } catch (IOException ioe) {
                System.out.println("Error reading splits file.");
            }
        }

//        String line = null;
//        int index = 0;
//        int i = 0;
//        int k = 0;
//
//        try
//        {
//            LineNumberReader ln_reader = new LineNumberReader(new FileReader(fileName));
//            //read number of taxa
//            line = ln_reader.readLine();
//            nTaxa = Integer.parseInt(line);
//            //read number of splits
//            line = ln_reader.readLine();
//            nSplits = Integer.parseInt(line);
//
//            splits = new boolean[nSplits][nTaxa];
//            weights = new double[nSplits];
//
//            for (i = 0; i < nSplits; i++)
//            {
//                line = ln_reader.readLine();
//
//                index = line.indexOf(',');
//                line = line.substring(0, index);
//                line = line.trim();
//
//                index = line.indexOf(' ');
//                weights[i] = Double.parseDouble(line.substring(0, index));
//                line = line.substring(index);
//                line = line.trim();
//
//                for (k = 0; k < nTaxa; k++)
//                {
//                    splits[i][k] = false;
//                }
//                while (line.length() > 0)
//                {
//                    index = line.indexOf(' ');
//                    if (index == -1)
//                    {
//                        index = line.length();
//                    }
//                    //note that in nexus files the taxa are numbered 1,2,...,nTaxa
//                    k = Integer.parseInt(line.substring(0, index));
//                    splits[i][k - 1] = true;
//                    line = line.substring(index);
//                    line = line.trim();
//                }
//            }
//
//            ln_reader.close();
//        }
//        catch (IOException exception)
//        {
//            System.out.println("Couldn't read file.");
//        }
    }

    public SplitSystem(String fileName, int a) {
        String line = "";
        int i = 0;
        int k = 0;

        try {
            LineNumberReader ln_reader = new LineNumberReader(new FileReader(fileName));
            //read number of taxa

            while (line.toLowerCase().contains("begin") && line.toLowerCase().contains("splits")) {
                line = ln_reader.readLine();
            }

            while (!(line.contains("ntax") && line.contains("nsplits"))) {
                line = ln_reader.readLine();
            }

            int index = line.indexOf("ntax=");
            String tmp = line.substring(index + 5);
            index = tmp.indexOf(" ");
            nTaxa = Integer.parseInt(tmp.substring(0, index));
            index = tmp.indexOf("nsplits=");
            tmp = tmp.substring(index + 8);
            index = tmp.indexOf(";");
            nSplits = Integer.parseInt(tmp.substring(0, index));

            splits = new boolean[nSplits][nTaxa];
            weights = new double[nSplits];


            while (!line.toLowerCase().contains("matrix")) {
                line = ln_reader.readLine();
            }

            line = ln_reader.readLine();
            i = 0;

            while (!line.toLowerCase().contains(";")) {

                index = line.lastIndexOf(',');
                line = line.substring(0, index);
                line = line.trim();

                if (line.contains("]")) {
                    index = line.indexOf("]") + 1;
                } else {
                    index = line.indexOf(' ') + 1;
                }
                line = line.substring(index);
                line = line.trim();

                index = line.indexOf(' ') + 1;
                weights[i] = Double.parseDouble(line.substring(0, index));
                line = line.substring(index).trim();

                for (k = 0; k < nTaxa; k++) {
                    splits[i][k] = false;
                }
                while (line.length() > 0) {
                    index = line.indexOf(' ');
                    if (index == -1) {
                        index = line.length();
                    }
                    //note that in nexus files the taxa are numbered 1,2,...,nTaxa
                    k = Integer.parseInt(line.substring(0, index));
                    splits[i][k - 1] = true;
                    line = line.substring(index);
                    line = line.trim();
                }

                i++;
                line = ln_reader.readLine();
            }

            ln_reader.close();
        } catch (IOException exception) {
            System.out.println("Couldn't read file.");
        }
    }

    //Constructor of this class from a permutation sequence.
    public SplitSystem(PermutationSequence ps) {
        int h;

        nTaxa = ps.getnTaxa();

        cycle = new int[nTaxa];
        System.arraycopy(ps.getSequence(), 0, cycle, 0, nTaxa);

        active = ps.getActive();

        nSplits = ps.getnSwaps();

        taxaMap = new int[nTaxa];
        System.arraycopy(ps.getSequence(), 0, taxaMap, 0, taxaMap.length);

        int maxN = taxaMap[0];
        for (int i = 1; i < taxaMap.length; i++) {
            maxN = (maxN < taxaMap[i]) ? taxaMap[i] : maxN;
        }
        maxN++;

        splits = new boolean[nSplits][maxN];
        weights = new double[nSplits];
        int[] cur_sequ = new int[nTaxa];

        double[] psWeights = ps.getWeights();

        System.arraycopy(ps.getSequence(), 0, cur_sequ, 0, nTaxa);

        //Write splits into 0/1-array.
        int k = 0;
        int[] swaps = ps.getSwaps();
        for (int i = 0; i < ps.getnSwaps(); i++) {
            //compute current permutation
            h = cur_sequ[swaps[i]];
            cur_sequ[swaps[i]] = cur_sequ[swaps[i] + 1];
            cur_sequ[swaps[i] + 1] = h;
            //turn it into a 0/1 sequence
            if (active[i]) {
                for (int j = 0; j < nTaxa; j++) {
                    if (j <= swaps[i]) {
                        splits[k][cur_sequ[j]] = true;
                    } else {
                        splits[k][cur_sequ[j]] = false;
                    }
                }
            }
            k++;
        }

        if (psWeights != null) {
            setWeights(psWeights, ps.getTrivial());
        } else {
            for (int i = 0; i < nSplits; i++) {
                weights[i] = 1.0;
            }
        }
    }

    public SplitSystem(boolean[][] splits, double[] weights) {
        nSplits = splits.length;
        nTaxa = splits[0].length;

        this.splits = new boolean[splits.length][splits[0].length];
        for (int i = 0; i < splits.length; i++) {
            System.arraycopy(splits[i], 0, this.splits[i], 0, splits[i].length);
        }
        this.weights = new double[weights.length];
        System.arraycopy(weights, 0, this.weights, 0, weights.length);

        trivialWeights = new double[nTaxa];
    }

    public SplitSystem(boolean[][] splits, double[] weights, int[] cycle) {
        nSplits = splits.length;
        nTaxa = splits[0].length;

        this.splits = new boolean[splits.length][splits[0].length];
        for (int i = 0; i < splits.length; i++) {
            System.arraycopy(splits[i], 0, this.splits[i], 0, splits[i].length);
        }
        this.weights = new double[weights.length];
        System.arraycopy(weights, 0, this.weights, 0, weights.length);

        this.cycle = cycle;

        trivialWeights = new double[nTaxa];
    }


    public SplitSystem(boolean[][] splits, double[] weights, int[] cycle, boolean[] active) {
        nSplits = splits.length;
        nTaxa = splits[0].length;

        this.active = active;

        this.splits = new boolean[splits.length][splits[0].length];
        for (int i = 0; i < splits.length; i++) {
            System.arraycopy(splits[i], 0, this.splits[i], 0, splits[i].length);
        }
        this.weights = new double[weights.length];
        System.arraycopy(weights, 0, this.weights, 0, weights.length);

        this.cycle = cycle;

        trivialWeights = new double[nTaxa];
    }

    //Standard constructor. Doesn't do anything.
    //Only exists for testing purposes.
    public SplitSystem() {
    }

    public void printSplitsInNexus(Writer outputFile, Alignment alignment) throws IOException {
        if (alignment == null) {
            Utilities.printFictitiousTaxaBlock(outputFile, nTaxa);
        } else {
            Utilities.printTaxaBlock(outputFile, alignment.getTaxaLabels());
        }
        outputFile.write("BEGIN Splits;\n");
        outputFile.write("DIMENSIONS NTAX=" + nTaxa + " NSPLITS=" + nSplits + ";\n");
        outputFile.write("FORMAT LABELS=NO WEIGHTS=YES CONFIDENCES=NO INTERVALS=NO;\n");
        outputFile.write("PROPERTIES FIT=100.0;\n");
        outputFile.write("MATRIX\n");

        for (int i = 0; i < nSplits; i++) {
            outputFile.write(weights[i] + "\t");
            for (int j = 0; j < nTaxa; j++) {
                if (splits[i][j] == true) {
                    outputFile.write(" " + (j + 1));
                }
            }
            outputFile.write(",\n");
        }
        outputFile.write(";\nEND;\n");
        outputFile.close();
    }

    public void printSplitsForTest(Writer outputFile, double fraction) throws IOException {
        int visibleSplits = 0;
        String contents = "";
        for (int i = 0; i < nSplits; i++) {
            double rand = Math.random();
            if (rand < fraction) {
                visibleSplits++;
                contents = contents.concat(weights[i] + " ");
                for (int j = 0; j < nTaxa; j++) {
                    if (splits[i][j] == true) {
                        contents = contents.concat(" " + (j + 1));
                    }
                }
                contents = contents.concat(",\n");
            }
        }
        contents = (nTaxa + "\n" + visibleSplits + "\n").concat(contents);
        outputFile.write(contents);
        outputFile.close();
    }

    public boolean equals(SplitSystem ss) {
//        if(this.nSplits != ss.nSplits)
//        {
//            return false;
//        }
        if (this.nTaxa != ss.nTaxa) {
            return false;
        }
        for (int i = 0; i < splits.length; i++) {
            boolean found = false;
            for (int j = 0; j < ss.splits.length; j++) {
                int same = 0;
                int opposite = 0;
                for (int i1 = 0; i1 < ss.splits[0].length; i1++) {
                    if (splits[i][i1] == ss.splits[j][i1]) {
                        same++;
                    } else {
                        opposite++;
                    }
                }
                if (same == 0 || opposite == 0) {
                    found = true;
                    if (weights[i] != ss.weights[j]) {
                        return false;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public double equalSplits(SplitSystem ss) {
        double splitsAtAll = 0;
        double sameSplits = 0;


        for (int i = 0; i < splits.length; i++) {
            splitsAtAll += weights[i];
            boolean found = false;
            for (int j = 0; j < ss.splits.length; j++) {
                int same = 0;
                int opposite = 0;
                for (int i1 = 0; i1 < ss.splits[0].length; i1++) {
                    if (splits[i][i1] == ss.splits[j][i1]) {
                        same++;
                    } else {
                        opposite++;
                    }
                }
                if (same == 0 || opposite == 0) {
                    //if(Math.min(weights[i],ss.weights[j])/Math.max(weights[i],ss.weights[j]) > 0.000000001)
                    //{
                    sameSplits += weights[i];
                    found = true;
                    //}
                }
            }

//            if(found == false)
//            {
//                ss.printSplit(i);
//            }
        }
        return (double) sameSplits / splitsAtAll;
    }

    public int falseSplits(SplitSystem ss) {
        int falseSplits = 0;

        for (int i = 0; i < ss.splits.length; i++) {
            boolean found = false;
            for (int j = 0; j < splits.length; j++) {
                int same = 0;
                int opposite = 0;
                for (int i1 = 0; i1 < ss.splits[0].length; i1++) {
                    if (splits[j][i1] == ss.splits[i][i1]) {
                        same++;
                    } else {
                        opposite++;
                    }
                }
                if (same == 0 || opposite == 0) {
                    if (Math.min(weights[j], ss.weights[i]) / Math.max(weights[j], ss.weights[i]) > 0.1) {
                        found = true;
                    }
                }
            }
            if (!found && ss.weights[i] > 0.1) {
                falseSplits++;
            }
        }
        return falseSplits;
    }

    public void setRandomWeights() {
        weights = new double[splits.length];
        trivialWeights = new double[nTaxa];
        for (int i = 0; i < weights.length; i++) {
            if (isTrivial(splits[i])) {
                weights[i] = 0;
            } else {
                weights[i] = Math.random();
            }
        }
        for (int i = 0; i < trivialWeights.length; i++) {
            trivialWeights[i] = Math.random();
        }
    }

    public double whatDoWeGetBackWithTrivial(SplitSystem ss) {
        double atAll = 0;
        double getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            atAll += weights[i];
            if (ss.splitExists(splits[i])) {
                getBack += weights[i];
            }
        }
        for (int i = 0; i < trivialWeights.length; i++) {
            atAll += trivialWeights[i];
            if (ss.trivialWeights[i] > 0) {
                getBack += trivialWeights[i];
            }
        }

        return (getBack / atAll);
    }

    public double whatDoWeGetBack2(SplitSystem ss) {
        double atAll = 0;
        double getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            atAll += weights[i];
            if (ss.splitExists(splits[i])) {
                getBack += Math.min(weights[i], ss.getWeight(splits[i]));
            }
        }

        return (getBack / atAll);
    }

    public boolean splitExists(boolean[] i) {
        for (int j = 0; j < splits.length; j++) {
            int same = 0;
            int opposite = 0;
            for (int i1 = 0; i1 < splits[0].length; i1++) {
                if (splits[j][i1] == i[i1]) {
                    same++;
                } else {
                    opposite++;
                }
            }
            if ((same == 0 || opposite == 0) && active[j]) {
                return true;
            }
        }
        return false;
    }

    private double splitExists2(boolean[] i) {
        boolean found = false;
        for (int j = 0; j < splits.length; j++) {
            int same = 0;
            int opposite = 0;
            for (int i1 = 0; i1 < splits[0].length; i1++) {
                if (splits[j][i1] == i[i1]) {
                    same++;
                } else {
                    opposite++;
                }
            }
            if (same == 0 || opposite == 0) {
                return weights[j];
            }
        }
        return 0;
    }

    private int splitNr(boolean[] i) {
        for (int j = 0; j < splits.length; j++) {
            int same = 0;
            int opposite = 0;
            for (int i1 = 0; i1 < splits[0].length; i1++) {
                if (splits[j][i1] == i[i1]) {
                    same++;
                } else {
                    opposite++;
                }
            }
            if (same == 0 || opposite == 0) {
                return j;
            }
        }
        return -1;
    }

    private void printSplit(int j) {
        for (int i = 0; i < splits[j].length; i++) {
            if (splits[j][i] == true) {
                System.out.print(i + " ");
            }
        }
        System.out.print("|");
        for (int i = 0; i < splits[j].length; i++) {
            if (splits[j][i] == false) {
                System.out.print(" " + i);
            }
        }
        System.out.println();
    }

    public void compare(SplitSystem ssr) {
        int rounding = 1000;
        for (int i = 0; i < splits.length; i++) {
            int j = ssr.splitNr(splits[i]);
            if (j > -1) {
                System.out.println((double) (Math.round(rounding * Math.abs(weights[i] - ssr.weights[j]) / weights[i])) / rounding);
            } else {
                System.out.println("Split " + i + " is missing.");
            }
        }

    }

    public void unifyWeights() {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1.0;
        }
    }

    public double whatDoWeGetBackWithoutTrivial(SplitSystem ss) {
        double atAll = 0;
        double getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            if (!isTrivial(splits[i])) {
                atAll += weights[i];
                if (ss.splitExists(splits[i])) {
                    getBack += weights[i];
                }
            }
        }

        return (getBack / atAll);
    }

    public boolean isTrivial(boolean[] b) {
        int s = Utilities.size(b);
        int e = b.length;
        if (s == 1 || e - s == 1) {
            return true;
        }
        return false;
    }

    public double whatDoWeGetBackUnit(SplitSystem ss) {
        int atAll = 0;
        int getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            atAll++;
            if (ss.splitExists(splits[i])) {
                getBack++;
            }
        }

        return ((double) getBack / (double) atAll);
    }

    public double whatDoWeGetBackUnitThree(SplitSystem ss1, SplitSystem ss2) {
        int atAll = 0;
        int getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            atAll++;
            if (ss1.splitExists(splits[i]) && ss2.splitExists(splits[i])) {
                getBack++;
            }
        }

        return ((double) getBack / (double) atAll);
    }

    public double whatDoWeGetBackWithoutTrivialUnit(SplitSystem ss) {
        int atAll = 0;
        int getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            if (!isTrivial(splits[i])) {
                atAll++;
                if (ss.splitExists(splits[i])) {
                    getBack++;
                }
            }
        }

        return ((double) getBack / (double) atAll);
    }

    public SplitSystem cutUnimportantSplits(double fraction) {

        int nonTrivial = nonTrivialSplits();
        int f = (int) (fraction * nonTrivial);

        double[] w = new double[nonTrivial];
        int k = 0;
        for (int i = 0; i < splits.length; i++) {
            if (!isTrivial(splits[i])) {
                w[k++] = weights[i];
            }
        }
        Arrays.sort(w);
        double treshold = w[w.length - f];
        SplitSystem ssNew = new SplitSystem();
        ssNew.splits = new boolean[f][splits[0].length];
        ssNew.weights = new double[f];
        int i = 0;
        for (int j = 0; j < splits.length; j++) {
            if (weights[j] >= treshold && !isTrivial(splits[j])) {
                System.arraycopy(splits[j], 0, ssNew.splits[i], 0, splits[j].length);
                ssNew.weights[i] = weights[j];
                i++;
            }
        }
        ssNew.nSplits = f;
        ssNew.nTaxa = nTaxa;

        return ssNew;
    }

    public int nonTrivialSplits() {
        int nonTrivial = 0;
        for (int i = 0; i < splits.length; i++) {
            if (!isTrivial(splits[i]) && active[i]) {
                nonTrivial++;
            }
        }
        return nonTrivial;
    }

    private double getWeight(boolean[] b) {
        int i = splitNumber(b);
        if (i > -1) {
            return weights[i];
        }
        return 0;
    }

    private int splitNumber(boolean[] i) {
        for (int j = 0; j < splits.length; j++) {
            int same = 0;
            int opposite = 0;
            for (int i1 = 0; i1 < splits[0].length; i1++) {
                if (splits[j][i1] == i[i1]) {
                    same++;
                } else {
                    opposite++;
                }
            }
            if (same == 0 || opposite == 0) {
                return j;
            }
        }
        return -1;
    }

    public double same(SplitSystem ss) {
        double same = 0;

        for (int i = 0; i < splits.length; i++) {
            if (ss.splitExists(splits[i])) {
                same += Math.min(weights[i], ss.getWeight(splits[i]));
            }
        }

        return same;
    }

    public double same(SplitSystem ss1, SplitSystem ss2) {
        double same = 0;

        for (int i = 0; i < splits.length; i++) {
            if (ss1.splitExists(splits[i]) && ss2.splitExists(splits[i])) {
                double min = (weights[i] < ss1.getWeight(splits[i])) ? weights[i] : ss1.getWeight(splits[i]);
                min = (min < ss2.getWeight(splits[i])) ? min : ss2.getWeight(splits[i]);

                same += min;
            }
        }

        return same;
    }

    public double totalWeight() {
        double w = 0;
        for (int i = 0; i < weights.length; i++) {
            w += weights[i];
        }
        for (int i = 0; i < trivialWeights.length; i++) {
            w += trivialWeights[i];
        }
        return w;
    }

    public void normalizeWeights() {
        int nonTrivial = nonTrivialSplits();

        double[] w = new double[nonTrivial];
        int k = 0;
        for (int i = 0; i < splits.length; i++) {
            if (!isTrivial(splits[i])) {
                w[k++] = weights[i];
            }
        }
        Arrays.sort(w);
        for (int i = 0; i < weights.length; i++) {
            weights[i] /= w[w.length - 1];
        }
    }

    public double whatDoWeGetBackNoTrivialWithWeightThreshold(SplitSystem ss, double tolerance) {
        int atAll = 0;
        int getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            if (!isTrivial(splits[i])) {
                atAll++;
                if (ss.splitExists(splits[i]) && ss.getWeight(splits[i]) >= weights[i] * (1 - tolerance) && ss.getWeight(splits[i]) <= weights[i] * (1 + tolerance)) {
                    getBack++;
                }
            }
        }

        return ((double) getBack / (double) atAll);
    }

    public double leastSquaresDifference(SplitSystem ss) {
        int diff = 0;
        int getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            if (ss.splitExists(splits[i])) {
                diff += (ss.getWeight(splits[i]) - weights[i]) * (ss.getWeight(splits[i]) - weights[i]);
            }
        }
        for (int i = 0; i < trivialWeights.length; i++) {
            diff += (ss.trivialWeights[i] - trivialWeights[i]) * (ss.trivialWeights[i] - trivialWeights[i]);
        }

        return Math.sqrt(diff);
    }

    public int commonSplits(SplitSystem ss2) {
        int atAll = 0;
        int getBack = 0;

        for (int i = 0; i < splits.length; i++) {
            if (!isTrivial(splits[i]) && active[i]) {
                atAll++;
                if (ss2.splitExists(splits[i])) {
                    getBack++;
                }
            }
        }

        return getBack;//((double)getBack / (double)atAll);
    }

    public double[] getWeights() {
        return weights;
    }

    public int getnTaxa() {
        return nTaxa;
    }

    public int getnSplits() {
        return nSplits;
    }

    public boolean[][] getSplits() {
        return splits;
    }

    public void setWeights(double[] weights, double[] minTrivial) {
        this.weights = new double[weights.length];
        trivialWeights = new double[nTaxa];
        for (int i = 0; i < weights.length; i++) {
            if (isTrivial(splits[i]) && minTrivial != null) {
                this.weights[i] = minTrivial[trivialTaxa(splits[i])];
            }
            this.weights[i] += weights[i];
        }
    }

    public void printDistanceMatrix(String filePath) throws IOException {
        Writer writer = new FileWriter(new File(filePath));

        Utilities.printFictitiousTaxaBlock(writer, nTaxa);

        writer.write("BEGIN DISTANCES;\n");
        writer.write("DIMENSIONS NTAX=" + nTaxa + ";\n");
        writer.write("FORMAT\n");
        writer.write("\ttriangle=LOWER\n\tdiagonal\n\tlabels\n\tmissing=?\n;\n");
        writer.write("MATRIX\n");

        for (int i = 0; i < nTaxa; i++) {
            writer.write("\tTaxon" + i + "\t");
            for (int j = 0; j <= i; j++) {
                writer.write(" " + distance(i, j));
            }
            writer.write("\n");
        }

        writer.write(";\nEND;\n");
        writer.close();
    }

    public double distance(int x1, int x2) {
        double distance = 0.0;
        for (int i = 0; i < splits.length; i++) {
            if (splits[i][x1] ^ splits[i][x2]) {
                distance += weights[i];
            }
        }
        return distance;
    }

    protected int trivialTaxa(boolean[] b) {
        int len = Utilities.size(b);
        boolean right = true;
        if (len > 1) {
            right = false;
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i] == right) {
                return i;
            }
        }
        return -1;
    }

    public double[] getTrivialWeights() {
        return trivialWeights;
    }

    public void printSplitsBoth(SplitSystem ss) {
        int i = 0;
        int j = 0;

        double[] trivial = new double[nTaxa];

        System.out.println("Number of taxa: " + nTaxa + " *");
        System.out.println("Number of splits: " + (nonTrivialSplits() + nTaxa) + " *");

        System.out.println("List of splits:");
        System.out.println("Not trivial:");

        for (i = 0; i < nSplits; i++) {
            if (!isTrivial(splits[i])) {
                System.out.print(weights[i] + " : ");
                for (j = 0; j < nTaxa; j++) {
                    int x = (splits[i][j]) ? 1 : 0;
                    System.out.print(x);
                    if (j < nTaxa - 1) {
                        System.out.print("-");
                    }
                }
                System.out.println(" " + ss.getWeight(splits[i]));
            } else {
                trivial[trivialTaxa(splits[i])] = weights[i];
            }
        }
        System.out.println("Trivial:");
        for (i = 0; i < nTaxa; i++) {
            System.out.print((trivial[i] + trivialWeights[i]) + " : ");
            for (j = 0; j < nTaxa; j++) {
                int x = (i == j) ? 1 : 0;
                System.out.print(x);
                if (j < nTaxa - 1) {
                    System.out.print("-");
                }
            }
            System.out.println(" " + ss.trivialWeights[i]);
        }

    }

    public boolean isCompatible(int a, int b) {
        //variables for counting the number of occurences of patterns
        int count11 = 0;
        int count10 = 0;
        int count01 = 0;
        int count00 = 0;

        for (int i = 0; i < nTaxa; i++) {
            if ((splits[a][i] == true) && (splits[b][i] == true)) {
                count11++;
            }
            if ((splits[a][i] == true) && (splits[b][i] == false)) {
                count10++;
            }
            if ((splits[a][i] == false) && (splits[b][i] == true)) {
                count01++;
            }
            if ((splits[a][i] == false) && (splits[b][i] == false)) {
                count00++;
            }
        }

        if (count11 == 0 || count10 == 0 || count01 == 0 || count00 == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int[] getCycle() {
        return cycle;
    }

    public String[] getTaxaNames() {
        return taxaNames;
    }

    public boolean[] getActive() {
        return active;
    }

    public void setActive(boolean[] active) {
        this.active = active;
    }

    public void setFit(double fit) {
        this.fit = fit;
    }

    public double getWeightForSplitsNoTrivial(Set<Integer> uniq) {
        double all = 0.0;
        double uni = 0.0;

        for (int i = 0; i < splits.length; i++) {
            if (active[i] && !isTrivial(splits[i])) {
                all += weights[i];
                if (uniq.contains(i)) {
                    uni += weights[i];
                }
            }
        }

        return uni / all;
    }

    public void filterSplits(double threshold) {
        if (active == null) {
            active = new boolean[nSplits];
        }

        if (Utilities.size(active) == 0) {
            for (int i = 0; i < active.length; i++) {
                active[i] = true;
            }
        }

        for (int i = splits.length - 1; i >= 0; i--) {
            for (int j = 0; j < splits.length; j++) {
                if (!isCompatible(i, j)) {
                    if (weights[i] < weights[j] * threshold) {
                        active[i] = false;
                    } else if (weights[j] < weights[i] * threshold) {
                        active[j] = false;
                    }
                }
            }
        }

        nSplits = Utilities.size(active);

        boolean[][] splits2 = new boolean[nSplits][nTaxa];
        double[] weights2 = new double[nSplits];
        boolean[] active2 = new boolean[nSplits];

        int j = 0;

        for (int i = 0; i < active.length; i++) {
            if (active[i]) {
                System.arraycopy(splits[i], 0, splits2[j], 0, nTaxa);
                weights2[j] = weights[i];
                active2[j] = true;
                j++;
            }
        }

        splits = splits2;
        weights = weights2;
        active = active2;
    }

    public int sizeNoTrivial() {
        int size = Utilities.size(active);
        for (int i = 0; i < splits.length; i++) {
            if (active[i] && isTrivial(splits[i])) {
                size--;
            }
        }
        return size;
    }

    public double getBackWithSimilarityThreshold(SplitSystem ss2, double threshold) {
        Set<Integer> indices = similarSplits(ss2, threshold);
        double atAll = 0.0;
        double same = 0.0;
        for (int i = 0; i < splits.length; i++) {
            if (indices.contains(i)) {
                same += weights[i];
            }
            if (active[i] && !isTrivial(splits[i])) {
                atAll += weights[i];
            }
        }
        return same / atAll;
    }

    public int commonSplitsSimilarityThreshold(SplitSystem ss2, double threshold) {
        return similarSplits(ss2, threshold).size();
    }

    public Set<Integer> similarSplits(SplitSystem ss2, double threshold) {
        int distThr = (int) (nTaxa * threshold);
        Set<Integer> indices = new HashSet<>();

        boolean[][] splits2 = ss2.getSplits();
        boolean[] active2 = ss2.getActive();

        for (int i = 0; i < splits.length; i++) {
            for (int j = 0; j < splits2.length; j++) {
                if (!isTrivial(splits[i]) && !isTrivial(splits2[j]) && active[i] && active2[j]) {
                    int dist = minHamming(splits[i], splits2[j]);
                    if (dist <= distThr) {
//                        System.out.println(dist + " <= " + distThr);
//                        System.out.println(Arrays.toString(splits[i]));
//                        System.out.println(Arrays.toString(splits2[j]));
//                        System.out.println();
                        indices.add(i);
                        break;
                    }
                }
            }
        }

        return indices;
    }

    private int minHamming(boolean[] b1, boolean[] b2) {
        int dist = 0;
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                dist++;
            }
        }
        return Math.min(dist, nTaxa - dist);
    }

}