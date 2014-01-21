package uk.ac.uea.cmp.phybre.core.ds.quartet;

import uk.ac.uea.cmp.phybre.core.ds.Taxa;
import uk.ac.uea.cmp.phybre.core.ds.tree.newick.NewickTree;

/**
 * Created by dan on 05/01/14.
 */
public class QuartetSystem {

    public static enum Sense {
        MIN,
        MAX;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }


    private Taxa taxa;
    private double weight;
    private Sense sense;
    private CanonicalWeightedQuartetMap quartets;

    public QuartetSystem() {
        this(new Taxa(), 1.0, new CanonicalWeightedQuartetMap());
    }

    public QuartetSystem(Taxa taxa, double weight, CanonicalWeightedQuartetMap quartets) {
        this.taxa = taxa;
        this.weight = weight;
        this.sense = Sense.MAX;
        this.quartets = quartets;
    }

    /*public QuartetSystem(DistanceMatrix distanceMatrix, double weight) {

        final int N = distanceMatrix.getNbTaxa();
        final int expectedNbQuartets = Quartet.over4(N);

        this.quartets = new CanonicalWeightedQuartetMap(distanceMatrix);

        if (this.quartets.size() != expectedNbQuartets) {
            throw new IllegalArgumentException("Found unexpected number of quartets.  Something went wrong creating the " +
                    "quartet hash.  There were " + N + " taxa in the distance matrix, which should correspond to " + expectedNbQuartets +
                    "quartets in the hash.  Instead we found " + this.quartets.size() + " quartets in the hash.");
        }

        this.taxa = distanceMatrix.getTaxa();
        this.weight = weight;
    }*/

    public QuartetSystem(NewickTree newickTree) {
        this(newickTree.getTaxa(), newickTree.getScalingFactor(), newickTree.createQuartets());
    }

    public QuartetSystem(GroupedQuartetSystem groupedQuartetSystem) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    public Taxa getTaxa() {
        return taxa;
    }

    public void setTaxa(Taxa taxa) {
        this.taxa = taxa;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Sense getSense() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense = sense;
    }

    public CanonicalWeightedQuartetMap getQuartets() {
        return quartets;
    }

    public void setQuartets(CanonicalWeightedQuartetMap quartets) {
        this.quartets = quartets;
    }

}
