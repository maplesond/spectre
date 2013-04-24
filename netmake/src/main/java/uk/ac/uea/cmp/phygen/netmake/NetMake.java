package uk.ac.uea.cmp.phygen.netmake;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.ds.Split;
import uk.ac.uea.cmp.phygen.core.ds.SplitSystem;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriter;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriterFactory;
import uk.ac.uea.cmp.phygen.netmake.edge.Tableau;
import uk.ac.uea.cmp.phygen.netmake.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.phygen.netmake.weighting.TreeWeighting;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weighting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs the NeighborNet algorithm to retrieve a split system and a circular
 * order.
 *
 * @author Sarah Bastkowski
 * See Sarah Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class NetMake {

    // Input class variables
    private final Distances distances;
    private Weighting weighting1;
    private Weighting weighting2;
    private final RunMode mode;
    private final int NB_TAXA;

    // Temporary state variables.
    private Tableau<Integer> components;

    // Output class variables
    private SplitSystem tree;
    private SplitSystem network;

    /**
     * Creates a new NetMake object with a distance matrix and a single weighting.
     * @param distances Distance matrix, which defines distances between taxa
     * @param weighting Weighting system to be applied
     */
    public NetMake(final Distances distances, Weighting weighting) {
        this(distances, weighting, null);
    }

    /**
     * Creates a new NetMake object with a distance matrix and two weightings, for use
     * in a "hybrid" mode.
     * @param  distances Distance matrix, which defines distances between taxa
     * @param weighting1 First weighting system to be applied
     * @param weighting2 Second weighting system to be applied
     */
    public NetMake(final Distances distances, Weighting weighting1, Weighting weighting2) {
        if (distances == null) {
            throw new NullPointerException("Must specify a Distance matrix to work with.");
        }

        this.mode = determineRunMode(weighting1, weighting2);

        if (this.mode == RunMode.UNKNOWN)
            throw new IllegalArgumentException("Unknown run mode configuration.  Please ensure the weighting configuration is valid.");

        // Set input variables
        this.distances = distances;
        this.weighting1 = weighting1;
        this.weighting2 = weighting2;

        this.NB_TAXA = this.distances.size();

        // Initialise state and output variables.
        reset();
    }

    public void save(File outputDir, String prefix) throws IOException {


        PhygenWriter phygenWriter = PhygenWriterFactory.NEXUS.create();

        File networkOutputFile = new File(outputDir, prefix + ".network." + phygenWriter.getExtension());
        File treeOutputFile = new File(outputDir, prefix + ".tree." + phygenWriter.getExtension());

        phygenWriter.writeNetwork(networkOutputFile, this.getNetwork());
        phygenWriter.writeTree(treeOutputFile, this.getTree(), this.getTree().calculateTreeWeighting(this.distances));
    }

    private enum RunMode {

        UNKNOWN {

            public void run(NetMake nn) {
                throw new UnsupportedOperationException("Run Mode was not known");
            }
        },
        NORMAL {

            public void run(NetMake nn) {
                nn.runNN();
            }
        },
        HYBRID {

            public void run(NetMake nn) {
                nn.runNN();
            }
        },
        HYBRID_GREEDYME {

            public void run(NetMake nn) {
                nn.runNN();
            }
        };

        public boolean isHybrid()
        {
            return (this == HYBRID || this == HYBRID_GREEDYME);
        }

        public abstract void run(NetMake nn);
    };



    /**
     * Retrieves the network constructed by NeighborNet
     * @return The network.
     */
    public SplitSystem getNetwork() {
        return network;
    }

    /**
     * Retrieves the tree constructed by NeighborNet
     * @return The tree.
     */
    public SplitSystem getTree() {
        return tree;
    }

    /**
     * Helper method that returns the type of run mode configuration the client
     * has requested.  If unknown, configuration then RunMode.UNKNOWN is returned.
     * @param w1 First weighting to be applied.
     * @param w2 Second weighting system to be applied.
     * @return The RunMode determined by the supplied weighting systems.
     */
    private RunMode determineRunMode(Weighting w1, Weighting w2)
    {
        if (w1 == null) {
            return RunMode.UNKNOWN;
        }
        else if (w2 == null && !isGreedyMEWeighting(w1)) {
            return RunMode.NORMAL;
        }
        else if (isGreedyMEWeighting(w1) && !isGreedyMEWeighting(w2)) {
            return RunMode.HYBRID_GREEDYME;
        }
        else if (!isGreedyMEWeighting(w1) && w2 != null) {
            return RunMode.HYBRID;
        }

        return RunMode.UNKNOWN;
    }

    protected boolean isGreedyMEWeighting(Weighting w)
    {
        if (w == null)
            return false;

        return (w instanceof GreedyMEWeighting);
    }

    /**
     * Resets the output class variables, so the user can re-run NeighbourNet.
     */
    public final void reset() {
        this.network = null;
        this.tree = null;
        this.components = initialiseComponents(this.NB_TAXA);
    }

    /**
     * Create a Tree SplitSystem and a Network SplitSystem by running the NeighborNet
     * algorithm.
     *
     * @return A tree split system
     */
    public void process() {

        this.mode.run(this);
    }

    protected Tableau<Integer> runNN() {
        // Contains the result of the neighbornet process.
        Tableau<Integer> treeSplits = new Tableau<Integer>();
        addTrivialSplits(treeSplits);

        // Distances between components (make deep copy from initial distance matrix)
        Distances c2c = new Distances(this.distances);

        //Distances between components and vertices (make deep copy from initial distance matrix)
        Distances c2v = new Distances(this.distances);

        // If required create a new GreedyME instance.
        GreedyMEWeighting gme = this.mode == RunMode.HYBRID_GREEDYME ? new GreedyMEWeighting(this.distances) : null;

        // Loop until components has only one entry left.
        while (components.rows() > 1) {

            Pair<Integer, Integer> selectedComponents = this.mode == RunMode.HYBRID_GREEDYME ?
                    gme.makeMECherry(treeSplits, this.components) :
                    selectionStep1(c2c);

            int sc1 = selectedComponents.getLeft();
            int sc2 = selectedComponents.getRight();

            Pair<Integer, Integer> selectedVerticies = selectionStep2(c2v, selectedComponents);

            int sv1 = selectedVerticies.getLeft();
            int sv2 = selectedVerticies.getRight();


            //merging of components
            if (components.get(sc1, 0) == sv1) {
                components.reverseRow(sc1);
            }

            if (components.get(sc2, 0) == sv2) {
                components.mergeRows(sc1, sc2);
            } else {
                components.reverseRow(sc2);
                components.mergeRows(sc1, sc2);
            }

            //add new component/split to Split list
            treeSplits.addRow((ArrayList<Integer>) components.getRow(sc1).clone());

            // Update component to component distances (assuming not in GreedyME mode)
            if (this.mode != RunMode.HYBRID_GREEDYME)
                updateC2C(c2c, weighting1);

            // Update component to vertex distances (also updates weighting1 params)
            updateC2V(c2v, this.mode.isHybrid() ? weighting2 : weighting1, selectedComponents);
        }

        // Remove last two rows (last row is the whole set and the last but
        // one is not required)
        treeSplits.removeRow(treeSplits.rows() - 1);
        treeSplits.removeRow(treeSplits.rows() - 1);

        // Create ordering
        int[] permutation = createCircularOrdering();
        organiseSplits(treeSplits, permutation);

        // Set tree split system
        this.tree = treeSplits.convertToSplitSystem(this.distances.size());
        this.tree.setCircularOrdering(permutation);
        this.network = new SplitSystem((List<Split>)null);
        this.network.setCircularOrdering(permutation);


        return treeSplits;
    }

    protected Pair<Integer, Integer> selectionStep1(Distances c2c) {
        double min1 = Double.POSITIVE_INFINITY;
        double[] sum_1 = new double[this.NB_TAXA];
        double[] sum_2 = new double[this.NB_TAXA];

        int sc1 = -1;
        int sc2 = -1;

        for (int i = 0; i < components.rows(); i++) {
            sum_1[i] = 0;
            sum_2[i] = 0;
            for (int j = 0; j < components.rows(); j++) {
                if (j != i) {
                    sum_1[i] = sum_1[i] + c2c.getDistance(i, j);
                    sum_2[i] = sum_2[i] + c2c.getDistance(j, i);
                }
            }
        }

        // first Selection step for Components
        for (int i = 0; i < components.rows(); i++) {
            for (int j = i + 1; j < components.rows(); j++) {
                double qDist = (components.rows() - 2)
                        * c2c.getDistance(i, j)
                        - sum_1[i] - sum_2[j];

                if (qDist < min1) {
                    min1 = qDist;
                    sc1 = i;
                    sc2 = j;
                }
            }
        }

        return new ImmutablePair<Integer, Integer>(sc1, sc2);
    }

    protected Pair<Integer, Integer> selectionStep2(Distances c2v, Pair<Integer, Integer> selectedComponents) {
        double min2 = Double.POSITIVE_INFINITY;
        int selectedVertex1 = -1;
        int selectedVertex2 = -1;

        int sc1 = selectedComponents.getKey();
        int sc2 = selectedComponents.getValue();


        /*
         * second selection step for vertices with
         * ComponentsVerticesSistances
         */
        for (int i = 0; i < components.rowSize(sc1); i++) {
            for (int j = 0; j < components.rowSize(sc2);
                 j++) {
                double sum1 = 0;
                double sum2 = 0;
                double sum3 = 0;
                double sum4 = 0;
                int vertex_last1 = 0;
                int vertex_last2 = 0;

                for (int k = 0; k < components.rows(); k++) {
                    if ((k != sc1)
                            && (k != sc2)) {
                        sum1 += c2v.getDistance(
                                components.get(sc1, i), k);
                    }

                    if ((k != sc1)
                            && (k != sc2)) {
                        sum2 += c2v.getDistance(
                                components.get(sc2, j), k);
                    }
                }

                for (int k = 0; k < components.rowSize(sc2);
                     k++) {
                    sum3 += distances.getDistance(
                            components.get(sc1, i),
                            components.get(sc2, k));

                    if (components.get(sc2, k)
                            != components.get(sc2, j)) {
                        vertex_last2 = components.get(sc2, k);
                    }
                }

                for (int k = 0; k < components.rowSize(sc1);
                     k++) {
                    int vertex1 = components.get(sc1, k);

                    if (vertex1 != components.get(sc1, i)) {
                        vertex_last1 = vertex1;
                    }
                }

                sum3 += distances.getDistance(components.get(sc2, j), vertex_last1);
                sum4 += distances.getDistance(components.get(sc1, i), vertex_last2);

                int outerVertices1 = 0;
                if (components.rowSize(sc1) == 1) {
                    outerVertices1 = 1;
                } else {
                    outerVertices1 = 2;
                }

                int outerVertices2 = 0;
                if (components.rowSize(sc2) == 1) {
                    outerVertices2 = 1;
                } else {
                    outerVertices2 = 2;
                }

                double qDist = (components.rows() - 4 + outerVertices1
                        + outerVertices2) * distances.getDistance(components.get(sc1, i),
                        components.get(sc2, j))
                        - sum1 - sum2 - sum3 - sum4;

                if (qDist < min2) {
                    min2 = qDist;
                    selectedVertex1 = components.get(sc1, i);
                    selectedVertex2 = components.get(sc2, j);
                }

                j = (j == components.rowSize(sc2) - 1) ? components.rowSize(sc2) : components.rowSize(sc2) - 2;
            }

            i = (i == components.rowSize(sc1) - 1) ? components.rowSize(sc1) : components.rowSize(sc1) - 2;
        }

        return new ImmutablePair<Integer, Integer>(selectedVertex1, selectedVertex2);
    }

    protected void updateC2C(Distances c2c, Weighting w) {
        for (int i = 0; i < components.rows(); i++) {
            for (int j = 0; j < components.rows(); j++) {
                if (i == j) {
                    c2c.setDistance(i, j, 0.);
                } else {
                    double aComponentDistance = 0.;

                    for (int k = 0; k < components.rowSize(i); k++) {
                        for (int m = 0; m < components.rowSize(j); m++) {
                            int vertex1 = components.get(i, k);
                            int vertex2 = components.get(j, m);
                            double vertexDistance = w.getWeightingParam(vertex1)
                                    * w.getWeightingParam(vertex2)
                                    * distances.getDistance(vertex1, vertex2);

                            aComponentDistance += vertexDistance;
                        }
                    }
                    c2c.setDistance(i, j, aComponentDistance);
                }
            }
        }
    }

    protected void updateC2V(Distances c2v, Weighting w, Pair<Integer, Integer> selectedComponents) {

        int position = -1;
        int sc1 = selectedComponents.getKey();
        int componentSplitPosition = components.rowSize(sc1);

        for (int i = 0; i < distances.size(); i++) {
            for (int j = 0; j < components.rowSize(sc1);
                 j++) {
                if (i == components.get(sc1, j)) {
                    position = j;

                    if (w instanceof TreeWeighting) {
                        w.updateWeightingParam(i, position,
                                componentSplitPosition);
                    } else {
                        w.updateWeightingParam(i, position,
                                components.rowSize(sc1));
                    }
                }
            }
            for (int j = 0; j < components.rows(); j++) {
                double aComponentVertexDistance = 0.;
                int k = 0;

                while (k < components.rowSize(j)) {
                    if (components.get(j, k) == i) {
                        aComponentVertexDistance = 0.;
                        k = components.rowSize(j);
                    } else {
                        int vertex1 = i;
                        int vertex2 = components.get(j, k);
                        double vertexDistance = w.getWeightingParam(vertex2)
                                * distances.getDistance(vertex1, vertex2);

                        aComponentVertexDistance += vertexDistance;
                        k++;
                    }
                }
                c2v.setDistance(i, j, aComponentVertexDistance);
            }
        }
    }

    protected void organiseSplits(Tableau<Integer> splits, int[] permutation) {
        int[] permutationInvert = new int[this.NB_TAXA];
        for (int i = 0; i < this.NB_TAXA; i++) {
            permutationInvert[permutation[i]] = i;
//            System.out.println("permutation: "+permutation[i]+ " permutationInvert: "+permutationInvert[permutation[i]]);
        }
        for (int i = 0; i < splits.rows(); i++) {
            int k = permutationInvert[splits.get(i, 0)];
            int l = permutationInvert[splits.get(i, splits.rowSize(i) - 1)];

            if (l < k) {
                splits.reverseRow(i);
            }
        }
    }

    protected final Tableau<Integer> initialiseComponents(final int size) {
        Tableau<Integer> c = new Tableau<Integer>();

        for (int i = 0; i < size; i++) {
            c.addRow(i);
        }

        return c;
    }

    protected int[] createCircularOrdering() {
        int[] permutation = new int[distances.size()];
        for (int i = 0; i < components.rowSize(0); i++) {
            permutation[i] = components.get(0, i);
        }

        return permutation;
    }

    protected void addTrivialSplits(Tableau<Integer> splits) {
        for (int i = 0; i < this.NB_TAXA; i++) {
            splits.addRow(i);
        }
    }

}
