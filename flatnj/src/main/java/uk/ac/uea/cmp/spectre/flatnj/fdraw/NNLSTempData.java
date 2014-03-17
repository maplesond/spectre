/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.flatnj.fdraw;

import java.util.SortedSet;

//This class is used to pass data to the implementation
//of the nnls-algorithm 

public class NNLSTempData {
    //This flag indicates whether we run the
    //outer loop for the first time or not.
    //In the first pass we use the generalization
    //of Chepoi's formula to compute an optimal
    //unconstrained solution. After that we use
    //conjugate gradients to solve the unconstrained
    //subproblems.
    public boolean firsttime = true;

    //This is the epsilon-bound that can be
    //provided by the user. It is used in the test
    //whether we have arrived at a constrained
    //minimum. The smaller this bound, the tighter
    //the approximation of the minimum. But this
    //also means that the number of iterations
    //and thus the run time grow when this bound
    //is set to smaller values.
    public double usereps = 0.0001;

    //This array contains the distance matrix in
    //a 1-dimensional format. The order of the
    //entries is the same as the order of the
    //swaps in the permutation sequence.
    public double[] b = null;

    //This array is used to temporary store the
    //current length of the splits
    public double[] tempx = null;

    //This array is used to store the product of
    //the transpose of the topological matrix of
    //the split system with the vector b that
    //contains the distance matrix
    public double[] atb = null;

    //This array is used to store the weights
    //of the splits from the previous iteration.
    public double[] oldx = null;

    //This array stores the current split weights.
    //This array can also be used to provide a start
    //vector.
    public double[] curx = null;

    //These arrays are used to store intermediate
    //results and which have the same dimension
    //as the distance matrix (as a 1-dimensional
    //vecor).
    public double[] tempdist1 = null;
    public double[] tempdist2 = null;

    //These arrays are used to store intermediate
    //results and which have the same dimension
    //as the vector of the split weights. Note
    //that for full flat split systems the number
    //of splits equals teh number of 2-subsets of
    //the set of taxa. So tempdist* and tempsplit*
    //have the same length
    public double[] tempsplit1 = null;
    public double[] tempsplit2 = null;

    //This array is used to store the gradient of
    //the objective function at the current vector
    //of split weights. It has the same dimension
    //as the vector of split weights.
    public double[] grad = null;

    //This array contains a flag for each split
    //indicating wheter the length of this split
    //should be fixed to 0. This is used in the
    //active set method for NNLS due to Lawson
    //and Hanson.
    public boolean[] fixed = null;

    //This set contains the indices of those
    //splits that currently have negative length.
    public SortedSet negweights = null;

}