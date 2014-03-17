// Generated from uk/ac/uea/cmp/phybre/core/ds/tree/newick/parser/NewickTree.g4 by ANTLR 4.1
package uk.ac.uea.cmp.phybre.core.ds.tree.newick.parser;

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
            T__5 = 1, T__4 = 2, T__3 = 3, T__2 = 4, T__1 = 5, T__0 = 6, WORD = 7, LENGTH = 8, WEIGHT = 9,
            WS = 10;
    public static final String[] tokenNames = {
            "<INVALID>", "'''", "')'", "','", "'\"'", "'('", "';'", "WORD", "LENGTH",
            "WEIGHT", "WS"
    };
    public static final int
            RULE_parse = 0, RULE_subtree = 1, RULE_leaf = 2, RULE_internal = 3, RULE_branchset = 4,
            RULE_branch = 5, RULE_name = 6, RULE_length = 7, RULE_weight = 8;
    public static final String[] ruleNames = {
            "parse", "subtree", "leaf", "internal", "branchset", "branch", "name",
            "length", "weight"
    };

    @Override
    public String getGrammarFileName() {
        return "NewickTree.g4";
    }

    @Override
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public NewickTreeParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class ParseContext extends ParserRuleContext {
        public TerminalNode EOF() {
            return getToken(NewickTreeParser.EOF, 0);
        }

        public BranchContext branch() {
            return getRuleContext(BranchContext.class, 0);
        }

        public WeightContext weight() {
            return getRuleContext(WeightContext.class, 0);
        }

        public ParseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_parse;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterParse(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitParse(this);
        }
    }

    public final ParseContext parse() throws RecognitionException {
        ParseContext _localctx = new ParseContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_parse);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                branch();
                setState(19);
                weight();
                setState(20);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class SubtreeContext extends ParserRuleContext {
        public InternalContext internal() {
            return getRuleContext(InternalContext.class, 0);
        }

        public LeafContext leaf() {
            return getRuleContext(LeafContext.class, 0);
        }

        public SubtreeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_subtree;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterSubtree(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitSubtree(this);
        }
    }

    public final SubtreeContext subtree() throws RecognitionException {
        SubtreeContext _localctx = new SubtreeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_subtree);
        try {
            setState(24);
            switch (_input.LA(1)) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 6:
                case WORD:
                case LENGTH:
                case WEIGHT:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(22);
                    leaf();
                }
                break;
                case 5:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(23);
                    internal();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class LeafContext extends ParserRuleContext {
        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public LeafContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_leaf;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterLeaf(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitLeaf(this);
        }
    }

    public final LeafContext leaf() throws RecognitionException {
        LeafContext _localctx = new LeafContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_leaf);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                name();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class InternalContext extends ParserRuleContext {
        public BranchsetContext branchset() {
            return getRuleContext(BranchsetContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public InternalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_internal;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterInternal(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitInternal(this);
        }
    }

    public final InternalContext internal() throws RecognitionException {
        InternalContext _localctx = new InternalContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_internal);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(5);
                setState(29);
                branchset();
                setState(30);
                match(2);
                setState(31);
                name();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class BranchsetContext extends ParserRuleContext {
        public List<BranchContext> branch() {
            return getRuleContexts(BranchContext.class);
        }

        public BranchContext branch(int i) {
            return getRuleContext(BranchContext.class, i);
        }

        public BranchsetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_branchset;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterBranchset(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitBranchset(this);
        }
    }

    public final BranchsetContext branchset() throws RecognitionException {
        BranchsetContext _localctx = new BranchsetContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_branchset);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(33);
                branch();
                setState(38);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == 3) {
                    {
                        {
                            setState(34);
                            match(3);
                            setState(35);
                            branch();
                        }
                    }
                    setState(40);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class BranchContext extends ParserRuleContext {
        public LengthContext length() {
            return getRuleContext(LengthContext.class, 0);
        }

        public SubtreeContext subtree() {
            return getRuleContext(SubtreeContext.class, 0);
        }

        public BranchContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_branch;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterBranch(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitBranch(this);
        }
    }

    public final BranchContext branch() throws RecognitionException {
        BranchContext _localctx = new BranchContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_branch);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                subtree();
                setState(42);
                length();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class NameContext extends ParserRuleContext {
        public TerminalNode WORD() {
            return getToken(NewickTreeParser.WORD, 0);
        }

        public NameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitName(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_name);
        try {
            setState(52);
            switch (_input.LA(1)) {
                case 2:
                case 3:
                case 6:
                case LENGTH:
                case WEIGHT:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 1:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(45);
                    match(1);
                    setState(46);
                    match(WORD);
                    setState(47);
                    match(1);
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(48);
                    match(4);
                    setState(49);
                    match(WORD);
                    setState(50);
                    match(4);
                }
                break;
                case WORD:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(51);
                    match(WORD);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class LengthContext extends ParserRuleContext {
        public TerminalNode LENGTH() {
            return getToken(NewickTreeParser.LENGTH, 0);
        }

        public LengthContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_length;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterLength(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitLength(this);
        }
    }

    public final LengthContext length() throws RecognitionException {
        LengthContext _localctx = new LengthContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_length);
        try {
            setState(56);
            switch (_input.LA(1)) {
                case 2:
                case 3:
                case 6:
                case WEIGHT:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case LENGTH:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(55);
                    match(LENGTH);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class WeightContext extends ParserRuleContext {
        public TerminalNode WEIGHT() {
            return getToken(NewickTreeParser.WEIGHT, 0);
        }

        public WeightContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weight;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).enterWeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NewickTreeListener) ((NewickTreeListener) listener).exitWeight(this);
        }
    }

    public final WeightContext weight() throws RecognitionException {
        WeightContext _localctx = new WeightContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_weight);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                _la = _input.LA(1);
                if (!(_la == 6 || _la == WEIGHT)) {
                    _errHandler.recoverInline(this);
                }
                consume();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN =
            "\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\f?\4\2\t\2\4\3\t" +
                    "\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2" +
                    "\3\2\3\3\3\3\5\3\33\n\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\7\6\'" +
                    "\n\6\f\6\16\6*\13\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\67" +
                    "\n\b\3\t\3\t\5\t;\n\t\3\n\3\n\3\n\2\13\2\4\6\b\n\f\16\20\22\2\3\4\2\b" +
                    "\b\13\13;\2\24\3\2\2\2\4\32\3\2\2\2\6\34\3\2\2\2\b\36\3\2\2\2\n#\3\2\2" +
                    "\2\f+\3\2\2\2\16\66\3\2\2\2\20:\3\2\2\2\22<\3\2\2\2\24\25\5\f\7\2\25\26" +
                    "\5\22\n\2\26\27\7\2\2\3\27\3\3\2\2\2\30\33\5\6\4\2\31\33\5\b\5\2\32\30" +
                    "\3\2\2\2\32\31\3\2\2\2\33\5\3\2\2\2\34\35\5\16\b\2\35\7\3\2\2\2\36\37" +
                    "\7\7\2\2\37 \5\n\6\2 !\7\4\2\2!\"\5\16\b\2\"\t\3\2\2\2#(\5\f\7\2$%\7\5" +
                    "\2\2%\'\5\f\7\2&$\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2\2\2)\13\3\2\2\2*" +
                    "(\3\2\2\2+,\5\4\3\2,-\5\20\t\2-\r\3\2\2\2.\67\3\2\2\2/\60\7\3\2\2\60\61" +
                    "\7\t\2\2\61\67\7\3\2\2\62\63\7\6\2\2\63\64\7\t\2\2\64\67\7\6\2\2\65\67" +
                    "\7\t\2\2\66.\3\2\2\2\66/\3\2\2\2\66\62\3\2\2\2\66\65\3\2\2\2\67\17\3\2" +
                    "\2\28;\3\2\2\29;\7\n\2\2:8\3\2\2\2:9\3\2\2\2;\21\3\2\2\2<=\t\2\2\2=\23" +
                    "\3\2\2\2\6\32(\66:";
    public static final ATN _ATN =
            ATNSimulator.deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}