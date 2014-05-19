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

package uk.ac.uea.cmp.spectre.core.ds.split;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dan on 21/03/14.
 */
public class SpectreSplitSystemTest {

    @Test
    public void testCompatibleAndCircular() {

        List<Split> splits = new ArrayList<>();

        final int nbTaxa = 5;

        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{1}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{2}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{3}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{4}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{5}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{1,2}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{4,5}), nbTaxa));

        SplitSystem ss = new SpectreSplitSystem(nbTaxa, splits);

        assertNotNull(ss);
        assertTrue(ss.isCompatible());
        assertTrue(ss.isCircular());
    }

    @Test
    public void testInCompatibleAndNotCircular() {

        List<Split> splits = new ArrayList<>();

        final int nbTaxa = 5;

        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{1}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{2}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{3}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{4}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{5}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{1,5}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{2,4}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{1,2}), nbTaxa));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{4,5}), nbTaxa));

        SplitSystem ss = new SpectreSplitSystem(nbTaxa, splits);

        assertNotNull(ss);
        assertFalse(ss.isCompatible());
        assertFalse(ss.isCircular());
    }
}
