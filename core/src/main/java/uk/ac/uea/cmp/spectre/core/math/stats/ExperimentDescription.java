/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.math.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 18/04/14.
 */
public class ExperimentDescription {

    private String type;

    public ExperimentDescription(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String createHeaderString() {
        return "type";
    }

    public String toTabSeparatedString() {
        return "" + type;
    }

    public List<String> getReportLines() {
        List<String> report = new ArrayList<>();
        report.add("Experiment Type:\t" + this.type);
        return report;
    }
}
