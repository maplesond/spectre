package uk.ac.uea.cmp.phygen.core.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class TreeWeights {

    private final static Logger log = LoggerFactory.getLogger(TreeWeights.class);

    private double[][] tw;

    public TreeWeights(int nbTaxa) {
        this.tw = new double[nbTaxa][nbTaxa];
    }

    public TreeWeights(double[][] tw) {

        if (tw == null) {
            this.tw = null;
        }

        int n = tw.length;

        if (n != tw[0].length)
            throw new IllegalArgumentException("Tree weights must be a square matrix");

        this.tw = new double[n][n];

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                this.tw[i][j] = tw[i][j];
            }
        }
    }

    public double getAt(int i, int j) {
        return this.tw[i][j];
    }

    /**
     * Returns the length of the minimum evolution tree.
     *
     * @return length of the minimum evolution tree
     */
    public final double calcTreeLength() {
        double sum = 0;

        for(int i = 0; i < this.tw.length; i++){
            for(int j = 0; j < this.tw.length; j++){
                if (this.tw[j][i] == Double.POSITIVE_INFINITY) {
                    log.warn("TreeWeight [" + j + "," + i + "] is infinity");
                }
                sum += this.tw[j][i];
            }
        }

        return sum;
    }

    /**
     * Updates the tree weights matrix.
     *
     * @param branchLength length of a vertex
     * @param i specifies the split
     * @param j specifies the split
     */
    public void setWeightAt(double branchLength, int i, int j) {

        final int n = this.tw.length;
        if (i == 0) {
            this.tw[n - 1][j] = branchLength;
        } else {
            if ((j < n - 1) && (i - 1 > j)) {
                this.tw[i - 1][j] = branchLength;
            } else {
                this.tw[j][i - 1] = branchLength;
            }
        }
    }
}
