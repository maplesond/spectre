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

package uk.ac.uea.cmp.spectre.flatnj.netvi;

import java.util.HashSet;

/**
 * @param <T>
 * @author Monika
 */
public class SelectableSet<T> extends HashSet<T> {

    public SelectableSet() {
        super();
    }

    @Override
    public boolean add(T t) {
        try {
            ((Selectable) t).setSelected(true);
        } catch (Exception ex) {
        }
        return super.add(t);
    }

    @Override
    public void clear() {
        for (T t : this) {
            try {
                ((Selectable) t).setSelected(false);
            } catch (Exception ex) {
            }
        }
        super.clear();
    }

}
