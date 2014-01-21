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

package uk.ac.uea.cmp.phybre.flatnj.fdraw;

/*This class is used to store the permutation sequence representing
 a flat split system*/

import uk.ac.uea.cmp.phybre.core.util.CollectionUtils;

import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Random;

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
    //Constructor of this class from a given initial sequence 
    //and a sequence of swaps. Exists mainly for testing
    //purposes.
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

    //Constructor of this class from a given initial sequence,
    //a sequence of swaps, weights and array of active flags. Fot the
    //transition from PS in FNet.
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


    //Constructor of this class from a permutation
    //sequence stored in a nexus file
    //The threshold can be used to filter
    //the splits.
    public PermutationSequenceDraw(String filename, double thold) {
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
                set_active(thold);
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
    }

    //Constructor of a template for a circular
    //split system from a list of taxa 
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

    //Constructor that generates a random permutation
    //sequence. The first parameter is the number of
    //taxa. The second parameter is used to indicate
    //the type of split system:
    //t = 0: general flat split system
    //t = 1: circular split system
    public PermutationSequenceDraw(int n, int t) {
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
        if (t == 1) {
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
        else if (t == 0) {
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

    //*********************************************************************
//Other public methods of this class
//*********************************************************************
    //This method prints the sequence on the screen.
    public void print_sequence() {
        int i = 0;

        System.out.print("Number of taxa: ");
        System.out.println(ntaxa);

        System.out.println("List of taxa:");
        for (i = 0; i < ntaxa; i++) {
            System.out.println(taxaname[i]);
        }

        System.out.print("Initial permutation: ");
        for (i = 0; i < ntaxa; i++) {
            System.out.print(initSequ[i] + " ");
        }
        System.out.print("\n");

        System.out.print("Sequence of swaps: ");
        for (i = 0; i < nswaps; i++) {
            System.out.print(swaps[i] + " ");
        }
        System.out.print("\n");

        if (hasWeights) {
            System.out.print("Weights: ");
            for (i = 0; i < nswaps; i++) {
                System.out.print(weights[i] + " ");
            }
            System.out.print("\n");
        }

        System.out.print("Active swaps: ");
        for (i = 0; i < nswaps; i++) {
            if (active[i]) {
                System.out.print("1 ");
            } else {
                System.out.print("0 ");
            }
        }
        System.out.print("\n");
    }

    //This method filters the splits by labeling
    //those with a length below the given threshold
    //inactive.
    public void set_active(double thold) {
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

    //Checks whether the permutation sequence is
    //valid, that is, whether all arrays have
    //been initialized properly and whether every 
    //pair of distinct taxe swaps precisely once. 
    //The latter test, in particular, was included
    //to detect potential numerical problems when
    //constructing a permutation sequence from a
    //set of points in the plane whose coordinates
    //are given by floating point numbers.
    public int checkconsistency() {
        int i = 0;
        int j = 0;
        int k = 0;
        int h = 0;

        if (initSequ == null) {
            System.out.println("Array init_sequ is null");
            return 1;
        }

        if (taxaname == null) {
            System.out.println("Array taxaname is null");
            return 1;
        }

        if (swaps == null) {
            System.out.println("Array swaps is null");
            return 1;
        }

        if (active == null) {
            System.out.println("Array active is null");
            return 1;
        }

        if (representedby == null) {
            System.out.println("Array representedby is null");
            return 1;
        }

        if (activeTaxa == null) {
            System.out.println("Array activetaxa is null");
            return 1;
        }

        if (weights == null) {
            System.out.println("Array weights is null");
            return 1;
        }

        if (initSequ.length != ntaxa) {
            System.out.println("Length of array init_sequ not okay");
            return 1;
        }

        if (taxaname.length != ntaxa) {
            System.out.println("Length of array taxaname not okay");
            return 1;
        }

        if (swaps.length != nswaps) {
            System.out.println("Length of array swaps not okay");
            return 1;
        }

        if (active.length != nswaps) {
            System.out.println("Length of array active not okay");
            return 1;
        }

        if (representedby.length != ntaxa) {
            System.out.println("Length of array representedby not okay");
            return 1;
        }

        if (activeTaxa.length != ntaxa) {
            System.out.println("Length of array activetaxa not okay");
            return 1;
        }

        if (weights.length != nswaps) {
            System.out.println("Length of array weights not okay");
            return 1;
        }

        for (i = 0; i < (ntaxa - 1); i++) {
            for (j = i + 1; j < ntaxa; j++) {
                if (initSequ[i] == initSequ[j]) {
                    System.out.println("Entries of init_sequ are not pairwise distinct");
                    return 1;
                }
            }
        }

        for (i = 0; i < ntaxa; i++) {
            if ((initSequ[i] < 0) || (initSequ[i] >= ntaxa)) {
                System.out.println("Entries of init_sequ are not in range 0,1,...,(ntaxa-1)");
                return 1;
            }
        }

        for (i = 0; i < nswaps; i++) {
            if (weights[i] < 0.0) {
                System.out.println("Some split weights are negative");
                return 1;
            }
        }

        for (i = 0; i < ntaxa; i++) {
            if (taxaname[i] == null) {
                System.out.println("Some names of taxa are null");
                return 1;
            }
        }

        h = 0;
        for (i = 0; i < nswaps; i++) {
            if (active[i]) {
                h++;
            }
        }
        if (h != nActive) {
            System.out.println("Number of active splits not okay");
            return 1;
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
                System.out.println("Sequence of swaps not valid");
                return 1;
            }
        }

        //System.out.println("Permutation sequence set up okay");
        return 0;
    }

    //This method opens a nexus file and writes the 
    //permutation sequence to a flatsplits block in
    //a nexus file. 
    public void write_to_flatplits_block(String filename) {
        PrintWriter pw = NexusIO.openprintwriter(filename);
        NexusIO.writeheader(pw);
        NexusIO.writetaxa(ntaxa, taxaname, pw);
        NexusIO.writeflatsplits(ntaxa, nswaps, this, pw);
        NexusIO.closeprintwriter(pw);
    }

    //This method opens a nexus file and writes the 
    //permutation sequence to a splits block in
    //a nexus file. 
    public void write_to_splits_block(String filename) {
        PrintWriter pw = NexusIO.openprintwriter(filename);
        NexusIO.writeheader(pw);
        NexusIO.writetaxa(ntaxa, taxaname, pw);
        NexusIO.writesplits(ntaxa, nActive, this, pw);
        NexusIO.closeprintwriter(pw);
    }

    //This method writes the split system and the
    //induced distance matrix to a nexus file
    public void write_induced_distances(String filename) {
        double[][] dist = FitWeight.compute_induced_distance_arr(this);
        PrintWriter pw = NexusIO.openprintwriter(filename);

        NexusIO.writeheader(pw);
        NexusIO.writetaxa(ntaxa, taxaname, pw);
        NexusIO.writeflatsplits(ntaxa, nswaps, this, pw);
        NexusIO.writedistances(ntaxa, dist, pw);
        NexusIO.closeprintwriter(pw);
    }

    //We need to number the active splits consecutively
    //since SplitsTree seems only able to handle indices
    //up to 10000
    public void compress_split_indices() {
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

    //This method computes information about the graph 
    //that corresponds to the arrangement of pseudolines
    //that is used to carry out matrix-vector multiplications
    //efficiently in the class FitWeight. 
    public ArrangementData compute_arrangement() {
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

    //This method is used to set the flags
    //so that the local search is performed
    //on full flat split systems
    public void init_local_search() {
        hasWeights = true;
        hasActiveFlags = true;

        int i = 0;

        for (i = 0; i < nswaps; i++) {
            active[i] = true;
        }
        compress_split_indices();
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

    void restoreTrivialWeightsForExternalVertices() {
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

    public double[] getWeights() {
        return weights;
    }

    public void setTaxaNames(String[] taxaNames) {
        taxaname = new String[taxaNames.length];
        System.arraycopy(taxaNames, 0, taxaname, 0, taxaNames.length);
    }

    void removeSplitsSmallerOrEqualThan(double thr) {
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] <= 0) {
                active[i] = false;
            }
        }
    }


}
