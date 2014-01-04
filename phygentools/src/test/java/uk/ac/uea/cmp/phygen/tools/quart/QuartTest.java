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

package uk.ac.uea.cmp.phygen.tools.quart;

import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemCombiner;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 28/10/13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class QuartTest {

    @Test
    public void testFiveTaxaTree() throws IOException {

        NewickTree tree = new NewickTree("(((A:1,B:1):1,C:1),(D:1,E:1):1);");

        QuartetSystemList qsl = new QuartetSystemList(new QuartetSystem(tree));

        QuartetSystemCombiner qsc = new Quart().execute(qsl);

        assertTrue(true);
    }

    @Test
    public void testSevenTaxaTree() throws IOException {

        NewickTree tree = new NewickTree("(((A:1,B:1):1,((C:1,D:1):1,E:1):1),(F:1,G:1):1);");

        QuartetSystemList qsl = new QuartetSystemList(new QuartetSystem(tree));

        QuartetSystemCombiner qsc = new Quart().execute(qsl);

        assertTrue(true);
    }
}
