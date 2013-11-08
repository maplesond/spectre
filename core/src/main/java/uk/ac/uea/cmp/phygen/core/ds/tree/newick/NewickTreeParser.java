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
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NewickTreeParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, SubTree=2, Leaf=3, Internal=4, Branch=5, Name=6, Length=7, Weight=8, 
		Number=9, String=10, WS=11;
	public static final String[] tokenNames = {
		"<INVALID>", "';'", "SubTree", "Leaf", "Internal", "Branch", "Name", "Length", 
		"Weight", "Number", "String", "WS"
	};
	public static final int
		RULE_parse = 0;
	public static final String[] ruleNames = {
		"parse"
	};

	@Override
	public String getGrammarFileName() { return "NewickTree.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public NewickTreeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(NewickTreeParser.EOF, 0); }
		public TerminalNode Weight() { return getToken(NewickTreeParser.Weight, 0); }
		public TerminalNode Branch() { return getToken(NewickTreeParser.Branch, 0); }
		public TerminalNode SubTree() { return getToken(NewickTreeParser.SubTree, 0); }
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitParse(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2);
			_la = _input.LA(1);
			if ( !(_la==SubTree || _la==Branch) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(3); match(1);
			setState(4); match(Weight);
			setState(5); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\r\n\4\2\t\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\2\3\2\2\3\4\2\4\4\7\7\b\2\4\3\2\2\2\4\5\t\2\2\2\5\6"+
		"\7\3\2\2\6\7\7\n\2\2\7\b\7\2\2\3\b\3\3\2\2\2\2";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}