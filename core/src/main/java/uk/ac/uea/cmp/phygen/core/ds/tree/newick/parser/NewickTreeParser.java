// Generated from uk/ac/uea/cmp/phygen/core/ds/tree/newick/parser/NewickTree.g4 by ANTLR 4.1
package uk.ac.uea.cmp.phygen.core.ds.tree.newick.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NewickTreeParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__4=1, T__3=2, T__2=3, T__1=4, T__0=5, REAL=6, WORD=7, WS=8;
	public static final String[] tokenNames = {
		"<INVALID>", "')'", "','", "'('", "':'", "';'", "REAL", "WORD", "WS"
	};
	public static final int
		RULE_parse = 0, RULE_subtree = 1, RULE_leaf = 2, RULE_internal = 3, RULE_branchset = 4, 
		RULE_branch = 5, RULE_name = 6, RULE_length = 7, RULE_weight = 8;
	public static final String[] ruleNames = {
		"parse", "subtree", "leaf", "internal", "branchset", "branch", "name", 
		"length", "weight"
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
		public BranchContext branch() {
			return getRuleContext(BranchContext.class,0);
		}
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18); branch();
			setState(19); match(5);
			setState(20); weight();
			setState(21); match(EOF);
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

	public static class SubtreeContext extends ParserRuleContext {
		public InternalContext internal() {
			return getRuleContext(InternalContext.class,0);
		}
		public LeafContext leaf() {
			return getRuleContext(LeafContext.class,0);
		}
		public SubtreeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtree; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterSubtree(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitSubtree(this);
		}
	}

	public final SubtreeContext subtree() throws RecognitionException {
		SubtreeContext _localctx = new SubtreeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_subtree);
		try {
			setState(25);
			switch (_input.LA(1)) {
			case 1:
			case 2:
			case 4:
			case 5:
			case WORD:
				enterOuterAlt(_localctx, 1);
				{
				setState(23); leaf();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 2);
				{
				setState(24); internal();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class LeafContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public LeafContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leaf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterLeaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitLeaf(this);
		}
	}

	public final LeafContext leaf() throws RecognitionException {
		LeafContext _localctx = new LeafContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_leaf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27); name();
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

	public static class InternalContext extends ParserRuleContext {
		public BranchsetContext branchset() {
			return getRuleContext(BranchsetContext.class,0);
		}
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public InternalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_internal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterInternal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitInternal(this);
		}
	}

	public final InternalContext internal() throws RecognitionException {
		InternalContext _localctx = new InternalContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_internal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29); match(3);
			setState(30); branchset();
			setState(31); match(1);
			setState(32); name();
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

	public static class BranchsetContext extends ParserRuleContext {
		public List<BranchContext> branch() {
			return getRuleContexts(BranchContext.class);
		}
		public BranchContext branch(int i) {
			return getRuleContext(BranchContext.class,i);
		}
		public BranchsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_branchset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterBranchset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitBranchset(this);
		}
	}

	public final BranchsetContext branchset() throws RecognitionException {
		BranchsetContext _localctx = new BranchsetContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_branchset);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34); branch();
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==2) {
				{
				{
				setState(35); match(2);
				setState(36); branch();
				}
				}
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class BranchContext extends ParserRuleContext {
		public LengthContext length() {
			return getRuleContext(LengthContext.class,0);
		}
		public SubtreeContext subtree() {
			return getRuleContext(SubtreeContext.class,0);
		}
		public BranchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_branch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterBranch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitBranch(this);
		}
	}

	public final BranchContext branch() throws RecognitionException {
		BranchContext _localctx = new BranchContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_branch);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42); subtree();
			setState(43); length();
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

	public static class NameContext extends ParserRuleContext {
		public TerminalNode WORD() { return getToken(NewickTreeParser.WORD, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitName(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_name);
		try {
			setState(47);
			switch (_input.LA(1)) {
			case 1:
			case 2:
			case 4:
			case 5:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case WORD:
				enterOuterAlt(_localctx, 2);
				{
				setState(46); match(WORD);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class LengthContext extends ParserRuleContext {
		public TerminalNode REAL() { return getToken(NewickTreeParser.REAL, 0); }
		public LengthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_length; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterLength(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitLength(this);
		}
	}

	public final LengthContext length() throws RecognitionException {
		LengthContext _localctx = new LengthContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_length);
		try {
			setState(52);
			switch (_input.LA(1)) {
			case 1:
			case 2:
			case 5:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 2);
				{
				setState(50); match(4);
				setState(51); match(REAL);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class WeightContext extends ParserRuleContext {
		public TerminalNode REAL() { return getToken(NewickTreeParser.REAL, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NewickTreeListener ) ((NewickTreeListener)listener).exitWeight(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_weight);
		try {
			setState(56);
			switch (_input.LA(1)) {
			case EOF:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case REAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(55); match(REAL);
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\n=\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\3\2\3\2\3\3\3\3\5\3\34\n\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\7"+
		"\6(\n\6\f\6\16\6+\13\6\3\7\3\7\3\7\3\b\3\b\5\b\62\n\b\3\t\3\t\3\t\5\t"+
		"\67\n\t\3\n\3\n\5\n;\n\n\3\n\2\13\2\4\6\b\n\f\16\20\22\2\28\2\24\3\2\2"+
		"\2\4\33\3\2\2\2\6\35\3\2\2\2\b\37\3\2\2\2\n$\3\2\2\2\f,\3\2\2\2\16\61"+
		"\3\2\2\2\20\66\3\2\2\2\22:\3\2\2\2\24\25\5\f\7\2\25\26\7\7\2\2\26\27\5"+
		"\22\n\2\27\30\7\2\2\3\30\3\3\2\2\2\31\34\5\6\4\2\32\34\5\b\5\2\33\31\3"+
		"\2\2\2\33\32\3\2\2\2\34\5\3\2\2\2\35\36\5\16\b\2\36\7\3\2\2\2\37 \7\5"+
		"\2\2 !\5\n\6\2!\"\7\3\2\2\"#\5\16\b\2#\t\3\2\2\2$)\5\f\7\2%&\7\4\2\2&"+
		"(\5\f\7\2\'%\3\2\2\2(+\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*\13\3\2\2\2+)\3\2"+
		"\2\2,-\5\4\3\2-.\5\20\t\2.\r\3\2\2\2/\62\3\2\2\2\60\62\7\t\2\2\61/\3\2"+
		"\2\2\61\60\3\2\2\2\62\17\3\2\2\2\63\67\3\2\2\2\64\65\7\6\2\2\65\67\7\b"+
		"\2\2\66\63\3\2\2\2\66\64\3\2\2\2\67\21\3\2\2\28;\3\2\2\29;\7\b\2\2:8\3"+
		"\2\2\2:9\3\2\2\2;\23\3\2\2\2\7\33)\61\66:";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}