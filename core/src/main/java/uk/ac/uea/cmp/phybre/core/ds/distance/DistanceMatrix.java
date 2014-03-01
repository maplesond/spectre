package uk.ac.uea.cmp.phybre.core.ds.distance;

import uk.ac.uea.cmp.phybre.core.ds.Taxa;
import uk.ac.uea.cmp.phybre.core.ds.Taxon;

import java.util.Comparator;
import java.util.List;

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
    double getDistance(final Taxon taxon1, final Taxon taxon2);

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
    double setDistance(final Taxon taxon1, final Taxon taxon2, final double value);

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
    double incrementDistance(final Taxon taxon1, final Taxon taxon2, final double increment);

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




    /**
     * Returns the number of taxa represented by this distance matrix
     * @return number of taxa in this distance matrix
     */
    int getNbTaxa();

    /**
     * Retrieves the entire taxa set associated with this DistanceMatrix.
     *
     * @return The set of taxa.
     */
    Taxa getTaxa();

    /**
     * Retrieves the entire taxa set associated with this DistanceMatrix, using the specified comparator.
     *
     * @return The set of sorted taxa.
     */
    Taxa getTaxa(Comparator<Taxon> comparator);


    DistanceList getDistances(Taxon taxon, Comparator<Taxon> comparator);
    DistanceList getDistances(int taxonId, Comparator<Taxon> comparator);
    DistanceList getDistances(String taxonName, Comparator<Taxon> comparator);

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
    List<DistanceList> getAllDistances(Comparator<Taxon> comparator);


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
    double[][] getMatrix(Comparator<Taxon> comparator);

    /**
     * Returns the size (number of rows or number of columns) of the square matrix.  This is also equivalent to the
     * number of taxa in the system
     *
     * @return The size of the matrix along one dimension, or the number of taxa in the system.
     */
    int size();




}
