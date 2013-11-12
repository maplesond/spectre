// Generated from NewickTree.g4 by ANTLR 4.1

package uk.ac.uea.cmp.phygen.core.ds.tree.newick.parser;

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
		T__3=1, T__2=2, T__1=3, T__0=4, INT=5, REAL=6, NAME=7, WS=8, LENGTH=9;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"')'", "','", "'('", "';'", "INT", "REAL", "NAME", "WS", "LENGTH"
	};
	public static final String[] ruleNames = {
		"T__3", "T__2", "T__1", "T__0", "INT", "REAL", "DIGIT", "NAME", "WS", 
		"LENGTH"
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
		case 8: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\13J\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\6\6!\n\6\r\6\16\6\"\3\7\6\7&\n"+
		"\7\r\7\16\7\'\3\7\3\7\7\7,\n\7\f\7\16\7/\13\7\3\7\3\7\6\7\63\n\7\r\7\16"+
		"\7\64\3\7\5\78\n\7\3\b\3\b\3\t\6\t=\n\t\r\t\16\t>\3\n\6\nB\n\n\r\n\16"+
		"\nC\3\n\3\n\3\13\3\13\3\13\2\f\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17"+
		"\2\1\21\t\1\23\n\2\25\13\1\3\2\5\3\2\62;\4\2C\\c|\5\2\13\f\17\17\"\"P"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\3\27\3\2\2\2\5\31\3\2\2\2"+
		"\7\33\3\2\2\2\t\35\3\2\2\2\13 \3\2\2\2\r\67\3\2\2\2\179\3\2\2\2\21<\3"+
		"\2\2\2\23A\3\2\2\2\25G\3\2\2\2\27\30\7+\2\2\30\4\3\2\2\2\31\32\7.\2\2"+
		"\32\6\3\2\2\2\33\34\7*\2\2\34\b\3\2\2\2\35\36\7=\2\2\36\n\3\2\2\2\37!"+
		"\5\17\b\2 \37\3\2\2\2!\"\3\2\2\2\" \3\2\2\2\"#\3\2\2\2#\f\3\2\2\2$&\5"+
		"\17\b\2%$\3\2\2\2&\'\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2()\3\2\2\2)-\7\60\2"+
		"\2*,\5\17\b\2+*\3\2\2\2,/\3\2\2\2-+\3\2\2\2-.\3\2\2\2.8\3\2\2\2/-\3\2"+
		"\2\2\60\62\7\60\2\2\61\63\5\17\b\2\62\61\3\2\2\2\63\64\3\2\2\2\64\62\3"+
		"\2\2\2\64\65\3\2\2\2\658\3\2\2\2\668\5\13\6\2\67%\3\2\2\2\67\60\3\2\2"+
		"\2\67\66\3\2\2\28\16\3\2\2\29:\t\2\2\2:\20\3\2\2\2;=\t\3\2\2<;\3\2\2\2"+
		"=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?\22\3\2\2\2@B\t\4\2\2A@\3\2\2\2BC\3\2\2"+
		"\2CA\3\2\2\2CD\3\2\2\2DE\3\2\2\2EF\b\n\2\2F\24\3\2\2\2GH\7<\2\2HI\5\r"+
		"\7\2I\26\3\2\2\2\n\2\"\'-\64\67>C";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}