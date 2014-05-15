package uk.ac.uea.cmp.spectre.core.ds.quartet;

import org.apache.commons.lang3.tuple.Pair;

import java.text.NumberFormat;

/**
 * Created by maplesod on 15/05/14.
 */
public interface Quartet extends Comparable<Quartet> {

    /**
     * Creates a new copy of this quartet that has already been sorted
     * @return A sorted copy of this quartet
     */
    Quartet createSortedCopy();

    int getA();

    int getB();

    int getC();

    int getD();

    /**
     *
     * @return
     */
    Pair<Quartet, Integer> getGroupKeys();

    /**
     * This method checks whether or not for given taxa indices are distinct.
     * @return True if this quartet contains all distinct elements, false otherwise.
     */
    boolean areDistinct();

    /**
     * Returns the taxa indices represented by this quartet as an int array.
     * @return An int array representing this quartet
     */
    int[] toIntArray();

    /**
     * Returns a string representation of this quartet using the specified number format.
     * @param quartetFormat The NumberFormat to represent the taxa indicies represented by this quartet
     * @return a string representation of this quartet
     */
    String toString(NumberFormat quartetFormat);
}
