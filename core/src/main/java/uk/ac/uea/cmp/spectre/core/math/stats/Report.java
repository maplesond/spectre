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

package uk.ac.uea.cmp.spectre.core.math.stats;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 18/04/14.
 */
public class Report {

    private List<Experiment> experiments;

    public Report() {
        this.experiments = new ArrayList<Experiment>();
    }

    public void addExperiment(Experiment experiment) {
        this.experiments.add(experiment);
    }

    public void save(File reportFile) throws IOException {

        if (experiments == null || experiments.isEmpty())
            return;

        List<String> lines = new ArrayList<String>();

        lines.add(this.experiments.get(0).createHeaderString());

        for(Experiment e : this.experiments) {
            lines.add(e.toTabSeparatedString());
        }

        FileUtils.writeLines(reportFile, lines);
    }
}
