package uk.ac.uea.cmp.phygen.core.ds.quartet;

import java.util.HashMap;

/**
 * Created by dan on 04/01/14.
 */
public class CanonicalWeightedQuartetMap extends HashMap<Quartet, Double> {

    public void incrementWeight(Quartet quartet, double weightIncrement) {

        this.put(quartet, this.containsKey(quartet) ? this.get(quartet) + weightIncrement : weightIncrement);
    }
}
