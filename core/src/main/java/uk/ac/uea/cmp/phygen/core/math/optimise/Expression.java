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
public class Expression {

    private List<QuadraticTerm> quadraticTerms;
    private List<LinearTerm> linearTerms;
    private double constant;

    public Expression() {
        this(new ArrayList<QuadraticTerm>(), new ArrayList<LinearTerm>(), 0.0);
    }

    public Expression(List<QuadraticTerm> quadraticTerms, List<LinearTerm> linearTerms, double constant) {
        this.quadraticTerms = quadraticTerms;
        this.linearTerms = linearTerms;
        this.constant = constant;
    }

    public boolean isQuadratic() {
        if (this.quadraticTerms == null || this.quadraticTerms.isEmpty())
            return false;

        return true;
    }

    public boolean isLinear() {
        if (this.quadraticTerms == null || this.quadraticTerms.isEmpty())
            return true;

        return false;
    }


    public List<QuadraticTerm> getQuadraticTerms() {
        return quadraticTerms;
    }

    public List<LinearTerm> getLinearTerms() {
        return linearTerms;
    }

    public double getConstant() {
        return constant;
    }

    public Expression addTerm(QuadraticTerm term) {
        this.quadraticTerms.add(term);
        return this;
    }

    public Expression addTerm(LinearTerm term) {
        this.linearTerms.add(term);
        return this;
    }

    public Expression addTerm(double coefficient, Variable variable1, Variable variable2) {
        this.quadraticTerms.add(new QuadraticTerm(coefficient, variable1, variable2));
        return this;
    }

    public Expression addTerm(double coefficient, Variable variable) {
        this.linearTerms.add(new LinearTerm(coefficient, variable));
        return this;
    }

    public Expression addConstant(double constant) {
        this.constant += constant;
        return this;
    }
}
