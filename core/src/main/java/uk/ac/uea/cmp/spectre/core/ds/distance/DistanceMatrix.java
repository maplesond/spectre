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
     * @param taxon1
     * @param taxon2
     * @return The distance between taxon1 and taxon2, or null if not found
     */
    double getDistance(final Identifier taxon1, final Identifier taxon2);

    /**
     * Retrieves the distance between two taxa, using taxa indices
     *
     * @param taxon1Id
     * @param taxon2Id
     * @return The distance between taxon1 and taxon2, or null if not found
     */
    double getDistance(final int taxon1Id, final int taxon2Id);

    /**
     * Retrieves the distance between two taxa
     *
     * @param taxon1Name
     * @param taxon2Name
     * @return The distance between taxon1 and taxon2, or null if not found
     */
    double getDistance(final String taxon1Name, final String taxon2Name);

    /**
     * Sets the specified distance between the specified taxa
     * @param taxon1
     * @param taxon2
     * @param value
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final Identifier taxon1, final Identifier taxon2, final double value);

    /**
     * Sets the specified distance between the specified taxa using their names
     * @param taxonName1
     * @param taxonName2
     * @param value
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final String taxonName1, final String taxonName2, final double value);

    /**
     * Sets the specified distance between the specified taxa using their ids
     * @param taxonId1
     * @param taxonId2
     * @param value
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final int taxonId1, final int taxonId2, final double value);

    /**
     * Increments the distance between taxon1 and taxon2 by incValue.  Returns the new distance between taxon1 and taxon2
     *
     * @param taxon1
     * @param taxon2
     * @param increment
     * @return The value at [row][col] after incrementation.
     */
    double incrementDistance(final Identifier taxon1, final Identifier taxon2, final double increment);

    /**
     * Using taxa ids, increments the distance between taxon1 and taxon2 by incValue.  Returns the new distance between
     * taxon1 and taxon2
     *
     * @param taxon1Id
     * @param taxon2Id
     * @param increment
     * @return The value at [row][col] after incrementation.
     */
    double incrementDistance(final int taxon1Id, final int taxon2Id, final double increment);

    /**
     * Using taxa names, increments the distance between taxon1 and taxon2 by incValue.  Returns the new value between
     * taxon1 and taxon2
     *
     * @param taxon1Name
     * @param taxon2Name
     * @param increment
     * @return The value at [row][col] after incrementation.
     */
    double incrementDistance(final String taxon1Name, final String taxon2Name, final double increment);



    void addIdentifier(Identifier id);


    /**
     * Retrieves the entire taxa set associated with this DistanceMatrix.
     *
     * @return The set of taxa.
     */
    IdentifierList getTaxa();

    /**
     * Retrieves the entire taxa set associated with this DistanceMatrix, using the specified comparator.
     *
     * @return The set of sorted taxa.
     */
    IdentifierList getTaxa(Comparator<Identifier> comparator);


    DistanceList getDistances(Identifier taxon, Comparator<Identifier> comparator);
    DistanceList getDistances(int taxonId, Comparator<Identifier> comparator);
    DistanceList getDistances(String taxonName, Comparator<Identifier> comparator);

    /**
     *
     * @return
     */
    List<DistanceList> getAllDistances();

    /**
     * Returns a list of distances for each taxon ordered using the specified Taxon comparator
     * @param comparator How to order the distance lists
     * @return A List of DistanceLists for each taxon in this DistanceMatrix
     */
    List<DistanceList> getAllDistances(Comparator<Identifier> comparator);


    /**
     * Returns a copy on the underlying matrix as a 2D double array
     * @return A copy of the distances represented as a 2D array.
     */
    double[][] getMatrix();

    /**
     * Returns a copy on the underlying matrix as a 2D double array
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
     * @param taxon
     */
    void removeTaxon(Identifier taxon);

    /**
     * Removes all entries relating to the supplied taxon, based on its id, from the distance matrix
     * @param taxonId
     */
    void removeTaxon(int taxonId);

    /**
     * Removes all entries relating to the supplied taxon, based on its name from the distance matrix
     * @param taxonName
     */
    void removeTaxon(String taxonName);


    /**
     * Returns the map storing the distance matrix in non-redundant form
     * @return The map representing the distance matrix
     */
    Map<Pair<Identifier,Identifier>, Double> getMap();

}
