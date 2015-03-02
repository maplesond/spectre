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

package uk.ac.uea.cmp.spectre.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringJoiner {

    private List<String> parts;
    private String sep;

    public StringJoiner(String separator) {
        this.parts = new ArrayList<String>();
        this.sep = separator;
    }

    public void add(String part) {
        this.add("", part);
    }

    public void add(String prefix, String part) {
        this.add(true, prefix, part);
    }

    public void add(boolean test, String prefix, String part) {
        if (test && part != null && !part.isEmpty()) {
            this.parts.add((prefix == null ? "" : prefix) + part);
        }
    }

    public void add(Object obj) {
        this.add("", obj);
    }

    public void add(String prefix, Object obj) {
        if (obj != null) {
            String str = obj.toString();
            if (str != null && !str.isEmpty()) {
                this.parts.add((prefix == null ? "" : prefix) + str);
            }
        }
    }

    public String toString() {
        return StringUtils.join(this.parts, this.sep == null ? " " : this.sep);
    }

    public void clear() {
        this.parts.clear();
    }
}
