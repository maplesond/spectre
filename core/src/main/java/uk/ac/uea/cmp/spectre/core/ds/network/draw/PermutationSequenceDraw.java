/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.network.*;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

import java.util.*;

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
     * Constructor of this class from a given initial sequence, a sequence of swaps, weights and array of active flags.
     * For the transition from PS in FNet.
     * @param in_init_sequ Initial sequence
     * @param in_swaps Sequence of swaps
     * @param in_weights Weights
     * @param in_active Active flags
     * @param trivialWeights Trivial weights
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

        nActive = CollectionUtils.nbTrueElements(active);
    }


    /**
     * Constructor for this class from a SplitSystem-object. This is just used as a quick and dirty way to feed circular
     * split systems into the drawing algorithm and then display them with the viewer.
     * @param ss Circular split system
     */
    public PermutationSequenceDraw(SplitSystem ss){

        //first get the number of taxa and number of splits
        //in a full circular split system on that number of taxa
        ntaxa = ss.getNbTaxa();
        nclasses = ntaxa;
        nswaps = ntaxa * (ntaxa - 1) / 2;
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
        for (int i = 0; i < ntaxa; i++) {
            //Initial permutation must equal circular ordering underlying the circular split system.
            //Assumes that Ids used are 1,2,3,...,ntaxa.
            initSequ[i] = ss.getOrderedTaxa().get(i).getId()-1;
            representedby[i] = i;
            activeTaxa[i] = true;
            taxaname[ss.getOrderedTaxa().get(i).getId()-1] = ss.getOrderedTaxa().get(i).getName();
            trivial[i] = 0.0; //no extra length added to pendant edge;
        }

        //array used to store the current permutation from which
        //we extract the current split
        int[] cursequ = new int[ntaxa];
        for (int i = 0; i < ntaxa; i++) {
            cursequ[i] = initSequ[i];
        }

        int h = 0;
        int k = 0;
        int a = 0;

        //the generic structure of a circular split system
        for (int i = 0; i < ntaxa; i++) {
            for (int j = 0; j < ntaxa - i - 1; j++) {
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
                for(a=0;a<=j;a++) {sideA[a]=cursequ[a]+1;}
                SplitBlock ssb = new SpectreSplitBlock(sideA);
                ssb.sort();

                //next check whether the input split system contains a split with one SplitBlock equal to the one we just created
                for (Split s : ss) {
                    SplitBlock ssba = s.getASide();
                    ssba.sort();
                    SplitBlock ssbb = s.getBSide();
                    ssbb.sort();
                    if(ssba.equals(ssb) || ssbb.equals(ssb)){
                        weights[k] = s.getWeight();
                        if(weights[k] > 0) {active[k] = true;}
                    }
                }
                k++;
            }
        }

        //update number of active splits
        nActive = CollectionUtils.nbTrueElements(active);
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

    public boolean hasWeights() {
        return hasWeights;
    }

    public boolean hasActiveFlags() {
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

    public boolean getActive(int index) {
        return active[index];
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

    public Network createOptimisedNetwork() {
        Vertex net = drawSplitSystem(-1.0);
        SpectreNetwork network = new SpectreNetwork(net);
        net = net.optimiseLayout(this, network);
        /*CompatibleCorrector compatibleCorrectorPrecise = new CompatibleCorrector(new AngleCalculatorMaximalArea());
        compatibleCorrectorPrecise.addInnerTrivial(net, this, network);
        if (!network.veryLongTrivial()) {
            compatibleCorrectorPrecise.moveTrivial(net, 5, network);
        }*/
        for(Vertex v : network.getLabeledVertices()) {
            v.setSize(3);
            v.setShape(null);
        }

        return network;
    }


    /**
     * This method filters the splits by labeling those with a length below the given threshold inactive.
     * @param thold Threshold value
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

    /**
     * This method is the main public method that should be called for computing a plane split network for a flat split
     * system given as a permutation sequence.
     * @param thr Threshold, below which splits are ignored
     * @return Split graph represented by a single vertex.  The network can be traversed from this vertex.
     */
    public Vertex drawSplitSystem(double thr) {
        this.restoreTrivialWeightsForExternalVertices();

        this.removeSplitsSmallerOrEqualThan(thr);

        //Array of sets of edges, one for each split
        TreeSet<Edge>[] splitedges = new TreeSet[this.getNswaps()];

        Vertex v = this.computeSplitGraph(splitedges);

        v = this.removeCompatibleBoxes(v, false, splitedges);

        return v;
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

    public void removeSplitsSmallerOrEqualThan(double thr) {
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] <= 0) {
                active[i] = false;
            }
        }
    }

    public int[] collectIndicesOfActiveSplits() {
        int[] activeSplits = new int[this.getnActive()];

        //Index used to fill in array of active splits
        int j = 0;
        //Go through all the splits and select active ones
        for (int i = 0; i < this.getActive().length; i++) {
            if (this.getActive(i)) {
                activeSplits[j++] = i;
            }
        }
        return activeSplits;
    }

    public TreeSet<Edge>[] collectEdgesForTheSplits(final Vertex v) {
        TreeSet<Edge>[] splitedges = new TreeSet[this.getNswaps()];

        for (int i = 0; i < this.getActive().length; i++) {
            LinkedList<Edge> edges = v.collectEdgesForSplit(i);
            splitedges[i] = new TreeSet<>();
            for (int k = 0; k < edges.size(); k++) {
                splitedges[i].add(edges.get(k));
            }
        }
        return splitedges;
    }

    /**
     * This method computes a split graph representing the flat split system given by a permutation sequence.  It returns
     * a vertex of the resulting split graph from which the network can be traversed.  The split graph may contain
     * unlabeled vertices of degree two and trivial splits may be represented by more than one edge in the network. This
     * is the first step of the computation of the final network.  It is provided as a public method for testing
     * purposes.
     * @param splitedges Array of treesets representing split edges
     * @return Split graph represented by a single vertex.  The network can be traversed from this vertex.
     */
    public Vertex computeSplitGraph(TreeSet[] splitedges) {
        //Compute the leftmost edges in the network.
        //This also initializes the sets of edges
        //associated to each split.
        Edge[] chain = this.leftmostEdges(splitedges);

        //Complete the network and return it
        return this.completeNetwork(chain, splitedges);
    }

    public enum Direction {
        LEFT,
        RIGHT
    }


    // ***********************
    // *** Private methods ***
    // ***********************

    /**
     * This method computes the chain of edges that form the left boundary of the resulting split network before trimming
     * away unlabeled degree two vertices and pushing out trivial splits.
     * @param splitedges Set of splits
     * @return Left-most edges in the split network
     */
    private Edge[] leftmostEdges(TreeSet<Edge>[] splitedges) {
        int i = 0;
        int j = 0;
        double dx = 0.0;
        double dy = 0.0;
        double xcoord = 0.0;
        double ycoord = 0.0;

        Vertex u = null;
        Vertex v = null;
        Edge e = null;

        Edge[] chain = new Edge[this.getnActive()];

        u = new Vertex(xcoord, ycoord);

        for (i = 0; i < this.getNswaps(); i++) {
            //create new set for edges associated to this split
            splitedges[i] = new TreeSet<>();

            if (this.getActive()[i]) {
                dx = -Math.cos(((j + 1) * Math.PI) / (this.getnActive() + 1));
                dy = -Math.sin(((j + 1) * Math.PI) / (this.getnActive() + 1));

                xcoord = xcoord + (this.getWeights()[i] * dx);
                ycoord = ycoord + (this.getWeights()[i] * dy);

                v = u;
                u = new Vertex(xcoord, ycoord);
                e = new Edge(v, u, i, 1);
                splitedges[i].add(e);
                chain[j] = e;
                v.add_edge_before_first(e);
                u.add_edge_after_last(e);
                j++;
            }
        }

        return chain;
    }

    /**
     * This method extends the initial chain computed by the method leftmost_edges() to a split network for the split
     * system.
     * @param chain Left-most edges in the split network
     * @param splitedges The split network
     * @return Completed network
     */
    private Vertex completeNetwork(Edge[] chain, TreeSet[] splitedges) {
        SplitSystemDraw ssyst = new SplitSystemDraw(this);

        int i = 0;
        int j = 0;

        boolean inverted = true;
        double dx = 0.0;
        double dy = 0.0;

        Vertex v = null;
        Edge e1 = null;
        Edge e2 = null;

        for (i = 0; i < this.getNtaxa(); i++) {
            inverted = true;
            //System.out.println("Taxon " + pseq.initSequ[i]);

            final int initSequI = this.getInitSequ()[i];

            while (inverted) {
                inverted = false;

                for (j = 1; j < chain.length; j++) {
                    //test if the splits associated to edges
                    //chain[j-1] and chain[j] must be inverted

                    if (ssyst.splits[chain[j - 1].getIdxsplit()][initSequI] == 0
                            && ssyst.splits[chain[j].getIdxsplit()][initSequI] == 1) {
                        dx = (chain[j].getBottom()).getX() - (chain[j].getTop()).getX();
                        dy = (chain[j].getBottom()).getY() - (chain[j].getTop()).getY();

                        v = new Vertex((chain[j - 1].getTop()).getX() + dx, (chain[j - 1].getTop()).getY() + dy);
                        e1 = new Edge(chain[j - 1].getTop(), v, chain[j].getIdxsplit(), chain[j].getTimestp() + 1);
                        splitedges[e1.getIdxsplit()].add(e1);
                        e2 = new Edge(v, chain[j].getBottom(), chain[j - 1].getIdxsplit(), chain[j - 1].getTimestp() + 1);
                        splitedges[e2.getIdxsplit()].add(e2);
                        chain[j - 1] = e1;
                        chain[j] = e2;
                        v.add_edge_before_first(e2);
                        v.add_edge_after_last(e1);
                        (e1.getTop()).add_edge_before_first(e1);
                        (e2.getBottom()).add_edge_after_last(e2);
                        inverted = true;
                    }
                }
            }

            //Now it remains to find the vertex that should be
            //labeled by taxon i

            if (chain.length == 0) {
                if (v == null) {
                    v = new Vertex(0.0, 0.0);
                }
                v.getTaxa().add(new Identifier(taxaname[initSequI], initSequI));
                this.setRepresentedByAt(initSequI, 0);
                if (initSequI != 0) {
                    this.setActiveTaxaAt(initSequI, false);
                }
                this.setNClasses(1);
            } else {
                v = chain[0].getTop();
                for (j = 0; j < chain.length; j++) {
                    if (ssyst.splits[chain[j].getIdxsplit()][initSequI] == 0) {
                        (chain[j].getTop()).getTaxa().add(new Identifier(taxaname[initSequI], initSequI));
                        if (chain[j].getTop().getTaxa().size() > 1) {
                            this.setRepresentedByAt(initSequI, chain[j].getTop().getTaxa().getFirst().getId());
                            this.setActiveTaxaAt(initSequI, false);
                            this.decrementNClasses();
                        }
                        break;
                    } else {
                        if (j == (chain.length - 1)) {
                            (chain[j].getBottom()).getTaxa().add(new Identifier(taxaname[initSequI], initSequI));
                            if (chain[j].getBottom().getTaxa().size() > 1) {
                                this.setRepresentedByAt(initSequI, chain[j].getBottom().getTaxa().getFirst().getId());
                                this.setActiveTaxaAt(initSequI, false);
                                this.decrementNClasses();
                            }
                        }
                    }
                }
            }
        }
        return v;
    }

    /**
     * This method checks for every pair of distinct splits in the split system whether they are compatible.  If they
     * are it is checked whether these two splits form a box in the network. If they do we remove it. This ensures that
     * the resulting split network is minimal.
     * @param v Starting vertex of the network
     * @param check Do checking for valid result
     * @param splitedges Split edges
     * @return Network with compatible boxes removed
     */
    public Vertex removeCompatibleBoxes(Vertex v, boolean check, TreeSet<Edge>[] splitedges) {

        //get the split system from the permutation sequence
        SplitSystemDraw ssyst = new SplitSystemDraw(this);

        //loop variables
        int i = 0;
        int j = 0;

        //count incompatible pairs
        int count = 0;

        //number of rounds
        int rounds = 0;

        //vertex in the split network
        Vertex u = v;

        //code for compatibility pattern
        SplitSystemDraw.Compatible pattern = null;

        //check all pairs of active splits
        for (i = 0; i < (this.getNswaps() - 1); i++) {
            if (this.getActive()[i]) {

                for (j = i + 1; j < this.getNswaps(); j++) {
                    if (this.getActive()[j]) {
                        pattern = ssyst.isCompatible(i, j);
                        if (pattern.isCompatible()) {
                            //rounds++;
                            u = this.removeBox(u, i, j, pattern, splitedges);
                            if (u != null) {
                                //s = filename + rounds + ".nex";
                                //write_splits_graph_to_file(u,s,psequ.ntaxa,psequ.taxaname,psequ);
                            } else {
                                throw new NullPointerException("Vertex u is null - stop here");
                            }
                        } else {
                            count++;
                        }
                    }
                }
            }
        }

        //Check if the numbers of boxes and edges in the output makes sense
        //First compute the number edges in the network
        int nedges = 0;

        for (i = 0; i < splitedges.length; i++) {
            nedges = nedges + splitedges[i].size();
        }

        final int nbActive = this.getnActive();

        if ((check) && ((nedges - nbActive) / 2) != count) {
            throw new IllegalStateException("Numbers do not match!!!!!");
        }

        return u;
    }

    /**
     * This method checks whether a pair of distinct compatible splits form a box in the network. If they do the box is
     * removed.
     * @param net Network
     * @param a Split A
     * @param b Split B
     * @param pattern Compatibility pattern
     * @param splitedges Edges in the split network
     * @return Network with box removed if found, otherwise the same network that was passed in.
     */
    private Vertex removeBox(Vertex net, int a, int b, SplitSystemDraw.Compatible pattern, TreeSet<Edge>[] splitedges) {

        //check if a and b form a box in the network
        NetworkBox netbox = NetworkBox.formBox(a, b, splitedges);
        if (netbox != null) {

            // Regenerate the network without the box
            Vertex v = this.removeBoxByFlipping(a, b, pattern, netbox, splitedges);

            if (v == null) {
                throw new NullPointerException("Got a null vertex back");
            }

            return v;
        }

        // Otherwise just return the same network that was passed in
        return net;
    }

    /**
     * This method removes the whole unnecessary part of the network identified by two compatible splits from the
     * network
     * @param a Split A
     * @param b Split B
     * @param pattern Compatibility pattern
     * @param netbox Network box
     * @param splitedges Eplit edges
     * @return Network with box removed
     */
    private Vertex removeBoxByFlipping(int a, int b, SplitSystemDraw.Compatible pattern, NetworkBox netbox, TreeSet[] splitedges) {
        Vertex u = null;


        //direction for splits a and b
        //-1:left,1:right
        Direction dira = null;
        Direction dirb = null;
        int parta = 0;
        int partb = 0;

        //determine direction in which we collect edges
        if (netbox.getF1().getTimestp() < netbox.getF2().getTimestp()) {
            if (pattern == SplitSystemDraw.Compatible.YES_11) {
                dira = Direction.LEFT;
                dirb = Direction.RIGHT;
                parta = 1;
                partb = 1;
            } else if (pattern == SplitSystemDraw.Compatible.YES_10) {
                dira = Direction.RIGHT;
                dirb = Direction.RIGHT;
                parta = 1;
                partb = 0;
            } else if (pattern == SplitSystemDraw.Compatible.YES_01) {
                dira = Direction.LEFT;
                dirb = Direction.LEFT;
                parta = 0;
                partb = 1;
            } else if (pattern == SplitSystemDraw.Compatible.YES_00) {
                dira = Direction.RIGHT;
                dirb = Direction.LEFT;
                parta = 0;
                partb = 0;
            }
        } else {
            if (pattern == SplitSystemDraw.Compatible.YES_11) {
                dira = Direction.RIGHT;
                dirb = Direction.LEFT;
                parta = 1;
                partb = 1;
            } else if (pattern == SplitSystemDraw.Compatible.YES_10) {
                dira = Direction.LEFT;
                dirb = Direction.LEFT;
                parta = 1;
                partb = 0;
            } else if (pattern == SplitSystemDraw.Compatible.YES_01) {
                dira = Direction.RIGHT;
                dirb = Direction.RIGHT;
                parta = 0;
                partb = 1;
            } else if (pattern == SplitSystemDraw.Compatible.YES_00) {
                dira = Direction.LEFT;
                dirb = Direction.RIGHT;
                parta = 0;
                partb = 0;
            }
        }

        //now collect edges
        Pair<EdgeList, EdgeList> aLists = this.collectEdgesFromBox(netbox.getE1(), netbox.getE2(), a, dira, splitedges, parta);
        Pair<EdgeList, EdgeList> bLists = this.collectEdgesFromBox(netbox.getF1(), netbox.getF2(), b, dirb, splitedges, partb);

        //find splits that cross a and b
        EdgeList crossboth = this.findCrossBoth(aLists.getRight(), bLists.getRight(), b);

        //Temporary list used when clearing a triangle
        EdgeList cleanlist = new EdgeList();

        while (crossboth.size() > 1) {
            //Need to get rid of the splits that cross a and b
            //First find a triangle
            int s = this.findTriangle(a, crossboth, cleanlist, splitedges);
            this.getRidOfTriangle(a, b, s, dira, parta, crossboth, cleanlist, aLists.getLeft(), bLists.getLeft(), splitedges);
        }

        //now simply cut off the unnecessary part of the network
        if (crossboth.size() <= 1) {
            //System.out.println("Cut off quadrant");
            u = cutOffUnnecessaryPart(a, b, aLists.getLeft(), bLists.getLeft(), dira, dirb, parta, partb, splitedges);
        }

        return u;
    }

    /**
     * Collect the edges that are relevant for clearing the quadrant.
     * @param h1 Edge 1
     * @param h2 Edge 2
     * @param s Split index
     * @param dirs Direction (Left or Right)
     * @param splitedges Split system
     * @param parts Unknown ?
     * @return A pair of edge lists.  The first represents a list of relevant edges and the second represents those that cross
     */
    private Pair<EdgeList, EdgeList> collectEdgesFromBox(Edge h1, Edge h2, int s, Direction dirs,
                                               TreeSet[] splitedges, int parts) {
        Iterator iter = splitedges[s].iterator();
        EdgeList crossLists = new EdgeList();
        EdgeList eLists = new EdgeList();
        int stopstp = 0;

        Edge g = null;

        if (h1.getTimestp() < h2.getTimestp()) {
            if (dirs == Direction.LEFT) {
                stopstp = h2.getTimestp();
            } else {
                stopstp = h1.getTimestp();
            }
        } else {
            if (dirs == Direction.LEFT) {
                stopstp = h1.getTimestp();
            } else {
                stopstp = h2.getTimestp();
            }
        }

        if (dirs == Direction.LEFT) {
            while (iter.hasNext()) {
                g = (Edge) iter.next();
                if (g.getTimestp() < stopstp) {
                    eLists.addFirst(g);
                    if (parts == 1) {
                        crossLists.addFirst(g.getTop().getEdgeList().get((g.getTop().getEdgeList().indexOf(g) + g.getTop().getEdgeList().size() - 1) % g.getTop().getEdgeList().size()));
                    } else {
                        crossLists.addFirst(g.getBottom().getEdgeList().get((g.getBottom().getEdgeList().indexOf(g) + 1) % g.getBottom().getEdgeList().size()));
                    }
                } else {
                    break;
                }
            }
        } else {
            while (iter.hasNext()) {
                g = (Edge) iter.next();
                if (g.getTimestp() > stopstp) {
                    eLists.addLast(g);
                    if (parts == 1) {
                        crossLists.addLast(g.getTop().getEdgeList().get((g.getTop().getEdgeList().indexOf(g) + 1) % g.getTop().getEdgeList().size()));
                    } else {
                        crossLists.addLast(g.getBottom().getEdgeList().get((g.getBottom().getEdgeList().indexOf(g) + g.getBottom().getEdgeList().size() - 1) % g.getBottom().getEdgeList().size()));
                    }
                }
            }
        }

        return new ImmutablePair<>(eLists, crossLists);
    }


    /**
     * Extract the edges from crosslist a that correspond to splits that also crosslist b
     * @param crosslista cross list A
     * @param crosslistb cross list B
     * @param b Split
     * @return Edges that cross both lists
     */
    private EdgeList findCrossBoth(EdgeList crosslista, EdgeList crosslistb, int b) {
        ListIterator itera = crosslista.listIterator();
        ListIterator iterb = null;

        Edge e = null;
        Edge f = null;

        EdgeList crossboth = new EdgeList();

        while (itera.hasNext()) {
            e = (Edge) itera.next();

            if (e.getIdxsplit() == b) {
                crossboth.addLast(e);
            } else {
                iterb = crosslistb.listIterator();

                while (iterb.hasNext()) {
                    f = (Edge) iterb.next();
                    if (e.getIdxsplit() == f.getIdxsplit()) {
                        crossboth.addLast(e);
                        break;
                    }
                }
            }
        }

        return crossboth;
    }


    /**
     * This method locates a triangle and also collects the the cleanlist containing those edges that can be used to
     * clear the triangle if necessary
     * @param a
     * @param crossboth
     * @param cleanlist
     * @param splitedges
     * @return
     */
    private int findTriangle(int a, EdgeList crossboth, EdgeList cleanlist, TreeSet[] splitedges) {
        cleanlist.clear();

        TreeSet<Integer> crossindices = crossboth.getCrossIndices();

        Edge e = null;
        Edge f = null;

        int indexe = -1;
        int indexf = -1;

        LinkedList<Edge> liste = new LinkedList<>();
        LinkedList<Edge> listf = new LinkedList<>();

        ListIterator crossiter = crossboth.listIterator();

        if (crossiter.hasNext()) {
            e = (Edge) crossiter.next();
        }

        while (crossiter.hasNext()) {
            f = (Edge) crossiter.next();

            liste.clear();
            listf.clear();

            //System.out.println("Find first crossing for e");
            indexe = go_to_first_crossing(e, a, crossindices, liste, splitedges);
            //System.out.println("Find first crossing for f");
            indexf = go_to_first_crossing(f, a, crossindices, listf, splitedges);

            //triangle found?
            if ((indexe == f.getIdxsplit()) && (indexf == e.getIdxsplit())) {
                cleanlist.addAll(listf);
                break;
            } else {
                e = f;
            }
        }

        return e.getIdxsplit();
    }

    /**
     * This method walks through the network to the first scrossing with a split in crossindices
     * @param e
     * @param a
     * @param crossindices
     * @param liste
     * @param splitedges
     * @return
     */
    private static int go_to_first_crossing(Edge e, int a, TreeSet crossindices, LinkedList<Edge> liste, TreeSet[] splitedges) {
        int dire = 0;
        int sidx = 0;

        //first check in which direction we need to go
        SortedSet testtail = splitedges[e.getIdxsplit()].tailSet(e);

        Edge f = null;

        if (testtail.size() == 1) {
            dire = -1;
        } else if (a == (e.getBottom().getEdgeList().get((e.getBottom().getEdgeList().indexOf(e) + 1) % e.getBottom().getEdgeList().size())).getIdxsplit()) {
            dire = 1;
        } else {
            dire = -1;
        }

        if (dire == 1) {
            SortedSet tail = splitedges[e.getIdxsplit()].tailSet(e);
            Iterator tailiter = tail.iterator();
            f = (Edge) tailiter.next();
            while (tailiter.hasNext()) {
                f = (Edge) tailiter.next();
                sidx = (f.getTop().getEdgeList().get((f.getTop().getEdgeList().indexOf(f) + 1) % f.getTop().getEdgeList().size())).getIdxsplit();
                if (crossindices.contains(new Integer(sidx))) {
                    return sidx;
                } else {
                    liste.addLast(f);
                }
            }
        } else {
            SortedSet head = splitedges[e.getIdxsplit()].headSet(e);
            Iterator headiter = head.iterator();
            LinkedList reverse = new LinkedList();

            //Get edges in head in reverse order
            while (headiter.hasNext()) {
                f = (Edge) headiter.next();
                reverse.addFirst(f);
            }

            Iterator reviter = reverse.iterator();

            while (reviter.hasNext()) {
                f = (Edge) reviter.next();
                sidx = ((Edge) f.getBottom().getEdgeList().get((f.getBottom().getEdgeList().indexOf(f) + 1) % f.getBottom().getEdgeList().size())).getIdxsplit();
                if (crossindices.contains(new Integer(sidx))) {
                    return sidx;
                } else {
                    liste.addLast(f);
                }
            }
        }

        // Something's wrong! :(
        throw new IllegalStateException("Did not find a first crossing");
    }





    /**
     * This method removes one triangle from the empty quadrant of two compatible splits
     * @param a
     * @param b
     * @param s
     * @param dira
     * @param parta
     * @param crossboth
     * @param cleanlist
     * @param elista
     * @param elistb
     * @param splitedges
     */
    private void getRidOfTriangle(int a, int b, int s, Direction dira, int parta,
                                            EdgeList crossboth, EdgeList cleanlist, EdgeList elista,
                                            EdgeList elistb, TreeSet[] splitedges) {
        Edge e = cleanlist.getFirst();

        Direction dire = null;

        //first check in which direction we need to go
        SortedSet testtail = splitedges[e.getIdxsplit()].tailSet(e);

        Edge f = null;

        if (testtail.size() == 1) {
            dire = Direction.RIGHT;
        } else if (a == (e.getBottom().getEdgeList().get((e.getBottom().getEdgeList().indexOf(e) + 1) % e.getBottom().getEdgeList().size())).getIdxsplit()) {
            dire = Direction.LEFT;
        } else {
            dire = Direction.RIGHT;
        }

        Flip flipDir = flipDirection(parta, dira, dire);

        while (!cleanlist.isEmpty()) {
            this.findFlippableCubeInTriangle(cleanlist, crossboth, flipDir, a, b, s, parta, dira, elista, elistb, splitedges);
        }
    }

    public enum Flip {
        UP,
        DOWN
    }

    private static Flip flipDirection(int partA, Direction d, Direction e) {
        if (partA == 1) {
            if (d == Direction.LEFT) {
                return e == Direction.LEFT ? Flip.DOWN : Flip.UP;
            } else {
                return e == Direction.RIGHT ? Flip.DOWN : Flip.UP;
            }
        } else {
            if (d == Direction.LEFT) {
                return e == Direction.RIGHT ? Flip.DOWN : Flip.UP;
            } else {
                return e == Direction.LEFT ? Flip.DOWN : Flip.UP;
            }
        }
    }


    /**
     * This method locates and flips a cube during the removal of a triangle
     * @param elist
     * @param crossboth
     * @param flipdir
     * @param a
     * @param b
     * @param s
     * @param parta
     * @param dira
     * @param elista
     * @param elistb
     * @param splitedges
     */
    private static void findFlippableCubeInTriangle(EdgeList elist, EdgeList crossboth,
                                                        Flip flipdir, int a, int b, int s, int parta, Direction dira,
                                                        EdgeList elista, EdgeList elistb,
                                                        TreeSet[] splitedges) {
        if (elist.isEmpty()) {
            throw new IllegalArgumentException("List of flip edges is empty");
        }


        Edge e = null;
        Edge h1 = null;
        Edge h2 = null;
        Edge h3 = null;
        Edge e1 = null;
        Edge e2 = null;
        Edge e3 = null;
        Edge e4 = null;
        Edge e5 = null;
        Edge e6 = null;
        Edge e7 = null;
        Edge e8 = null;
        Vertex v = null;
        Vertex v1 = null;
        Vertex v2 = null;
        Vertex v3 = null;
        Vertex v4 = null;
        Vertex v5 = null;
        Vertex v6 = null;
        Vertex v7 = null;
        Vertex v8 = null;
        Vertex v9 = null;

        ListIterator iter = null;

        if (flipdir == Flip.UP) {
            //flip cubes upwards

            iter = elist.listIterator();

            //find a flippable cube
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                v = e.getTop();
                if (v.getEdgeList().size() == 3) {
                    e1 = v.getEdgeList().get((v.getEdgeList().indexOf(e) + 2) % 3);
                    e2 = v.getEdgeList().get((v.getEdgeList().indexOf(e) + 1) % 3);
                    v5 = e.getBottom();
                    if (v == e2.getBottom()) {
                        v1 = e2.getTop();
                    } else {
                        v1 = e2.getBottom();
                    }
                    if (v == e1.getBottom()) {
                        v3 = e1.getTop();
                    } else {
                        v3 = e1.getBottom();
                    }
                    e3 = v3.getEdgeList().get((v3.getEdgeList().indexOf(e1) + 1) % v3.getEdgeList().size());
                    e4 = v1.getEdgeList().get((v1.getEdgeList().indexOf(e2) + v1.getEdgeList().size() - 1) % v1.getEdgeList().size());
                    e5 = v1.getEdgeList().get((v1.getEdgeList().indexOf(e2) + 1) % v1.getEdgeList().size());
                    e6 = v5.getEdgeList().get((v5.getEdgeList().indexOf(e) + v5.getEdgeList().size() - 1) % v5.getEdgeList().size());
                    e7 = v5.getEdgeList().get((v5.getEdgeList().indexOf(e) + 1) % v5.getEdgeList().size());
                    e8 = v3.getEdgeList().get((v3.getEdgeList().indexOf(e1) + v3.getEdgeList().size() - 1) % v3.getEdgeList().size());
                    if (v1 == e4.getBottom()) {
                        v2 = e4.getTop();
                    } else {
                        v2 = e4.getBottom();
                    }
                    if (v3 == e3.getBottom()) {
                        v4 = e3.getTop();
                    } else {
                        v4 = e3.getBottom();
                    }
                    if (v3 == e8.getBottom()) {
                        v6 = e8.getTop();
                    } else {
                        v6 = e8.getBottom();
                    }
                    if (v5 == e7.getBottom()) {
                        v7 = e7.getTop();
                    } else {
                        v7 = e7.getBottom();
                    }
                    if (v5 == e6.getBottom()) {
                        v8 = e6.getTop();
                    } else {
                        v8 = e6.getBottom();
                    }
                    if (v1 == e5.getBottom()) {
                        v9 = e5.getTop();
                    } else {
                        v9 = e5.getBottom();
                    }
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.getIdxsplit() == e5.getIdxsplit())
                            && (e.getIdxsplit() == e8.getIdxsplit())
                            && (e2.getIdxsplit() == e3.getIdxsplit())
                            && (e2.getIdxsplit() == e6.getIdxsplit())
                            && (e1.getIdxsplit() == e4.getIdxsplit())
                            && (e1.getIdxsplit() == e7.getIdxsplit())) {
                        break;
                    }
                }
            }

            //flip cube
            v1.getEdgeList().remove(e2);
            v3.getEdgeList().remove(e1);
            v5.getEdgeList().remove(e);
            v.getEdgeList().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h1 = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h1 = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getEdgeList().add(v6.getEdgeList().indexOf(e7) + 1, h1);
            v.getEdgeList().addLast(h1);
            if (v6 == e7.getTop()) {
                h2 = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h2 = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getEdgeList().add(v8.getEdgeList().indexOf(e5) + 1, h2);
            v.getEdgeList().addLast(h2);
            h3 = new Edge(v2, v, e.getIdxsplit(), e.getTimestp());
            v2.getEdgeList().add(v2.getEdgeList().indexOf(e3) + 1, h3);
            v.getEdgeList().addLast(h3);

            //System.out.println("Size of elist before update: " + elist.size());

            //update lists of edges accordingly
            splitedges[e.getIdxsplit()].remove(e);
            splitedges[e.getIdxsplit()].add(h3);
            splitedges[e2.getIdxsplit()].remove(e2);
            splitedges[e2.getIdxsplit()].add(h1);
            splitedges[e1.getIdxsplit()].remove(e1);
            splitedges[e1.getIdxsplit()].add(h2);

            if (parta == 1) {
                if (dira == Direction.LEFT) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                }
            } else {
                if (dira == Direction.LEFT) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                }
            }
        } else {
            //flip cubes downwards

            iter = elist.listIterator();

            //find flippable cube
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                v = e.getBottom();
                if (v.getEdgeList().size() == 3) {
                    e1 = (Edge) v.getEdgeList().get((v.getEdgeList().indexOf(e) + 2) % 3);
                    e2 = (Edge) v.getEdgeList().get((v.getEdgeList().indexOf(e) + 1) % 3);
                    v5 = e.getTop();
                    if (v == e2.getBottom()) {
                        v1 = e2.getTop();
                    } else {
                        v1 = e2.getBottom();
                    }
                    if (v == e1.getBottom()) {
                        v3 = e1.getTop();
                    } else {
                        v3 = e1.getBottom();
                    }
                    e3 = v3.getEdgeList().get((v3.getEdgeList().indexOf(e1) + 1) % v3.getEdgeList().size());
                    e4 = v1.getEdgeList().get((v1.getEdgeList().indexOf(e2) + v1.getEdgeList().size() - 1) % v1.getEdgeList().size());
                    e5 = v1.getEdgeList().get((v1.getEdgeList().indexOf(e2) + 1) % v1.getEdgeList().size());
                    e6 = v5.getEdgeList().get((v5.getEdgeList().indexOf(e) + v5.getEdgeList().size() - 1) % v5.getEdgeList().size());
                    e7 = v5.getEdgeList().get((v5.getEdgeList().indexOf(e) + 1) % v5.getEdgeList().size());
                    e8 = v3.getEdgeList().get((v3.getEdgeList().indexOf(e1) + v3.getEdgeList().size() - 1) % v3.getEdgeList().size());
                    if (v1 == e4.getBottom()) {
                        v2 = e4.getTop();
                    } else {
                        v2 = e4.getBottom();
                    }
                    if (v3 == e3.getBottom()) {
                        v4 = e3.getTop();
                    } else {
                        v4 = e3.getBottom();
                    }
                    if (v3 == e8.getBottom()) {
                        v6 = e8.getTop();
                    } else {
                        v6 = e8.getBottom();
                    }
                    if (v5 == e7.getBottom()) {
                        v7 = e7.getTop();
                    } else {
                        v7 = e7.getBottom();
                    }
                    if (v5 == e6.getBottom()) {
                        v8 = e6.getTop();
                    } else {
                        v8 = e6.getBottom();
                    }
                    if (v1 == e5.getBottom()) {
                        v9 = e5.getTop();
                    } else {
                        v9 = e5.getBottom();
                    }
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.getIdxsplit() == e5.getIdxsplit())
                            && (e.getIdxsplit() == e8.getIdxsplit())
                            && (e2.getIdxsplit() == e3.getIdxsplit())
                            && (e2.getIdxsplit() == e6.getIdxsplit())
                            && (e1.getIdxsplit() == e4.getIdxsplit())
                            && (e1.getIdxsplit() == e7.getIdxsplit())) {
                        break;
                    }
                }
            }

            //flip cube
            v1.getEdgeList().remove(e2);
            v3.getEdgeList().remove(e1);
            v5.getEdgeList().remove(e);
            v.getEdgeList().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h1 = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h1 = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getEdgeList().add(v6.getEdgeList().indexOf(e7) + 1, h1);
            v.getEdgeList().addLast(h1);
            if (v6 == e7.getTop()) {
                h2 = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h2 = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getEdgeList().add(v8.getEdgeList().indexOf(e5) + 1, h2);
            v.getEdgeList().addLast(h2);
            h3 = new Edge(v, v2, e.getIdxsplit(), e.getTimestp());
            v2.getEdgeList().add(v2.getEdgeList().indexOf(e3) + 1, h3);
            v.getEdgeList().addLast(h3);

            //System.out.println("Size of elist before update: " + elist.size());

            //update lists of edges accordingly
            splitedges[e.getIdxsplit()].remove(e);
            splitedges[e.getIdxsplit()].add(h3);
            splitedges[e2.getIdxsplit()].remove(e2);
            splitedges[e2.getIdxsplit()].add(h1);
            splitedges[e1.getIdxsplit()].remove(e1);
            splitedges[e1.getIdxsplit()].add(h2);

            if (parta == 1) {
                if (dira == Direction.LEFT) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    //System.out.println("Size of elist after first update: " + elist.size());

                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                }
            } else {
                if (dira == Direction.LEFT) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() == a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.getIdxsplit() != a) && (e6.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() == a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.getIdxsplit() != a) && (e7.getIdxsplit() == s) && (s != b)) {
                        elist.removeLast();
                    }
                }
            }
        }
    }


    //This method cuts off the unecessary part of the network
    private static Vertex cutOffUnnecessaryPart(int a, int b, LinkedList<Edge> elista, LinkedList<Edge> elistb,
                                                   Direction dira, Direction dirb, int parta, int partb, TreeSet[] splitedges) {
        //System.out.println("cut off unnecessary part");

        Vertex u = null;

        Edge e = (Edge) elista.getFirst();
        Edge g = null;
        Edge h = null;
        Edge g1 = null;
        Edge g2 = null;

        //first eliminate the edges that correspond to splits that cross a
        if (dira == Direction.LEFT) {
            SortedSet head = splitedges[e.getIdxsplit()].headSet(e);
            Iterator headiter = head.iterator();
            while (headiter.hasNext()) {
                h = (Edge) headiter.next();
                g1 = h.getTop().getEdgeList().get((h.getTop().getEdgeList().indexOf(h) + (h.getTop().getEdgeList().size() - 1)) % h.getTop().getEdgeList().size());
                g2 = h.getBottom().getEdgeList().get((h.getBottom().getEdgeList().indexOf(h) + 1) % h.getBottom().getEdgeList().size());

                if ((parta == 1) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 1) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            SortedSet tail = splitedges[e.getIdxsplit()].tailSet(e);
            Iterator tailiter = tail.iterator();
            //tail contains e!!!
            h = (Edge) tailiter.next();
            while (tailiter.hasNext()) {
                h = (Edge) tailiter.next();
                g1 = h.getTop().getEdgeList().get((h.getTop().getEdgeList().indexOf(h) + 1) % h.getTop().getEdgeList().size());
                g2 = h.getBottom().getEdgeList().get((h.getBottom().getEdgeList().indexOf(h) + (h.getBottom().getEdgeList().size() - 1)) % h.getBottom().getEdgeList().size());

                if ((parta == 1) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 1) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        Edge f = elistb.getFirst();

        //next eliminate edges that correspond to splits thath cross b
        if (dirb == Direction.LEFT) {
            SortedSet head = splitedges[f.getIdxsplit()].headSet(f);
            Iterator headiter = head.iterator();
            while (headiter.hasNext()) {
                h = (Edge) headiter.next();
                g1 = h.getTop().getEdgeList().get((h.getTop().getEdgeList().indexOf(h) + (h.getTop().getEdgeList().size() - 1)) % h.getTop().getEdgeList().size());
                g2 = h.getBottom().getEdgeList().get((h.getBottom().getEdgeList().indexOf(h) + 1) % h.getBottom().getEdgeList().size());

                if ((partb == 1) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 1) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            SortedSet tail = splitedges[f.getIdxsplit()].tailSet(f);
            Iterator tailiter = tail.iterator();
            //tail contains f!!!
            h = (Edge) tailiter.next();
            while (tailiter.hasNext()) {
                h = (Edge) tailiter.next();
                g1 = h.getTop().getEdgeList().get((h.getTop().getEdgeList().indexOf(h) + 1) % h.getTop().getEdgeList().size());
                g2 = h.getBottom().getEdgeList().get((h.getBottom().getEdgeList().indexOf(h) + (h.getBottom().getEdgeList().size() - 1)) % h.getBottom().getEdgeList().size());

                if ((partb == 1) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 1) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getIdxsplit()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getIdxsplit()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getIdxsplit()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        //now it remains to update the lists of edges for splits a and b
        if (dira == Direction.LEFT) {
            while (true) {
                g = (Edge) splitedges[a].first();
                if (g.getTimestp() <= e.getTimestp()) {
                    splitedges[a].remove(g);
                    if (parta == 1) {
                        g.getTop().getEdgeList().remove(g);
                        u = g.getTop();
                    } else {
                        g.getBottom().getEdgeList().remove(g);
                        u = g.getBottom();
                    }
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                g = (Edge) splitedges[a].last();
                if (g.getTimestp() >= e.getTimestp()) {
                    splitedges[a].remove(g);
                    if (parta == 1) {
                        g.getTop().getEdgeList().remove(g);
                        u = g.getTop();
                    } else {
                        g.getBottom().getEdgeList().remove(g);
                        u = g.getBottom();
                    }
                } else {
                    break;
                }
            }
        }

        if (dirb == Direction.LEFT) {
            while (true) {
                g = (Edge) splitedges[b].first();
                if (g.getTimestp() <= f.getTimestp()) {
                    splitedges[b].remove(g);
                    if (partb == 1) {
                        g.getTop().getEdgeList().remove(g);
                    } else {
                        g.getBottom().getEdgeList().remove(g);
                    }
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                g = (Edge) splitedges[b].last();
                if (g.getTimestp() >= f.getTimestp()) {
                    if (partb == 1) {
                        g.getTop().getEdgeList().remove(g);
                    } else {
                        g.getBottom().getEdgeList().remove(g);
                    }
                    splitedges[b].remove(g);
                } else {
                    break;
                }
            }
        }

        return u;
    }





}
