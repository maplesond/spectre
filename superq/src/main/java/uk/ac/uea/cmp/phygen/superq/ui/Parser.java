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
package uk.ac.uea.cmp.phygen.superq.ui;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

//This class handles the input/output
//from to nexus files
public class Parser {
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
            System.out.println("Error while reading nexus file.");
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
    private static void writeheader(PrintWriter pw) {
        pw.println("#nexus");
        pw.println("");
    }

    //The taxa block of the nexus file
    private static void writetaxa(TreeSet taxanames, PrintWriter pw) {
        int ntaxa = taxanames.size();
        int i = 0;

        pw.println("BEGIN TAXA;");
        pw.print("DIMENSIONS NTAX=");
        pw.print(ntaxa);
        pw.println(";");
        pw.println("TAXLABELS");

        Iterator iter = taxanames.iterator();

        while (iter.hasNext()) {
            pw.print("[");
            pw.print(i + 1);
            pw.print("]  '");
            pw.print((String) iter.next());
            pw.println("'");
            i++;
        }
        pw.println(";");
        pw.println("END;");
        pw.println("");
    }

    private static void write_taxa(TreeSet taxanames, PrintWriter pw) {
        int i = 0;
        Iterator iter = taxanames.iterator();

        while (iter.hasNext()) {
            pw.println((String) iter.next());
            i++;
        }
    }

    //The tree block of the nexus file
    private static void writetrees(TreeSet treelist, PrintWriter pw) {
        int i = 0;

        pw.println("BEGIN TREES;");

        Iterator iter = treelist.iterator();

        while (iter.hasNext()) {
            pw.print("[");
            pw.print(i + 1);
            pw.print("] tree ");
            pw.print((String) iter.next());
            pw.println("");
            i++;
        }
        pw.println("END;");
        pw.println("");
    }

    //The tree block of the nexus file
    private static void writetrees2(TreeSet treelist, PrintWriter pw) {
        int i = 0;

        pw.println("BEGIN TREES;");

        Iterator iter = treelist.iterator();

        while (iter.hasNext()) {
            pw.print("[");
            pw.print(i + 1);
            pw.print("] tree ");
            pw.print("'tree_" + (i + 1) + "' = ");
            pw.print((String) iter.next());
            pw.println(";");
            i++;
        }
        pw.println("END;");
        pw.println("");
    }

    //write the Sciptfile to use Spiltstree
    public static void writeScript(String infile, String outfile) {
        //Read the trees from the input file
        //They are stored as strings in a Vector
        TreeSet treelist = new TreeSet();
        read_tree_list2(infile, treelist);

        //Extract the taxa names from the trees
        TreeSet taxanames = new TreeSet();
        //read_taxa_list(liste,taxanames);
        extract_taxa_names2(treelist, taxanames);

        PrintWriter pw = openprintwriter(outfile);

        writeheader(pw);
        writetaxa(taxanames, pw);
        writetrees2(treelist, pw);

        pw.println("BEGIN st_Assumptions;");
        pw.println("END; [st_Assumptions]");
        pw.println();
        pw.println("Begin SplitsTree;");
        pw.println("ASSUME  treestransform=ConsensusNetwork Threshold = 0.33 EdgeWeights = none;");
        pw.println("SAVE FILE=netz.nex REPLACE=yes APPEND=no DATA=all;");
        pw.println("QUIT;");
        pw.println("end;");
        closeprintwriter(pw);
    }

    //write the Sciptfile to use Spiltstree
    public static void writeScript2(String line, String outfile) {
        //Read the trees from the input file
        //They are stored as strings in a Vector
        TreeSet treelist = new TreeSet();
        read_tree_list(line, treelist);

        //Extract the taxa names from the trees
        TreeSet taxanames = new TreeSet();
        //read_taxa_list(liste,taxanames);
        extract_taxa_names(treelist, taxanames);

        PrintWriter pw = openprintwriter(outfile);

        writeheader(pw);
        writetaxa(taxanames, pw);
        writetrees2(treelist, pw);

        pw.println("BEGIN st_Assumptions;");
        pw.println("END; [st_Assumptions]");
        pw.println();
        pw.println("Begin SplitsTree;");
        pw.println("ASSUME treestransform=TreeSelector;");
        pw.println("SAVE FILE=help.nex REPLACE=yes APPEND=no DATA=all;");
        pw.println("QUIT;");
        pw.println("end;");
        closeprintwriter(pw);
    }

    //The trees and only the trees
    public static void writebaretrees(TreeSet treelist, PrintWriter pw) {
        Iterator iter = treelist.iterator();

        while (iter.hasNext()) {
            pw.print((String) iter.next());
            pw.println("");
        }
    }

    //This method extracts the taxanames recursively
    private static void extract_recursive(String newick, TreeSet taxanames) {
        newick = newick.trim();

        //First check if we have arrived at a leaf
        int index = newick.indexOf('(');
        int in = newick.indexOf(':');

        int start = 0;
        int level = 0;
        int length = newick.length();
        int i = 0;

        String subtree = null;

        if (index < 0) {
            taxanames.add(newick.substring(0, in));
        } else {
            start = 1;
            for (i = 1; i < (length - 1); i++) {
                if (newick.charAt(i) == '(') {
                    level++;
                } else if (newick.charAt(i) == ')') {
                    level--;
                } else if ((newick.charAt(i) == ',') && (level == 0)) {
                    subtree = newick.substring(start, i);
                    subtree = subtree.trim();
                    extract_recursive(subtree, taxanames);
                    start = i + 1;
                } else if (i == (length - 2)) {
                    subtree = newick.substring(start, i + 1);
                    subtree = subtree.trim();
                    extract_recursive(subtree, taxanames);
                }
            }
        }
    }

    //This method extracts the taxanames recursively
    private static void extract_recursive2(String newick, TreeSet taxanames) {
        newick = newick.trim();
        //System.out.println(newick);
        //First check if we have arrived at a leaf
        int index = newick.indexOf('(');

        int start = 0;
        int level = 0;
        int length = newick.length();
        int i = 0;

        String subtree = null;

        if (index < 0) {
            taxanames.add(newick);
        } else {
            start = 1;
            for (i = 1; i < (length - 1); i++) {
                if (newick.charAt(i) == '(') {
                    level++;
                } else if (newick.charAt(i) == ')') {
                    level--;
                }
                if ((newick.charAt(i) == ',') && (level == 0)) {
                    subtree = newick.substring(start, i);
                    subtree = subtree.trim();
                    extract_recursive2(subtree, taxanames);
                    start = i + 1;
                }
                if (i == (length - 2)) {
                    subtree = newick.substring(start, i + 1);
                    subtree = subtree.trim();
                    extract_recursive2(subtree, taxanames);
                }
            }
        }
    }

    //This method restricts the trees recursively
    private static String restrict_recursive(String newick, TreeSet taxanames) {
        String restriction = new String();

        //First check if we have arrived at a leaf
        int index = newick.indexOf('(');

        int start = 0;
        int level = 0;
        int length = newick.length();
        int i = 0;
        int nsubtrees = 0;

        String subtree = null;
        String restrictedsubtree;

        if (index < 0) {
            if (taxanames.contains(newick)) {
                restriction = restriction.concat(newick);
            }
            return restriction;
        } else {
            start = 1;
            for (i = 1; i < (length - 1); i++) {
                if (newick.charAt(i) == '(') {
                    level++;
                } else if (newick.charAt(i) == ')') {
                    level--;
                } else if ((newick.charAt(i) == ',') && (level == 0)) {
                    subtree = newick.substring(start, i);
                    subtree = subtree.trim();
                    restrictedsubtree = restrict_recursive(subtree, taxanames);
                    if (restrictedsubtree.length() > 0) {
                        nsubtrees++;
                        if (nsubtrees == 1) {
                            restriction = restriction.concat(restrictedsubtree);
                        } else {
                            restriction = restriction.concat(",");
                            restriction = restriction.concat(restrictedsubtree);
                        }
                    }
                    start = i + 1;
                } else if (i == (length - 2)) {
                    subtree = newick.substring(start, i + 1);
                    subtree = subtree.trim();
                    restrictedsubtree = restrict_recursive(subtree, taxanames);
                    if (restrictedsubtree.length() > 0) {
                        nsubtrees++;
                        if (nsubtrees == 1) {
                            restriction = restriction.concat(restrictedsubtree);
                        } else {
                            restriction = restriction.concat(",");
                            restriction = restriction.concat(restrictedsubtree);
                        }
                    }
                }
            }

            //if(restriction.length() > 0)
            //{
            //   System.out.println(restriction);
            //}

            if (nsubtrees == 1) {
                return restriction;
            } else if (nsubtrees == 0) {
                return (new String());
            } else {
                return (((new String()).concat("(")).concat(restriction)).concat(")");
            }
        }
    }

    //This method restricts trees with edge weights recursively
    private static String restrict_recursive_with_weights(String newick, Set<String> taxanames) {
        String restriction = new String();

        int index = newick.indexOf('(');

        int start = 0;
        int level = 0;
        int length = newick.length();
        int i = 0;
        int j = 0;
        int nsubtrees = 0;

        String subtree = null;
        String restrictedsubtree = null;
        String weight = null;
        String subweight = null;


        //check if we have arrived at a leaf
        if (index < 0) {
            //note: the weight has been cut off before going into the recursion
            if (taxanames.contains(newick)) {
                restriction = restriction.concat(newick);
            }
            return restriction;
        } else//not a leaf
        {
            //extract newick strings for subtrees one at a time
            start = 1;
            for (i = 1; i < (length - 1); i++) {
                if (newick.charAt(i) == '(') {
                    level++;
                } else if (newick.charAt(i) == ')') {
                    level--;
                } //are we at the : of a subtree?
                else if ((newick.charAt(i) == ':') && (level == 0)) {
                    //find the substring that represents the weight of the subtree
                    for (j = i + 1; j < (length - 1); j++) {
                        if (newick.charAt(j) == ',' || newick.charAt(j) == ')') {
                            break;
                        }
                    }

                    //get the subtree without the weight
                    subtree = newick.substring(start, i);

                    //get the weight
                    weight = newick.substring(i + 1, j);

                    //will carry on from here later
                    start = j + 1;

                    //restrict the subtree recursivly
                    restrictedsubtree = restrict_recursive_with_weights(subtree, taxanames);

                    //now check if the restriction yields a nonempty subtree
                    if (restrictedsubtree.length() > 0) {
                        //keep track of the number of non-empty subtrees
                        nsubtrees++;

                        //Check whether we can just glue the weight back or need to add it up
                        if (restrictedsubtree.indexOf(':') == -1 || restrictedsubtree.charAt(restrictedsubtree.length() - 1) == ')') {
                            restrictedsubtree = restrictedsubtree.concat(":");
                            restrictedsubtree = restrictedsubtree.concat(weight);
                        } else {
                            for (j = restrictedsubtree.length() - 1; j >= 0; j--) {
                                if (restrictedsubtree.charAt(j) == ':') {
                                    break;
                                }
                            }

                            subweight = restrictedsubtree.substring(j + 1, restrictedsubtree.length());
                            restrictedsubtree = restrictedsubtree.substring(0, j + 1);

                            weight = Double.toString(Double.parseDouble(weight) + Double.parseDouble(subweight));
                            restrictedsubtree = restrictedsubtree.concat(weight);
                        }

                        if (nsubtrees == 1) {
                            restriction = restriction.concat(restrictedsubtree);
                        } else {
                            restriction = restriction.concat(",");
                            restriction = restriction.concat(restrictedsubtree);
                        }
                    }
                }
            }

            if (nsubtrees == 1) {
                return restriction;
            } else if (nsubtrees == 0) {
                return (new String());
            } else {
                return (((new String()).concat("(")).concat(restriction)).concat(")");
            }
        }
    }

    //This method counts the number of leaves
    private static int count_leaves_recursive(String newick) {
        newick = newick.trim();

        //First check if we have arrived at a leaf
        int index = newick.indexOf('(');

        int start = 0;
        int level = 0;
        int length = newick.length();
        int i = 0;

        int nleaves = 0;

        String subtree = null;

        if (index < 0) {
            nleaves = 1;
        } else {
            start = 1;
            for (i = 1; i < (length - 1); i++) {
                if (newick.charAt(i) == '(') {
                    level++;
                } else if (newick.charAt(i) == ')') {
                    level--;
                } else if ((newick.charAt(i) == ',') && (level == 0)) {
                    subtree = newick.substring(start, i);
                    subtree = subtree.trim();
                    nleaves = nleaves + count_leaves_recursive(subtree);
                    start = i + 1;
                } else if (i == (length - 2)) {
                    subtree = newick.substring(start, i + 1);
                    subtree = subtree.trim();
                    nleaves = nleaves + count_leaves_recursive(subtree);
                }
            }
        }

        return nleaves;
    }

    //Need to delete weight at root
    private static String clean_weight_at_root(String newick) {
        int i = newick.length();

        if (newick.charAt(newick.length() - 1) != ')') {
            for (i = newick.length() - 1; i >= 0; i--) {
                if (newick.charAt(i) == ':') {
                    break;
                }
            }
        }

        return newick.substring(0, i);
    }

//*************************************************************************
//public methods of this class
//*************************************************************************
    //This method extracts the trees from the input file
    public static void read_tree_list(String line, TreeSet treelist) {
        line = line.trim();
        if (line.charAt(line.length() - 1) == ';') {
            line = line.substring(0, line.length() - 1);
        }
        treelist.add(line);
    }

    //This method extracts the trees from the input file
    public static void read_tree_list2(String infile, TreeSet treelist) {
        LineNumberReader lnr = openlinereader(infile);

        String line = null;

        while (true) {
            try {
                line = lnr.readLine();

            } catch (IOException exception) {
                System.out.println("Error occured while reading nexus file.");
                break;
            }

            if (line == null) {
                break;
            } else {
                line = line.trim();
                if (line.charAt(line.length() - 1) == ';') {
                    line = line.substring(0, line.length() - 1);
                }
                treelist.add(line);
            }
        }

        closelinereader(lnr);
    }

    //This method reads the taxa from the taxa block
    public static void readtaxa(String infile, TreeSet taxanames) {
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

        LineNumberReader lnr = openlinereader(infile);

        while (status < 4) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error occured while reading taxa block.");
                status = 4;
            }

            if ((status < 3) && (line == null)) {
                System.out.println("Error occured while parsing taxa block;");
                status = 4;
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
                        }
                    } else {
                        scanner = new Scanner(line);
                        matched = scanner.findInLine("\\[(\\d+)\\](\\s+)('*)([^']+)");
                        if (matched != null) {
                            index = Integer.parseInt((scanner.match()).group(1));
                            taxanames.add((scanner.match()).group(4));
                            r++;
                        }
                    }
                }
            }
        }
        closelinereader(lnr);
    }

    //This method extracts the trees from the input file
    public static void read_taxa_list(String infile, TreeSet taxanames) {
        LineNumberReader lnr = openlinereader(infile);

        String line = null;
        String matched = null;
        Scanner scanner = null;

        while (true) {
            try {
                line = lnr.readLine();
            } catch (IOException exception) {
                System.out.println("Error occured while reading nexus file.");
                break;
            }

            if (line == null) {
                break;
            } else {
                line = line.trim();
                taxanames.add(line);
            }
        }

        closelinereader(lnr);
    }

    //this method extracts the names of taxa from the trees
    public static void extract_taxa_names(TreeSet treelist, TreeSet taxanames) {
        Iterator iter = treelist.iterator();
        String newick = null;
        Scanner scanner = null;

        while (iter.hasNext()) {
            scanner = new Scanner((String) iter.next());
            scanner.findInLine("(\\S+)");
            newick = (scanner.match()).group(1);
            extract_recursive(newick, taxanames);
        }
    }

    //this method extracts the names of taxa from the trees
    public static void extract_taxa_names2(TreeSet treelist, TreeSet taxanames) {
        Iterator iter = treelist.iterator();

        String newick = null;
        Scanner scanner = null;

        while (iter.hasNext()) {
            newick = (String) iter.next();
            extract_recursive2(newick, taxanames);
        }
    }

    //This method restricts a tree with weights
    public static void restrict_trees_with_weights(TreeSet treelist, Set<String> taxanames, TreeSet restrictedtreelist) {
        Iterator iter = treelist.iterator();
        String newick = null;
        String restriction = null;

        while (iter.hasNext()) {
            newick = (String) iter.next();
            restriction = restrict_recursive_with_weights(newick, taxanames);
            if (restriction.length() > 0) {
                restriction = clean_weight_at_root(restriction);
                restriction = restriction.concat(";");
                restrictedtreelist.add(restriction);
            }
        }
    }

    //This method restricts the trees to the taxa
    //in the list
    public static void restrict_trees(TreeSet treelist, TreeSet taxanames, TreeSet restrictedtreelist) {
        Iterator iter = treelist.iterator();
        String newick = null;
        String restriction = null;
        String name = null;
        Scanner scanner = null;

        while (iter.hasNext()) {
            scanner = new Scanner((String) iter.next());
            scanner.findInLine("(\\S+)(\\s*)=(\\s*)(\\S+);");
            name = (scanner.match()).group(1);
            name = name.concat(" = ");
            newick = (scanner.match()).group(4);
            newick = newick.trim();
            restriction = restrict_recursive(newick, taxanames);
            if (restriction.length() > 0) {
                name = name.concat(restriction);
                name = name.concat(";");
                restrictedtreelist.add(name);
            }
        }
    }

    //delete all trees with less than 4 leaves
    public static void delete_small_trees(TreeSet treelist, TreeSet thinnedtreelist) {
        Iterator iter = treelist.iterator();
        String newick = null;
        String restriction = null;
        String name = null;
        Scanner scanner = null;

        int nleaves = 0;

        while (iter.hasNext()) {
            scanner = new Scanner((String) iter.next());
            scanner.findInLine("(\\S+)(\\s*)=(\\s*)(\\S+);");
            newick = (scanner.match()).group(4);
            newick = newick.trim();
            nleaves = count_leaves_recursive(newick);
            if (nleaves > 3) {
                newick = newick.concat(";");
                thinnedtreelist.add(newick);
            }
        }
    }

    //This method writes the nexus file
    public static void write_nexus_file(String outfile, TreeSet treelist, TreeSet taxanames) {
        PrintWriter pw = openprintwriter(outfile);

        writeheader(pw);
        writetaxa(taxanames, pw);
        writetrees(treelist, pw);

        closeprintwriter(pw);
    }

    //This method writes only the taxa
    public static void write_nexus_taxa(String outfile, TreeSet treelist, TreeSet taxanames) {
        PrintWriter pw = openprintwriter(outfile);
        write_taxa(taxanames, pw);
        closeprintwriter(pw);
    }

    //This method writes only the trees
    public static void write_bare_trees(String outfile, TreeSet treelist) {
        PrintWriter pw = openprintwriter(outfile);

        writebaretrees(treelist, pw);

        closeprintwriter(pw);
    }

    //This method writes only the taxa
    public static void write_bare_taxa(String outfile, TreeSet treelist) {
        PrintWriter pw = openprintwriter(outfile);

        write_taxa(treelist, pw);

        closeprintwriter(pw);
    }

    public static void print_set(TreeSet set) {
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            System.out.println((String) iter.next());
        }
    }
}