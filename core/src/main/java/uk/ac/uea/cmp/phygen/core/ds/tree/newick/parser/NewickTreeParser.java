// Generated from NewickTree.g4 by ANTLR 4.1

package uk.ac.uea.cmp.phygen.core.ds.tree.newick.parser;

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
		T__3=1, T__2=2, T__1=3, T__0=4, INT=5, REAL=6, NAME=7, WS=8, LENGTH=9;
	public static final String[] tokenNames = {
		"<INVALID>", "')'", "','", "'('", "';'", "INT", "REAL", "NAME", "WS", 
		"LENGTH"
	};
	public static final int
		RULE_parse = 0, RULE_subtree = 1, RULE_leaf = 2, RULE_internal = 3, RULE_branchset = 4, 
		RULE_branch = 5;
	public static final String[] ruleNames = {
		"parse", "subtree", "leaf", "internal", "branchset", "branch"
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
		public TerminalNode REAL() { return getToken(NewickTreeParser.REAL, 0); }
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
			setState(12); branch();
			setState(13); match(4);
			setState(15);
			_la = _input.LA(1);
			if (_la==REAL) {
				{
				setState(14); match(REAL);
				}
			}

			setState(17); match(EOF);
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
			setState(21);
			switch (_input.LA(1)) {
			case 1:
			case 2:
			case 4:
			case NAME:
			case LENGTH:
				enterOuterAlt(_localctx, 1);
				{
				setState(19); leaf();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 2);
				{
				setState(20); internal();
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
		public TerminalNode NAME() { return getToken(NewickTreeParser.NAME, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(23); match(NAME);
				}
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

	public static class InternalContext extends ParserRuleContext {
		public BranchsetContext branchset() {
			return getRuleContext(BranchsetContext.class,0);
		}
		public TerminalNode NAME() { return getToken(NewickTreeParser.NAME, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26); match(3);
			setState(27); branchset();
			setState(28); match(1);
			setState(30);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(29); match(NAME);
				}
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
			setState(32); branch();
			setState(37);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==2) {
				{
				{
				setState(33); match(2);
				setState(34); branch();
				}
				}
				setState(39);
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
		public SubtreeContext subtree() {
			return getRuleContext(SubtreeContext.class,0);
		}
		public TerminalNode LENGTH() { return getToken(NewickTreeParser.LENGTH, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40); subtree();
			setState(42);
			_la = _input.LA(1);
			if (_la==LENGTH) {
				{
				setState(41); match(LENGTH);
				}
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

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\13/\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\5\2\22\n\2\3\2\3\2\3\3"+
		"\3\3\5\3\30\n\3\3\4\5\4\33\n\4\3\5\3\5\3\5\3\5\5\5!\n\5\3\6\3\6\3\6\7"+
		"\6&\n\6\f\6\16\6)\13\6\3\7\3\7\5\7-\n\7\3\7\2\b\2\4\6\b\n\f\2\2.\2\16"+
		"\3\2\2\2\4\27\3\2\2\2\6\32\3\2\2\2\b\34\3\2\2\2\n\"\3\2\2\2\f*\3\2\2\2"+
		"\16\17\5\f\7\2\17\21\7\6\2\2\20\22\7\b\2\2\21\20\3\2\2\2\21\22\3\2\2\2"+
		"\22\23\3\2\2\2\23\24\7\2\2\3\24\3\3\2\2\2\25\30\5\6\4\2\26\30\5\b\5\2"+
		"\27\25\3\2\2\2\27\26\3\2\2\2\30\5\3\2\2\2\31\33\7\t\2\2\32\31\3\2\2\2"+
		"\32\33\3\2\2\2\33\7\3\2\2\2\34\35\7\5\2\2\35\36\5\n\6\2\36 \7\3\2\2\37"+
		"!\7\t\2\2 \37\3\2\2\2 !\3\2\2\2!\t\3\2\2\2\"\'\5\f\7\2#$\7\4\2\2$&\5\f"+
		"\7\2%#\3\2\2\2&)\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2(\13\3\2\2\2)\'\3\2\2\2"+
		"*,\5\4\3\2+-\7\13\2\2,+\3\2\2\2,-\3\2\2\2-\r\3\2\2\2\b\21\27\32 \',";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}