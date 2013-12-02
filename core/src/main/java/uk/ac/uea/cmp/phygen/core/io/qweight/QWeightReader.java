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

package uk.ac.uea.cmp.phygen.core.io.qweight;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.quartet.*;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartet;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;
import uk.ac.uea.cmp.phygen.core.io.AbstractPhygenReader;
import uk.ac.uea.cmp.phygen.core.io.PhygenDataType;
import uk.ac.uea.cmp.phygen.core.io.qweight.parser.QWeightLexer;
import uk.ac.uea.cmp.phygen.core.io.qweight.parser.QWeightParser;
import uk.ac.uea.cmp.phygen.core.io.qweight.parser.QWeightPopulator;
import uk.ac.uea.cmp.phygen.core.util.DefaultParsingErrorListener;
import uk.ac.uea.cmp.phygen.core.util.DefaultParsingErrorStrategy;

import java.io.*;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 01/12/13
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
@MetaInfServices(uk.ac.uea.cmp.phygen.core.io.PhygenReader.class)
public class QWeightReader extends AbstractPhygenReader {


    public QuartetNetwork parse(File file) throws IOException {
        return this.parse(file, false);
    }

    public QuartetNetwork parse(File file, boolean logNormalize) throws IOException {

        // Convert loader into a character stream
        CharStream in = new ANTLRInputStream(new FileInputStream(file));

        // Setup lexer
        QWeightLexer lexer = new QWeightLexer(in);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new DefaultParsingErrorListener());

        // Do the lexing
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // The results of parsing go in here
        QuartetNetwork quartetNetwork = new QuartetNetwork();

        // Setup parser
        QWeightParser parser = new QWeightParser(tokens);
        parser.removeParseListeners();
        parser.removeErrorListeners();
        parser.addParseListener(new QWeightPopulator(quartetNetwork, logNormalize));
        parser.addErrorListener(new DefaultParsingErrorListener());
        parser.setErrorHandler(new DefaultParsingErrorStrategy());

        // Do the parsing
        try {
            parser.parse();
        }
        catch(RuntimeException e) {
            throw new IOException(e);
        }

        // Return the populated Nexus object
        return quartetNetwork;
    }


    @Override
    public String[] commonFileExtensions() {
        return new String[]{"qw"};
    }

    @Override
    public String getIdentifier() {
        return "QWEIGHT";
    }

    @Override
    public boolean acceptsDataType(PhygenDataType phygenDataType) {
        if (phygenDataType == PhygenDataType.QUARTETS)
            return true;

        return false;
    }



    // ****** Unsuppported ******

    @Override
    public DistanceMatrix readDistanceMatrix(File input) throws IOException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public List<NewickTree> readTrees(File input, double weight) throws IOException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public SplitSystem readSplitSystem(File file) throws IOException {
        throw new UnsupportedOperationException("");
    }
}
