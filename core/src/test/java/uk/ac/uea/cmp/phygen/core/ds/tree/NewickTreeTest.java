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

package uk.ac.uea.cmp.phygen.core.ds.tree;

import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 07/11/13
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */
public class NewickTreeTest {



       /*    (A,B,(C,D));                           leaf nodes are named
            (A,B,(C,D)E)F;                         all nodes are named
            (:0.1,:0.2,(:0.3,:0.4):0.5);           all but root node have a distance to parent
            (:0.1,:0.2,(:0.3,:0.4):0.5):0.0;       all have a distance to parent
            (A:0.1,B:0.2,(C:0.3,D:0.4):0.5);       distances and leaf names (popular)
    (A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;     distances and all names
            ((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A;    a tree rooted on a leaf node (rare)    */

    @Test
    public void noNamedNodes() throws IOException {

        String source = "(,,(,));";

        new NewickTree(source);

        assertTrue(true);
    }



    @Test
    public void sevenTree() throws IOException {

        String source = "(((1:1,2:1):1,((3:1,4:1):1,(5:1):1):1):1,(6:1,7:1):1);5";

        new NewickTree(source);

        assertTrue(true);

    }
}
