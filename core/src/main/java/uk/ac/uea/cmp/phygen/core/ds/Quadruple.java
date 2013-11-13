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

package uk.ac.uea.cmp.phygen.core.ds;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 12/11/13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public class Quadruple {

    private Taxon q1;
    private Taxon q2;
    private Taxon q3;
    private Taxon q4;

    public Quadruple(Taxon q1, Taxon q2, Taxon q3, Taxon q4) {
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
    }

    public Taxon getQ1() {
        return q1;
    }

    public Taxon getQ2() {
        return q2;
    }

    public Taxon getQ3() {
        return q3;
    }

    public Taxon getQ4() {
        return q4;
    }
}
