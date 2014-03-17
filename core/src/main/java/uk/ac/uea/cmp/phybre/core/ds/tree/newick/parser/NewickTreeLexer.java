// Generated from uk/ac/uea/cmp/phybre/core/ds/tree/newick/parser/NewickTree.g4 by ANTLR 4.1
package uk.ac.uea.cmp.phybre.core.ds.tree.newick.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NewickTreeLexer extends Lexer {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__5 = 1, T__4 = 2, T__3 = 3, T__2 = 4, T__1 = 5, T__0 = 6, WORD = 7, LENGTH = 8, WEIGHT = 9,
            WS = 10;
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] tokenNames = {
            "<INVALID>",
            "'''", "')'", "','", "'\"'", "'('", "';'", "WORD", "LENGTH", "WEIGHT",
            "WS"
    };
    public static final String[] ruleNames = {
            "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "WORD", "LENGTH", "WEIGHT",
            "DIGIT", "LETTER", "OTHER", "WS"
    };


    public NewickTreeLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

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
            case 12:
                WS_action((RuleContext) _localctx, actionIndex);
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

    public static final String _serializedATN =
            "\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\f\u0081\b\1\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3" +
                    "\6\3\7\3\7\3\b\3\b\3\b\6\b-\n\b\r\b\16\b.\3\t\3\t\6\t\63\n\t\r\t\16\t" +
                    "\64\3\t\3\t\7\t9\n\t\f\t\16\t<\13\t\3\t\3\t\7\t@\n\t\f\t\16\tC\13\t\3" +
                    "\t\3\t\6\tG\n\t\r\t\16\tH\3\t\3\t\6\tM\n\t\r\t\16\tN\5\tQ\n\t\3\n\3\n" +
                    "\6\nU\n\n\r\n\16\nV\3\n\3\n\6\n[\n\n\r\n\16\n\\\3\n\3\n\7\na\n\n\f\n\16" +
                    "\nd\13\n\3\n\3\n\7\nh\n\n\f\n\16\nk\13\n\3\n\3\n\6\no\n\n\r\n\16\np\5" +
                    "\ns\n\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\6\16|\n\16\r\16\16\16}\3\16\3\16" +
                    "\2\17\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\2" +
                    "\1\27\2\1\31\2\1\33\f\2\3\2\6\3\2\62;\4\2C\\c|\4\2%%\u0080\u0080\5\2\13" +
                    "\f\17\17\"\"\u008f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13" +
                    "\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\33\3\2\2" +
                    "\2\3\35\3\2\2\2\5\37\3\2\2\2\7!\3\2\2\2\t#\3\2\2\2\13%\3\2\2\2\r\'\3\2" +
                    "\2\2\17,\3\2\2\2\21P\3\2\2\2\23r\3\2\2\2\25t\3\2\2\2\27v\3\2\2\2\31x\3" +
                    "\2\2\2\33{\3\2\2\2\35\36\7)\2\2\36\4\3\2\2\2\37 \7+\2\2 \6\3\2\2\2!\"" +
                    "\7.\2\2\"\b\3\2\2\2#$\7$\2\2$\n\3\2\2\2%&\7*\2\2&\f\3\2\2\2\'(\7=\2\2" +
                    "(\16\3\2\2\2)-\5\27\f\2*-\5\31\r\2+-\5\25\13\2,)\3\2\2\2,*\3\2\2\2,+\3" +
                    "\2\2\2-.\3\2\2\2.,\3\2\2\2./\3\2\2\2/\20\3\2\2\2\60\62\7<\2\2\61\63\5" +
                    "\25\13\2\62\61\3\2\2\2\63\64\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\65\66" +
                    "\3\2\2\2\66:\7\60\2\2\679\5\25\13\28\67\3\2\2\29<\3\2\2\2:8\3\2\2\2:;" +
                    "\3\2\2\2;Q\3\2\2\2<:\3\2\2\2=A\7<\2\2>@\5\25\13\2?>\3\2\2\2@C\3\2\2\2" +
                    "A?\3\2\2\2AB\3\2\2\2BD\3\2\2\2CA\3\2\2\2DF\7\60\2\2EG\5\25\13\2FE\3\2" +
                    "\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2IQ\3\2\2\2JL\7<\2\2KM\5\25\13\2LK\3" +
                    "\2\2\2MN\3\2\2\2NL\3\2\2\2NO\3\2\2\2OQ\3\2\2\2P\60\3\2\2\2P=\3\2\2\2P" +
                    "J\3\2\2\2Q\22\3\2\2\2RT\7=\2\2SU\5\25\13\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2" +
                    "\2VW\3\2\2\2WX\3\2\2\2XZ\7\60\2\2Y[\5\25\13\2ZY\3\2\2\2[\\\3\2\2\2\\Z" +
                    "\3\2\2\2\\]\3\2\2\2]s\3\2\2\2^b\7=\2\2_a\5\25\13\2`_\3\2\2\2ad\3\2\2\2" +
                    "b`\3\2\2\2bc\3\2\2\2ce\3\2\2\2db\3\2\2\2ei\7\60\2\2fh\5\25\13\2gf\3\2" +
                    "\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2\2\2js\3\2\2\2ki\3\2\2\2ln\7=\2\2mo\5\25" +
                    "\13\2nm\3\2\2\2op\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rR\3\2\2\2r^\3" +
                    "\2\2\2rl\3\2\2\2s\24\3\2\2\2tu\t\2\2\2u\26\3\2\2\2vw\t\3\2\2w\30\3\2\2" +
                    "\2xy\t\4\2\2y\32\3\2\2\2z|\t\5\2\2{z\3\2\2\2|}\3\2\2\2}{\3\2\2\2}~\3\2" +
                    "\2\2~\177\3\2\2\2\177\u0080\b\16\2\2\u0080\34\3\2\2\2\22\2,.\64:AHNPV" +
                    "\\bipr}";
    public static final ATN _ATN =
            ATNSimulator.deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}