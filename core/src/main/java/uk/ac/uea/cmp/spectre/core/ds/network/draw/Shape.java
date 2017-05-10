package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.EdgeList;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;

/**
 * Collection of shape structures useful for drawing the network
 * Created by maplesod on 10/05/17.
 */
public class Shape {
    public static class Triangle {
        public int splitidx;
        public EdgeList edges;

        public Triangle(int splitidx, EdgeList edges) {
            this.splitidx = splitidx;
            this.edges = edges;
        }
    }

    public static class Cube {

        Edge e = null;
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
        Edge h1 = null;
        Edge h2 = null;
        Edge h3 = null;

        boolean flippable = false;
        boolean valid = false;

        DrawSplitSystem.Flip flip;

        public Cube() {}

        public Cube(Edge e, DrawSplitSystem.Flip flip) {
            this.flippable = false;
            this.valid = false;
            this.flip = flip;
            this.e = e;
            if (flip == DrawSplitSystem.Flip.UP) {
                v = e.getTop();
                if (v.getEdgeList().size() == 3) {
                    e1 = v.getEdgeList().get((v.getEdgeList().indexOf(e) + 2) % 3);
                    e2 = v.getEdgeList().getNextEdge(e);
                    v5 = e.getBottom();
                    v1 = v == e2.getBottom() ? e2.getTop() : e2.getBottom();
                    v3 = v == e1.getBottom() ? e1.getTop() : e1.getBottom();
                    e3 = v3.getEdgeList().getNextEdge(e1);
                    e4 = v1.getEdgeList().getPreviousEdge(e2);
                    e5 = v1.getEdgeList().getNextEdge(e2);
                    e6 = v5.getEdgeList().getPreviousEdge(e);
                    e7 = v5.getEdgeList().getNextEdge(e);
                    e8 = v3.getEdgeList().getPreviousEdge(e1);
                    v2 = v1 == e4.getBottom() ? e4.getTop() : e4.getBottom();
                    v4 = v3 == e3.getBottom() ? e3.getTop() : e3.getBottom();
                    v6 = v3 == e8.getBottom() ? e8.getTop() : e8.getBottom();
                    v7 = v5 == e7.getBottom() ? e7.getTop() : e7.getBottom();
                    v8 = v5 == e6.getBottom() ? e6.getTop() : e6.getBottom();
                    v9 = v1 == e5.getBottom() ? e5.getTop() : e5.getBottom();
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.getSplitIndex() == e5.getSplitIndex())
                            && (e.getSplitIndex() == e8.getSplitIndex())
                            && (e2.getSplitIndex() == e3.getSplitIndex())
                            && (e2.getSplitIndex() == e6.getSplitIndex())
                            && (e1.getSplitIndex() == e4.getSplitIndex())
                            && (e1.getSplitIndex() == e7.getSplitIndex())) {
                        this.flippable = true;
                    }
                    this.valid = true;
                }
            }
            else {
                v = e.getBottom();
                if (v.getEdgeList().size() == 3) {
                    e1 = v.getEdgeList().get((v.getEdgeList().indexOf(e) + 2) % 3);
                    e2 = v.getEdgeList().getNextEdge(e);
                    v1 = v == e2.getBottom() ? e2.getTop() : e2.getBottom();
                    v3 = v == e1.getBottom() ? e1.getTop() : e1.getBottom();
                    v5 = e.getTop();
                    e3 = v3.getEdgeList().getNextEdge(e1);
                    e4 = v1.getEdgeList().getPreviousEdge(e2);
                    e5 = v1.getEdgeList().getNextEdge(e2);
                    e6 = v5.getEdgeList().getPreviousEdge(e);
                    e7 = v5.getEdgeList().getNextEdge(e);
                    e8 = v3.getEdgeList().getPreviousEdge(e1);
                    v2 = v1 == e4.getBottom() ? e4.getTop() : e4.getBottom();
                    v4 = v3 == e3.getBottom() ? e3.getTop() : e3.getBottom();
                    v6 = v3 == e8.getBottom() ? e8.getTop() : e8.getBottom();
                    v7 = v5 == e7.getBottom() ? e7.getTop() : e7.getBottom();
                    v8 = v5 == e6.getBottom() ? e6.getTop() : e6.getBottom();
                    v9 = v1 == e5.getBottom() ? e5.getTop() : e5.getBottom();
                    if ((v2 == v4)
                            && (v6 == v7)
                            && (v8 == v9)
                            && (e.getSplitIndex() == e5.getSplitIndex())
                            && (e.getSplitIndex() == e8.getSplitIndex())
                            && (e2.getSplitIndex() == e3.getSplitIndex())
                            && (e2.getSplitIndex() == e6.getSplitIndex())
                            && (e1.getSplitIndex() == e4.getSplitIndex())
                            && (e1.getSplitIndex() == e7.getSplitIndex())) {
                        this.flippable = true;
                    }
                    this.valid = true;
                }
            }
        }

        public void flip() {
            v1.getEdgeList().remove(e2);
            v3.getEdgeList().remove(e1);
            v5.getEdgeList().remove(e);
            v.getEdgeList().clear();
            v.setX(v2.getX() + (v5.getX() - v.getX()));
            v.setY(v2.getY() + (v5.getY() - v.getY()));
            h1 = v8 == e6.getTop() ?
                    new Edge(v, v6, e6.getSplitIndex(), e2.getTimestp()) :
                    new Edge(v6, v, e6.getSplitIndex(), e2.getTimestp());
            v6.getEdgeList().add(v6.getEdgeList().indexOf(e7) + 1, h1);
            v.getEdgeList().addLast(h1);
            h2 = v6 == e7.getTop() ?
                    new Edge(v, v8, e7.getSplitIndex(), e1.getTimestp()) :
                    new Edge(v8, v, e7.getSplitIndex(), e1.getTimestp());
            v8.getEdgeList().add(v8.getEdgeList().indexOf(e5) + 1, h2);
            v.getEdgeList().addLast(h2);
            h3 = new Edge(v2, v, e.getSplitIndex(), e.getTimestp());
            v2.getEdgeList().add(v2.getEdgeList().indexOf(e3) + 1, h3);
            v.getEdgeList().addLast(h3);
        }

        public void update(boolean parta, Split.Direction dir, int s, int a, int b, EdgeList edges, EdgeList crossboth, EdgeList elista, EdgeList elistb) {

            if ((parta && dir == Split.Direction.LEFT) ||
                    (!parta && dir == Split.Direction.RIGHT)) {

                edges.add(edges.indexOf(e) + 1, h3);
                edges.remove(e);
                if ((e6.getSplitIndex() == a) && (e7.getSplitIndex() != s)) {
                    edges.removeFirst();
                    crossboth.add(crossboth.indexOf(e5) + 1, h3);
                    crossboth.remove(e5);
                    elista.add(elista.indexOf(e2) + 1, h1);
                    elista.remove(e2);
                }
                if ((e6.getSplitIndex() == a) && (e7.getSplitIndex() == s) && (s == b)) {
                    edges.removeFirst();
                    crossboth.remove(e5);
                    crossboth.add(crossboth.indexOf(e4) + 1, h2);
                    crossboth.remove(e4);
                    elista.remove(e2);
                    elistb.remove(e1);
                }
                if ((e6.getSplitIndex() == a) && (e7.getSplitIndex() == s) && (s != b)) {
                    edges.removeFirst();
                    crossboth.add(crossboth.indexOf(e4) + 1, h3);
                    crossboth.remove(e4);
                    crossboth.add(crossboth.indexOf(e5) + 1, h2);
                    crossboth.remove(e5);
                    elista.add(elista.indexOf(e2) + 1, h1);
                    elista.remove(e2);
                }
                if ((e6.getSplitIndex() != a) && (e7.getSplitIndex() == s) && (s == b)) {
                    edges.removeLast();
                    elistb.add(elistb.indexOf(e1) + 1, h2);
                    elistb.remove(e1);
                }
                if ((e6.getSplitIndex() != a) && (e7.getSplitIndex() == s) && (s != b)) {
                    edges.removeLast();
                }
            }
            else if ((parta && dir == Split.Direction.RIGHT) ||
                    (!parta && dir == Split.Direction.LEFT)) {
                edges.add(edges.indexOf(e) + 1, h3);
                edges.remove(e);

                if ((e7.getSplitIndex() == a) && (e6.getSplitIndex() != s)) {
                    edges.removeFirst();
                    crossboth.add(crossboth.indexOf(e8) + 1, h3);
                    crossboth.remove(e8);
                    elista.add(elista.indexOf(e1) + 1, h2);
                    elista.remove(e1);
                }
                if ((e7.getSplitIndex() == a) && (e6.getSplitIndex() == s) && (s == b)) {
                    edges.removeFirst();
                    crossboth.remove(e8);
                    crossboth.add(crossboth.indexOf(e3) + 1, h1);
                    crossboth.remove(e3);
                    elista.remove(e1);
                    elistb.remove(e2);
                }
                if ((e7.getSplitIndex() == a) && (e6.getSplitIndex() == s) && (s != b)) {
                    edges.removeFirst();
                    crossboth.add(crossboth.indexOf(e3) + 1, h3);
                    crossboth.remove(e3);
                    crossboth.add(crossboth.indexOf(e8) + 1, h1);
                    crossboth.remove(e8);
                    elista.add(elista.indexOf(e1) + 1, h2);
                    elista.remove(e1);
                }
                if ((e7.getSplitIndex() != a) && (e6.getSplitIndex() == s) && (s == b)) {
                    edges.removeLast();
                    elistb.add(elistb.indexOf(e2) + 1, h1);
                    elistb.remove(e2);
                }
                if ((e7.getSplitIndex() != a) && (e6.getSplitIndex() == s) && (s != b)) {
                    edges.removeLast();
                }
            }
            else {
                throw new IllegalStateException("Unexpected combination of directions!");
            }

        }



    }
}
