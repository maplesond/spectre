package uk.ac.uea.cmp.phybre.core.alg;

/**
 * Created by dan on 09/03/14.
 */
public class NeighborNetParams {

    private double alpha;
    private double beta;
    private double gamma;

    public NeighborNetParams(final double alpha, final double beta) {

        this(alpha, beta, 1.0 - alpha - beta);
    }

    public NeighborNetParams(final double alpha, final double beta, final double gamma) {

        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;

        if (!valid()) {
            throw new IllegalArgumentException("Parameters do not all sum to 1.0: " + this.toString());
        }
    }

    boolean valid() {
        return alpha + beta + gamma == 1.0;
    }

    @Override
    public String toString() {
        return "alpha: " + alpha + "; beta: " + beta + "; gamma: " + gamma;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public double getBeta() {
        return this.beta;
    }

    public double getGamma() {
        return this.gamma;
    }

}
