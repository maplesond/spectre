package uk.ac.uea.cmp.phygen.netmake.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.SummedDistanceList;

/**
 * Describes the adjacent elements of an edge.
 * @author Sarah Bastkowski
 */
public class EdgeAdjacents {

    private final static Logger log = LoggerFactory.getLogger(EdgeAdjacents.class);


    private int leaves_in_adjacents[];
    private SummedDistanceList temp_sdl;
    private int split_point;

    public EdgeAdjacents(int[] leaves_in_adjacents, SummedDistanceList temp_sdl, int split_point) {
        this.leaves_in_adjacents = leaves_in_adjacents;
        this.temp_sdl = temp_sdl;
        this.split_point = split_point;
    }

    public int[] getNumberOfLeavesInAdjacents() {
        return leaves_in_adjacents;
    }

    public SummedDistanceList getTempSDL() {
        return temp_sdl;
    }

    public void setSplitPoint(int split_point) {
        this.split_point = split_point;
    }

    public void setNumberOfLeavesInAdjacents(int[] leaves_in_adjacents) {
        this.leaves_in_adjacents = leaves_in_adjacents;
    }

    public void setTempSDL(SummedDistanceList temp_sdl) {
        this.temp_sdl = temp_sdl;
    }

    public int getSplitPoint() {
        return split_point;
    }

    /**
     * Creates an object EdgeAdjacents, so C, pTemp and C_alpha will be determined
     *
     * @param splitsCopy The split list
     * @param k The index of particular split we are dealing with
     * @param P
     * @return A new EdgeAdjacents object containing C, pTemp and C_alpha values.
     */
    public static EdgeAdjacents retrieveAdjacents(Tableau<Integer> splitsASide, int k, SummedDistanceList sdl, int nbTaxa){

        SummedDistanceList sdlCopy = new SummedDistanceList(sdl);
        sdl.addAll(sdlCopy);

        TableauSplits splits = new TableauSplits(splitsASide, nbTaxa);

        // Get the current split and sort it
        Edge edge = new Edge();
        edge.addAll(splits.getRow(TableauSplits.SplitSide.A_SIDE, k));
        edge.sort();
        log.debug("Edge_a: {0}", edge);
        Edge edge_b = new Edge(splits.getBSide().getRow(k));
        edge_b.sort();


        Tableau<Integer> combinedSplitList = splits.combineSides();
        // Also we want to sort the elements in all rows of both a_side and b_side 
        // of the split object.
        splits.sortElementsInAllRows();

//        if (Utils.VERBOSE)
//            splits.print();

        EdgeSubsetFinder.SubsetList a_side = null;
        boolean internalEdge = edge.getType() == Edge.EdgeType.INTERNAL;
        if (internalEdge) {
            Edge edge_a = new Edge(edge);

            EdgeSubsetFinder esf = new EdgeSubsetFinder(combinedSplitList, sdl, edge_a);
            a_side = esf.process();
        }


        //Getting the B_side

        //First we have to get the elements on the B side A|B all taxa/leafs x \in B
        //these are stores in edge_b


        EdgeSubsetFinder esf_b = new EdgeSubsetFinder(combinedSplitList, sdl, edge_b);
        EdgeSubsetFinder.SubsetList b_side = esf_b.process();


        if (internalEdge) {
            log.debug(a_side.toString("A"));
        }

        log.debug(b_side.toString("B"));

//        System.out.println("A_side: " + A_side.rows() + " B_side: " + B_side.rows());

        int offset = (edge.getType() == Edge.EdgeType.EXTERNAL) ? 0 : a_side.size();
        int C[] = new int[offset + b_side.size()];
//        System.out.println("A_side Row size: "+A_side.rows());
        if (internalEdge) {
            for (int i = 0; i < a_side.size(); i++) {
//            A_side.set(i, A_side.get(i).replaceAll(" ", ""));
//            String help2[] = C_order.get(i).trim().split(" ");
//            C[i] = help2.length;
//            System.out.println("C lenght " + A_side.get(i).length());
                C[i] = a_side.get(i).size();
            }
        }


        for (int i = 0; i < b_side.size(); i++) {
//            splitsCopy.set(i, splitsCopy.get(i).replaceAll(" ", ""));
//            String help2[] = C_order.get(i).trim().split(" ");
//            C[i] = help2.length;
            C[i + offset] = b_side.get(i).size();
        }
// for (int i = 0; i < C.length; i++) {
//
//            System.out.println("C_i: "+C[i]);
//        }
        SummedDistanceList ptemp = new SummedDistanceList();
        if (internalEdge) {
            for (EdgeSubsetFinder.Subset ss : a_side) {

                ptemp.add(ss.getSummedDistance());

            }
        }
        for (EdgeSubsetFinder.Subset ss : b_side) {

            ptemp.add(ss.getSummedDistance());

        }

        EdgeAdjacents aEdgeAdjacents = new EdgeAdjacents(C, ptemp, offset);

        return aEdgeAdjacents;

    }

}

