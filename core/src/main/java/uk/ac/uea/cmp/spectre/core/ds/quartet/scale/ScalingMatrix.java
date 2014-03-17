package uk.ac.uea.cmp.spectre.core.ds.quartet.scale;

import uk.ac.uea.cmp.spectre.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.spectre.core.math.Equality;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dan on 14/12/13.
 */
public class ScalingMatrix {

    public static final double LOCAL_TOLERANCE = 0.0000001;

    //Matrix of coefficients for the quadratic program
    //or
    //matrix of pairwise local scaling factors
    //used to check if there is a perfect match
    //between quartet weights. In this case we
    //cannot use the quadratic program solver.
    private double[][] matrix;

    //Flag used to indicate whether quartet weights
    //match up perfectly.
    private boolean perfectMatch;

    //remember the quartet system list used to set up the matrix
    private QuartetSystemList quartetSystemList;

    /**
     * Creates the matrix of coefficients form a list of quartet networks
     * @param quartetSystemList
     */
    public ScalingMatrix(QuartetSystemList quartetSystemList) {

        this.quartetSystemList = quartetSystemList;

        // The number of quartet networks to process
        int nbNetworks = this.quartetSystemList.size();

        // Matrix of coefficients to be computed
        this.matrix = new double[nbNetworks][nbNetworks];
        this.perfectMatch = true;

        //compute entries of local matrix
        for (int i = 0; i < nbNetworks; i++) {

            //diagnal entry is -1.0
            this.matrix[i][i] = -1.0;

            for (int j = i + 1; j < nbNetworks; j++) {
                this.matrix[i][j] = computeLocalOffDiagonalElement(
                        new GroupedQuartetSystem(this.quartetSystemList.get(i)),
                        new GroupedQuartetSystem(this.quartetSystemList.get(j)));
                this.matrix[j][i] = 1/this.matrix[i][j];
            }
        }

        if(!this.perfectMatch) {
            this.recomputeMatrix();
        }
    }

    public double[][] getMatrix() {
        return this.matrix;
    }

    public boolean isPerfectMatch() {
        return this.perfectMatch;
    }

    public void noPerfectMatch() {
        this.perfectMatch = false;
    }

    public void recomputeMatrix() {

        // The number of quartet networks to process
        int nbNetworks = this.quartetSystemList.size();

        // Compute diagonal elements of coefficient matrix
        for (int i = 0; i < nbNetworks; i++) {
            this.matrix[i][i] = this.computeDiagonalElement(this.quartetSystemList, i);
        }

        // Compute non-diagonal elements of coefficient matrix
        for (int i = 0; i < (nbNetworks - 1); i++) {
            for (int j = i + 1; j < nbNetworks; j++) {
                this.matrix[i][j] = computeOffDiagonalElement(
                        new GroupedQuartetSystem(this.quartetSystemList.get(i)),
                        new GroupedQuartetSystem(this.quartetSystemList.get(j)));
                this.matrix[j][i] = this.matrix[i][j];
            }
        }
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

        int ntaxaj = qnj.getTaxa().size();

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

                        wvj = qnj.getQuartets().get(new Quartet((t1+1), (t2+1), (t3+1), (t4+1)).createSortedQuartet());

                        if(transind[t1]>= 0 && transind[t2]>= 0 && transind[t3]>= 0 && transind[t4]>= 0) {
                            wvi = qni.getQuartets().get(new Quartet((transind[t1]+1), (transind[t2]+1), (transind[t3]+1), (transind[t4]+1)).createSortedQuartet());
                        }
                        else {
                            wvi = null;
                        }
                        if (wvi != null && wvj != null) {

                            QuartetWeights wviPermuted = wvi.permute((transind[t1]+1), (transind[t2]+1), (transind[t3]+1), (transind[t4]+1));

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
        //in tree j to the indices of those taxa in tree
        //number i
        int[] transind = translateIndices(qni.getTaxa().getNames(), qnj.getTaxa().getNames());

        int ntaxaj = qnj.getTaxa().size();

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

                        wvj = qnj.getQuartets().get(new Quartet((t1+1), (t2+1), (t3+1), (t4+1)).createSortedQuartet());

                        if(transind[t1]>= 0 && transind[t2]>= 0 && transind[t3]>= 0 && transind[t4]>= 0) {

                            //need to sort indices after translation, best in
                            //constructor of Key
                            wvi = qni.getQuartets().get(new Quartet((transind[t1] + 1), (transind[t2] + 1), (transind[t3] + 1), (transind[t4] + 1)).createSortedQuartet());
                        }
                        else {
                            wvi = null;
                        }

                        if (wvi != null && wvj != null) {
                            QuartetWeights wviPermuted = wvi.permute((transind[t1]+1), (transind[t2]+1), (transind[t3]+1), (transind[t4]+1));
                            coeff += wviPermuted.scalarProduct(wvj);
                        }
                    }
                }
            }
        }

        return (-coeff);
    }

    /**
     * Computes entry at position (i,j), i<j
     * @param qni quartet network of first tree (i)
     * @param qnj quartet network of second tree (j)
     * @return Value of the off diagonal element
     * @throws IOException
     */
    protected double computeLocalOffDiagonalElement(GroupedQuartetSystem qni, GroupedQuartetSystem qnj) {

        //auxiliary variable used to store intermediate results
        double coeff = -1.0;

        //get an array that translates the indices of taxa
        //in tree j to the indices of those taxa in tree
        //number i
        int[] transind = translateIndices(qni.getTaxa().getNames(), qnj.getTaxa().getNames());

        int ntaxaj = qnj.getTaxa().size();

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

                        wvj = qnj.getQuartets().get(new Quartet((t1+1), (t2+1), (t3+1), (t4+1)).createSortedQuartet());

                        if(transind[t1]>= 0 && transind[t2]>= 0 && transind[t3]>= 0 && transind[t4]>= 0) {

                            //need to sort indices after translation, best in
                            //constructor of Key
                            wvi = qni.getQuartets().get(new Quartet((transind[t1]+1), (transind[t2]+1), (transind[t3]+1), (transind[t4]+1)).createSortedQuartet());
                        }
                        else {
                            wvi = null;
                        }
                            
                        if (wvi != null && wvj != null) {

                            //check first weight in triplet
                            if(wvi.getA() == 0.0) {
                                if(wvj.getA() > 0.0) {
                                    this.perfectMatch = false;
                                }
                            }
                            else {
                                if(wvj.getA() == 0.0) {
                                    this.perfectMatch = false;
                                }
                                else {
                                    if(coeff < 0.0) {
                                        coeff = wvi.getA() / wvj.getA();
                                    }
                                    else {
                                        if(!Equality.approxEquals(coeff, wvi.getA() / wvj.getA(), LOCAL_TOLERANCE)) {
                                            this.perfectMatch = false;
                                        }
                                    }
                                }
                            }

                            //check second weight in triplet
                            if(wvi.getB() == 0.0) {
                                if(wvj.getB() > 0.0) {
                                    this.perfectMatch = false;
                                }
                            }
                            else {
                                if(wvj.getB() == 0.0) {
                                    this.perfectMatch = false;
                                }
                                else {
                                    if(coeff < 0.0) {
                                        coeff = wvi.getB() / wvj.getB();
                                    }
                                    else {
                                        if(!Equality.approxEquals(coeff, wvi.getB() / wvj.getB(), LOCAL_TOLERANCE)) {
                                            this.perfectMatch = false;
                                        }
                                    }
                                }
                            }

                            //check third weight in triplet
                            if(wvi.getC() == 0.0) {
                                if(wvj.getC() > 0.0) {
                                    this.perfectMatch = false;
                                }
                            }
                            else {
                                if(wvj.getC() == 0.0) {
                                    this.perfectMatch = false;
                                }
                                else {
                                    if(coeff < 0.0) {
                                        coeff = wvi.getC() / wvj.getC();
                                    }
                                    else {
                                        if(!Equality.approxEquals(coeff, wvi.getC() / wvj.getC(),LOCAL_TOLERANCE)) {
                                            this.perfectMatch = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return coeff;
    }

    public double[] computeFactorsDirectly() {

        //the matrix of local scaling factors
        final double[][] m = this.matrix;
        //the global scaling factors
        double[] s = new double[m.length];

        //initialize global scaling factors
        for(int i=0; i < s.length; i++) {
            s[i] = -1.0;
        }

        //Linked list used in the search for connected components
        ArrayList<Integer> reached = new ArrayList<Integer>();

        //Computing the global scaling factors is
        //similar to finding connected components in
        //a graph given its adjacency matrix. It can
        //happen that we find out that there is no
        //perfect match.
        for(int i=0; i < s.length; i++) {

            //check if i starts a new connected component
            if(s[i] == -1.0) {
                s[i] = 1.0;
                reached.add(i);
            }

            //process all elements in the current connected component
            while(!reached.isEmpty()) {
                int k = reached.remove(0);
                for(int j=0; j < m.length; j++) {
                    if(m[k][j] >= 0.0) {
                        if(s[j] == -1.0) {
                            s[j] = m[k][j]*s[k];
                            reached.add(j);
                        }
                        else {
                            if(!uk.ac.tgac.metaopt.Equality.approxEquals(s[j], m[k][j] * s[k], LOCAL_TOLERANCE)) {
                                this.noPerfectMatch();
                            }
                        }
                    }
                }
            }
        }

        //normalize global scaling factors
        if(this.isPerfectMatch()) {
            double sum = 0.0;

            for(int i = 0; i < s.length; i++) {
                sum += s[i];
            }

            for(int i = 0; i < s.length; i++) {
                s[i] /= sum;
            }
        }

        return s;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix.length; j++) {
                sb.append(matrix[i][j] + " ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}
