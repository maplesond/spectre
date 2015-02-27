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

package uk.ac.uea.cmp.spectre.core.ds;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 12/11/13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public class Quadruple {

    private Identifier q1;
    private Identifier q2;
    private Identifier q3;
    private Identifier q4;

    public Quadruple(Identifier q1, Identifier q2, Identifier q3, Identifier q4) {
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
    }

    public Identifier getQ1() {
        return q1;
    }

    public Identifier getQ2() {
        return q2;
    }

    public Identifier getQ3() {
        return q3;
    }

    public Identifier getQ4() {
        return q4;
    }
}
