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
import uk.ac.uea.cmp.phybre.core.math.tuple.Triplet;

import java.util.Map;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements NeighborNet {


    @Override
    public CompatibleSplitSystem execute(DistanceMatrix distanceMatrix, NeighborNetParams params) {

        // Make a shortcut to the taxa used in the distance matrix
        IdentifierList taxa = distanceMatrix.getTaxa();

        // Creates a trivial network for the taxa specified in the distance matrix
        Network network = new Network(taxa);

        // Creates a simple split system with trivial splits from the given distance matrix
        SplitSystem treeSplits = new SimpleSplitSystem(taxa);

        // DistanceMatrix between components (make deep copy from initial distance matrix)
        DistanceMatrix c2c = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between components and vertices (make deep copy from initial distance matrix)
        DistanceMatrix c2v = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between vertices and vertices (make deep copy from initial distance matrix)
        DistanceMatrix v2v = new FlexibleDistanceMatrix(distanceMatrix);

        // Reduce down to 2 nodes
        while (network.size() >= 2) {

            // Choose a pair of components that minimise the Q criterion
            Pair<Identifier, Identifier> selectedVertices1 = this.selectionStep1(c2c);

            // Both should be 1 or 2 components in length
            IdentifierList component1 = network.get(selectedVertices1.getLeft());
            IdentifierList component2 = network.get(selectedVertices1.getRight());

            IdentifierList componentUnion = new IdentifierList();
            componentUnion.addAll(component1);
            componentUnion.addAll(component2);

            // Choose a pair of taxa that minimise our formula
            Pair<Identifier, Identifier> selectedVertices2 = this.selectionStep2(selectedVertices1, c2v, v2v, component1, component2, componentUnion);

            // Merges selected components and reduces components of size > 2 to components of size 2
            Pair<Identifier, Identifier> mergedVertices = this.merge(params, selectedVertices2, component1, component2, componentUnion, v2v);

            // Remove the selected components from step 1 from connected components
            network.remove(selectedVertices1.getLeft());
            network.remove(selectedVertices1.getRight());

            // Add the merged vertices to the connected components
            IdentifierList mergedSet = new IdentifierList();
            mergedSet.add(mergedVertices.getLeft());
            mergedSet.add(mergedVertices.getRight());
            network.put(network.createNextIdentifier(), mergedSet);

            this.updateC2C(network, c2c, v2v);

            this.updateC2V(network, c2v, v2v);
        }


        return null;
    }

    protected void updateC2C(Network network, DistanceMatrix c2c, DistanceMatrix v2v) {

        for(Map.Entry<Identifier, IdentifierList> components1 : network.entrySet()) {

            for(Map.Entry<Identifier, IdentifierList> components2 : network.entrySet()) {

                double sum1 = 0.0;

                for(Identifier id1 : components1.getValue()) {
                    for(Identifier id2 : components2.getValue()) {
                        sum1 += v2v.getDistance(id1, id2);
                    }
                }

                c2c.setDistance(components1.getKey(), components2.getKey(),
                        1.0 / (components1.getValue().size() * components2.getValue().size()) * sum1);
            }
        }
    }

    protected void updateC2V(Network network, DistanceMatrix c2v, DistanceMatrix v2v) {

        IdentifierList activeTaxa = v2v.getTaxa();

        for(Map.Entry<Identifier, IdentifierList> components1 : network.entrySet()) {

            for(Identifier activeTaxon : activeTaxa) {

                double sum1 = 0.0;

                for(Identifier id1 : components1.getValue()) {
                    sum1 += v2v.getDistance(id1, activeTaxon);
                }

                c2v.setDistance(components1.getKey(), activeTaxon,
                        1.0 / components1.getValue().size() * sum1);
            }
        }
    }


    protected Pair<Identifier, Identifier> merge(NeighborNetParams params, Pair<Identifier, Identifier> selectedTaxon,
                         IdentifierList component1, IdentifierList component2, IdentifierList componentUnion,
                         DistanceMatrix v2v) {

        final int nbVerticies = componentUnion.size();

        IdentifierList mergedComponent = new IdentifierList();

        if (nbVerticies == 2) {
            return new ImmutablePair<>(componentUnion.get(0), componentUnion.get(1));
        }
        else if (nbVerticies == 3) {

            int first = -1;
            int second = -1;
            int third = -1;

            if (component1.size() == 1) {

                first = component1.get(0).getId();

                if (component2.get(0).getId() == selectedTaxon.getLeft().getId() || component2.get(0).getId() == selectedTaxon.getRight().getId()) {
                    second = component2.get(0).getId();
                    third = component2.get(1).getId();
                }
                else {
                    second = component2.get(1).getId();
                    third = component2.get(0).getId();
                }
            }
            else {
                first = component2.get(0).getId();

                if (component1.get(0).getId() == selectedTaxon.getLeft().getId() || component1.get(0).getId() == selectedTaxon.getRight().getId()) {
                    second = component1.get(0).getId();
                    third = component1.get(1).getId();
                }
                else {
                    second = component1.get(1).getId();
                    third = component1.get(0).getId();
                }
            }

            return reduction(new Triplet<>(first, second, third), params, v2v);
        }
        else { // Should be 4

            int first = component1.get(0).getId() == selectedTaxon.getLeft().getId() ?
                    component1.get(1).getId() :
                    component1.get(0).getId();

            int second = selectedTaxon.getLeft().getId();
            int third = selectedTaxon.getRight().getId();

            int fourth = component2.get(0).getId() == selectedTaxon.getRight().getId() ?
                    component2.get(1).getId() :
                    component2.get(0).getId();

            Pair<Identifier, Identifier> reduced = reduction(new Triplet<Integer>(), params, v2v);

            reduction(new Triplet<Integer>(), params, v2v);
        }

        throw new IllegalArgumentException("Number of vertices must be >= 2 || <= 4.  Found " + nbVerticies + " vertices");
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
                                                DistanceMatrix v2v, IdentifierList component1, IdentifierList component2, IdentifierList union) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

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

                double q = ((union.size() - 4 + component1.size() + component2.size()) * v2v.getDistance(id1, id2)) -
                        sumId1 - sumId2 - sumVId1 - sumVId2;

                if (q < minQ) {
                    minQ = q;
                    bestPair = new ImmutablePair<>(id1, id2);
                }
            }
        }

        return bestPair;
    }


    protected void update() {

    }

    /**
     * Reduces the 3 selected vertices down to 2 new vertices.  The output takes the form of an updated distance
     * matrix represented by v2v and an updated split system
     * @param vertices The selected vertices to reduce
     * @param params alpha, beta and gamma parameters
     * @param v2v the distance matrix to update based on this reduction
     * @return The two new verticies that represent the reduced input
     */
    protected Pair<Identifier, Identifier> reduction(final Triplet<Integer> vertices, NeighborNetParams params,
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

        return new ImmutablePair<>(newVertex1, newVertex2);
    }
}
