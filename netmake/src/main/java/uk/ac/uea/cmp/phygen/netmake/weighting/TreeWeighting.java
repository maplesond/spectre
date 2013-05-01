package uk.ac.uea.cmp.phygen.netmake.weighting;

/**
 * Weighting of vertices in merged component depends on number of vertices in
 * original component before mergin
 * (equal to number of times the components has been choosen in selection step)
 *
 * @author Sarah Bastkowski
 * @see S. Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class TreeWeighting extends Weighting {
    private double alpha;

    public TreeWeighting(int size) {
        this(size, 0.5);
    }


    /**
     * Creates a TreeWeighting object with a weighting list of a specified size
     * and a specified constant alpha.
     *
     * @param size the number of weighting parameters to be stored
     * @param alpha 0.5 for balanced tree weighting leads to NJ tree
     */
    public TreeWeighting(int size, double alpha) {
        super(size);

        this.alpha = alpha;
    }

    /**
     *
     * @param i index of weighting parameter to be updated
     * @param position position of i in component
     * @param componentSplitposition position where second component begins after merging step
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public void updateWeightingParam(int i, int position,
                                     int componentSplitposition) {
        double weightingparameter = 0.;

        if (position < componentSplitposition) {
            weightingparameter = alpha * getWeightingParam(i);
        } else {
            weightingparameter = (1. - alpha) * getWeightingParam(i);
        }

        setWeightingParam(i, weightingparameter);
    }

    public void process(int i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}