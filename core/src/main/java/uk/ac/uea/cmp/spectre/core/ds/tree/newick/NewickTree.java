/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ds.tree.newick;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.parser.NewickTreeLexer;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.parser.NewickTreeParser;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.parser.NewickTreePopulator;
import uk.ac.uea.cmp.spectre.core.util.DefaultParsingErrorListener;
import uk.ac.uea.cmp.spectre.core.util.DefaultParsingErrorStrategy;

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
    private IdentifierList taxa;

    public NewickTree(String source) throws IOException {
        super();
        this.scalingFactor = 1.0;
        this.taxa = new IdentifierList();
        this.parseNewick(source, scalingFactor);
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

    public void parseNewick(String source, double scalingFactor) throws IOException {

        // Make sure we have something to work with
        if (source == null)
            return;

        // Parse should handle this but let's make it easy for it
        String trimmedSource = source.trim();

        // Just ignore empty lines
        if (trimmedSource.isEmpty()) {
            return;
        }

        // Convert loader into a character stream
        CharStream in = new ANTLRInputStream(trimmedSource);

        // Setup lexer
        NewickTreeLexer lexer = new NewickTreeLexer(in);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new DefaultParsingErrorListener());

        // Do the lexing
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Setup parser
        NewickTreeParser parser = new NewickTreeParser(tokens);
        parser.removeParseListeners();
        parser.removeErrorListeners();
        parser.addParseListener(new NewickTreePopulator(this, false));
        parser.addErrorListener(new DefaultParsingErrorListener());
        parser.setErrorHandler(new DefaultParsingErrorStrategy());

        // Do the parsing
        parser.parse();

        // This NewickTree should be nicely populated now.  Store some convenience methods
        this.taxa = this.findAllTaxa();

        // Set default indicies to taxa
        this.taxa.setDefaultIndicies();

        //TODO apply scaling factor  ???
    }

    public void applyPrefixToNames(String prefix) {
        if (this.branches.isEmpty())
            return;

        for(Identifier id : this.taxa) {
            id.setName(prefix + id.getName());
        }
    }

    @Override
    public String toString() {

        if (this.branches.isEmpty())
            return "";

        if (this.branches.size() != 1)
            return "";

        return this.getFirstBranch() + ";";
    }

    @Override
    public boolean isBinary() {

        if (this.branches.isEmpty())
            return true;

        if (this.branches.size() != 1)
            return false;

        return this.getFirstBranch().isBinary();
    }

    @Override
    public boolean allHaveLengths() {

        if (this.branches.isEmpty())
            return true;

        if (this.branches.size() != 1)
            return false;

        return this.getFirstBranch().allHaveLengths();
    }


    public IdentifierList getTaxa() {
        return this.taxa;
    }

    public CanonicalWeightedQuartetMap createQuartets() {

        CanonicalWeightedQuartetMap qW = new CanonicalWeightedQuartetMap();

        this.split(qW, new IdentifierList());

        return qW;
    }


}
