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

package uk.ac.uea.cmp.spectre.core.io.qweight;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by dan on 12/01/14.
 */
public class QWeightFileFilter extends FileFilter {


    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String[] fileExtensions = commonFileExtensions();

        for (String ext : fileExtensions) {
            if (f.getName().endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "QWeight";
    }

    public String[] commonFileExtensions() {
        return new String[]{"qw", "qweight"};
    }
}
