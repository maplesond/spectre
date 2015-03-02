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

package uk.ac.uea.cmp.spectre.flatnj.tools;

import uk.ac.uea.cmp.spectre.core.ds.network.*;

import java.awt.*;
import java.util.Scanner;

/**
 * @author balvociute
 */
public class NexusReaderNetwork extends NexusReader {
    VertexList vertices;
    EdgeList edges;

    boolean readingVertices = true;
    boolean readingLabels = false;
    boolean readingEdges = false;

    String fontFamily;
    String fontType;
    int fontSize;

    public NexusReaderNetwork() {
        block = "network";
        data = "vertices";
    }

    @Override
    protected void initializeDataStructures(Dimensions dimensions) {
        vertices = new VertexList();
        edges = new EdgeList();
    }

    @Override
    protected void parseLine(Format format) {
        scannerLC = new Scanner(lineLC);
        if (scannerLC.findInLine("vertices") != null) {
            readingVertices = true;
            readingLabels = false;
            readingEdges = false;
        } else if (scannerLC.findInLine("vlabels") != null) {
            readingVertices = false;
            readingLabels = true;
            readingEdges = false;
        } else if (scannerLC.findInLine("edges") != null) {
            readingVertices = false;
            readingLabels = false;
            readingEdges = true;
        } else {
            scanner = new Scanner(line);
            if (readingVertices) {
                Vertex v = null;
                if (scanner.findInLine("^\\s*(\\d+)\\s+(\\S+)\\s+(\\S+)") != null) {
                    int i = Integer.parseInt(scanner.match().group(1)) - 1;
                    try {
                        double x = Double.parseDouble(scanner.match().group(2));
                        double y = Double.parseDouble(scanner.match().group(3));

                        v = new Vertex(x, y);
                        v.setNxnum(i);
                        vertices.add(i,v);
                    } catch (NumberFormatException nfe) {
                        exitError("Coordinates must be real numbers.");
                    }
                    if (scanner.findInLine("w\\s*=\\s*(\\d+)") != null) {
                        v.setWidth(Integer.parseInt(scanner.match().group(1)));
                    }
                    if (scanner.findInLine("h\\s*=\\s*(\\d+)") != null) {
                        v.setHeight(Integer.parseInt(scanner.match().group(1)));
                    }
                    if (scanner.findInLine("s\\s*=\\s*(\\S+)") != null) {
                        v.setShape(scanner.match().group(1));
                    }
                    if (scanner.findInLine("fg\\s*=\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)") != null) {
                        Color c = parseColors(scanner);
                        v.setLineColor(c);
                    }
                    if (scanner.findInLine("bg\\s*=\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)") != null) {
                        Color c = parseColors(scanner);
                        v.setBackgroundColor(c);
                    }
                }
            } else if (readingLabels) {
                if (scanner.findInLine("^\\s*(\\d+)\\s+'([^']+)'") != null) {
                    NetworkLabel l = new NetworkLabel();
                    int i = Integer.parseInt(scanner.match().group(1)) - 1;
                    l.setName(scanner.match().group(2));
                    if (scanner.findInLine("x\\s*=\\s*(-?\\d+)") != null) {
                        l.setOffsetX(Integer.parseInt(scanner.match().group(1)));
                    }
                    if (scanner.findInLine("\\s+y\\s*=\\s*(-?\\d+)") != null) {
                        l.setOffsetY(Integer.parseInt(scanner.match().group(1)));
                    }
                    if (scanner.findInLine("f\\s*=\\s*'(.+)-(.+)-(\\d+)'") != null) {
                        fontFamily = scanner.match().group(1);
                        fontType = scanner.match().group(2);
                        fontSize = Integer.parseInt(scanner.match().group(3));
                    }
                    if (scanner.findInLine("lc\\s*=\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)") != null) {
                        Color c = parseColors(scanner);
                        l.setFontColor(c);
                    }
                    if (scanner.findInLine("lk\\s*=\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)") != null) {
                        Color c = parseColors(scanner);
                        l.setBackgoundColor(c);
                    }

                    l.setFontFamily(fontFamily);
                    l.setFontStyle(fontType);
                    l.setFontSize(fontSize);
                    vertices.get(i).setLabel(l);
                }
            } else if (readingEdges) {
                if (scanner.findInLine("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+s\\s*=\\s*(\\d+)") != null) {
                    int id = Integer.parseInt(scanner.match().group(1)) - 1;
                    int iTop = Integer.parseInt(scanner.match().group(2)) - 1;
                    int iBot = Integer.parseInt(scanner.match().group(3)) - 1;
                    int split = Integer.parseInt(scanner.match().group(4)) - 1;

                    Vertex viTop = vertices.get(iTop);
                    Vertex viBot = vertices.get(iBot);
                    Edge e = new Edge(viTop, viBot, split, 0);
                    e.setNxnum(id);

                    viBot.getEdgeList().add(e);
                    viTop.getEdgeList().add(e);

                    e.setIdxsplit(split);

                    if (scanner.findInLine("l\\s*=\\s*(\\d+)") != null) {
                        e.setWidth(Integer.parseInt(scanner.match().group(1)));
                    }
                    if (scanner.findInLine("fg\\s*=\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)") != null) {
                        Color c = parseColors(scanner);
                        e.setColor(c);
                    }
                    edges.set(id, e);
                }
            }
        }
    }

    private Color parseColors(Scanner scanner) {
        int[] c = new int[scanner.match().groupCount()];
        for (int i = 0; i < c.length; i++) {
            c[i] = Integer.parseInt(scanner.match().group(i + 1));
        }
        return new Color(c[0], c[1], c[2]);
    }

    @Override
    protected Object createObject(Dimensions dimensions, Cycle cycle, Draw draw) {
        removeZeroEdges();
        Network network = new FlatNetwork(vertices, edges);
        return network;
    }

    private void removeZeroEdges() {
        for (int i = 0; i < vertices.size(); i++) {
            Vertex w = vertices.get(i);
            if (w.getEdgeList().size() == 1) {
                Edge e = w.getFirstEdge();

                Vertex v = (e.getBottom() == w) ? e.getTop() : e.getBottom();

                if (w.getX() == v.getX() && w.getY() == v.getY()) {
                    if (v.getLabel() == null) {
                        v.setLabel(w.getLabel());
                        v.setHeight(w.getHeight());
                        v.setWidth(w.getWidth());
                        v.setShape(w.getShape());
                        v.setLineColor(w.getLineColor());
                        v.setBackgroundColor(w.getBackgroundColor());
                    } else if (v.getLabel() != null && w.getLabel() != null) {
                        String name = v.getLabel().getName();
                        name = name.concat("," + w.getLabel().getName());
                        v.getLabel().setName(name);
                    }
                    w.setLabel(null);
                    v.getEdgeList().remove(e);
                    w.getEdgeList().remove(e);
                }
            }
        }
    }


}
