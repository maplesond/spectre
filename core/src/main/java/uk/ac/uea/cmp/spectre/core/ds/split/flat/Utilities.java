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

package uk.ac.uea.cmp.spectre.core.ds.split.flat;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;

/**
 * @author balvociutes
 */
public class Utilities {

    public static void printTaxaBlock(Writer outputFile, String[] taxa) throws IOException {
        outputFile.write("#NEXUS\n");
        outputFile.write("BEGIN TAXA;\n");
        outputFile.write("DIMENSIONS NTAX=" + taxa.length + ";\n");
        outputFile.write("TAXLABELS\n");
        for (int i = 0; i < taxa.length; i++) {
            outputFile.write("[" + (i + 1) + "]  '" + taxa[i] + "'\n");
        }
        outputFile.write(";\nEND;\n\n");
    }

    public static void printFictitiousTaxaBlock(Writer outputFile, int nTaxa) throws IOException {
        outputFile.write("#NEXUS\n");
        outputFile.write("BEGIN TAXA;\n");
        outputFile.write("DIMENSIONS NTAX=" + nTaxa + ";\n");
        outputFile.write("TAXLABELS\n");
        for (int i = 0; i < nTaxa; i++) {
            outputFile.write("[" + (i + 1) + "]  'Taxon" + i + "'\n");
        }
        outputFile.write(";\nEND;\n\n");
    }

    public static int combination(int i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static int combinations(int above, int below) {
        int c = 0;
        if (below == above) {
            c = 1;
        } else if (below > above) {
            c = below;
            for (int i = 1; i < above; i++) {
                c *= (below - i);
            }
            for (int i = 2; i <= above; i++) {
                c /= i;
            }
        }
        return c;
    }


    public static Color getColor(int[] c) {
        return new Color(c[0], c[1], c[2]);
    }

    public static int[] colorToInt(Color c) {
        int[] rgb = new int[3];

        rgb[0] = c.getRed();
        rgb[1] = c.getGreen();
        rgb[2] = c.getBlue();

        return rgb;
    }

    public static boolean differentSide(int x1, int y1, int x2, int y2,
                                        int X1, int Y1, int X2, int Y2) {
        return different(x1, y1, x1, y2, X1, Y1, X2, Y2)
                &&
                different(X1, Y1, X2, Y2, x1, y1, x1, y2);
    }

    private static boolean different(int x1, int y1, int x2, int y2,
                                     int X1, int Y1, int X2, int Y2) {
        return (Math.signum((x1 - X1) * (y2 - Y1) - (y1 - Y1) * (x2 - X1))
                !=
                Math.signum((x1 - X2) * (y2 - Y2) - (y1 - Y2) * (x2 - X2)));
    }
}
