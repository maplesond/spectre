package uk.ac.uea.cmp.spectre.core.ds.split;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dan on 21/01/14.
 */
public class SplitDistanceMap extends LinkedHashMap<Split, Double> {

    public SplitDistanceMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SplitDistanceMap(int initialCapacity) {
        super(initialCapacity);
    }

    public SplitDistanceMap() {
        super();
    }

    public SplitDistanceMap(Map<? extends Split, ? extends Double> m) {
        super(m);
    }

    /**
     * This will run in linear time.
     * @param splitBlock
     * @return
     */
    public Double getUsingSplitBlock(SplitBlock splitBlock) {

        for(Split split : this.keySet()) {
            if (split.getASide().equals(splitBlock) || split.getBSide().equals(splitBlock)) {
                return this.get(split);
            }
        }

        return null;
    }
}
