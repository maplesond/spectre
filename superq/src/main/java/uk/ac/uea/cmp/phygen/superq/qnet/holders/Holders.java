package uk.ac.uea.cmp.phygen.superq.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.superq.qnet.QNetException;

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

    /**
     * Initialises all QNet Holders, using the supplied paths and quartets
     * @param paths The initial paths
     * @param N The number of quartets
     * @param canonicalWeightedQuartets the canonical weighted quartets
     * @throws QNetException Thrown if there are any inconsistencies in the holder initialisation
     */
    public Holders(List<Taxa> paths, int N, CanonicalWeightedQuartetMap canonicalWeightedQuartets) throws QNetException {
        z = new ZHolder(paths, N);
        w = new WHolder(paths, N, canonicalWeightedQuartets);
        u0 = new U0Holder(paths, N, canonicalWeightedQuartets);
        u1 = new U1Holder(paths, N, canonicalWeightedQuartets);
        ns = new NSHolder(paths, N, canonicalWeightedQuartets);
        t = new THolder(paths, N, canonicalWeightedQuartets);
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


    /**
     * Finds the first path that contains the specified ID
     * @param paths The list of paths to search
     * @param id The id of the taxa to find
     * @return The first path found containing the specified taxa ID, or null.
     */
    public static Taxa findFirstPathContainingId(List<Taxa> paths, int id) {

        for (Taxa path : paths) {

            if (path.containsId(id)) {

                return path;
            }
        }

        return null;
    }
}
