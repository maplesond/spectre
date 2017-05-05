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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import uk.ac.uea.cmp.spectre.core.ds.network.VertexList;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author balvociute
 */
public class ViewerConfig {
    private Dimension dimensions;
    private Leader.LeaderType leaderType;
    private Leader.LeaderStroke leaderStroke;
    private Color leaderColor;
    private boolean showTrivial;
    private boolean showRange;
    private boolean showLeaders;
    private boolean showLabels;
    private boolean colorLabels;
    private Set<Integer> fixed;
    private double ratio;
    private double angle;
    private VertexList labeledVertices;

    public ViewerConfig() {
        this(
                new Dimension(800, 600),
                Leader.LeaderType.NONE,
                Leader.LeaderStroke.SOLID,
                new Color(0,0,0),
                true,
                true,
                true,
                true,
                new HashSet<Integer>(),
                1.0,
                0.0,
                new VertexList());
    }

    public ViewerConfig(Dimension dimensions,
                        Leader.LeaderType leaderType,
                        Leader.LeaderStroke leaderStroke,
                        Color leaderColor,
                        boolean showTrivial,
                        boolean showRange,
                        boolean showLabels,
                        boolean colorLabels,
                        Set<Integer> fixed,
                        double ratio,
                        double angle,
                        VertexList labeledVertices) {
        this.dimensions = dimensions;
        this.leaderType = leaderType;
        this.leaderStroke = leaderStroke;
        this.leaderColor = leaderColor;
        this.showTrivial = showTrivial;
        this.showRange = showRange;
        this.showLabels = showLabels;
        this.colorLabels = colorLabels;
        this.fixed = fixed;
        this.ratio = ratio;
        this.angle = angle;
        this.labeledVertices = labeledVertices;
    }


    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public void setLeaderType(Leader.LeaderType leaderType) {
        this.leaderType = leaderType;
    }

    public Leader.LeaderType getLeaderType() {
        return leaderType;
    }

    public void setShowTrivial(boolean showTrivial) {
        this.showTrivial = showTrivial;
    }

    public boolean showTrivial() {
        return showTrivial;
    }

    public boolean showRange() {
        return showRange;
    }

    public boolean leadersVisible() {
        return this.leaderType != Leader.LeaderType.NONE;
    }

    public void setShowRange(boolean showRange) {
        this.showRange = showRange;
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

    public Leader.LeaderStroke getLeaderStroke() {
        return leaderStroke;
    }

    public void setLeaderStroke(Leader.LeaderStroke leaderStroke) {
        this.leaderStroke = leaderStroke;
    }

    public Set<Integer> getFixed() {
        return fixed;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getRatio() {
        return ratio;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void incAngle(double delta) {
        final double PI2 = 2 * Math.PI;
        this.angle += delta;
        while (this.angle > PI2) {
            this.angle -= PI2;
        }
        while (this.angle < 0.0) {
            this.angle += PI2;
        }
    }

    public VertexList getLabeledVertices() {
        return labeledVertices;
    }

    public void setLabeledVertices(VertexList labeledVertices) {
        this.labeledVertices = labeledVertices;
    }

}
