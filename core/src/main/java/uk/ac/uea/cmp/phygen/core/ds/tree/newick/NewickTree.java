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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 06/11/13
 * Time: 22:30
 * To change this template use File | Settings | File Templates.
 */
public class NewickTree extends NewickNode {

    private double scalingFactor;

    public NewickTree(String source) throws IOException {
        super();
        this.scalingFactor = 1.0;
        this.parseNewick(source, scalingFactor);
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

    public void parseNewick(String source, double scalingFactor) throws IOException {

        if (source == null)
            return;

        String trimmedSource = source.trim();

        CharStream in = new ANTLRInputStream(trimmedSource);
        NewickTreeLexer lexer = new NewickTreeLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NewickTreeParser parser = new NewickTreeParser(tokens);
        parser.addParseListener(new NewickTreePopulator(this));
        parser.parse();

        // This NewickTree should be nicely populated now

        //TODO apply scaling factor  ???
    }

}
