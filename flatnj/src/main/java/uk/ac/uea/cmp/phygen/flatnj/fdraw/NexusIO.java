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

package uk.ac.uea.cmp.phygen.flatnj.fdraw;


import uk.ac.uea.cmp.phygen.core.ds.Alignment;
import uk.ac.uea.cmp.phygen.flatnj.ds.Locations;

import java.io.*;
import java.util.*;

//This class handles the input/output
//from to nexus files
public class NexusIO {
//***********************************************************************
//Methods to open and close a file
//***********************************************************************

    public static PrintWriter openprintwriter(String filename) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        } catch (IOException exception) {
            System.out.println("Error while creating nexus file");
        }
        return pw;
    }

    public static void closeprintwriter(PrintWriter pw) {
        pw.close();
    }

    public static LineNumberReader openlinereader(String filename) {
        LineNumberReader ln_reader = null;
        try {
            ln_reader = new LineNumberReader(new FileReader(filename));
        } catch (IOException exception) {
            System.out.println("Error: File \"" + filename + "\" not found.");
        }
        return ln_reader;
    }

    public static void closelinereader(LineNumberReader ln_reader) {
        try {
            ln_reader.close();
        } catch (IOException exception) {
            System.out.println("Error while reading nexus file.");
        }
    }

    //*************************************************************************
//Methods that write the various types of nexus blocks
//*************************************************************************
    //The header of the nexus file
    public static void writeheader(PrintWriter pw) {
        pw.println("#nexus");
        pw.println("");
    }

    //The taxa block of the nexus file
    public static void writetaxa(int ntaxa, String[] taxaname, PrintWriter pw) {
        int i = 0;

        pw.println("BEGIN TAXA;");
        pw.print("DIMENSIONS NTAX=");
        pw.print(ntaxa);
        pw.println(";");
        pw.println("TAXLABELS");
        for (i = 0; i < ntaxa; i++) {
            pw.print("[");
            pw.print(i + 1);
            pw.print("]  '");
            pw.print(taxaname[i]);
            pw.println("'");
        }
        pw.println(";");
        pw.println("END;");
        pw.println("");
    }

    //The locations block of the nexus file
    public static void writelocations(int ntaxa, double[] xcoord, double[] ycoord, PrintWriter pw) {
        int i = 0;

        pw.println("BEGIN LOCATIONS;");
        pw.print("DIMENSIONS NTAX=");
        pw.print(ntaxa);
        pw.println(";");
        pw.println("MATRIX");
        for (i = 0; i < ntaxa; i++) {
            pw.print("[");
            pw.print(i + 1);
            pw.print("] ");
            pw.print(xcoord[i]);
            pw.print(" ");
            pw.print(ycoord[i]);
            pw.println(",");
        }
        pw.println(";");
        pw.println("END;");
        pw.println("");
    }

    //The distances block of the nexus file
    public static void writedistances(int ntaxa, double[][] dist, PrintWriter pw) {
        int i = 0;
        int j = 0;

        pw.println("BEGIN DISTANCES;");
        pw.print("DIMENSIONS NTAX=");
        pw.print(ntaxa);
        pw.println(";");
        pw.println("FORMAT TRIANGLE=both DIAGONAL LABELS=no;");
        pw.println("MATRIX");
        for (i = 0; i < ntaxa; i++) {
            pw.print("[");
            pw.print(i + 1);
            pw.print("]");
            for (j = 0; j < ntaxa; j++) {
                pw.print(" ");
                pw.print(dist[i][j]);
            }
            pw.println("");
        }
        pw.println(";");
        pw.println("END;");
        pw.println("");
    }

    //The flatsplits block of the nexus file
    public static void writesplits(int ntaxa, int nsplits, PermutationSequenceDraw psequ, PrintWriter pw) {
        int i = 0;
        int j = 0;
        int idx = 0;

        SplitSystemDraw ssyst = new SplitSystemDraw(psequ);

        pw.println("BEGIN SPLITS;");
        pw.print("DIMENSIONS NTAX=");
        pw.print(ntaxa);
        pw.print(" NSPLITS=");
        pw.print(nsplits);
        pw.println(";");
        pw.print("FORMAT LABELS=NO WEIGHTS=");
        if (psequ.hasWeights) {
            pw.println("yes;");
        } else {
            pw.println("no;");
        }
        pw.println("MATRIX");

        for (i = 0; i < psequ.nswaps; i++) {
            if (psequ.active[i]) {
                idx++;
                pw.print("[" + idx + "]");

                if (psequ.hasWeights) {
                    pw.print(" " + psequ.weights[i]);
                }

                for (j = 0; j < psequ.ntaxa; j++) {
                    if (ssyst.splits[i][j] == 1) {
                        pw.print(" " + (j + 1));
                    }
                }
                pw.println(",");
            }
        }

        pw.println(";");
        pw.println("END;");
        pw.println("");
    }

    //The flatsplits block of the nexus file
    public static void writeflatsplits(int ntaxa, int nsplits, PermutationSequenceDraw psequ, PrintWriter pw) {
        int i = 0;

        pw.println("BEGIN FLATSPLITS;");
        pw.print("DIMENSIONS NTAX=");
        pw.print(ntaxa);
        pw.print(" NSPLITS=");
        pw.print(nsplits);
        pw.println(";");
        pw.print("FORMAT WEIGHTS=");
        if (psequ.hasWeights) {
            pw.print("yes");
        } else {
            pw.print("no");
        }
        if (psequ.hasActiveFlags) {
            pw.println(" ACTIVEFLAGS=yes;");
        } else {
            pw.println(" ACTIVEFLAGS=no;");
        }
        pw.print("CYCLE ");
        for (i = 0; i < ntaxa; i++) {
            pw.print(" ");
            pw.print(psequ.initSequ[i] + 1);
        }
        pw.println(";");
        pw.println("MATRIX");
        for (i = 0; i < nsplits; i++) {
            pw.print(psequ.swaps[i] + 1);
            if (psequ.hasActiveFlags) {
                pw.print(" ");
                if (psequ.active[i] == true) {
                    pw.print("1");
                } else {
                    pw.print("0");
                }
            }
            if (psequ.hasWeights) {
                pw.print(" ");
                pw.print(psequ.weights[i]);
            }
            pw.println(",");
        }
        pw.println(";");
        pw.println("END;");
        pw.println("");
    }

    //The network block of the nexus file
    public static void writenetwork(int ntaxa, int nvert, int nedges, PermutationSequenceDraw psequ, LinkedList vlist, LinkedList elist, PrintWriter pw) {
        int i = 0;
        ListIterator iter = null;
        ListIterator taxiter = null;
        Vertex v = null;
        Edge e = null;

        pw.println("BEGIN NETWORK;");
        pw.print("DIMENSIONS NTAX=");
        pw.print(ntaxa);
        pw.print(" NVERTICES=");
        pw.print(nvert);
        pw.print(" NEDGES=");
        pw.print(nedges);
        pw.println(";");
        pw.println("DRAW to_scale;");
        pw.println("TRANSLATE");
        //write translate section
        iter = vlist.listIterator();
        while (iter.hasNext()) {
            v = (Vertex) iter.next();
            if (v.taxa.size() > 0) {
                taxiter = v.taxa.listIterator();
                pw.print(v.nxnum);

                while (taxiter.hasNext()) {
                    pw.print(" '" + psequ.taxaname[((Integer) taxiter.next()).intValue()] + "'");
                }

                pw.println(",");
            }
        }
        pw.println(";");
        //write vertices section
        pw.println("VERTICES");
        iter = vlist.listIterator();
        while (iter.hasNext()) {
            v = (Vertex) iter.next();
            pw.print(v.nxnum);
            pw.print(" ");
            pw.print(v.x);
            pw.print(" ");
            pw.print(v.y);
            //if(v.taxa.size() == 0)
            {
                pw.print(" w=" + v.width + " h=" + v.height + " bg=" + v.bgColor.getRed() + " " + v.bgColor.getGreen() + " " + v.bgColor.getBlue());
            }
            //----------------- just for texting, so that vertices that have some taxa would be visible ------
//            else
//            {
//                pw.print(" w=4 h=4 bg=255 255 0");
//            }
            if (v.taxa.size() == 0 && v.height == 2) {
                pw.println(" s=n,");
            } else {
                pw.println(",");
            }
        }
        pw.println(";");
        //write vertex labels section
        pw.println("VLABELS");
        iter = vlist.listIterator();
        while (iter.hasNext()) {
            v = (Vertex) iter.next();
            if (v.taxa.size() > 0) {
                pw.print(v.nxnum);
                pw.print(" '");
                String label = new String();
                taxiter = v.taxa.listIterator();
                while (taxiter.hasNext()) {
                    label = (psequ.taxaname[((Integer) taxiter.next()).intValue()] + ", ").concat(label);
                    //--------------------- just for testing, so that labels are nor visible --------
                    //label = "";
                }
                label = label.substring(0, label.length() - 2);
                pw.print(label);
                pw.println("' x=2 y=2 f='Dialog-PLAIN-10',");
            }
        }
        pw.println(";");
        //Write the edges.
        pw.println("EDGES");
        iter = elist.listIterator();
        while (iter.hasNext()) {
            e = (Edge) iter.next();
            pw.print(e.nxnum);
            pw.print(" ");
            pw.print((e.top).nxnum);
            pw.print(" ");
            pw.print((e.bot).nxnum);
            pw.print(" ");
            pw.print("s=");
            pw.print((psequ.compressed[e.idxsplit] + 1));
            pw.print(" ");
            pw.print("l=" + e.width);
            pw.print(" ");
            pw.print("fg=" + e.color.getRed() + " " + e.color.getGreen() + " " + e.color.getBlue());
            pw.println(",");
        }
        pw.println(";");
        pw.println("END;");
    }

    //*********************************************************************
//methods that read various blocks from a nexus file
//*********************************************************************
    //the taxa block
    public static String[] readtaxa(LineNumberReader lnr) {
        String line = null;
        String linelc = null;
        String matched = null;
        Scanner scanner = null;
        Scanner scannerlc = null;
        int status = 0;

        int index = 0;
        int i = 0;
        int ntaxa = 0;
        int r = 0;

        String[] taxaname = null;

        while (status < 4) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error occured while reading taxa block.");
                status = 4;
                taxaname = null;
            }

            if ((status < 3) && (line == null)) {
                System.out.println("Error occured while parsing taxa block;");
                status = 4;
                taxaname = null;
            } else {
                linelc = line.toLowerCase();

                if (status == 0) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("begin(\\s+)taxa;");
                    if (matched != null) {
                        status = 1;
                    }
                } else if (status == 1) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("ntax=(\\d+)");
                    if (matched != null) {
                        status = 2;
                        ntaxa = Integer.parseInt((scannerlc.match()).group(1));
                        taxaname = new String[ntaxa];
                    }
                } else if (status == 2) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("taxlabels");
                    if (matched != null) {
                        status = 3;
                    }
                } else if (status == 3) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine(";");
                    if (matched != null) {
                        status = 4;
                        if (r < ntaxa) {
                            System.out.println("Number of taxa larger than number of labels");
                            taxaname = null;
                        }
                    } else {
                        scanner = new Scanner(line);
                        matched = scanner.findInLine("\\[(\\d+)\\](\\s+)('*)([^']+)");
                        if (matched != null) {
                            index = Integer.parseInt((scanner.match()).group(1));
                            taxaname[index - 1] = (scanner.match()).group(4);
                            r++;
                        }
                    }
                }
            }
        }
        return taxaname;
    }

    //Read a circular split system from a splits block
    //and transform it into the permutation sequence.
    //This allows us to directly compare the drawings
    //produced by SplitsTree and our new algorithm for
    //circular split systems.
    public static int readcircularsplitsystem(PermutationSequenceDraw psequ, LineNumberReader lnr) {
        String line = null;
        String linelc = null;
        String matched = null;
        String cycle = null;
        String row = null;
        Scanner scanner = null;
        Scanner scannerlc = null;
        int status = 0;

        int posweights = 0;

        int index = 0;
        int h = 0;
        int i = 0;
        int j = 0;
        int k = 0;
        int r = 0;

        Split s = null;
        Split scirc = null;
        Split stemp = null;
        TreeSet<Split> ssyst = new TreeSet(new Split());
        TreeSet<Split> template = new TreeSet(new Split());

        //first parse the splits block and extract the splits
        //and their weights
        while (status < 6) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error occured while reading splits block.");
                status = -1;
                break;
            }

            if ((status < 6) && (line == null)) {
                System.out.println("Error occured while parsing flatsplits block");
                status = -1;
                break;
            } else {
                linelc = line.toLowerCase();

                if (status == 0) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("begin(\\s+)splits;");
                    if (matched != null) {
                        status = 1;
                    }
                } else if (status == 1) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("dimensions(\\s+)ntax=(\\S+)(\\s+)nsplits=(\\S+);");
                    if (matched != null) {
                        status = 2;
                        if (Integer.parseInt((scannerlc.match()).group(2)) != psequ.ntaxa) {
                            System.out.println("Number of taxa is not okay");
                            status = -1;
                            break;
                        }
                    }
                } else if (status == 2) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("format(\\s+)labels=(\\S+)(\\s+)weights=(\\S+)");
                    if (matched != null) {
                        status = 3;
                        if (((scannerlc.match()).group(2)).equals("yes")) {
                            posweights = 1;
                        }
                        if (((scannerlc.match()).group(4)).equals("yes")) {
                            psequ.hasWeights = true;
                        } else {
                            psequ.hasWeights = false;
                        }
                        if (((scannerlc.match()).group(4)).equals("yes")) {
                            psequ.hasActiveFlags = true;
                        } else {
                            psequ.hasActiveFlags = false;
                        }
                    }
                } else if (status == 3) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("cycle(\\s+)(.+);");
                    if (matched != null) {
                        status = 4;
                        cycle = (scannerlc.match()).group(2);
                        cycle = cycle.trim();
                        i = 0;
                        while (cycle.length() > 0) {
                            index = cycle.indexOf(' ');
                            if (index == -1) {
                                index = cycle.length();
                            }
                            //note that in nexus files the taxa are numbered 1,2,...,ntaxa
                            k = Integer.parseInt(cycle.substring(0, index));
                            psequ.initSequ[i] = k - 1;
                            cycle = cycle.substring(index);
                            cycle = cycle.trim();
                            i++;
                        }
                        if (i < psequ.ntaxa) {
                            System.out.println("Cycle contains too few elements");
                            status = -1;
                            break;
                        }
                    }
                } else if (status == 4) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("matrix");
                    if (matched != null) {
                        status = 5;
                        System.out.println("Read splits");
                    }
                } else if (status == 5) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine(";");
                    if (matched != null) {
                        status = 6;
                    } else {
                        scanner = new Scanner(line);
                        matched = scanner.findInLine("\\](\\s*)(\\S*)(\\s*)(((\\d+)(\\s+))*(\\d+)),");
                        if (matched != null) {
                            if (psequ.hasWeights) {
                                if (posweights == 0) {
                                    s = new Split(Double.parseDouble((scanner.match()).group(2)), psequ.ntaxa, -1);
                                } else {
                                    s = new Split(Double.parseDouble((scanner.match()).group(5)), psequ.ntaxa, -1);
                                }
                            } else {
                                s = new Split(1.0, psequ.ntaxa, -1);
                            }
                            for (j = 0; j < s.ntaxa; j++) {
                                s.s[j] = 0;
                            }
                            row = (scanner.match()).group(4);
                            row = row.trim();
                            while (row.length() > 0) {
                                index = row.indexOf(' ');
                                if (index == -1) {
                                    index = row.length();
                                }
                                s.s[Integer.parseInt(row.substring(0, index)) - 1] = 1;
                                row = row.substring(index);
                                row = row.trim();
                            }
                            ssyst.add(s);
                        }
                    }
                }
            }
        }

        //now get the splits into a permutation sequence
        int[] cur_sequ = new int[psequ.ntaxa];

        //Initialize current sequence with initial permutation
        for (i = 0; i < psequ.ntaxa; i++) {
            cur_sequ[i] = psequ.initSequ[i];
        }

        //run through splits in the order in which they occur
        //in the permutation sequence
        for (i = 0; i < psequ.nswaps; i++) {
            //compute current permutation
            h = cur_sequ[psequ.swaps[i]];
            cur_sequ[psequ.swaps[i]] = cur_sequ[psequ.swaps[i] + 1];
            cur_sequ[psequ.swaps[i] + 1] = h;

            s = new Split(1.0, psequ.ntaxa, i);

            //turn it into a 0/1 sequence
            for (j = 0; j < psequ.ntaxa; j++) {
                if (j <= psequ.swaps[i]) {
                    s.s[cur_sequ[j]] = 1;
                } else {
                    s.s[cur_sequ[j]] = 0;
                }
            }

            template.add(s);

            s = new Split(1.0, psequ.ntaxa, i);

            //turn it into a 0/1 sequence
            for (j = 0; j < psequ.ntaxa; j++) {
                if (j <= psequ.swaps[i]) {
                    s.s[cur_sequ[j]] = 0;
                } else {
                    s.s[cur_sequ[j]] = 1;
                }
            }

            template.add(s);
        }

        //now add information to permutation sequence
        while (true) {
            if (template.isEmpty()) {
                break;
            }

            stemp = (Split) template.first();

            if (ssyst.isEmpty()) {
                template.remove(stemp);
                psequ.weights[stemp.index] = 0.0;
                psequ.active[stemp.index] = false;
            } else {
                while (true) {
                    scirc = (Split) ssyst.first();
                    h = ((new Split()).compare(stemp, scirc));
                    if (h == -1) {
                        template.remove(stemp);
                        psequ.weights[stemp.index] = 0.0;
                        psequ.active[stemp.index] = false;
                        break;
                    }
                    if (h == 0) {
                        template.remove(stemp);
                        ssyst.remove(scirc);
                        psequ.weights[stemp.index] = scirc.weight;
                        psequ.active[stemp.index] = true;
                        break;
                    }
                    if (h == 1) {
                        System.out.println("Error while running trough lists of splits");
                        return -1;
                    }
                }
            }
        }

        psequ.nActive = 0;

        for (i = 0; i < psequ.nswaps; i++) {
            if (psequ.active[i]) {
                psequ.nActive++;
            }
        }

        return status;
    }

    //the flatsplits block
    public static int readflatsplits(PermutationSequenceDraw psequ, LineNumberReader lnr) {
        String line = null;
        String linelc = null;
        String matched = null;
        String cycle = null;
        Scanner scanner = null;
        Scanner scannerlc = null;
        int status = 0;

        int index = 0;
        int i = 0;
        int j = 0;
        int k = 0;

        while (status < 7) {
            try {
                line = lnr.readLine();
                line = (line != null) ? line.toLowerCase() : line;
            } catch (IOException exception) {
                System.out.println("Error occured while reading flatsplits block.");
                status = -1;
                break;
            }

            if ((status < 7) && (line == null)) {
                System.out.println("Error occured while parsing flatsplits block.");
                status = -1;
                break;
            } else {
                linelc = line.toLowerCase();

                if (status == 0) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("begin(\\s+)flatsplits;");
                    if (matched != null) {
                        status = 1;
                    }
                } else if (status == 1) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("dimensions(\\s+)ntax=(\\S+)(\\s+)nsplits=(\\S+);");
                    if (matched != null) {
                        status = 2;
                        if (Integer.parseInt((scannerlc.match()).group(2)) != psequ.ntaxa) {
                            System.out.println("Number of taxa is not okay");
                            status = -1;
                            break;
                        }
                        if (Integer.parseInt((scannerlc.match()).group(4)) != psequ.nswaps) {
                            System.out.println("Number of splits is not okay");
                            status = -1;
                            break;
                        }
                    }
                } else if (status == 2) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("format(\\s+)weights=(\\S+)(\\s+)activeflags=(\\S+);");
                    if (matched != null) {
                        status = 3;
                        if (((scannerlc.match()).group(2)).equals("yes")) {
                            psequ.hasWeights = true;
                        } else {
                            psequ.hasWeights = false;
                        }
                        if (((scannerlc.match()).group(4)).equals("yes")) {
                            psequ.hasActiveFlags = true;
                        } else {
                            psequ.hasActiveFlags = false;
                        }
                    }
                } else if (status == 3) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("cycle(\\s+)(.+);");
                    if (matched != null) {
                        status = 4;
                        cycle = (scannerlc.match()).group(2);
                        cycle = cycle.trim();
                        i = 0;
                        while (cycle.length() > 0) {
                            index = cycle.indexOf(' ');
                            if (index == -1) {
                                index = cycle.length();
                            }
                            //note that in nexus files the taxa are numbered 1,2,...,ntaxa
                            k = Integer.parseInt(cycle.substring(0, index));
                            psequ.initSequ[i] = k - 1;
                            cycle = cycle.substring(index);
                            cycle = cycle.trim();
                            i++;
                        }
                        if (i < psequ.ntaxa) {
                            System.out.println("Cycle contains too few elements");
                            status = -1;
                            break;
                        }
                    }
                } else if (status == 4) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("matrix");
                    if (matched != null) {
                        status = 5;
                    }
                } else if (status == 5) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine(";");
                    if (matched == null) {
                        matched = scannerlc.findInLine("trivial");
                    } else {
                        status = 7;
                    }

                    if (matched != null) {
                        status = 6;
                        if (j < psequ.nswaps) {
                            System.out.println("Number of splits larger than number of matrix entries");
                            status = -1;
                            break;
                        }
                    } else {
                        scanner = new Scanner(line);
                        matched = scanner.findInLine("(\\d+)(\\s*)(\\S*)(\\s*)(\\S*),");
                        if (matched != null) {
                            psequ.swaps[j] = Integer.parseInt((scanner.match()).group(1)) - 1;
                            if (psequ.hasActiveFlags) {
                                if (Integer.parseInt((scanner.match()).group(3)) == 1) {
                                    psequ.active[j] = true;
                                } else {
                                    psequ.active[j] = false;
                                    psequ.nActive--;
                                }
                            }
                            if (psequ.hasWeights) {
                                if (psequ.hasActiveFlags) {
                                    psequ.weights[j] = Double.parseDouble((scanner.match()).group(5));
                                } else {
                                    psequ.weights[j] = Double.parseDouble((scanner.match()).group(3));
                                }
                                if (psequ.weights[j] == 0) {
                                    psequ.active[j] = false;
                                    psequ.nActive--;
                                }
                            }
                            j++;
                        }
                    }
                } else if (status == 6) {
                    scanner = new Scanner(line);
                    matched = scanner.findInLine(";");
                    if (matched == null) {
                        matched = scanner.findInLine("(\\d+)(\\s+)(\\S+),");
                        if (matched != null) {
                            int taxaNr = Integer.parseInt((scanner.match()).group(1)) - 1;
                            psequ.trivial[taxaNr] = Double.parseDouble((scanner.match()).group(3));
                        }
                    } else {
                        status = 7;
                    }
                }
            }
        }
        return status;
    }

    //the locations block
    public static int readlocations(double[] xcoord, double[] ycoord, LineNumberReader lnr) {
        String line = null;
        String linelc = null;
        String matched = null;
        Scanner scanner = null;
        Scanner scannerlc = null;
        int status = 0;

        int index = 0;
        int i = 0;
        int j = 0;
        int k = 0;

        while (status < 4) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error while reading locations block.");
                status = -1;
                break;
            }

            if ((status < 4) && (line == null)) {
                System.out.println("Error occured while parsing locations block");
                status = -1;
                break;
            } else {
                linelc = line.toLowerCase();

                if (status == 0) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("begin(\\s+)locations;");
                    if (matched != null) {
                        status = 1;
                    }
                } else if (status == 1) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("dimensions(\\s+)ntax=(\\S+);");
                    if (matched != null) {
                        status = 2;
                        if (Integer.parseInt((scannerlc.match()).group(2)) != xcoord.length) {
                            System.out.println("Number of taxa not okay");
                            status = -1;
                            break;
                        }
                    }
                } else if (status == 2) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("matrix");
                    if (matched != null) {
                        status = 3;
                    }
                } else if (status == 3) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine(";");
                    if (matched != null) {
                        status = 4;
                        if (j < xcoord.length) {
                            System.out.println("Number of locations larger than number of matrix entries");
                            status = -1;
                            break;
                        }
                    } else {
                        scanner = new Scanner(line);
                        matched = scanner.findInLine("\\[(\\d+)\\](\\s+)(\\S+)(\\s+)(\\S+)(\\s*),");
                        if (matched != null) {
                            index = Integer.parseInt((scanner.match()).group(1));
                            xcoord[index - 1] = Double.parseDouble((scanner.match()).group(3));
                            ycoord[index - 1] = Double.parseDouble((scanner.match()).group(5));
                            j++;
                        }
                    }
                }
            }
        }
        return status;
    }

    //the distances block
    public static int readDistances(double[][] dist, LineNumberReader lnr) {
        String line = null;
        String linelc = null;
        String part = null;
        String matched = null;
        String row = null;
        Scanner scanner = null;
        Scanner scannerlc = null;
        int status = 0;

        int index = 0;
        int i = 0;
        int j = 0;
        int k = 0;
        int r = 0;
        int c = 0;

        //If not specified otherwise in format we assume
        //that both triangles and the diagonal are there
        //and there are no labels.
        int diagonal = 1;
        int triangle = 0;
        int labels = 0;

        while (status < 5) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error while reading distances block.");
                status = -1;
                break;
            }

            if ((status < 5) && (line == null)) {
                System.out.println("Error occured while parsing distances block");
                status = -1;
                break;
            } else {
                linelc = line.toLowerCase();
                //scanner = new Scanner(line);
                //scannerlc = new Scanner(linelc);

                if (status == 0) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("begin(\\s+)distances;");
                    if (matched != null) {
                        status = 1;
                    }
                } else if (status == 1) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("dimensions(\\s+)ntax=(\\S+);");
                    if (matched != null) {
                        status = 2;
                        if (Integer.parseInt((scannerlc.match()).group(2)) != dist.length) {
                            System.out.println("Number of taxa not okay");
                            status = -1;
                            break;
                        }
                    }
                } else if (status == 2) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("format(.*);");
                    if (matched != null) {
                        status = 3;
                        part = (scannerlc.match()).group(1);

                        scannerlc = new Scanner(part);
                        matched = scannerlc.findInLine("triangle=(\\S+)");
                        if (matched != null) {
                            if (((scannerlc.match()).group(1)).equals("lower")) {
                                triangle = -1;
                            }
                            if (((scannerlc.match()).group(1)).equals("both")) {
                                triangle = 0;
                            }
                            if (((scannerlc.match()).group(1)).equals("upper")) {
                                triangle = 1;
                            }
                        }
                        //System.out.println("Triangle: "+triangle);

                        scannerlc = new Scanner(part);
                        matched = scannerlc.findInLine("no(\\s+)diagonal");
                        if (matched != null) {
                            diagonal = 0;
                        }
                        //System.out.println("Diagonal: "+diagonal);

                        scannerlc = new Scanner(part);
                        matched = scannerlc.findInLine("labels=(\\S+)");
                        if (matched != null) {
                            if (((scannerlc.match()).group(1)).equals("left")) {
                                labels = 1;
                            }
                            if (((scannerlc.match()).group(1)).equals("no")) {
                                labels = 0;
                            }
                        }
                        //System.out.println("Labels: "+labels);
                    }
                } else if (status == 3) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("matrix");
                    if (matched != null) {
                        status = 4;
                    }
                } else if (status == 4) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine(";");
                    if (matched != null) {
                        status = 5;
                        if (r < dist.length) {
                            System.out.println("Number of taxa larger than number of matrix rows");
                            status = -1;
                            break;
                        }
                    } else {
                        scanner = new Scanner(line);
                        if (labels == 1) {
                            matched = scanner.findInLine("(\\[(\\d+)\\])*(\\s*)(\\S+)(.*)");
                            if (matched != null) {
                                row = (scanner.match()).group(5);
                                row = row.trim();
                            }
                        } else {
                            matched = scanner.findInLine("(\\[(\\d+)\\])*(.*)");
                            if (matched != null) {
                                row = (scanner.match()).group(3);
                                row = row.trim();
                            }
                        }

                        if ((matched != null)) {
                            //rectangular matrix
                            if (triangle == 0) {
                                c = 0;
                                while (row.length() > 0) {
                                    index = row.indexOf(' ');
                                    if (index == -1) {
                                        index = row.length();
                                    }
                                    dist[r][c] = Double.parseDouble(row.substring(0, index));
                                    row = row.substring(index);
                                    row = row.trim();
                                    c++;
                                }
                                r++;
                            }

                            //lower triangle matrix without diagonal
                            if ((triangle == -1) && (diagonal == 0)) {
                                dist[r][r] = 0.0;
                                c = 0;
                                while (row.length() > 0) {
                                    index = row.indexOf(' ');
                                    if (index == -1) {
                                        index = row.length();
                                    }
                                    dist[r][c] = Double.parseDouble(row.substring(0, index));
                                    dist[c][r] = dist[r][c];
                                    row = row.substring(index);
                                    row = row.trim();
                                    c++;
                                }
                                r++;
                            }

                            //lower triangle matrix with diagonal
                            if ((triangle == -1) && (diagonal == 1)) {
                                c = 0;
                                while (row.length() > 0) {
                                    index = row.indexOf(' ');
                                    if (index == -1) {
                                        index = row.length();
                                    }
                                    dist[r][c] = Double.parseDouble(row.substring(0, index));
                                    dist[c][r] = dist[r][c];
                                    row = row.substring(index);
                                    row = row.trim();
                                    c++;
                                }
                                r++;
                            }

                            //upper triangle matrix without diagonal
                            if ((triangle == 1) && (diagonal == 0)) {
                                dist[r][r] = 0.0;
                                c = r + 1;
                                while (row.length() > 0) {
                                    index = row.indexOf(' ');
                                    if (index == -1) {
                                        index = row.length();
                                    }
                                    dist[r][c] = Double.parseDouble(row.substring(0, index));
                                    dist[c][r] = dist[r][c];
                                    row = row.substring(index);
                                    row = row.trim();
                                    c++;
                                }
                                r++;
                            }

                            //upper triangle matrix with diagonal
                            if ((triangle == 1) && (diagonal == 1)) {
                                c = r;
                                while (row.length() > 0) {
                                    index = row.indexOf(' ');
                                    if (index == -1) {
                                        index = row.length();
                                    }
                                    dist[r][c] = Double.parseDouble(row.substring(0, index));
                                    dist[c][r] = dist[r][c];
                                    row = row.substring(index);
                                    row = row.trim();
                                    c++;
                                }
                                r++;
                            }
                        }
                    }
                }
            }
        }
        return status;
    }

    public static Alignment readAlignment(LineNumberReader lnr, String inBlock, int nTaxa, String[] taxaNames) {
        Alignment a = null;

        String line = null;
        String linelc = null;
        String matched = null;
        Scanner scanner = null;
        Scanner scannerlc = null;
        int status = 0;

        int index = 0;
        int i = 0;
        int ntaxa = 0;
        int r = 0;

        boolean labels = false;

        Map<String, String> aln = null;

        while (status < 3) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error occured while reading " + inBlock + " block.");
                status = 4;
                a = null;
            }
            if ((status < 2) && (line == null)) {
                System.out.println("Error occured while parsing " + inBlock + " block;");
                status = 4;
                a = null;
            } else {
                linelc = line.toLowerCase();
                scannerlc = new Scanner(linelc);

                if (status == 0) {
                    matched = scannerlc.findInLine("begin(\\s+)" + inBlock.toLowerCase() + ";");
                    if (matched != null) {
                        status = 1;
                    }
                } else if (status == 1) {
                    //-------------------------------------------Check for ntax:
                    matched = scannerlc.findInLine("ntax\\s*=\\s*(\\d+);");
                    if (matched != null) {
                        ntaxa = Integer.parseInt((scannerlc.match()).group(1));
                    }
                    //-----------------------------------------Check for labels:
                    labels = findOptions(labels, "labels", scannerlc);

                    matched = scannerlc.findInLine("matrix");
                    boolean ok = false;
                    if (matched != null) {
                        status = 2;
                        if (nTaxa > 0 && ntaxa > 0 && nTaxa != ntaxa) {
                            System.err.println("Error: ntax in TAXA block is not equal to ntax in " + inBlock + " block.");
                        } else {
                            aln = new LinkedHashMap();
                        }
                    }
                } else if (status == 2) {
                    matched = scannerlc.findInLine(";");
                    if (matched == null) {
                        line = line.trim();
                        if (line.length() > 0) {
                            scannerlc = new Scanner(line);
                            matched = scanner.findInLine("\\[(\\d+)\\]");
                            {
                                if (matched != null) {
                                    index = Integer.parseInt((scannerlc.match()).group(1));
                                }
                                line = line.substring(line.indexOf(']') + 1);
                            }
                            if (labels) {
                                int spaceIndex = line.indexOf("\\s+");
                                String taxLabel = line.substring(0, spaceIndex);
                                String sequence = line.substring(spaceIndex + 1);
                                taxLabel = taxLabel.replace("\\s", "");
                                sequence = sequence.replace("\\s", "");
                                sequence = (aln.get(taxLabel) != null) ? aln.get(taxLabel).concat(sequence) : sequence;
                                aln.put(taxLabel, sequence);
                            } else {
                                String sequence = line.replace("\\s", "");
                                aln.put(String.valueOf(index), sequence);
                                index++;
                            }
                        } else {
                            index = 0;
                        }
                    } else {
                        status = 3;
                    }
                }
            }

        }
        if (nTaxa > 0 && nTaxa != aln.size()) {
            System.err.println("Wrong number of sequences in the " + inBlock + " block. Expected " + nTaxa + ", found " + aln.size());
        } else if (ntaxa > 0 && ntaxa != aln.size())

        {
            System.err.println("Wrong number of sequences in the " + inBlock + " block. Expected " + ntaxa + ", found " + aln.size());
        } else {
            if (labels) {
                a = new Alignment(aln);
            } else {
                if (taxaNames != null) {
                    taxaNames = new String[aln.size()];
                    for (int j = 0; j < taxaNames.length; j++) {
                        taxaNames[j] = "taxon" + j;
                    }
                }
                a = new Alignment(taxaNames, (String[]) aln.values().toArray());
            }
        }

        return a;

    }

    public static Locations readlocations(LineNumberReader lnr, String[] taxaNames) {
        String line = null;
        String linelc = null;
        String matched = null;
        Scanner scanner = null;
        Scanner scannerlc = null;
        int status = 0;

        int index = 0;
        int i = 0;
        int j = 0;
        int k = 0;

        int nTaxa = 0;
        boolean labels = false;

        LinkedList<Location> coordinates = new LinkedList();
        Locations locations = null;

        while (status < 3) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error while reading LOCATIONS block.");
                status = -1;
                break;
            }

            if ((status < 3) && (line == null)) {
                System.out.println("Error occured while parsing LOCATIONS block");
                status = -1;
                break;
            } else {
                linelc = line.toLowerCase();

                if (status == 0) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("begin(\\s+)locations;");
                    if (matched != null) {
                        status = 1;
                    }
                } else if (status == 1) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine("ntax\\s*=\\s*(\\S+);");
                    if (matched != null) {
                        status = 2;
                        nTaxa = Integer.parseInt((scannerlc.match()).group(1));
                        if (taxaNames != null && taxaNames.length != nTaxa) {
                            System.err.println("Error: ntax in TAXA block is not equal to ntax in LOCATIONS block.");
                            status = -1;
                            break;
                        }
                    }
                    labels = findOptions(labels, "labels", scannerlc);

                    matched = scannerlc.findInLine("matrix");
                    if (matched != null) {
                        if (nTaxa == 0 && taxaNames != null) {
                            nTaxa = taxaNames.length;
                        }
                        status = 2;
                    }
                } else if (status == 2) {
                    scannerlc = new Scanner(linelc);
                    matched = scannerlc.findInLine(";");
                    if (matched != null) {
                        status = 3;
                        if (nTaxa != coordinates.size()) {
                            System.out.println("Number of locations is not equal to ntax. Expected " + nTaxa + " locations, but found " + coordinates.size() + ".");
                            status = -1;
                            break;
                        }
                    } else {
                        if (!labels) {
                            scanner = new Scanner(line);
                            matched = scanner.findInLine("\\[(\\d+)\\]\\s+(\\S+)\\s+(\\S+)\\s*,");
                            if (matched != null) {
                                index = Integer.parseInt((scanner.match()).group(1));
                                double x = Double.parseDouble((scanner.match()).group(2));
                                double y = Double.parseDouble((scanner.match()).group(3));
                                coordinates.add(new Location(index, x, y));
                            }
                        } else {
                            matched = scanner.findInLine("\\[(\\d+)\\]\\s*(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*");
                            if (matched != null) {
                                index = Integer.parseInt((scanner.match()).group(1));
                                String name = (scanner.match()).group(2);
                                double x = Double.parseDouble((scanner.match()).group(3));
                                double y = Double.parseDouble((scanner.match()).group(4));
                                coordinates.add(new Location(name, index, x, y));
                            } else {
                                matched = scanner.findInLine("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*");
                                if (matched != null) {
                                    String name = (scanner.match()).group(1);
                                    double x = Double.parseDouble((scanner.match()).group(2));
                                    double y = Double.parseDouble((scanner.match()).group(3));
                                    coordinates.add(new Location(name, x, y));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (coordinates != null && coordinates.size() > 0) {
            if (nTaxa > 0 && coordinates.size() != nTaxa) {
                System.err.println("Error: wrong number of locations in LOCATIONS block. Expected " + nTaxa + ", found " + coordinates.size());
                locations = null;
            } else {
                nTaxa = coordinates.size();
                double[] x = new double[nTaxa];
                double[] y = new double[nTaxa];
                if (labels || taxaNames == null) {
                    taxaNames = new String[nTaxa];
                }
                for (int l = 0; l < nTaxa; l++) {
                    Location loc = coordinates.get(l);
                    index = (loc.index > 0) ? loc.index - 1 : l;
                    x[index] = loc.x;
                    y[index] = loc.y;
                    if (labels) {
                        taxaNames[index] = loc.name;
                    } else if (taxaNames[index] == null) {
                        taxaNames[l] = "taxon" + index;
                    }
                }
            }
        }

        return locations;
    }


    private static boolean findOptions(boolean option, String optiontext, Scanner scannerlc) {
        String matched = scannerlc.findInLine(optiontext + "\\s*=\\s*(\\w+);");
        if (matched != null) {
            if ((scannerlc.match()).group(1).contentEquals("left") || (scannerlc.match()).group(1).contentEquals("yes")) {
                option = true;
            }
        } else {
            matched = scannerlc.findInLine(optiontext + "[;\\s]");
            option = (matched != null) ? true : option;
        }

        return option;
    }

    private static class Location {
        String name;
        int index;
        double x;
        double y;

        public Location() {
        }

        public Location(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }


        public Location(int index, double x, double y) {
            this.index = index;
            this.x = x;
            this.y = y;
        }

        public Location(String name, int index, double x, double y) {
            this.name = name;
            this.index = index;
            this.x = x;
            this.y = y;
        }

    }

}