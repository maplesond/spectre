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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;


import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

import java.util.Random;

/**
 * This class is used to store the permutation sequence representing a flat split system
 **/
public class PermutationSequenceDraw {

    //*************************************************************
    //Variables used in this class
    //*************************************************************

    //number of taxa
    int ntaxa = 0;
    //number of swaps
    //note that each swap corresponds to a split
    int nswaps = 0;
    //flag for weights on the swaps/splits
    boolean hasWeights = false;
    //flag for flags on active splits
    boolean hasActiveFlags = false;
    //number of active swaps/splits
    //Only the active splits are included in the network.
    int nActive = 0;
    //number of classes of taxa
    //If not all splits are active, there might exist
    //taxa that are not separated by an active split.
    //Then these taxa belong to the same class and
    //label the same vertex in the split network.
    int nclasses = 0;
    //initial permutation of the taxa.
    //the set of taxa is always {0,1,...,ntaxa-1}
    int[] initSequ = null;
    //names of the taxa
    String[] taxaname = null;
    //sequence of swaps. A swap is specified by the index
    //of the element in the array that is swapped with the 
    //successor in the array. Each swap corresponds to
    //a split, namely the elements up to the element
    //that is indexed in the swap vs the rest
    int[] swaps = null;
    //We record which swaps correspond to an active split.
    //Used when filtering out splits with small length etc.
    boolean[] active = null;
    //This array keeps track of those classes of taxa that
    //are not distinguished by the active splits.
    //One representative is randomly selected and all
    //other taxa in the class point to this taxon.
    int[] representedby = null;
    //This array keeps track of the representative of
    //the classes of taxa. Those are active. All other
    //taxa are inactive.
    boolean[] activeTaxa = null;
    //weights associated to the swaps, that is, splits
    double[] weights = null;

    //maps the indices of the active splits to new indices
    //so that they are numbered without gaps. This is done
    //to facilitate the visualization of the split network
    //by SplitsTree. There edges are labeled by the index
    //of the corresponding split and the range of possible
    //indices is limited, it seems.
    int[] compressed = null;
    //weights of all trivial splits
    double[] trivial = null;



    //**********************************************************
    //Constructors for this class
    //**********************************************************

    /**
     * Constructor of this class from a given initial sequence and a sequence of swaps. Exists mainly for testing purposes
     * @param in_init_sequ
     * @param in_swaps
     */
    public PermutationSequenceDraw(int[] in_init_sequ, int[] in_swaps) {
        int i = 0;

        ntaxa = initSequ.length;
        nclasses = ntaxa;
        nswaps = swaps.length;
        nActive = nswaps;

        hasWeights = true;
        hasActiveFlags = true;

        initSequ = new int[ntaxa];
        representedby = new int[ntaxa];
        activeTaxa = new boolean[ntaxa];
        taxaname = new String[ntaxa];

        swaps = new int[nswaps];
        active = new boolean[nswaps];
        compressed = new int[nswaps];
        weights = new double[nswaps];

        for (i = 0; i < ntaxa; i++) {
            initSequ[i] = in_init_sequ[i];
            representedby[i] = i;
            activeTaxa[i] = true;
            taxaname[i] = "taxon" + i;
        }

        for (i = 0; i < nswaps; i++) {
            swaps[i] = in_swaps[i];
            compressed[i] = i;
            active[i] = true;
            weights[i] = 1.0;
        }
    }


    /**
     * Constructor of this class from a given initial sequence, a sequence of swaps, weights and array of active flags.
     * For the transition from PS in FNet.
     * @param in_init_sequ
     * @param in_swaps
     * @param in_weights
     * @param in_active
     * @param trivialWeights
     */
    public PermutationSequenceDraw(int[] in_init_sequ, int[] in_swaps, double[] in_weights, boolean[] in_active, double[] trivialWeights) {
        int i = 0;

        ntaxa = in_init_sequ.length;
        nclasses = ntaxa;
        nswaps = in_swaps.length;
        nActive = nswaps;

        hasWeights = true;
        hasActiveFlags = true;

        initSequ = new int[ntaxa];
        representedby = new int[ntaxa];
        activeTaxa = new boolean[ntaxa];
        taxaname = new String[ntaxa];
        trivial = new double[ntaxa];

        swaps = new int[nswaps];
        active = new boolean[nswaps];
        compressed = new int[nswaps];
        weights = new double[nswaps];

        for (i = 0; i < ntaxa; i++) {
            initSequ[i] = in_init_sequ[i];
            representedby[i] = i;
            activeTaxa[i] = true;
            taxaname[i] = "taxon" + i;
            trivial[i] = trivialWeights[i];
        }

        for (i = 0; i < nswaps; i++) {
            swaps[i] = in_swaps[i];
            compressed[i] = i;
            active[i] = in_active[i];
            weights[i] = in_weights[i];
            if (weights[i] == 0) {
                active[i] = false;
            }
        }

        nActive = CollectionUtils.size(active);
    }


    /**
     * Constructor for this class from a SplitSystem-object. This is just used as a quick and dirty way to feed circular
     * split systems into the drawing algorithm and then display them with the viewer.
     * @param ss
     */
    public PermutationSequenceDraw(SplitSystem ss){
        int h = 0;
        int i = 0;
        int j = 0;
        int k = 0;
        int a = 0;

        //first get the number of taxa and number of splits
        //in a full circular split system on that number of taxa
        ntaxa = ss.getNbTaxa();
        nclasses = ntaxa;
        nswaps = ntaxa * (ntaxa - 1) / 2;;
        nActive = nswaps;

        //assume for now that all splits have weights and
        //and all splits are active, that is, the split system is full
        hasWeights = true;
        hasActiveFlags = true;

        //set up the arrays used to store all the information about the split system
        initSequ = new int[ntaxa];
        representedby = new int[ntaxa];
        activeTaxa = new boolean[ntaxa];
        taxaname = new String[ntaxa];
        trivial = new double[ntaxa];

        swaps = new int[nswaps];
        active = new boolean[nswaps];
        compressed = new int[nswaps];
        weights = new double[nswaps];

        //store information about the taxa in arrays
        for (i = 0; i < ntaxa; i++) {
            initSequ[i] = i;
            representedby[i] = i;
            activeTaxa[i] = true;
            taxaname[i] = ss.getOrderedTaxa().get(i).getName();
            trivial[i] = 0.0; //no extra length added to pendant edge;
        }

        //array used to store the current permutation from which
        //we extract the current split
        int[] cursequ = new int[ntaxa];
        for (i = 0; i < ntaxa; i++) {
            cursequ[i] = initSequ[i];
        }

        //the generic structure of a circular split system
        for (i = 0; i < ntaxa; i++) {
            for (j = 0; j < ntaxa - i - 1; j++) {
                swaps[k] = j;
                compressed[k] = k;
                weights[k] = 0;
                active[k] = false;

                //update current permutation
                h = cursequ[j];
                cursequ[j] = cursequ[j + 1];
                cursequ[j + 1] = h;

                //get the split represented by this swap
                int[] sideA = new int[j+1]; //get one side of the split as an int-array, the elements must be the ids according to the identifier list
                for(a=0;a<=j;a++) {sideA[a]=cursequ[a];}
                SplitBlock ssb = new SpectreSplitBlock(sideA);
                ssb.sort();

                //next check whether the input split system contains a split with one SplitBlock equal to the one we just created
                for (Split s : ss) {
                    s.getASide().sort();
                    if(s.getASide().equals(ssb) || s.getBSide().equals(ssb)){
                        weights[k] = s.getWeight();
                        if(weights[k] > 0) {active[k] = true;}
                    }
                }
                k++;
            }
        }

        //update number of active splits
        nActive = CollectionUtils.size(active);
    }

    //Constructor of this class from a permutation
    //sequence stored in a nexus file
    //The threshold can be used to filter
    //the splits.
    /*public PermutationSequenceDraw(String filename, double thold) {
        int i = 0;

        LineNumberReader lnr = NexusIO.openlinereader(filename);
        taxaname = NexusIO.readtaxa(lnr);

        if (taxaname == null) {
            System.out.println("Reading the taxa block failed");
        } else {
            ntaxa = taxaname.length;
            nclasses = ntaxa;
            nswaps = ntaxa * (ntaxa - 1) / 2;
            nActive = nswaps;

            initSequ = new int[ntaxa];
            representedby = new int[ntaxa];
            activeTaxa = new boolean[ntaxa];
            trivial = new double[ntaxa];

            swaps = new int[nswaps];
            active = new boolean[nswaps];
            compressed = new int[nswaps];
            weights = new double[nswaps];

            for (i = 0; i < ntaxa; i++) {
                representedby[i] = i;
                activeTaxa[i] = true;
            }

            for (i = 0; i < nswaps; i++) {
                compressed[i] = i;
                active[i] = true;
                weights[i] = 1.0;
            }

            if (NexusIO.readflatsplits(this, lnr) < 0) {
                System.out.println("Reading the flatsplits block failed");
            }

            if (thold >= 0.0) {
                setActive(thold);
            }
        }
        NexusIO.closelinereader(lnr);
    }

    //Constructor of this class from a set of points
    //stored in a nexus file. The split system contains
    //the affine split system induced by these points.
    public PermutationSequenceDraw(String filename) {
        int i = 0;
        int j = 0;
        int k = 0;
        int h = 0;
        int l = 0;
        double dx = 0.0;
        double dy = 0.0;
        double r = 0.0;

        LineNumberReader lnr = NexusIO.openlinereader(filename);
        taxaname = NexusIO.readtaxa(lnr);

        if (taxaname == null) {
            System.out.println("Reading the taxa block failed");
        } else {
            ntaxa = taxaname.length;
            nclasses = ntaxa;
            nswaps = ntaxa * (ntaxa - 1) / 2;
            nActive = nswaps;

            hasWeights = true;
            hasActiveFlags = true;

            initSequ = new int[ntaxa];
            representedby = new int[ntaxa];
            activeTaxa = new boolean[ntaxa];

            swaps = new int[nswaps];
            active = new boolean[nswaps];
            compressed = new int[nswaps];
            weights = new double[nswaps];

            for (i = 0; i < ntaxa; i++) {
                representedby[i] = i;
                activeTaxa[i] = true;
                initSequ[i] = i;
            }

            for (i = 0; i < nswaps; i++) {
                compressed[i] = i;
                active[i] = true;
                weights[i] = 1.0;
            }

            double[] xcoord = new double[ntaxa];
            double[] ycoord = new double[ntaxa];

            if (NexusIO.readlocations(xcoord, ycoord, lnr) < 0) {
                System.out.println("Reading the locations block failed");
            } else {
                sortinitsequence(xcoord, ycoord);

                int[] cursequ = new int[ntaxa];

                for (i = 0; i < ntaxa; i++) {
                    cursequ[i] = initSequ[i];
                }

                boolean[][] swapped = new boolean[ntaxa][ntaxa];

                for (i = 0; i < ntaxa; i++) {
                    for (j = 0; j < ntaxa; j++) {
                        swapped[i][j] = false;
                    }
                }

                for (k = 0; k < nswaps; k++) {
                    r = Double.POSITIVE_INFINITY;
                    h = -1;

                    for (i = 0; i < (ntaxa - 1); i++) {
                        if (swapped[cursequ[i]][cursequ[i + 1]] == false) {
                            dx = xcoord[cursequ[i]] - xcoord[cursequ[i + 1]];
                            dy = ycoord[cursequ[i]] - ycoord[cursequ[i + 1]];
                            if (dx == 0.0) {
                                if (r == Double.POSITIVE_INFINITY) {
                                    h = i;
                                }
                            } else {
                                if (r >= (dy / dx)) {
                                    r = dy / dx;
                                    h = i;
                                }
                            }
                        }
                    }

                    swaps[k] = h;
                    swapped[cursequ[h]][cursequ[h + 1]] = true;
                    swapped[cursequ[h + 1]][cursequ[h]] = true;
                    l = cursequ[h];
                    cursequ[h] = cursequ[h + 1];
                    cursequ[h + 1] = l;
                }
            }
        }
        NexusIO.closelinereader(lnr);
    }*/

    /**
     * Constructor of a template for a circular split system from a list of taxa
     * @param tname
     */
    public PermutationSequenceDraw(String[] tname) {
        ntaxa = tname.length;
        nswaps = ntaxa * (ntaxa - 1) / 2;
        nActive = nswaps;
        nclasses = ntaxa;
        hasWeights = true;
        hasActiveFlags = true;

        initSequ = new int[ntaxa];
        taxaname = new String[ntaxa];
        swaps = new int[nswaps];
        active = new boolean[nswaps];
        compressed = new int[nswaps];
        representedby = new int[ntaxa];
        activeTaxa = new boolean[ntaxa];
        weights = new double[nswaps];

        int i = 0;
        int j = 0;
        int k = 0;
        int h = 0;
        int r = 0;
        int a = 0;

        for (i = 0; i < ntaxa; i++) {
            taxaname[i] = tname[i];
        }

        //circular split system
        for (i = 0; i < ntaxa; i++) {
            initSequ[i] = i;
        }

        for (i = 0; i < ntaxa; i++) {
            for (j = 0; j < ntaxa - i - 1; j++) {
                swaps[k] = j;
                k++;
            }
        }
    }

    public static enum SplitSystemType {
        FLAT,
        CIRCULAR
    }

    /**
     * Constructor that generates a random permutation sequence. The first parameter is the number of taxa. The second
     * parameter is used to indicate the type of split system: general flat or circular
     * @param n
     * @param t
     */
    public PermutationSequenceDraw(int n, SplitSystemType t) {
        ntaxa = n;
        nswaps = ntaxa * (ntaxa - 1) / 2;
        nActive = nswaps;
        nclasses = ntaxa;
        hasWeights = true;
        hasActiveFlags = true;

        initSequ = new int[ntaxa];
        taxaname = new String[ntaxa];
        swaps = new int[nswaps];
        active = new boolean[nswaps];
        compressed = new int[nswaps];
        representedby = new int[ntaxa];
        activeTaxa = new boolean[ntaxa];

        weights = new double[nswaps];

        Random rgen = new Random();

        int i = 0;
        int j = 0;
        int k = 0;
        int h = 0;
        int r = 0;
        int a = 0;

        for (i = 0; i < nswaps; i++) {
            active[i] = true;
            weights[i] = rgen.nextDouble();
            //weights[i] = 1.0;
            compressed[i] = i;
        }

        for (i = 0; i < ntaxa; i++) {
            taxaname[i] = "Taxon" + i;
            representedby[i] = i;
            activeTaxa[i] = true;
        }

        //circular split system
        if (t == SplitSystemType.CIRCULAR) {
            for (i = 0; i < ntaxa; i++) {
                initSequ[i] = i;
            }

            //generate a random initial permutation
            for (i = 0; i < ntaxa; i++) {
                r = rgen.nextInt(ntaxa - i);
                h = initSequ[i];
                initSequ[i] = initSequ[i + r];
                initSequ[i + r] = h;
            }

            for (i = 0; i < ntaxa; i++) {
                for (j = 0; j < ntaxa - i - 1; j++) {
                    swaps[k] = j;
                    k++;
                }
            }
        }
        //general flat split system
        else if (t == SplitSystemType.FLAT) {
            for (i = 0; i < ntaxa; i++) {
                initSequ[i] = i;
            }

            //generate a random initial permutation
            for (i = 0; i < ntaxa; i++) {
                r = rgen.nextInt(ntaxa - i);
                h = initSequ[i];
                initSequ[i] = initSequ[i + r];
                initSequ[i + r] = h;
            }

            //generate random sequence of swaps
            boolean[][] swapped = new boolean[ntaxa][ntaxa];
            for (i = 0; i < ntaxa; i++) {
                for (j = 0; j < ntaxa; j++) {
                    swapped[i][j] = false;
                }
            }

            int[] cursequ = new int[ntaxa];
            for (i = 0; i < ntaxa; i++) {
                cursequ[i] = initSequ[i];
            }

            for (k = 0; k < nswaps; k++) {
                a = 0;
                for (i = 0; i < (ntaxa - 1); i++) {
                    if (swapped[cursequ[i]][cursequ[i + 1]] == false) {
                        a++;
                    }
                }

                r = rgen.nextInt(a) + 1;

                a = 0;
                for (i = 0; i < (ntaxa - 1); i++) {
                    if (swapped[cursequ[i]][cursequ[i + 1]] == false) {
                        a++;
                    }
                    if (a == r) {
                        swaps[k] = i;
                        swapped[cursequ[i]][cursequ[i + 1]] = true;
                        swapped[cursequ[i + 1]][cursequ[i]] = true;
                        h = cursequ[i];
                        cursequ[i] = cursequ[i + 1];
                        cursequ[i + 1] = h;
                        break;
                    }
                }
            }
        } else {
            System.out.println("Type of split system not supported by construtor of Perm_sequence");
        }
    }

    //***********************************
    // Getters
    //***********************************


    public int getNtaxa() {
        return ntaxa;
    }

    public int getNswaps() {
        return nswaps;
    }

    public boolean isHasWeights() {
        return hasWeights;
    }

    public boolean isHasActiveFlags() {
        return hasActiveFlags;
    }

    public int getnActive() {
        return nActive;
    }

    public int getNclasses() {
        return nclasses;
    }

    public void setNClasses(final int value) {
        nclasses = value;
    }

    public void decrementNClasses() {
        nclasses--;
    }

    public int[] getInitSequ() {
        return initSequ;
    }

    public String[] getTaxaname() {
        return taxaname;
    }

    public int[] getSwaps() {
        return swaps;
    }

    public boolean[] getActive() {
        return active;
    }

    public int[] getRepresentedby() {
        return representedby;
    }

    public void setRepresentedByAt(final int index, final int value) {
        representedby[index] = value;
    }

    public boolean[] getActiveTaxa() {
        return activeTaxa;
    }

    public boolean getActiveTaxaAt(final int index) {
        return activeTaxa[index];
    }

    public void setActiveTaxaAt(final int index, boolean value) {
        activeTaxa[index] = value;
    }

    public int[] getCompressed() {
        return compressed;
    }

    public double[] getTrivial() {
        return trivial;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeightAt(final int index, double value) {
        weights[index] = value;
    }

    //*********************************************************************
    //Other public methods of this class
    //*********************************************************************

    /**
     * Returns a string representation of this sequence
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Number of taxa: ").append(ntaxa)
          .append("List of taxa:");

        for (int i = 0; i < ntaxa; i++) {
            sb.append(taxaname[i]);
        }

        sb.append("Initial permutation: ");
        for (int i = 0; i < ntaxa; i++) {
            sb.append(initSequ[i] + " ");
        }

        sb.append("\nSequence of swaps: ");
        for (int i = 0; i < nswaps; i++) {
            sb.append(swaps[i] + " ");
        }

        sb.append("\n");

        if (hasWeights) {
            sb.append("Weights: ");
            for (int i = 0; i < nswaps; i++) {
                sb.append(weights[i] + " ");
            }
            sb.append("\n");
        }

        sb.append("Active swaps: ");
        for (int i = 0; i < nswaps; i++) {
            if (active[i]) {
                sb.append("1 ");
            } else {
                sb.append("0 ");
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    /**
     * This method filters the splits by labeling those with a length below the given threshold inactive.
     * @param thold
     */
    public void setActive(double thold) {
        if (active == null) {
            active = new boolean[nswaps];
        }

        int i = 0;
        nActive = 0;

        if (weights == null) {
            for (i = 0; i < nswaps; i++) {
                active[i] = true;
            }
            nActive = nswaps;
        } else {
            for (i = 0; i < nswaps; i++) {
                if (weights[i] < thold) {
                    active[i] = false;
                } else {
                    active[i] = true;
                    nActive++;
                }
            }
        }
    }

    //This method randomly sets some splits inactive.
    //For testing purposes. The parameter is a real
    //number between 0 and 1. This number equals
    //the probability with which a split is set
    //to be active.
    public void knock_out(double thold) {
        int i = 0;
        Random rgen = new Random();
        nActive = 0;
        if (active == null) {
            active = new boolean[nswaps];
        }

        for (i = 0; i < nswaps; i++) {
            if (rgen.nextDouble() < thold) {
                active[i] = true;
                nActive++;
            } else {
                active[i] = false;
            }
        }
    }

    //This method is used to reset the various flags used
    //when computing a split graph for the active splits.
    public void reset() {
        int i = 0;
        for (i = 0; i < ntaxa; i++) {
            representedby[i] = i;
            activeTaxa[i] = true;
        }
        for (i = 0; i < nswaps; i++) {
            active[i] = true;
        }
        nclasses = ntaxa;
        nActive = nswaps;
    }

    /**
     * Checks whether the permutation sequence is valid, that is, whether all arrays have been initialized properly and
     * whether every pair of distinct taxe swaps precisely once.  The latter test, in particular, was included to detect
     * potential numerical problems when constructing a permutation sequence from a set of points in the plane whose
     * coordinates are given by floating point numbers.
     * @return
     */
    public boolean checkConsistency() {
        int i = 0;
        int j = 0;
        int k = 0;
        int h = 0;

        if (initSequ == null) {
            throw new IllegalStateException("Array init_sequ is null");
        }

        if (taxaname == null) {
            throw new IllegalStateException("Array taxaname is null");
        }

        if (swaps == null) {
            throw new IllegalStateException("Array swaps is null");
        }

        if (active == null) {
            throw new IllegalStateException("Array active is null");
        }

        if (representedby == null) {
            throw new IllegalStateException("Array representedby is null");
        }

        if (activeTaxa == null) {
            throw new IllegalStateException("Array activetaxa is null");
        }

        if (weights == null) {
            throw new IllegalStateException("Array weights is null");
        }

        if (initSequ.length != ntaxa) {
            throw new IllegalStateException("Length of array init_sequ not okay");
        }

        if (taxaname.length != ntaxa) {
            throw new IllegalStateException("Length of array taxaname not okay");
        }

        if (swaps.length != nswaps) {
            throw new IllegalStateException("Length of array swaps not okay");
        }

        if (active.length != nswaps) {
            throw new IllegalStateException("Length of array active not okay");
        }

        if (representedby.length != ntaxa) {
            throw new IllegalStateException("Length of array representedby not okay");
        }

        if (activeTaxa.length != ntaxa) {
            throw new IllegalStateException("Length of array activetaxa not okay");
        }

        if (weights.length != nswaps) {
            throw new IllegalStateException("Length of array weights not okay");
        }

        for (i = 0; i < (ntaxa - 1); i++) {
            for (j = i + 1; j < ntaxa; j++) {
                if (initSequ[i] == initSequ[j]) {
                    throw new IllegalStateException("Entries of init_sequ are not pairwise distinct");
                }
            }
        }

        for (i = 0; i < ntaxa; i++) {
            if ((initSequ[i] < 0) || (initSequ[i] >= ntaxa)) {
                throw new IllegalStateException("Entries of init_sequ are not in range 0,1,...,(ntaxa-1)");
            }
        }

        for (i = 0; i < nswaps; i++) {
            if (weights[i] < 0.0) {
                throw new IllegalStateException("Some split weights are negative");
            }
        }

        for (i = 0; i < ntaxa; i++) {
            if (taxaname[i] == null) {
                throw new IllegalStateException("Some names of taxa are null");
            }
        }

        h = 0;
        for (i = 0; i < nswaps; i++) {
            if (active[i]) {
                h++;
            }
        }
        if (h != nActive) {
            throw new IllegalStateException("Number of active splits not okay");
        }

        boolean[][] swapped = new boolean[ntaxa][ntaxa];

        for (i = 0; i < ntaxa; i++) {
            for (j = 0; j < ntaxa; j++) {
                swapped[i][j] = false;
            }
        }

        int[] cursequ = new int[ntaxa];
        for (i = 0; i < ntaxa; i++) {
            cursequ[i] = initSequ[i];
        }

        for (k = 0; k < nswaps; k++) {
            if (swapped[cursequ[swaps[k]]][cursequ[swaps[k] + 1]] == false) {
                swapped[cursequ[swaps[k]]][cursequ[swaps[k] + 1]] = true;
                swapped[cursequ[swaps[k] + 1]][cursequ[swaps[k]]] = true;
                h = cursequ[swaps[k]];
                cursequ[swaps[k]] = cursequ[swaps[k] + 1];
                cursequ[swaps[k] + 1] = h;
            } else {
                throw new IllegalStateException("Sequence of swaps not valid");
            }
        }

        return true;
    }

    /**
     * We need to number the active splits consecutively since SplitsTree seems only able to handle indices up to 10000
     */
    public void compressSplitIndices() {
        int i = 0;

        if (compressed == null) {
            compressed = new int[nswaps];
        }

        //mark active splits by 1
        for (i = 0; i < nswaps; i++) {
            if (active[i]) {
                compressed[i] = 1;
            } else {
                compressed[i] = 0;
            }
        }
        compressed[0] = compressed[0] - 1;
        //compute prefix sum
        for (i = 1; i < nswaps; i++) {
            compressed[i] = compressed[i] + compressed[i - 1];
        }
    }

    //This method computes the distance matrix
    //induced by the active splits in the flat split
    //system. This method still exists mainly for testing
    //purposes. It should not be called to compute
    //the induced distance on larger flat split systems
    //as it is very slow. A faster method for computing
    //the induced distance is available in the 
    //class FitWeight
    public double[][] get_induced_distance() {
        double[][] dist = new double[ntaxa][ntaxa];
        SplitSystemDraw ssyst = new SplitSystemDraw(this);

        int i = 0;
        int j = 0;
        int k = 0;

        for (i = 0; i < ntaxa; i++) {
            dist[i][i] = 0.0;
        }

        for (i = 0; i < (ntaxa - 1); i++) {
            for (j = i + 1; j < ntaxa; j++) {
                dist[i][j] = 0.0;
                for (k = 0; k < nswaps; k++) {
                    if (active[k]) {
                        if (ssyst.splits[k][i] != ssyst.splits[k][j]) {
                            dist[i][j] = dist[i][j] + weights[k];
                        }
                    }
                }
                dist[j][i] = dist[i][j];
            }
        }
        return dist;
    }

    /**
     * This method computes information about the graph that corresponds to the arrangement of pseudolines that is used
     * to carry out matrix-vector multiplications efficiently in the class FitWeight.
     * @return
     */
    public ArrangementData computeArrangement() {
        //Create the object that stores the information about the arrangement
        ArrangementData arrdata = new ArrangementData();
        //Allocate memory for the arrays in this object.
        arrdata.arr = new int[nswaps][4];
        arrdata.lastswap = new int[ntaxa];
        arrdata.upperlower = new int[ntaxa];
        arrdata.change = new double[nswaps][2];

        int[] cursequ = new int[ntaxa];

        int i = 0;
        int h = 0;

        for (i = 0; i < ntaxa; i++) {
            cursequ[i] = initSequ[i];
            arrdata.lastswap[i] = -i - 1;
        }

        for (i = 0; i < nswaps; i++) {
            //store the two edges that lead to the left
            arrdata.arr[i][0] = arrdata.lastswap[cursequ[swaps[i] + 1]];
            arrdata.arr[i][1] = arrdata.lastswap[cursequ[swaps[i]]];
            //store two dummy edges that lead to the right
            arrdata.arr[i][2] = -cursequ[swaps[i] + 1] - 1;
            arrdata.arr[i][3] = -cursequ[swaps[i]] - 1;
            //store edges that go from last swap to the right
            //if they exist to replace the dummy edges
            if (arrdata.lastswap[cursequ[swaps[i] + 1]] >= 0) {
                if (arrdata.upperlower[cursequ[swaps[i] + 1]] == -1) {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i] + 1]]][2] = i;
                } else {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i] + 1]]][3] = i;
                }
            }
            if (arrdata.lastswap[cursequ[swaps[i]]] >= 0) {
                if (arrdata.upperlower[cursequ[swaps[i]]] == -1) {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i]]]][2] = i;
                } else {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i]]]][3] = i;
                }
            }
            //update arrays
            arrdata.upperlower[cursequ[swaps[i] + 1]] = -1;
            arrdata.upperlower[cursequ[swaps[i]]] = 1;
            arrdata.lastswap[cursequ[swaps[i] + 1]] = i;
            arrdata.lastswap[cursequ[swaps[i]]] = i;
            h = cursequ[swaps[i] + 1];
            cursequ[swaps[i] + 1] = cursequ[swaps[i]];
            cursequ[swaps[i]] = h;
        }
        return arrdata;
    }

    /**
     * This method is used to set the flags so that the local search is performed on full flat split systems
     */
    public void init_local_search() {
        hasWeights = true;
        hasActiveFlags = true;

        int i = 0;

        for (i = 0; i < nswaps; i++) {
            active[i] = true;
        }
        compressSplitIndices();
    }

    //********************************************************************
//Private methods of this class
//********************************************************************
    //Used in the constructor that computes a permutation
    //sequence from a set of points in the plane. The
    //elements in the initial permutation are sorted according
    //to decreasing x-coordinates as these coordinates
    //correspond to the slope of the dual straight line
    //associated to a point. This is just a simple insertion-
    //sort and, hence, very slow. If this turns out to
    //be a bottleneck at some point, it should be replaced
    //by a faster algorithm.
    private void sortinitsequence(double[] xcoord, double[] ycoord) {
        int i = 0;
        int j = 0;
        int h = 0;

        for (i = 1; i < ntaxa; i++) {
            for (j = i; j > 0; j--) {
                if ((xcoord[initSequ[j]] > xcoord[initSequ[j - 1]])
                        || ((xcoord[initSequ[j]] == xcoord[initSequ[j - 1]]) && (ycoord[initSequ[j]] > ycoord[initSequ[j - 1]]))) {
                    h = initSequ[j - 1];
                    initSequ[j - 1] = initSequ[j];
                    initSequ[j] = h;
                } else {
                    break;
                }
            }
        }
    }

    public void restoreTrivialWeightsForExternalVertices() {
        SplitSystemDraw ss = new SplitSystemDraw(this);
        double min = -1;
        for (int i = 0; i < trivial.length; i++) {
            if (trivial[i] != 0 && (min == -1 || min > trivial[i])) {
                min = trivial[i];
            }
        }
        for (int i = 0; i < nswaps; i++) {
            Integer taxaNr = isTrivial(ss.splits[i]);
            if (taxaNr != null && trivial[taxaNr] > 0) {
                if (!active[i]) {
                    active[i] = true;
                    nActive++;
                }
                weights[i] = trivial[taxaNr];
                trivial[taxaNr] = 0;
            }
        }
    }

    private Integer isTrivial(int[] i) {
        int ones = 0;
        int zeros = 0;
        for (int j = 0; j < i.length; j++) {
            if (i[j] == 0) {
                zeros++;
            } else if (i[j] == 1) {
                ones++;
            } else {
                return null;
            }
        }
        Integer indicator = null;
        if (ones == 1) {
            indicator = 1;
        } else if (zeros == 1) {
            indicator = 0;
        }
        if (indicator != null) {
            for (int j = 0; j < i.length; j++) {
                if (i[j] == indicator) {
                    return j;
                }
            }
        }
        return null;
    }

    void filter(double threshold) {
        for (int i = 0; i < active.length; i++) {
            active[i] = true;
        }

        SplitSystemDraw ss = new SplitSystemDraw(this);

        boolean current;
        for (int i = 0; i < ss.nsplits; i++) {
            if (active[i]) {
                for (int j = 0; j < ss.nsplits; j++) {
                    if (!ss.isCompatible(i, j)) {
                        if (weights[i] < weights[j] * threshold) {
                            active[i] = false;
                        } else if (weights[j] < weights[i] * threshold) {
                            active[j] = false;
                        }
                    }
                }
            }
        }

        nActive = 0;
        for (int i = 0; i < active.length; i++) {
            if (active[i]) {
                nActive++;
            }
        }
    }




    public void setTaxaNames(String[] taxaNames) {
        taxaname = new String[taxaNames.length];
        System.arraycopy(taxaNames, 0, taxaname, 0, taxaNames.length);
    }

    public void removeSplitsSmallerOrEqualThan(double thr) {
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] <= 0) {
                active[i] = false;
            }
        }
    }


}
