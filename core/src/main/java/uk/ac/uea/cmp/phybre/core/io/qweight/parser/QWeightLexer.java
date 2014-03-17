// Generated from uk/ac/uea/cmp/phybre/core/io/qweight/parser/QWeight.g4 by ANTLR 4.1
package uk.ac.uea.cmp.phybre.core.io.qweight.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QWeightLexer extends Lexer {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__10 = 1, T__9 = 2, T__8 = 3, T__7 = 4, T__6 = 5, T__5 = 6, T__4 = 7, T__3 = 8, T__2 = 9,
            T__1 = 10, T__0 = 11, NUMERIC = 12, IDENTIFIER = 13, LETTER_US = 14, WS = 15, NL = 16;
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] tokenNames = {
            "<INVALID>",
            "'max'", "'description'", "'min'", "'name'", "'quartet'", "':'", "'sense'",
            "'weights'", "'taxon'", "';'", "'taxanumber'", "NUMERIC", "IDENTIFIER",
            "LETTER_US", "WS", "NL"
    };
    public static final String[] ruleNames = {
            "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2",
            "T__1", "T__0", "NUMERIC", "IDENTIFIER", "DIGIT", "NZ_DIGIT", "LETTER",
            "LETTER_US", "WS", "NL"
    };


    public QWeightLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "QWeight.g4";
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
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    @Override
    public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
        switch (ruleIndex) {
            case 17:
                WS_action((RuleContext) _localctx, actionIndex);
                break;

            case 18:
                NL_action((RuleContext) _localctx, actionIndex);
                break;
        }
    }

    private void WS_action(RuleContext _localctx, int actionIndex) {
        switch (actionIndex) {
            case 0:
                skip();
                break;
        }
    }

    private void NL_action(RuleContext _localctx, int actionIndex) {
        switch (actionIndex) {
            case 1:
                skip();
                break;
        }
    }

    public static final String _serializedATN =
            "\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\22\u00af\b\1\4\2" +
                    "\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4" +
                    "\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22" +
                    "\t\22\4\23\t\23\4\24\t\24\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3" +
                    "\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3" +
                    "\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t" +
                    "\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f" +
                    "\3\f\3\f\3\f\3\f\3\f\3\r\5\ro\n\r\3\r\6\rr\n\r\r\r\16\rs\3\r\3\r\7\rx" +
                    "\n\r\f\r\16\r{\13\r\3\r\5\r~\n\r\3\r\7\r\u0081\n\r\f\r\16\r\u0084\13\r" +
                    "\3\r\3\r\6\r\u0088\n\r\r\r\16\r\u0089\3\r\5\r\u008d\n\r\3\r\6\r\u0090" +
                    "\n\r\r\r\16\r\u0091\5\r\u0094\n\r\3\16\3\16\3\16\6\16\u0099\n\16\r\16" +
                    "\16\16\u009a\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\6\23\u00a6\n" +
                    "\23\r\23\16\23\u00a7\3\23\3\23\3\24\3\24\3\24\3\24\2\25\3\3\1\5\4\1\7" +
                    "\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33" +
                    "\17\1\35\2\1\37\2\1!\2\1#\20\1%\21\2\'\22\3\3\2\b\3\2\62;\3\2\63;\4\2" +
                    "C\\c|\5\2C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\u00b9\2\3\3\2\2\2\2\5\3\2" +
                    "\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21" +
                    "\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2" +
                    "\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\3)\3\2\2\2\5-\3\2\2\2\79\3\2\2" +
                    "\2\t=\3\2\2\2\13B\3\2\2\2\rJ\3\2\2\2\17L\3\2\2\2\21R\3\2\2\2\23Z\3\2\2" +
                    "\2\25`\3\2\2\2\27b\3\2\2\2\31\u0093\3\2\2\2\33\u0098\3\2\2\2\35\u009c" +
                    "\3\2\2\2\37\u009e\3\2\2\2!\u00a0\3\2\2\2#\u00a2\3\2\2\2%\u00a5\3\2\2\2" +
                    "\'\u00ab\3\2\2\2)*\7o\2\2*+\7c\2\2+,\7z\2\2,\4\3\2\2\2-.\7f\2\2./\7g\2" +
                    "\2/\60\7u\2\2\60\61\7e\2\2\61\62\7t\2\2\62\63\7k\2\2\63\64\7r\2\2\64\65" +
                    "\7v\2\2\65\66\7k\2\2\66\67\7q\2\2\678\7p\2\28\6\3\2\2\29:\7o\2\2:;\7k" +
                    "\2\2;<\7p\2\2<\b\3\2\2\2=>\7p\2\2>?\7c\2\2?@\7o\2\2@A\7g\2\2A\n\3\2\2" +
                    "\2BC\7s\2\2CD\7w\2\2DE\7c\2\2EF\7t\2\2FG\7v\2\2GH\7g\2\2HI\7v\2\2I\f\3" +
                    "\2\2\2JK\7<\2\2K\16\3\2\2\2LM\7u\2\2MN\7g\2\2NO\7p\2\2OP\7u\2\2PQ\7g\2" +
                    "\2Q\20\3\2\2\2RS\7y\2\2ST\7g\2\2TU\7k\2\2UV\7i\2\2VW\7j\2\2WX\7v\2\2X" +
                    "Y\7u\2\2Y\22\3\2\2\2Z[\7v\2\2[\\\7c\2\2\\]\7z\2\2]^\7q\2\2^_\7p\2\2_\24" +
                    "\3\2\2\2`a\7=\2\2a\26\3\2\2\2bc\7v\2\2cd\7c\2\2de\7z\2\2ef\7c\2\2fg\7" +
                    "p\2\2gh\7w\2\2hi\7o\2\2ij\7d\2\2jk\7g\2\2kl\7t\2\2l\30\3\2\2\2mo\7/\2" +
                    "\2nm\3\2\2\2no\3\2\2\2oq\3\2\2\2pr\5\35\17\2qp\3\2\2\2rs\3\2\2\2sq\3\2" +
                    "\2\2st\3\2\2\2tu\3\2\2\2uy\7\60\2\2vx\5\35\17\2wv\3\2\2\2x{\3\2\2\2yw" +
                    "\3\2\2\2yz\3\2\2\2z\u0094\3\2\2\2{y\3\2\2\2|~\7/\2\2}|\3\2\2\2}~\3\2\2" +
                    "\2~\u0082\3\2\2\2\177\u0081\5\35\17\2\u0080\177\3\2\2\2\u0081\u0084\3" +
                    "\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2\2\2\u0084" +
                    "\u0082\3\2\2\2\u0085\u0087\7\60\2\2\u0086\u0088\5\35\17\2\u0087\u0086" +
                    "\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a" +
                    "\u0094\3\2\2\2\u008b\u008d\7/\2\2\u008c\u008b\3\2\2\2\u008c\u008d\3\2" +
                    "\2\2\u008d\u008f\3\2\2\2\u008e\u0090\5\35\17\2\u008f\u008e\3\2\2\2\u0090" +
                    "\u0091\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0094\3\2" +
                    "\2\2\u0093n\3\2\2\2\u0093}\3\2\2\2\u0093\u008c\3\2\2\2\u0094\32\3\2\2" +
                    "\2\u0095\u0099\5#\22\2\u0096\u0099\5\35\17\2\u0097\u0099\4/\60\2\u0098" +
                    "\u0095\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0097\3\2\2\2\u0099\u009a\3\2" +
                    "\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\34\3\2\2\2\u009c\u009d" +
                    "\t\2\2\2\u009d\36\3\2\2\2\u009e\u009f\t\3\2\2\u009f \3\2\2\2\u00a0\u00a1" +
                    "\t\4\2\2\u00a1\"\3\2\2\2\u00a2\u00a3\t\5\2\2\u00a3$\3\2\2\2\u00a4\u00a6" +
                    "\t\6\2\2\u00a5\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7" +
                    "\u00a8\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00aa\b\23\2\2\u00aa&\3\2\2\2" +
                    "\u00ab\u00ac\t\7\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ae\b\24\3\2\u00ae(\3" +
                    "\2\2\2\17\2nsy}\u0082\u0089\u008c\u0091\u0093\u0098\u009a\u00a7";
    public static final ATN _ATN =
            ATNSimulator.deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}