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
package uk.ac.uea.cmp.spectre.core.ds.tree;

import java.util.List;

/**
 * InnerNode class
 */
public class InnerNode implements BinaryTree {

    private BinaryTree left, right;

    public InnerNode(BinaryTree aLeft, BinaryTree aRight) {
        left = aLeft;
        right = aRight;
    }

    @Override
    public void fillList(List<Integer> aList) {
        left.fillList(aList);
        right.fillList(aList);
    }

    public BinaryTree getLeft() {
        return left;
    }

    public BinaryTree getRight() {
        return right;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }
}