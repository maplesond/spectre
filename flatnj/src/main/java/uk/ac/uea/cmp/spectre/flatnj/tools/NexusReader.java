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

package uk.ac.uea.cmp.spectre.flatnj.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * @author balvociute
 */
public abstract class NexusReader<E> {

    protected BufferedReader br;
    protected String line;
    protected String lineLC;
    protected Scanner scanner;
    protected Scanner scannerLC;
    protected String matched;
    protected String block;
    protected String data = "matrix";
    int index;
    private String originalLine;

    public E readBlock(String inFile) {
        E e = null;
        openFile(inFile);
        if (findBlock()) {
            Dimensions dimensions = null;
            Format format = new Format();
            Cycle cycle = null;
            Draw draw = null;
            while (!lineLC.startsWith(data)) {
                if (lineLC.contains("dimensions")) {
                    dimensions = parseDimensions();
                } else if (lineLC.contains("format")) {
                    format = parseFormat();
                } else if (lineLC.contains("cycle")) {
                    cycle = parseCycle();
                } else if (lineLC.contains("draw")) {
                    draw = parseDraw();
                }
                readLine();
            }
            e = parseBlock(dimensions, format, cycle, draw);
        }
        closeFile();
        return e;
    }

    protected E parseBlock(Dimensions dimensions, Format format, Cycle cycle, Draw draw) {
        E e = null;
        index = 0;

        while (lineLC != null && !lineLC.contains(data)) {
            readLine();
        }
        if (lineLC != null && lineLC.contains(data)) {
            initializeDataStructures(dimensions);

            line = line.substring(lineLC.indexOf(data) + data.length());
            boolean exit = false;
            while (!exit) {
                if (line.startsWith("[")) {
                    line = line.replaceFirst("\\[[^\\[\\]]+\\]", "");
                }
                scanner = new Scanner(line);
                matched = scanner.findInLine(",\\s*$");
                if (matched != null) {
                    line = line.replace(matched, "");
                }

                if (lineLC.contains("end;")) {
                    line = line.substring(0, lineLC.indexOf("end;"));
                    exit = true;
                }
                line = line.replace(";", "");
                line = line.trim();
                if (line.length() > 0) {
                    lineLC = line.toLowerCase();
                    scanner = new Scanner(line);
                    scannerLC = new Scanner(lineLC);
                    parseLine(format);
                } else if (format != null && format.interleaved) {
                    index = 0;
                }
                readLine();
            }
            e = createObject(dimensions, cycle, draw);
        }
        return e;
    }

    private Dimensions parseDimensions() {
        Dimensions dimensions = new Dimensions();
        boolean exit = false;
        while (!exit) {
            scannerLC = new Scanner(lineLC);
            matched = scannerLC.findInLine("ntax\\s*=\\s*(\\d+)");
            if (matched != null) {
                dimensions.nTax = Integer.parseInt((scannerLC.match()).group(1));
            }
            scannerLC = new Scanner(lineLC);
            matched = scannerLC.findInLine("nchar\\s*=\\s*(\\s+)");
            if (matched != null) {
                dimensions.nChar = Integer.parseInt((scannerLC.match()).group(1));
            }
            scannerLC = new Scanner(lineLC);
            matched = scannerLC.findInLine("nsplits\\s*=\\s*(\\d+)");
            if (matched != null) {
                dimensions.nSplits = Integer.parseInt((scannerLC.match()).group(1));
            }
            scannerLC = new Scanner(lineLC);
            matched = scannerLC.findInLine("nvertices\\s*=\\s*(\\d+)");
            if (matched != null) {
                dimensions.nVertices = Integer.parseInt((scannerLC.match()).group(1));
            }
            scannerLC = new Scanner(lineLC);
            matched = scannerLC.findInLine("nedges\\s*=\\s*(\\d+)");
            if (matched != null) {
                dimensions.nEdges = Integer.parseInt((scannerLC.match()).group(1));
            }
            scannerLC = new Scanner(lineLC);
            matched = scannerLC.findInLine("height\\s*=\\s*(\\d+)");
            if (matched != null) {
                dimensions.height = Integer.parseInt((scannerLC.match()).group(1));
            }
            scannerLC = new Scanner(lineLC);
            matched = scannerLC.findInLine("width\\s*=\\s*(\\d+)");
            if (matched != null) {
                dimensions.width = Integer.parseInt((scannerLC.match()).group(1));
            }
            if (lineLC.contains(";")) {
                exit = true;
            }
            if (!exit) {
                readLine();
            }
        }
        return dimensions;
    }

    private Format parseFormat() {
        Format format = new Format();

        boolean exit = false;
        while (!exit) {
            format.labels = findNoOptions(format.labels, "labels");
            format.interleaved = findNoOptions(format.interleaved, "interleaved");
            format.diagonal = findNoOptions(format.interleaved, "interleaved");
            format.triangle = findOption(format.triangle, "triangle");
            format.activeFlags = findNoOptions(format.activeFlags, "activeflags");
            format.weights = findNoOptions(format.weights, "weights");
            format.confidences = findNoOptions(format.confidences, "confidences");

            if (lineLC.contains(";")) {
                exit = true;
            }
            if (!exit) {
                readLine();
            }
        }
        return format;
    }

    private Cycle parseCycle() {
        Cycle cycle = new Cycle();

        if (lineLC.contains("cycle")) {
            lineLC = lineLC.substring(5).trim();
            List<Integer> permutation = new LinkedList<>();
            boolean exit = false;
            while (!exit) {
                String[] tmp = lineLC.split("\\s+");
                for (int i = 0; i < tmp.length; i++) {
                    if (!tmp[i].contentEquals(";")) {
                        try {
                            permutation.add(Integer.parseInt(tmp[i].replace(";", "")));
                        } catch (NumberFormatException nfe) {
                            exitError("Taxa indexes in the cycle must be integers");
                        }
                    }
                }

                if (lineLC.contains(";")) {
                    exit = true;
                }
                if (!exit) {
                    readLine();
                }
            }
            cycle.permutation = new int[permutation.size()];
            for (int i = 0; i < permutation.size(); i++) {
                cycle.permutation[i] = permutation.get(i) - 1;
            }
        }
        return cycle;
    }

    private Draw parseDraw() {
        Draw draw = new Draw();

        boolean exit = false;
        while (!exit) {
            if (scannerLC.findInLine("rotateabout\\s*=\\s*(\\S+)[\\s;]") != null) {
                try {
                    draw.angle = Double.parseDouble(scannerLC.match().group(1));
                } catch (NumberFormatException nfe) {
                    exitError("Rotation angle must be real number");
                }
            }
            if (scannerLC.findInLine("scale\\s*=\\s*(\\S+)[\\s;]") != null) {
                try {
                    draw.scale = Double.parseDouble(scannerLC.match().group(1));
                } catch (NumberFormatException nfe) {
                    exitError("Scale factor must be real number");
                }

            }
            if (lineLC.contains(";")) {
                exit = true;
            }
            if (!exit) {
                readLine();
            }
        }
        return draw;

    }

    private String findOption(String option, String optiontext) {
        matched = scannerLC.findInLine(optiontext + "\\s*=\\s*(\\w+);?");
        if (matched != null) {
            option = scannerLC.match().group(1);
        }

        return option;
    }

    private boolean findNoOptions(boolean option, String optiontext) {
        matched = scannerLC.findInLine(optiontext + "\\s*=\\s*(\\w+);?");
        if (matched != null) {
            if ((scannerLC.match()).group(1).contentEquals("no")) {
                option = false;
            } else if ((scannerLC.match()).group(1).contentEquals("yes")) {
                option = true;
            }
        } else {
            matched = scannerLC.findInLine("no" + optiontext + "[;\\s]");
            option = (matched != null) ? false : option;
        }
        return option;
    }

    private void openFile(String inFile) {
        try {
            br = new BufferedReader(new FileReader(inFile));
        } catch (FileNotFoundException fnfe) {
            System.err.println("File " + inFile + " not found.");
            System.exit(1);
        }
    }

    private void closeFile() {
        try {
            br.close();
        } catch (IOException ioe) {
            System.err.println("Unable to close input file.");
        }
    }

    private boolean findBlock() {
        readLine();
        while (originalLine != null && scannerLC.findInLine("begin\\s+" + block) == null) {
            readLine();
        }
        return (originalLine != null);
    }

    protected void readLine() {
        try {
            line = br.readLine();
            originalLine = line;
            line = (line != null) ? line.trim() : "";
            lineLC = (line != null) ? line.toLowerCase() : "";

            scanner = new Scanner(line);
            scannerLC = new Scanner(lineLC);
        } catch (IOException ioe) {
            System.err.println("Error reading input file.");
            System.exit(1);
        }
    }

    public void exitError(String message) {
        System.err.println("Error: " + message + ":\n" + originalLine + "\n");
        System.exit(1);
    }

    protected abstract void initializeDataStructures(Dimensions dimensions);

    protected abstract void parseLine(Format format);

    protected abstract E createObject(Dimensions dimensions, Cycle cycle, Draw draw);

    protected class Dimensions {

        Integer nTax;
        Integer nChar;
        Integer nSplits;
        Integer nVertices;
        Integer nEdges;
        Integer height;
        Integer width;

        public Dimensions() {
        }

        public void print() {
            String string = "Dimensions:\n";
            if (nTax != null) {
                string = string + "\tntax=" + nTax + "\n";
            }
            if (nChar != null) {
                string = string + "\tnchar=" + nChar + "\n";
            }
            if (nSplits != null) {
                string = string + "\tnsplits=" + nSplits + "\n";
            }
            if (nVertices != null) {
                string = string + "\tnvertices=" + nVertices + "\n";
            }
            if (nEdges != null) {
                string = string + "\tnedges=" + nEdges + "\n";
            }

            System.out.println(string);
        }
    }

    protected class Format {

        boolean labels;
        boolean interleaved;
        String triangle;
        boolean diagonal;
        boolean activeFlags;
        boolean weights;
        boolean confidences;

        public Format() {
            labels = true;
            interleaved = true;
            triangle = "both";
            diagonal = false;
            activeFlags = true;
            weights = true;
            confidences = false;
        }

        public void print() {
            String string = "Format:\n";
            if (labels == true) {
                string = string + "\tlabels\n";
            }
            if (interleaved == true) {
                string = string + "\tinterleaved\n";
            }
            string = string + "\ttriangle=" + triangle + "\n";
            if (diagonal == true) {
                string = string + "\tdiagonal\n";
            }
            if (activeFlags == true) {
                string = string + "\tactiveflags\n";
            }
            if (weights == true) {
                string = string + "\tweights\n";
            }
            if (confidences == true) {
                string = string + "\tconfidences\n";
            }

            System.out.println(string);
        }
    }

    protected class Cycle {

        int[] permutation;
    }

    protected class Draw {
        double angle;
        double scale;
    }
}
