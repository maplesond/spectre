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
package uk.ac.uea.cmp.phygen.qnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * The QNet main class
 *
 * Presently runnable holder for Stefan Gr�newalds circular ordering-generating
 * algorithm
 *
 */
public class QNet {

    private static Logger log = LoggerFactory.getLogger(QNet.class);

    /**
     *
     * Runnable method...
     *
     * Let�s see... the program should:
     *
     * - load the file according to specs
     *
     * - set up the proper set of lists
     *
     * - perform the joining loop
     *
     * - deliver the final single list
     *
     */
    public static QNet theQNet = new QNet();

    public static void main(String[] args) {
        theQNet = new QNet();

        /**
         *
         * Just a primitive version, command-line, no options. However, that�s
         * just the way of testing. load and run methods are the core.
         *
         * Usage: <lin/log> <infile> <outfile> <optional: tolerance>
         *
         *
         */
        //QNet theQNet = new QNet ();

        try {

            if (args.length > 2) {

                /**
                 *
                 * Now, calculate split weights
                 *
                 */
                boolean log = false;

                double tolerance = -1.0;

                if (args[0].equals("log")) {

                    log = true;

                }

                try {

                    if (args.length > 3) {

                        tolerance = Double.parseDouble(args[3]);

                    }

                } catch (NumberFormatException e) {
                }

                if (args[1].endsWith(".nex")) {

                    QNetLoader.loadNexus(theQNet, args[1], log);

                } else {

                    QNetLoader.load(theQNet, args[1], log);

                }

                //Iterator it = theQNet.getTaxonNames().iterator();
                //while(it.hasNext())
                //{
                //    System.out.println("Inside the QNet before joining: " + it.next());
                //}

                NewCyclicOrderer.order(theQNet);

                //System.out.println ("QNet: Joining complete.");
                if(args.length > 4) {
                    WeightsComputeNNLSInformative.computeWeights(theQNet, args [1] + ".info", theQNet.getTheLists (), args[2], tolerance,args[4]);
                }
                else {
                    WeightsComputeNNLSInformative.computeWeights(theQNet, args [1] + ".info", theQNet.getTheLists (), args[2], tolerance,"gurobi");
                }
                //WeightsWriterNNLSInformative.writeWeights (theQNet, args [1] + ".info", theQNet.getTheLists (), args[2], tolerance);
                //WeightsWriterNNLS.writeWeights (theQNet, theQNet.getTheLists (), args[2], tolerance);

                //System.out.println ("QNet: Splits written to file.");

            }
            else {
                log.error("QNet: Please specify choice of (lin/log) treatments for weights, input and output filenames!");
            }
        }
        catch(IOException e) {
            log.error(e.getMessage(), e);
            System.exit(2);
        }
        catch(Exception e) {
            log.error(e.getMessage(), e);
            System.exit(3);
        }
    }

    public int getN() {

        return N;
    }

    public void setN(int newN) {

        N = newN;
    }

    public QuartetWeights getWeights() {

        return theQuartetWeights;
    }

    public void setWeights(QuartetWeights newWeights) {

        theQuartetWeights = newWeights;
    }

    public ArrayList getTaxonNames() {

        return taxonNames;
    }

    public void setTaxonNames(ArrayList newTaxonNames) {

        taxonNames = newTaxonNames;
    }

    public boolean getUseMax() {

        return useMax;
    }

    public void setUseMax(boolean newUseMax) {

        useMax = newUseMax;
    }

    public ArrayList getTheLists() {

        return theLists;
    }

    public void setTheLists(ArrayList newLists) {

        theLists = newLists;
    }

    /**
     *
     * Structure for quartet weights.
     *
     * In this object, quartet weights are held by quartet + tree.
     *
     * Say... ArrayList of ArrayLists of ArrayLists of triplets?
     *
     * Always access by small->large... with subtraction of the previous and
     * one... so 3, 5, 1, 8 is 1, 3, 5, 8 is 0 1 1 2 and then choose which tree
     * according to order u, v, x, y
     *
     */
    QuartetWeights theQuartetWeights = new QuartetWeights();
    /**
     *
     * Taxon names
     *
     * ArrayList of Strings
     *
     */
    ArrayList taxonNames = new ArrayList();
    /**
     *
     * Listing holder...
     *
     * Just an ArrayList of TaxonLists
     *
     */
    ArrayList theLists = new ArrayList();
    /**
     *
     * Number of taxa...
     *
     */
    int N;
    /**
     *
     * Choice of min/max usage
     *
     */
    boolean useMax;
}