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


import uk.ac.uea.cmp.phybre.flatnj.ds.Taxa;

import java.io.PrintWriter;
import java.util.*;

//This class provides the algorithm for drawing a
//network representing a flat split system
public class DrawFlat {
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//Methods for computing a plane drawing of a split
//network representing a flat split system which is
//given by a permutation sequence
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    //******************************************************
    //public methods for computing a drawing of the network
    //******************************************************
    //This method computes a split graph representing
    //the flat split system given by a permutation sequence.
    //It returns a vertex of the resulting split graph
    //from which the network can be traversed.
    //The split graph may contain unlabeled vertices of degree
    //two and trivial splits may be represented by more than
    //one edge in the network. This is the first step
    //of the computation of the final network.
    //It is provided as a public method for testing
    //purposes.
    public static Vertex compute_split_graph(PermutationSequenceDraw pseq, String[] taxaname, TreeSet[] splitedges) {
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
        LinkedList vlist = collect_vertices(v);
        ListIterator viter = vlist.listIterator();
        ListIterator eiter = null;
        Vertex u = null;
        Vertex w = v;
        Edge e = null;

        int c = 0;

        while (viter.hasNext()) {
            u = (Vertex) viter.next();
            if ((u.taxa.size() > 0) || (u.elist.size() > 2)) {
                viter.remove();
            }
        }
        //System.out.println(vlist.size() + "unlabeled vertices of degree 2 collected");

        while (vlist.size() > 0) {
            viter = vlist.listIterator();
            while (viter.hasNext()) {
                u = (Vertex) viter.next();
                viter.remove();
                eiter = u.elist.listIterator();
                while (eiter.hasNext()) {
                    e = (Edge) eiter.next();
                    if (u == e.bot) {
                        e.top.elist.remove(e);
                        w = e.top;
                        if (((w.taxa.size() == 0) && (w.elist.size() < 3))
                                && (vlist.contains(w) == false)) {
                            viter.add(w);
                        }
                    } else {
                        e.bot.elist.remove(e);
                        w = e.bot;
                        if (((w.taxa.size() == 0) && (w.elist.size() < 3))
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
            if (pseq.active[i] && is_trivial(pseq, i, ssyst)) {
                //System.out.println("is trivial");
                LinkedList elist = collect_edges_for_split(i, u);
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
            elist = collect_edges((Edge) (v.elist).getFirst());
        } else {
            elist = new LinkedList();
        }
        int nvert = vlist.size();
        int nedges = elist.size();
        //System.out.println("write " + filename);
        psequ.compress_split_indices();
        //get the printwriter and write blocks
        PrintWriter pw = NexusIO.openprintwriter(filename);
        NexusIO.writeheader(pw);
        NexusIO.writetaxa(ntaxa, taxaname, pw);
        NexusIO.writenetwork(ntaxa, nvert, nedges, psequ, vlist, elist, pw);
        NexusIO.closeprintwriter(pw);
    } */

    //This method is the main public method that should be
    //called for computing a plane split network for a
    //flat split system given as a permutation sequence.
    public static Vertex drawsplitsystem(PermutationSequenceDraw pseq, double thr, Taxa taxa) {
        pseq.restoreTrivialWeightsForExternalVertices();

        pseq.removeSplitsSmallerOrEqualThan(thr);
        //Array of sets of edges, one for each split
        TreeSet[] splitedges = new TreeSet[pseq.nswaps];

        //start timing
        long start = System.currentTimeMillis();

        Vertex v = compute_split_graph(pseq, pseq.taxaname, splitedges);

        uk.ac.uea.cmp.phybre.flatnj.tools.Writer w = new uk.ac.uea.cmp.phybre.flatnj.tools.Writer();

        v = remove_compatible_boxes(v, pseq, false, splitedges);

        //stop timing      
        long stop = System.currentTimeMillis();

        //print timing information on screen
        System.out.println("Total time elapsed: " + (stop - start));

        //store split network


        //lay_opt.highlightExternalEdges(v, pseq);
        return v;
        //write_splits_graph_to_file(v, filename, pseq.ntaxa, pseq.taxaname, pseq);
    }

    //for checking if the resulting network as a minimal
    //number of boxes
    public static void checkdrawsplitsystem(PermutationSequenceDraw pseq) {
        //Array of sets of edges, one for each split
        TreeSet[] splitedges = new TreeSet[pseq.nswaps];

        Vertex v = compute_split_graph(pseq, pseq.taxaname, splitedges);
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

        System.out.print("Check active splits:");

        //check all pairs of active splits
        for (i = 0; i < (psequ.nswaps - 1); i++) {
            if (psequ.active[i]) {
                System.out.print("*");

                for (j = i + 1; j < psequ.nswaps; j++) {
                    if (psequ.active[j]) {
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
                                System.out.println("Vertex u is null - stop here");
                                System.exit(0);
                            }
                        } else {
                            //System.out.println("not compatible");
                            count++;
                        }
                    }
                }
            }
        }
        System.out.println("");

        //Check if the numbers of boxes and edges in the output makes sense 
        //First compute the number edges in the network
        int nedges = 0;

        for (i = 0; i < splitedges.length; i++) {
            nedges = nedges + splitedges[i].size();
        }

        System.out.println("Number of active splits: " + psequ.nActive);
        System.out.println("Number of edges in network: " + nedges);
        System.out.println("Number of incompatible pairs: " + count);
        System.out.println("Number of boxes: " + ((nedges - psequ.nActive) / 2));

        if ((check) && ((nedges - psequ.nActive) / 2) != count) {
            System.out.println("Numbers do not match!!!!!");
            System.exit(0);
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
                f1 = (Edge) e1.top.elist.get((e1.top.elist.indexOf(e1) + e1.top.elist.size() - 1) % e1.top.elist.size());
                f2 = (Edge) e1.bot.elist.get((e1.bot.elist.indexOf(e1) + 1) % e1.bot.elist.size());

                if (f1.idxsplit == b) {
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
        if (netbox.f1.timestp < netbox.f2.timestp) {
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

        if (h1.timestp < h2.timestp) {
            if (dirs == -1) {
                stopstp = h2.timestp;
            } else {
                stopstp = h1.timestp;
            }
        } else {
            if (dirs == -1) {
                stopstp = h1.timestp;
            } else {
                stopstp = h2.timestp;
            }
        }

        if (dirs == -1) {
            while (iter.hasNext()) {
                g = (Edge) iter.next();
                if (g.timestp < stopstp) {
                    elists.addFirst(g);
                    if (parts == 1) {
                        crosslists.addFirst(g.top.elist.get((g.top.elist.indexOf(g) + g.top.elist.size() - 1) % g.top.elist.size()));
                    } else {
                        crosslists.addFirst(g.bot.elist.get((g.bot.elist.indexOf(g) + 1) % g.bot.elist.size()));
                    }
                } else {
                    break;
                }
            }
        } else {
            while (iter.hasNext()) {
                g = (Edge) iter.next();
                if (g.timestp > stopstp) {
                    elists.addLast(g);
                    if (parts == 1) {
                        crosslists.addLast((Edge) g.top.elist.get((g.top.elist.indexOf(g) + 1) % g.top.elist.size()));
                    } else {
                        crosslists.addLast((Edge) g.bot.elist.get((g.bot.elist.indexOf(g) + g.bot.elist.size() - 1) % g.bot.elist.size()));
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

            if (e.idxsplit == b) {
                crossboth.addLast(e);
            } else {
                iterb = crosslistb.listIterator();

                while (iterb.hasNext()) {
                    f = (Edge) iterb.next();
                    if (e.idxsplit == f.idxsplit) {
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
            SortedSet head = splitedges[e.idxsplit].headSet(e);
            Iterator headiter = head.iterator();
            while (headiter.hasNext()) {
                h = (Edge) headiter.next();
                g1 = h.top.elist.get((h.top.elist.indexOf(h) + (h.top.elist.size() - 1)) % h.top.elist.size());
                g2 = h.bot.elist.get((h.bot.elist.indexOf(h) + 1) % h.bot.elist.size());

                if ((parta == 1) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].last();
                        if (g.timestp > g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 1) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].first();
                        if (g.timestp < g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].first();
                        if (g.timestp < g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].last();
                        if (g.timestp > g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            SortedSet tail = splitedges[e.idxsplit].tailSet(e);
            Iterator tailiter = tail.iterator();
            //tail contains e!!!
            h = (Edge) tailiter.next();
            while (tailiter.hasNext()) {
                h = (Edge) tailiter.next();
                g1 = h.top.elist.get((h.top.elist.indexOf(h) + 1) % h.top.elist.size());
                g2 = h.bot.elist.get((h.bot.elist.indexOf(h) + (h.bot.elist.size() - 1)) % h.bot.elist.size());

                if ((parta == 1) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].last();
                        if (g.timestp > g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 1) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].first();
                        if (g.timestp < g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].first();
                        if (g.timestp < g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((parta == 0) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].last();
                        if (g.timestp > g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
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
            SortedSet head = splitedges[f.idxsplit].headSet(f);
            Iterator headiter = head.iterator();
            while (headiter.hasNext()) {
                h = (Edge) headiter.next();
                g1 = h.top.elist.get((h.top.elist.indexOf(h) + (h.top.elist.size() - 1)) % h.top.elist.size());
                g2 = h.bot.elist.get((h.bot.elist.indexOf(h) + 1) % h.bot.elist.size());

                if ((partb == 1) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].last();
                        if (g.timestp > g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 1) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].first();
                        if (g.timestp < g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].first();
                        if (g.timestp < g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].last();
                        if (g.timestp > g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            SortedSet tail = splitedges[f.idxsplit].tailSet(f);
            Iterator tailiter = tail.iterator();
            //tail contains f!!!
            h = (Edge) tailiter.next();
            while (tailiter.hasNext()) {
                h = (Edge) tailiter.next();
                g1 = h.top.elist.get((h.top.elist.indexOf(h) + 1) % h.top.elist.size());
                g2 = h.bot.elist.get((h.bot.elist.indexOf(h) + (h.bot.elist.size() - 1)) % h.bot.elist.size());

                if ((partb == 1) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].last();
                        if (g.timestp > g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 1) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g1.idxsplit].first();
                        if (g.timestp < g1.timestp) {
                            splitedges[g1.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.timestp < g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].first();
                        if (g.timestp < g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
                        } else {
                            break;
                        }
                    }
                } else if ((partb == 0) && (g1.timestp > g2.timestp)) {
                    while (true) {
                        g = (Edge) splitedges[g2.idxsplit].last();
                        if (g.timestp > g2.timestp) {
                            splitedges[g2.idxsplit].remove(g);
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
                if (g.timestp <= e.timestp) {
                    splitedges[a].remove(g);
                    if (parta == 1) {
                        g.top.elist.remove(g);
                        u = g.top;
                    } else {
                        g.bot.elist.remove(g);
                        u = g.bot;
                    }
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                g = (Edge) splitedges[a].last();
                if (g.timestp >= e.timestp) {
                    splitedges[a].remove(g);
                    if (parta == 1) {
                        g.top.elist.remove(g);
                        u = g.top;
                    } else {
                        g.bot.elist.remove(g);
                        u = g.bot;
                    }
                } else {
                    break;
                }
            }
        }

        if (dirb == -1) {
            while (true) {
                g = (Edge) splitedges[b].first();
                if (g.timestp <= f.timestp) {
                    splitedges[b].remove(g);
                    if (partb == 1) {
                        g.top.elist.remove(g);
                    } else {
                        g.bot.elist.remove(g);
                    }
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                g = (Edge) splitedges[b].last();
                if (g.timestp >= f.timestp) {
                    if (partb == 1) {
                        g.top.elist.remove(g);
                    } else {
                        g.bot.elist.remove(g);
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
            if ((indexe == f.idxsplit) && (indexf == e.idxsplit)) {
                cleanlist.addAll(listf);
                break;
            } else {
                e = f;
            }
        }

        return e.idxsplit;
    }

    //This method walks through the network to the
    //first scrossing with a split in crossindices
    private static int go_to_first_crossing(Edge e, int a, TreeSet crossindices, LinkedList<Edge> liste, TreeSet[] splitedges) {
        int dire = 0;
        int sidx = 0;

        //first check in which direction we need to go
        SortedSet testtail = splitedges[e.idxsplit].tailSet(e);

        Edge f = null;

        if (testtail.size() == 1) {
            dire = -1;
        } else if (a == (e.bot.elist.get((e.bot.elist.indexOf(e) + 1) % e.bot.elist.size())).idxsplit) {
            dire = 1;
        } else {
            dire = -1;
        }

        if (dire == 1) {
            SortedSet tail = splitedges[e.idxsplit].tailSet(e);
            Iterator tailiter = tail.iterator();
            f = (Edge) tailiter.next();
            while (tailiter.hasNext()) {
                f = (Edge) tailiter.next();
                sidx = (f.top.elist.get((f.top.elist.indexOf(f) + 1) % f.top.elist.size())).idxsplit;
                if (crossindices.contains(new Integer(sidx))) {
                    return sidx;
                } else {
                    liste.addLast(f);
                }
            }
        } else {
            SortedSet head = splitedges[e.idxsplit].headSet(e);
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
                sidx = ((Edge) f.bot.elist.get((f.bot.elist.indexOf(f) + 1) % f.bot.elist.size())).idxsplit;
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
            index = new Integer(e.idxsplit);
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
        SortedSet testtail = splitedges[e.idxsplit].tailSet(e);

        Edge f = null;

        if (testtail.size() == 1) {
            dire = 1;
        } else if (a == (e.bot.elist.get((e.bot.elist.indexOf(e) + 1) % e.bot.elist.size())).idxsplit) {
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
                v = e.top;
                if (v.elist.size() == 3) {
                    e1 = v.elist.get((v.elist.indexOf(e) + 2) % 3);
                    e2 = v.elist.get((v.elist.indexOf(e) + 1) % 3);
                    v5 = e.bot;
                    if (v == e2.bot) {
                        v1 = e2.top;
                    } else {
                        v1 = e2.bot;
                    }
                    if (v == e1.bot) {
                        v3 = e1.top;
                    } else {
                        v3 = e1.bot;
                    }
                    e3 = v3.elist.get((v3.elist.indexOf(e1) + 1) % v3.elist.size());
                    e4 = v1.elist.get((v1.elist.indexOf(e2) + v1.elist.size() - 1) % v1.elist.size());
                    e5 = v1.elist.get((v1.elist.indexOf(e2) + 1) % v1.elist.size());
                    e6 = v5.elist.get((v5.elist.indexOf(e) + v5.elist.size() - 1) % v5.elist.size());
                    e7 = v5.elist.get((v5.elist.indexOf(e) + 1) % v5.elist.size());
                    e8 = v3.elist.get((v3.elist.indexOf(e1) + v3.elist.size() - 1) % v3.elist.size());
                    if (v1 == e4.bot) {
                        v2 = e4.top;
                    } else {
                        v2 = e4.bot;
                    }
                    if (v3 == e3.bot) {
                        v4 = e3.top;
                    } else {
                        v4 = e3.bot;
                    }
                    if (v3 == e8.bot) {
                        v6 = e8.top;
                    } else {
                        v6 = e8.bot;
                    }
                    if (v5 == e7.bot) {
                        v7 = e7.top;
                    } else {
                        v7 = e7.bot;
                    }
                    if (v5 == e6.bot) {
                        v8 = e6.top;
                    } else {
                        v8 = e6.bot;
                    }
                    if (v1 == e5.bot) {
                        v9 = e5.top;
                    } else {
                        v9 = e5.bot;
                    }
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.idxsplit == e5.idxsplit)
                            && (e.idxsplit == e8.idxsplit)
                            && (e2.idxsplit == e3.idxsplit)
                            && (e2.idxsplit == e6.idxsplit)
                            && (e1.idxsplit == e4.idxsplit)
                            && (e1.idxsplit == e7.idxsplit)) {
                        break;
                    }
                }
            }

            //flip cube
            v1.elist.remove(e2);
            v3.elist.remove(e1);
            v5.elist.remove(e);
            v.elist.clear();
            v.x = v2.x + (v5.x - v.x);
            v.y = v2.y + (v5.y - v.y);
            if (v8 == e6.top) {
                h1 = new Edge(v, v6, e6.idxsplit, e2.timestp);
            } else {
                h1 = new Edge(v6, v, e6.idxsplit, e2.timestp);
            }
            v6.elist.add(v6.elist.indexOf(e7) + 1, h1);
            v.elist.addLast(h1);
            if (v6 == e7.top) {
                h2 = new Edge(v, v8, e7.idxsplit, e1.timestp);
            } else {
                h2 = new Edge(v8, v, e7.idxsplit, e1.timestp);
            }
            v8.elist.add(v8.elist.indexOf(e5) + 1, h2);
            v.elist.addLast(h2);
            h3 = new Edge(v2, v, e.idxsplit, e.timestp);
            v2.elist.add(v2.elist.indexOf(e3) + 1, h3);
            v.elist.addLast(h3);

            //System.out.println("Size of elist before update: " + elist.size());

            //update lists of edges accordingly
            splitedges[e.idxsplit].remove(e);
            splitedges[e.idxsplit].add(h3);
            splitedges[e2.idxsplit].remove(e2);
            splitedges[e2.idxsplit].add(h1);
            splitedges[e1.idxsplit].remove(e1);
            splitedges[e1.idxsplit].add(h2);

            if (parta == 1) {
                if (dira == -1) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    if ((e6.idxsplit == a) && (e7.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.idxsplit == a) && (e6.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeLast();
                    }
                }
            } else {
                if (dira == -1) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.idxsplit == a) && (e6.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    if ((e6.idxsplit == a) && (e7.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s != b)) {
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
                v = e.bot;
                if (v.elist.size() == 3) {
                    e1 = (Edge) v.elist.get((v.elist.indexOf(e) + 2) % 3);
                    e2 = (Edge) v.elist.get((v.elist.indexOf(e) + 1) % 3);
                    v5 = e.top;
                    if (v == e2.bot) {
                        v1 = e2.top;
                    } else {
                        v1 = e2.bot;
                    }
                    if (v == e1.bot) {
                        v3 = e1.top;
                    } else {
                        v3 = e1.bot;
                    }
                    e3 = v3.elist.get((v3.elist.indexOf(e1) + 1) % v3.elist.size());
                    e4 = v1.elist.get((v1.elist.indexOf(e2) + v1.elist.size() - 1) % v1.elist.size());
                    e5 = v1.elist.get((v1.elist.indexOf(e2) + 1) % v1.elist.size());
                    e6 = v5.elist.get((v5.elist.indexOf(e) + v5.elist.size() - 1) % v5.elist.size());
                    e7 = v5.elist.get((v5.elist.indexOf(e) + 1) % v5.elist.size());
                    e8 = v3.elist.get((v3.elist.indexOf(e1) + v3.elist.size() - 1) % v3.elist.size());
                    if (v1 == e4.bot) {
                        v2 = e4.top;
                    } else {
                        v2 = e4.bot;
                    }
                    if (v3 == e3.bot) {
                        v4 = e3.top;
                    } else {
                        v4 = e3.bot;
                    }
                    if (v3 == e8.bot) {
                        v6 = e8.top;
                    } else {
                        v6 = e8.bot;
                    }
                    if (v5 == e7.bot) {
                        v7 = e7.top;
                    } else {
                        v7 = e7.bot;
                    }
                    if (v5 == e6.bot) {
                        v8 = e6.top;
                    } else {
                        v8 = e6.bot;
                    }
                    if (v1 == e5.bot) {
                        v9 = e5.top;
                    } else {
                        v9 = e5.bot;
                    }
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.idxsplit == e5.idxsplit)
                            && (e.idxsplit == e8.idxsplit)
                            && (e2.idxsplit == e3.idxsplit)
                            && (e2.idxsplit == e6.idxsplit)
                            && (e1.idxsplit == e4.idxsplit)
                            && (e1.idxsplit == e7.idxsplit)) {
                        break;
                    }
                }
            }

            //flip cube
            v1.elist.remove(e2);
            v3.elist.remove(e1);
            v5.elist.remove(e);
            v.elist.clear();
            v.x = v2.x + (v5.x - v.x);
            v.y = v2.y + (v5.y - v.y);
            if (v8 == e6.top) {
                h1 = new Edge(v, v6, e6.idxsplit, e2.timestp);
            } else {
                h1 = new Edge(v6, v, e6.idxsplit, e2.timestp);
            }
            v6.elist.add(v6.elist.indexOf(e7) + 1, h1);
            v.elist.addLast(h1);
            if (v6 == e7.top) {
                h2 = new Edge(v, v8, e7.idxsplit, e1.timestp);
            } else {
                h2 = new Edge(v8, v, e7.idxsplit, e1.timestp);
            }
            v8.elist.add(v8.elist.indexOf(e5) + 1, h2);
            v.elist.addLast(h2);
            h3 = new Edge(v, v2, e.idxsplit, e.timestp);
            v2.elist.add(v2.elist.indexOf(e3) + 1, h3);
            v.elist.addLast(h3);

            //System.out.println("Size of elist before update: " + elist.size());

            //update lists of edges accordingly
            splitedges[e.idxsplit].remove(e);
            splitedges[e.idxsplit].add(h3);
            splitedges[e2.idxsplit].remove(e2);
            splitedges[e2.idxsplit].add(h1);
            splitedges[e1.idxsplit].remove(e1);
            splitedges[e1.idxsplit].add(h2);

            if (parta == 1) {
                if (dira == -1) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    //System.out.println("Size of elist after first update: " + elist.size()); 

                    if ((e6.idxsplit == a) && (e7.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.idxsplit == a) && (e6.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeLast();
                    }
                }
            } else {
                if (dira == -1) {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);

                    if ((e7.idxsplit == a) && (e6.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e8) + 1, h3);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e8);
                        crossboth.add(crossboth.indexOf(e3) + 1, h1);
                        crossboth.remove(e3);
                        elista.remove(e1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit == a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e3) + 1, h3);
                        crossboth.remove(e3);
                        crossboth.add(crossboth.indexOf(e8) + 1, h1);
                        crossboth.remove(e8);
                        elista.add(elista.indexOf(e1) + 1, h2);
                        elista.remove(e1);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e2) + 1, h1);
                        elistb.remove(e2);
                    }
                    if ((e7.idxsplit != a) && (e6.idxsplit == s) && (s != b)) {
                        elist.removeLast();
                    }
                } else {
                    elist.add(elist.indexOf(e) + 1, h3);
                    elist.remove(e);
                    if ((e6.idxsplit == a) && (e7.idxsplit != s)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e5) + 1, h3);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeFirst();
                        crossboth.remove(e5);
                        crossboth.add(crossboth.indexOf(e4) + 1, h2);
                        crossboth.remove(e4);
                        elista.remove(e2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit == a) && (e7.idxsplit == s) && (s != b)) {
                        elist.removeFirst();
                        crossboth.add(crossboth.indexOf(e4) + 1, h3);
                        crossboth.remove(e4);
                        crossboth.add(crossboth.indexOf(e5) + 1, h2);
                        crossboth.remove(e5);
                        elista.add(elista.indexOf(e2) + 1, h1);
                        elista.remove(e2);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s == b)) {
                        elist.removeLast();
                        elistb.add(elistb.indexOf(e1) + 1, h2);
                        elistb.remove(e1);
                    }
                    if ((e6.idxsplit != a) && (e7.idxsplit == s) && (s != b)) {
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
    private static Edge[] leftmost_edges(PermutationSequenceDraw pseq, TreeSet[] splitedges) {
        int i = 0;
        int j = 0;
        double dx = 0.0;
        double dy = 0.0;
        double xcoord = 0.0;
        double ycoord = 0.0;

        Vertex u = null;
        Vertex v = null;
        Edge e = null;

        Edge[] chain = new Edge[pseq.nActive];

        u = new Vertex(xcoord, ycoord);

        for (i = 0; i < pseq.nswaps; i++) {
            //create new set for edges associated to this split
            splitedges[i] = new TreeSet(new EdgeComparator());

            if (pseq.active[i]) {
                dx = -Math.cos(((j + 1) * Math.PI) / (pseq.nActive + 1));
                dy = -Math.sin(((j + 1) * Math.PI) / (pseq.nActive + 1));

                xcoord = xcoord + (pseq.weights[i] * dx);
                ycoord = ycoord + (pseq.weights[i] * dy);

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

        for (i = 0; i < pseq.ntaxa; i++) {
            inverted = true;
            //System.out.println("Taxon " + pseq.initSequ[i]);

            while (inverted) {
                inverted = false;

                for (j = 1; j < chain.length; j++) {
                    //test if the splits associated to edges
                    //chain[j-1] and chain[j] must be inverted

                    if (ssyst.splits[chain[j - 1].idxsplit][pseq.initSequ[i]] == 0
                            && ssyst.splits[chain[j].idxsplit][pseq.initSequ[i]] == 1) {
                        dx = (chain[j].bot).x - (chain[j].top).x;
                        dy = (chain[j].bot).y - (chain[j].top).y;

                        v = new Vertex((chain[j - 1].top).x + dx, (chain[j - 1].top).y + dy);
                        e1 = new Edge(chain[j - 1].top, v, chain[j].idxsplit, chain[j].timestp + 1);
                        splitedges[e1.idxsplit].add(e1);
                        e2 = new Edge(v, chain[j].bot, chain[j - 1].idxsplit, chain[j - 1].timestp + 1);
                        splitedges[e2.idxsplit].add(e2);
                        chain[j - 1] = e1;
                        chain[j] = e2;
                        v.add_edge_before_first(e2);
                        v.add_edge_after_last(e1);
                        (e1.top).add_edge_before_first(e1);
                        (e2.bot).add_edge_after_last(e2);
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
                v.taxa.add(new Integer(pseq.initSequ[i]));
                pseq.representedby[pseq.initSequ[i]] = 0;
                if (pseq.initSequ[i] != 0) {
                    pseq.activeTaxa[pseq.initSequ[i]] = false;
                }
                pseq.nclasses = 1;
            } else {
                v = chain[0].top;
                for (j = 0; j < chain.length; j++) {
                    if (ssyst.splits[chain[j].idxsplit][pseq.initSequ[i]] == 0) {
                        (chain[j].top).taxa.addLast(new Integer(pseq.initSequ[i]));
                        if (chain[j].top.taxa.size() > 1) {
                            pseq.representedby[pseq.initSequ[i]] = ((Integer) (chain[j].top.taxa.getFirst())).intValue();
                            pseq.activeTaxa[pseq.initSequ[i]] = false;
                            pseq.nclasses--;
                        }
                        break;
                    } else {
                        if (j == (chain.length - 1)) {
                            (chain[j].bot).taxa.addLast(new Integer(pseq.initSequ[i]));
                            if (chain[j].bot.taxa.size() > 1) {
                                pseq.representedby[pseq.initSequ[i]] = ((Integer) chain[j].bot.taxa.getFirst()).intValue();
                                pseq.activeTaxa[pseq.initSequ[i]] = false;
                                pseq.nclasses--;
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
            if (pseq.activeTaxa[i] == true) {
                c = c + ssyst.splits[s][i];
            }
        }

        if ((c == 1) || (c == (pseq.nclasses - 1))) {
            return true;
        } else {
            return false;
        }
    }

    //This method collects the edges that represent a
    //given split in the network.
    public static LinkedList<Edge> collect_edges_for_split(int s, Vertex v) {
        LinkedList elistall = collect_edges((Edge) (v.elist).getFirst());
        ListIterator iter = elistall.listIterator();
        LinkedList elist = new LinkedList();
        Edge e = null;
        while (iter.hasNext()) {
            e = (Edge) iter.next();
            if (e.idxsplit == s) {
                elist.add(e);
            }
        }
        return elist;
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
                if (e.idxsplit == s) {
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
            if (pseq.activeTaxa[i] == true) {
                c = c + ssyst.splits[s][i];
            }
        }

        if (c == 1) {
            for (i = 0; i < ssyst.ntaxa; i++) {
                if ((pseq.activeTaxa[i] == true) && (ssyst.splits[s][i] == 1)) {
                    idx = i;
                    break;
                }
            }
        } else {
            for (i = 0; i < ssyst.ntaxa; i++) {
                if ((pseq.activeTaxa[i] == true) && (ssyst.splits[s][i] == 0)) {
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

        if (ssyst.splits[((Edge) elist.getFirst()).idxsplit][taxon] == 0) {
            if (e == null) {
                iter = elist.listIterator();
                while (iter.hasNext()) {
                    h = (Edge) iter.next();
                    if (h.top.taxa.contains(new Integer(taxon))) {
                        break;
                    } else {
                        h = null;
                    }
                }
            } else {
                if (e.top.taxa.contains(new Integer(taxon))) {
                    h = e;
                }
            }
        } else {
            if (e == null) {
                iter = elist.listIterator();
                while (iter.hasNext()) {
                    h = (Edge) iter.next();
                    if (h.bot.taxa.contains(new Integer(taxon))) {
                        break;
                    } else {
                        h = null;
                    }
                }
            } else {
                if (e.bot.taxa.contains(new Integer(taxon))) {
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

        if (ssyst.splits[((Edge) elist.getFirst()).idxsplit][taxon] == 0) {
            iter = elist.listIterator();
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                v = e.top;
                if (v.elist.size() == 3) {
                    e1 = v.elist.get((v.elist.indexOf(e) + 2) % 3);
                    e2 = v.elist.get((v.elist.indexOf(e) + 1) % 3);
                    v5 = e.bot;
                    if (v == e2.bot) {
                        v1 = e2.top;
                    } else {
                        v1 = e2.bot;
                    }
                    if (v == e1.bot) {
                        v3 = e1.top;
                    } else {
                        v3 = e1.bot;
                    }
                    e3 = v3.elist.get((v3.elist.indexOf(e1) + 1) % v3.elist.size());
                    e4 = v1.elist.get((v1.elist.indexOf(e2) + v1.elist.size() - 1) % v1.elist.size());
                    e5 = v1.elist.get((v1.elist.indexOf(e2) + 1) % v1.elist.size());
                    e6 = v5.elist.get((v5.elist.indexOf(e) + v5.elist.size() - 1) % v5.elist.size());
                    e7 = v5.elist.get((v5.elist.indexOf(e) + 1) % v5.elist.size());
                    e8 = v3.elist.get((v3.elist.indexOf(e1) + v3.elist.size() - 1) % v3.elist.size());
                    if (v1 == e4.bot) {
                        v2 = e4.top;
                    } else {
                        v2 = e4.bot;
                    }
                    if (v3 == e3.bot) {
                        v4 = e3.top;
                    } else {
                        v4 = e3.bot;
                    }
                    if (v3 == e8.bot) {
                        v6 = e8.top;
                    } else {
                        v6 = e8.bot;
                    }
                    if (v5 == e7.bot) {
                        v7 = e7.top;
                    } else {
                        v7 = e7.bot;
                    }
                    if (v5 == e6.bot) {
                        v8 = e6.top;
                    } else {
                        v8 = e6.bot;
                    }
                    if (v1 == e5.bot) {
                        v9 = e5.top;
                    } else {
                        v9 = e5.bot;
                    }
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.idxsplit == e5.idxsplit)
                            && (e.idxsplit == e8.idxsplit)
                            && (e2.idxsplit == e3.idxsplit)
                            && (e2.idxsplit == e6.idxsplit)
                            && (e1.idxsplit == e4.idxsplit)
                            && (e1.idxsplit == e7.idxsplit)) {
                        break;
                    }
                }
            }
            v1.elist.remove(e2);
            v3.elist.remove(e1);
            v5.elist.remove(e);
            v.elist.clear();
            v.x = v2.x + (v5.x - v.x);
            v.y = v2.y + (v5.y - v.y);
            if (v8 == e6.top) {
                h = new Edge(v, v6, e6.idxsplit, e2.timestp);
            } else {
                h = new Edge(v6, v, e6.idxsplit, e2.timestp);
            }
            v6.elist.add(v6.elist.indexOf(e7) + 1, h);
            v.elist.addLast(h);
            if (v6 == e7.top) {
                h = new Edge(v, v8, e7.idxsplit, e1.timestp);
            } else {
                h = new Edge(v8, v, e7.idxsplit, e1.timestp);
            }
            v8.elist.add(v8.elist.indexOf(e5) + 1, h);
            v.elist.addLast(h);
            h = new Edge(v2, v, e.idxsplit, e.timestp);
            v2.elist.add(v2.elist.indexOf(e3) + 1, h);
            v.elist.addLast(h);
        } else {
            iter = elist.listIterator();
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                v = e.bot;
                if (v.elist.size() == 3) {
                    e1 = (Edge) v.elist.get((v.elist.indexOf(e) + 2) % 3);
                    e2 = (Edge) v.elist.get((v.elist.indexOf(e) + 1) % 3);
                    v5 = e.top;
                    if (v == e2.bot) {
                        v1 = e2.top;
                    } else {
                        v1 = e2.bot;
                    }
                    if (v == e1.bot) {
                        v3 = e1.top;
                    } else {
                        v3 = e1.bot;
                    }
                    e3 = v3.elist.get((v3.elist.indexOf(e1) + 1) % v3.elist.size());
                    e4 = v1.elist.get((v1.elist.indexOf(e2) + v1.elist.size() - 1) % v1.elist.size());
                    e5 = v1.elist.get((v1.elist.indexOf(e2) + 1) % v1.elist.size());
                    e6 = v5.elist.get((v5.elist.indexOf(e) + v5.elist.size() - 1) % v5.elist.size());
                    e7 = v5.elist.get((v5.elist.indexOf(e) + 1) % v5.elist.size());
                    e8 = v3.elist.get((v3.elist.indexOf(e1) + v3.elist.size() - 1) % v3.elist.size());
                    if (v1 == e4.bot) {
                        v2 = e4.top;
                    } else {
                        v2 = e4.bot;
                    }
                    if (v3 == e3.bot) {
                        v4 = e3.top;
                    } else {
                        v4 = e3.bot;
                    }
                    if (v3 == e8.bot) {
                        v6 = e8.top;
                    } else {
                        v6 = e8.bot;
                    }
                    if (v5 == e7.bot) {
                        v7 = e7.top;
                    } else {
                        v7 = e7.bot;
                    }
                    if (v5 == e6.bot) {
                        v8 = e6.top;
                    } else {
                        v8 = e6.bot;
                    }
                    if (v1 == e5.bot) {
                        v9 = e5.top;
                    } else {
                        v9 = e5.bot;
                    }
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.idxsplit == e5.idxsplit)
                            && (e.idxsplit == e8.idxsplit)
                            && (e2.idxsplit == e3.idxsplit)
                            && (e2.idxsplit == e6.idxsplit)
                            && (e1.idxsplit == e4.idxsplit)
                            && (e1.idxsplit == e7.idxsplit)) {
                        break;
                    }
                }
            }
            v1.elist.remove(e2);
            v3.elist.remove(e1);
            v5.elist.remove(e);
            v.elist.clear();
            v.x = v2.x + (v5.x - v.x);
            v.y = v2.y + (v5.y - v.y);
            if (v8 == e6.top) {
                h = new Edge(v, v6, e6.idxsplit, e2.timestp);
            } else {
                h = new Edge(v6, v, e6.idxsplit, e2.timestp);
            }
            v6.elist.add(v6.elist.indexOf(e7) + 1, h);
            v.elist.addLast(h);
            if (v6 == e7.top) {
                h = new Edge(v, v8, e7.idxsplit, e1.timestp);
            } else {
                h = new Edge(v8, v, e7.idxsplit, e1.timestp);
            }
            v8.elist.add(v8.elist.indexOf(e5) + 1, h);
            v.elist.addLast(h);
            h = new Edge(v, v2, e.idxsplit, e.timestp);
            v2.elist.add(v2.elist.indexOf(e3) + 1, h);
            v.elist.addLast(h);
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
        int taxon = find_taxon_index(pseq, ((Edge) elist.getFirst()).idxsplit, ssyst);
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
        int taxon = find_taxon_index(pseq, e.idxsplit, ssyst);

        if (ssyst.splits[((Edge) elist.getFirst()).idxsplit][taxon] == 0) {
            v = e.top;
            iter = elist.listIterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (e != h) {
                    h.bot.elist.remove(h);
                }
            }
            v.elist.clear();
            v.elist.addFirst(e);
        } else {
            v = e.bot;
            iter = elist.listIterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (e != h) {
                    h.top.elist.remove(h);
                }
            }
            v.elist.clear();
            v.elist.addFirst(e);
        }

        return v;
    }

    //Auxiliary method used to collect the vertices
    //in the network.
    private static void aux_collect_vertices(Vertex v, LinkedList<Vertex> vlist) {
        LinkedList<Vertex> tobeexplored = new LinkedList<>();
        tobeexplored.addLast(v);
        v.visited = true;
        Vertex u = null;
        Edge e = null;

        while (tobeexplored.size() > 0) {
            u = (Vertex) tobeexplored.removeFirst();
            vlist.addLast(u);
            ListIterator iter = (u.elist).listIterator();
            while (iter.hasNext()) {
                e = (Edge) iter.next();
                if (u == e.top) {
                    if ((e.bot).visited == false) {
                        tobeexplored.addLast(e.bot);
                        (e.bot).visited = true;
                    }
                } else {
                    if ((e.top).visited == false) {
                        tobeexplored.addLast(e.top);
                        (e.top).visited = true;
                    }
                }
            }
        }
    }

    //This method computes a list of the vertices 
    //in the split network
    public static LinkedList<Vertex> collect_vertices(Vertex v) {
        LinkedList vlist = new LinkedList();
        if (v == null) {
            System.out.println("Start vertex is null -- stop here");
            System.exit(0);
        }
        aux_collect_vertices(v, vlist);
        ListIterator iter = vlist.listIterator(0);
        int i = 1;
        Vertex u = null;
        while (iter.hasNext()) {
            u = (Vertex) iter.next();
            u.visited = false;
            u.nxnum = i;
            i++;
        }
        return vlist;
    }

    //Auxiliary method used to collect the edges
    //in the split network.
    private static void aux_collect_edges(Edge e, LinkedList<Edge> elist) {
        LinkedList<Edge> tobeexplored = new LinkedList<>();
        tobeexplored.addLast(e);
        e.visited = true;

        Edge g = null;
        Edge h = null;

        while (tobeexplored.size() > 0) {
            g = tobeexplored.removeFirst();
            elist.addLast(g);
            Iterator iter = ((g.top).elist).iterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (h.visited == false) {
                    tobeexplored.addLast(h);
                    h.visited = true;
                }
            }
            iter = ((g.bot).elist).iterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (h.visited == false) {
                    tobeexplored.addLast(h);
                    h.visited = true;
                }
            }
        }
    }

    //This method computes a list of the edges 
    //in the split network.
    public static LinkedList<Edge> collect_edges(Edge e) {
        LinkedList<Edge> elist = new LinkedList<>();
        aux_collect_edges(e, elist);
        ListIterator iter = elist.listIterator(0);
        int i = 1;
        Edge h = null;
        while (iter.hasNext()) {
            h = (Edge) iter.next();
            h.visited = false;
            h.nxnum = i;
            i++;
        }
        return elist;
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
                System.out.print(" " + f.timestp);
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
         * (Edge)iter.next(); System.out.print(" " + f.idxsplit); }
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
            System.out.print(" " + f.timestp);
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
            System.out.print(" " + f.idxsplit);
        }
        System.out.println("");
    }
}
