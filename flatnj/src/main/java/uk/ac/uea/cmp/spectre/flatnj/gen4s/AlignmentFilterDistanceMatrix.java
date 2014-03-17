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

package uk.ac.uea.cmp.spectre.flatnj.gen4s;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

import java.util.Set;

/**
 * Multiple sequence alignment filter for illegal characters based on the
 * {@linkplain uk.ac.uea.cmp.spectre.core.ds.distance.ImmutableDistanceMatrix}.
 * 
 * @author balvociute
 */
public class AlignmentFilterDistanceMatrix extends AlignmentFilter
{
    /**
     * Set that contains all symbols that are allowed in the 
     * {@linkplain uk.ac.uea.cmp.spectre.core.ds.Alignment}.
     */
    private Set<String> allowedSymbols;

    /**
     * Initializes {@linkplain AlignmentFilter} using 
     * {@linkplain uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix}.
     * @param dm {@linkplain  uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix}.
     */
    public AlignmentFilterDistanceMatrix(DistanceMatrix dm)
    {
        allowedSymbols = dm.getTaxa().getNameSet();
    }

    @Override
    public boolean isAllowed(char symbol)
    {
        if(allowedSymbols.contains(symbol))
        {
            /* if symbol is in the filter then it is allowed */
            return true;
        }
        else
        {
            /* otherwise not. */
            return false;
        }
    }

    /**
     * Returns the alphabet that this filter is based on.
     * @return a set containing allowable symbols.
     */
    public Set getAllowedSymbols()
    {
        return allowedSymbols;
    }
    
}
