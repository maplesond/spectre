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

package uk.ac.uea.cmp.phygen.core.ds.tree.newick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 08/11/13
 * Time: 00:08
 * To change this template use File | Settings | File Templates.
 */
abstract class NewickNode {

    protected String name;
    protected List<NewickNode> branches;
    protected double length;

    protected NewickNode() {
        this.name = "";
        this.branches = new ArrayList<>();
        this.length = 0.0;
    }

    public NewickNode getFirstBranch() {
        if (branches.size() >= 1)
            return branches.get(0);

        return null;
    }

    public NewickNode getSecondBranch() {
        if (branches.size() >= 2)
            return branches.get(1);

        return null;
    }

    public boolean hasChildren() {
        return this.branches.size() > 0;
    }

    public boolean isLeaf() {
        return this.branches.isEmpty();
    }

    public boolean isBinary() {

        if (this.isLeaf())
            return true;

        if (branches.size() != 2)
            return false;

        return this.getFirstBranch().isBinary() && this.getSecondBranch().isBinary();
    }

    public List<NewickNode> getBranches() {
        return this.branches;
    }

    public String getName() {
        return this.name;
    }

    public double getLength() {
        return this.length;
    }

    void setName(String name) {
        this.name = name;
    }

    void setBranches(List<NewickNode> branches) {
        this.branches = branches;
    }

    void setLength(double length) {
        this.length = length;
    }
}
