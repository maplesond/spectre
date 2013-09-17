/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

package uk.ac.uea.cmp.phygen.flatnj.fdraw;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

//This class provides the algorithm for computing
//NNLS-fitting of a distance matrix on a flat split
//system

public class FitWeight
{
//*******************************************************************
//public methods
//*******************************************************************

   //This method wraps the actual nnls algorithm and
   //performs some initialization when the
   //algorithm is called from scratch.
   //
   /**
    Parameters:
    psequ   --> permutation sequence representing a flat split system
    dist    --> distance matrix we want to fit on the flat split system
    usereps --> epsilon bound provided by the user. Used determine
                the precision of the approximation of the optimal solution.
   */
   public static void lh_nnls_arr(PermutationSequenceDraw psequ,double[][] dist,double usereps)
   {
      //loop variable
      int i = 0;

      //Setting up the object containing information 
      //about the arrangement of pseudolines.
      ArrangementData arrdata = psequ.compute_arrangement();

      //Setting up the temporary arrays used in the NNLS algorithm
      NNLSTempData tmpdata = new NNLSTempData();

      //This will be the first pass.
      tmpdata.firsttime = true;

      //setting the epsilon to the value provided by the user.
      tmpdata.usereps = usereps;

      //Arrays for storing the split weights
      tmpdata.tempx = new double[psequ.nswaps];
      tmpdata.oldx = new double[psequ.nActive];
      tmpdata.curx = new double[psequ.nActive];

      //Getting the distance matrix as a 1-dimensional array.
      tmpdata.b = new double[psequ.nswaps];
      compute_dist_vector_arr(tmpdata.b,psequ.nswaps,dist,psequ);  

      //Computing the product of the transpose of the 
      //topological matrix of the split system and the
      //distance vector.
      tmpdata.atb = new double[psequ.nActive];
      //compute_aty_arr(tmpdata.atb,tmpdata.tempx,tmpdata.b,arrdata.change,arrdata.arr,arrdata.lastswap,arrdata.upperlower,psequ);
      compute_aty_arr(tmpdata.atb,tmpdata.tempx,tmpdata.b,arrdata,psequ);

      //The all 1 weighting is clearly a feasible solution so we pretend 
      //it was the previous weighting.
      init_x(tmpdata.oldx,1.0);

      //Now compute a new weighting, possibly with some negative
      //weights, by solving the full unconstrained problem.
      System.out.println("Compute solution for full unconstrained problem.");
      //compute_unconstrained(tmpdata.curx,tmpdata.tempx,tmpdata.b,arrdata.arr,arrdata.lastswap,psequ);
      compute_unconstrained(tmpdata.curx,tmpdata,arrdata,psequ);
  
      //Set flags to indicate which split weights are fixed to 0.
      //Initially no weights are fixed, that is, all flags are
      //set to false.
      tmpdata.fixed = new boolean[psequ.nActive];
      init_fixed(tmpdata.fixed);
      
      //Create object to store the indices of the splits with negative weight.
      tmpdata.negweights = new TreeSet(new SplitWeight(0.0,0));

      //Allocate memory for other arrays in tmpdata.
      tmpdata.tempdist1 = new double[psequ.nswaps];
      tmpdata.tempdist2 = new double[psequ.nswaps];
      tmpdata.tempsplit1 = new double[psequ.nActive];
      tmpdata.tempsplit2 = new double[psequ.nActive];
      tmpdata.grad = new double[psequ.nActive]; 

      //Score of the objective function at the optimal weight vector
      //found by the NNLS algorithm
      double score = 0.0;
 
      //Run the NNLS algorithm.
      score = lh_nnls_arr_inner(psequ,tmpdata,arrdata);
      System.out.println("Optimal score found in FitWeight: " + score);

      //store solution in weights array of the permutation sequence
      for(i=0;i<psequ.nswaps;i++)
      {
         psequ.weights[i] = tmpdata.curx[i];
      }
   }

   //This method uses the NNLS algorithm by
   //Lawson and Hanson. The particular implementation
   //uses the method of conjugate gradients to
   //solve the unconstraint subproblems. This
   //idea is due to David Bryant. The matrix-vector
   //multiplication is arrangement-based and one
   //can provide a start vector and so on.
   //This method is wrapped by lh_nnls_arr()
   /**
    Parameters
    psequ   --> permutation sequence representing a flat split system
    tmpdata --> object containing temporary data used by the algorithm.
                The main purpose is to avoid the re-allocation of memory
                by providing arrays that can be used throughout.
    arrdata --> object storing information about the arrangement of pseudolines
                representing the flat split system
   */
   public static double lh_nnls_arr_inner(PermutationSequenceDraw psequ,NNLSTempData tmpdata,ArrangementData arrdata)
   { 
      eps = tmpdata.usereps;
      cgeps = tmpdata.usereps;
      
      //Upper bound on the number of iterations.
      //It would be good to investigate how many are really necessary.
      //In practice this seems to be more than enough.
      int maxiter = 100*psequ.nswaps;
      int niter   = 0;

      int i = 0;

      double delta = 0.0;
      double mindelta = 0.0;
      int minind = 0;

      while(true)
      {
         System.out.println("Outer loop");          

	 while(true)
         {
             System.out.println("Inner loop"); 

            //Check if we enter this loop for the first time.
            if(tmpdata.firsttime)
            {
               //We just use the optimal solution of the full unconstraint problem.
               tmpdata.firsttime = false;
            }
            else
            {
               //We compute the optimal solution of the unconstrained problem restricted
               //to those elements that are not fixed to 0.
               //conjugate_gradient_arr(tmpdata,arrdata.arr,arrdata.lastswap,arrdata.upperlower,arrdata.change,psequ);
               conjugate_gradient_arr(tmpdata,arrdata,psequ);
            }

            //Store the indices of the splits that currently have 
            //negative weight.
            collect_negative_splits(tmpdata.curx,tmpdata.negweights);

            //Sometimes we get a lot of splits with negative weight.
            //Then we fix the worst among them to be 0 and re-compute
            //the unconstained optimum restricted to the splits that
            //are not fixed.
            if(remove_worst_splits(tmpdata.negweights,tmpdata.curx,tmpdata.fixed))
            {
               //conjugate_gradient_arr(tmpdata,arrdata.arr,arrdata.lastswap,arrdata.upperlower,arrdata.change,psequ);
               conjugate_gradient_arr(tmpdata,arrdata,psequ);
            }
            
            //Move to last feasible solution on the line
            //segment from curx to oldx.
            minind = -1;
            for(i=0;i<tmpdata.curx.length;i++)
            {
               if(tmpdata.curx[i] < 0.0)
               {
                  delta = tmpdata.oldx[i]/(tmpdata.oldx[i] - tmpdata.curx[i]);
                  if((minind == -1) || (delta < mindelta))
                  {
                  minind = i;
                  mindelta = delta;
                  }
               }
            }
            if(minind != -1)
            {
               for(i=0;i<tmpdata.oldx.length;i++)
               {
                  if(!tmpdata.fixed[i])
                  {
                     tmpdata.oldx[i] = tmpdata.oldx[i] + (mindelta*(tmpdata.curx[i]-tmpdata.oldx[i]));
                  }
               }
               tmpdata.fixed[minind] = true;
               tmpdata.curx[minind] = 0.0;
            }
            else
            {
               break;
            }
         }
         
         //Check whether we have reached a minimum
         //of the constraint problem.
         //compute_gradient_arr(tmpdata,arr,lastswap,upperlower,psequ,change);
         compute_gradient_arr(tmpdata,arrdata,psequ);
         minind = -1;
         for(i=0;i<tmpdata.grad.length;i++)
         {
            if(tmpdata.fixed[i])
            {
               delta = tmpdata.grad[i];
               if((minind == -1) || (delta < mindelta))
               {
                  minind = i;
                  mindelta = delta;
               }
            }
         }

         if((minind == -1) || (mindelta > -eps))
         {
            break;
         }
         else
         {
            tmpdata.fixed[minind] = false;
         }
         niter++;
      }

      return compute_objective_arr(tmpdata,arrdata.arr,psequ);
   }

   //This method computes the distance induced by the
   //active splits using the arrangement of pseudolines
   //This method exists for testing purposes. It is not
   //used by the NNLS algorithm.
   /**
    Parameters
    psequ   --> permutation sequence representing a flat split system 
   */
   public static double[][] compute_induced_distance_arr(PermutationSequenceDraw psequ)
   {
      //Array for the distance matrix to be computed.
      double[][] dist = null;
      
      //Temporary array used to store the induced distance
      //matrix as a 1-dimensional array.
      double[] y = new double[psequ.nswaps];

      //Temporary array used in the matrix vector
      //multiplication.
      double[] tempdist = new double[psequ.nswaps];

      //Vector of the split weights.
      double[] x = new double[psequ.nActive];
      
      //loop variable
      int i = 0;

      //Copy the split weights to x. Splits that are not active
      //cannot contribute to the induced distance.
      for(i=0;i<psequ.nswaps;i++)
      {
         if(psequ.active[i])
         {
            x[i] = psequ.weights[i];
         }
         else
         {
            x[i] = 0.0;
         }
      }

      //Get the neccessary information about the arrangement of
      //pseudolines.
      ArrangementData arrdata = psequ.compute_arrangement();
       
      //Compute the induced distance as a 1-dimensional array.
      compute_ax_arr(y,tempdist,x,arrdata.arr,psequ);
      
      //Transform the 1-dimensional array int a 2-dimensional one.
      dist = compute_dist_matrix_arr(y,psequ);

      return dist;
   }
   

   //This method computes the 1-dimensional array
   //that corresponds to one triangle of the distance
   //matrix. The order of entries corresponds to
   //the left to right order of the swaps in the
   //arrangement of pseudolines.
   /**
    Parameters
    b      --> 1-dimensional vector corresponding to the given distance matrix
    npairs --> number of 2-element subsets of the set of taxa
    dist   --> given distance matrix
    psequ  --> permutation sequence representing a flat split system 
   */
   public static void compute_dist_vector_arr(double[] b,int npairs,double[][] dist,PermutationSequenceDraw psequ)
   {
      int[] cursequ = new int[psequ.ntaxa];

      int i = 0;
      int h = 0;

      for(i=0;i<psequ.ntaxa;i++)
      {
         cursequ[i] = psequ.initSequ[i];
      }

      for(i=0;i<npairs;i++)
      {
         b[i] = dist[cursequ[psequ.swaps[i]]][cursequ[psequ.swaps[i]+1]];
         h = cursequ[psequ.swaps[i]];
         cursequ[psequ.swaps[i]] = cursequ[psequ.swaps[i]+1];
         cursequ[psequ.swaps[i]+1] = h;
      }
   }


   //This method initializes the flags for
   //fixed enries
   /**
    Parameters
    fixed --> array of flags, a flag is true iff the weight
              of the corresponding split is fixed to 0.0.
              Initially no split weights are fixed.
   */
   public static void init_fixed(boolean[] fixed)
   {
      int i = 0;

      for(i=0;i<fixed.length;i++)
      {
         fixed[i] = false;
      }
   }


   //Computes split weights from distances
   /**
    Parameters
    x       --> resulting weight vector
    tmpdata --> object containing temporary data used by the algorithm.
                The main purpose is to avoid the re-allocation of memory
                by providing arrays that can be used throughout.
    arrdata --> object storing information about the arrangement of pseudolines
                representing the flat split system 
    psequ   --> permutation sequence representing a flat split system
   */
   public static void compute_aty_arr(double[] x,double[] tempx,double[] y,ArrangementData arrdata,PermutationSequenceDraw psequ) 
   {
      //loop variables
      int i = 0;
      int j = 0;

      //temporary indices of vertices in the arrangement
      int firstvertex = 0;
      int ulindex = 0;
      int prevvertex = 0;
      int curvertex = 0;

      //temporary weights 
      double curweight = 0.0;
      double curchange = 0.0;

      //first compute net change across the last edge on
      //each pseudoline
      for(i=0;i<arrdata.lastswap.length;i++)
      {
         prevvertex = -1;
         curvertex = arrdata.lastswap[i];

         while(curvertex >= 0)
         {
            if(prevvertex < 0)
            {
               if(arrdata.upperlower[i] == 1)
               {
                  firstvertex = curvertex;
                  ulindex = 1;
                  arrdata.change[firstvertex][ulindex] = y[firstvertex];
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][1];
                  if(curvertex >= 0)
                  {
                     if(prevvertex == arrdata.arr[curvertex][3])
                     {
                        arrdata.change[curvertex][1] = -2*y[prevvertex];
                     }
                     else
                     {
                        arrdata.change[curvertex][0] = -2*y[prevvertex];
                     }
                  }
               }
               else
               { 
                  firstvertex = curvertex;
                  ulindex = 0; 
                  arrdata.change[firstvertex][ulindex] = -y[curvertex];
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][0];
                  if(curvertex >= 0)
                  {
                     if(prevvertex == arrdata.arr[curvertex][3])
                     {
                        arrdata.change[curvertex][1] = 2*y[prevvertex];
                     }
                     else
                     {
                        arrdata.change[curvertex][0] = 2*y[prevvertex];
                     }
                  }
               }
            }
            else
            {
               if(prevvertex == arrdata.arr[curvertex][3])
               {
                  arrdata.change[firstvertex][ulindex] = arrdata.change[firstvertex][ulindex] + y[curvertex];
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][1];
                  if(curvertex >= 0)
                  {
                     if(prevvertex == arrdata.arr[curvertex][3])
                     {
                        arrdata.change[curvertex][1] = -2*y[prevvertex];
                     }
                     else
                     {
                        arrdata.change[curvertex][0] = -2*y[prevvertex];
                     }
                  }
               }
               else
               {
                  arrdata.change[firstvertex][ulindex] = arrdata.change[firstvertex][ulindex] - y[curvertex];
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][0];
                  if(curvertex >= 0)
                  {
                     if(prevvertex == arrdata.arr[curvertex][3])
                     {
                        arrdata.change[curvertex][1] = 2*y[prevvertex];
                     }
                     else
                     {
                        arrdata.change[curvertex][0] = 2*y[prevvertex];
                     }
                  }
               }
            }
         }
      }

      //Next propagate the change along each pseudoline
      for(i=0;i<arrdata.lastswap.length;i++)
      {
         prevvertex = -1;
         curvertex = arrdata.lastswap[i];

         while(curvertex >= 0)
         {
            if(prevvertex < 0)
            {
               if(arrdata.upperlower[i] == 1)
               {
                  curchange = arrdata.change[curvertex][1];
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][1];
               }
               else
               {
                  curchange = arrdata.change[curvertex][0];
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][0];
               }
            }
            else
            {
               if(prevvertex == arrdata.arr[curvertex][3])
               {
                  curchange = curchange + arrdata.change[curvertex][1];
                  arrdata.change[curvertex][1] = curchange;
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][1]; 
               }
               else
               {
                  curchange = curchange + arrdata.change[curvertex][0];
                  arrdata.change[curvertex][0] = curchange;
                  prevvertex = curvertex;
                  curvertex = arrdata.arr[curvertex][0]; 
               }
            }
         }
      }

      //Next we compute the weight of the splits that
      //correspond to an unbounded face on the right
      //side of the arrangement
      for(i=0;i<psequ.ntaxa;i++)
      {
         curvertex = arrdata.lastswap[psequ.initSequ[i]];

         //check if curvertex corresponds to the face
         //below the current last edge
         if(arrdata.upperlower[psequ.initSequ[i]] == 1)
         {
            curweight = curweight + arrdata.change[curvertex][1];
            tempx[curvertex] = curweight;
         }
         else
         {
            curweight = curweight + arrdata.change[curvertex][0];
         }
      }
    
      //Finally we progagate the weight of the
      //rightmost splits through the arrangement
      for(i=psequ.nswaps-1;i>=0;i--)
      {
         if(arrdata.arr[i][3] >= 0)
         {
            if(arrdata.arr[arrdata.arr[i][3]][0] == i)
            {
               tempx[i] = tempx[arrdata.arr[i][3]] - arrdata.change[arrdata.arr[i][3]][1] + arrdata.change[i][1];
            }
            else
            {
               tempx[i] = tempx[arrdata.arr[i][3]] + arrdata.change[arrdata.arr[i][3]][0];
            }
         }
      }

      //It remains to copy the weights of the active
      //splits to x
      for(i=0;i<tempx.length;i++)
      {
         if(psequ.active[i])
         {
            x[i] = tempx[i];
         }
         else
         {
            x[i] = 0.0;
         }
      }
   }


   //This method initializes the current solution
   /**
    Parameters
    x --> vector to be initialized
    w --> real number that is assigned to all entries of x
   */
   public static void init_x(double[] x,double w)
   {
      int i = 0;

      for(i=0;i<x.length;i++)
      {
         x[i] = w;
      }
   }

   //This method computes the unique (not necessarily non-negative)
   //weighting of the splits that induces the distance matrix 
   /**
    Parameters
    tmpdata --> object containing temporary data used by the algorithm.
                The main purpose is to avoid the re-allocation of memory
                by providing arrays that can be used throughout.
    arrdata --> object storing information about the arrangement of pseudolines
                representing the flat split system 
    psequ   --> permutation sequence representing a flat split system
   */
   //public static void compute_unconstrained(double[] x,double[] tempx,double[] b,int[][] arr,int[] lastswap,PermutationSequenceDraw psequ)
   public static void compute_unconstrained(double[] x,NNLSTempData tmpdata,ArrangementData arrdata,PermutationSequenceDraw psequ)
   {
      //loop variable
      int i = 0;

      //indices of vertex in arrangement
      int index = 0;
      int leftmost = 0;
      int beforeleftmost = 0;

      //temporary variable used to compute the weight of a split
      double weight  = 0.0;

      //Process vertices of the arrangement from right to left.
      for(i=psequ.nswaps-1;i>=0;i--)
      {
         weight = tmpdata.b[i];

         //walk along upper boundary of face
         leftmost = arrdata.arr[i][0];
         beforeleftmost = i;
         while(true)
         {
            if((leftmost < 0) || (arrdata.arr[leftmost][3] == beforeleftmost))
            {
               break;
            }
            else
            {
               weight = weight - tmpdata.b[leftmost];
               beforeleftmost = leftmost;
               leftmost = arrdata.arr[leftmost][1];
            }
         }
         if(leftmost >= 0)
         {
            weight = weight + tmpdata.b[leftmost];
            index = leftmost;
         }
         else
         {
            //The face is unbounded to the left.
            //We need to continue walking along the
            //lower part of the corresponding unbounded
            //face on the right side of the arrangement.
            beforeleftmost = leftmost;
            leftmost = arrdata.lastswap[-beforeleftmost-1];
            while(true)
            {
               if(arrdata.arr[leftmost][2] == beforeleftmost)
               {
                  weight = weight + tmpdata.b[leftmost];
                  index = leftmost;
                  break;
               }
               else
               {
                  weight = weight - tmpdata.b[leftmost];
                  beforeleftmost = leftmost;
                  leftmost = arrdata.arr[leftmost][0];
               }
            }
         }

         //walk along the lower boundary of the face
         leftmost = arrdata.arr[i][1];
         beforeleftmost = i;
         while(true)
         {
            if((leftmost < 0) || (arrdata.arr[leftmost][2] == beforeleftmost))
            {
               break;
            }
            else
            {
               weight = weight - tmpdata.b[leftmost];
               beforeleftmost = leftmost;
               leftmost = arrdata.arr[leftmost][0];
            }
         }
         if(leftmost < 0)
         {
            //The face is unbounded to the left.
            //We need to continue walking along the
            //upper part of the corresponding unbounded
            //face on the right side of the arrangement.
            beforeleftmost = leftmost;
            leftmost = arrdata.lastswap[-beforeleftmost-1];
            while(true)
            {
               if(arrdata.arr[leftmost][3] == beforeleftmost)
               {
                  break;
               }
               else
               {
                  weight = weight - tmpdata.b[leftmost];
                  beforeleftmost = leftmost;
                  leftmost = arrdata.arr[leftmost][1];
               }
            }
         }

         tmpdata.tempx[index] = weight/2;
      }

      //It remains to copy the weights of the active
      //splits to x
      for(i=0;i<tmpdata.tempx.length;i++)
      {
         x[i] = tmpdata.tempx[i];
      }
   }

//*******************************************************************
//private variables and methods
//*******************************************************************

   //variable to count the number of times we
   //compute av or atw
   private static int ncalls = 0;

   //tolerance used when testing whether a value will
   //be treated as 0
   private static double eps = 0.000001;
   private static double cgeps = 0.000001;
   

   //This method computes a distance matrix from a
   //1-dimensional distance vector that corresponds
   //to the swaps in the arrangement of pseudolines
   /**
    Parameters
    y     --> 1-dimensional distance vector
    psequ --> permutation sequence representing a flat split system
   */
   private static double[][] compute_dist_matrix_arr(double[] y,PermutationSequenceDraw psequ)
   {
      //Distance matrix to be filled in
      double[][] dist = new double[psequ.ntaxa][psequ.ntaxa];

      //Temporary array use to store the current permutation
      //while sweeping through the arrangement of pseudolines
      int[] cursequ = new int[psequ.ntaxa];
 
      //loop variable
      int i = 0;

      //variable used for swapping two elements
      int h = 0;

      //Initialize current permutation and main diagonal
      //of distance matrix.
      for(i=0;i<psequ.ntaxa;i++)
      {
         cursequ[i] = psequ.initSequ[i];
         dist[i][i] = 0.0;
      }

      //Sweep through arrangement of pseudolines.
      for(i=0;i<y.length;i++)
      {
         dist[cursequ[psequ.swaps[i]]][cursequ[psequ.swaps[i]+1]] = y[i];
         dist[cursequ[psequ.swaps[i]+1]][cursequ[psequ.swaps[i]]] = y[i];
         h = cursequ[psequ.swaps[i]];
         cursequ[psequ.swaps[i]] = cursequ[psequ.swaps[i]+1];
         cursequ[psequ.swaps[i]+1] = h;
      }

      return dist;      
   }

   //This method computes the product of the topological
   //matrix and a vector of split weigts without explicitly
   //computing the topological matrix
   /**
    Parameter
    y        --> resulting distance vector
    tempdist --> temporary array provided to avoid re-allocation of memory
    x        --> vector of split weights
    arr      --> data about the arrangement of pseudolines (see class ArrangementData)
    psequ    --> permutation sequence representing the flat split system    
   */
   private static void compute_ax_arr(double[] y,double[] tempdist,double[] x,int[][] arr,PermutationSequenceDraw psequ)
   {
      int i = 0;
      int rightmost = 0;
      int beforerightmost = 0;

      //ncalls++;

      //first collect weight of splits to the
      //right of a swap
      for(i=psequ.nswaps-1;i>=0;i--)
      {
         if(psequ.active[i])
         {
            y[i] = x[psequ.compressed[i]];
         }
         else
         {
            y[i] = 0.0;
         }
         //walk along upper boundary of face
         rightmost = arr[i][3];
         beforerightmost = i;
         while(true)
         {
            if((rightmost < 0) || (arr[rightmost][0] == beforerightmost))
            {
               break;
            }
            else
            {
               y[i] = y[i] + y[rightmost];
               beforerightmost = rightmost;
               rightmost = arr[rightmost][2];
            }
         }
         if(rightmost >= 0)
         {
            y[i] = y[i] - y[rightmost];
         }
         //walk along lower part of face
         rightmost = arr[i][2];
         beforerightmost = i; 
         while(true)
         {
            if((rightmost < 0) || (arr[rightmost][1] == beforerightmost))
            {
               break;
            }
            else
            {
               y[i] = y[i] + y[rightmost];
               beforerightmost = rightmost;
               rightmost = arr[rightmost][3];
            }
         }
      }
 
      int leftmost = 0;
      int beforeleftmost = 0;

      //next collect weight of splits to the
      //left of a swap
      for(i=0;i<psequ.nswaps;i++)
      {
         tempdist[i] = 0.0;

         //walk along upper boundary of face
         leftmost = arr[i][0];
         beforeleftmost = i;
         while(true)
         {
            if((leftmost < 0) || (arr[leftmost][3] == beforeleftmost))
            {
               break;
            }
            else
            {
               tempdist[i] = tempdist[i] + tempdist[leftmost];
               beforeleftmost = leftmost;
               leftmost = arr[leftmost][1];
            }
         }
         if(leftmost >= 0)
         {
            if(psequ.active[leftmost])
            {
               tempdist[i] = (tempdist[i] - tempdist[leftmost]) + x[psequ.compressed[leftmost]];
            }
            else
            {
               tempdist[i] = tempdist[i] - tempdist[leftmost];
            }
         }
         //walk along lower part of face
         leftmost = arr[i][1];
         beforeleftmost = i; 
         while(true)
         {
            if((leftmost < 0) || (arr[leftmost][2] == beforeleftmost))
            {
               break;
            }
            else
            {
               tempdist[i] = tempdist[i] + tempdist[leftmost];
               beforeleftmost = leftmost;
               leftmost = arr[leftmost][0];
            }
         }
      }

      //combine weights to the left and right of a swap
      for(i=0;i<y.length;i++)
      {
         y[i] = y[i] + tempdist[i];
      }
   }


   //Same as previous method but uses arrangment
   //of pseudolines to do the matrix-vector
   //multiplication
   /**
    Parameters
    tmpdata --> object containing temporary data used by the algorithm.
                The main purpose is to avoid the re-allocation of memory
                by providing arrays that can be used throughout.
    arrdata --> object storing information about the arrangement of pseudolines
                representing the flat split system 
    psequ   --> permutation sequence representing a flat split system
   */
   private static void compute_gradient_arr(NNLSTempData tmpdata,ArrangementData arrdata,PermutationSequenceDraw psequ)
   {
      compute_ax_arr(tmpdata.tempdist1,tmpdata.tempdist2,tmpdata.curx,arrdata.arr,psequ);
      compute_aty_arr(tmpdata.grad,tmpdata.tempx,tmpdata.tempdist1,arrdata,psequ);
      
      int i = 0;

      for(i=0;i<tmpdata.grad.length;i++)
      {
         tmpdata.grad[i] = tmpdata.grad[i] - tmpdata.atb[i];
      }
   }

   //This method replaces every negative entry in the
   //vector by 0
   /**
    Parameter
    x --> input vector
   */
   private static void make_non_negative(double[] x)
   {
      int i = 0;

      for(i=0;i<x.length;i++)
      {
         if(x[i] < 0.0)
         {
            x[i] = 0.0;
         }
      }
   }


   //This method computes the l_2-norm of a vector
   /**
    Parameter
    v --> input vector
   */
   private static double l2_norm(double[] v)
   {
      int i = 0;
      double l = 0.0;

      for(i=0;i<v.length;i++)
      {
         l = l + (v[i]*v[i]);
      }

      l = Math.sqrt(l);
      return l;
   }

   //This method computes the value of the objective
   //function at a vector x using the arrangement
   //based matrix-vector multiplication
   /**
    Parameters
    tmpdata --> object containing temporary data used by the algorithm.
                The main purpose is to avoid the re-allocation of memory
                by providing arrays that can be used throughout.
    arr     --> array storing information about the arrangement of pseudolines
                representing the flat split system 
    psequ   --> permutation sequence representing a flat split system
   */
   private static double compute_objective_arr(NNLSTempData tmpdata,int[][] arr,PermutationSequenceDraw psequ)
   {
      int i = 0;
      double l = 0.0;

      compute_ax_arr(tmpdata.tempdist1,tmpdata.tempdist2,tmpdata.curx,arr,psequ);
      
      for(i=0;i<tmpdata.b.length;i++)
      {
         tmpdata.tempdist1[i] = tmpdata.tempdist1[i] - tmpdata.b[i];
      }

      l = l2_norm(tmpdata.tempdist1);
      l = l*l;
      return l;
   }


   //This method computes the scalar product of two
   //vectors
   /**
    Parameters
    u --> input vector
    v --> input vector
   */
   private static double compute_scalar_product(double[] u,double[] v)
   {
      int i = 0;
      double l = 0.0;

      for(i=0;i<u.length;i++)
      {
         l = l + (u[i]*v[i]);
      }
      return l;
   }

   //This method prints a vector on the screen
   /**
    Parameters
    v       --> input vector
    message --> string provided by the user that will be printed before the vector
   */
   private static void print_vector(double[] v,String message)
   {
      int i = 0;

      System.out.println(message);

      for(i=0;i<v.length;i++)
      {
         System.out.print(v[i] + " ");
      }
      System.out.println(" ");
   }

   //This method computes the solution to a linear
   //system of equations with the conjugate gradient
   //method. The matrix-vector multiplication is
   //arrangement based
   /**
    Parameters
    tmpdata --> object containing temporary data used by the algorithm.
                The main purpose is to avoid the re-allocation of memory
                by providing arrays that can be used throughout.
    arrdata --> object storing information about the arrangement of pseudolines
                representing the flat split system 
    psequ   --> permutation sequence representing a flat split system
   */
   private static void conjugate_gradient_arr(NNLSTempData tmpdata,ArrangementData arrdata,PermutationSequenceDraw psequ)
   {
      //maximum number of iterations. Would be good to
      //investigate how many are sufficient. In practice
      //this seems to work fine.
      int maxiter = 10*tmpdata.tempdist1.length;

      //for counting the actual number of iterations
      int niter = 0;

      //loop variable
      int i = 0;

      compute_ax_arr(tmpdata.tempdist1,tmpdata.tempdist2,tmpdata.curx,arrdata.arr,psequ);
      compute_aty_arr(tmpdata.grad,tmpdata.tempx,tmpdata.tempdist1,arrdata,psequ);
      for(i=0;i<tmpdata.grad.length;i++)
      {
         if(!tmpdata.fixed[i])
         {
            tmpdata.grad[i] = tmpdata.atb[i] - tmpdata.grad[i];
         }
         else
         {
            tmpdata.grad[i] = 0.0;
         }
      }

      double curnorm = l2_norm(tmpdata.grad);
      curnorm = curnorm*curnorm;
      double oldnorm = 0.0;
      double termcond = cgeps*Math.sqrt(l2_norm(tmpdata.atb));

      while((curnorm > (termcond*termcond)) && (niter < maxiter))
      {
         if(niter == 0)
         {
            for(i=0;i<tmpdata.grad.length;i++)
            {
               tmpdata.tempsplit1[i] = tmpdata.grad[i];
            }
         }
         else
         {
            double beta = curnorm/oldnorm;
            for(i=0;i<tmpdata.grad.length;i++)
            {
               tmpdata.tempsplit1[i] = tmpdata.grad[i] +(beta*tmpdata.tempsplit1[i]);
            }
         }
         
         compute_ax_arr(tmpdata.tempdist1,tmpdata.tempdist2,tmpdata.tempsplit1,arrdata.arr,psequ);
         compute_aty_arr(tmpdata.tempsplit2,tmpdata.tempx,tmpdata.tempdist1,arrdata,psequ);
         set_to_zero(tmpdata.tempsplit2,tmpdata.fixed);

         double alpha = 0.0;
         for(i=0;i<tmpdata.tempsplit2.length;i++)
         {
            alpha = alpha + (tmpdata.tempsplit1[i]*tmpdata.tempsplit2[i]);
         }
         alpha = curnorm/alpha;

         for(i=0;i<tmpdata.curx.length;i++)
         {
            tmpdata.curx[i] = tmpdata.curx[i] + (alpha*tmpdata.tempsplit1[i]);
            tmpdata.grad[i] = tmpdata.grad[i] - (alpha*tmpdata.tempsplit2[i]);
         }
   
         oldnorm = curnorm;
         curnorm = l2_norm(tmpdata.grad);
         curnorm = curnorm*curnorm;
         niter++;
      }
   }

   //This method collects the splits with negative weight
   /**
    Parameters
    curx       --> vector of split weights
    negweights --> collection for storing the indices of the splits with negative weight
   */
   private static void collect_negative_splits(double[] curx,SortedSet negweights)
   {
      int i = 0;
 
      negweights.clear();

      for(i=0;i<curx.length;i++)
      { 
         if(curx[i] < 0.0)
         {
            negweights.add(new SplitWeight(curx[i],i));            
         }
      }    
   }

   //This method filters the set of those splits with
   //negative weight in the current solution. The worst
   //are fixed to 0.0.
   /**
    Parameters
    negweights --> collection for storing the indices of the splits with negative weight
    curx       --> vector of split weights
    fixed      --> array of flags indicating which splits have a weight fixed to 0.0
   */
   private static boolean remove_worst_splits(SortedSet negweights,double[] curx,boolean[] fixed)
   {
      int nnegweights = negweights.size();
      boolean removedsomething = false;

      if(nnegweights > 0)
      {
         removedsomething = true;
         //Remove worst 60%
         int max = ((6*nnegweights)/10);
         int k = 0;
         Iterator iter = negweights.iterator();
         while((iter.hasNext()) && (k < max))
         {
            SplitWeight sw = (SplitWeight)(iter.next());
            curx[sw.index] = 0.0;
            fixed[sw.index] = true;
         }
      }
      return removedsomething;
   }

   //This method sets the values of fixed split weights
   //to 0.0
   /**
    Parameters
    x     --> input vector
    fixed --> array of flags indicating which splits have a weight fixed to 0.0
   */
   private static void set_to_zero(double[] x,boolean[] fixed)
   {
      int i = 0;
   
      for(i=0;i<x.length;i++)
      {
         if(fixed[i])
         {
            x[i] = 0.0;
         }
      }
   }

   
}
