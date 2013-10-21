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
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public class QuadraticExpression {

    private List<QuadraticTerm> quadraticTerms;
    private List<LinearTerm> linearTerms;
    private double constant;

    public QuadraticExpression() {
        this(new ArrayList<QuadraticTerm>(), new ArrayList<LinearTerm>(), 0.0);
    }

    public QuadraticExpression(List<QuadraticTerm> quadraticTerms, List<LinearTerm> linearTerms, double constant) {
        this.quadraticTerms = quadraticTerms;
        this.linearTerms = linearTerms;
        this.constant = constant;
    }

    public List<QuadraticTerm> getQuadraticTerms() {
        return quadraticTerms;
    }

    public double getConstant() {
        return constant;
    }

    public QuadraticExpression addTerm(QuadraticTerm term) {
        this.quadraticTerms.add(term);
        return this;
    }

    public QuadraticExpression addTerm(LinearTerm term) {
        this.linearTerms.add(term);
        return this;
    }

    public QuadraticExpression addTerm(double coefficient, Variable variable1, Variable variable2) {
        this.quadraticTerms.add(new QuadraticTerm(coefficient, variable1, variable2));
        return this;
    }

    public QuadraticExpression addTerm(double coefficient, Variable variable) {
        this.linearTerms.add(new LinearTerm(coefficient, variable));
        return this;
    }

    public QuadraticExpression addConstant(double constant) {
        this.constant += constant;
        return this;
    }
}
