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
package uk.ac.uea.cmp.spectre.qtools.qnet.holders;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.List;

public class ZHolder {

    private int[] sizes;

    public ZHolder(List<IdentifierList> paths, int N) {

        sizes = new int[N];

        for (int i = 1; i <= N; i++) {
            sizes[i - 1] = z(paths, i);
        }
    }

    public int getZ(int i) {

        return sizes[i - 1];
    }

    public void setZ(int i, int newZ) {

        sizes[i - 1] = newZ;
    }

    protected int z(List<IdentifierList> paths, int i) {

        for (IdentifierList p : paths) {

            if (p.containsId(i)) {

                if (i == p.first().getId()) {
                    return p.size();
                }
            }
        }

        return 0;
    }
}
