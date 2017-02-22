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

package uk.ac.uea.cmp.spectre.core.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 11/03/14.
 */
public class StringUtilsTest {

    /*@Test
    public void testConvertNumbers1() {

        for(int i = 1; i < 300; i++) {
            System.out.println("Id: " + i + " - " + StringUtils.ConvertArabicToLetters(i));
        }
    }   */

    @Test
    public void testConvertNumbersLen1() {

        String str = StringUtils.ConvertArabicToLetters(3);

        assertTrue(str.equals("C"));
    }

    @Test
    public void testConvertNumbersLen2() {

        String str = StringUtils.ConvertArabicToLetters(27);

        assertTrue(str.equals("AA"));
    }
}
