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

/**
 * @author balvociutes
 */
public class Utilities {


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

    public static int[] colorToInt(Color c) {
        int[] rgb = new int[3];

        rgb[0] = c.getRed();
        rgb[1] = c.getGreen();
        rgb[2] = c.getBlue();

        return rgb;
    }

}
