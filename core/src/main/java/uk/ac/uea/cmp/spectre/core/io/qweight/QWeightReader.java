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

package uk.ac.uea.cmp.spectre.core.io.qweight;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.io.AbstractSpectreReader;
import uk.ac.uea.cmp.spectre.core.io.SpectreDataType;
import uk.ac.uea.cmp.spectre.core.io.SpectreReader;
import uk.ac.uea.cmp.spectre.core.io.qweight.parser.QWeightLexer;
import uk.ac.uea.cmp.spectre.core.io.qweight.parser.QWeightParser;
import uk.ac.uea.cmp.spectre.core.io.qweight.parser.QWeightPopulator;
import uk.ac.uea.cmp.spectre.core.util.DefaultParsingErrorListener;
import uk.ac.uea.cmp.spectre.core.util.DefaultParsingErrorStrategy;

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
@MetaInfServices(SpectreReader.class)
public class QWeightReader extends AbstractSpectreReader {

    public GroupedQuartetSystem parse(File file) throws IOException {

        // Convert loader into a character stream
        CharStream in = new ANTLRInputStream(new FileInputStream(file));

        // Setup lexer
        QWeightLexer lexer = new QWeightLexer(in);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new DefaultParsingErrorListener());

        // Do the lexing
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // The results of parsing go in here
        GroupedQuartetSystem quartetNetwork = new GroupedQuartetSystem();

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
        } catch (RuntimeException e) {
            throw new IOException(e);
        }

        // Return the populated Nexus object
        return quartetNetwork;
    }


    @Override
    public QuartetSystem readQuartets(File file) throws IOException {
        return new QuartetSystem(this.parse(file));
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
    public boolean acceptsDataType(SpectreDataType spectreDataType) {
        if (spectreDataType == SpectreDataType.QUARTETS)
            return true;

        return false;
    }

}
