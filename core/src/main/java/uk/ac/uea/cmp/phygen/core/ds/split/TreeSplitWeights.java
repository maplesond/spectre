package uk.ac.uea.cmp.phygen.core.ds.split;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class TreeSplitWeights extends SplitWeights {

    private final static Logger log = LoggerFactory.getLogger(TreeSplitWeights.class);


    public TreeSplitWeights(int nbTaxa) {
        super(nbTaxa);
    }

    public TreeSplitWeights(double[][] weights) {
        super(weights);
    }



    /**
     * Returns the length of the minimum evolution tree.
     *
     * @return length of the minimum evolution tree
     */
    public final double calcTreeLength() {
        double sum = 0;

        for(int i = 0; i < this.size(); i++){
            for(int j = 0; j < this.size(); j++){
                if (this.getAt(j,i) == Double.POSITIVE_INFINITY) {
                    log.warn("TreeWeight [" + j + "," + i + "] is infinity");
                }
                sum += this.getAt(j,i);
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
    public void setBranchLengthAt(double branchLength, int i, int j) {

        final int n = this.size();
        if (i == 0) {
            this.setValAt(branchLength, n - 1, j);
        } else {
            if ((j < n - 1) && (i - 1 > j)) {
                this.setValAt(branchLength, i - 1, j);
            } else {
                this.setValAt(branchLength, j, i - 1);
            }
        }
    }
}
