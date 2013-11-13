// Generated from uk/ac/uea/cmp/phygen/core/ds/tree/newick/parser/NewickTree.g4 by ANTLR 4.1
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
		T__4=1, T__3=2, T__2=3, T__1=4, T__0=5, REAL=6, WORD=7, WS=8;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"')'", "','", "'('", "':'", "';'", "REAL", "WORD", "WS"
	};
	public static final String[] ruleNames = {
		"T__4", "T__3", "T__2", "T__1", "T__0", "REAL", "DIGIT", "LETTER", "WORD", 
		"WS"
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
		case 9: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\nb\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\6\7#\n\7\r\7\16\7$\3\7"+
		"\3\7\7\7)\n\7\f\7\16\7,\13\7\3\7\7\7/\n\7\f\7\16\7\62\13\7\3\7\3\7\6\7"+
		"\66\n\7\r\7\16\7\67\3\7\6\7;\n\7\r\7\16\7<\5\7?\n\7\3\b\3\b\3\t\3\t\3"+
		"\n\6\nF\n\n\r\n\16\nG\3\n\3\n\6\nL\n\n\r\n\16\nM\3\n\3\n\3\n\3\n\6\nT"+
		"\n\n\r\n\16\nU\3\n\3\n\5\nZ\n\n\3\13\6\13]\n\13\r\13\16\13^\3\13\3\13"+
		"\2\f\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\2\1\21\2\1\23\t\1\25\n\2"+
		"\3\2\5\3\2\62;\4\2C\\c|\5\2\13\f\17\17\"\"l\2\3\3\2\2\2\2\5\3\2\2\2\2"+
		"\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\23\3\2\2\2\2\25\3\2"+
		"\2\2\3\27\3\2\2\2\5\31\3\2\2\2\7\33\3\2\2\2\t\35\3\2\2\2\13\37\3\2\2\2"+
		"\r>\3\2\2\2\17@\3\2\2\2\21B\3\2\2\2\23Y\3\2\2\2\25\\\3\2\2\2\27\30\7+"+
		"\2\2\30\4\3\2\2\2\31\32\7.\2\2\32\6\3\2\2\2\33\34\7*\2\2\34\b\3\2\2\2"+
		"\35\36\7<\2\2\36\n\3\2\2\2\37 \7=\2\2 \f\3\2\2\2!#\5\17\b\2\"!\3\2\2\2"+
		"#$\3\2\2\2$\"\3\2\2\2$%\3\2\2\2%&\3\2\2\2&*\7\60\2\2\')\5\17\b\2(\'\3"+
		"\2\2\2),\3\2\2\2*(\3\2\2\2*+\3\2\2\2+?\3\2\2\2,*\3\2\2\2-/\5\17\b\2.-"+
		"\3\2\2\2/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\63\3\2\2\2\62\60\3\2"+
		"\2\2\63\65\7\60\2\2\64\66\5\17\b\2\65\64\3\2\2\2\66\67\3\2\2\2\67\65\3"+
		"\2\2\2\678\3\2\2\28?\3\2\2\29;\5\17\b\2:9\3\2\2\2;<\3\2\2\2<:\3\2\2\2"+
		"<=\3\2\2\2=?\3\2\2\2>\"\3\2\2\2>\60\3\2\2\2>:\3\2\2\2?\16\3\2\2\2@A\t"+
		"\2\2\2A\20\3\2\2\2BC\t\3\2\2C\22\3\2\2\2DF\5\21\t\2ED\3\2\2\2FG\3\2\2"+
		"\2GE\3\2\2\2GH\3\2\2\2HZ\3\2\2\2IK\7$\2\2JL\5\17\b\2KJ\3\2\2\2LM\3\2\2"+
		"\2MK\3\2\2\2MN\3\2\2\2NO\3\2\2\2OP\7$\2\2PZ\3\2\2\2QS\7$\2\2RT\5\21\t"+
		"\2SR\3\2\2\2TU\3\2\2\2US\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\7$\2\2XZ\3\2\2"+
		"\2YE\3\2\2\2YI\3\2\2\2YQ\3\2\2\2Z\24\3\2\2\2[]\t\4\2\2\\[\3\2\2\2]^\3"+
		"\2\2\2^\\\3\2\2\2^_\3\2\2\2_`\3\2\2\2`a\b\13\2\2a\26\3\2\2\2\16\2$*\60"+
		"\67<>GMUY^";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}