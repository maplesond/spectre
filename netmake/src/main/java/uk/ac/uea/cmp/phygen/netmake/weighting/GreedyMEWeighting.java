package uk.ac.uea.cmp.phygen.netmake.weighting;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.ds.SplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.SummedDistanceList;
import uk.ac.uea.cmp.phygen.netmake.edge.EdgeAdjacents;
import uk.ac.uea.cmp.phygen.netmake.edge.Tableau;

import java.util.ArrayList;
import java.util.List;

/**
 * GreedyMEWeighting extends weighting, although it doesn't actually update a Weighting param.
 * Explanation....
 * @author Sarah Bastkowski
 */
public class GreedyMEWeighting extends Weighting {

    private final static Logger log = LoggerFactory.getLogger(GreedyMEWeighting.class);

    // Input class variables.
    private Distances distances;

    // Output class variables
    private Pair<Integer, Integer> bestSplits;

    /**
     * Initialises a GreedyMEWeighting with a SplitSystem
     * @param distances
     */
    public GreedyMEWeighting(Distances distances) {

        super();

        this.distances = distances;

        this.bestSplits = null;
    }


    public Pair<Integer, Integer> makeMECherry(Tableau<Integer> splits, final Tableau<Integer> splitsCreation) {
        double oldTreeLength = Double.POSITIVE_INFINITY;
        for (int i = 0; i < splitsCreation.rows() - 1; i++) {
            for (int j = i + 1; j < splitsCreation.rows(); j++) {

                splits.addRow(splitsCreation.copyRow(i));
                splits.addRow(splitsCreation.copyRow(j));
                splits.mergeRows(splits.rows() - 2, splits.rows() - 1);

                double treeLength = this.calculateTreeLength(splits);
                log.debug("Treelength: {0}", treeLength);

                if (treeLength < oldTreeLength) {
                    oldTreeLength = treeLength;
                    this.bestSplits = new ImmutablePair<Integer, Integer>(i, j);
                }

                splits.removeRow(splits.rows() - 1);
            }
        }

        return bestSplits;
    }

    private double calculateTreeLength(Tableau<Integer> splits) {

        double treeLength = 0.0;

        List<Double> edgeWeights = this.getEdgeWeights(splits);

        for(Double i : edgeWeights) {
            treeLength += i.doubleValue();
        }
        return treeLength;
    }

    /*
     * method to calculate a split weight
     * input is an arrayList of splits representing the tree topology,
     * the relevant distances d and the number of the split, the weight is calculated for
     */
    private double calculateEdges(double P_0, EdgeAdjacents aEdgeAdjacents, boolean external) {

        double edgeWeight = 0;
        int nb_taxa = this.distances.size();

//        boolean external = false;
        log.debug("P_0: {0}", P_0);

        int C[] = aEdgeAdjacents.getNumberOfLeavesInAdjacents();

        SummedDistanceList P = aEdgeAdjacents.getTempSDL();

        log.debug("P: {0}", P.toString());

        int C_alpha = aEdgeAdjacents.getSplitPoint();

        int v[] = new int[C.length];
        double s[] = new double[C.length];
        double w[] = new double[C.length];

        int n_alpha = 0, n_beta = 0;
        double k = 1;
        double gamma = 0;
        int zero = -1;

        double d[] = new double[C.length];

        for (int i = 0; i < C.length; i++) {
//            System.out.println("C : " + C[i]);
            d[i] = (double) nb_taxa / (double) C[i] - 2.0;
        }



        if (external == true) {
            for (int i = 0; i < C.length; i++) {
                v[i] = 1;
            }
            n_alpha = (nb_taxa - 1);
            n_beta = 1;
            C_alpha = C.length;
//            System.out.println("external edge");
        } else {
            //Check if C_alpha is right index
            for (int i = 0; i < C_alpha; i++) {
                n_alpha += C[i];
            }

            for (int i = C_alpha; i < C.length; i++) {
                n_beta += C[i];
            }

            for (int i = 0; i < C_alpha; i++) {
                v[i] = n_beta;
            }

            for (int i = C_alpha; i < C.length; i++) {
                v[i] = n_alpha;
            }
        }


//        for (int i = 0; i < C.length; i++) {
//            System.out.println("Vector v: " + v[i]);
//        }
        //Determine if edge is external or internal

//        if(C_alpha == 1 || C_alpha == C.length){
//           external = true;
//        }

        //Matrix inversion for X^-1 (see D.Bryant thesis page 153)
        for (int i = 0; i < C.length; i++) {
            if (C[i] == (double) nb_taxa / 2.0) {
                s[i] = 0;
                zero = i;

            } else {
                log.debug("this.nb_taxa: {0} Ci: {1}", new Object[]{nb_taxa, C[i]});
                s[i] = (double) C[i] / ((double) nb_taxa - 2.0 * (double) C[i]);
            }
        }

//        for (int i = 0; i < C.length; i++) {
//            System.out.println("s: " + s[i]);
//        }

        for (int i = 0; i < C.length; i++) {
            k += s[i];
        }
        log.debug("c-alpha: {0}", C_alpha);
        for (int i = 0; i < C_alpha; i++) {
            gamma += n_beta * s[i];
        }
        for (int i = C_alpha; i < C.length; i++) {

            gamma += n_alpha * s[i];
        }


//        System.out.println("nbeta: " + n_beta + " nalpha: " + n_alpha);
//        System.out.println("k: " + k + " gamma: " + gamma);
//there is no zero in the matrix "D" (notation refers to D.Bryant thesis algorithm 15
        if (zero == -1) {
            for (int i = 0; i < C.length; i++) {
                w[i] = (- 1 / ((double) nb_taxa / (double) C[i] - 2.0)) * (gamma / k - (double) v[i]);
            }
            //there is a zero at position zero
        } else {
            for (int i = 0; i < zero; i++) {
                w[i] = (1.0 / d[i]) * (v[i] - v[zero]);
            }
            w[zero] = (-gamma) + k * v[zero];
            for (int i = zero + 1; i < C.length; i++) {
                w[i] = (1.0 / d[i]) * (v[i] - v[zero]);
                log.debug("v : {0}", v[i]);
            }

        }

        double sum_1 = 0, sum_2 = 0, sum_3 = 0;

        for (int i = 0; i < C.length; i++) {
            //System.out.println("P " + i + ": " + P.get(i) + " C " + i + ": " + C[i] + " w " + i + ": " + w[i]);

            sum_1 += (w[i] * P.get(i)) / (double) C[i];
        }
        log.debug("sum1: {0}", sum_1);

        for (int i = 0; i < C_alpha; i++) {
            sum_2 += n_beta * w[i];
        }
        log.debug("n_alpha: {0}", n_alpha);
        log.debug("n_beta: {0}", n_beta);

        log.debug("sum2: {0}", sum_2);
        for (int i = C_alpha; i < C.length; i++) {
            sum_3 += n_alpha * w[i];
        }
        log.debug("sum3: {0}", sum_3);
        //edge length calculation
        edgeWeight = (P_0 - sum_1);


        if (edgeWeight != 0) {

            double part2 = sum_2 + sum_3;

//            if (n_alpha == 0.0 || part2 == 0.0) {
//                log.log(Level.FINE, "Oh noes");
//            }



            edgeWeight = edgeWeight / (n_alpha * n_beta - part2);

            if (Double.isInfinite(edgeWeight) || Double.isInfinite(part2)) {
                log.warn("Oh noes");
            }
        }

        if (Double.isInfinite(edgeWeight)) {
            throw new IllegalArgumentException("Edge Weight was infinite!");
        }
        if (Double.isNaN(edgeWeight)) {
            throw new IllegalArgumentException("Edge Weight was not a number");
        }

        //Gives INF cus denominator GIVES ZERO SOMETIMES :(
//        System.out.println("edge: " + edgeWeight);
        return edgeWeight;
    }


    public List<Double> getEdgeWeights(Tableau<Integer> tableau) {

        ArrayList<Double> edgeWeights = new ArrayList<Double>();

        SplitSystem splitSystem = tableau.convertToSplitSystem(this.distances.size());

        SummedDistanceList P = splitSystem.calculateP(this.distances);

        for (int i = 0; i < splitSystem.getNbSplits(); i++) {

            Tableau<Integer> splitsCopy = new Tableau<Integer>(tableau);

            boolean external = splitSystem.getSplitAt(i).onExternalEdge();

            EdgeAdjacents aEdgeAdjacents = EdgeAdjacents.retrieveAdjacents(splitsCopy, i, P, this.distances.size());

            //determine if edge external or internal

//            System.out.println("P_0: " + calculateP_0(splits.getRow(i)));
            edgeWeights.add(calculateEdges(P.get(i), aEdgeAdjacents, external));
        }

        return edgeWeights;
    }

    /**
     * Will always throw an UnsupportedOperationException because this GreedyMEWeighting class
     * is not technically a Weighting but is sometimes used in a Weighting object's
     * place in NeighborNet
     * @param i
     * @param position
     * @param customParameter
     */
    @Override
    public void updateWeightingParam(int i, int position, int customParameter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

