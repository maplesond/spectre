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

package uk.ac.uea.cmp.phygen.flatnj.ds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author balvociute
 */
public class DistanceMatrix
{
    /* matrix stores all pairwise distances between the leters */
    protected double[][] matrix;
    
    protected double same;
    protected double different;
    
    /* symbols maps letters included in the distance matrix to their indexes in
     * the matrix.
     */
    private Map<Character,Integer> symbols;
    /* letters is used to store all the letters in the same order as they are in
     * the distance antrix
     */
    private char[] letters;
    /* matrix stores all pairwise distances between the leters */
    

    public DistanceMatrix()
    {
        matrix = null;
        same = 0;
        different = 1;
    }
    
    public DistanceMatrix(double[][] distances, char[] letters)
    {
        this.matrix = distances;
        this.letters = letters;
        
        symbols = new HashMap();
        for (int i = 0; i < letters.length; i++)
        {
            symbols.put(letters[i], i);
        }
    }
    
    public DistanceMatrix(double[][] distances)
    {
        this.matrix = distances;
    }

    public double[][] getMatrix()
    {
        return matrix;
    }
 
    //Just print the distance matrix to the screen
    public void printDistanceMatrix()
    {
        for(int i = 0; i < letters.length; i++)
        {
            System.out.print("\t" + letters[i]);
        }
        System.out.println();
        for (int i1 = 0 ; i1 < matrix.length; i1++)
        {
            /* Distance matrix is printed so that it would be symetric */
            System.out.print(letters[i1]);
            for (int i2 = 0 ; i2 < matrix.length; i2++)
            {
                System.out.print("\t" + matrix[i1][i2]);
            }
            System.out.println();
        }
    }
    
    //Returns characters that are present in the matrix
    //a.k.a. all allowable characters
    public Set<Character> getSymbols()
    {
        Set<Character> s = new HashSet();
        for (int i = 0; i < letters.length; i++)
        {
            s.add(letters[i]);
        }
        return s;
    }
    
    //Returns distance between two characters
    public double getDistance(char s1, char s2)
    {
        if(matrix != null)
        {
            return matrix[symbols.get(s1)][symbols.get(s2)];
        }
        else
        {
            if(s1 == s2)
            {
                return same;
            }
            else
            {
                return different;
            }
        }
    }

    //Returns distance between two characters by their indices
    public double getDistance(int s1, int s2)
    {
        if(matrix != null)
        {
            return matrix[s1][s2];
        }
        else
        {
            if(s1 == s2)
            {
                return same;
            }
            else
            {
                return different;
            }
        }
    }

    //Returns number of characters
    public int getSize()
    {
        return matrix.length;
    }
}
