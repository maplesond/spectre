package uk.ac.uea.cmp.phygen.netmake.weighting;

/**
 * Fills and updates weighting list
 *
 * @author Sarah Bastkowski
 * @see S. Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public abstract class Weighting {
    private Double[] weightingParameters;

    /**
     * Does nothing... therefore weighting params array will not be initialised.
     */
    public Weighting()
    {}

    /**
     *
     * @param size the number of weighting parameters to be stored
     */
    public Weighting(int size) {
        weightingParameters = new Double[size];

        for (int i = 0; i < weightingParameters.length; i++) {
            weightingParameters[i] = 1.;
        }
    }

    /**
     * Updates the weighting parameter at a specified position.
     *
     * @param i index of the weighting parameter to be updated
     * @param position
     * @param customParameter parameter depending on implemented algorithm
     */
    public abstract void updateWeightingParam(int i, int position, int customParameter);

    /**
     * Returns the weighting parameter stored at the specified position
     * in the weighting array.
     *
     * @param i index of the weighting parameter to be returned
     * @return the weighting parameter stored at the specified position
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     */
    public Double getWeightingParam(int i) {
        return weightingParameters[i];
    }

    /**
     * Replaces the weighting parameter at the specified position
     * in the weighting array with a new value.
     *
     * @param i index of the value to be replaced
     * @param newValue value that replaces the one at the specified position
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     */
    public void setWeightingParam(int i, Double newValue) {
        weightingParameters[i] = newValue;
    }
}