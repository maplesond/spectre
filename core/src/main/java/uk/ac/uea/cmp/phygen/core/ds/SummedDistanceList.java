package uk.ac.uea.cmp.phygen.core.ds;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 *
 * @author Sarah Bastkowski
 */
public class SummedDistanceList extends ArrayList<Double> {

    public SummedDistanceList()
    {
        super();
    }


    public SummedDistanceList(double[] sdl)
    {
        for(double sd : sdl)
        {
            this.add(new Double(sd));
        }
    }

    public SummedDistanceList(SummedDistanceList copy)
    {
        this();

        //Maybe this is unnecessary and could simply be done with a call to copy.addAll()
        //but just to be sure...
        for(Double sd : copy)
        {
            this.add(Double.valueOf(sd));
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(StringUtils.join(this, ","));
        sb.append("]");
        return sb.toString();
    }
}
