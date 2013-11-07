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
package uk.ac.uea.cmp.phygen.core.ds.distance;

import uk.ac.uea.cmp.phygen.core.math.Statistics;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Immutable class representing the distances between each taxa provided
 */
public class DistanceMatrix {

    private final String[] taxa;
    private final double[][] matrix;
    private final int nbTaxa;


    /**
     * Creates a new DistanceMatrix the specified size with the default taxa values and all distances set to 0.0.
     *
     * @param size The size of the distance matrix (number of taxa)
     */
    public DistanceMatrix(final int size) {
        this(createDefaultTaxaSet(size));
    }

    /**
     * Creates a new DistanceMatrix object using a taxa set.  Will validate the taxa set
     * to ensure there's data to work with, and that there are no duplicated elements.
     * The distances matrix is initialised so that all elements are 0.0.
     *
     * @param taxa The taxa set.
     */
    public DistanceMatrix(final String[] taxa) {
        this(taxa, 0.0);
    }


    /**
     * Creates a new DistanceMatrix object of specified size with the default taxa values and all distances set to
     * the specified value
     *
     * @param size The size of the distance matrix (number of taxa)
     * @param val  The value to initialise each element to
     */
    public DistanceMatrix(final int size, final double val) {
        this(createDefaultTaxaSet(size), val);
    }

    /**
     * Creates a new DistanceMatrix object using a taxa set.  Will validate the taxa set
     * to ensure there's data to work with, and that there are no duplicated elements.
     * The distances matrix is initialised so that all elements are set to val.
     *
     * @param taxa The taxa set.
     * @param val  The value to initalise all elements of the distance matrix with.
     */
    public DistanceMatrix(final String[] taxa, final double val) {
        validateTaxa(taxa);

        this.taxa = taxa;
        this.nbTaxa = taxa.length;
        this.matrix = new double[this.nbTaxa][this.nbTaxa];

        this.fill(val);
    }

    /**
     * Creates a deep copy of the provided DistanceMatrix object.  Ensures this object
     * is in a valid state.
     *
     * @param copy The copy to duplicate in this object.
     */
    public DistanceMatrix(final DistanceMatrix copy) {
        this.taxa = copyTaxa(copy.taxa);
        this.matrix = copyDistances(copy.matrix);
        this.nbTaxa = copy.nbTaxa;

        validate();
    }

    /**
     * Creates a new DistanceMatrix object by making a deep copy of the provided taxa
     * set and distances matrix. Ensures this object is in a valid state.
     *
     * @param distances
     */
    public DistanceMatrix(final String[] taxa, double[][] distances) {
        this.taxa = copyTaxa(taxa);
        this.matrix = copyDistances(distances);
        this.nbTaxa = taxa.length;

        validate();
    }

    /**
     * Creates a default taxa set, which starts at 1 and goes up to the specified
     * size.
     *
     * @param size The size of the taxa set to create.
     * @return A default taxa set of specified size, with incrementing integer values
     *         starting at 1 and going up to the specified size.
     */
    protected static String[] createDefaultTaxaSet(final int size) {
        String[] nt = new String[size];

        for (int i = 1; i <= size; i++) {
            nt[i - 1] = Integer.toString(i);
        }

        return nt;
    }

    /**
     * Creates a deep copy of a taxa set.
     *
     * @param copy The taxa set to copy.
     * @return A copy of the provided taxa set.
     */
    protected final String[] copyTaxa(final String[] copy) {
        String[] newTaxa = new String[copy.length];
        System.arraycopy(copy, 0, newTaxa, 0, copy.length);
        return newTaxa;
    }

    /**
     * Creates a deep copy of a distance matrix.
     *
     * @param copy The distance matrix to copy.
     * @return A new copy of the provided distance matrix.
     */
    protected final double[][] copyDistances(final double[][] copy) {
        double[][] newMatrix = new double[copy.length][copy.length];
        for (int i = 0; i < copy.length; i++) {
            System.arraycopy(copy[i], 0, newMatrix[i], 0, copy.length);
        }
        return newMatrix;
    }

    /**
     * Ensure the provided taxa set is valid.  This means that it is non-null and
     * contains at least one element.  Also there must be no duplicated elements.
     *
     * @param taxa The taxa set to validate.
     */
    protected final void validateTaxa(final String[] taxa) {
        // Ensure we have some taxa to work with.
        if (taxa == null || taxa.length == 0) {
            throw new NullPointerException("Must specify taxa");
        }

        // Ensure no duplicated elements in the taxa set.
        LinkedHashSet<String> tCheck = new LinkedHashSet<String>(Arrays.asList(taxa));
        if (tCheck.size() != taxa.length) {
            throw new IllegalArgumentException("Provided taxa set contains duplicated elements.");
        }
    }

    /**
     * Ensure the provided taxa set is valid.  This means that it is non-null and
     * contains at least one element.  Also there must be no duplicated elements.
     *
     * @param distances The 2D array of distances to validate.
     */
    protected final void validateDistances(final double[][] distances) {
        // Ensure we have some taxa to work with.
        if (distances == null || distances.length == 0 || distances[0] == null || distances[0].length == 0) {
            throw new NullPointerException("Must specify distances");
        }

        if (distances.length != distances[0].length) {
            throw new IllegalArgumentException("Distance matrix is not square");
        }

        for (double[] row : distances) {
            for (double d : row) {
                if (d < 0.0)
                    throw new IllegalArgumentException("Distance matrix contains negative numbers");
            }
        }
    }

    /**
     * Ensures this class is in a valid state.
     */
    protected final void validate() {
        validateTaxa(this.taxa);
        validateDistances(this.matrix);

        if (this.taxa.length != this.matrix.length && this.taxa.length == this.nbTaxa)
            throw new IllegalArgumentException("Distance matrix is not the same size as the taxa set.");
    }

    @Override
    public boolean equals(Object d) {
        if (d instanceof DistanceMatrix) {
            DistanceMatrix dd = (DistanceMatrix) d;

            if (!Arrays.equals(taxa, dd.taxa))
                return false;

            for (int i = 0; i < nbTaxa; i++) {
                if (!Arrays.equals(matrix[i], dd.matrix[i]))
                    return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Arrays.deepHashCode(this.taxa);
        hash = 79 * hash + Arrays.deepHashCode(this.matrix);
        return hash;
    }

    /**
     * Returns the number of taxa represented by this distance matrix
     * @return number of taxa in this distance matrix
     */
    public int getNbTaxa() {
        return nbTaxa;
    }

    /**
     * Retrieves the entire taxa set.
     *
     * @return The set of taxa.
     */
    public String[] getTaxaSet() {
        return this.taxa;
    }

    /**
     * Retrieves the taxa at the given position.
     *
     * @param i The index of the taxa to return.
     * @return The taxa
     */
    public String getTaxa(final int i) {
        return this.taxa[i];
    }

    /**
     * Sets the taxa at the given position
     *
     * @param i    The index of the taxa to set
     * @param taxa The String representation of the taxa to set.
     */
    public void setTaxa(final int i, String taxa) {
        this.taxa[i] = taxa;
    }

    /**
     * Sets the distance between two taxa.
     *
     * @param row
     * @param col
     * @param value
     */
    public void setDistance(final int row, final int col, final double value) {
        matrix[row][col] = value;
    }

    /**
     * Retrieves the distance between two taxa
     *
     * @param row
     * @param col
     * @return
     */
    public double getDistance(final int row, final int col) {
        return matrix[row][col];
    }

    /**
     * Increments the distance at [row][col] by incValue.  Returns the new value at [row][col]
     *
     * @param row
     * @param col
     * @param incValue
     * @return
     */
    public double incrementDistance(final int row, final int col, final double incValue) {
        matrix[row][col] += incValue;
        return matrix[row][col];
    }

    /**
     * Retrieves a single row of the distance matrix
     *
     * @param row The index of the row to return.
     * @return The actual selected row of the distance matrix
     */
    public double[] getRow(final int row) {
        return matrix[row];
    }

    /**
     * Returns a handle on the underlying matrix
     * @return
     */
    public double[][] getMatrix() {
        return this.matrix;
    }

    /**
     * Retrieves a single row of the distance matrix in string form.
     *
     * @param row The index of the row to return.
     * @return The string form of the actual selected row of the distance matrix
     */
    public String getRowAsString(final int row) {
        StringBuilder sb = new StringBuilder();

        for (double d : matrix[row]) {
            sb.append(d);
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    /**
     * Returns the size (number of rows or number of columns) of the square matrix.  This is also equivalent to the
     * number of taxa in the system
     *
     * @return The size of the matrix along one dimension, or the number of taxa in the system.
     */
    public int size() {
        return nbTaxa;
    }

    /**
     * Returns the total number of elements in the 2D square distance matrix
     *
     * @return The total number of elements in the matrix
     */
    public int elements() {
        return nbTaxa * nbTaxa;
    }

    /**
     * Sums up all the distances in the row, which is equivalent to the summed up
     * distances from this element to all the others.
     *
     * @param row The row to sum
     * @return The sum of distances from this element (row index == one taxon) to
     *         all the others.
     */
    public double sumRow(final int row) {

        return Statistics.sumDoubles(matrix[row]);
    }


    /**
     * Fills the distance matrix with the specified value
     *
     * @param val The value to be set in all elements of the distance matrix
     */
    public void fill(double val) {

        final int n = this.size();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.matrix[i][j] = val;
            }
        }
    }

    /**
     * Represents the distance matrix with each row on a new line surrounded with
     * square brackets, with columns delimited with commas.
     *
     * @return String representation of the Distance matrix.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (int i = 0; i < nbTaxa; i++) {
            sb.append("\"");
            sb.append(taxa[i]);
            sb.append("\"");

            if (i != nbTaxa - 1)
                sb.append(",");
        }
        sb.append("]\n");
        for (int i = 0; i < nbTaxa; i++) {
            sb.append("[");
            for (int j = 0; j < nbTaxa; j++) {
                sb.append(matrix[i][j]);

                if (j != nbTaxa - 1)
                    sb.append(",");
            }
            sb.append("]");
            sb.append("\n");
        }

        return sb.toString();
    }

}
