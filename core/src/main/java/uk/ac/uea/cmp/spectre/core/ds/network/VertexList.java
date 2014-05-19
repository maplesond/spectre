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

package uk.ac.uea.cmp.spectre.core.ds.network;

import java.util.LinkedList;

/**
 * Created by dan on 20/03/14.
 */
public class VertexList extends LinkedList<Vertex> {


    public Vertex getRightmostVertex() {

        if (this.isEmpty())
            return null;

        Vertex v = this.getFirst();
        for (Vertex vi : this) {
            if (v.getX() > vi.getX()) {
                v = vi;
            }
        }

        return v;
    }

    public EdgeList collectExternalEdges() {

        /*if (externalEdges != null) {
            return new LinkedList(externalEdges);
        } */

        Vertex v = this.getRightmostVertex();

        Edge first = null;

        EdgeList ext = new EdgeList();
        Vertex w = null;

        if (v.getElist().size() == 1) {
            w = (v.getElist().getFirst().getBot() == v) ? v.getElist().getFirst().getTop() : v.getElist().getFirst().getBot();
            first = v.getElist().getFirst();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            EdgeList elist = v.getElist();

            for (int i = 0; i < elist.size(); i++) {
                Vertex ww = null;
                Vertex w0 = (elist.get(i).getBot() == v) ? elist.get(i).getTop() : elist.get(i).getBot();
                double angle = 0;
                for (int j = 0; j < elist.size(); j++) {
                    if (i != j) {
                        Vertex w1 = (elist.get(j).getBot() == v) ? elist.get(j).getTop() : elist.get(j).getBot();
                        double currentAngle = Vertex.getClockwiseAngle(w0, v, w1);
                        if (ww == null || currentAngle < angle) {
                            ww = w0;
                            angle = currentAngle;
                            first = elist.get(i);
                        }
                    }
                }
                if (angle > Math.PI) {
                    w = ww;
                    break;
                }
            }
        }

        Edge currentE = first;

        boolean roundMade = false;

        while (currentE != first || !roundMade) {
            roundMade = true;
            LinkedList<Edge> vIn = v.getElist();
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (int i = 0; i < vIn.size(); i++) {
                Edge e = vIn.get(i);
                Vertex w2 = (e.getBot() == v) ? e.getTop() : e.getBot();
                double angle = (currentE == e) ? 2 * Math.PI : Vertex.getClockwiseAngle(w, v, w2);
                if (nextE == null || minAngle > angle) {
                    nextE = e;
                    minAngle = angle;
                    W2 = w2;
                }
            }
            ext.add(nextE);
            currentE = nextE;
            w = v;
            v = W2;
        }

        //externalEdges = new LinkedList(ext);


        return ext;
    }

}
