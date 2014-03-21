/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.spectre.core.alg.nn;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.util.Arrays;
import java.util.Stack;

/**
 * Implements Neighbor Net method of Bryant and Moulton (2004).
 */
public class NeighborNetOld {


    private static final int MAX_TAXA_FOR_BASIC_ORDERING = 3;

    /**
     * Executes the NeighborNet algorithm for the given distance matrix, and produces a split system.
     *
     * @param distanceMatrix The Distance matrix to process
     * @return The computed split system
     */
    public SplitSystem createCircularSplitSystem(final DistanceMatrix distanceMatrix) {

        return new SpectreSplitSystem(distanceMatrix, this.computeCircularOrdering(distanceMatrix), SpectreSplitSystem.LeastSquaresCalculator.TREE_IN_CYCLE);
    }

    /**
     * Executes the NeighborNet algorithm for the given distance matrix to produce a circular ordering.
     *
     * @param distanceMatrix The Distance matrix to process
     * @return A CircularOrdering computed from the distance matrix using the NeighborNet algorithm
     */
    public IdentifierList computeCircularOrdering(final DistanceMatrix distanceMatrix) {

        // Special case for small taxa sets: use the order they came in; otherwise run NN proper
        return distanceMatrix.size() <= MAX_TAXA_FOR_BASIC_ORDERING ?
                distanceMatrix.getTaxa() :
                executeNeighborNet(distanceMatrix);
    }


    /**
     * Runs the core neighbor net algorithm.
     *
     * @param distanceMatrix The Distance matrix to process
     * @return A CircularOrdering computed from the distance matrix using the NeighborNet algorithm
     */
    protected IdentifierList executeNeighborNet(DistanceMatrix distanceMatrix) {

        // Get number of taxa in the distance matrix
        int nbTaxa = distanceMatrix.size();

        // Setup a big matrix to handle the maximum number of nodes
        double[][] bigDistanceMatrix = this.setupMatrix(distanceMatrix);

        // Create the initial network of taxa to agglomerate
        NetNode network = this.createInitialNetwork(nbTaxa);

        // Perform the agglomeration step
        int[] oneBasedOrdering = agglomerateNodes(bigDistanceMatrix, network, nbTaxa);

        // Return the ordered taxa
        IdentifierList ids = new IdentifierList();
        for (int i = 0; i < nbTaxa; i++) {
            ids.add(distanceMatrix.getTaxa().getById(oneBasedOrdering[i]));
        }

        return ids;
    }

    /**
     * Sets up the working matrix. The original distance matrix is enlarged to handle the maximum number of nodes.
     *
     * @param distanceMatrix Distance matrix to expand
     * @return A working matrix of appropriate cardinality
     */
    private static double[][] setupMatrix(DistanceMatrix distanceMatrix) {

        int nbTaxa = distanceMatrix.size();
        int maxNbNodes = 3 * nbTaxa - 5;
        double[][] bigMatrix = new double[maxNbNodes][maxNbNodes];

        for (int i = 1; i <= nbTaxa; i++) {
            for (int j = 1; j <= nbTaxa; j++)
                bigMatrix[i][j] = distanceMatrix.getDistance(i - 1, j - 1);
            Arrays.fill(bigMatrix[i], nbTaxa + 1, maxNbNodes, 0.0);
        }

        for (int i = nbTaxa + 1; i < maxNbNodes; i++)
            Arrays.fill(bigMatrix[i], 0, maxNbNodes, 0.0);

        return bigMatrix;
    }

    /**
     * Creates the initial NeighborNet Network as a doubly linked list which needs to be agglomerated.
     *
     * @param nbTaxa Initial size of the network represented by the number of taxa.
     * @return Initialised network
     */
    protected NetNode createInitialNetwork(final int nbTaxa) {

        // Create the first node
        NetNode netNodes = new NetNode();

        // Nodes are stored in a doubly linked list that we set up here.  Initially, all singleton nodes are active
        for (int i = nbTaxa; i >= 1; i--) {
            NetNode taxNode = new NetNode();
            taxNode.id = i;
            taxNode.next = netNodes.next;
            netNodes.next = taxNode;
        }

        // Set up links in other direction
        for (NetNode taxNode = netNodes; taxNode.next != null; taxNode = taxNode.next)
            taxNode.next.prev = taxNode;

        return netNodes;
    }

    /**
     * Agglomerates then expands the nodes.  Returns a 1-based circular ordering
     *
     * @param D        Distance matrix
     * @param netNodes Network to agglomerate
     * @param nbTaxa   Number of taxa in the network
     * @return Circular ordering for the agglomerated network
     */
    protected int[] agglomerateNodes(double D[][], NetNode netNodes, final int nbTaxa) {

        Stack<NetNode> amalgamations = new Stack<>();

        NetNode p, q, Cx, Cy, x, y;
        double Qpq, best;
        int nbNodes = nbTaxa;
        int nbActiveNodes = nbTaxa;
        int nbClusters = nbTaxa;
        int m;
        double Dpq;

        while (nbActiveNodes > 3) {

            // Special case: If we let this one go then we get a divide by zero when computing Qpq
            if (nbActiveNodes == 4 && nbClusters == 2) {
                p = netNodes.next;
                if (p.next != p.nbr)
                    q = p.next;
                else
                    q = p.next.next;
                if (D[p.id][q.id] + D[p.nbr.id][q.nbr.id] < D[p.id][q.nbr.id] + D[p.nbr.id][q.id]) {
                    agg3way(p, q, q.nbr, amalgamations, D, netNodes, nbNodes);
                    nbNodes += 2;
                } else {
                    agg3way(p, q.nbr, q, amalgamations, D, netNodes, nbNodes);
                    nbNodes += 2;
                }
                break;
            }

            // Compute the "averaged" sums s_i from each cluster to every other cluster.

            //TODO: 2x speedup by using symmetry

            for (p = netNodes.next; p != null; p = p.next)
                p.Sx = 0.0;
            for (p = netNodes.next; p != null; p = p.next) {
                if (p.nbr == null || p.nbr.id > p.id) {
                    for (q = p.next; q != null; q = q.next) {
                        if (q.nbr == null || (q.nbr.id > q.id) && (q.nbr != p)) {
                            Dpq = 0.0;
                            if ((p.nbr == null) && (q.nbr == null))
                                Dpq = D[p.id][q.id];
                            else if ((p.nbr != null) && (q.nbr == null))
                                Dpq = (D[p.id][q.id] + D[p.nbr.id][q.id]) / 2.0;
                            else if ((p.nbr == null) && (q.nbr != null))
                                Dpq = (D[p.id][q.id] + D[p.id][q.nbr.id]) / 2.0;
                            else
                                Dpq = (D[p.id][q.id] + D[p.id][q.nbr.id] + D[p.nbr.id][q.id] + D[p.nbr.id][q.nbr.id]) / 4.0;

                            p.Sx += Dpq;
                            if (p.nbr != null)
                                p.nbr.Sx += Dpq;
                            q.Sx += Dpq;
                            if (q.nbr != null)
                                q.nbr.Sx += Dpq;
                        }
                    }

                }
            }

            Cx = Cy = null;
            // Now minimize (m-2) D[C_i,C_k] - Sx - Sy
            best = 0;
            for (p = netNodes.next; p != null; p = p.next) {
                if ((p.nbr != null) && (p.nbr.id < p.id))
                    // We only evaluate one node per cluster
                    continue;
                for (q = netNodes.next; q != p; q = q.next) {
                    if ((q.nbr != null) && (q.nbr.id < q.id))
                        // We only evaluate one node per cluster
                        continue;
                    if (q.nbr == p)
                        // We only evaluate nodes in different clusters
                        continue;
                    if ((p.nbr == null) && (q.nbr == null))
                        Dpq = D[p.id][q.id];
                    else if ((p.nbr != null) && (q.nbr == null))
                        Dpq = (D[p.id][q.id] + D[p.nbr.id][q.id]) / 2.0;
                    else if ((p.nbr == null) && (q.nbr != null))
                        Dpq = (D[p.id][q.id] + D[p.id][q.nbr.id]) / 2.0;
                    else
                        Dpq = (D[p.id][q.id] + D[p.id][q.nbr.id] + D[p.nbr.id][q.id] + D[p.nbr.id][q.nbr.id]) / 4.0;
                    Qpq = ((double) nbClusters - 2.0) * Dpq - p.Sx - q.Sx;
                    // Check if this is the best so far
                    if ((Cx == null || (Qpq < best)) && (p.nbr != q)) {
                        Cx = p;
                        Cy = q;
                        best = Qpq;
                    }
                }
            }

            // Find the node in each cluster
            x = Cx;
            y = Cy;

            if (Cx.nbr != null || Cy.nbr != null) {
                Cx.Rx = ComputeRx(Cx, Cx, Cy, D, netNodes);
                if (Cx.nbr != null)
                    Cx.nbr.Rx = ComputeRx(Cx.nbr, Cx, Cy, D, netNodes);
                Cy.Rx = ComputeRx(Cy, Cx, Cy, D, netNodes);
                if (Cy.nbr != null)
                    Cy.nbr.Rx = ComputeRx(Cy.nbr, Cx, Cy, D, netNodes);
            }

            m = nbClusters;
            if (Cx.nbr != null)
                m++;
            if (Cy.nbr != null)
                m++;

            best = ((double) m - 2.0) * D[Cx.id][Cy.id] - Cx.Rx - Cy.Rx;
            if (Cx.nbr != null) {
                Qpq = ((double) m - 2.0) * D[Cx.nbr.id][Cy.id] - Cx.nbr.Rx - Cy.Rx;
                if (Qpq < best) {
                    x = Cx.nbr;
                    y = Cy;
                    best = Qpq;
                }
            }
            if (Cy.nbr != null) {
                Qpq = ((double) m - 2.0) * D[Cx.id][Cy.nbr.id] - Cx.Rx - Cy.nbr.Rx;
                if (Qpq < best) {
                    x = Cx;
                    y = Cy.nbr;
                    best = Qpq;
                }
            }
            if ((Cx.nbr != null) && (Cy.nbr != null)) {
                Qpq = ((double) m - 2.0) * D[Cx.nbr.id][Cy.nbr.id] - Cx.nbr.Rx - Cy.nbr.Rx;
                if (Qpq < best) {
                    x = Cx.nbr;
                    y = Cy.nbr;
                    best = Qpq;
                }
            }

            // We perform an agglomeration... one of three types
            if ((null == x.nbr) && (null == y.nbr)) {
                // Both vertices are isolated...add edge {x,y}
                agg2way(x, y);
                nbClusters--;
            } else if (null == x.nbr) {
                // X is isolated,  Y  is not isolated
                agg3way(x, y, y.nbr, amalgamations, D, netNodes, nbNodes);
                nbNodes += 2;
                nbActiveNodes--;
                nbClusters--;
            } else if ((null == y.nbr) || (nbActiveNodes == 4)) {
                // Y is isolated, X is not isolated OR theres only four active nodes and none are isolated
                agg3way(y, x, x.nbr, amalgamations, D, netNodes, nbNodes);
                nbNodes += 2;
                nbActiveNodes--;
                nbClusters--;
            } else {
                // Both nodes are connected to others and there are more than 4 active nodes
                nbNodes = agg4way(x.nbr, x, y, y.nbr, amalgamations, D, netNodes, nbNodes);
                nbActiveNodes -= 2;
                nbClusters--;
            }
        }

        return expandNodes(nbNodes, amalgamations, netNodes);
    }

    /**
     * Agglomerate 2 nodes
     *
     * @param x one node
     * @param y other node
     */
    protected void agg2way(NetNode x, NetNode y) {
        x.nbr = y;
        y.nbr = x;
    }

    /**
     * Agglomerate 3 nodes: x,y and z; to give 2 new nodes: u and v
     * <p/>
     * In terms of the linked list: we replace x and z by u and v and remove y from the linked list and replace y with
     * the new node z.  Returns a pointer to the node u
     * <p/>
     * Note that this version doesn't update nbNodes, you need to
     * nbNodes += 2 after calling this!
     *
     * @param x one node
     * @param y other node
     * @param z another node
     * @return one of the new nodes: u
     */
    protected NetNode agg3way(NetNode x, NetNode y, NetNode z,
                              Stack<NetNode> amalgamations, double[][] D, NetNode netNodes, int nbNodes) {

        NetNode u = new NetNode();
        u.id = nbNodes + 1;
        u.ch1 = x;
        u.ch2 = y;

        NetNode v = new NetNode();
        v.id = nbNodes + 2;
        v.ch1 = y;
        v.ch2 = z;

        // Replace x by u in the linked list
        u.next = x.next;
        u.prev = x.prev;
        if (u.next != null)
            u.next.prev = u;
        if (u.prev != null)
            u.prev.next = u;

        // Replace z by v in the linked list
        v.next = z.next;
        v.prev = z.prev;
        if (v.next != null)
            v.next.prev = v;
        if (v.prev != null)
            v.prev.next = v;

        // Remove y from the linked list
        if (y.next != null)
            y.next.prev = y.prev;
        if (y.prev != null)
            y.prev.next = y.next;

        // Add an edge between u and v, and add u into the list of amalgamations
        u.nbr = v;
        v.nbr = u;

        // Update distance matrix
        for (NetNode p = netNodes.next; p != null; p = p.next) {
            D[u.id][p.id] = D[p.id][u.id] = (2.0 / 3.0) * D[x.id][p.id] + D[y.id][p.id] / 3.0;
            D[v.id][p.id] = D[p.id][v.id] = (2.0 / 3.0) * D[z.id][p.id] + D[y.id][p.id] / 3.0;
        }
        D[u.id][u.id] = D[v.id][v.id] = 0.0;

        amalgamations.push(u);

        return u;
    }

    /**
     * Agglomerate four nodes.  Replace x2,x,y,y2 by with two vertices... performed using two 3 way amalgamations
     *
     * @param x2 a node
     * @param x  a node
     * @param y  a node
     * @param y2 a node
     * @return the new number of nodes
     */
    protected int agg4way(NetNode x2, NetNode x, NetNode y, NetNode y2,
                          Stack<NetNode> amalgamations, double[][] D, NetNode netNodes, int nbNodes) {

        // Replace x2, x and y by two nodes equal to x2_prev.next and y_prev.next.
        NetNode u = agg3way(x2, x, y, amalgamations, D, netNodes, nbNodes);
        nbNodes += 2;

        // z = y_prev . next
        agg3way(u, u.nbr, y2, amalgamations, D, netNodes, nbNodes);
        nbNodes += 2;

        return nbNodes;
    }

    /**
     * Computes the Rx (a matrix operation)
     *
     * @param z        a node
     * @param Cx       a node
     * @param Cy       a node
     * @param D        the distances
     * @param netNodes the net nodes
     * @return the Rx value
     */
    protected double ComputeRx(NetNode z, NetNode Cx, NetNode Cy, double[][] D,
                               NetNode netNodes) {
        double Rx = 0.0;

        for (NetNode p = netNodes.next; p != null; p = p.next) {
            if (p == Cx || p == Cx.nbr || p == Cy || p == Cy.nbr || p.nbr == null) {
                Rx += D[z.id][p.id];
            }
            // p.adjacent != null so we take the average of the distances
            else {
                Rx += D[z.id][p.id] / 2.0;
            }
        }

        return Rx;
    }

    /**
     * Quickly expands the net nodes to obtain the ordering
     *
     * @param nbTaxa   number of taxa
     * @param amalgs   stack of amalgamations
     * @param netNodes the net nodes
     */
    protected int[] expandNodes(final int nbTaxa, Stack<NetNode> amalgs, NetNode netNodes) {

        int[] ordering = new int[nbTaxa + 1];

        NetNode x, y, z, u, v, a;

        // Set up the circular order for the first three nodes
        x = netNodes.next;
        y = x.next;
        z = y.next;
        z.next = x;
        x.prev = z;

        // Now do the rest of the expansions
        while (!amalgs.empty()) {

            // Find the three elements replacing u and v. Swap u and v around if v comes before u in the circular
            // ordering being built up
            u = (NetNode) (amalgs.pop());
            v = u.nbr;
            x = u.ch1;
            y = u.ch2;
            z = v.ch2;
            if (v != u.next) {
                NetNode tmp = u;
                u = v;
                v = tmp;
                tmp = x;
                x = z;
                z = tmp;
            }

            // Insert x,y,z into the circular order
            x.prev = u.prev;
            x.prev.next = x;
            x.next = y;
            y.prev = x;
            y.next = z;
            z.prev = y;
            z.next = v.next;
            z.next.prev = z;
        }

        // When we exit, we know that the point x points to a node in the circular order we loop through until we find
        // the node after taxa zero
        while (x.id != 1) {
            x = x.next;
        }

        // Extract the ordering
        a = x;
        int t = 0;
        do {
            ordering[++t] = a.id;
            a = a.next;
        } while (a != x);

        return ordering;
    }


    /**
     * Represents a NeighborNet node and, by extension, network.  This data structure is implemented as a doubly linked
     * list.
     * <p/>
     * For simplicity and speed we have not encapsulated the members of this class.
     */
    protected static class NetNode {

        protected int id = 0;
        protected NetNode nbr = null; // adjacent node
        protected NetNode ch1 = null; // first child
        protected NetNode ch2 = null; // second child
        protected NetNode next = null; // next in list of active nodes
        protected NetNode prev = null; // prev in list of active nodes
        protected double Rx = 0;
        protected double Sx = 0;

        @Override
        public String toString() {
            String str = "[id=" + id;
            str += " nbr=" + (nbr == null ? "null" : ("" + nbr.id));
            str += " ch1=" + (ch1 == null ? "null" : ("" + ch1.id));
            str += " ch2=" + (ch2 == null ? "null" : ("" + ch2.id));
            str += " prev=" + (prev == null ? "null" : ("" + prev.id));
            str += " next=" + (next == null ? "null" : ("" + next.id));
            str += " Rx=" + Rx;
            str += " Sx=" + Sx;
            str += "]";
            return str;
        }

    }
}

