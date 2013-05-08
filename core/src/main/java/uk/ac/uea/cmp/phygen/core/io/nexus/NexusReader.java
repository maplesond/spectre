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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.phygen.core.io.PhygenReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Used to handle streaming of Nexus format files into memory, and convertion of
 * the data into a SplitSystem object.
 *
 * @author Dan
 */
public class NexusReader implements PhygenReader {

    private static Logger log = LoggerFactory.getLogger(NexusReader.class);

    /**
     * Reads the file specified by this reader and converts the data into a set
     * of taxa and the distances between taxa.
     *
     * @return The distance matrix, with associated taxa set.
     * @throws IOException    Thrown if there were any problems accessing the file.
     * @throws ParseException Thrown if there were any syntax issues when
     *                        parsing the file.
     */
    public DistanceMatrix read(File file) throws IOException {

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
            aLine = inLines.get(p);
            if (aLine.trim().toUpperCase().contains("BEGIN TAXA")) {
                taxaBloc = true;
            } else if (aLine.trim().toUpperCase().contains("BEGIN DISTANCES")) {
                distanceBloc = true;
            }
            p++;
        }

        //If no taxa bloc, read distanceMatrix & tax labels from distance bloc
        if (distanceBloc == true) {

            int s = 0;

            while (matrix == false) {
                aLine = inLines.get(p);
                if (aLine.trim().toUpperCase().startsWith("DIMENSIONS")) {
                    int beginIdx = aLine.toUpperCase().indexOf("NTAX=") + 5;
                    int endIdx = aLine.indexOf(";");
                    String dimString = aLine.substring(beginIdx, endIdx).trim();
                    int n = Integer.parseInt(dimString);
                    distanceMatrix = new DistanceMatrix(n);
                }

                if (aLine.trim().toUpperCase().startsWith("MATRIX")) {
                    matrix = true;
                }
                taxaNumberString = Integer.toString(taxaIndex);
                p++;
            }
            aLine = inLines.get(p);
            do {
                if (aLine.isEmpty() == false) {
                    aLine = aLine.trim();
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

                    distanceMatrix.setTaxa(s, identifier);
                    for (int i = distanceMatrix.size() - 1; i >= 0; i--) {
                        int lastIdx = aLine.length();
                        int firstIdx = aLine.lastIndexOf(" ");
                        String distance = aLine.substring(firstIdx + 1, lastIdx - 1);
                        try {
                            distanceMatrix.setDistance(s, i, Double.parseDouble(distance));
                            aLine = aLine.substring(0, firstIdx).trim();
                        } catch (Exception e) {
                            i = -1;
                            isTriangle = true;
                        }
                    }
                    s++;
                }
                p++;
                aLine = inLines.get(p);
            } while (aLine.trim().toUpperCase().contains(";") == false);

        }


        //If taxa bloc is included, read in taxa names from taxa bloc
        if (taxaBloc == true) {
            p--;
            do {
                log.debug("IF");
                if (aLine.trim().toUpperCase().startsWith("DIMENSIONS")) {
                    log.debug("IFIF");
                    int beginIdx = aLine.toUpperCase().indexOf("NTAX=") + 5;
                    int endIdx = aLine.indexOf(";");
                    String dimString = aLine.substring(beginIdx, endIdx).trim();

                    int n = Integer.parseInt(dimString);
                    log.debug(String.valueOf(n));
                    distanceMatrix = new DistanceMatrix(n);

                }
                p++;
                aLine = inLines.get(p);
            } while (aLine.toUpperCase().contains("TAXLABELS") == false);


            p++;
            aLine = inLines.get(p);

            do {
                if (aLine.isEmpty() == false && aLine.equals(";") == false) {
                    int beginIdx = aLine.indexOf("'") + 1;
                    int endIdx = aLine.lastIndexOf("'");

                    log.debug(beginIdx + " " + endIdx);
                    try {
                        log.debug("try");
                        String help = aLine.substring(beginIdx, endIdx);
                        log.debug(help);
                        help = help.replace(' ', '_');

                        distanceMatrix.setTaxa(taxaIndex - 1, help);
                        taxaIndex++;
                    } catch (StringIndexOutOfBoundsException e) {
                        throw new IOException("Error at offset " + offset + " in " + file.getPath());
                    }
                    taxaNumberString = Integer.toString(taxaIndex);
                }

                p++;
                aLine = inLines.get(p);
            } while (aLine.trim().toUpperCase().contains("END") == false);


            int s = 0;
            do {
                log.debug("DO");
                if (distanceBloc == true && matrix == true) {
                    log.debug("IF2");
                    if (aLine.isEmpty() == false || aLine.equals(";") == false) {
                        log.debug("DistIF" + distanceMatrix.size());
                        for (int i = distanceMatrix.size() - 1; i >= 0; i--) {
                            int lastIdx = aLine.length();
                            int firstIdx = aLine.lastIndexOf(" ");
                            String distance = aLine.substring(firstIdx + 1, lastIdx - 1);


                            log.debug("Dist: " + distance);
                            log.debug("S & i " + s + " " + i);
                            try {
                                distanceMatrix.setDistance(s, i, Double.parseDouble(distance));
                            } catch (Exception e) {
                                i = -1;
                                isTriangle = true;
                            } /*
                             * assuming triangular matrix
                             */
//                      System.out.println("Dist: "+ distanceMatrix[s][i]);
                            aLine = aLine.substring(0, firstIdx).trim();

                        }
                        s++;

                    }
                }

                if (aLine.trim().toUpperCase().contains("BEGIN DISTANCES")) {
                    distanceBloc = true;
                }
                if (aLine.trim().toUpperCase().contains("MATRIX")) {
                    matrix = true;
                }
                p++;
                aLine = inLines.get(p);
            } while (aLine.trim().toUpperCase().contains("END") == false);
        }

        /*
         * filling the missing entries in a triangular matrix
         */
//        if (isTriangle) {
//            for (int i = 0; i < distanceMatrix.length; i++) {
//                for (int j = 0; j < (i + 1); j++) {
//                    Double distValue = distanceMatrix[i][distanceMatrix.length
//                                                    - (i + 1) + j];
//
//                    distanceMatrix[i][j] = distValue;
//                }
//            }
//
//            for (int i = 0; i < distanceMatrix.length; i++) {
//                for (int j = i + 1; j < distanceMatrix.length; j++) {
//                    Double distValue = distanceMatrix[j][i];
//
//                    distanceMatrix[i][j] = distValue;
//                }
//            }
//        }

        return distanceMatrix;
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
            if (aLine.trim().toUpperCase().startsWith("DIMENSIONS") && taxaFound == false) {
                int startIdx = aLine.toUpperCase().indexOf("NTAX=") + 5;
                String dimString = aLine.substring(startIdx).trim();
                int endIdx = dimString.indexOf(";");
                dimString = dimString.substring(0, endIdx).trim();
                n = Integer.parseInt(dimString);
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

    public NexusData readNexusData(File inFile) throws IOException {

        List<String> taxonNames = new LinkedList<>();
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
                        taxonNames.add(new String(""));
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
                        taxonNames.set(n, aS);
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

                    for(String cyclePart : cycleParts) {

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

        return new NexusData(taxonNames, cycle, splits, weights);
    }
}
