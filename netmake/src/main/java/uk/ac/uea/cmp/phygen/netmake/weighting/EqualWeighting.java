package uk.ac.uea.cmp.phygen.netmake.weighting;

/**
 * Every vertex of one component (after merging) gets the same weighting
 * parameter, so they are all equally weighted.
 *
 * @author Sarah Bastkowski
 * @see S. Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class EqualWeighting extends Weighting {

    /**
     * Creates an EqualWeighting object with a weighting list of the given size.
     *
     * @param size size of the weighting list
     */
    public EqualWeighting(int size) {
        super(size);
    }

    /**
     *
     * @param i index of weighting parameter to be updated
     * @param dummy is not used
     * @param componentSize size of component
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public void updateWeightingParam(int i, int dummy, int componentSize) {
        Double weightingparameter = 0.;

        weightingparameter = 1. / componentSize;

        setWeightingParam(i, weightingparameter);
    }

    public void process(int i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}