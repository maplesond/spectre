package uk.ac.uea.cmp.phygen.netmake.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.SummedDistanceList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 22:20
 * To change this template use File | Settings | File Templates.
 */
public class EdgeSubsetFinder {

    private final static Logger log = LoggerFactory.getLogger(EdgeSubsetFinder.class.getName());

    private Tableau t;
    private SummedDistanceList sdl;
    private Edge edge;

    /**
     *
     * @param t
     * @param sdl
     * @param edge
     */
    public EdgeSubsetFinder(Tableau t, SummedDistanceList sdl, Edge edge)
    {
        this.t = t;
        this.edge = edge;
        this.sdl = sdl;
    }

    public SubsetList process()
    {
        Edge e = new Edge(this.edge);
        SubsetList result = new SubsetList();

        boolean first_run = true;

        while(e.size() > 0)
        {
            Subset ss = getLargestSubset(e, first_run);
            result.add(ss);
            first_run = false;
        }

        return result;
    }

    private Subset getLargestSubset(Edge e, boolean first_run)
    {
        Subset result = null;


        int largest_subset = -1;

        for(int i = 0; i < t.rows(); i++)
        {
            if (e.size() == 1)
            {
                result = new Subset(sdl.get(e.get(0)), e);
                // Should be exactly 1 row in the Tableau... otherwise this doesn't work!
                e.removeAll(result);
                return result;
            }

            // Edge must be larger than the current row for the current row
            // to be considered
            if (e.size() < t.rowSize(i))
            {
                // do something in this case
                // probably not interested in this row?
                continue;
            }

            if ((e.size() == t.rowSize(i)) && first_run)
            {
                continue;
            }

            // edge must contain all the element from the current row
            if (e.containsAll(t.getRow(i)))
            {
                if (largest_subset == -1)
                    largest_subset = i;
                else
                {
                    int largest_subset_size = t.rowSize(largest_subset);
                    int current_row_size = t.rowSize(i);

                    if (current_row_size <= largest_subset_size)
                    {
                        // Not interested?
                        continue;
                    }
                    else
                    {
                        largest_subset = i;
                    }
                }
            }
        }

        log.debug("P: {0}", sdl.get(largest_subset));

        result = new Subset(sdl.get(largest_subset), t.copyRow(largest_subset));

        // Should be exactly 1 row in the Tableau... otherwise this doesn't work!
        e.removeAll(result);

        return result;
    }


    public static class SubsetList extends ArrayList<Subset>
    {

        public String toString(String prefix)
        {
            StringBuilder sb = new StringBuilder();

            for (Subset ss : this) {
                sb.append(prefix).append(": ").append(ss).append("\n");
            }

            return sb.toString();
        }
    }

    public static class Subset extends ArrayList<Integer>
    {
        private double summed_distance;

        public Subset()
        {
            this(-1.0, new ArrayList<Integer>());
        }

        public Subset(double summed_distance, List<Integer> elements)
        {
            super();
            this.summed_distance = summed_distance;
            this.addAll(elements);
        }

        public Subset(Subset copy)
        {
            this();
            this.addAll(copy);
        }

        public double getSummedDistance() {
            return summed_distance;
        }
    }
}