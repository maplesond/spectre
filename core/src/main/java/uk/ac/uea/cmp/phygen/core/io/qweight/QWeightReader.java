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
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.io.AbstractPhygenReader;
import uk.ac.uea.cmp.phygen.core.io.PhygenDataType;
import uk.ac.uea.cmp.phygen.core.io.qweight.parser.QWeightLexer;
import uk.ac.uea.cmp.phygen.core.io.qweight.parser.QWeightParser;
import uk.ac.uea.cmp.phygen.core.io.qweight.parser.QWeightPopulator;
import uk.ac.uea.cmp.phygen.core.util.DefaultParsingErrorListener;
import uk.ac.uea.cmp.phygen.core.util.DefaultParsingErrorStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 01/12/13
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
@MetaInfServices(uk.ac.uea.cmp.phygen.core.io.PhygenReader.class)
public class QWeightReader extends AbstractPhygenReader {

    public QuartetSystem parse(File file) throws IOException {

        // Convert loader into a character stream
        CharStream in = new ANTLRInputStream(new FileInputStream(file));

        // Setup lexer
        QWeightLexer lexer = new QWeightLexer(in);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new DefaultParsingErrorListener());

        // Do the lexing
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // The results of parsing go in here
        QuartetSystem quartetNetwork = new QuartetSystem();

        // Setup parser
        QWeightParser parser = new QWeightParser(tokens);
        parser.removeParseListeners();
        parser.removeErrorListeners();
        parser.addParseListener(new QWeightPopulator(quartetNetwork));
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
    public QuartetSystem readQuartets(File file) throws IOException {
        return this.parse(file);
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

}
