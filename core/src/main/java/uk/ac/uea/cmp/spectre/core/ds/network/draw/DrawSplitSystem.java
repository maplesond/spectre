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
import uk.ac.uea.cmp.spectre.core.ds.split.*;

import java.util.*;

/**
 * This class is used to store the permutation sequence representing a flat split system
 **/
public class DrawSplitSystem {

    //*************************************************************
    //Variables used in this class
    //*************************************************************

    //This array keeps track of the representative of
    //the classes of taxa. Those are active. All other
    //taxa are inactive.
    Map<Integer, Boolean> activeTaxa;
    //weights of all trivial splits
    Map<Integer, Double> trivial;
    // The split system to draw
    SplitSystem ss;



    //**********************************************************
    //Constructors for this class
    //**********************************************************


    /**
     * Constructor for this class from a SplitSystem-object. In input might not necessarily be a "full" split system, so
     * we need to recreate the split system as a full one.
     * @param ss Circular split system
     */
    public DrawSplitSystem(SplitSystem ss) {

        this.ss = ss;

        final int ntaxa = ss.getNbTaxa();

        activeTaxa = new HashMap<>(ntaxa);
        trivial = new HashMap<>(ntaxa);

        //store information about the taxa in arrays
        for (Identifier i : this.ss.getOrderedTaxa()) {
            //Initial permutation must equal circular ordering underlying the circular split system.
            //Assumes that Ids used are 1,2,3,...,ntaxa.
            activeTaxa.put(i.getId(), true);
            trivial.put(i.getId(), 0.0); //no extra length added to pendant edge;
        }
    }


    //***********************************
    // Getters
    //***********************************

    public int getNbTaxa() {return this.ss.getNbTaxa();}

    public SplitSystem getSplitSystem() {
        return this.ss;
    }

    public boolean[] getActive() {
        boolean[] active = new boolean[this.ss.getNbSplits()];
        for(int i = 0; i < this.ss.getNbSplits(); i++) {
            active[i] = this.ss.get(i).isActive();
        }
        return active;
    }

    public boolean getActive(int index) {
        return this.ss.get(index).isActive();
    }

    public void setActiveTaxaAt(final int index, boolean value) {
        activeTaxa.put(index, value);
    }

    public double getTrivial(int taxonId) {
        return trivial.get(taxonId);
    }

    public double[] getWeights() {
        return this.ss.getWeightsAsArray();
    }

    //*********************************************************************
    //Other public methods of this class
    //*********************************************************************

    public Network createUnoptimisedNetwork() {
        Vertex net = drawSplitSystem();
        SpectreNetwork network = new SpectreNetwork(net);
        return network;
    }

    public Network createOptimisedNetwork() {
        Vertex net = drawSplitSystem();
        SpectreNetwork network = new SpectreNetwork(net);
        net = net.optimiseLayout(this, network);
        CompatibleCorrector compatibleCorrectorPrecise = new CompatibleCorrector(new AngleCalculatorMaximalArea());
        compatibleCorrectorPrecise.addInnerTrivial(net, this, network);
        if (!network.veryLongTrivial()) {
            compatibleCorrectorPrecise.moveTrivial(net, 5, network);
        }
        return network;
    }

    private static class NetworkDrawing {
        Vertex v;
        TreeSet<Edge>[] splitedges;

        public NetworkDrawing(Vertex v, TreeSet<Edge>[] splitEdges) {
            this.v = v;
            this.splitedges = splitEdges;
        }

        public int getNumberEdges() {
            int nbEdges = 0;
            for (int i = 0; i < this.splitedges.length; i++) {
                nbEdges += this.splitedges[i].size();
            }

            return nbEdges;
        }
    }

    /**
     * This method is the main public method that should be called for computing a plane split network for a split
     * system.
     * @return Split graph represented by a single vertex.  The network can be traversed from this vertex.
     */
    public Vertex drawSplitSystem() {

        // Creates a regular network from the stored split system
        NetworkDrawing network = this.computeRegularNetwork();

        // The regular network is not necessarily minimal.  Some splits might be compatible and can therefore be minimised.
        // So we apply another algorithm to the regular network to minimise it
        network = this.makeNetworkMinimal(network, true);

        return network.v;
    }

    public int[] collectIndicesOfActiveSplits() {
        int[] activeSplits = new int[this.ss.getNbActiveWeightedSplits()];

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
        TreeSet<Edge>[] splitedges = new TreeSet[this.ss.getNbSplits()];

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
     * @return Split graph represented by a single vertex and edges.  The network can be traversed from this vertex.
     */
    public NetworkDrawing computeRegularNetwork() {

        // Compute the initial path which consists of the leftmost edges in the network.
        // This also initializes the sets of edges associated to each split.
        Path path = this.createInitialPath();

        // This is only used if there are no splits in the split system and hence all taxa fit on this vertex
        Vertex v = new Vertex(0.0, 0.0);

        // In the original, this was done via the initial ordering... not sure if this is a problem or not.
        for (Identifier i : this.ss.getOrderedTaxa()) {

            // First check to see if we have a chain at all first.
            // If not then just add this taxon to a single vertex at the origin
            if (path.chain.length == 0) {
                v.getTaxa().add(i);
                if (i.getId() != 0) {
                    this.setActiveTaxaAt(i.getId(), false);
                }
            }
            else {

                // Add all boxes to path required by this taxon
                addBoxes(i.getId(), path);

                // Now to find the vertex that should be labeled by taxon i and label it
                labelVertex(i, path.chain);
            }
        }

        return new NetworkDrawing(path.chain.length == 0 ? v : path.chain[0].getTop(), path.splitEdges);
    }


    private static class Path {
        TreeSet<Edge>[] splitEdges;
        Edge[] chain;

        public Path(TreeSet<Edge>[] splitEdges, Edge[] chain) {
            this.splitEdges = splitEdges;
            this.chain = chain;
        }
    }

    // ***********************
    // *** Private methods ***
    // ***********************

    /**
     * This method computes the chain of edges that form the left boundary of the resulting split network before trimming
     * away unlabeled degree two vertices and pushing out trivial splits.
     * @return Left-most edges in the split network
     */
    private Path createInitialPath() {

        int j = 0;

        final int nactive = this.ss.getNbActiveWeightedSplits();

        Edge[] chain = new Edge[nactive];
        TreeSet<Edge>[] splitedges = new TreeSet[this.ss.getNbSplits()];

        Vertex cur = new Vertex(0.0, 0.0);

        for (int i = 0; i < this.ss.getNbSplits(); i++) {

            //create new set for edges associated to this split
            splitedges[i] = new TreeSet<>();

            if (this.ss.get(i).isActive()) {

                // Current vertex is now previous
                Vertex prev = cur;

                // Calc angle for this split.  Just assume a gradually increasing angle for now.
                final double angle = ((double)(j + 1) * Math.PI) / ((double)nactive + 1.0);
                final double weight = this.ss.get(i).getWeight();

                // Update coords for current vertex based on previous coords and this split's angle and weight
                double xCoord = prev.getX() - weight * Math.cos(angle);
                double yCoord = prev.getY() - weight * Math.sin(angle);

                cur = new Vertex(xCoord, yCoord);

                // Create new edge from previous and current vertices and include the split index
                Edge e = new Edge(prev, cur, i, 1);
                splitedges[i].add(e);
                chain[j] = e;

                // Ensure previous and current vertices are aware of this edge
                prev.prependEdge(e);
                cur.appendEdge(e);

                j++;
            }
        }

        return new Path(splitedges, chain);
    }

    /**
     * This modifies the provided path so that all required boxes are added for this taxon
     * @param i The taxon id
     * @param p The path (will get modified by this method)
     */
    private void addBoxes(int i, Path p) {

        Edge[] chain = p.chain;
        TreeSet<Edge>[] splitedges = p.splitEdges;

        boolean inverted = true;

        while (inverted) {
            inverted = false;

            for (int j = 1; j < chain.length; j++) {
                //test if the splits associated to edges
                //chain[j-1] and chain[j] must be inverted
                Edge ce1 = chain[j - 1];
                Edge ce2 = chain[j];

                if (this.ss.get(ce1.getSplitIndex()).getSide(i) == Split.SplitSide.B_SIDE &&
                        this.ss.get(ce2.getSplitIndex()).getSide(i) == Split.SplitSide.A_SIDE) {

                    double newX = ce1.getTop().getX() + ce2.getDeltaX();
                    double newY = ce1.getTop().getY() + ce2.getDeltaY();

                    Vertex v = new Vertex(newX, newY);

                    Edge e1 = new Edge(ce1.getTop(), v, ce2.getSplitIndex(), ce2.getTimestp() + 1);
                    Edge e2 = new Edge(v, ce2.getBottom(), ce1.getSplitIndex(), ce1.getTimestp() + 1);

                    splitedges[e1.getSplitIndex()].add(e1);
                    splitedges[e2.getSplitIndex()].add(e2);
                    chain[j - 1] = e1;
                    chain[j] = e2;
                    v.prependEdge(e2);
                    v.appendEdge(e1);
                    e1.getTop().prependEdge(e1);
                    e2.getBottom().appendEdge(e2);
                    inverted = true;
                }
            }
        }
    }

    private void labelVertex(Identifier i, Edge[] chain) {
        for(Edge ej : chain) {
            Split.SplitSide side = this.ss.get(ej.getSplitIndex()).getSide(i.getId());
            if (side == Split.SplitSide.B_SIDE) {
                ej.getTop().getTaxa().add(i);
                if (ej.getTop().getTaxa().size() > 1) {
                    this.setActiveTaxaAt(i.getId(), false);
                }
                break;
            }
            else if (side == Split.SplitSide.A_SIDE) {
                // This is just a special case to make sure we aren't missing the identifier at the bottom of the last edge
                if (ej == chain[chain.length - 1]) {
                    ej.getBottom().getTaxa().add(i);
                    if (ej.getBottom().getTaxa().size() > 1) {
                        this.setActiveTaxaAt(i.getId(), false);
                    }
                }
            }
            else {
                throw new IllegalStateException("Couldn't location identifier in split.");
            }
        }
    }


    /**
     * This method checks for every pair of distinct splits in the split system whether they are compatible.  If they
     * are it is checked whether these two splits form a box in the network. If they do we remove it. This ensures that
     * the resulting split network is minimal.
     * @param network The regular network to be minimised, includes the starting vertex of the network and the splitedges.
     *                Warning: Will be modified by this function!
     * @param check Validate the result
     * @return Network with compatible boxes removed
     */
    public NetworkDrawing makeNetworkMinimal(NetworkDrawing network, boolean check) {

        //count incompatible pairs
        int count = 0;

        //check all pairs of active splits
        for (int i = 0; i < this.ss.getNbSplits() - 1; i++) {
            Split si = this.ss.get(i);
            if (si.isActive()) {
                for (int j = i + 1; j < this.ss.getNbSplits(); j++) {
                    Split sj = this.ss.get(j);
                    if (sj.isActive()) {
                        if (this.ss.isCompatible(i, j)) {
                            network.v = this.removeBox(network.v, i, j, network.splitedges);
                            if (network.v == null) {
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
        if (check) {
            int nedges = network.getNumberEdges();

            if (((nedges - this.ss.getNbActiveWeightedSplits()) / 2) != count) {
                throw new IllegalStateException("Numbers do not match!!!!!");
            }
        }

        return network;
    }

    /**
     * This method checks whether a pair of distinct compatible splits form a box in the network. If they do the box is
     * removed.
     * @param net Network
     * @param a Compatible split A
     * @param b Compatible split B
     * @param splitedges Edges in the split network
     * @return Network with box removed if found, otherwise the same network that was passed in.
     */
    private Vertex removeBox(Vertex net, int a, int b, TreeSet<Edge>[] splitedges) {

        //check if a and b form a box in the network
        NetworkBox netbox = NetworkBox.formBox(a, b, splitedges);
        if (netbox != null) {

            // Regenerate the network without the box
            Vertex v = this.removeBoxByFlipping(a, b, netbox, splitedges);

            if (v == null) {
                throw new NullPointerException("Got a null vertex back");
            }

            return v;
        }

        // Otherwise it was not possible to form a box, so just return the same network that was passed in.
        return net;
    }

    /**
     * This method removes the whole unnecessary part of the network identified by two compatible splits from the
     * network
     * @param a Compatible split A
     * @param b Compatible split B
     * @param netbox Network box
     * @param splitedges Path
     * @return Network with box removed
     */
    private Vertex removeBoxByFlipping(int a, int b, NetworkBox netbox, TreeSet<Edge>[] splitedges) {

        Vertex u = null;

        // Direction for splits a and b
        Split.Direction dira;
        Split.Direction dirb;
        Split.Compatible pattern = this.ss.getCompatible(a, b);

        boolean parta = pattern.partAOn();
        boolean partb = pattern.partBOn();

        //determine direction in which we collect edges
        if (netbox.getF1().getTimestp() < netbox.getF2().getTimestp()) {
            dira = pattern.getDirA(false);
            dirb = pattern.getDirB(false);
        } else {
            dira = pattern.getDirA(true);
            dirb = pattern.getDirB(true);
        }

        // Now collect edges associated with the network box
        Pair<EdgeList, EdgeList> aLists = this.collectEdgesFromBox(netbox.getE1(), netbox.getE2(), dira, splitedges[a], parta);
        Pair<EdgeList, EdgeList> bLists = this.collectEdgesFromBox(netbox.getF1(), netbox.getF2(), dirb, splitedges[b], partb);

        // Find splits that cross a and b
        EdgeList crossboth = this.findCrossBoth(aLists.getRight(), bLists.getRight(), b);

        while (crossboth.size() > 1) {

            // Need to get rid of the splits that cross a and b. First find a triangle.
            Shape.Triangle triangle = this.findTriangle(a, crossboth, splitedges);

            if (triangle == null) {
                throw new IllegalStateException("Should this be allowed?");
            }
            this.removeTriangle(a, b, triangle, dira, parta, crossboth, aLists.getLeft(), bLists.getLeft(), splitedges);
        }

        // Now cut off the unnecessary part of the network
        if (crossboth.size() <= 1) {
            u = clearSector(a, b, aLists.getLeft(), bLists.getLeft(), dira, dirb, parta, partb, splitedges);
        }

        return u;
    }

    /**
     * Collect the edges that are relevant for clearing the quadrant.
     * @param h1 Edge 1
     * @param h2 Edge 2
     * @param dirs Direction (Left or Right)
     * @param splitedges Edges associated with this split
     * @param parts Part of compatiblility pattern
     * @return A pair of edge lists.  The first represents a list of relevant edges and the second represents those that cross
     */
    private Pair<EdgeList, EdgeList> collectEdgesFromBox(Edge h1, Edge h2, Split.Direction dirs,
                                               TreeSet<Edge> splitedges, boolean parts) {

        EdgeList crossLists = new EdgeList();
        EdgeList eLists = new EdgeList();

        int stopstp = h1.getTimestp() < h2.getTimestp() ?
                dirs == Split.Direction.LEFT ? h2.getTimestp() : h1.getTimestp() :
                dirs == Split.Direction.LEFT ? h1.getTimestp() : h2.getTimestp();

        if (dirs == Split.Direction.LEFT) {
            for (Edge g : splitedges) {
                if (g.getTimestp() < stopstp) {
                    eLists.addFirst(g);
                    crossLists.addFirst(parts ?
                            g.getTop().getEdgeList().getPreviousEdge(g) :
                            g.getBottom().getEdgeList().getNextEdge(g));
                } else {
                    break;
                }
            }
        } else {
            for (Edge g : splitedges) {
                if (g.getTimestp() > stopstp) {
                    eLists.addLast(g);
                    crossLists.addLast(parts ?
                            g.getTop().getEdgeList().getNextEdge(g) :
                            g.getBottom().getEdgeList().getPreviousEdge(g));
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

        EdgeList crossboth = new EdgeList();

        for (Edge e : crosslista) {
            if (e.getSplitIndex() == b) {
                crossboth.addLast(e);
            } else {
                for (Edge f : crosslistb) {
                    if (e.getSplitIndex() == f.getSplitIndex()) {
                        crossboth.addLast(e);
                        break;
                    }
                }
            }
        }

        return crossboth;
    }

    /**
     * This method locates a triangle and also collects the edges that can be used to
     * clear the triangle if necessary
     * @param splitidx
     * @param crossboth
     * @param splitedges
     * @return
     */
    private Shape.Triangle findTriangle(int splitidx, EdgeList crossboth, TreeSet[] splitedges) {
        EdgeList edges = new EdgeList();

        if (crossboth.size() < 2) {
            throw new IllegalArgumentException("Expected crossboth to contain at least two edges.");
        }

        int s = -1;
        Set<Integer> crossindices = crossboth.getSplitIndexSet();

        for(int i = 1; i < crossboth.size(); i++) {
            Edge e = crossboth.get(i-1);
            Edge f = crossboth.get(i);

            Crossing crossingE = goToFirstCrossing(e, splitidx, crossindices, splitedges[e.getSplitIndex()]);
            Crossing crossingF = goToFirstCrossing(f, splitidx, crossindices, splitedges[f.getSplitIndex()]);

            // Triangle found?
            if ((crossingE.splitidx == f.getSplitIndex()) && (crossingF.splitidx == e.getSplitIndex())) {
                edges.addAll(crossingF.edges);
                s = e.getSplitIndex();
                break;
            }
        }

        return s == -1 || edges.isEmpty() ? null : new Shape.Triangle(s, edges);
    }

    private static class Crossing {
        private int splitidx;
        private LinkedList<Edge> edges;

        public Crossing(int splitidx, LinkedList<Edge> edges) {
            this.splitidx = splitidx;
            this.edges = edges;
        }

    }

    /**
     * This method walks through the network to the first crossing with a split in crossindices
     * @param e
     * @param a
     * @param crossindices
     * @param splitedges
     * @return
     */
    private static Crossing goToFirstCrossing(Edge e, int a, Set crossindices, TreeSet splitedges) {

        Edge f;
        LinkedList<Edge> liste = new LinkedList<>();

        // First check which direction we need to go
        Split.Direction dir = a == e.getBottom().getEdgeList().getNextEdge(e).getSplitIndex() ?
            Split.Direction.RIGHT : Split.Direction.LEFT;

        if (dir == Split.Direction.RIGHT) {
            Iterator<Edge> tail = splitedges.tailSet(e).iterator();
            tail.next();  // Skip the first entry as that will be the current edge
            while (tail.hasNext()) {
                f = tail.next();
                int sidx = f.getTop().getEdgeList().getNextEdge(f).getSplitIndex();
                if (crossindices.contains(sidx)) {
                    return new Crossing(sidx, liste);
                } else {
                    liste.addLast(f);
                }
            }
        } else {
            // Get edges in head in reverse order
            Iterator<Edge> headiter = new LinkedList<>(splitedges.headSet(e)).descendingIterator();
            while (headiter.hasNext()) {
                f = headiter.next();
                int sidx = f.getBottom().getEdgeList().getNextEdge(f).getSplitIndex();
                if (crossindices.contains(sidx)) {
                    return new Crossing(sidx, liste);
                } else {
                    liste.addLast(f);
                }
            }
        }

        // Something's wrong! :(
        throw new IllegalStateException("Did not find a crossing");
    }


    /**
     * This method removes one triangle from the empty quadrant of two compatible splits
     * @param a
     * @param b
     * @param triangle
     * @param dira
     * @param parta
     * @param crossboth
     * @param elista
     * @param elistb
     * @param splitedges
     */
    private void removeTriangle(int a, int b, Shape.Triangle triangle, Split.Direction dira, boolean parta,
                                EdgeList crossboth, EdgeList elista,
                                EdgeList elistb, TreeSet[] splitedges) {
        Edge e = triangle.edges.getFirst();

        // First check which direction we need to go (left or right)
        Split.Direction dire = splitedges[e.getSplitIndex()].tailSet(e).size() != 1 &&
                a == e.getBottom().getEdgeList().getNextEdge(e).getSplitIndex() ?
                Split.Direction.RIGHT : Split.Direction.LEFT;

        // Now check if we need to go up or down
        Flip flipDir = flipDirection(parta, dira, dire);

        while (!triangle.edges.isEmpty()) {
            this.findFlippableCubeInTriangle(triangle, crossboth, flipDir, a, b, parta, dira, elista, elistb, splitedges);
        }
    }

    public enum Flip {
        UP,
        DOWN
    };

    private Flip flipDirection(boolean partA, Split.Direction d, Split.Direction e) {
        if (partA) {
            return d == Split.Direction.LEFT ?
                e == Split.Direction.LEFT ? Flip.DOWN : Flip.UP :
                e == Split.Direction.RIGHT ? Flip.DOWN : Flip.UP;
        } else {
            return d == Split.Direction.LEFT ?
                e == Split.Direction.RIGHT ? Flip.DOWN : Flip.UP :
                e == Split.Direction.LEFT ? Flip.DOWN : Flip.UP;
        }
    }


    /**
     * This method locates and flips a cube during the removal of a triangle
     * @param triangle
     * @param crossboth
     * @param flipdir
     * @param a
     * @param b
     * @param parta
     * @param dira
     * @param elista
     * @param elistb
     * @param splitedges
     */
    private void findFlippableCubeInTriangle(Shape.Triangle triangle, EdgeList crossboth,
                                                        Flip flipdir, int a, int b, boolean parta, Split.Direction dira,
                                                        EdgeList elista, EdgeList elistb,
                                                        TreeSet[] splitedges) {
        if (triangle.edges.isEmpty()) {
            throw new IllegalArgumentException("List of flip edges is empty");
        }


        Shape.Cube c = null;

        final int s = triangle.splitidx;

        for(Edge e : triangle.edges) {
            c = new Shape.Cube(e, flipdir);
            if (c.flippable) {
                break;
            }
        }

        if (c == null || !c.valid) {
            throw new IllegalStateException("Could not find flippable cube in triangle");
        }

        // Flip cube
        c.flip();

        // Update lists of edges accordingly
        splitedges[c.e.getSplitIndex()].remove(c.e);
        splitedges[c.e.getSplitIndex()].add(c.h3);
        splitedges[c.e2.getSplitIndex()].remove(c.e2);
        splitedges[c.e2.getSplitIndex()].add(c.h1);
        splitedges[c.e1.getSplitIndex()].remove(c.e1);
        splitedges[c.e1.getSplitIndex()].add(c.h2);

        // Update other data stuctures, possibly removing edges crossing the triangle
        c.update(parta, dira, s, a, b, triangle.edges, crossboth, elista, elistb);
    }


    //This method cuts off the unnecessary part of the network
    private Vertex clearSector(int a, int b, LinkedList<Edge> elista, LinkedList<Edge> elistb,
                                      Split.Direction dira, Split.Direction dirb, boolean parta, boolean partb, TreeSet[] splitedges) {

        Vertex u = null;

        Edge e = elista.getFirst();
        Edge g = null;
        Edge g1 = null;
        Edge g2 = null;

        //first eliminate the edges that correspond to splits that cross a
        if (dira == Split.Direction.LEFT) {
            SortedSet<Edge> head = splitedges[e.getSplitIndex()].headSet(e);
            for (Edge h : head) {
                g1 = h.getTop().getEdgeList().getPreviousEdge(h);
                g2 = h.getBottom().getEdgeList().getNextEdge(h);

                if (parta && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (parta && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!parta && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!parta && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            SortedSet<Edge> tail = splitedges[e.getSplitIndex()].tailSet(e);
            for (Edge h : tail) {
                if (h == tail.first()) {
                    continue;
                }
                g1 = h.getTop().getEdgeList().getNextEdge(h);
                g2 = h.getBottom().getEdgeList().getPreviousEdge(h);

                if (parta && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (parta && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!parta && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!parta && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        Edge f = elistb.getFirst();

        //next eliminate edges that correspond to splits thath cross b
        if (dirb == Split.Direction.LEFT) {
            SortedSet<Edge> head = splitedges[f.getSplitIndex()].headSet(f);
            for (Edge h : head) {
                g1 = h.getTop().getEdgeList().getPreviousEdge(h);
                g2 = h.getBottom().getEdgeList().getNextEdge(h);

                if (partb && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (partb && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!partb && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!partb && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            SortedSet<Edge> tail = splitedges[f.getSplitIndex()].tailSet(f);
            for (Edge h : tail) {
                if (h == tail.first()) {
                    continue;
                }
                g1 = h.getTop().getEdgeList().getNextEdge(h);
                g2 = h.getBottom().getEdgeList().getPreviousEdge(h);

                if (partb && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].last();
                        if (g.getTimestp() > g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (partb && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g1.getSplitIndex()].first();
                        if (g.getTimestp() < g1.getTimestp()) {
                            splitedges[g1.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!partb && (g1.getTimestp() < g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].first();
                        if (g.getTimestp() < g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if (!partb && (g1.getTimestp() > g2.getTimestp())) {
                    while (true) {
                        g = (Edge) splitedges[g2.getSplitIndex()].last();
                        if (g.getTimestp() > g2.getTimestp()) {
                            splitedges[g2.getSplitIndex()].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        //now it remains to update the lists of edges for splits a and b
        if (dira == Split.Direction.LEFT) {
            while (true) {
                g = (Edge) splitedges[a].first();
                if (g.getTimestp() <= e.getTimestp()) {
                    splitedges[a].remove(g);
                    if (parta) {
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
                    if (parta) {
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

        if (dirb == Split.Direction.LEFT) {
            while (true) {
                g = (Edge) splitedges[b].first();
                if (g.getTimestp() <= f.getTimestp()) {
                    splitedges[b].remove(g);
                    if (partb) {
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
                    if (partb) {
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
