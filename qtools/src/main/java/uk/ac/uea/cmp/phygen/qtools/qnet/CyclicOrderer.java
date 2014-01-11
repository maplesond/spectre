package uk.ac.uea.cmp.phygen.qtools.qnet;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetGroupMap;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.qtools.qnet.holders.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 08/01/14.
 */
public class CyclicOrderer {

    private static Logger log = LoggerFactory.getLogger(CyclicOrderer.class);

    private Holders holders;

    private int N;

    private static final double c = 0.5;

    public CyclicOrderer() {

        holders = null;
        N = -1;
    }

    /**
     * Computes a circular ordering from a combination of quartet systems
     * @param taxa All of the taxa used in the merged quartet system
     * @param theQuartetWeights The quartet groups mapped to the normalised weights
     * @return A circular ordering
     * @throws QNetException if there were any problems.
     */
    public CircularOrdering computeCircularOrdering(Taxa taxa, WeightedQuartetGroupMap theQuartetWeights)
            throws QNetException {

        // This method is probably going to take a while so start a timer.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        // Number of taxa in combined quartet system
        this.N = taxa.size();

        // Initialise paths
        List<Taxa> paths = this.initPaths(taxa);

        // Initialise the X'
        List<Integer> X = this.initXPrime(taxa);


        // Convert grouped quartets to canonical quartets
        CanonicalWeightedQuartetMap canonicalWeightedQuartets = new CanonicalWeightedQuartetMap(theQuartetWeights);

        log.debug("Initialising QNet Holders");

        // Init all the holders
        this.holders = new Holders(paths, N, canonicalWeightedQuartets);

        log.debug("QNet Holders initialised");

        // Iterate N-3 times
        int nbIterations = N-3;

        log.info("Processing " + nbIterations + " path joining iterations");
        for (int p = 1; p <= nbIterations; p++) {

            paths = this.iteration(X, paths);

            log.debug("Path joining iteration " + p + " of " + nbIterations + " complete.");
        }

        log.debug("Commencing QNet termination step");

        // termination step
        paths = this.terminationJoin(X.get(0), X.get(1), X.get(2), paths);

        // Now, the lists should contain the desired circular ordering, convert those into a circular ordering object
        CircularOrdering circularOrdering = new CircularOrdering(paths.get(0).getIds());

        log.info("QNet circular ordering computed: " + circularOrdering.toString());

        stopWatch.stop();
        log.info("Time taken to compute ordering: " + stopWatch.toString());

        return circularOrdering;
    }

    private List<Taxa> initPaths(Taxa taxa) {

        List<Taxa> paths = new ArrayList<>();

        for(Taxon taxon : taxa) {

            Taxa path = new Taxa();
            path.add(new Taxon(taxon));
            paths.add(path);
        }

        return paths;
    }

    private List<Integer> initXPrime(Taxa taxa) {

        List<Integer> X = new ArrayList<>();

        for (Taxon taxon : taxa) {

            // The taxa set X (prime)
            X.add(taxon.getId());
        }

        return X;
    }


    protected List<Taxa> iteration(List<Integer> X, List<Taxa> paths) throws QNetException {

        // find a, b, a < b, in X so s (a, b) / n (a, b) maximal
        Pair<Integer, Integer> ab = this.findMaxAB(X);
        int a = ab.getLeft();
        int b = ab.getRight();

        // Get a join type
        int y = this.holders.selectJoin2(a, b, c, X);

        if (y < 0) {

            throw new QNetException("Join type unknown!");
        }

        // Make shortcuts to directions
        Taxa.Direction FORWARD = Taxa.Direction.FORWARD;
        Taxa.Direction BACKWARD = Taxa.Direction.BACKWARD;

        // Perform the requested path join
        switch(y) {
            case 1:
                join2(paths, a, BACKWARD, b, FORWARD);
                break;
            case 2:
                join2(paths, a, BACKWARD, b, BACKWARD);
                break;
            case 3:
                join2(paths, a, FORWARD, b, FORWARD);
                break;
            case 4:
                join2(paths, a, FORWARD, b, BACKWARD);
                break;
        }


        // remove b from X
        X.remove(new Integer(b));

        // Update counts and weights in the holders
        this.holders.update(a, b, y, X);

        return paths;
    }


    protected List<Taxa> terminationJoin(int i, int j, int k, List<Taxa> taxaSets) throws QNetException {

        // Work out which path join to use
        int y = this.holders.selectJoin3(i, j, k, c);

        // Make shortcuts to directions
        Taxa.Direction FORWARD = Taxa.Direction.FORWARD;
        Taxa.Direction BACKWARD = Taxa.Direction.BACKWARD;

        // Perform the requested path join
        switch(y) {
            case 1:
                join3(taxaSets, i, FORWARD, j, FORWARD, k, FORWARD);
                break;
            case 2:
                join3(taxaSets, i, FORWARD, j, FORWARD, k, BACKWARD);
                break;
            case 3:
                join3(taxaSets, i, FORWARD, j, BACKWARD, k, FORWARD);
                break;
            case 4:
                join3(taxaSets, i, FORWARD, j, BACKWARD, k, BACKWARD);
                break;
            case 5:
                join3(taxaSets, i, BACKWARD, j, FORWARD, k, FORWARD);
                break;
            case 6:
                join3(taxaSets, i, BACKWARD, j, FORWARD, k, BACKWARD);
                break;
            case 7:
                join3(taxaSets, i, BACKWARD, j, BACKWARD, k, FORWARD);
                break;
            case 8:
                join3(taxaSets, i, BACKWARD, j, BACKWARD, k, BACKWARD);
                break;
        }

        return taxaSets;
    }

    protected List<Taxa> join2(List<Taxa> paths, int taxon1, Taxa.Direction reversed1,
                               int taxon2, Taxa.Direction reversed2) throws QNetException {

        Taxa tL1 = null, tL2 = null;

        for (Taxa path : paths) {

            if (path.containsId(taxon1)) {
                tL1 = path;
            }

            if (path.containsId(taxon2)) {
                tL2 = path;
            }
        }

        if (tL1 == null || tL2 == null) {
            throw new QNetException("Couldn't find specified taxon ids in any path.  Taxon 1: " + taxon1 + "; Taxon 2: " + taxon2);
        }

        paths.remove(tL1);
        paths.remove(tL2);

        Taxa tL12 = Taxa.join(tL1, reversed1, tL2, reversed2);

        paths.add(tL12);

        return paths;

    }

    protected List<Taxa> join3(List<Taxa> paths, int taxon1, Taxa.Direction reversed1,
                               int taxon2, Taxa.Direction reversed2,
                               int taxon3, Taxa.Direction reversed3) throws QNetException {

        Taxa tL1 = null, tL2 = null, tL3 = null;

        for (Taxa tL : paths) {

            if (tL.containsId(taxon1)) {
                tL1 = tL;
            }

            if (tL.containsId(taxon2)) {
                tL2 = tL;
            }

            if (tL.containsId(taxon3)) {
                tL3 = tL;
            }
        }

        if (tL1 == null || tL2 == null || tL3 == null) {
            throw new QNetException("Couldn't find specified taxon ids in any path.  Taxon 1: " + taxon1 + "; Taxon 2: " + taxon2 + "; Taxon 3: " + taxon3);
        }

        paths.remove(tL1);
        paths.remove(tL2);
        paths.remove(tL3);

        Taxa tL123 = Taxa.join(Taxa.join(tL1, reversed1, tL2, reversed2), Taxa.Direction.FORWARD, tL3, reversed3);

        paths.add(tL123);

        return paths;
    }

    /**
     * Find a, b, a < b, in X so s (a, b) / n (a, b) maximal
     * @param X
     * @return
     */
    protected Pair<Integer, Integer> findMaxAB(List<Integer> X) {

        int aMax = -1;
        int bMax = -1;
        double qMax = Double.NEGATIVE_INFINITY;
        NSHolder nsHolder = this.holders.getNsHolder();

        for (int xA = 0; xA < X.size() - 1; xA++) {

            for (int xB = xA + 1; xB < X.size(); xB++) {

                int a = X.get(xA);
                int b = X.get(xB);

                double q = nsHolder.calcWeightedCount(a, b);

                if (q > qMax) {

                    qMax = q;
                    aMax = a;
                    bMax = b;
                }
            }
        }

        return new ImmutablePair<>(aMax, bMax);
    }


}
