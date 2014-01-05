package uk.ac.uea.cmp.phygen.superq.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;

import java.util.List;

/**
 * Created by dan on 05/01/14.
 */
public class Holders {

    protected ZHolder z;
    protected WHolder w;
    protected U0Holder u0;
    protected U1Holder u1;
    protected NSHolder ns;
    protected THolder t;


    public Holders(List<Taxa> taxaSets, int N, CanonicalWeightedQuartetMap canonicalWeightedQuartets) {
        z = new ZHolder(taxaSets, N);
        w = new WHolder(taxaSets, N, canonicalWeightedQuartets);
        u0 = new U0Holder(taxaSets, N, canonicalWeightedQuartets);
        u1 = new U1Holder(taxaSets, N, canonicalWeightedQuartets);
        ns = new NSHolder(taxaSets, N, canonicalWeightedQuartets);
        t = new THolder(taxaSets, N, canonicalWeightedQuartets);
    }

    public ZHolder getZ() {
        return z;
    }

    public WHolder getW() {
        return w;
    }

    public U0Holder getU0() {
        return u0;
    }

    public U1Holder getU1() {
        return u1;
    }

    public NSHolder getNs() {
        return ns;
    }

    public THolder getT() {
        return t;
    }
}
