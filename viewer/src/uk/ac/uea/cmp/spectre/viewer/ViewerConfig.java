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

package uk.ac.uea.cmp.spectre.viewer;

import java.awt.*;
import java.util.Set;

/**
 * @author balvociute
 */
public class ViewerConfig {
    private Dimension dimensions;
    private String leaderType = "";
    private String leaderStroke = "";
    private Color leaderColor;
    boolean showTrivial;
    boolean showLabels;
    boolean colorLabels;
    Set<Integer> fixed;
    Double ratio;

    public ViewerConfig() {
    }

    public ViewerConfig(Dimension dimensions,
                        String leaderType,
                        String leaderStroke,
                        Color leaderColor,
                        boolean showTrivial,
                        boolean showLabels,
                        boolean colorLabels,
                        Set<Integer> fixed,
                        Double ratio) {
        this.dimensions = dimensions;
        this.leaderType = leaderType;
        this.leaderStroke = leaderStroke;
        this.leaderColor = leaderColor;
        this.showTrivial = showTrivial;
        this.showLabels = showLabels;
        this.colorLabels = colorLabels;
        this.fixed = fixed;
        this.ratio = ratio;
    }

    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public void setLeaderType(String leaderType) {
        this.leaderType = leaderType;
    }

    public String getLeaderType() {
        return leaderType;
    }

    public void setShowTrivial(boolean showTrivial) {
        this.showTrivial = showTrivial;
    }

    public boolean showTrivial() {
        return showTrivial;
    }


    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    public boolean showLabels() {
        return showLabels;
    }

    public void setColorLabels(boolean colorLabels) {
        this.colorLabels = colorLabels;
    }

    public boolean colorLabels() {
        return colorLabels;
    }

    public void setLeaderColor(Color leaderColor) {
        this.leaderColor = leaderColor;
    }

    public Color getLeaderColor() {
        return leaderColor;
    }

    public String getLeaderStroke() {
        return leaderStroke;
    }

    public void setLeaderStroke(String leaderStroke) {
        this.leaderStroke = leaderStroke;
    }

    public Set<Integer> getFixed() {
        return fixed;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public Double getRatio() {
        return ratio;
    }
}
