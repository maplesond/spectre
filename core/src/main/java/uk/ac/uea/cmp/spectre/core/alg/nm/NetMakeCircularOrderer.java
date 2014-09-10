/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.alg.nm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.alg.CircularOrderingCreator;
import uk.ac.uea.cmp.spectre.core.alg.nm.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.spectre.core.alg.nm.weighting.TreeWeighting;
import uk.ac.uea.cmp.spectre.core.alg.nm.weighting.Weighting;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.*;

import java.util.*;

/**
 * Created by dan on 07/09/14.
 */
public class NetMakeCircularOrderer implements CircularOrderingCreator {

    protected enum RunMode {

        UNKNOWN,
        NORMAL,
        HYBRID,
        HYBRID_GREEDYME;

        public boolean isHybrid() {
            return (this == HYBRID || this == HYBRID_GREEDYME);
        }
    }

    protected static boolean isGreedyMEWeighting(Weighting w) {
        if (w == null)
            return false;

        return (w instanceof GreedyMEWeighting);
    }

    public static RunMode getRunMode(Weighting weighting1, Weighting weighting2) {

        if (weighting1 == null) {
            return RunMode.UNKNOWN;
        } else if (weighting2 == null && !isGreedyMEWeighting(weighting1)) {
            return RunMode.NORMAL;
        } else if (isGreedyMEWeighting(weighting1) && !isGreedyMEWeighting(weighting2)) {
            return RunMode.HYBRID_GREEDYME;
        } else if (!isGreedyMEWeighting(weighting1) && weighting2 != null) {
            return RunMode.HYBRID;
        }

        return RunMode.UNKNOWN;
    }

    private Weighting weighting1;
    private Weighting weighting2;
    private RunMode runMode;

    private SplitSystem treeSplits;

    public NetMakeCircularOrderer(Weighting weighting1, Weighting weighting2) {

        this.weighting1 = weighting1;
        this.weighting2 = weighting2;

        this.runMode = getRunMode(this.weighting1, this.weighting2);

        this.treeSplits = null;
    }


    @Override
    public IdentifierList createCircularOrdering(final DistanceMatrix distanceMatrix) {

        // Translate distance matrix to ensure normal ids



        final IdentifierList taxa = distanceMatrix.getTaxa();

        this.weighting1.setupWeightingParameters(distanceMatrix.size());
        if (weighting2 != null) {
            this.weighting2.setupWeightingParameters(distanceMatrix.size());
        }


        // Creates a simple split system with trivial splits from the given distance matrix
        this.treeSplits = new SpectreSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));

        CVMatrices cvMatrices = new CVMatrices(distanceMatrix);

        // If required create a new GreedyME instance.
        GreedyMEWeighting gme = runMode == RunMode.HYBRID_GREEDYME ?
                new GreedyMEWeighting(distanceMatrix) :
                null;

        // Loop until components has at least two entries left.
        while (cvMatrices.components.size() > 2) {

            // The pair should be splits with the shortest tree length between them
            Pair<Identifier, Identifier> selectedComponents = runMode == RunMode.HYBRID_GREEDYME ?
                    gme.makeMECherry(treeSplits, cvMatrices) :
                    selectionStep1(cvMatrices.c2c);

            Pair<Identifier, Identifier> selectedVertices = selectionStep2(cvMatrices, selectedComponents);

            // Merge of components
            Identifier mergedComponent = cvMatrices.merge(selectedComponents, selectedVertices);

            // Add new component/split to Split list
            treeSplits.add(cvMatrices.getVertices(mergedComponent).copy());

            // Update component to component distanceMatrix (assuming not in GreedyME mode)
            if (runMode != RunMode.HYBRID_GREEDYME)
                cvMatrices.updateC2C(
                        weighting1);

            // Update component to vertex distanceMatrix (also updates weighting1 params)
            cvMatrices.updateC2V(
                    runMode.isHybrid() ? weighting2 : weighting1,
                    selectedComponents);
        }

        // Remove last row (last row is the whole set and the last but one is not required)
        treeSplits.removeLastSplit();

        // Create ordering
        IdentifierList permutation = this.createCircularOrdering(cvMatrices);

        return permutation;
    }

    @Override
    public boolean createsTreeSplits() {
        return true;
    }

    @Override
    public SplitSystem getTreeSplits() {
        return this.treeSplits;
    }

    protected Pair<Identifier, Identifier> selectionStep1(final DistanceMatrix c2c) {

        final int nbComponents = c2c.size();
        IdentifierList components = c2c.getTaxa();

        double[] sum1 = new double[nbComponents];
        double[] sum2 = new double[nbComponents];

        for (int i = 0; i < nbComponents; i++) {
            sum1[i] = 0;
            sum2[i] = 0;

            Identifier taxonI = components.get(i);

            for (int j = 0; j < nbComponents; j++) {

                if (j != i) {

                    Identifier taxonJ = components.get(j);

                    sum1[i] = sum1[i] + c2c.getDistance(taxonI, taxonJ);
                    sum2[i] = sum2[i] + c2c.getDistance(taxonJ, taxonI);
                }
            }
        }

        double min1 = Double.POSITIVE_INFINITY;
        Identifier best1 = null;
        Identifier best2 = null;

        // first Selection step for Components
        for (int i = 0; i < nbComponents; i++) {

            Identifier taxonI = components.get(i);
            for (int j = i + 1; j < nbComponents; j++) {
                Identifier taxonJ = components.get(j);
                double qDist = (nbComponents - 2)
                        * c2c.getDistance(taxonI, taxonJ)
                        - sum1[i] - sum2[j];

                if (qDist < min1) {
                    min1 = qDist;
                    best1 = taxonI;
                    best2 = taxonJ;
                }
            }
        }

        return new ImmutablePair<>(best1, best2);
    }

    protected Pair<Identifier, Identifier> selectionStep2(CVMatrices cv,
                                                    Pair<Identifier, Identifier> selectedComponents) {
        double min2 = Double.POSITIVE_INFINITY;
        Identifier selectedVertex1 = null;
        Identifier selectedVertex2 = null;

        Identifier sc1 = selectedComponents.getKey();
        Identifier sc2 = selectedComponents.getValue();

        IdentifierList sc1Vertices = cv.getVertices(sc1);
        IdentifierList sc2Vertices = cv.getVertices(sc2);

        /*
         * second selection step for vertices with
         * ComponentsVerticesDistances
         */
        for (Identifier sc1V : sc1Vertices) {
            for (Identifier sc2V : sc2Vertices) {

                double sum1 = 0;
                double sum2 = 0;
                double sum3 = 0;
                double sum4 = 0;
                Identifier vertexLast1 = null;
                Identifier vertexLast2 = null;

                for (Map.Entry<Identifier, IdentifierList> c : cv.c2vs.entrySet()) {
                    Identifier k = c.getKey();
                    if ((k != sc1) && (k != sc2)) {
                        sum1 += cv.getC2VDistance(k, sc1V);
                    }

                    if ((k != sc1) && (k != sc2)) {
                        sum2 += cv.getC2VDistance(k, sc2V);
                    }
                }

                for (int k = 0; k < sc2Vertices.size(); k++) {
                    sum3 += cv.v2v.getDistance(
                            sc1V,
                            sc2Vertices.get(k));

                    if (!sc2Vertices.get(k).equals(sc2V)) {
                        vertexLast2 = sc2Vertices.get(k);
                    }
                }

                for (int k = 0; k < sc1Vertices.size(); k++) {
                    Identifier vertex1 = sc1Vertices.get(k);

                    if (!vertex1.equals(sc1V)) {
                        vertexLast1 = vertex1;
                    }
                }

                sum3 += cv.v2v.getDistance(sc2V, vertexLast1);
                sum4 += cv.v2v.getDistance(sc1V, vertexLast2);

                int outerVertices1 = sc1Vertices.size() == 1 ? 1 : 2;
                int outerVertices2 = sc2Vertices.size() == 1 ? 1 : 2;

                final double totalSum = sum1 - sum2 - sum3 - sum4;
                final double topPart = cv.components.size() - 4 + outerVertices1
                        + outerVertices2;

                double qDist = topPart * cv.v2v.getDistance(sc1V, sc2V) - totalSum;

                if (qDist < min2) {
                    min2 = qDist;
                    selectedVertex1 = sc1V;
                    selectedVertex2 = sc2V;
                }

                //j = (j == sc2Vertices.size() - 1) ? sc2Vertices.size() : sc2Vertices.size() - 2;
            }

            //i = (i == sc1Vertices.size() - 1) ? sc1Vertices.size() : sc1Vertices.size() - 2;
        }

        return new ImmutablePair<>(selectedVertex1, selectedVertex2);
    }

    protected int getNextComponent(DistanceMatrix c2c) {

        return c2c.getTaxa().getNextId();
    }





    protected IdentifierList createCircularOrdering(CVMatrices cv) {

        ArrayList<Identifier> help = new ArrayList<>();
        for (Identifier c : cv.components) {
            for (Identifier v : cv.c2vs.get(c)) {
                help.add(v);
            }
        }
        int[] permutation = new int[help.size()];
        for (int i = 0; i < help.size(); i++) {
            permutation[i] = help.get(i);
        }

        return new IdentifierList(permutation);
    }


    public static class CVMatrices {

        private IdentifierList components;
        private IdentifierList vertices;

        private Map<Integer, Identifier> componentIdMap;
        private Map<Integer, Identifier> vertexIdMap;

        private Map<Identifier, Identifier> vertexTranslation;          // User 2 canonical
        private Map<Identifier, Identifier> reverseVertexTranslation;   // Canonical 2 user
        private Map<Identifier, Identifier> componentTranslation;

        private DistanceMatrix c2c;
        private DistanceMatrix v2v;
        private Map<Pair<Identifier, Identifier>, Double> c2v;

        private Map<Identifier, IdentifierList> c2vs;

        public CVMatrices() {

            this.components = new IdentifierList();
            this.vertices = new IdentifierList();

            this.componentIdMap = new HashMap<>();
            this.vertexIdMap = new HashMap<>();

            this.vertexTranslation = new HashMap<>();
            this.reverseVertexTranslation = new HashMap<>();
            this.componentTranslation = new HashMap<>();

            this.v2v = new FlexibleDistanceMatrix();
            this.c2c = new FlexibleDistanceMatrix();
            this.c2v = new HashMap<>();
            this.c2vs = new HashMap<>();
        }

        /**
         * Assumes id of vertex can be translated to component id
         * @param userMatrix
         */
        public CVMatrices(DistanceMatrix userMatrix) {

            this();

            // Do vertexTranslation from user taxa and create lookup maps
            this.setupMaps(userMatrix);

            // Setup the component to component matrix (derived form user matrix)
            this.setupC2C(userMatrix);

            // Setup the vertex to vertex matrix (also derived from user matrix)
            this.setupV2V(userMatrix);

            // Setup the component to vertex matrix
            this.setupC2V(userMatrix);
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
                Identifier componentCanonical = new Identifier("C" + i, i);
                this.vertexTranslation.put(user, vertexCanonical);
                this.reverseVertexTranslation.put(vertexCanonical, user);
                this.componentTranslation.put(user, componentCanonical);
                this.componentIdMap.put(componentCanonical.getId(), componentCanonical);
                this.vertexIdMap.put(vertexCanonical.getId(), vertexCanonical);
                this.components.add(componentCanonical);
                this.vertices.add(vertexCanonical);

                IdentifierList vs = new IdentifierList();
                vs.add(vertexCanonical);
                this.c2vs.put(componentCanonical, vs);
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
         * Create the initial component -> component distance matrix derived from the user matrix but using canonical taxa
         * @param userMatrix The user distance matrix
         */
        private final void setupC2C(DistanceMatrix userMatrix) {
            for(Identifier x : userMatrix.getTaxa()) {
                Identifier xCanonical = componentTranslation.get(x);
                for(Identifier y : userMatrix.getTaxa()) {
                    Identifier yCanonical = componentTranslation.get(y);
                    this.c2c.setDistance(xCanonical , yCanonical, userMatrix.getDistance(x, y));
                }
            }
        }

        private final void setupC2V(DistanceMatrix userMatrix) {
            for(Identifier c : this.components) {
                for(Identifier v : this.vertices) {
                    this.c2v.put(new ImmutablePair<>(c, v),
                            userMatrix.getDistance(
                                    this.reverseVertexTranslation.get(c.getId()),
                                    this.reverseVertexTranslation.get(v.getId())));
                }
            }
        }


        public IdentifierList getComponents() {
            return components;
        }

        public IdentifierList getVertices() {
            return vertices;
        }

        public IdentifierList getVertices(Identifier component) {
            return vertices;
        }



        public Identifier createNextComponent() {

            int maxId = 0;

            for (Identifier t : this.components) {
                if (maxId < t.getId()) {
                    maxId = t.getId();
                }
            }

            maxId++;

            return new Identifier("C" + Integer.toString(maxId), maxId);
        }

        public Identifier createNextVertex() {

            int maxId = 0;

            for (Identifier t : this.vertices) {
                if (maxId < t.getId()) {
                    maxId = t.getId();
                }
            }

            maxId++;

            return new Identifier("V" + Integer.toString(maxId), maxId);
        }

        protected IdentifierList reverseTranslate(IdentifierList circularOrdering) {

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

        public void setC2C(DistanceMatrix c2c) {
            this.c2c = c2c;
        }

        public void setV2V(DistanceMatrix v2v) {
            this.v2v = v2v;
        }

        public double getC2VDistance(Identifier c, Identifier v) {
            return this.c2v.get(new ImmutablePair<>(c, v));
        }

        protected Identifier merge(Identifier sc1, Identifier sc2) {
            IdentifierList v1 = this.c2vs.get(sc1);
            IdentifierList v2 = this.c2vs.get(sc2);

            for(Identifier v : v2) {
                v1.add(v);
            }

            Identifier next = createNextComponent();

            this.c2vs.remove(sc1);
            this.c2vs.remove(sc2);
            this.components.remove(sc1);
            this.components.remove(sc2);

            this.c2vs.put(next, v1);
            this.components.add(next);

            return next;
        }

        public Identifier merge(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> selectedVertices) {

            Identifier sc1 = selectedComponents.getLeft();
            Identifier sc2 = selectedComponents.getRight();
            Identifier sv1 = selectedVertices.getLeft();
            Identifier sv2 = selectedVertices.getRight();

            IdentifierList sc1Vertices = this.getVertices(sc1);
            IdentifierList sc2Vertices = this.getVertices(sc2);

            // Merging of components
            if (sc1Vertices.getFirst() == sv1) {
                sc1Vertices.reverse();
            }

            if (sc2Vertices.getFirst() == sv2) {
                return this.merge(sc1, sc2);
            } else {
                sc2Vertices.reverse();
                return this.merge(sc1, sc2);
            }
        }

        public void updateC2C(Weighting w) {

            final int nbComponents = components.size();
            this.c2c = new FlexibleDistanceMatrix(nbComponents);

            for (int i = 0; i < nbComponents; i++) {
                for (int j = 0; j < nbComponents; j++) {
                    if (i == j) {
                        c2c.setDistance(i + 1, j + 1, 0.0);
                    } else {
                        double aComponentDistance = 0.0;

                        for (Identifier vertex1 : this.c2vs.get(i)) {
                            for (Identifier vertex2 : this.c2vs.get(j)) {
                                double vertexDistance = w.getWeightingParam(vertex1)
                                        * w.getWeightingParam(vertex2)
                                        * this.v2v.getDistance(vertex1, vertex2);

                                aComponentDistance += vertexDistance;
                            }
                        }
                        c2c.setDistance(i + 1, j + 1, aComponentDistance);
                    }
                }
            }
        }

        public void updateC2V(Weighting w, Pair<Identifier, Identifier> selectedComponents) {

            int position = -1;
            final Identifier sc1 = selectedComponents.getLeft();

            IdentifierList sc1Vertices = this.c2vs.get(sc1);
            int componentSplitPosition = sc1Vertices.size();

            for (int i = 0; i < this.c2c.size(); i++) {
                for (int j = 0; j < sc1Vertices.size(); j++) {
                    if (i == sc1Vertices.get(j) - 1) {
                        position = j;

                        if (w instanceof TreeWeighting) {
                            w.updateWeightingParam(i, position,
                                    componentSplitPosition);
                        } else {
                            w.updateWeightingParam(i, position,
                                    sc1Vertices.size());
                        }
                    }
                }
                for (IdentifierList sbJ : this.c2vs.values()) {
                    double aComponentVertexDistance = 0.;
                    int k = 0;

                    while (k < sbJ.size()) {
                        if (sbJ.get(k) - 1 == i) {
                            aComponentVertexDistance = 0.;
                            k = sbJ.size();
                        } else {
                            int vertex1 = i + 1;
                            int vertex2 = sbJ.get(k);
                            double vertexDistance = w.getWeightingParam(vertex2 - 1)
                                    * this.v2v.getDistance(vertex1, vertex2);

                            aComponentVertexDistance += vertexDistance;
                            k++;
                        }
                    }
                    c2v.put(new ImmutablePair<>(i + 1, s), aComponentVertexDistance);
                }
            }
        }
    }
}
