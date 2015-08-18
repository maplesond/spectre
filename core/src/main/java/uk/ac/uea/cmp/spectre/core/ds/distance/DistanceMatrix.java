/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.distance;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by dan on 27/02/14.
 */
public interface DistanceMatrix {


    /**
     * Basic functionality to get, set and increment values in the distance matrix.  The functions are overridden to allow
     * the user to access a Distance matrix, through the taxon id, taxon name or the taxon object itself.
     */


    /**
     * Retrieves the distance between two taxa
     *
     * @param taxon1 First taxon
     * @param taxon2 Second taxon
     * @return The distance between taxon1 and taxon2, or null if not found
     */
    double getDistance(final Identifier taxon1, final Identifier taxon2);

    /**
     * Retrieves the distance between two taxa, using taxa indices
     *
     * @param taxon1Id ID of first taxon
     * @param taxon2Id ID of second taxon
     * @return The distance between taxon1 and taxon2, or null if not found
     */
    double getDistance(final int taxon1Id, final int taxon2Id);

    /**
     * Retrieves the distance between two taxa
     *
     * @param taxon1Name Name of first taxon
     * @param taxon2Name Name of second taxon
     * @return The distance between taxon1 and taxon2, or null if not found
     */
    double getDistance(final String taxon1Name, final String taxon2Name);

    /**
     * Sets the specified distance between the specified taxa
     *
     * @param taxon1 First taxon
     * @param taxon2 Second taxon
     * @param value Distance between first and second taxa
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final Identifier taxon1, final Identifier taxon2, final double value);

    /**
     * Sets the specified distance between the specified taxa using their names
     *
     * @param taxonName1 Name of first taxon
     * @param taxonName2 Name of second taxon
     * @param value Distance between first and second taxa
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final String taxonName1, final String taxonName2, final double value);

    /**
     * Sets the specified distance between the specified taxa using their ids
     *
     * @param taxonId1 ID of first taxon
     * @param taxonId2 ID of second taxon
     * @param value Distance between first and second taxa
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final int taxonId1, final int taxonId2, final double value);

    /**
     * Increments the distance between taxon1 and taxon2 by incValue.  Returns the new distance between taxon1 and taxon2
     *
     * @param taxon1 First taxon
     * @param taxon2 Second taxon
     * @param increment Amount to increment distance between first and second taxa
     * @return The value at [row][col] after incrementation.
     */
    double incrementDistance(final Identifier taxon1, final Identifier taxon2, final double increment);

    /**
     * Using taxa ids, increments the distance between taxon1 and taxon2 by incValue.  Returns the new distance between
     * taxon1 and taxon2
     *
     * @param taxon1Id ID of first taxon
     * @param taxon2Id ID of second taxon
     * @param increment Amount to increment distance between first and second taxa
     * @return The value at [row][col] after incrementation.
     */
    double incrementDistance(final int taxon1Id, final int taxon2Id, final double increment);

    /**
     * Using taxa names, increments the distance between taxon1 and taxon2 by incValue.  Returns the new value between
     * taxon1 and taxon2
     *
     * @param taxon1Name Name of first taxon
     * @param taxon2Name Name of second taxon
     * @param increment Amount to increment distance between first and second taxa
     * @return The value at [row][col] after incrementation.
     */
    double incrementDistance(final String taxon1Name, final String taxon2Name, final double increment);

    /**
     * Adds a new identifier into this distance matrix.  Should initialise distances from all other idnetifiers in this
     * matrix
     * @param id New taxon to add to the matrix
     */
    void addIdentifier(Identifier id);


    /**
     * Retrieves the entire taxa set associated with this DistanceMatrix.
     *
     * @return The set of taxa.
     */
    IdentifierList getTaxa();

    /**
     * Retrieves the entire taxa set associated with this DistanceMatrix, using the specified comparator.
     * @param comparator The comparator to use
     * @return The set of sorted taxa.
     */
    IdentifierList getTaxa(Comparator<Identifier> comparator);


    DistanceList getDistances(Identifier taxon);

    DistanceList getDistances(Identifier taxon, Comparator<Identifier> comparator);

    DistanceList getDistances(int taxonId);

    DistanceList getDistances(int taxonId, Comparator<Identifier> comparator);

    DistanceList getDistances(String taxonName);

    DistanceList getDistances(String taxonName, Comparator<Identifier> comparator);

    /**
     * Returns a list of DistanceList for all taxa in this matrix
     * @return A list of distance lists
     */
    List<DistanceList> getAllDistances();

    /**
     * Returns a list of distances for each taxon ordered using the specified Taxon comparator
     *
     * @param comparator How to order the distance lists
     * @return A List of DistanceLists for each taxon in this DistanceMatrix
     */
    List<DistanceList> getAllDistances(Comparator<Identifier> comparator);


    /**
     * Returns a copy on the underlying matrix as a 2D double array
     *
     * @return A copy of the distances represented as a 2D array.
     */
    double[][] getMatrix();

    /**
     * Returns a copy on the underlying matrix as a 2D double array
     *
     * @param comparator How the taxa should be sorted.
     * @return A copy of the distances represented as a 2D array.
     */
    double[][] getMatrix(Comparator<Identifier> comparator);

    /**
     * Returns the size (number of rows or number of columns) of the square matrix.  This is also equivalent to the
     * number of taxa in the system
     *
     * @return The size of the matrix along one dimension, or the number of taxa in the system.
     */
    int size();


    /**
     * Removes all entries relating to the supplied taxon from the distance matrix
     *
     * @param taxon The taxon to remove
     */
    void removeTaxon(Identifier taxon);

    /**
     * Removes all entries relating to the supplied taxon, based on its id, from the distance matrix
     *
     * @param taxonId ID of the taxon to remove
     */
    void removeTaxon(int taxonId);

    /**
     * Removes all entries relating to the supplied taxon, based on its name from the distance matrix
     *
     * @param taxonName Name of the taxon to remove
     */
    void removeTaxon(String taxonName);


    /**
     * Returns the map storing the distance matrix in non-redundant form
     *
     * @return The map representing the distance matrix
     */
    Map<Pair<Identifier, Identifier>, Double> getMap();

}
