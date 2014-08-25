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

package uk.ac.uea.cmp.spectre.core.ds;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class CircularOrderingTest {

    private CircularOrdering co1;


    @Before
    public void setup() {

        co1 = new CircularOrdering(new String[]{"A", "B", "C", "D", "E"});
    }


    @Test
     public void testEquality1() {

        CircularOrdering test = new CircularOrdering(co1);

        assertTrue(co1.equals(test));
    }

    @Test
    public void testEquality2() {

        CircularOrdering test = new CircularOrdering(new String[]{"B", "C", "D", "E", "A"});

        assertTrue(co1.equals(test));
    }

    @Test
    public void testEquality3() {

        CircularOrdering test = new CircularOrdering(new String[]{"A", "E", "D", "C", "B"});

        assertTrue(co1.equals(test));
    }



}