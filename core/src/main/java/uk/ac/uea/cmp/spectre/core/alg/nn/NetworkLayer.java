package uk.ac.uea.cmp.spectre.core.alg.nn;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

import java.util.LinkedList;

/**
 * Created by dan on 11/03/14.
 */
public class NetworkLayer extends LinkedList<NetworkNode> {

    /**
     * Computes the Rx (a matrix operation)
     *
     * @param z        a node
     * @param Cx       a node
     * @param Cy       a node
     * @param D        the distances
     * @param netNodes the net nodes
     * @return the Rx value
     */
    protected double computeRx(NetworkNode z, NetworkNode Cx, NetworkNode Cy, DistanceMatrix D,
                               NetworkNode netNodes) {
        double Rx = 0.0;

        for (NetworkNode p : this) {
            if (p == Cx || p == Cx.adjacent || p == Cy || p == Cy.adjacent || p.adjacent == null) {
                Rx += D.getDistance(z.id, p.id);
            }
            // p.adjacent != null so we take the average of the distances
            else {
                Rx += D.getDistance(z.id, p.id) / 2.0;
            }
        }

        return Rx;
    }
}
