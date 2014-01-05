package uk.ac.uea.cmp.phygen.core.ds.quartet.scale;

import uk.ac.uea.cmp.phygen.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.IOException;

/**
 * Created by dan on 14/12/13.
 */
public class ScalingMatrix {

    private double[][] matrix;

    /**
     * Creates the matrix of coefficients form a list of quartet networks
     * @param quartetSystemList
     */
    public ScalingMatrix(QuartetSystemList quartetSystemList) {

        // The number of quartet networks to process
        int nbNetworks = quartetSystemList.size();

        // Matrix of coefficients to be computed
        this.matrix = new double[nbNetworks][nbNetworks];

        // Compute diagonal elements of coefficient matrix
        for (int i = 0; i < nbNetworks; i++) {
            this.matrix[i][i] = this.computeDiagonalElement(quartetSystemList, i);
        }

        // Compute non-diagonal elements of coefficient matrix
        for (int i = 0; i < (nbNetworks - 1); i++) {
            for (int j = i + 1; j < nbNetworks; j++) {
                this.matrix[i][j] = computeOffDiagonalElement(
                        new GroupedQuartetSystem(quartetSystemList.get(i)),
                        new GroupedQuartetSystem(quartetSystemList.get(j)));
                this.matrix[j][i] = this.matrix[i][j];
            }
        }

    }

    public double[][] getMatrix() {
        return this.matrix;
    }

    /**
     * Computes an array in which at position k we find the index of taxon k in tree i
     * @param taxalisti array with taxanames for tree i sorted increasingly by indices of taxa
     * @param taxalistj array with taxanames for tree j sorted increasingly by indices of taxa
     * @return Translated taxa indicies
     */
    protected int[] translateIndices(String[] taxalisti, String[] taxalistj) {
        //loop variables
        int k = 0;
        int l = 0;

        //array in which the translation is stored
        int[] trans = new int[taxalistj.length];

        //loop throuph lists of taxa
        for (k = 0; k < taxalistj.length; k++) {
            //assume at the beginning that the taxon
            //with index k in tree j does not occur in
            //tree i
            trans[k] = -1;
            for (l = 0; l < taxalisti.length; l++) {
                if (taxalistj[k].compareTo(taxalisti[l]) == 0) {
                    trans[k] = l;
                    break;
                }
            }
        }
        return trans;
    }


    /**
     * Computes the contribution of tree number j to the diagonal element at position (i,i)
     * @param qni the quartet network of the tree for which we compute the diagonal element (i)
     * @param qnj the quartet network of the other tree (j)
     * @return Sum of the diagonals
     * @throws java.io.IOException
     */
    protected double sumUpDiagonal(GroupedQuartetSystem qni, GroupedQuartetSystem qnj) {

        //auxilliary variable to store intermediate
        //results when summing up the values for the
        //coefficient
        double part = 0.0;


        //get an array that translates the indices of taxa
        //in tree j to the indices of those taxa in tree
        //number i
        int[] transind = translateIndices(qni.getTaxa().getNames(), qnj.getTaxa().getNames());

        int ntaxaj = qni.getTaxa().size();

        //find common quartets and sum up squares of weights

        //auxiliary variables used to store the weights of the
        //quartets associated with the current line and, if
        //it exists, in tree i
        QuartetWeights wvi, wvj;

        //Now loop over the taxa so that we get all
        //4-subsets lexicographically ordered
        for (int t1 = 0; t1 < (ntaxaj - 3); t1++) {
            for (int t2 = t1 + 1; t2 < (ntaxaj - 2); t2++) {
                for (int t3 = t2 + 1; t3 < (ntaxaj - 1); t3++) {
                    for (int t4 = t3 + 1; t4 < ntaxaj; t4++) {

                        wvj = qnj.getQuartets().get(new Quartet(t1, t2, t3, t4).createSortedQuartet());

                        wvi = qni.getQuartets().get(new Quartet(transind[t1], transind[t2], transind[t3], transind[t4]).createSortedQuartet());

                        if (wvi != null && wvj != null) {

                            QuartetWeights wviPermuted = wvi.permute(transind[t1], transind[t2], transind[t3], transind[t4]);

                            if (wviPermuted.scalarProduct(wvj) != 0.0) {
                                part += wviPermuted.squaredLength();
                            }
                        }
                    }
                }
            }
        }

        return part;
    }

    /**
     * Computes the diagonal elements of the coefficient matrix
     * @param quartetSystemList the list of quartet networks
     * @param i index for the quartet network of the tree for which we compute the diagonal element
     * @return Value of the diagonal element
     * @throws IOException
     */
    protected double computeDiagonalElement(QuartetSystemList quartetSystemList, int i) {

        //auxilliary variable to store intermediate
        //results when summing up the values for the
        //coefficient
        double coeff = 0.0;


        //loop through the trees with index j distinct from i
        for (int j = 0; j < quartetSystemList.size(); j++) {
            if (j != i) {
                coeff = coeff + this.sumUpDiagonal(
                        new GroupedQuartetSystem(quartetSystemList.get(i)),
                        new GroupedQuartetSystem(quartetSystemList.get(j)));
            }
        }

        return coeff;
    }

    /**
     * Computes entry at position (i,j), i<j
     * @param qni quartet network of first tree (i)
     * @param qnj quartet network of second tree (j)
     * @return Value of the off diagonal element
     * @throws IOException
     */
    protected double computeOffDiagonalElement(GroupedQuartetSystem qni, GroupedQuartetSystem qnj) {

        //auxiliary variable used to store intermediate results
        double coeff = 0.0;

        //get an array that translates the indices of taxa
        //in tree j to the indeices of those taxa in tree
        //number i
        int[] transind = translateIndices(qni.getTaxa().getNames(), qnj.getTaxa().getNames());

        int ntaxaj = qni.getTaxa().size();

        //find common quartets and sum up the products of weights

        //auxiliary variables used to store the weights of the
        //quartets associated with the current line and, if
        //it exists, in tree i
        QuartetWeights wvi, wvj;

        // Now loop over the taxa so that we get all 4-subsets lexicographically ordered
        for (int t1 = 0; t1 < (ntaxaj - 3); t1++) {
            for (int t2 = t1 + 1; t2 < (ntaxaj - 2); t2++) {
                for (int t3 = t2 + 1; t3 < (ntaxaj - 1); t3++) {
                    for (int t4 = t3 + 1; t4 < ntaxaj; t4++) {

                        wvj = qnj.getQuartets().get(new Quartet(t1, t2, t3, t4).createSortedQuartet());

                        //need to sort indices after translation, best in
                        //constructor of Key
                        wvi = qni.getQuartets().get(new Quartet(transind[t1], transind[t2], transind[t3], transind[t4]).createSortedQuartet());

                        if (wvi != null && wvj != null) {
                            QuartetWeights wviPermuted = wvi.permute(transind[t1], transind[t2], transind[t3], transind[t4]);
                            coeff += wviPermuted.scalarProduct(wvj);
                        }
                    }
                }
            }
        }

        return (-coeff);
    }



}
