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

package uk.ac.uea.cmp.phygen.gurobi;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 23/10/13
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class OptimiserTestUtils {

    public static boolean approxEquals(double actual, double expected) {
        return approxEquals(actual, expected, 0.001);
    }

    public static boolean approxEquals(double actual, double expected, double tolerance) {
        double val = Math.abs(actual - expected);
        return val < tolerance;
    }
}
