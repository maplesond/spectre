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

package uk.ac.uea.cmp.spectre.flatnj;

/**
 * Multiple sequence alignment filter for illegal characters.
 *
 * @author balvociute
 */
public class AlignmentFilter {
    /**
     * Checks if the <code>symbol</code> is in the alphabet.
     *
     * @param symbol a character to be checked.
     * @return true if <code>symbol</code> is in the alphabet and false
     * otherwise.
     */
    public boolean isAllowed(char symbol) {
        if (symbol == '-') {
            return false;
        } else {
            return true;
        }
    }

}
