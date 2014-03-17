package uk.ac.uea.cmp.spectre.core.alg;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;

/**
 * Represents a NeighborNet node.  Each node is part of a NetworkLayer, which is managed as a doubly linked list.  Each
 * NetworkNode also can point at up to two child nodes.
 * <p/>
 * For simplicity and speed we have not encapsulated the members of this class.
 */
class NetworkNode {

    protected Identifier id;
    protected NetworkNode adjacent;
    protected NetworkNode child1;
    protected NetworkNode child2;
    protected double Rx;
    protected double Sx;

    public NetworkNode(Identifier id) {
        this.id = id;
        this.adjacent = null;
        this.child1 = null;
        this.child2 = null;
        this.Rx = 0;
        this.Sx = 0;
    }


    @Override
    public String toString() {
        String str = "[id=" + id.getId();
        str += " adjacent=" + (adjacent == null ? "null" : ("" + adjacent.id));
        str += " child1=" + (child1 == null ? "null" : ("" + child1.id));
        str += " child2=" + (child2 == null ? "null" : ("" + child2.id));
        str += " Rx=" + Rx;
        str += " Sx=" + Sx;
        str += "]";
        return str;
    }
}
