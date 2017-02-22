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

package uk.ac.uea.cmp.spectre.core.ds.split.circular;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.Arrays;

/**
 * Created by dan on 11/08/14.
 */
public class CircularOrdering {

    private String[] taxa;

    public CircularOrdering(String[] taxa) {
        this.taxa = taxa;
    }

    public CircularOrdering(CircularOrdering co) {
        this.taxa = new String[co.taxa.length];
        System.arraycopy(co.taxa, 0, this.taxa, 0, co.taxa.length);
    }

    public CircularOrdering(IdentifierList idl) {

        this.taxa = new String[idl.size()];

        for(int i = 0; i < idl.size(); i++) {
            this.taxa[i] = new String(idl.get(i).getName());
        }
    }


    private static int indexOf(String[] array, String taxon) {
        for(int i = 0; i < array.length; i++) {
            if (array[i].equals(taxon)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircularOrdering that = (CircularOrdering) o;

        if (this.taxa.length == 0 && that.taxa.length == 0) {
            return true;
        }

        if (this.taxa.length != that.taxa.length) {
            return false;
        }

        int startPos = indexOf(that.taxa, this.taxa[0]);

        // forward check
        boolean forwardOk = true;
        for(int i = 0; i < this.taxa.length; i++) {

            int offset = i + startPos > this.taxa.length - 1 ?
                        i + startPos - this.taxa.length :
                        i + startPos;

            if (!this.taxa[i].equals(that.taxa[offset])) {
                forwardOk = false;
                break;
            }
        }

        if (forwardOk) {
            return true;
        }

        // forward check failed so try reverse check
        for(int i = 0; i < this.taxa.length; i++) {

            int offset = -i + startPos < 0 ?
                    -i + startPos + this.taxa.length :
                    -i + startPos;

            if (!this.taxa[i].equals(that.taxa[offset])) {
                return false;
            }

        }

        return true;

    }

    @Override
    public int hashCode() {
        return taxa != null ? Arrays.hashCode(taxa) : 0;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(org.apache.commons.lang3.StringUtils.join(this.taxa, ","));
        sb.append("]");

        return sb.toString();
    }
}
