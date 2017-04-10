/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * @author Sarah Bastkowski
 */
public class SummedDistanceList extends ArrayList<Double> {

    public SummedDistanceList() {
        super();
    }


    public SummedDistanceList(double[] sdl) {
        for (double sd : sdl) {
            this.add(new Double(sd));
        }
    }

    public SummedDistanceList(SummedDistanceList copy) {
        this();

        //Maybe this is unnecessary and could simply be done with a call to copy.addAll()
        //but just to be sure...
        for (Double sd : copy) {
            this.add(Double.valueOf(sd));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(StringUtils.join(this, ","));
        sb.append("]");
        return sb.toString();
    }
}
