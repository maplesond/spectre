/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

public class Objective {

    private String name;
    private ObjectiveDirection direction;
    private Expression expression;


    public Objective(String name, ObjectiveDirection direction, Expression expression) {
        this.name = name;
        this.direction = direction;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns what type of objective this is.
     * @return
     */
    public ObjectiveType getType() {
        return this.expression == null ?
                ObjectiveType.LINEAR :
                this.expression.isQuadratic() ?
                        ObjectiveType.QUADRATIC :
                        ObjectiveType.LINEAR;
    }

    /**
     * Returns whether to try and minimise or maximise this objective
     * @return
     */
    public ObjectiveDirection getDirection() {
        return this.direction;
    }


    public Expression getExpression() {
        return expression;
    }

    /**
     * Defines whether this objective is linear or quadratic in nature.  This will effect which optimisers can optimise
     * this objective
     */
    public static enum ObjectiveType {
        LINEAR,
        QUADRATIC;

        public boolean isLinear() {
            return this == ObjectiveType.LINEAR;
        }

        public boolean isQuadratic() {
            return this == ObjectiveType.QUADRATIC;
        }
    }

    /**
     * Whether to try and minimise or maximise this objective
     */
    public enum ObjectiveDirection {
        MINIMISE,
        MAXIMISE
    }
}
