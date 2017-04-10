/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.util;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 15/11/13
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class DefaultParsingErrorListener implements ANTLRErrorListener {


    /**
     * Just rethrows exception
     *
     * @param recognizer Recognizer
     * @param offendingSymbol Offending symbol
     * @param line Line error occured at
     * @param charPositionInLine Character in line causing the problem
     * @param msg Error message
     * @param e Recognition Exception object
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new RuntimeException("Line: " + line + "; Char: " + charPositionInLine + "; Message: " + msg, e);
    }


    @Override
    public void reportAmbiguity( Parser parser,  DFA dfa, int i, int i2, boolean b,  BitSet bitSet,  ATNConfigSet atnConfigs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reportAttemptingFullContext( Parser parser,  DFA dfa, int i, int i2, BitSet bitSet,  ATNConfigSet atnConfigs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reportContextSensitivity( Parser parser,  DFA dfa, int i, int i2, int i3,  ATNConfigSet atnConfigs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
