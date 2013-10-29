package uk.ac.uea.cmp.phygen.core.ds.quartet;

import uk.ac.uea.cmp.phygen.core.math.tuple.Triplet;

/**
* Created with IntelliJ IDEA.
* User: maplesod
* Date: 29/10/13
* Time: 17:47
* To change this template use File | Settings | File Templates.
*/
public class QuartetWeighting extends Triplet<Double> {

    public QuartetWeighting() {
        this(0.0, 0.0, 0.0);
    }

    public QuartetWeighting(double a, double b, double c) {
        super(a,b,c);
    }

    public boolean allNonZero() {
        return this.getA() != 0 && this.getB() != 0 && this.getC() != 0;
    }

    private double logNormValue(double value) {
        if (value < 0.0) {
            throw new IllegalStateException("Error: Quartet file contains negative weight!");
        }

        return value == 0.0 ? -15.0 : Math.log(value);
    }


    public void normalise(boolean log, boolean useMax) {

        // lowest weight must be nonnegative
        double newA = this.getA();
        double newB = this.getB();
        double newC = this.getC();

        if (log) {
            newA = logNormValue(this.getA());
            newB = logNormValue(this.getB());
            newC = logNormValue(this.getC());
        }

        if (useMax) {
            double min = Math.min(Math.min(newA, newB), newC);

            this.setA(newA - min);
            this.setB(newB - min);
            this.setC(newC - min);
        }
        else {
            double max = Math.max(Math.max(newA, newB), newC);

            this.setA(max - newA);
            this.setB(max - newB);
            this.setC(max - newC);
        }
    }

    public void divide(QuartetWeighting other) {

        if (this.allNonZero()) {
            this.setA(this.getA() / other.getA());
            this.setB(this.getB() / other.getB());
            this.setC(this.getC() / other.getC());
        }
    }

    /**
     * just a potentially weighted mean with this, quartets supported by few trees will be weak, although if the
     * trees are strong, they may be too
     * @param other
     * @param weight
     */
    public void weightedSum(QuartetWeighting other, double weight) {

        this.setA(this.getA() + weight * other.getA());
        this.setB(this.getB() + weight * other.getB());
        this.setC(this.getC() + weight * other.getC());
    }
}
