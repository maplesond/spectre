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

    @Test
    public void sevenTree() throws IOException {

        String source = "(((1:1,2:1):1,((3:1,4:1):1,(5:1):1):1):1,(6:1,7:1):1);";

        new NewickTree(source);

        assertTrue(true);

    }
}
