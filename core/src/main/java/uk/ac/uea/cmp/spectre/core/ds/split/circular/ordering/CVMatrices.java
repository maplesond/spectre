/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by dan on 11/09/14.
 */
public class CVMatrices {

    private Map<Integer, Identifier> clusterIdMap;
    private Map<Integer, Identifier> vertexIdMap;

    private Map<Identifier, Identifier> vertexTranslation;          // User 2 canonical
    private Map<Identifier, Identifier> reverseVertexTranslation;   // Canonical 2 user
    private Map<Identifier, Identifier> clusterTranslation;

    private DistanceMatrix c2c;
    private DistanceMatrix v2v;

    private Map<Pair<Identifier, Identifier>, Double> c2v;
    private Map<Identifier, IdentifierList> c2vs;
    private Map<Identifier, Identifier> v2c;

    public CVMatrices() {

        this.clusterIdMap = new HashMap<>();
        this.vertexIdMap = new HashMap<>();

        this.vertexTranslation = new HashMap<>();
        this.reverseVertexTranslation = new HashMap<>();
        this.clusterTranslation = new HashMap<>();

        this.v2v = new FlexibleDistanceMatrix();
        this.c2c = new FlexibleDistanceMatrix();
        this.c2v = new HashMap<>();
        this.c2vs = new HashMap<>();
        this.v2c = new HashMap<>();
    }

    /**
     * Assumes id of vertex can be translated to cluster id
     * @param userMatrix The user distance matrix
     */
    public CVMatrices(DistanceMatrix userMatrix) {

        this();

        // Do vertexTranslation from user taxa and create lookup maps
        this.setupMaps(userMatrix);

        // Setup the cluster to cluster matrix (derived form user matrix)
        this.setupC2C(userMatrix);

        // Setup the vertex to vertex matrix (also derived from user matrix)
        this.setupV2V(userMatrix);

        // Setup the linkage between clusters and vertices
        this.setupC2V();
    }

    /**
     * Translate the vertices to normal form (makes it easier to debug!)
     * Also create vertexTranslation and lookup maps
     * @param userMatrix The user distance matrix
     */
    private final void setupMaps(DistanceMatrix userMatrix) {

        int i = 1;
        for(Identifier user : userMatrix.getTaxa()) {
            Identifier vertexCanonical = new Identifier("V" + i, i);
            Identifier clusterCanonical = new Identifier("C" + i, i);
            this.vertexTranslation.put(user, vertexCanonical);
            this.reverseVertexTranslation.put(vertexCanonical, user);
            this.clusterTranslation.put(user, clusterCanonical);
            this.clusterIdMap.put(clusterCanonical.getId(), clusterCanonical);
            this.vertexIdMap.put(vertexCanonical.getId(), vertexCanonical);
            i++;
        }
    }

    /**
     * Create the initial vertex -> vertex distance matrix derived from the user matrix but using canonical taxa
     * @param userMatrix The user distance matrix
     */
    private final void setupV2V(DistanceMatrix userMatrix) {
        for(Identifier x : userMatrix.getTaxa()) {
            Identifier xCanonical = this.vertexTranslation.get(x);
            for(Identifier y : userMatrix.getTaxa()) {
                Identifier yCanonical = this.vertexTranslation.get(y);
                this.v2v.setDistance(xCanonical, yCanonical, userMatrix.getDistance(x, y));
            }
        }
    }

    /**
     * Create the initial cluster -> cluster distance matrix derived from the user matrix but using canonical taxa
     * @param userMatrix The user distance matrix
     */
    private final void setupC2C(DistanceMatrix userMatrix) {
        for(Identifier x : userMatrix.getTaxa()) {
            Identifier xCanonical = clusterTranslation.get(x);
            for(Identifier y : userMatrix.getTaxa()) {
                Identifier yCanonical = clusterTranslation.get(y);
                this.c2c.setDistance(xCanonical , yCanonical, userMatrix.getDistance(x, y));
            }
        }
    }


    /**
     * Creates the linkage between clusters and vertices.  Manages the distances between them.
     */
    private final void setupC2V() {

        // List of vertices for each cluster
        this.c2vs = new HashMap<>();
        for(Identifier c : this.c2c.getTaxa()) {
            IdentifierList vs = new IdentifierList();
            vs.add(this.vertexIdMap.get(c.getId()));
            this.c2vs.put(c, vs);
        }

        // Map of each vertex to its parent cluster
        this.v2c = new HashMap<>();
        for(Identifier v : this.v2v.getTaxa()) {
            this.v2c.put(v, this.clusterIdMap.get(v.getId()));
        }

        // C2V distances will initially be the same as C2C and V2V so just leverage
        // the calculations already done there
        for(Identifier c : c2c.getTaxa()) {
            for(Identifier v : v2v.getTaxa()) {
                this.setDistance(c, v, v2v.getDistance(c.getId(), v.getId()));
            }
        }
    }

    public IdentifierList getClusters() {
        return c2c.getTaxa();
    }

    public IdentifierList getVertices() {
        return v2v.getTaxa();
    }

    public Map<Identifier, IdentifierList> getC2Vs() {
        return this.c2vs;
    }

    public int getNbClusters() {
        return this.c2c.size();
    }

    public int getNbActiveComponents() { return c2vs.size(); }

    public int getNbVertices() {
        return this.v2v.size();
    }

    public Identifier createNextCluster() {

        int maxId = 0;

        for (Identifier t : this.getClusters()) {
            if (maxId < t.getId()) {
                maxId = t.getId();
            }
        }

        maxId++;

        return new Identifier("C" + Integer.toString(maxId), maxId);
    }

    /**
     * Creates a new vertex with appropriate ID and adds it to the v2v matrix
     * @return New vertex
     */
    public Identifier createNextVertex() {

        int maxId = 0;

        for (Identifier t : this.getVertices()) {
            if (maxId < t.getId()) {
                maxId = t.getId();
            }
        }

        maxId++;

        Identifier v = new Identifier("V" + Integer.toString(maxId), maxId);
        this.v2v.getTaxa().add(v);

        return v;
    }

    public void linkV2C(Identifier v, Identifier c) {
        this.v2c.put(v, c);
    }

    public double setDistance(Identifier cluster, Identifier vertex, final double value) {

        if (cluster == null || vertex == null)
            throw new IllegalArgumentException("Need two valid taxa to set a distance");

        Pair<Identifier, Identifier> pair = new ImmutablePair<>(cluster, vertex);
        Double oldVal = this.c2v.get(pair);
        this.c2v.put(pair, value);

        return oldVal == null ? 0.0 : oldVal;
    }

    public double getDistance(final Identifier cluster, final Identifier vertex) {

        if (cluster == null || vertex == null)
            throw new IllegalArgumentException("Need two valid taxa to get a distance");

        Double val = this.c2v.get(new ImmutablePair<>(cluster, vertex));

        return val == null ? 0.0 : val;
    }

    public IdentifierList getVertices(Identifier cluster) {
        return this.c2vs.get(cluster);
    }


    public LinkedList<Integer> getOrder() {
        LinkedList<Integer> order = new LinkedList<>();

        for (Map.Entry<Identifier, IdentifierList> entry : this.c2vs.entrySet()) {

            for (Identifier i : entry.getValue()) {
                order.add(i.getId());
            }
        }
        return order;
    }

    public Set<Map.Entry<Identifier, IdentifierList>> getMapEntries() {

        return this.c2vs.entrySet();
    }

    public double sumVertex2Clusters(Identifier v) {

        double sum = 0.0;

        for (Identifier c : this.c2c.getTaxa()) {
            sum += this.getDistance(c, v);
        }

        return sum;
    }

    public IdentifierList reverseTranslate(IdentifierList circularOrdering) {

        IdentifierList translatedTaxa = new IdentifierList();

        for (Identifier i : circularOrdering) {
            translatedTaxa.add(this.reverseVertexTranslation.get(i));
        }

        return translatedTaxa;
    }

    public DistanceMatrix getC2C() {
        return c2c;
    }

    public DistanceMatrix getV2V() {
        return v2v;
    }

    public Map<Pair<Identifier, Identifier>, Double> getC2V() {
        return c2v;
    }

    public void setC2C(DistanceMatrix c2c) {
        this.c2c = c2c;
    }

    public void setV2V(DistanceMatrix v2v) {
        this.v2v = v2v;
    }

    public Identifier getVertex(int vertexIndex) {
        return this.vertexIdMap.get(vertexIndex);
    }

    public void removeVertexTriplet(Identifier vertex1, Identifier vertex2, Identifier vertex3) {

        // Remove the vertces from V2V
        this.v2v.removeTaxon(vertex1);
        this.v2v.removeTaxon(vertex2);
        this.v2v.removeTaxon(vertex3);

        // Also remove vertices from V2C
        this.v2c.remove(vertex1);
        this.v2c.remove(vertex2);
        this.v2c.remove(vertex3);
    }
}
