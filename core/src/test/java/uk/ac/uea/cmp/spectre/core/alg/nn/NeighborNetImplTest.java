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

package uk.ac.uea.cmp.spectre.core.alg.nn;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.util.Arrays;
import java.util.Stack;

import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 02/03/14.
 */
public class NeighborNetImplTest {

    private String[] taxa;
    private double[][] distances1;
    private double[][] distances2;
    private double[][] distances3;

    @Before
    public void setup() {

        this.taxa = new String[]{"A", "B", "C", "D", "E"};

        this.distances1 = new double[][]{
                {0, 3, 6, 6, 6},
                {3, 0, 6, 6, 6},
                {6, 6, 0, 3, 9},
                {6, 6, 3, 0, 9},
                {6, 6, 9, 9, 0}
        };



        this.distances3 = new double[][]{
                {0, 3, 2, 5, 7},
                {3, 0, 6, 6, 6},
                {2, 6, 0, 3, 8},
                {5, 6, 3, 0, 9},
                {7, 6, 8, 9, 0}
        };
    }


    @Test
    public void testReduction() {

        DistanceMatrix v2v = new FlexibleDistanceMatrix(new IdentifierList(taxa), distances1);

        final double aThird = 1.0 / 3.0;

        assertTrue(v2v.size() == 5);

        NeighborNetImpl nn = new NeighborNetImpl();

        nn.v2v = v2v;
        nn.params = new NeighborNetParams(aThird, aThird, aThird);
        nn.stackedVertexTriplets = new Stack<>();
        nn.c2vsMap = new Component2VertexSetMap(v2v.getTaxa());

        nn.vertexTripletReduction(
                new NeighborNetImpl.VertexTriplet(
                        v2v.getTaxa().getById(1), v2v.getTaxa().getById(2), v2v.getTaxa().getById(3)));

        assertTrue(v2v.size() == 4);

        double[][] matrix = v2v.getMatrix();

        assertTrue(matrix.length == 4);
        assertTrue(Arrays.equals(matrix[0], new double[]{0.0, 9.0, 6.0, 4.0}));
        assertTrue(Arrays.equals(matrix[1], new double[]{9.0, 0.0, 6.0, 8.0}));
        assertTrue(Arrays.equals(matrix[2], new double[]{6.0, 6.0, 0.0, 5.0}));
        assertTrue(Arrays.equals(matrix[3], new double[]{4.0, 8.0, 5.0, 0.0}));
    }

    @Test
    public void testSelectionStep1() {

        DistanceMatrix c2c = new FlexibleDistanceMatrix(new IdentifierList(taxa), distances1);

        NeighborNetImpl nn = new NeighborNetImpl();
        nn.c2c = c2c;

        Pair<Identifier, Identifier> selectedComponents = nn.selectionStep1();

        assertTrue(selectedComponents.getLeft().getName().equals("C"));
        assertTrue(selectedComponents.getRight().getName().equals("D"));
    }

    @Test
    public void testSelectionStep2() {

        DistanceMatrix c2c = new FlexibleDistanceMatrix(new IdentifierList(taxa), distances1);

        NeighborNetImpl nn = new NeighborNetImpl();
        nn.c2c = c2c;

        Pair<Identifier, Identifier> selectedComponents = nn.selectionStep1();

        assertTrue(selectedComponents.getLeft().getName().equals("C"));
        assertTrue(selectedComponents.getRight().getName().equals("D"));
    }

    @Test
    public void testExecuteDist1() {

        SplitSystem ss = new NeighborNetImpl().execute(new FlexibleDistanceMatrix(distances1), new NeighborNetParams(0.3, 0.3));

        String orderedTaxa = ss.getOrderedTaxa().toString();

        assertTrue(orderedTaxa.equalsIgnoreCase("[A,C,D,E,B]"));
        assertTrue(true);
    }


}
