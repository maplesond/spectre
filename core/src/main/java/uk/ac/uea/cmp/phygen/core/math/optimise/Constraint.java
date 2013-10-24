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

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 20/10/13
 * Time: 21:59
 * To change this template use File | Settings | File Templates.
 */
public class Constraint {

    public static enum Relation {

        EQUAL,
        LESS_THAN_OR_EQUAL_TO,
        GREATER_THAN_OR_EQUAL_TO;

        public static Relation EQ = EQUAL;
        public static Relation LTE = LESS_THAN_OR_EQUAL_TO;
        public static Relation GTE = GREATER_THAN_OR_EQUAL_TO;
    }

    private String name;
    private double value;
    private Relation relation;
    private Expression expression;

    public Constraint(String name, Expression expression, Relation relation, double value) {

        this.name = name;
        this.value = value;
        this.relation = relation;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public Relation getRelation() {
        return relation;
    }

    public Expression getExpression() {
        return expression;
    }

    /**
     * Returns what type of constraint this is.
     * @return
     */
    public ConstraintType getType() {
        return this.expression == null ?
                ConstraintType.LINEAR :
                this.expression.isQuadratic() ?
                        ConstraintType.QUADRATIC :
                        ConstraintType.LINEAR;
    }

    public enum ConstraintType {
        LINEAR,
        QUADRATIC;

        public boolean isLinear() {
            return this == ConstraintType.LINEAR;
        }

        public boolean isQuadratic() {
            return this == ConstraintType.QUADRATIC;
        }
    }
}
