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


import org.apache.commons.math3.util.Pair;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 21/10/13
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public class Solution {

    private List<Pair<String, Double>> variables;
    private double solution;
    private String timeTaken;

    public Solution(List<Pair<String, Double>> variables, double solution) {
        this.variables = variables;
        this.solution = solution;
        this.timeTaken = "";
    }

    public List<Pair<String, Double>> getVariables() {
        return variables;
    }

    public double[] getVariableValues() {

        double[] variableValues = new double[variables.size()];

        for(int i = 0; i < variables.size(); i++) {
            variableValues[i] = variables.get(i).getValue();
        }

        return variableValues;
    }

    public double getSolution() {
        return solution;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Variable Values: \n");

        for(Pair<String, Double> variables : this.variables) {
            sb.append(variables.getKey() + " " + variables.getValue() + "\n");
        }

        sb.append("Objective solution: " + this.solution);

        if (!timeTaken.isEmpty()) {
            sb.append("\nTime taken to calculate solution: " + timeTaken);
        }

        return sb.toString();
    }
}
