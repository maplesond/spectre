/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.viewer;

import uk.ac.uea.cmp.spectre.core.ds.network.NetworkLabel;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;

import java.awt.*;

/**
 * Created by dan on 29/07/14.
 */
public class ViewerNexusWriter extends NexusWriter {

    public NexusWriter append(ViewerConfig config) {
        Dimension dm = config.getDimensions();

        this.appendLine("BEGIN Viewer;");
        this.appendLine("DIMENSIONS width=" + dm.width + " height=" + dm.height + ";");
        this.appendLine(" MATRIX");
        this.appendLine("  ratio=" + config.getRatio());
        this.appendLine("  showtrivial=" + config.showTrivial());
        this.appendLine("  showlabels=" + config.showLabels());
        this.appendLine("  colorlabels=" + config.colorLabels());
        this.appendLine("  leaders=" + config.getLeaderType());
        this.appendLine("  leaderstroke=" + config.getLeaderStroke());
        Color leaderColor = config.getLeaderColor();
        this.appendLine("  leadercolor=" + leaderColor.getRed() + " " +
                leaderColor.getGreen() + " " + leaderColor.getBlue());
        for (Vertex v : config.getLabeledVertices()) {
            NetworkLabel label = v.getLabel();
            if (!label.movable) {
                this.appendLine("  fix " + (v.getNxnum() + 1));
            }
        }
        this.appendLine(" ;\nEND [Viewer];");

        return this;
    }
}
