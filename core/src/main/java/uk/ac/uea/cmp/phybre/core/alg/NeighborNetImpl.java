package uk.ac.uea.cmp.phybre.core.alg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phybre.core.ds.Identifier;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phybre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.phybre.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.phybre.core.ds.split.SimpleSplitSystem;
import uk.ac.uea.cmp.phybre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phybre.core.ds.split.SplitUtils;
import uk.ac.uea.cmp.phybre.core.math.tuple.Triplet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements NeighborNet {


    @Override
    public CompatibleSplitSystem execute(DistanceMatrix distanceMatrix, NeighborNetParams params) {

        // Make a shortcut to the taxa used in the distance matrix
        IdentifierList taxa = distanceMatrix.getTaxa();

        // Creates a set of trivial splits for ...
        ConnectedComponents components = new ConnectedComponents(taxa);

        // Creates a simple split system with trivial splits from the given distance matrix
        SplitSystem treeSplits = new SimpleSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));


        // DistanceMatrix between components (make deep copy from initial distance matrix)
        DistanceMatrix c2c = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between components and vertices (make deep copy from initial distance matrix)
        DistanceMatrix c2v = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between vertices and vertices (make deep copy from initial distance matrix)
        DistanceMatrix v2v = new FlexibleDistanceMatrix(distanceMatrix);

        // Choose a pair of components that minimise the Q criterion
        Pair<Identifier, Identifier> selectedComponents = this.selectionStep1(c2c);

        Pair<Identifier, Identifier> selectedTaxon = this.selectionStep2(selectedComponents, c2v, v2v, components);


        return null;
    }


    /**
     * Choose a pair of components that minimise the Q criterion
     * @param c2c components
     * @return a pair of components that minimise the Q criterion
     */
    protected Pair<Identifier, Identifier> selectionStep1(final DistanceMatrix c2c) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        for(Map.Entry<Pair<Identifier,Identifier>, Double> entry : c2c.getMap().entrySet()) {

            Identifier id1 = entry.getKey().getLeft();
            Identifier id2 = entry.getKey().getRight();

            final double id1_2_id2 = c2c.getDistance(id1, id2);

            final double sumId1 = c2c.getDistances(id1, null).sum() - id1_2_id2;
            final double sumId2 = c2c.getDistances(id2, null).sum() - id1_2_id2;

            final double q = (c2c.size() - 2) * c2c.getDistance(id1, id2) - sumId1 - sumId2;

            if (q < minQ) {
                minQ = q;
                bestPair = entry.getKey();
            }
        }

        return bestPair;
    }


    protected Pair<Identifier, Identifier> selectionStep2(Pair<Identifier, Identifier> selectedComponents, DistanceMatrix c2v,
                                                DistanceMatrix v2v, ConnectedComponents components) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        IdentifierList component1 = components.get(selectedComponents.getLeft());
        IdentifierList component2 = components.get(selectedComponents.getRight());

        IdentifierList union = new IdentifierList();
        union.addAll(component1);
        union.addAll(component2);

        for(Identifier id1 : component1) {

            for(Identifier id2 : component2) {

                final double sumId1 = c2v.getDistances(id1, null).sum() - c2v.getDistance(id1, selectedComponents.getRight());
                final double sumId2 = c2v.getDistances(id2, null).sum() - c2v.getDistance(id2, selectedComponents.getLeft());

                double sumVId1 = 0.0;
                double sumVId2 = 0.0;

                for(Identifier id3 : union) {
                    sumVId1 += c2v.getDistance(id1, id3);
                    sumVId2 += c2v.getDistance(id2, id3);
                }

                double q = ((components.size() - 4 + component1.size() + component2.size()) * v2v.getDistance(id1, id2)) -
                        sumId1 - sumId2 - sumVId1 - sumVId2;

                if (q < minQ) {
                    minQ = q;
                    bestPair = new ImmutablePair<>(id1, id2);
                }
            }
        }

        return bestPair;
    }



    /**
     * Reduces the 3 selected vertices down to 2 new vertices.  The output takes the form of an updated distance
     * matrix represented by v2v and an updated split system
     * @param vertices The selected vertices to reduce
     * @param params alpha, beta and gamma parameters
     * @param v2v the distance matrix to update based on this reduction
     */
    protected void reduction(final Triplet<Integer> vertices, NeighborNetParams params,
                             DistanceMatrix v2v) {

        final int maxId = v2v.getTaxa().getMaxId();

        // Create two new taxa for the reduced
        Identifier newVertex1 = new Identifier(Integer.toString(maxId+1), maxId+1);
        Identifier newVertex2 = new Identifier(Integer.toString(maxId+2), maxId+2);

        v2v.getTaxa().add(newVertex1);
        v2v.getTaxa().add(newVertex2);

        final Identifier tA = v2v.getTaxa().getById(vertices.getA());
        final Identifier tB = v2v.getTaxa().getById(vertices.getB());
        final Identifier tC = v2v.getTaxa().getById(vertices.getC());

        // Iterate over all active vertices
        for(Identifier vertex : v2v.getTaxa()) {

            // Only process this vertex if it is not in the selected vertex list
            if (vertex != tA && vertex != tB && vertex != tC) {

                v2v.setDistance(newVertex1, vertex,
                                ((params.getAlpha() + params.getBeta()) * v2v.getDistance(tA, vertex)) +
                                (params.getGamma() * v2v.getDistance(tB, vertex)));

                v2v.setDistance(newVertex2, vertex,
                                (params.getAlpha() * v2v.getDistance(tB, vertex)) +
                                ((1 - params.getAlpha()) * v2v.getDistance(tC, vertex)));

                v2v.setDistance(newVertex1, newVertex2,
                                (params.getAlpha() * v2v.getDistance(tA, tB)) +
                                (params.getBeta() * v2v.getDistance(tA, tC)) +
                                (params.getGamma() * v2v.getDistance(tB, tC)));
            }
        }

        // Remove the selected vertices from v2v
        v2v.removeTaxon(vertices.getA());
        v2v.removeTaxon(vertices.getB());
        v2v.removeTaxon(vertices.getC());
    }
}
