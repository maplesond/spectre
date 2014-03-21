package uk.ac.uea.cmp.spectre.core.ds.distance;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

/**
 * Created by dan on 27/02/14.
 */
public interface DistanceList {

    double getDistance(Identifier taxon);

    double getDistance(int taxonId);

    double getDistance(String taxonName);


    /**
     * Sets the distance to the specified taxon
     *
     * @param taxon
     * @param value
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final Identifier taxon, final double value);

    /**
     * Sets the distance to the specified taxon using its name
     *
     * @param taxonName
     * @param value
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final String taxonName, final double value);

    /**
     * Sets the distance to the specified taxa using its id
     *
     * @param taxonId
     * @param value
     * @return The previous distance set between the two taxa, or 0.0 if the value has never been set before
     */
    double setDistance(final int taxonId, final double value);


    /**
     * Increments the distance to the specified taxon by incValue.  Returns the new distance to the specified taxon.
     *
     * @param taxon
     * @param increment
     * @return The value after incrementation.
     */
    double incrementDistance(final Identifier taxon, final double increment);

    /**
     * Increments the distance to the specified taxon by incValue.  Returns the new distance to the specified taxon.
     *
     * @param taxonId
     * @param increment
     * @return The value after incrementation.
     */
    double incrementDistance(final int taxonId, final double increment);

    /**
     * Increments the distance to the specified taxon by incValue.  Returns the new distance to the specified taxon.
     *
     * @param taxonName
     * @param increment
     * @return The value after incrementation.
     */
    double incrementDistance(final String taxonName, final double increment);


    /**
     * Returns the taxon represented by this DistanceList
     *
     * @return The taxon master taxon to which all distances in this list relate to
     */
    Identifier getTaxon();


    /**
     * Gets all the other taxa targeted by this DistanceList
     *
     * @return The list of all taxa that this taxon relates to
     */
    IdentifierList getOtherTaxa();


    /**
     * Sums up all the distances in this DistanceList.
     *
     * @return The sum of distances from this element to all the others.
     */
    double sum();


}
