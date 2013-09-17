/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

package uk.ac.uea.cmp.phygen.flatnj.ds;

/**
 *
 * @author balvociute
 */
public class Taxa
{
    private String[] taxaNames;

    public Taxa(String[] taxaNames)
    {
        this.taxaNames = new String[taxaNames.length];
        System.arraycopy(taxaNames, 0, this.taxaNames, 0, taxaNames.length);
    }

    public String[] getTaxaNames()
    {
        return taxaNames;
    }

    public Integer getN()
    {
        return taxaNames.length;
    }

}
