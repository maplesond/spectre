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

    @Test
    public void bigWithUnderscore() throws IOException {

        NewickTree tree = new NewickTree("((((taxon_25:1.4740707,taxon_6:1.4740707):1.8012785,taxon_4:3.2753491):2.0288513,"
                + "((((taxon_31:0.80242527,taxon_16:0.80242527):1.5820602,taxon_19:2.3844855):2.1938484,(taxon_1:2.8675997,"
                + "taxon_17:2.8675997):1.7107344):0.61534387,((((taxon_22:2.6362004,taxon_5:2.6362004):0.38200533,"
                + "(((taxon_30:1.2894044,taxon_32:1.2894044):0.75605637,(taxon_3:0.753872,taxon_18:0.753872):1.2915888):0.8016502,"
                + "taxon_11:2.847111):0.17109478):1.0738244,(taxon_12:4.089012,(taxon_15:3.2422023,taxon_29:3.2422023):0.84680986):0.0030181012):0.31761545,"
                + "taxon_8:4.4096456):0.78403217):0.1105225):4.6958,(taxon_7:8.936606,(((((taxon_27:0.030184621,taxon_2:0.030184621):0.4502795,"
                + "taxon_23:0.48046413):0.52728015,taxon_13:1.0077443):3.7435637,((taxon_21:0.32780123,taxon_14:0.32780123):3.972376,"
                + "(taxon_10:1.3552276,((taxon_28:0.29636884,taxon_26:0.29636884):0.48958504,taxon_9:0.7859539):0.56927377):2.9449496):0.45113078):4.081427,"
                + "(taxon_24:2.0934403,taxon_20:2.0934403):6.739295):0.10387135):1.0633935);");

        String taxa = tree.getTaxa().toString();

        assertTrue(tree.isBinary());
    }

    @Test
    public void testParseAndWrite() throws IOException {
        NewickTree nt = new NewickTree("(A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;");
        String str = nt.toString();

        assertTrue(true);
    }

}
