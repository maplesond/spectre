/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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


import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.EdgeList;
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
            if ((u.getTaxa().size() > 0) || (u.getEdgeList().size() > 2)) {
                viter.remove();
            }
        }
        //System.out.println(vlist.size() + "unlabeled vertices of degree 2 collected");

        while (vlist.size() > 0) {
            viter = vlist.listIterator();
            while (viter.hasNext()) {
                u = (Vertex) viter.next();
                viter.remove();
                eiter = u.getEdgeList().listIterator();
                while (eiter.hasNext()) {
                    e = (Edge) eiter.next();
                    if (u == e.getBottom()) {
                        e.getTop().getEdgeList().remove(e);
                        w = e.getTop();
                        if (((w.getTaxa().size() == 0) && (w.getEdgeList().size() < 3))
                                && (vlist.contains(w) == false)) {
                            viter.add(w);
                        }
                    } else {
                        e.getBottom().getEdgeList().remove(e);
                        w = e.getBottom();
                        if (((w.getTaxa().size() == 0) && (w.getEdgeList().size() < 3))
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
    /*public static Vertex push_out_trivial_splits(Vertex v, PermutationSequenceDraw pseq, String[] taxaname) {
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
    }*/

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
            elist = collect_edges((Edge) (v.getEdgeList()).getFirst());
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






    //******************************************************
    //private methods for computing a drawing of the network
    //******************************************************
















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
                    if (h.getBottom().getTaxa().contains(new Integer(taxon))) {
                        break;
                    } else {
                        h = null;
                    }
                }
            } else {
                if (e.getBottom().getTaxa().contains(new Integer(taxon))) {
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
            v1.getEdgeList().remove(e2);
            v3.getEdgeList().remove(e1);
            v5.getEdgeList().remove(e);
            v.getEdgeList().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getEdgeList().add(v6.getEdgeList().indexOf(e7) + 1, h);
            v.getEdgeList().addLast(h);
            if (v6 == e7.getTop()) {
                h = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getEdgeList().add(v8.getEdgeList().indexOf(e5) + 1, h);
            v.getEdgeList().addLast(h);
            h = new Edge(v2, v, e.getIdxsplit(), e.getTimestp());
            v2.getEdgeList().add(v2.getEdgeList().indexOf(e3) + 1, h);
            v.getEdgeList().addLast(h);
        } else {
            iter = elist.listIterator();
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
            v1.getEdgeList().remove(e2);
            v3.getEdgeList().remove(e1);
            v5.getEdgeList().remove(e);
            v.getEdgeList().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            if (v8 == e6.getTop()) {
                h = new Edge(v, v6, e6.getIdxsplit(), e2.getTimestp());
            } else {
                h = new Edge(v6, v, e6.getIdxsplit(), e2.getTimestp());
            }
            v6.getEdgeList().add(v6.getEdgeList().indexOf(e7) + 1, h);
            v.getEdgeList().addLast(h);
            if (v6 == e7.getTop()) {
                h = new Edge(v, v8, e7.getIdxsplit(), e1.getTimestp());
            } else {
                h = new Edge(v8, v, e7.getIdxsplit(), e1.getTimestp());
            }
            v8.getEdgeList().add(v8.getEdgeList().indexOf(e5) + 1, h);
            v.getEdgeList().addLast(h);
            h = new Edge(v, v2, e.getIdxsplit(), e.getTimestp());
            v2.getEdgeList().add(v2.getEdgeList().indexOf(e3) + 1, h);
            v.getEdgeList().addLast(h);
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
                    h.getBottom().getEdgeList().remove(h);
                }
            }
            v.getEdgeList().clear();
            v.getEdgeList().addFirst(e);
        } else {
            v = e.getBottom();
            iter = elist.listIterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (e != h) {
                    h.getTop().getEdgeList().remove(h);
                }
            }
            v.getEdgeList().clear();
            v.getEdgeList().addFirst(e);
        }

        return v;
    }

}
