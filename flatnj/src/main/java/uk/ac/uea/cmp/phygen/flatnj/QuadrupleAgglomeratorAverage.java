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

package uk.ac.uea.cmp.phygen.flatnj;

import uk.ac.uea.cmp.phygen.core.util.CollectionUtils;
import uk.ac.uea.cmp.phygen.flatnj.ds.Quadruple;
import uk.ac.uea.cmp.phygen.flatnj.ds.QuadrupleSystem;

/**
 * {@linkplain QuadrupleSystem} agglomerator that averages weights in each pair of
 * joined {@linkplain uk.ac.uea.cmp.phygen.flatnj.ds.Quadruple}s.
 * 
 * @author balvociute
 */
public class QuadrupleAgglomeratorAverage implements QuadrupleAgglomerator
{
    @Override
    public void agglomerate(QuadrupleSystem qs, Neighbours neighbours)
    {       
        int[] taxa = CollectionUtils.getElements(qs.getTaxa());
                
        int a = neighbours.getA();
        int b = neighbours.getB();
        
        int[] i = new int[3];
        
        for (int i1 = 0; i1 < taxa.length; i1++)
        {
            i[0] = taxa[i1];
            if(i[0] != a && i[0] != b)
            {
                for (int i2 = i1+1; i2 < taxa.length; i2++)
                {
                    i[1] = taxa[i2];
                    if(i[1] != a && i[1] != b)
                    {
                        for (int i3 = i2+1; i3 < taxa.length; i3++)
                        {
                            i[2] = taxa[i3];
                            if(i[2] != a && i[2] != b)
                            {
                                Quadruple qA = qs.getQuadrupleUnsorted(a, i[0], i[1], i[2]);
                                Quadruple qB = qs.getQuadrupleUnsorted(b, i[0], i[1], i[2]);
                                averageWeights(a, b, qA, qB);
                            }
                        }
                    }
                }
            }
        }
        
        qs.setInactive(b);
    }
      
    /**
     * Averages weights of quadruple splits given {@linkplain Quadruple}s and sets 
     * those weights to quadruple qA.
     * @param a index of the first taxa from {@linkplain Neighbours}.
     * @param b index of the second taxa from {@linkplain Neighbours}.
     * @param qA {@linkplain Quadruple} containing taxa a.
     * @param qB {@linkplain Quadruple} containing taxa b.
     */
    private void averageWeights(int a, int b, Quadruple qA, Quadruple qB)
    {        
        int aIndex = qA.getTaxa(a, 0);
        
        double[] weights = new double[7];
        
        int bIndex = qB.getTaxa(b, 0);
        
        double[] weights1 = new double[7];
        System.arraycopy(qA.getWeights(), 0, weights1, 0, 7);
        double[] weights2 = new double[7];
        System.arraycopy(qB.getWeights(), 0, weights2, 0, 7);
        
        if (bIndex - aIndex == 1)
        {
            CollectionUtils.swapTwoInAnArray(weights2, aIndex, bIndex);
            int index2 = (aIndex == 1) ? 4 : 6;
            CollectionUtils.swapTwoInAnArray(weights2, 5, index2);
        }
        if(bIndex - aIndex == 2)
        {
            CollectionUtils.swapTwoInAnArray(weights2, bIndex, bIndex-1);
            CollectionUtils.swapTwoInAnArray(weights2, aIndex, bIndex-1);
            if(aIndex == 0)
            {
                CollectionUtils.swapTwoInAnArray(weights2, 4, 5);
                CollectionUtils.swapTwoInAnArray(weights2, 5, 6);
            }
            if(aIndex == 1)
            {
                CollectionUtils.swapTwoInAnArray(weights2, 5, 6);
                CollectionUtils.swapTwoInAnArray(weights2, 4, 5);
            }
        }
        if(bIndex - aIndex == 3)
        {
            CollectionUtils.swapTwoInAnArray(weights2, 2, 3);
            CollectionUtils.swapTwoInAnArray(weights2, 1, 2);
            CollectionUtils.swapTwoInAnArray(weights2, 0, 1);
            CollectionUtils.swapTwoInAnArray(weights2, 4, 6);
        }
        
        for(int i = 0; i < weights.length; i++)
        {
            weights[i] = (weights1[i]+weights2[i])/2.0;
        }
        qA.setWeights(weights);
    }
}
