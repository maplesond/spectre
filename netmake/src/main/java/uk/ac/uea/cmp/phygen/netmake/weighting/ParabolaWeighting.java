package uk.ac.uea.cmp.phygen.netmake.weighting;

/**
 * A vertex is weighted after its position in the component.
 * The inner vertices are lower weighted then the outer ones.
 * The weighting follows an adjusted parabola function.
 *
 * @author Sarah Bastkowski
 * @see S. Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class ParabolaWeighting extends Weighting {

    /**
     * Creates an ParabolaWeighting object
     * with a weighting list of the given size.
     *
     * @param size size of the weighting list
     */
    public ParabolaWeighting(int size) {
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
        double weightingparameter = 0.;
        double alpha = 0.; //normalization factor

        for (int j = 0; j < componentSize; j++) {
            alpha += (j - 0.5 * (componentSize - 1))
                    * (j - 0.5 * (componentSize - 1));
        }
        weightingparameter = 1. / alpha * (position - 0.5 * (componentSize - 1))
                * (position - 0.5 * (componentSize - 1));

        setWeightingParam(i, weightingparameter);
    }

    public void process(int i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}