/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.core.ds.split;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 18/11/13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public interface SplitSystem {


    // **** Taxa methods ****

    int getNbTaxa();

    Taxa getTaxa();



    // **** Standard split methods ****

    int getNbSplits();

    List<Split> getSplits();

    Split getSplitAt(final int i);



    // **** Methods related to split weights ****

    boolean isWeighted();

    double getWeightAt(final int i);

    List<Split> filterByWeight(double threshold);


    // **** Methods related to circular ordering ****

    boolean isCircular();

    CircularOrdering getCircularOrdering();


}
