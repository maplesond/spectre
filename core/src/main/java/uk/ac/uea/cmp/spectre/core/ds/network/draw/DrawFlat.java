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


import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.*;

/**
 * This class provides the algorithm for drawing a network representing a flat split system
 */
public class DrawFlat {
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//Methods for computing a plane drawing of a split
//network representing a flat split system which is
//given by a permutation sequence
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    //******************************************************
    //public methods for computing a drawing of the network
    //******************************************************

    /**
     * This method computes a split graph representing the flat split system given by a permutation sequence.  It returns
     * a vertex of the resulting split graph from which the network can be traversed.  The split graph may contain
     * unlabeled vertices of degree two and trivial splits may be represented by more than one edge in the network. This
     * is the first step of the computation of the final network.  It is provided as a public method for testing
     * purposes.
     * @param pseq Permutation sequence draw object
     * @param taxaname Array of taxa names
     * @param splitedges Array of treesets representing split edges
     * @return Split graph represented by a single vertex.  The network can be traversed from this vertex.
     */
    public static Vertex computeSplitGraph(PermutationSequenceDraw pseq, String[] taxaname, TreeSet[] splitedges) {
        //Compute the leftmost edges in the network.
        //This also initializes the sets of edges
        //associated to each split.
        Edge[] chain = leftmost_edges(pseq, splitedges);

        //Complete the network and return it
        return complete_network(pseq, chain, taxaname, splitedges);
    }

    //This method trims the split network by removing 
    //unlabeled vertices of degree 2. This is the second
    //step in the computation of the resulting split
    //network. The input is a vertex in the network
    //computed in the first step. It return a vertex
    //in the trimmed network. Again this method is
    //provided as a public method for testing purposes.
    public static Vertex trim_network(Vertex v) {
        LinkedList vlist = v.collectVertices();
        ListIterator viter = vlist.listIterator();
        ListIterator eiter = null;
        Vertex u = null;
        Vertex w = v;
        Edge e = null;

        int c = 0;

        while (viter.hasNext()) {
            u = (Vertex) viter.next();
            if ((u.getTaxa().size() > 0) || (u.getElist().size() > 2)) {
                viter.remove();
            }
        }
        //System.out.println(vlist.size() + "unlabeled vertices of degree 2 collected");

        while (vlist.size() > 0) {
            viter = vlist.listIterator();
            while (viter.hasNext()) {
                u = (Vertex) viter.next();
                viter.remove();
                eiter = u.getElist().listIterator();
                while (eiter.hasNext()) {
                    e = (Edge) eiter.next();
                    if (u == e.getBot()) {
                        e.getTop().getElist().remove(e);
                        w = e.getTop();
                        if (((w.getTaxa().size() == 0) && (w.getElist().size() < 3))
                                && (vlist.contains(w) == false)) {
                            viter.add(w);
                        }
                    } else {
                        e.getBot().getElist().remove(e);
                        w = e.getBot();
                        if (((w.getTaxa().size() == 0) && (w.getElist().size() < 3))
                                && (vlist.contains(w) == false)) {
                            viter.add(w);
                        }
                    }
                }
            }
            c++;
            //System.out.println("Round " + c);
        }
        //System.out.println("Unlabeled vertices of degree 2 removed");    

        return w;
    }

    //This method ensures that in the split network
    //every trivial split is represented by a pendant
    //edge. This is the third step of the computation of
    //the resulting split network. The input is again
    //a vertex of the network produced in the second
    //step and it also returns a vertex in the pushed
    //out network. Again this method is made public 
    //for testing purposes.
    public static Vertex push_out_trivial_splits(Vertex v, PermutationSequenceDraw pseq, String[] taxaname) {
        SplitSystemDraw ssyst = new SplitSystemDraw(pseq);
        Vertex u = v;
        Edge e = null;
        int i = 0;

        for (i = 0; i < ssyst.nsplits; i++) {
            //System.out.print("Split " + i + " "); 
            if (pseq.getActive()[i] && is_trivial(pseq, i, ssyst)) {
                //System.out.println("is trivial");
                LinkedList elist = u.collectEdgesForSplit(i);
                //System.out.println(elist.size() + " edges collected");
                e = flip_cubes(v, pseq, elist, ssyst, taxaname);
                //System.out.println("Cubes flipped");
                u = remove_clutter(pseq, e, elist, ssyst);
            } else {
                //System.out.println("is not trivial or not active"); 
            }
        }
        return u;
    }

    //This method opens a nexus file and writes the split network
    //to it. In addition to the network block it also
    //writes a taxa block and a flatsplits block to
    //this file. This is the last step of the
    //computation of the split network. Again this
    //method is only made public for testing purposes.
    /*public static void write_splits_graph_to_file(Vertex v, String filename, int ntaxa, String[] taxaname, PermutationSequenceDraw psequ) {
        //collect vertices and edges of the network
        LinkedList<Vertex> vlist = collect_vertices(v);
        LinkedList<Edge> elist = null;
        if (vlist.size() > 1) {
            elist = collect_edges((Edge) (v.getElist()).getFirst());
        } else {
            elist = new LinkedList();
        }
        int nvert = vlist.size();
        int nedges = elist.size();
        //System.out.println("write " + filename);
        psequ.compressSplitIndices();
        //get the printwriter and write blocks
        PrintWriter pw = NexusIO.openprintwriter(filename);
        NexusIO.writeheader(pw);
        NexusIO.writetaxa(ntaxa, taxaname, pw);
        NexusIO.writenetwork(ntaxa, nvert, nedges, psequ, vlist, elist, pw);
        NexusIO.closeprintwriter(pw);
    } */

    /**
     * This method is the main public method that should be called for computing a plane split network for a flat split
     * system given as a permutation sequence.
     * @param pseq Permuatation sequence draw object
     * @param thr Threshold, below which splits are ignored
     * @return Split graph represented by a single vertex.  The network can be traversed from this vertex.
     */
    public static Vertex drawsplitsystem(PermutationSequenceDraw pseq, double thr) {
        pseq.restoreTrivialWeightsForExternalVertices();

        pseq.removeSplitsSmallerOrEqualThan(thr);
        //Array of sets of edges, one for each split
        TreeSet[] splitedges = new TreeSet[pseq.getNswaps()];

        //start timing
        //long start = System.currentTimeMillis();

        Vertex v = computeSplitGraph(pseq, pseq.getTaxaname(), splitedges);

        v = remove_compatible_boxes(v, pseq, false, splitedges);

        //stop timing      
        //long stop = System.currentTimeMillis();

        //print timing information on screen
        //System.out.println("Total time elapsed: " + (stop - start));

        //store split network


        //lay_opt.highlightExternalEdges(v, pseq);
        return v;
        //write_splits_graph_to_file(v, filename, pseq.ntaxa, pseq.taxaname, pseq);
    }

    //for checking if the resulting network as a minimal
    //number of boxes
    public static void checkdrawsplitsystem(PermutationSequenceDraw pseq) {
        //Array of sets of edges, one for each split
        TreeSet[] splitedges = new TreeSet[pseq.getNswaps()];

        Vertex v = computeSplitGraph(pseq, pseq.getTaxaname(), splitedges);
        v = remove_compatible_boxes(v, pseq, true, splitedges);
    }

    //This method checks for every pair of distinct
    //splits in the split system whether they are compatible.
    //If they are it is checked whether these two splits form
    //a box in the network. If they do we remove it. This
    //ensures that the resulting split network is minimal.
    public static Vertex remove_compatible_boxes(Vertex v, PermutationSequenceDraw psequ, boolean check, TreeSet[] splitedges) {
        //get the split system from the permutation sequence
        SplitSystemDraw ssyst = new SplitSystemDraw(psequ);

        String s = null;
        ;

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
        int pattern = 0;

        //System.out.print("Check active splits:");

        //check all pairs of active splits
        for (i = 0; i < (psequ.getNswaps() - 1); i++) {
            if (psequ.getActive()[i]) {
                //System.out.print("*");

                for (j = i + 1; j < psequ.getNswaps(); j++) {
                    if (psequ.getActive()[j]) {
                        //System.out.print("Check splits " + i + " and " + j + " -- ");
                        pattern = ssyst.is_compatible(i, j);
                        if (pattern > 0) {
                            //rounds++;
                            //System.out.println("compatible");
                            u = remove_box(u, psequ, ssyst, i, j, pattern, splitedges);
                            if (u != null) {
                                //s = filename + rounds + ".nex"; 
                                //write_splits_graph_to_file(u,s,psequ.ntaxa,psequ.taxaname,psequ);                      
                            } else {
                                throw new NullPointerException("Vertex u is null - stop here");
                            }
                        } else {
                            //System.out.println("not compatible");
                            count++;
                        }
                    }
                }
            }
        }
        //System.out.println("");

        //Check if the numbers of boxes and edges in the output makes sense 
        //First compute the number edges in the network
        int nedges = 0;

        for (i = 0; i < splitedges.length; i++) {
            nedges = nedges + splitedges[i].size();
        }

        final int nbActive = psequ.getnActive();

        //System.out.println("Number of active splits: " + nbActive);
        //System.out.println("Number of edges in network: " + nedges);
        //System.out.println("Number of incompatible pairs: " + count);
        //System.out.println("Number of boxes: " + ((nedges - nbActive) / 2));

        if ((check) && ((nedges - nbActive) / 2) != count) {
            throw new IllegalStateException("Numbers do not match!!!!!");
        }

        return u;
    }

    //******************************************************
    //private methods for computing a drawing of the network
    //******************************************************
    //This method checks whether a pair of distinct
    //compatible splits form a box in the network. If they
    //do the box is removed.
    private static Vertex remove_box(Vertex v, PermutationSequenceDraw psequ, SplitSystemDraw ssyst, int a, int b, int pattern, TreeSet[] splitedges) {
        //vertex in the split network
        Vertex u = v;

        //System.out.println("Remove box for splits " + a + " and " + b);

        //check if a and b form a box in the network
        NetworkBox netbox = form_box(a, b, splitedges);
        if (netbox != null) {
            //System.out.println("Remove box");
            u = remove_box_by_flipping(psequ, ssyst, a, b, pattern, netbox, splitedges);
        } else {
            //System.out.println("No box found");
        }

        if (u == null) {
            System.out.println("Got a null vertex back -- stop here.");
            System.exit(0);
            return null;
        } else {
            return u;
        }
    }

    //This method checks if two splits form a box in the network
    public static NetworkBox form_box(int a, int b, TreeSet[] splitedges) {
        NetworkBox netbox = null;

        Edge e1 = null;
        Edge e2 = null;
        Edge f1 = null;
        Edge f2 = null;

        Iterator iter = splitedges[a].iterator();
        Edge h = (Edge) splitedges[a].last();

        while (iter.hasNext()) {
            e1 = (Edge) iter.next();
            if (e1 != h) {
                f1 = (Edge) e1.getTop().getElist().get((e1.getTop().getElist().indexOf(e1) + e1.getTop().getElist().size() - 1) % e1.getTop().getElist().size());
                f2 = (Edge) e1.getBot().getElist().get((e1.getBot().getElist().indexOf(e1) + 1) % e1.getBot().getElist().size());

                if (f1.getIdxsplit() == b) {
                    netbox = new NetworkBox(e1, (Edge) iter.next(), f1, f2);
                    break;
                }
            }
        }
        return netbox;
    }

    //This method removes the whole unneccessary part of the
    //network indentified by two compatible splits from the
    //network
    private static Vertex remove_box_by_flipping(PermutationSequenceDraw psequ, SplitSystemDraw ssyst, int a, int b, int pattern, NetworkBox netbox, TreeSet[] splitedges) {
        Vertex u = null;

        //list of relevant edges that correspond to split a
        LinkedList elista = new LinkedList();
        //list of relevant edges that correspond to split b
        LinkedList elistb = new LinkedList();
        //list of relevant edges of splits that cross a
        LinkedList crosslista = new LinkedList();
        //list of relevant edges of splits that cross b
        LinkedList crosslistb = new LinkedList();
        //List of relevant edges of splits that cross a and b
        LinkedList crossboth = new LinkedList();
        //Temporary list used when clearing a triangle
        LinkedList cleanlist = new LinkedList();

        //direction for splits a and b
        //-1:left,1:right
        int dira = 0;
        int dirb = 0;
        int parta = 0;
        int partb = 0;

        int s = 0;

        //System.out.print("Collect edges -- ");

        //determine direction in which we collect edges
        if (netbox.f1.getTimestp() < netbox.f2.getTimestp()) {
            if (pattern == 1) {
                dira = -1;
                dirb = 1;
                parta = 1;
                partb = 1;
            } else if (pattern == 2) {
                dira = 1;
                dirb = 1;
                parta = 1;
                partb = 0;
            } else if (pattern == 3) {
                dira = -1;
                dirb = -1;
                parta = 0;
                partb = 1;
            } else if (pattern == 4) {
                dira = 1;
                dirb = -1;
                parta = 0;
                partb = 0;
            }
        } else {
            if (pattern == 1) {
                dira = 1;
                dirb = -1;
                parta = 1;
                partb = 1;
            } else if (pattern == 2) {
                dira = -1;
                dirb = -1;
                parta = 1;
                partb = 0;
            } else if (pattern == 3) {
                dira = 1;
                dirb = 1;
                parta = 0;
                partb = 1;
            } else if (pattern == 4) {
                dira = -1;
                dirb = 1;
                parta = 0;
                partb = 0;
            }
        }

        //now collect edges
        collect_edges_from_box(netbox.e1, netbox.e2, a, dira, elista, crosslista, splitedges, parta);
        collect_edges_from_box(netbox.f1, netbox.f2, b, dirb, elistb, crosslistb, splitedges, partb);

        //System.out.println("done");

        //find splits that cross a and b
        find_cross_both(crosslista, crosslistb, crossboth, b);

        //System.out.println("Search for triangles");

        while (crossboth.size() > 1) {
            //Need to get rid of the splits that cross a and b
            //First find a triangle
            //System.out.println("Find triangle+++++++++++++++++++++++++++++++++++++++++++++++++");
            s = find_triangle(a, dira, parta, crossboth, cleanlist, splitedges);
            //System.out.println("Remove triangle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            get_rid_of_triangle(a, b, s, dira, dirb, parta, partb, crossboth, cleanlist, elista, elistb, splitedges, psequ);
        }

        //now simply cut off the unnecessary part of the network
        if (crossboth.size() <= 1) {
            //System.out.println("Cut off quadrant");
            u = cut_off_unnecessary_part(a, b, elista, elistb, dira, dirb, parta, partb, splitedges, psequ);
        }

        return u;
    }

    //Collect the edges that are relevant for clearing the quadrant.
    private static void collect_edges_from_box(Edge h1, Edge h2, int s, int dirs, LinkedList<Edge> elists,
                                               LinkedList<Edge> crosslists, TreeSet[] splitedges, int parts) {
        Iterator iter = splitedges[s].iterator();

        int stopstp = 0;

        Edge g = null;

        if (h1.getTimestp() < h2.getTimestp()) {
            if (dirs == -1) {
                stopstp = h2.getTimestp();
            } else {
                stopstp = h1.getTimestp();
            }
        } else {
            if (dirs == -1) {
                stopstp = h1.getTimestp();
            } else {
                stopstp = h2.getTimestp();
            }
        }

        if (dirs == -1) {
            while (iter.hasNext()) {
                g = (Edge) iter.next();
                if (g.getTimestp() < stopstp) {
                    elists.addFirst(g);
                    if (parts == 1) {
                        crosslists.addFirst(g.getTop().getElist().get((g.getTop().getElist().indexOf(g) + g.getTop().getElist().size() - 1) % g.getTop().getElist().size()));
                    } else {
                        crosslists.addFirst(g.getBot().getElist().get((g.getBot().getElist().indexOf(g) + 1) % g.getBot().getElist().size()));
                    }
                } else {
                    break;
                }
            }
        } else {
            while (iter.hasNext()) {
                g = (Edge) iter.next();
                if (g.getTimestp() > stopstp) {
                    elists.addLast(g);
                    if (parts == 1) {
                        crosslists.addLast((Edge) g.getTop().getElist().get((g.getTop().getElist().indexOf(g) + 1) % g.getTop().getElist().size()));
                    } else {
                        crosslists.addLast((Edge) g.getBot().getElist().get((g.getBot().getElist().indexOf(g) + g.getBot().getElist().size() - 1) % g.getBot().getElist().size()));
                    }
                }
            }
        }
    }

    //Extract the edges from crosslista that correspond to
    //splits that also cross b
    private static void find_cross_both(LinkedList<Edge> crosslista, LinkedList<Edge> crosslistb, LinkedList<Edge> crossboth, int b) {
        ListIterator itera = crosslista.listIterator();
        ListIterator iterb = null;

        Edge e = null;
        Edge f = null;

        crossboth.clear();

        //System.out.println("Find splits that cross both");

        while (itera.hasNext()) {
            e = (Edge) itera.next();
            //System.out.println(e);

            if (e.getIdxsplit() == b) {
                crossboth.addLast(e);
            } else {
                iterb = crosslistb.listIterator();

                while (iterb.hasNext()) {
                    f = (Edge) iterb.next();
                    if (e.getIdxsplit() == f.getIdxsplit()) {
                        crossboth.addLast(e);
                        break;
                    } else {
                    }
                }
            }
        }
    }

    //This method cuts off the unecessary part of the network
    private static Vertex cut_off_unnecessary_part(int a, int b, LinkedList<Edge> elista, LinkedList<Edge> elistb,
                                                   int dira, int dirb, int parta, int partb, TreeSet[] splitedges, PermutationSequenceDraw psequ) {
        //System.out.println("cut off unnecessary part");

        Vertex u = null;

        Edge e = (Edge) elista.getFirst();
        Edge g = null;
        Edge h = null;
        Edge g1 = null;
        Edge g2 = null;

        //first eliminate the edges that correspond to splits that cross a
        if (dira == -1) {
            SortedSet head = splitedges[e.getIdxsplit()].headSet(e);
            Iterator headiter = head.iterator();
            while (headiter.hasNext()) {
                h = (Edge) headiter.next();
                g1 = h.getTop().getElist().get((h.getTop().getElist().indexOf(h) + (h.getTop().getElist().size() - 1)) % h.getTop().getElist().size());
                g2 = h.getBot().getElist().get((h.getBot().getElist().indexOf(h) + 1) % h.getBot().getElist().size());

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
                g1 = h.getTop().getElist().get((h.getTop().getElist().indexOf(h) + 1) % h.getTop().getElist().size());
                g2 = h.getBot().getElist().get((h.getBot().getElist().indexOf(h) + (h.getBot().getElist().size() - 1)) % h.getBot().getElist().size());

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
        if (dirb == -1) {
            SortedSet head = splitedges[f.getIdxsplit()].headSet(f);
            Iterator headiter = head.iterator();
            while (headiter.hasNext()) {
                h = (Edge) headiter.next();
                g1 = h.getTop().getElist().get((h.getTop().getElist().indexOf(h) + (h.getTop().getElist().size() - 1)) % h.getTop().getElist().size());
                g2 = h.getBot().getElist().get((h.getBot().getElist().indexOf(h) + 1) % h.getBot().getElist().size());

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
                g1 = h.getTop().getElist().get((h.getTop().getElist().indexOf(h) + 1) % h.getTop().getElist().size());
                g2 = h.getBot().getElist().get((h.getBot().getElist().indexOf(h) + (h.getBot().getElist().size() - 1)) % h.getBot().getElist().size());

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
        if (dira == -1) {
            while (true) {
                g = (Edge) splitedges[a].first();
                if (g.getTimestp() <= e.getTimestp()) {
                    splitedges[a].remove(g);
                    if (parta == 1) {
                        g.getTop().getElist().remove(g);
                        u = g.getTop();
                    } else {
                        g.getBot().getElist().remove(g);
                        u = g.getBot();
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
                        g.getTop().getElist().remove(g);
                        u = g.getTop();
                    } else {
                        g.getBot().getElist().remove(g);
                        u = g.getBot();
                    }
                } else {
                    break;
                }
            }
        }

        if (dirb == -1) {
            while (true) {
                g = (Edge) splitedges[b].first();
                if (g.getTimestp() <= f.getTimestp()) {
                    splitedges[b].remove(g);
                    if (partb == 1) {
                        g.getTop().getElist().remove(g);
                    } else {
                        g.getBot().getElist().remove(g);
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
                        g.getTop().getElist().remove(g);
                    } else {
                        g.getBot().getElist().remove(g);
                    }
                    splitedges[b].remove(g);
                } else {
                    break;
                }
            }
        }

        return u;
    }

    //This method locates a triangle and also collects the
    //the cleanlist containing those edges that can be used
    //to clear the triangle if necessary
    private static int find_triangle(int a, int dira, int parta, LinkedList<Edge> crossboth, LinkedList<Edge> cleanlist, TreeSet[] splitedges) {
        cleanlist.clear();

        TreeSet crossindices = new TreeSet();
        get_cross_indices(crossboth, crossindices);

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

    //This method walks through the network to the
    //first scrossing with a split in crossindices
    private static int go_to_first_crossing(Edge e, int a, TreeSet crossindices, LinkedList<Edge> liste, TreeSet[] splitedges) {
        int dire = 0;
        int sidx = 0;

        //first check in which direction we need to go
        SortedSet testtail = splitedges[e.getIdxsplit()].tailSet(e);

        Edge f = null;

        if (testtail.size() == 1) {
            dire = -1;
        } else if (a == (e.getBot().getElist().get((e.getBot().getElist().indexOf(e) + 1) % e.getBot().getElist().size())).getIdxsplit()) {
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
                sidx = (f.getTop().getElist().get((f.getTop().getElist().indexOf(f) + 1) % f.getTop().getElist().size())).getIdxsplit();
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
                sidx = ((Edge) f.getBot().getElist().get((f.getBot().getElist().indexOf(f) + 1) % f.getBot().getElist().size())).getIdxsplit();
                if (crossindices.contains(new Integer(sidx))) {
                    return sidx;
                } else {
                    liste.addLast(f);
                }
            }
        }

        System.out.println("Did not find a first crossing -- something is wrong -- stop here");
        System.exit(0);
        return -1;
    }

    //This method collects the indices of the splits
    //corresponding to the edges in crossboth
    private static void get_cross_indices(LinkedList<Edge> crossboth, TreeSet crossindices) {
        ListIterator crossiter = crossboth.listIterator();
        Edge e = null;
        Integer index = null;

        while (crossiter.hasNext()) {
            e = (Edge) crossiter.next();
            index = new Integer(e.getIdxsplit());
            crossindices.add(index);
        }
    }

    //This method removes one triangle from the empty
    //quadrant of two compatible splits
    private static void get_rid_of_triangle(int a, int b, int s, int dira, int dirb, int parta, int partb,
                                            LinkedList<Edge> crossboth, LinkedList<Edge> cleanlist, LinkedList<Edge> elista,
                                            LinkedList<Edge> elistb, TreeSet[] splitedges, PermutationSequenceDraw psequ) {
        Edge e = cleanlist.getFirst();

        int flipdir = 0;
        int dire = 0;

        //first check in which direction we need to go
        SortedSet testtail = splitedges[e.getIdxsplit()].tailSet(e);

        Edge f = null;

        if (testtail.size() == 1) {
            dire = 1;
        } else if (a == (e.getBot().getElist().get((e.getBot().getElist().indexOf(e) + 1) % e.getBot().getElist().size())).getIdxsplit()) {
            dire = -1;
        } else {
            dire = 1;
        }


        if (parta == 1) {
            if (dira == -1) {
                if (dire == -1) {
                    flipdir = 1;
                } else {
                    flipdir = 0;
                }
            } else {
                if (dire == -1) {
                    flipdir = 0;
                } else {
                    flipdir = 1;
                }
            }
        } else {
            if (dira == -1) {
                if (dire == -1) {
                    flipdir = 0;
                } else {
                    flipdir = 1;
                }
            } else {
                if (dire == -1) {
                    flipdir = 1;
                } else {
                    flipdir = 0;
                }
            }
        }

        while (!cleanlist.isEmpty()) {
            find_flippable_cube_in_triangle(cleanlist, crossboth, flipdir, a, b, s, parta, dira, elista, elistb, splitedges, psequ);
        }
    }

    //This method locates and flips a cube during the removal
    //of a triangle
    private static void find_flippable_cube_in_triangle(LinkedList<Edge> elist, LinkedList<Edge> crossboth,
                                                        int flipdir, int a, int b, int s, int parta, int dira,
                                                        LinkedList<Edge> elista, LinkedList<Edge> elistb,
                                                        TreeSet[] splitedges, PermutationSequenceDraw psequ) {
        if (elist.isEmpty()) {
            System.out.println("List of flip edges is empty -- error -- stop here");
            System.exit(0);
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

        if (flipdir == 0) {
            //flip cubes upwards

            iter = elist.listIterator();

            //find a flippable cube
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                v = e.getTop();
                if (v.getElist().size() == 3) {
                    e1 = v.getElist().get((v.getElist().indexOf(e) + 2) % 3);
                    e2 = v.getElist().get((v.getElist().indexOf(e) + 1) % 3);
                    v5 = e.getBot();
                    if (v == e2.getBot()) {
                        v1 = e2.getTop();
                    } else {
                        v1 = e2.getBot();
                    }
                    if (v == e1.getBot()) {
                        v3 = e1.getTop();
                    } else {
                        v3 = e1.getBot();
                    }
                    e3 = v3.getElist().get((v3.getElist().indexOf(e1) + 1) % v3.getElist().size());
                    e4 = v1.getElist().get((v1.getElist().indexOf(e2) + v1.getElist().size() - 1) % v1.getElist().size());
                    e5 = v1.getElist().get((v1.getElist().indexOf(e2) + 1) % v1.getElist().size());
                    e6 = v5.getElist().get((v5.getElist().indexOf(e) + v5.getElist().size() - 1) % v5.getElist().size());
                    e7 = v5.getElist().get((v5.getElist().indexOf(e) + 1) % v5.getElist().size());
                    e8 = v3.getElist().get((v3.getElist().indexOf(e1) + v3.getElist().size() - 1) % v3.getElist().size());
                    if (v1 == e4.getBot()) {
                        v2 = e4.getTop();
                    } else {
                        v2 = e4.getBot();
                    }
                    if (v3 == e3.getBot()) {
                        v4 = e3.getTop();
                    } else {
                        v4 = e3.getBot();
                    }
                    if (v3 == e8.getBot()) {
                        v6 = e8.getTop();
                    } else {
                        v6 = e8.getBot();
                    }
                    if (v5 == e7.getBot()) {
                        v7 = e7.getTop();
                    } else {
                        v7 = e7.getBot();
                    }
                    if (v5 == e6.getBot()) {
                        v8 = e6.getTop();
                    } else {
                        v8 = e6.getBot();
                    }
                    if (v1 == e5.getBot()) {
                        v9 = e5.getTop();
                    } else {
                        v9 = e5.getBot();
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
            v1.getElist().remove(e2);
            v3.getElist().remove(e1);
            v5.getElist().remove(e);
            v.getElist().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h1 = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h1 = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getElist().add(v6.getElist().indexOf(e7) + 1, h1);
            v.getElist().addLast(h1);
            if (v6 == e7.getTop()) {
                h2 = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h2 = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getElist().add(v8.getElist().indexOf(e5) + 1, h2);
            v.getElist().addLast(h2);
            h3 = new Edge(v2, v, e.getIdxsplit(), e.getTimestp());
            v2.getElist().add(v2.getElist().indexOf(e3) + 1, h3);
            v.getElist().addLast(h3);

            //System.out.println("Size of elist before update: " + elist.size());

            //update lists of edges accordingly
            splitedges[e.getIdxsplit()].remove(e);
            splitedges[e.getIdxsplit()].add(h3);
            splitedges[e2.getIdxsplit()].remove(e2);
            splitedges[e2.getIdxsplit()].add(h1);
            splitedges[e1.getIdxsplit()].remove(e1);
            splitedges[e1.getIdxsplit()].add(h2);

            if (parta == 1) {
                if (dira == -1) {
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
                if (dira == -1) {
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
                v = e.getBot();
                if (v.getElist().size() == 3) {
                    e1 = (Edge) v.getElist().get((v.getElist().indexOf(e) + 2) % 3);
                    e2 = (Edge) v.getElist().get((v.getElist().indexOf(e) + 1) % 3);
                    v5 = e.getTop();
                    if (v == e2.getBot()) {
                        v1 = e2.getTop();
                    } else {
                        v1 = e2.getBot();
                    }
                    if (v == e1.getBot()) {
                        v3 = e1.getTop();
                    } else {
                        v3 = e1.getBot();
                    }
                    e3 = v3.getElist().get((v3.getElist().indexOf(e1) + 1) % v3.getElist().size());
                    e4 = v1.getElist().get((v1.getElist().indexOf(e2) + v1.getElist().size() - 1) % v1.getElist().size());
                    e5 = v1.getElist().get((v1.getElist().indexOf(e2) + 1) % v1.getElist().size());
                    e6 = v5.getElist().get((v5.getElist().indexOf(e) + v5.getElist().size() - 1) % v5.getElist().size());
                    e7 = v5.getElist().get((v5.getElist().indexOf(e) + 1) % v5.getElist().size());
                    e8 = v3.getElist().get((v3.getElist().indexOf(e1) + v3.getElist().size() - 1) % v3.getElist().size());
                    if (v1 == e4.getBot()) {
                        v2 = e4.getTop();
                    } else {
                        v2 = e4.getBot();
                    }
                    if (v3 == e3.getBot()) {
                        v4 = e3.getTop();
                    } else {
                        v4 = e3.getBot();
                    }
                    if (v3 == e8.getBot()) {
                        v6 = e8.getTop();
                    } else {
                        v6 = e8.getBot();
                    }
                    if (v5 == e7.getBot()) {
                        v7 = e7.getTop();
                    } else {
                        v7 = e7.getBot();
                    }
                    if (v5 == e6.getBot()) {
                        v8 = e6.getTop();
                    } else {
                        v8 = e6.getBot();
                    }
                    if (v1 == e5.getBot()) {
                        v9 = e5.getTop();
                    } else {
                        v9 = e5.getBot();
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
            v1.getElist().remove(e2);
            v3.getElist().remove(e1);
            v5.getElist().remove(e);
            v.getElist().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h1 = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h1 = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getElist().add(v6.getElist().indexOf(e7) + 1, h1);
            v.getElist().addLast(h1);
            if (v6 == e7.getTop()) {
                h2 = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h2 = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getElist().add(v8.getElist().indexOf(e5) + 1, h2);
            v.getElist().addLast(h2);
            h3 = new Edge(v, v2, e.getIdxsplit(), e.getTimestp());
            v2.getElist().add(v2.getElist().indexOf(e3) + 1, h3);
            v.getElist().addLast(h3);

            //System.out.println("Size of elist before update: " + elist.size());

            //update lists of edges accordingly
            splitedges[e.getIdxsplit()].remove(e);
            splitedges[e.getIdxsplit()].add(h3);
            splitedges[e2.getIdxsplit()].remove(e2);
            splitedges[e2.getIdxsplit()].add(h1);
            splitedges[e1.getIdxsplit()].remove(e1);
            splitedges[e1.getIdxsplit()].add(h2);

            if (parta == 1) {
                if (dira == -1) {
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
                if (dira == -1) {
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

    //This method computes the chain of edges
    //that form the left boundary of the resulting
    //split network before trimming away unlabeled 
    //degree two vertices and pushing out trivial splits.
    private static Edge[] leftmost_edges(PermutationSequenceDraw pseq, TreeSet<Edge>[] splitedges) {
        int i = 0;
        int j = 0;
        double dx = 0.0;
        double dy = 0.0;
        double xcoord = 0.0;
        double ycoord = 0.0;

        Vertex u = null;
        Vertex v = null;
        Edge e = null;

        Edge[] chain = new Edge[pseq.getnActive()];

        u = new Vertex(xcoord, ycoord);

        for (i = 0; i < pseq.getNswaps(); i++) {
            //create new set for edges associated to this split
            splitedges[i] = new TreeSet<>();

            if (pseq.getActive()[i]) {
                dx = -Math.cos(((j + 1) * Math.PI) / (pseq.getnActive() + 1));
                dy = -Math.sin(((j + 1) * Math.PI) / (pseq.getnActive() + 1));

                xcoord = xcoord + (pseq.getWeights()[i] * dx);
                ycoord = ycoord + (pseq.getWeights()[i] * dy);

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

    //This method extends the initial chain computed
    //by the method leftmost_edges() to a split network 
    //for the split system.
    private static Vertex complete_network(PermutationSequenceDraw pseq, Edge[] chain, String[] taxaname, TreeSet[] splitedges) {
        SplitSystemDraw ssyst = new SplitSystemDraw(pseq);

        int i = 0;
        int j = 0;

        boolean inverted = true;
        double dx = 0.0;
        double dy = 0.0;

        Vertex v = null;
        Edge e1 = null;
        Edge e2 = null;

        for (i = 0; i < pseq.getNtaxa(); i++) {
            inverted = true;
            //System.out.println("Taxon " + pseq.initSequ[i]);

            final int initSequI = pseq.getInitSequ()[i];


            while (inverted) {
                inverted = false;

                for (j = 1; j < chain.length; j++) {
                    //test if the splits associated to edges
                    //chain[j-1] and chain[j] must be inverted

                    if (ssyst.splits[chain[j - 1].getIdxsplit()][initSequI] == 0
                            && ssyst.splits[chain[j].getIdxsplit()][initSequI] == 1) {
                        dx = (chain[j].getBot()).getX() - (chain[j].getTop()).getX();
                        dy = (chain[j].getBot()).getY() - (chain[j].getTop()).getY();

                        v = new Vertex((chain[j - 1].getTop()).getX() + dx, (chain[j - 1].getTop()).getY() + dy);
                        e1 = new Edge(chain[j - 1].getTop(), v, chain[j].getIdxsplit(), chain[j].getTimestp() + 1);
                        splitedges[e1.getIdxsplit()].add(e1);
                        e2 = new Edge(v, chain[j].getBot(), chain[j - 1].getIdxsplit(), chain[j - 1].getTimestp() + 1);
                        splitedges[e2.getIdxsplit()].add(e2);
                        chain[j - 1] = e1;
                        chain[j] = e2;
                        v.add_edge_before_first(e2);
                        v.add_edge_after_last(e1);
                        (e1.getTop()).add_edge_before_first(e1);
                        (e2.getBot()).add_edge_after_last(e2);
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
                pseq.setRepresentedByAt(initSequI, 0);
                if (initSequI != 0) {
                    pseq.setActiveTaxaAt(initSequI, false);
                }
                pseq.setNClasses(1);
            } else {
                v = chain[0].getTop();
                for (j = 0; j < chain.length; j++) {
                    if (ssyst.splits[chain[j].getIdxsplit()][initSequI] == 0) {
                        (chain[j].getTop()).getTaxa().add(new Identifier(taxaname[initSequI], initSequI));
                        if (chain[j].getTop().getTaxa().size() > 1) {
                            pseq.setRepresentedByAt(initSequI, chain[j].getTop().getTaxa().getFirst().getId());
                            pseq.setActiveTaxaAt(initSequI, false);
                            pseq.decrementNClasses();
                        }
                        break;
                    } else {
                        if (j == (chain.length - 1)) {
                            (chain[j].getBot()).getTaxa().add(new Identifier(taxaname[initSequI], initSequI));
                            if (chain[j].getBot().getTaxa().size() > 1) {
                                pseq.setRepresentedByAt(initSequI, chain[j].getBot().getTaxa().getFirst().getId());
                                pseq.setActiveTaxaAt(initSequI, false);
                                pseq.decrementNClasses();
                            }
                        }
                    }
                }
            }
        }
        return v;
    }

    //This method checks whether or not a split
    //in the given split system is trivial.
    //The split is given by its index in the 
    //2-dimensional 0-1-array representing the
    //split system.
    private static boolean is_trivial(PermutationSequenceDraw pseq, int s, SplitSystemDraw ssyst) {
        int i = 0;
        int c = 0;

        for (i = 0; i < ssyst.ntaxa; i++) {
            if (pseq.getActiveTaxa()[i] == true) {
                c = c + ssyst.splits[s][i];
            }
        }

        if ((c == 1) || (c == (pseq.getNclasses() - 1))) {
            return true;
        } else {
            return false;
        }
    }



    //This method collects the edges that represent a
    //given split in the network.
    public static LinkedList<Edge> collect_edges_for_split(int s, List<Edge> elistall) {
        ListIterator iter = elistall.listIterator();
        LinkedList elist = new LinkedList();
        Edge e = null;
        while (iter.hasNext()) {
            e = (Edge) iter.next();
            if (e != null) {
                if (e.getIdxsplit() == s) {
                    elist.add(e);
                }
            }
        }
        return elist;
    }

    //This method returns the index of the taxon x
    //that forms a trivial split S = x|X-x
    private static int find_taxon_index(PermutationSequenceDraw pseq, int s, SplitSystemDraw ssyst) {
        int i = 0;
        int c = 0;
        int idx = 0;

        for (i = 0; i < ssyst.ntaxa; i++) {
            if (pseq.getActiveTaxaAt(i)) {
                c = c + ssyst.splits[s][i];
            }
        }

        if (c == 1) {
            for (i = 0; i < ssyst.ntaxa; i++) {
                if (pseq.getActiveTaxaAt(i) && (ssyst.splits[s][i] == 1)) {
                    idx = i;
                    break;
                }
            }
        } else {
            for (i = 0; i < ssyst.ntaxa; i++) {
                if (pseq.getActiveTaxaAt(i) && (ssyst.splits[s][i] == 0)) {
                    idx = i;
                    break;
                }
            }
        }
        return idx;
    }

    //This method is used when flipping cubes to
    //push out the trivial splits.
    //This method checks whether the taxon labels
    //an endpoint of one of the edges in elist.
    //If we know which edge has changed, we pass
    //it to the method. Then the test can be performed
    //more efficiently.
    private static Edge taxon_not_reached(int taxon, Edge e, LinkedList<Edge> elist, SplitSystemDraw ssyst) {
        ListIterator iter = null;
        Edge h = null;

        if (ssyst.splits[((Edge) elist.getFirst()).getIdxsplit()][taxon] == 0) {
            if (e == null) {
                iter = elist.listIterator();
                while (iter.hasNext()) {
                    h = (Edge) iter.next();
                    if (h.getTop().getTaxa().contains(new Integer(taxon))) {
                        break;
                    } else {
                        h = null;
                    }
                }
            } else {
                if (e.getTop().getTaxa().contains(new Integer(taxon))) {
                    h = e;
                }
            }
        } else {
            if (e == null) {
                iter = elist.listIterator();
                while (iter.hasNext()) {
                    h = (Edge) iter.next();
                    if (h.getBot().getTaxa().contains(new Integer(taxon))) {
                        break;
                    } else {
                        h = null;
                    }
                }
            } else {
                if (e.getBot().getTaxa().contains(new Integer(taxon))) {
                    h = e;
                }
            }
        }
        return h;
    }

    //This method locates and flips a cube during the pushing
    //out of trivial splits.
    private static Edge find_flippable_cube(int taxon, LinkedList<Edge> elist, SplitSystemDraw ssyst) {
        Edge e = null;
        Edge h = null;
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

        if (ssyst.splits[((Edge) elist.getFirst()).getIdxsplit()][taxon] == 0) {
            iter = elist.listIterator();
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                v = e.getTop();
                if (v.getElist().size() == 3) {
                    e1 = v.getElist().get((v.getElist().indexOf(e) + 2) % 3);
                    e2 = v.getElist().get((v.getElist().indexOf(e) + 1) % 3);
                    v5 = e.getBot();
                    if (v == e2.getBot()) {
                        v1 = e2.getTop();
                    } else {
                        v1 = e2.getBot();
                    }
                    if (v == e1.getBot()) {
                        v3 = e1.getTop();
                    } else {
                        v3 = e1.getBot();
                    }
                    e3 = v3.getElist().get((v3.getElist().indexOf(e1) + 1) % v3.getElist().size());
                    e4 = v1.getElist().get((v1.getElist().indexOf(e2) + v1.getElist().size() - 1) % v1.getElist().size());
                    e5 = v1.getElist().get((v1.getElist().indexOf(e2) + 1) % v1.getElist().size());
                    e6 = v5.getElist().get((v5.getElist().indexOf(e) + v5.getElist().size() - 1) % v5.getElist().size());
                    e7 = v5.getElist().get((v5.getElist().indexOf(e) + 1) % v5.getElist().size());
                    e8 = v3.getElist().get((v3.getElist().indexOf(e1) + v3.getElist().size() - 1) % v3.getElist().size());
                    if (v1 == e4.getBot()) {
                        v2 = e4.getTop();
                    } else {
                        v2 = e4.getBot();
                    }
                    if (v3 == e3.getBot()) {
                        v4 = e3.getTop();
                    } else {
                        v4 = e3.getBot();
                    }
                    if (v3 == e8.getBot()) {
                        v6 = e8.getTop();
                    } else {
                        v6 = e8.getBot();
                    }
                    if (v5 == e7.getBot()) {
                        v7 = e7.getTop();
                    } else {
                        v7 = e7.getBot();
                    }
                    if (v5 == e6.getBot()) {
                        v8 = e6.getTop();
                    } else {
                        v8 = e6.getBot();
                    }
                    if (v1 == e5.getBot()) {
                        v9 = e5.getTop();
                    } else {
                        v9 = e5.getBot();
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
            v1.getElist().remove(e2);
            v3.getElist().remove(e1);
            v5.getElist().remove(e);
            v.getElist().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getElist().add(v6.getElist().indexOf(e7) + 1, h);
            v.getElist().addLast(h);
            if (v6 == e7.getTop()) {
                h = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getElist().add(v8.getElist().indexOf(e5) + 1, h);
            v.getElist().addLast(h);
            h = new Edge(v2, v, e.getIdxsplit(), e.getTimestp());
            v2.getElist().add(v2.getElist().indexOf(e3) + 1, h);
            v.getElist().addLast(h);
        } else {
            iter = elist.listIterator();
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                v = e.getBot();
                if (v.getElist().size() == 3) {
                    e1 = (Edge) v.getElist().get((v.getElist().indexOf(e) + 2) % 3);
                    e2 = (Edge) v.getElist().get((v.getElist().indexOf(e) + 1) % 3);
                    v5 = e.getTop();
                    if (v == e2.getBot()) {
                        v1 = e2.getTop();
                    } else {
                        v1 = e2.getBot();
                    }
                    if (v == e1.getBot()) {
                        v3 = e1.getTop();
                    } else {
                        v3 = e1.getBot();
                    }
                    e3 = v3.getElist().get((v3.getElist().indexOf(e1) + 1) % v3.getElist().size());
                    e4 = v1.getElist().get((v1.getElist().indexOf(e2) + v1.getElist().size() - 1) % v1.getElist().size());
                    e5 = v1.getElist().get((v1.getElist().indexOf(e2) + 1) % v1.getElist().size());
                    e6 = v5.getElist().get((v5.getElist().indexOf(e) + v5.getElist().size() - 1) % v5.getElist().size());
                    e7 = v5.getElist().get((v5.getElist().indexOf(e) + 1) % v5.getElist().size());
                    e8 = v3.getElist().get((v3.getElist().indexOf(e1) + v3.getElist().size() - 1) % v3.getElist().size());
                    if (v1 == e4.getBot()) {
                        v2 = e4.getTop();
                    } else {
                        v2 = e4.getBot();
                    }
                    if (v3 == e3.getBot()) {
                        v4 = e3.getTop();
                    } else {
                        v4 = e3.getBot();
                    }
                    if (v3 == e8.getBot()) {
                        v6 = e8.getTop();
                    } else {
                        v6 = e8.getBot();
                    }
                    if (v5 == e7.getBot()) {
                        v7 = e7.getTop();
                    } else {
                        v7 = e7.getBot();
                    }
                    if (v5 == e6.getBot()) {
                        v8 = e6.getTop();
                    } else {
                        v8 = e6.getBot();
                    }
                    if (v1 == e5.getBot()) {
                        v9 = e5.getTop();
                    } else {
                        v9 = e5.getBot();
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
            v1.getElist().remove(e2);
            v3.getElist().remove(e1);
            v5.getElist().remove(e);
            v.getElist().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getElist().add(v6.getElist().indexOf(e7) + 1, h);
            v.getElist().addLast(h);
            if (v6 == e7.getTop()) {
                h = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getElist().add(v8.getElist().indexOf(e5) + 1, h);
            v.getElist().addLast(h);
            h = new Edge(v, v2, e.getIdxsplit(), e.getTimestp());
            v2.getElist().add(v2.getElist().indexOf(e3) + 1, h);
            v.getElist().addLast(h);
        }

        elist.remove(e);
        elist.add(h);
        return h;
    }

    //This method pushes out a trivial split S=x|X-x
    //by flipping cubes until the vertex labeled by x
    //is an endpoint one of the edges representing S.
    //It returns this edge.
    private static Edge flip_cubes(Vertex v, PermutationSequenceDraw pseq, LinkedList<Edge> elist, SplitSystemDraw ssyst, String[] taxaname) {
        Edge e = null;
        ListIterator iter = null;
        int taxon = find_taxon_index(pseq, ((Edge) elist.getFirst()).getIdxsplit(), ssyst);
        int i = 0;
        int k = 0;
        int a = 0;

        //System.out.println("Taxon " + taxon + " gefunden");

        e = taxon_not_reached(taxon, e, elist, ssyst);

        //System.out.println("Must first flip some cubes.");

        while (e == null) {
            e = find_flippable_cube(taxon, elist, ssyst);

            //System.out.println("Cube flipped");
            //if(i<6)
            //{
            //   write_splits_graph_to_file(v,"pushedout"+i+".nex",pseq.ntaxa,taxaname);
            //   i++;
            //}

            e = taxon_not_reached(taxon, e, elist, ssyst);
        }

        return e;
    }

    //This method removes that part of the split graph
    //that is not needed to represent the trivial split x|X-x.
    //It returns the vertex labeled by x.
    private static Vertex remove_clutter(PermutationSequenceDraw pseq, Edge e, LinkedList<Edge> elist, SplitSystemDraw ssyst) {
        Vertex v = null;
        Edge h = null;
        ListIterator iter = null;
        int taxon = find_taxon_index(pseq, e.getIdxsplit(), ssyst);

        if (ssyst.splits[((Edge) elist.getFirst()).getIdxsplit()][taxon] == 0) {
            v = e.getTop();
            iter = elist.listIterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (e != h) {
                    h.getBot().getElist().remove(h);
                }
            }
            v.getElist().clear();
            v.getElist().addFirst(e);
        } else {
            v = e.getBot();
            iter = elist.listIterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (e != h) {
                    h.getTop().getElist().remove(h);
                }
            }
            v.getElist().clear();
            v.getElist().addFirst(e);
        }

        return v;
    }


    private static void log_splitedges(TreeSet[] splitedges, String mes) {
        int i = 0;
        Edge f = null;

        System.out.println(mes + "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        for (i = 0; i < splitedges.length; i++) {
            Iterator iter = splitedges[i].iterator();

            System.out.print("Split " + i + ":");
            while (iter.hasNext()) {
                f = (Edge) iter.next();
                System.out.print(" " + f.getTimestp());
            }
            System.out.println("");
        }

        /*
         * System.out.println("split indices");
         *
         * for(i=0;i<splitedges.length;i++) { Iterator iter =
         * splitedges[i].iterator();
         *
         * System.out.print("Split " + i + ":"); while(iter.hasNext()) { f =
         * (Edge)iter.next(); System.out.print(" " + f.getIdxsplit()); }
         * System.out.println(""); }
         */
    }

    private static void log_elist(LinkedList<Edge> elist, String mes) {
        int i = 0;
        Edge f = null;

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        Iterator iter = elist.iterator();

        System.out.println(mes + " ");

        while (iter.hasNext()) {
            f = (Edge) iter.next();
            System.out.print(" " + f.getTimestp());
        }
        System.out.println("");
    }

    private static void log_elist_idx(LinkedList<Edge> elist, String mes) {
        int i = 0;
        Edge f = null;

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        Iterator iter = elist.iterator();

        System.out.println(mes + " ");

        while (iter.hasNext()) {
            f = (Edge) iter.next();
            System.out.print(" " + f.getIdxsplit());
        }
        System.out.println("");
    }
}
