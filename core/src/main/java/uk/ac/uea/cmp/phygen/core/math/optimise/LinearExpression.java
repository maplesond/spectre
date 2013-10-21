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

package uk.ac.uea.cmp.phygen.core.math.optimise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 20/10/13
 * Time: 21:55
 * To change this template use File | Settings | File Templates.
 */
public class LinearExpression {

    private List<LinearTerm> terms;
    private double constant;

    public LinearExpression() {
        this(new ArrayList<LinearTerm>(), 0.0);
    }

    public LinearExpression(List<LinearTerm> terms, double constant) {
        this.terms = terms;
        this.constant = constant;
    }

    public List<LinearTerm> getTerms() {
        return terms;
    }

    public double getConstant() {
        return constant;
    }

    public LinearExpression addTerm(LinearTerm term) {
        this.terms.add(term);
        return this;
    }

    public LinearExpression addTerm(double coefficient, Variable variable) {
        this.terms.add(new LinearTerm(coefficient, variable));
        return this;
    }

    public LinearExpression addConstant(double constant) {
        this.constant += constant;
        return this;
    }
}
