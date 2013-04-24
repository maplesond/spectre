package uk.ac.uea.cmp.phygen.netmake.weighting;

/**
 * Resulting circular order is solution for TSPWeighting.
 *
 * @author Sarah Bastkowski
 * @see S. Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class TSPWeighting extends Weighting {

    /**
     * Creates an TSPWeighting object with a weighting list of the given size.
     *
     * @param size size of the weighting list
     */
    public TSPWeighting(int size) {
        super(size);
    }

    /**
     *
     * @param i index of weighting parameter to be updated
     * @param position position of i in component
     * @param componentSize size of component
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public void updateWeightingParam(int i, int position, int componentSize) {
        Double weightingparameter = 0.;

        if (componentSize == 1) {
            weightingparameter = 1.;
        }
        if (componentSize > 1
                && (position ==  0 || position == (componentSize - 1))) {
            weightingparameter = 0.5;
        } else {
            weightingparameter = 0.;
        }

        setWeightingParam(i, weightingparameter);
    }

    public void process(int i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}
