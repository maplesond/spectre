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
        LESS_THAN,
        LESS_THAN_OR_EQUAL_TO,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL_TO;

        public static Relation EQ = EQUAL;
        public static Relation LT = LESS_THAN;
        public static Relation LTE = LESS_THAN_OR_EQUAL_TO;
        public static Relation GT = GREATER_THAN;
        public static Relation GTE = GREATER_THAN_OR_EQUAL_TO;
    }

    private String name;
    private double value;
    private Relation relation;
    private LinearExpression linearExpression;

    public Constraint(String name, LinearExpression linearExpression, Relation relation, double value) {
        this.name = name;
        this.value = value;
        this.relation = relation;
        this.linearExpression = linearExpression;
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

    public LinearExpression getLinearExpression() {
        return linearExpression;
    }
}
