// Generated from uk/ac/uea/cmp/phybre/core/io/qweight/parser/QWeight.g4 by ANTLR 4.1
package uk.ac.uea.cmp.phybre.core.io.qweight.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QWeightParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__10=1, T__9=2, T__8=3, T__7=4, T__6=5, T__5=6, T__4=7, T__3=8, T__2=9, 
		T__1=10, T__0=11, NUMERIC=12, IDENTIFIER=13, LETTER_US=14, WS=15, NL=16;
	public static final String[] tokenNames = {
		"<INVALID>", "'max'", "'description'", "'min'", "'name'", "'quartet'", 
		"':'", "'sense'", "'weights'", "'taxon'", "';'", "'taxanumber'", "NUMERIC", 
		"IDENTIFIER", "LETTER_US", "WS", "NL"
	};
	public static final int
		RULE_parse = 0, RULE_nbtaxa = 1, RULE_description = 2, RULE_words = 3, 
		RULE_sense = 4, RULE_sense_option = 5, RULE_taxa = 6, RULE_taxon = 7, 
		RULE_quartets = 8, RULE_quartet = 9, RULE_x = 10, RULE_y = 11, RULE_u = 12, 
		RULE_v = 13, RULE_w1 = 14, RULE_w2 = 15, RULE_w3 = 16;
	public static final String[] ruleNames = {
		"parse", "nbtaxa", "description", "words", "sense", "sense_option", "taxa", 
		"taxon", "quartets", "quartet", "x", "y", "u", "v", "w1", "w2", "w3"
	};

	@Override
	public String getGrammarFileName() { return "QWeight.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public QWeightParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(QWeightParser.EOF, 0); }
		public NbtaxaContext nbtaxa() {
			return getRuleContext(NbtaxaContext.class,0);
		}
		public QuartetsContext quartets() {
			return getRuleContext(QuartetsContext.class,0);
		}
		public SenseContext sense() {
			return getRuleContext(SenseContext.class,0);
		}
		public DescriptionContext description() {
			return getRuleContext(DescriptionContext.class,0);
		}
		public TaxaContext taxa() {
			return getRuleContext(TaxaContext.class,0);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitParse(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34); nbtaxa();
			setState(35); description();
			setState(36); sense();
			setState(37); taxa();
			setState(38); quartets();
			setState(39); match(EOF);
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

	public static class NbtaxaContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public NbtaxaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nbtaxa; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterNbtaxa(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitNbtaxa(this);
		}
	}

	public final NbtaxaContext nbtaxa() throws RecognitionException {
		NbtaxaContext _localctx = new NbtaxaContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_nbtaxa);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41); match(11);
			setState(42); match(6);
			setState(43); match(NUMERIC);
			setState(44); match(10);
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

	public static class DescriptionContext extends ParserRuleContext {
		public WordsContext words() {
			return getRuleContext(WordsContext.class,0);
		}
		public DescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_description; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitDescription(this);
		}
	}

	public final DescriptionContext description() throws RecognitionException {
		DescriptionContext _localctx = new DescriptionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_description);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46); match(2);
			setState(47); match(6);
			setState(48); words();
			setState(49); match(10);
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

	public static class WordsContext extends ParserRuleContext {
		public WordsContext words() {
			return getRuleContext(WordsContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(QWeightParser.IDENTIFIER, 0); }
		public WordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_words; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterWords(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitWords(this);
		}
	}

	public final WordsContext words() throws RecognitionException {
		WordsContext _localctx = new WordsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_words);
		try {
			setState(54);
			switch (_input.LA(1)) {
			case 10:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(52); match(IDENTIFIER);
				setState(53); words();
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

	public static class SenseContext extends ParserRuleContext {
		public Sense_optionContext sense_option() {
			return getRuleContext(Sense_optionContext.class,0);
		}
		public SenseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sense; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterSense(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitSense(this);
		}
	}

	public final SenseContext sense() throws RecognitionException {
		SenseContext _localctx = new SenseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_sense);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56); match(7);
			setState(57); match(6);
			setState(58); sense_option();
			setState(59); match(10);
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

	public static class Sense_optionContext extends ParserRuleContext {
		public Sense_optionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sense_option; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterSense_option(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitSense_option(this);
		}
	}

	public final Sense_optionContext sense_option() throws RecognitionException {
		Sense_optionContext _localctx = new Sense_optionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_sense_option);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			_la = _input.LA(1);
			if ( !(_la==1 || _la==3) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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

	public static class TaxaContext extends ParserRuleContext {
		public TaxonContext taxon() {
			return getRuleContext(TaxonContext.class,0);
		}
		public TaxaContext taxa() {
			return getRuleContext(TaxaContext.class,0);
		}
		public TaxaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_taxa; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterTaxa(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitTaxa(this);
		}
	}

	public final TaxaContext taxa() throws RecognitionException {
		TaxaContext _localctx = new TaxaContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_taxa);
		try {
			setState(67);
			switch (_input.LA(1)) {
			case EOF:
			case 5:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 2);
				{
				setState(64); taxon();
				setState(65); taxa();
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

	public static class TaxonContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public TerminalNode IDENTIFIER() { return getToken(QWeightParser.IDENTIFIER, 0); }
		public TaxonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_taxon; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterTaxon(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitTaxon(this);
		}
	}

	public final TaxonContext taxon() throws RecognitionException {
		TaxonContext _localctx = new TaxonContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_taxon);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69); match(9);
			setState(70); match(6);
			setState(71); match(NUMERIC);
			setState(72); match(4);
			setState(73); match(6);
			setState(74); match(IDENTIFIER);
			setState(75); match(10);
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

	public static class QuartetsContext extends ParserRuleContext {
		public QuartetContext quartet() {
			return getRuleContext(QuartetContext.class,0);
		}
		public QuartetsContext quartets() {
			return getRuleContext(QuartetsContext.class,0);
		}
		public QuartetsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quartets; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterQuartets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitQuartets(this);
		}
	}

	public final QuartetsContext quartets() throws RecognitionException {
		QuartetsContext _localctx = new QuartetsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_quartets);
		try {
			setState(81);
			switch (_input.LA(1)) {
			case EOF:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 2);
				{
				setState(78); quartet();
				setState(79); quartets();
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

	public static class QuartetContext extends ParserRuleContext {
		public W1Context w1() {
			return getRuleContext(W1Context.class,0);
		}
		public W2Context w2() {
			return getRuleContext(W2Context.class,0);
		}
		public XContext x() {
			return getRuleContext(XContext.class,0);
		}
		public YContext y() {
			return getRuleContext(YContext.class,0);
		}
		public W3Context w3() {
			return getRuleContext(W3Context.class,0);
		}
		public VContext v() {
			return getRuleContext(VContext.class,0);
		}
		public UContext u() {
			return getRuleContext(UContext.class,0);
		}
		public QuartetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quartet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterQuartet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitQuartet(this);
		}
	}

	public final QuartetContext quartet() throws RecognitionException {
		QuartetContext _localctx = new QuartetContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_quartet);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83); match(5);
			setState(84); match(6);
			setState(85); x();
			setState(86); y();
			setState(87); u();
			setState(88); v();
			setState(89); match(8);
			setState(90); match(6);
			setState(91); w1();
			setState(92); w2();
			setState(93); w3();
			setState(94); match(10);
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

	public static class XContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public XContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_x; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitX(this);
		}
	}

	public final XContext x() throws RecognitionException {
		XContext _localctx = new XContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_x);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96); match(NUMERIC);
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

	public static class YContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public YContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_y; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterY(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitY(this);
		}
	}

	public final YContext y() throws RecognitionException {
		YContext _localctx = new YContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_y);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98); match(NUMERIC);
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

	public static class UContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public UContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterU(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitU(this);
		}
	}

	public final UContext u() throws RecognitionException {
		UContext _localctx = new UContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_u);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100); match(NUMERIC);
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

	public static class VContext extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public VContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_v; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterV(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitV(this);
		}
	}

	public final VContext v() throws RecognitionException {
		VContext _localctx = new VContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_v);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102); match(NUMERIC);
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

	public static class W1Context extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public W1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_w1; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterW1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitW1(this);
		}
	}

	public final W1Context w1() throws RecognitionException {
		W1Context _localctx = new W1Context(_ctx, getState());
		enterRule(_localctx, 28, RULE_w1);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104); match(NUMERIC);
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

	public static class W2Context extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public W2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_w2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterW2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitW2(this);
		}
	}

	public final W2Context w2() throws RecognitionException {
		W2Context _localctx = new W2Context(_ctx, getState());
		enterRule(_localctx, 30, RULE_w2);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106); match(NUMERIC);
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

	public static class W3Context extends ParserRuleContext {
		public TerminalNode NUMERIC() { return getToken(QWeightParser.NUMERIC, 0); }
		public W3Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_w3; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).enterW3(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QWeightListener ) ((QWeightListener)listener).exitW3(this);
		}
	}

	public final W3Context w3() throws RecognitionException {
		W3Context _localctx = new W3Context(_ctx, getState());
		enterRule(_localctx, 32, RULE_w3);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108); match(NUMERIC);
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\22q\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5"+
		"\3\5\5\59\n\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\5\bF\n\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\5\nT\n\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\2\23\2\4\6\b\n\f\16"+
		"\20\22\24\26\30\32\34\36 \"\2\3\4\2\3\3\5\5b\2$\3\2\2\2\4+\3\2\2\2\6\60"+
		"\3\2\2\2\b8\3\2\2\2\n:\3\2\2\2\f?\3\2\2\2\16E\3\2\2\2\20G\3\2\2\2\22S"+
		"\3\2\2\2\24U\3\2\2\2\26b\3\2\2\2\30d\3\2\2\2\32f\3\2\2\2\34h\3\2\2\2\36"+
		"j\3\2\2\2 l\3\2\2\2\"n\3\2\2\2$%\5\4\3\2%&\5\6\4\2&\'\5\n\6\2\'(\5\16"+
		"\b\2()\5\22\n\2)*\7\2\2\3*\3\3\2\2\2+,\7\r\2\2,-\7\b\2\2-.\7\16\2\2./"+
		"\7\f\2\2/\5\3\2\2\2\60\61\7\4\2\2\61\62\7\b\2\2\62\63\5\b\5\2\63\64\7"+
		"\f\2\2\64\7\3\2\2\2\659\3\2\2\2\66\67\7\17\2\2\679\5\b\5\28\65\3\2\2\2"+
		"8\66\3\2\2\29\t\3\2\2\2:;\7\t\2\2;<\7\b\2\2<=\5\f\7\2=>\7\f\2\2>\13\3"+
		"\2\2\2?@\t\2\2\2@\r\3\2\2\2AF\3\2\2\2BC\5\20\t\2CD\5\16\b\2DF\3\2\2\2"+
		"EA\3\2\2\2EB\3\2\2\2F\17\3\2\2\2GH\7\13\2\2HI\7\b\2\2IJ\7\16\2\2JK\7\6"+
		"\2\2KL\7\b\2\2LM\7\17\2\2MN\7\f\2\2N\21\3\2\2\2OT\3\2\2\2PQ\5\24\13\2"+
		"QR\5\22\n\2RT\3\2\2\2SO\3\2\2\2SP\3\2\2\2T\23\3\2\2\2UV\7\7\2\2VW\7\b"+
		"\2\2WX\5\26\f\2XY\5\30\r\2YZ\5\32\16\2Z[\5\34\17\2[\\\7\n\2\2\\]\7\b\2"+
		"\2]^\5\36\20\2^_\5 \21\2_`\5\"\22\2`a\7\f\2\2a\25\3\2\2\2bc\7\16\2\2c"+
		"\27\3\2\2\2de\7\16\2\2e\31\3\2\2\2fg\7\16\2\2g\33\3\2\2\2hi\7\16\2\2i"+
		"\35\3\2\2\2jk\7\16\2\2k\37\3\2\2\2lm\7\16\2\2m!\3\2\2\2no\7\16\2\2o#\3"+
		"\2\2\2\58ES";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}