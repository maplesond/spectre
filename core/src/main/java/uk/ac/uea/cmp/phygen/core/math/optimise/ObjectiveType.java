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

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 19/10/13
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public enum ObjectiveType {
    LINEAR,
    QUADRATIC;

    public boolean isLinear() {
        return this == ObjectiveType.LINEAR;
    }

    public boolean isQuadratic() {
        return this == ObjectiveType.QUADRATIC;
    }
}
