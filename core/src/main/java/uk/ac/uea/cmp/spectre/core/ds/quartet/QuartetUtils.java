/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ds.quartet;

/**
 * Created by maplesod on 15/05/14.
 */
public class QuartetUtils {

    public static int over4(int n) {

        return n > 4 ?
                n * (n - 1) * (n - 2) * (n - 3) / 24 :
                n == 4 ? 1 : 0;
    }

    public static int over3(int n) {

        return n > 3 ?
                n * (n - 1) * (n - 2) / 6 :
                n == 3 ? 1 : 0;
    }

    public static int over2(int n) {

        return n > 2 ?
                n * (n - 1) / 2 :
                n == 2 ? 1 : 0;
    }

    public static int over1(int n) {

        return n > 0 ? n : 0;
    }

    public static int sumOvers(int a, int b, int c, int d) {
        return over1(a) + over2(b) + over3(c) + over4(d);
    }

    /**
     * Gets the quartet index represented by the given quartet taxa indices using the over methods
     * @param a Taxon index a
     * @param b Taxon index b
     * @param c Taxon index c
     * @param d Taxon index d
     * @return Quartet Index
     */
    public static int getIndex(int a, int b, int c, int d) {
        return over4(a - 1) + over3(b - 1) + over2(c - 1) + over1(d - 1);
    }

    /**
     * This method checks whether or not for given taxa are distinct. The assumption is that a &le; b &le; c &le; d holds.
     *
     * @param a Taxon index a
     * @param b Taxon index b
     * @param c Taxon index c
     * @param d Taxon index d
     * @return True, if all taxa ids are distinct, false otherwise.
     */
    public static boolean areDistinct(int a, int b, int c, int d) {
        return !(a == b || b == c || c == d || a == c || b == d || a == d);
    }
}
