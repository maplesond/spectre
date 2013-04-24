package uk.ac.uea.cmp.phygen.netmake.edge;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class Edge extends ArrayList<Integer>
{
    public Edge()
    {
        super();
    }

    public Edge(Edge copy)
    {
        this();

        for(Integer i : copy)
        {
            this.add(new Integer(i.intValue()));
        }
//        this.addAll(copy);
    }

    public Edge(ArrayList<Integer> copy)
    {
        this();
        this.addAll(copy);
    }

    public void sort()
    {
        Collections.sort(this);
    }

    public EdgeType getType()
    {
        return this.size() == 1 ? EdgeType.EXTERNAL : EdgeType.INTERNAL;
    }

    public enum EdgeType
    {
        EXTERNAL,
        INTERNAL
    }
}

