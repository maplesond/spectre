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
package uk.ac.uea.cmp.phygen.tools.chopper;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 21:26:52 To
 * change this template use Options | File Templates.
 */
public class Traverser {

    // do it with all trees
    public static LinkedList<QuartetWeights> traverse(LinkedList<Tree> trees, int N) {

        ListIterator<Tree> lI = trees.listIterator();

        LinkedList<QuartetWeights> qWs = new LinkedList<>();

        while (lI.hasNext()) {
            qWs.add(quartetize(lI.next(), N));
        }

        return qWs;

    }

    // do it with any tree
    public static QuartetWeights quartetize(Tree tree, int N) {

        QuartetWeights qW = new QuartetWeights();

        qW.ensureCapacity(N);

        // this gives values for non-supported quartets in this tree...

        qW.initialize();

        tree.split(qW, new LinkedList<Integer>());

        return qW;

    }
}
