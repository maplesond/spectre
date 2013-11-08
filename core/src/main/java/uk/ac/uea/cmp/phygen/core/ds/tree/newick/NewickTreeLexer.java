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

package uk.ac.uea.cmp.phygen.core.ds.tree.newick;// Generated from NewickTree.g4 by ANTLR 4.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NewickTreeLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, SubTree=2, Leaf=3, Internal=4, Branch=5, Name=6, Length=7, Weight=8, 
		Number=9, String=10, WS=11;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"';'", "SubTree", "Leaf", "Internal", "Branch", "Name", "Length", "Weight", 
		"Number", "String", "WS"
	};
	public static final String[] ruleNames = {
		"T__0", "SubTree", "Leaf", "Internal", "Branch", "Name", "Length", "Weight", 
		"Number", "String", "WS"
	};


	public NewickTreeLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "NewickTree.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 10: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\rS\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\3\3\3\5\3\36\n\3\3\4\3\4\3\5\3\5\3\5\3\5\7\5&\n"+
		"\5\f\5\16\5)\13\5\3\5\3\5\3\5\3\6\3\6\3\6\3\7\5\7\62\n\7\3\b\3\b\5\b\66"+
		"\n\b\3\t\5\t9\n\t\3\n\6\n<\n\n\r\n\16\n=\3\n\3\n\6\nB\n\n\r\n\16\nC\5"+
		"\nF\n\n\3\13\6\13I\n\13\r\13\16\13J\3\f\6\fN\n\f\r\f\16\fO\3\f\3\f\2\r"+
		"\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27"+
		"\r\2\3\2\4\4\2C\\c|\5\2\13\f\17\17\"\"\\\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\3\31\3\2\2\2\5\35\3\2\2\2\7\37"+
		"\3\2\2\2\t!\3\2\2\2\13-\3\2\2\2\r\61\3\2\2\2\17\65\3\2\2\2\218\3\2\2\2"+
		"\23;\3\2\2\2\25H\3\2\2\2\27M\3\2\2\2\31\32\7=\2\2\32\4\3\2\2\2\33\36\5"+
		"\7\4\2\34\36\5\t\5\2\35\33\3\2\2\2\35\34\3\2\2\2\36\6\3\2\2\2\37 \5\r"+
		"\7\2 \b\3\2\2\2!\"\7*\2\2\"\'\5\13\6\2#$\7.\2\2$&\5\13\6\2%#\3\2\2\2&"+
		")\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2(*\3\2\2\2)\'\3\2\2\2*+\7+\2\2+,\5\r\7"+
		"\2,\n\3\2\2\2-.\5\5\3\2./\5\17\b\2/\f\3\2\2\2\60\62\5\25\13\2\61\60\3"+
		"\2\2\2\61\62\3\2\2\2\62\16\3\2\2\2\63\64\7<\2\2\64\66\5\23\n\2\65\63\3"+
		"\2\2\2\65\66\3\2\2\2\66\20\3\2\2\2\679\5\23\n\28\67\3\2\2\289\3\2\2\2"+
		"9\22\3\2\2\2:<\4\62;\2;:\3\2\2\2<=\3\2\2\2=;\3\2\2\2=>\3\2\2\2>E\3\2\2"+
		"\2?A\7\60\2\2@B\4\62;\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2DF\3\2"+
		"\2\2E?\3\2\2\2EF\3\2\2\2F\24\3\2\2\2GI\t\2\2\2HG\3\2\2\2IJ\3\2\2\2JH\3"+
		"\2\2\2JK\3\2\2\2K\26\3\2\2\2LN\t\3\2\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2\2O"+
		"P\3\2\2\2PQ\3\2\2\2QR\b\f\2\2R\30\3\2\2\2\r\2\35\'\61\658=CEJO";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}