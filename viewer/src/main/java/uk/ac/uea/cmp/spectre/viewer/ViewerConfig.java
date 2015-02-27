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

import uk.ac.uea.cmp.spectre.core.ds.network.VertexList;

import java.awt.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author balvociute
 */
public class ViewerConfig {
    private Dimension dimensions;
    private String leaderType = "";
    private String leaderStroke = "";
    private Color leaderColor;
    private boolean showTrivial;
    private boolean showLabels;
    private boolean colorLabels;
    private Set<Integer> fixed;
    private Double ratio;
    private VertexList labeledVertices;

    public ViewerConfig() {
        this(
                new Dimension(800, 600),
                "",
                "",
                new Color(0,0,0),
                true,
                true,
                true,
                new HashSet<Integer>(),
                0.0,
                new VertexList());
    }

    public ViewerConfig(Dimension dimensions,
                        String leaderType,
                        String leaderStroke,
                        Color leaderColor,
                        boolean showTrivial,
                        boolean showLabels,
                        boolean colorLabels,
                        Set<Integer> fixed,
                        Double ratio,
                        VertexList labeledVertices) {
        this.dimensions = dimensions;
        this.leaderType = leaderType;
        this.leaderStroke = leaderStroke;
        this.leaderColor = leaderColor;
        this.showTrivial = showTrivial;
        this.showLabels = showLabels;
        this.colorLabels = colorLabels;
        this.fixed = fixed;
        this.ratio = ratio;
        this.labeledVertices = labeledVertices;
    }

    public ViewerConfig(java.util.List<String> block) {

        this();

        for(String line : block) {
            this.parseLine(line.toLowerCase());
        }
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

    public VertexList getLabeledVertices() {
        return labeledVertices;
    }

    public void setLabeledVertices(VertexList labeledVertices) {
        this.labeledVertices = labeledVertices;
    }

    protected final void parseLine(String lineLC) {
        Scanner scannerLC = new Scanner(lineLC);
        String matched = scannerLC.findInLine("showtrivial\\s*=\\s*(\\S+)");
        if (matched != null) {
            this.showTrivial = Boolean.parseBoolean(scannerLC.match().group(1));
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("showlabels\\s*=\\s*(\\S+)");
        if (matched != null) {
            this.showLabels = Boolean.parseBoolean(scannerLC.match().group(1));
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("colorlabels\\s*=\\s*(\\S+)");
        if (matched != null) {
            this.colorLabels = Boolean.parseBoolean(scannerLC.match().group(1));
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("leaders\\s*=\\s*(\\S+)");
        if (matched != null) {
            this.leaderType = scannerLC.match().group(1);
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("leaderstroke\\s*=\\s*(\\S+)");
        if (matched != null) {
            leaderStroke = scannerLC.match().group(1);
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("leadercolor\\s*=\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)");
        if (matched != null) {
            leaderColor = new Color(Integer.parseInt(scannerLC.match().group(1)),
                    Integer.parseInt(scannerLC.match().group(2)),
                    Integer.parseInt(scannerLC.match().group(3)));
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("fix\\s*(\\d+)");
        if (matched != null) {
            fixed.add(Integer.parseInt(scannerLC.match().group(1)) - 1);
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("ratio\\s*=\\s*(\\.+)");
        if (matched != null) {
            try {
                ratio = Double.parseDouble(scannerLC.match().group(1));
            } catch (NumberFormatException nfe) {
            }
        }
    }
}
