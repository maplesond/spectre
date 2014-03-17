/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.spectre.core.ds.tree;

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.NewickTree;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 07/11/13
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */
public class NewickTreeTest {

    @Before
    public void setup() {
        //BasicConfigurator.configure();
    }

    @Test
    public void testNoNamedNodes() throws IOException {

        NewickTree tree = new NewickTree("(,,(,));");

        assertFalse(tree.hasNonLeafNames());
    }


    @Test
    public void testNamedLeafNodes() throws IOException {

        NewickTree newickTree = new NewickTree("(A,B,(C,D));");

        assertFalse(newickTree.hasNonLeafNames());
    }


    @Test
    public void testAllNamedNodes() throws IOException {
        NewickTree tree = new NewickTree("(A,B,(C,D)E)F;");

        assertTrue(tree.hasNonLeafNames());
    }

    @Test
    public void testAllButRootHaveDistance() throws IOException {
        NewickTree tree = new NewickTree("(:0.1,:0.2,(:0.3,:0.4):0.5);");

        assertTrue(tree.allHaveLengths());
    }

    @Test
    public void testAllHaveDistance() throws IOException {
        NewickTree tree = new NewickTree("(:0.1,:0.2,(:0.3,:0.4):0.5):0.0;");

        assertTrue(tree.allHaveLengths());
    }

    @Test
    public void testDistancesAndLeafNames() throws IOException {
        new NewickTree("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);");

        assertTrue(true);
    }

    @Test
    public void testDistancesAndNames() throws IOException {
        new NewickTree("(A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;");

        assertTrue(true);
    }

    @Test
    public void testTreeRootedOnLeafNode() throws IOException {
        new NewickTree("((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A;");

        assertTrue(true);
    }

    @Test(expected = RuntimeException.class)
    public void testBadTokens() throws IOException {
        new NewickTree("giblets***dff2;");
    }

    @Test(expected = RuntimeException.class)
    public void testBadSyntax() throws IOException {
        new NewickTree(")))(((;");
    }

    @Test
    public void testWeight() throws IOException {
        NewickTree tree = new NewickTree("(:0.1,(:0.3,:0.4):0.5);5.0");

        assertTrue(tree.isBinary());
    }

    @Test
    public void sevenTaxaTreeWithWeight() throws IOException {

        NewickTree tree = new NewickTree("(((A:1.0,B:1.0):1,((C:1,D:1):1,E:1):1):1,(F:1,G:1):1);2.");

        assertTrue(tree.getScalingFactor() == 2.0);
        assertTrue(tree.isBinary());
    }


    @Test
    public void sevenNumberTaxaTree() throws IOException {

        NewickTree tree = new NewickTree("(((\"1\":1,\"2\":1):1,((\"3\":1,\"4\":1):1,\"5\":1):1):1,(\"6\":1,\"7\":1):1);");

        String taxa = tree.getTaxa().toString();

        assertTrue(tree.isBinary());
        assertTrue(taxa.equals("[1,2,3,4,5,6,7]"));
    }


}
