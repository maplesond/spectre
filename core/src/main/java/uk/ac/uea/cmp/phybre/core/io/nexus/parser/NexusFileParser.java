// Generated from uk/ac/uea/cmp/phybre/core/io/nexus/parser/NexusFile.g4 by ANTLR 4.1
package uk.ac.uea.cmp.phybre.core.io.nexus.parser;

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
public class NexusFileParser extends Parser {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__121 = 1, T__120 = 2, T__119 = 3, T__118 = 4, T__117 = 5, T__116 = 6, T__115 = 7,
            T__114 = 8, T__113 = 9, T__112 = 10, T__111 = 11, T__110 = 12, T__109 = 13, T__108 = 14,
            T__107 = 15, T__106 = 16, T__105 = 17, T__104 = 18, T__103 = 19, T__102 = 20, T__101 = 21,
            T__100 = 22, T__99 = 23, T__98 = 24, T__97 = 25, T__96 = 26, T__95 = 27, T__94 = 28,
            T__93 = 29, T__92 = 30, T__91 = 31, T__90 = 32, T__89 = 33, T__88 = 34, T__87 = 35,
            T__86 = 36, T__85 = 37, T__84 = 38, T__83 = 39, T__82 = 40, T__81 = 41, T__80 = 42,
            T__79 = 43, T__78 = 44, T__77 = 45, T__76 = 46, T__75 = 47, T__74 = 48, T__73 = 49,
            T__72 = 50, T__71 = 51, T__70 = 52, T__69 = 53, T__68 = 54, T__67 = 55, T__66 = 56,
            T__65 = 57, T__64 = 58, T__63 = 59, T__62 = 60, T__61 = 61, T__60 = 62, T__59 = 63,
            T__58 = 64, T__57 = 65, T__56 = 66, T__55 = 67, T__54 = 68, T__53 = 69, T__52 = 70,
            T__51 = 71, T__50 = 72, T__49 = 73, T__48 = 74, T__47 = 75, T__46 = 76, T__45 = 77,
            T__44 = 78, T__43 = 79, T__42 = 80, T__41 = 81, T__40 = 82, T__39 = 83, T__38 = 84,
            T__37 = 85, T__36 = 86, T__35 = 87, T__34 = 88, T__33 = 89, T__32 = 90, T__31 = 91,
            T__30 = 92, T__29 = 93, T__28 = 94, T__27 = 95, T__26 = 96, T__25 = 97, T__24 = 98,
            T__23 = 99, T__22 = 100, T__21 = 101, T__20 = 102, T__19 = 103, T__18 = 104, T__17 = 105,
            T__16 = 106, T__15 = 107, T__14 = 108, T__13 = 109, T__12 = 110, T__11 = 111, T__10 = 112,
            T__9 = 113, T__8 = 114, T__7 = 115, T__6 = 116, T__5 = 117, T__4 = 118, T__3 = 119,
            T__2 = 120, T__1 = 121, T__0 = 122, INT = 123, FLOAT = 124, IDENTIFIER = 125, LETTER_US = 126,
            WS = 127, NL = 128, COMMENT = 129;
    public static final String[] tokenNames = {
            "<INVALID>", "'network'", "'TREES'", "'*'", "'taxa'", "'ntax'", "'false'",
            "'END'", "'nvertices'", "'endblock'", "'confidences'", "'TRUE'", "'h'",
            "'}'", "'Trees'", "')'", "'QUARTETS'", "'[&R]'", "'matrix'", "'begin'",
            "'yes'", "'Assumptions'", "'b'", "'lower'", "'BOTH'", "'[&U]'", "'cycle'",
            "'distances'", "','", "'triangle'", "'w'", "'UPPER'", "'TREE'", "'VLABELS'",
            "'#nexus'", "'Splits'", "'Draw'", "'Distances'", "'true'", "'FALSE'",
            "'ASSUMPTIONS'", "'NO'", "'LEFT'", "'vertices'", "'cyclic'", "'Vlabels'",
            "'Quartets'", "'#NEXUS'", "'Edges'", "'weights'", "'BEGIN'", "'TRANSLATE'",
            "'CYCLE'", "'left'", "'UTREE'", "'RIGHT'", "'st_quartets'", "'YES'", "'Vertices'",
            "'l'", "'y'", "'EDGES'", "'draw'", "'dimensions'", "'nodiagonal'", "'nolabels'",
            "'trees'", "'NODIAGONAL'", "'properties'", "'utree'", "'assumptions'",
            "'no'", "'SPLITS'", "'TAXLABELS'", "'both'", "'Translate'", "'interleave'",
            "'FORMAT'", "'='", "'TAXA'", "'x'", "'newtaxa'", "'labels'", "'st_Assumptions'",
            "'splits'", "'LOWER'", "'DISTANCES'", "'nedges'", "'rotateAbout'", "'Taxa'",
            "'MATRIX'", "'PROPERTIES'", "':'", "'('", "'DIAGONAL'", "'s'", "'missing'",
            "'f'", "'VERTICES'", "'''", "'DRAW'", "'{'", "'taxlabels'", "'upper'",
            "'translate'", "'to_scale'", "'diagonal'", "'vlabels'", "'quartets'",
            "'nchar'", "'edges'", "';'", "'Network'", "'format'", "'ENDBLOCK'", "'tree'",
            "'right'", "'DIMENSIONS'", "'nsplits'", "'NETWORK'", "'fit'", "'end'",
            "'intervals'", "INT", "FLOAT", "IDENTIFIER", "LETTER_US", "WS", "NL",
            "COMMENT"
    };
    public static final int
            RULE_parse = 0, RULE_nexus_header = 1, RULE_blocks = 2, RULE_block = 3,
            RULE_begin = 4, RULE_end = 5, RULE_block_declaration = 6, RULE_block_taxa = 7,
            RULE_taxa_header = 8, RULE_dimensions_taxa = 9, RULE_block_distances = 10,
            RULE_distances_header = 11, RULE_dimensions_distances = 12, RULE_nchar = 13,
            RULE_format_distances = 14, RULE_format_distances_list = 15, RULE_format_distances_item = 16,
            RULE_triangle = 17, RULE_triangle_option = 18, RULE_diagonal = 19, RULE_labels = 20,
            RULE_labels_header = 21, RULE_labels_option = 22, RULE_block_splits = 23,
            RULE_splits_block_header = 24, RULE_dimensions_splits = 25, RULE_format_splits = 26,
            RULE_format_splits_list = 27, RULE_format_splits_item = 28, RULE_labels_splits = 29,
            RULE_weights_splits = 30, RULE_weights_header = 31, RULE_confidences_splits = 32,
            RULE_confidences_header = 33, RULE_intervals_splits = 34, RULE_intervals_header = 35,
            RULE_properties_splits = 36, RULE_properties_splits_list = 37, RULE_properties_splits_item = 38,
            RULE_properties_splits_name = 39, RULE_cycle = 40, RULE_cycle_header = 41,
            RULE_cycle_item_list = 42, RULE_cycle_item = 43, RULE_matrix_splits_data = 44,
            RULE_matrix_split_identifier = 45, RULE_matrix_splits_list = 46, RULE_block_quartets = 47,
            RULE_quartets_block_header = 48, RULE_matrix_quartets_data = 49, RULE_matrix_quartet = 50,
            RULE_label_quartet = 51, RULE_weight_quartet = 52, RULE_x_quartet = 53,
            RULE_y_quartet = 54, RULE_u_quartet = 55, RULE_v_quartet = 56, RULE_sc_quartet = 57,
            RULE_cs_quartet = 58, RULE_block_assumptions = 59, RULE_assumptions_block_header = 60,
            RULE_assumptions_data = 61, RULE_assumptions_data_entry = 62, RULE_key_value_pairs = 63,
            RULE_key_value_pair = 64, RULE_block_trees = 65, RULE_tree_block_header = 66,
            RULE_translate = 67, RULE_translate_header = 68, RULE_translate_list = 69,
            RULE_newick_tree = 70, RULE_tree_header = 71, RULE_tree_rest = 72, RULE_tree_definition = 73,
            RULE_root = 74, RULE_tree_list = 75, RULE_tree_label = 76, RULE_tree_label_optional = 77,
            RULE_length = 78, RULE_block_network = 79, RULE_network_block_header = 80,
            RULE_dimensions_network = 81, RULE_draw_network = 82, RULE_draw_network_header = 83,
            RULE_draw_network_options = 84, RULE_draw_network_option = 85, RULE_scale_network = 86,
            RULE_rotate_network = 87, RULE_translate_network = 88, RULE_translate_network_header = 89,
            RULE_translate_network_data = 90, RULE_translate_network_entry = 91, RULE_vertices_network = 92,
            RULE_vertices_network_header = 93, RULE_vertices_network_data = 94, RULE_vertices_network_entry = 95,
            RULE_vertices_2d_data = 96, RULE_vertices_3d_data = 97, RULE_vlabels_network = 98,
            RULE_vlabels_network_header = 99, RULE_vlabels_network_data = 100, RULE_vlabels_network_entry = 101,
            RULE_vlabels_network_label = 102, RULE_vlabels_data = 103, RULE_edges_network = 104,
            RULE_edges_network_header = 105, RULE_edges_network_data = 106, RULE_edges_network_entry = 107,
            RULE_matrix_header = 108, RULE_matrix_data = 109, RULE_matrix_entry_list = 110,
            RULE_state_composed_word = 111, RULE_state_complex_word = 112, RULE_state_composed_list = 113,
            RULE_boolean_option = 114, RULE_dimensions = 115, RULE_format = 116, RULE_identifier_list = 117,
            RULE_missing = 118, RULE_ntax = 119, RULE_newtaxa = 120, RULE_newtaxa_optional = 121,
            RULE_nsplits = 122, RULE_nvertices = 123, RULE_nedges = 124, RULE_properties = 125,
            RULE_reference = 126, RULE_star = 127, RULE_taxlabels = 128, RULE_taxlabels_optional = 129,
            RULE_taxlabels_header = 130, RULE_number = 131;
    public static final String[] ruleNames = {
            "parse", "nexus_header", "blocks", "block", "begin", "end", "block_declaration",
            "block_taxa", "taxa_header", "dimensions_taxa", "block_distances", "distances_header",
            "dimensions_distances", "nchar", "format_distances", "format_distances_list",
            "format_distances_item", "triangle", "triangle_option", "diagonal", "labels",
            "labels_header", "labels_option", "block_splits", "splits_block_header",
            "dimensions_splits", "format_splits", "format_splits_list", "format_splits_item",
            "labels_splits", "weights_splits", "weights_header", "confidences_splits",
            "confidences_header", "intervals_splits", "intervals_header", "properties_splits",
            "properties_splits_list", "properties_splits_item", "properties_splits_name",
            "cycle", "cycle_header", "cycle_item_list", "cycle_item", "matrix_splits_data",
            "matrix_split_identifier", "matrix_splits_list", "block_quartets", "quartets_block_header",
            "matrix_quartets_data", "matrix_quartet", "label_quartet", "weight_quartet",
            "x_quartet", "y_quartet", "u_quartet", "v_quartet", "sc_quartet", "cs_quartet",
            "block_assumptions", "assumptions_block_header", "assumptions_data", "assumptions_data_entry",
            "key_value_pairs", "key_value_pair", "block_trees", "tree_block_header",
            "translate", "translate_header", "translate_list", "newick_tree", "tree_header",
            "tree_rest", "tree_definition", "root", "tree_list", "tree_label", "tree_label_optional",
            "length", "block_network", "network_block_header", "dimensions_network",
            "draw_network", "draw_network_header", "draw_network_options", "draw_network_option",
            "scale_network", "rotate_network", "translate_network", "translate_network_header",
            "translate_network_data", "translate_network_entry", "vertices_network",
            "vertices_network_header", "vertices_network_data", "vertices_network_entry",
            "vertices_2d_data", "vertices_3d_data", "vlabels_network", "vlabels_network_header",
            "vlabels_network_data", "vlabels_network_entry", "vlabels_network_label",
            "vlabels_data", "edges_network", "edges_network_header", "edges_network_data",
            "edges_network_entry", "matrix_header", "matrix_data", "matrix_entry_list",
            "state_composed_word", "state_complex_word", "state_composed_list", "boolean_option",
            "dimensions", "format", "identifier_list", "missing", "ntax", "newtaxa",
            "newtaxa_optional", "nsplits", "nvertices", "nedges", "properties", "reference",
            "star", "taxlabels", "taxlabels_optional", "taxlabels_header", "number"
    };

    @Override
    public String getGrammarFileName() {
        return "NexusFile.g4";
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

    public NexusFileParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class ParseContext extends ParserRuleContext {
        public BlocksContext blocks() {
            return getRuleContext(BlocksContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(NexusFileParser.EOF, 0);
        }

        public Nexus_headerContext nexus_header() {
            return getRuleContext(Nexus_headerContext.class, 0);
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
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterParse(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitParse(this);
        }
    }

    public final ParseContext parse() throws RecognitionException {
        ParseContext _localctx = new ParseContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_parse);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(264);
                nexus_header();
                setState(265);
                blocks();
                setState(266);
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

    public static class Nexus_headerContext extends ParserRuleContext {
        public Nexus_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nexus_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNexus_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNexus_header(this);
        }
    }

    public final Nexus_headerContext nexus_header() throws RecognitionException {
        Nexus_headerContext _localctx = new Nexus_headerContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_nexus_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(268);
                _la = _input.LA(1);
                if (!(_la == 34 || _la == 47)) {
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

    public static class BlocksContext extends ParserRuleContext {
        public BlocksContext blocks() {
            return getRuleContext(BlocksContext.class, 0);
        }

        public BlockContext block() {
            return getRuleContext(BlockContext.class, 0);
        }

        public BlocksContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blocks;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlocks(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlocks(this);
        }
    }

    public final BlocksContext blocks() throws RecognitionException {
        BlocksContext _localctx = new BlocksContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_blocks);
        try {
            setState(274);
            switch (_input.LA(1)) {
                case EOF:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 19:
                case 50:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(271);
                    block();
                    setState(272);
                    blocks();
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

    public static class BlockContext extends ParserRuleContext {
        public Block_declarationContext block_declaration() {
            return getRuleContext(Block_declarationContext.class, 0);
        }

        public BeginContext begin() {
            return getRuleContext(BeginContext.class, 0);
        }

        public EndContext end() {
            return getRuleContext(EndContext.class, 0);
        }

        public BlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock(this);
        }
    }

    public final BlockContext block() throws RecognitionException {
        BlockContext _localctx = new BlockContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_block);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(276);
                begin();
                setState(277);
                block_declaration();
                setState(278);
                end();
                setState(279);
                match(111);
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

    public static class BeginContext extends ParserRuleContext {
        public BeginContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_begin;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBegin(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBegin(this);
        }
    }

    public final BeginContext begin() throws RecognitionException {
        BeginContext _localctx = new BeginContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_begin);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(281);
                _la = _input.LA(1);
                if (!(_la == 19 || _la == 50)) {
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

    public static class EndContext extends ParserRuleContext {
        public EndContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_end;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterEnd(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitEnd(this);
        }
    }

    public final EndContext end() throws RecognitionException {
        EndContext _localctx = new EndContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_end);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(283);
                _la = _input.LA(1);
                if (!(_la == 7 || _la == 9 || _la == 114 || _la == 121)) {
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

    public static class Block_declarationContext extends ParserRuleContext {
        public Block_quartetsContext block_quartets() {
            return getRuleContext(Block_quartetsContext.class, 0);
        }

        public Block_distancesContext block_distances() {
            return getRuleContext(Block_distancesContext.class, 0);
        }

        public Block_taxaContext block_taxa() {
            return getRuleContext(Block_taxaContext.class, 0);
        }

        public Block_networkContext block_network() {
            return getRuleContext(Block_networkContext.class, 0);
        }

        public Block_treesContext block_trees() {
            return getRuleContext(Block_treesContext.class, 0);
        }

        public Block_assumptionsContext block_assumptions() {
            return getRuleContext(Block_assumptionsContext.class, 0);
        }

        public Block_splitsContext block_splits() {
            return getRuleContext(Block_splitsContext.class, 0);
        }

        public Block_declarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_declaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_declaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_declaration(this);
        }
    }

    public final Block_declarationContext block_declaration() throws RecognitionException {
        Block_declarationContext _localctx = new Block_declarationContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_block_declaration);
        try {
            setState(292);
            switch (_input.LA(1)) {
                case 4:
                case 79:
                case 89:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(285);
                    block_taxa();
                }
                break;
                case 27:
                case 37:
                case 86:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(286);
                    block_distances();
                }
                break;
                case 35:
                case 72:
                case 84:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(287);
                    block_splits();
                }
                break;
                case 16:
                case 46:
                case 56:
                case 108:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(288);
                    block_quartets();
                }
                break;
                case 21:
                case 40:
                case 70:
                case 83:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(289);
                    block_assumptions();
                }
                break;
                case 2:
                case 14:
                case 66:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(290);
                    block_trees();
                }
                break;
                case 1:
                case 112:
                case 119:
                    enterOuterAlt(_localctx, 7);
                {
                    setState(291);
                    block_network();
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

    public static class Block_taxaContext extends ParserRuleContext {
        public TaxlabelsContext taxlabels() {
            return getRuleContext(TaxlabelsContext.class, 0);
        }

        public Dimensions_taxaContext dimensions_taxa() {
            return getRuleContext(Dimensions_taxaContext.class, 0);
        }

        public Taxa_headerContext taxa_header() {
            return getRuleContext(Taxa_headerContext.class, 0);
        }

        public Block_taxaContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_taxa;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_taxa(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_taxa(this);
        }
    }

    public final Block_taxaContext block_taxa() throws RecognitionException {
        Block_taxaContext _localctx = new Block_taxaContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_block_taxa);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(294);
                taxa_header();
                setState(295);
                match(111);
                setState(296);
                dimensions_taxa();
                setState(297);
                taxlabels();
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

    public static class Taxa_headerContext extends ParserRuleContext {
        public Taxa_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_taxa_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTaxa_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTaxa_header(this);
        }
    }

    public final Taxa_headerContext taxa_header() throws RecognitionException {
        Taxa_headerContext _localctx = new Taxa_headerContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_taxa_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(299);
                _la = _input.LA(1);
                if (!(_la == 4 || _la == 79 || _la == 89)) {
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

    public static class Dimensions_taxaContext extends ParserRuleContext {
        public NtaxContext ntax() {
            return getRuleContext(NtaxContext.class, 0);
        }

        public DimensionsContext dimensions() {
            return getRuleContext(DimensionsContext.class, 0);
        }

        public Dimensions_taxaContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dimensions_taxa;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDimensions_taxa(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDimensions_taxa(this);
        }
    }

    public final Dimensions_taxaContext dimensions_taxa() throws RecognitionException {
        Dimensions_taxaContext _localctx = new Dimensions_taxaContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_dimensions_taxa);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(301);
                dimensions();
                setState(302);
                ntax();
                setState(303);
                match(111);
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

    public static class Block_distancesContext extends ParserRuleContext {
        public Dimensions_distancesContext dimensions_distances() {
            return getRuleContext(Dimensions_distancesContext.class, 0);
        }

        public Taxlabels_optionalContext taxlabels_optional() {
            return getRuleContext(Taxlabels_optionalContext.class, 0);
        }

        public Distances_headerContext distances_header() {
            return getRuleContext(Distances_headerContext.class, 0);
        }

        public Matrix_dataContext matrix_data() {
            return getRuleContext(Matrix_dataContext.class, 0);
        }

        public Matrix_headerContext matrix_header() {
            return getRuleContext(Matrix_headerContext.class, 0);
        }

        public Format_distancesContext format_distances() {
            return getRuleContext(Format_distancesContext.class, 0);
        }

        public Block_distancesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_distances;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_distances(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_distances(this);
        }
    }

    public final Block_distancesContext block_distances() throws RecognitionException {
        Block_distancesContext _localctx = new Block_distancesContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_block_distances);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(305);
                distances_header();
                setState(306);
                match(111);
                setState(307);
                dimensions_distances();
                setState(308);
                format_distances();
                setState(309);
                taxlabels_optional();
                setState(310);
                matrix_header();
                setState(311);
                matrix_data();
                setState(312);
                match(111);
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

    public static class Distances_headerContext extends ParserRuleContext {
        public Distances_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_distances_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDistances_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDistances_header(this);
        }
    }

    public final Distances_headerContext distances_header() throws RecognitionException {
        Distances_headerContext _localctx = new Distances_headerContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_distances_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(314);
                _la = _input.LA(1);
                if (!(((((_la - 27)) & ~0x3f) == 0 && ((1L << (_la - 27)) & ((1L << (27 - 27)) | (1L << (37 - 27)) | (1L << (86 - 27)))) != 0))) {
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

    public static class Dimensions_distancesContext extends ParserRuleContext {
        public NcharContext nchar() {
            return getRuleContext(NcharContext.class, 0);
        }

        public NewtaxaContext newtaxa() {
            return getRuleContext(NewtaxaContext.class, 0);
        }

        public DimensionsContext dimensions() {
            return getRuleContext(DimensionsContext.class, 0);
        }

        public Dimensions_distancesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dimensions_distances;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDimensions_distances(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDimensions_distances(this);
        }
    }

    public final Dimensions_distancesContext dimensions_distances() throws RecognitionException {
        Dimensions_distancesContext _localctx = new Dimensions_distancesContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_dimensions_distances);
        try {
            setState(322);
            switch (_input.LA(1)) {
                case 18:
                case 73:
                case 77:
                case 90:
                case 102:
                case 113:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 63:
                case 117:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(317);
                    dimensions();
                    setState(318);
                    newtaxa();
                    setState(319);
                    nchar();
                    setState(320);
                    match(111);
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

    public static class NcharContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public NcharContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nchar;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNchar(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNchar(this);
        }
    }

    public final NcharContext nchar() throws RecognitionException {
        NcharContext _localctx = new NcharContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_nchar);
        try {
            setState(328);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 109:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(325);
                    match(109);
                    setState(326);
                    match(78);
                    setState(327);
                    match(INT);
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

    public static class Format_distancesContext extends ParserRuleContext {
        public FormatContext format() {
            return getRuleContext(FormatContext.class, 0);
        }

        public Format_distances_listContext format_distances_list() {
            return getRuleContext(Format_distances_listContext.class, 0);
        }

        public Format_distancesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_format_distances;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterFormat_distances(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitFormat_distances(this);
        }
    }

    public final Format_distancesContext format_distances() throws RecognitionException {
        Format_distancesContext _localctx = new Format_distancesContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_format_distances);
        try {
            setState(335);
            switch (_input.LA(1)) {
                case 18:
                case 73:
                case 90:
                case 102:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 77:
                case 113:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(331);
                    format();
                    setState(332);
                    format_distances_list();
                    setState(333);
                    match(111);
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

    public static class Format_distances_listContext extends ParserRuleContext {
        public Format_distances_itemContext format_distances_item() {
            return getRuleContext(Format_distances_itemContext.class, 0);
        }

        public Format_distances_listContext format_distances_list() {
            return getRuleContext(Format_distances_listContext.class, 0);
        }

        public Format_distances_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_format_distances_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterFormat_distances_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitFormat_distances_list(this);
        }
    }

    public final Format_distances_listContext format_distances_list() throws RecognitionException {
        Format_distances_listContext _localctx = new Format_distances_listContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_format_distances_list);
        try {
            setState(341);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 29:
                case 64:
                case 65:
                case 67:
                case 76:
                case 82:
                case 94:
                case 96:
                case 106:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(338);
                    format_distances_item();
                    setState(339);
                    format_distances_list();
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

    public static class Format_distances_itemContext extends ParserRuleContext {
        public DiagonalContext diagonal() {
            return getRuleContext(DiagonalContext.class, 0);
        }

        public LabelsContext labels() {
            return getRuleContext(LabelsContext.class, 0);
        }

        public TriangleContext triangle() {
            return getRuleContext(TriangleContext.class, 0);
        }

        public MissingContext missing() {
            return getRuleContext(MissingContext.class, 0);
        }

        public Format_distances_itemContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_format_distances_item;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterFormat_distances_item(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitFormat_distances_item(this);
        }
    }

    public final Format_distances_itemContext format_distances_item() throws RecognitionException {
        Format_distances_itemContext _localctx = new Format_distances_itemContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_format_distances_item);
        try {
            setState(348);
            switch (_input.LA(1)) {
                case 29:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(343);
                    triangle();
                }
                break;
                case 64:
                case 67:
                case 94:
                case 106:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(344);
                    diagonal();
                }
                break;
                case 65:
                case 82:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(345);
                    labels();
                }
                break;
                case 96:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(346);
                    missing();
                }
                break;
                case 76:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(347);
                    match(76);
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

    public static class TriangleContext extends ParserRuleContext {
        public Triangle_optionContext triangle_option() {
            return getRuleContext(Triangle_optionContext.class, 0);
        }

        public TriangleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_triangle;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTriangle(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTriangle(this);
        }
    }

    public final TriangleContext triangle() throws RecognitionException {
        TriangleContext _localctx = new TriangleContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_triangle);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(350);
                match(29);
                setState(351);
                match(78);
                setState(352);
                triangle_option();
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

    public static class Triangle_optionContext extends ParserRuleContext {
        public Triangle_optionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_triangle_option;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTriangle_option(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTriangle_option(this);
        }
    }

    public final Triangle_optionContext triangle_option() throws RecognitionException {
        Triangle_optionContext _localctx = new Triangle_optionContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_triangle_option);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(354);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 23) | (1L << 24) | (1L << 31))) != 0) || ((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & ((1L << (74 - 74)) | (1L << (85 - 74)) | (1L << (103 - 74)))) != 0))) {
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

    public static class DiagonalContext extends ParserRuleContext {
        public DiagonalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_diagonal;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDiagonal(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDiagonal(this);
        }
    }

    public final DiagonalContext diagonal() throws RecognitionException {
        DiagonalContext _localctx = new DiagonalContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_diagonal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(356);
                _la = _input.LA(1);
                if (!(((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (64 - 64)) | (1L << (67 - 64)) | (1L << (94 - 64)) | (1L << (106 - 64)))) != 0))) {
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

    public static class LabelsContext extends ParserRuleContext {
        public Labels_optionContext labels_option() {
            return getRuleContext(Labels_optionContext.class, 0);
        }

        public Labels_headerContext labels_header() {
            return getRuleContext(Labels_headerContext.class, 0);
        }

        public LabelsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_labels;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterLabels(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitLabels(this);
        }
    }

    public final LabelsContext labels() throws RecognitionException {
        LabelsContext _localctx = new LabelsContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_labels);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(358);
                labels_header();
                setState(361);
                _la = _input.LA(1);
                if (_la == 78) {
                    {
                        setState(359);
                        match(78);
                        setState(360);
                        labels_option();
                    }
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

    public static class Labels_headerContext extends ParserRuleContext {
        public Labels_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_labels_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterLabels_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitLabels_header(this);
        }
    }

    public final Labels_headerContext labels_header() throws RecognitionException {
        Labels_headerContext _localctx = new Labels_headerContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_labels_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(363);
                _la = _input.LA(1);
                if (!(_la == 65 || _la == 82)) {
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

    public static class Labels_optionContext extends ParserRuleContext {
        public Labels_optionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_labels_option;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterLabels_option(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitLabels_option(this);
        }
    }

    public final Labels_optionContext labels_option() throws RecognitionException {
        Labels_optionContext _localctx = new Labels_optionContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_labels_option);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(365);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 11) | (1L << 20) | (1L << 38) | (1L << 39) | (1L << 41) | (1L << 42) | (1L << 53) | (1L << 55) | (1L << 57))) != 0) || _la == 71 || _la == 116)) {
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

    public static class Block_splitsContext extends ParserRuleContext {
        public Properties_splitsContext properties_splits() {
            return getRuleContext(Properties_splitsContext.class, 0);
        }

        public CycleContext cycle() {
            return getRuleContext(CycleContext.class, 0);
        }

        public Splits_block_headerContext splits_block_header() {
            return getRuleContext(Splits_block_headerContext.class, 0);
        }

        public Matrix_headerContext matrix_header() {
            return getRuleContext(Matrix_headerContext.class, 0);
        }

        public Dimensions_splitsContext dimensions_splits() {
            return getRuleContext(Dimensions_splitsContext.class, 0);
        }

        public Matrix_splits_dataContext matrix_splits_data() {
            return getRuleContext(Matrix_splits_dataContext.class, 0);
        }

        public Format_splitsContext format_splits() {
            return getRuleContext(Format_splitsContext.class, 0);
        }

        public Block_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_splits(this);
        }
    }

    public final Block_splitsContext block_splits() throws RecognitionException {
        Block_splitsContext _localctx = new Block_splitsContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_block_splits);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(367);
                splits_block_header();
                setState(368);
                match(111);
                setState(369);
                dimensions_splits();
                setState(370);
                format_splits();
                setState(371);
                properties_splits();
                setState(372);
                cycle();
                setState(373);
                matrix_header();
                setState(374);
                matrix_splits_data();
                setState(375);
                match(111);
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

    public static class Splits_block_headerContext extends ParserRuleContext {
        public Splits_block_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_splits_block_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterSplits_block_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitSplits_block_header(this);
        }
    }

    public final Splits_block_headerContext splits_block_header() throws RecognitionException {
        Splits_block_headerContext _localctx = new Splits_block_headerContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_splits_block_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(377);
                _la = _input.LA(1);
                if (!(((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (35 - 35)) | (1L << (72 - 35)) | (1L << (84 - 35)))) != 0))) {
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

    public static class Dimensions_splitsContext extends ParserRuleContext {
        public NsplitsContext nsplits() {
            return getRuleContext(NsplitsContext.class, 0);
        }

        public NtaxContext ntax() {
            return getRuleContext(NtaxContext.class, 0);
        }

        public DimensionsContext dimensions() {
            return getRuleContext(DimensionsContext.class, 0);
        }

        public Dimensions_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dimensions_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDimensions_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDimensions_splits(this);
        }
    }

    public final Dimensions_splitsContext dimensions_splits() throws RecognitionException {
        Dimensions_splitsContext _localctx = new Dimensions_splitsContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_dimensions_splits);
        try {
            setState(385);
            switch (_input.LA(1)) {
                case 18:
                case 26:
                case 52:
                case 68:
                case 77:
                case 90:
                case 91:
                case 113:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 63:
                case 117:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(380);
                    dimensions();
                    setState(381);
                    ntax();
                    setState(382);
                    nsplits();
                    setState(383);
                    match(111);
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

    public static class Format_splitsContext extends ParserRuleContext {
        public Format_splits_listContext format_splits_list() {
            return getRuleContext(Format_splits_listContext.class, 0);
        }

        public FormatContext format() {
            return getRuleContext(FormatContext.class, 0);
        }

        public Format_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_format_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterFormat_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitFormat_splits(this);
        }
    }

    public final Format_splitsContext format_splits() throws RecognitionException {
        Format_splitsContext _localctx = new Format_splitsContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_format_splits);
        try {
            setState(392);
            switch (_input.LA(1)) {
                case 18:
                case 26:
                case 52:
                case 68:
                case 90:
                case 91:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 77:
                case 113:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(388);
                    format();
                    setState(389);
                    format_splits_list();
                    setState(390);
                    match(111);
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

    public static class Format_splits_listContext extends ParserRuleContext {
        public Format_splits_listContext format_splits_list() {
            return getRuleContext(Format_splits_listContext.class, 0);
        }

        public Format_splits_itemContext format_splits_item() {
            return getRuleContext(Format_splits_itemContext.class, 0);
        }

        public Format_splits_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_format_splits_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterFormat_splits_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitFormat_splits_list(this);
        }
    }

    public final Format_splits_listContext format_splits_list() throws RecognitionException {
        Format_splits_listContext _localctx = new Format_splits_listContext(_ctx, getState());
        enterRule(_localctx, 54, RULE_format_splits_list);
        try {
            setState(398);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 10:
                case 49:
                case 65:
                case 82:
                case 122:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(395);
                    format_splits_item();
                    setState(396);
                    format_splits_list();
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

    public static class Format_splits_itemContext extends ParserRuleContext {
        public Confidences_splitsContext confidences_splits() {
            return getRuleContext(Confidences_splitsContext.class, 0);
        }

        public Intervals_splitsContext intervals_splits() {
            return getRuleContext(Intervals_splitsContext.class, 0);
        }

        public Labels_splitsContext labels_splits() {
            return getRuleContext(Labels_splitsContext.class, 0);
        }

        public Weights_splitsContext weights_splits() {
            return getRuleContext(Weights_splitsContext.class, 0);
        }

        public Format_splits_itemContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_format_splits_item;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterFormat_splits_item(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitFormat_splits_item(this);
        }
    }

    public final Format_splits_itemContext format_splits_item() throws RecognitionException {
        Format_splits_itemContext _localctx = new Format_splits_itemContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_format_splits_item);
        try {
            setState(404);
            switch (_input.LA(1)) {
                case 65:
                case 82:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(400);
                    labels_splits();
                }
                break;
                case 49:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(401);
                    weights_splits();
                }
                break;
                case 10:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(402);
                    confidences_splits();
                }
                break;
                case 122:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(403);
                    intervals_splits();
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

    public static class Labels_splitsContext extends ParserRuleContext {
        public Labels_optionContext labels_option() {
            return getRuleContext(Labels_optionContext.class, 0);
        }

        public Labels_headerContext labels_header() {
            return getRuleContext(Labels_headerContext.class, 0);
        }

        public Labels_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_labels_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterLabels_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitLabels_splits(this);
        }
    }

    public final Labels_splitsContext labels_splits() throws RecognitionException {
        Labels_splitsContext _localctx = new Labels_splitsContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_labels_splits);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(406);
                labels_header();
                setState(409);
                _la = _input.LA(1);
                if (_la == 78) {
                    {
                        setState(407);
                        match(78);
                        setState(408);
                        labels_option();
                    }
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

    public static class Weights_splitsContext extends ParserRuleContext {
        public Weights_headerContext weights_header() {
            return getRuleContext(Weights_headerContext.class, 0);
        }

        public Boolean_optionContext boolean_option() {
            return getRuleContext(Boolean_optionContext.class, 0);
        }

        public Weights_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weights_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterWeights_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitWeights_splits(this);
        }
    }

    public final Weights_splitsContext weights_splits() throws RecognitionException {
        Weights_splitsContext _localctx = new Weights_splitsContext(_ctx, getState());
        enterRule(_localctx, 60, RULE_weights_splits);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(411);
                weights_header();
                setState(412);
                match(78);
                setState(413);
                boolean_option();
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

    public static class Weights_headerContext extends ParserRuleContext {
        public Weights_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weights_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterWeights_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitWeights_header(this);
        }
    }

    public final Weights_headerContext weights_header() throws RecognitionException {
        Weights_headerContext _localctx = new Weights_headerContext(_ctx, getState());
        enterRule(_localctx, 62, RULE_weights_header);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(415);
                match(49);
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

    public static class Confidences_splitsContext extends ParserRuleContext {
        public Confidences_headerContext confidences_header() {
            return getRuleContext(Confidences_headerContext.class, 0);
        }

        public Boolean_optionContext boolean_option() {
            return getRuleContext(Boolean_optionContext.class, 0);
        }

        public Confidences_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_confidences_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterConfidences_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitConfidences_splits(this);
        }
    }

    public final Confidences_splitsContext confidences_splits() throws RecognitionException {
        Confidences_splitsContext _localctx = new Confidences_splitsContext(_ctx, getState());
        enterRule(_localctx, 64, RULE_confidences_splits);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(417);
                confidences_header();
                setState(418);
                match(78);
                setState(419);
                boolean_option();
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

    public static class Confidences_headerContext extends ParserRuleContext {
        public Confidences_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_confidences_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterConfidences_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitConfidences_header(this);
        }
    }

    public final Confidences_headerContext confidences_header() throws RecognitionException {
        Confidences_headerContext _localctx = new Confidences_headerContext(_ctx, getState());
        enterRule(_localctx, 66, RULE_confidences_header);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(421);
                match(10);
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

    public static class Intervals_splitsContext extends ParserRuleContext {
        public Intervals_headerContext intervals_header() {
            return getRuleContext(Intervals_headerContext.class, 0);
        }

        public Boolean_optionContext boolean_option() {
            return getRuleContext(Boolean_optionContext.class, 0);
        }

        public Intervals_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_intervals_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterIntervals_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitIntervals_splits(this);
        }
    }

    public final Intervals_splitsContext intervals_splits() throws RecognitionException {
        Intervals_splitsContext _localctx = new Intervals_splitsContext(_ctx, getState());
        enterRule(_localctx, 68, RULE_intervals_splits);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(423);
                intervals_header();
                setState(424);
                match(78);
                setState(425);
                boolean_option();
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

    public static class Intervals_headerContext extends ParserRuleContext {
        public Intervals_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_intervals_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterIntervals_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitIntervals_header(this);
        }
    }

    public final Intervals_headerContext intervals_header() throws RecognitionException {
        Intervals_headerContext _localctx = new Intervals_headerContext(_ctx, getState());
        enterRule(_localctx, 70, RULE_intervals_header);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(427);
                match(122);
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

    public static class Properties_splitsContext extends ParserRuleContext {
        public PropertiesContext properties() {
            return getRuleContext(PropertiesContext.class, 0);
        }

        public Properties_splits_listContext properties_splits_list() {
            return getRuleContext(Properties_splits_listContext.class, 0);
        }

        public Properties_splitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_properties_splits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterProperties_splits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitProperties_splits(this);
        }
    }

    public final Properties_splitsContext properties_splits() throws RecognitionException {
        Properties_splitsContext _localctx = new Properties_splitsContext(_ctx, getState());
        enterRule(_localctx, 72, RULE_properties_splits);
        try {
            setState(434);
            switch (_input.LA(1)) {
                case 18:
                case 26:
                case 52:
                case 90:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 68:
                case 91:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(430);
                    properties();
                    setState(431);
                    properties_splits_list();
                    setState(432);
                    match(111);
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

    public static class Properties_splits_listContext extends ParserRuleContext {
        public Properties_splits_itemContext properties_splits_item() {
            return getRuleContext(Properties_splits_itemContext.class, 0);
        }

        public Properties_splits_listContext properties_splits_list() {
            return getRuleContext(Properties_splits_listContext.class, 0);
        }

        public Properties_splits_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_properties_splits_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterProperties_splits_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitProperties_splits_list(this);
        }
    }

    public final Properties_splits_listContext properties_splits_list() throws RecognitionException {
        Properties_splits_listContext _localctx = new Properties_splits_listContext(_ctx, getState());
        enterRule(_localctx, 74, RULE_properties_splits_list);
        try {
            setState(440);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 44:
                case 120:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(437);
                    properties_splits_item();
                    setState(438);
                    properties_splits_list();
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

    public static class Properties_splits_itemContext extends ParserRuleContext {
        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public Properties_splits_nameContext properties_splits_name() {
            return getRuleContext(Properties_splits_nameContext.class, 0);
        }

        public Properties_splits_itemContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_properties_splits_item;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterProperties_splits_item(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitProperties_splits_item(this);
        }
    }

    public final Properties_splits_itemContext properties_splits_item() throws RecognitionException {
        Properties_splits_itemContext _localctx = new Properties_splits_itemContext(_ctx, getState());
        enterRule(_localctx, 76, RULE_properties_splits_item);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(442);
                properties_splits_name();
                setState(445);
                _la = _input.LA(1);
                if (_la == 78) {
                    {
                        setState(443);
                        match(78);
                        setState(444);
                        number();
                    }
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

    public static class Properties_splits_nameContext extends ParserRuleContext {
        public Properties_splits_nameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_properties_splits_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterProperties_splits_name(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitProperties_splits_name(this);
        }
    }

    public final Properties_splits_nameContext properties_splits_name() throws RecognitionException {
        Properties_splits_nameContext _localctx = new Properties_splits_nameContext(_ctx, getState());
        enterRule(_localctx, 78, RULE_properties_splits_name);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(447);
                _la = _input.LA(1);
                if (!(_la == 44 || _la == 120)) {
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

    public static class CycleContext extends ParserRuleContext {
        public Cycle_headerContext cycle_header() {
            return getRuleContext(Cycle_headerContext.class, 0);
        }

        public Cycle_item_listContext cycle_item_list() {
            return getRuleContext(Cycle_item_listContext.class, 0);
        }

        public CycleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cycle;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterCycle(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitCycle(this);
        }
    }

    public final CycleContext cycle() throws RecognitionException {
        CycleContext _localctx = new CycleContext(_ctx, getState());
        enterRule(_localctx, 80, RULE_cycle);
        try {
            setState(454);
            switch (_input.LA(1)) {
                case 18:
                case 90:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 26:
                case 52:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(450);
                    cycle_header();
                    setState(451);
                    cycle_item_list();
                    setState(452);
                    match(111);
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

    public static class Cycle_headerContext extends ParserRuleContext {
        public Cycle_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cycle_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterCycle_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitCycle_header(this);
        }
    }

    public final Cycle_headerContext cycle_header() throws RecognitionException {
        Cycle_headerContext _localctx = new Cycle_headerContext(_ctx, getState());
        enterRule(_localctx, 82, RULE_cycle_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(456);
                _la = _input.LA(1);
                if (!(_la == 26 || _la == 52)) {
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

    public static class Cycle_item_listContext extends ParserRuleContext {
        public Cycle_item_listContext cycle_item_list() {
            return getRuleContext(Cycle_item_listContext.class, 0);
        }

        public Cycle_itemContext cycle_item() {
            return getRuleContext(Cycle_itemContext.class, 0);
        }

        public Cycle_item_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cycle_item_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterCycle_item_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitCycle_item_list(this);
        }
    }

    public final Cycle_item_listContext cycle_item_list() throws RecognitionException {
        Cycle_item_listContext _localctx = new Cycle_item_listContext(_ctx, getState());
        enterRule(_localctx, 84, RULE_cycle_item_list);
        try {
            setState(462);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(459);
                    cycle_item();
                    setState(460);
                    cycle_item_list();
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

    public static class Cycle_itemContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public Cycle_itemContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cycle_item;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterCycle_item(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitCycle_item(this);
        }
    }

    public final Cycle_itemContext cycle_item() throws RecognitionException {
        Cycle_itemContext _localctx = new Cycle_itemContext(_ctx, getState());
        enterRule(_localctx, 86, RULE_cycle_item);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(464);
                match(INT);
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

    public static class Matrix_splits_dataContext extends ParserRuleContext {
        public Matrix_splits_listContext matrix_splits_list() {
            return getRuleContext(Matrix_splits_listContext.class, 0);
        }

        public TerminalNode FLOAT() {
            return getToken(NexusFileParser.FLOAT, 0);
        }

        public Matrix_split_identifierContext matrix_split_identifier() {
            return getRuleContext(Matrix_split_identifierContext.class, 0);
        }

        public Matrix_splits_dataContext matrix_splits_data() {
            return getRuleContext(Matrix_splits_dataContext.class, 0);
        }

        public Matrix_splits_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_splits_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMatrix_splits_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_splits_data(this);
        }
    }

    public final Matrix_splits_dataContext matrix_splits_data() throws RecognitionException {
        Matrix_splits_dataContext _localctx = new Matrix_splits_dataContext(_ctx, getState());
        enterRule(_localctx, 88, RULE_matrix_splits_data);
        try {
            setState(473);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 99:
                case INT:
                case FLOAT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(467);
                    matrix_split_identifier();
                    setState(468);
                    match(FLOAT);
                    setState(469);
                    matrix_splits_list();
                    setState(470);
                    match(28);
                    setState(471);
                    matrix_splits_data();
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

    public static class Matrix_split_identifierContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public Matrix_split_identifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_split_identifier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener)
                ((NexusFileListener) listener).enterMatrix_split_identifier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_split_identifier(this);
        }
    }

    public final Matrix_split_identifierContext matrix_split_identifier() throws RecognitionException {
        Matrix_split_identifierContext _localctx = new Matrix_split_identifierContext(_ctx, getState());
        enterRule(_localctx, 90, RULE_matrix_split_identifier);
        try {
            setState(480);
            switch (_input.LA(1)) {
                case FLOAT:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 99:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(476);
                    match(99);
                    setState(477);
                    match(INT);
                    setState(478);
                    match(99);
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(479);
                    match(INT);
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

    public static class Matrix_splits_listContext extends ParserRuleContext {
        public Matrix_splits_listContext matrix_splits_list() {
            return getRuleContext(Matrix_splits_listContext.class, 0);
        }

        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public Matrix_splits_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_splits_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMatrix_splits_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_splits_list(this);
        }
    }

    public final Matrix_splits_listContext matrix_splits_list() throws RecognitionException {
        Matrix_splits_listContext _localctx = new Matrix_splits_listContext(_ctx, getState());
        enterRule(_localctx, 92, RULE_matrix_splits_list);
        try {
            setState(485);
            switch (_input.LA(1)) {
                case 28:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(483);
                    match(INT);
                    setState(484);
                    matrix_splits_list();
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

    public static class Block_quartetsContext extends ParserRuleContext {
        public Matrix_headerContext matrix_header() {
            return getRuleContext(Matrix_headerContext.class, 0);
        }

        public Matrix_quartets_dataContext matrix_quartets_data() {
            return getRuleContext(Matrix_quartets_dataContext.class, 0);
        }

        public Quartets_block_headerContext quartets_block_header() {
            return getRuleContext(Quartets_block_headerContext.class, 0);
        }

        public Block_quartetsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_quartets;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_quartets(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_quartets(this);
        }
    }

    public final Block_quartetsContext block_quartets() throws RecognitionException {
        Block_quartetsContext _localctx = new Block_quartetsContext(_ctx, getState());
        enterRule(_localctx, 94, RULE_block_quartets);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(487);
                quartets_block_header();
                setState(488);
                match(111);
                setState(489);
                matrix_header();
                setState(490);
                matrix_quartets_data();
                setState(491);
                match(111);
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

    public static class Quartets_block_headerContext extends ParserRuleContext {
        public Quartets_block_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_quartets_block_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterQuartets_block_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitQuartets_block_header(this);
        }
    }

    public final Quartets_block_headerContext quartets_block_header() throws RecognitionException {
        Quartets_block_headerContext _localctx = new Quartets_block_headerContext(_ctx, getState());
        enterRule(_localctx, 96, RULE_quartets_block_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(493);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 16) | (1L << 46) | (1L << 56))) != 0) || _la == 108)) {
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

    public static class Matrix_quartets_dataContext extends ParserRuleContext {
        public Matrix_quartetContext matrix_quartet() {
            return getRuleContext(Matrix_quartetContext.class, 0);
        }

        public Matrix_splits_dataContext matrix_splits_data() {
            return getRuleContext(Matrix_splits_dataContext.class, 0);
        }

        public Matrix_quartets_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_quartets_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMatrix_quartets_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_quartets_data(this);
        }
    }

    public final Matrix_quartets_dataContext matrix_quartets_data() throws RecognitionException {
        Matrix_quartets_dataContext _localctx = new Matrix_quartets_dataContext(_ctx, getState());
        enterRule(_localctx, 98, RULE_matrix_quartets_data);
        try {
            setState(500);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(496);
                    matrix_quartet();
                    setState(497);
                    match(28);
                    setState(498);
                    matrix_splits_data();
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

    public static class Matrix_quartetContext extends ParserRuleContext {
        public Label_quartetContext label_quartet() {
            return getRuleContext(Label_quartetContext.class, 0);
        }

        public Sc_quartetContext sc_quartet() {
            return getRuleContext(Sc_quartetContext.class, 0);
        }

        public Y_quartetContext y_quartet() {
            return getRuleContext(Y_quartetContext.class, 0);
        }

        public U_quartetContext u_quartet() {
            return getRuleContext(U_quartetContext.class, 0);
        }

        public X_quartetContext x_quartet() {
            return getRuleContext(X_quartetContext.class, 0);
        }

        public Weight_quartetContext weight_quartet() {
            return getRuleContext(Weight_quartetContext.class, 0);
        }

        public V_quartetContext v_quartet() {
            return getRuleContext(V_quartetContext.class, 0);
        }

        public Cs_quartetContext cs_quartet() {
            return getRuleContext(Cs_quartetContext.class, 0);
        }

        public Matrix_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMatrix_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_quartet(this);
        }
    }

    public final Matrix_quartetContext matrix_quartet() throws RecognitionException {
        Matrix_quartetContext _localctx = new Matrix_quartetContext(_ctx, getState());
        enterRule(_localctx, 100, RULE_matrix_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(502);
                label_quartet();
                setState(503);
                weight_quartet();
                setState(504);
                x_quartet();
                setState(505);
                y_quartet();
                setState(506);
                sc_quartet();
                setState(507);
                u_quartet();
                setState(508);
                cs_quartet();
                setState(509);
                v_quartet();
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

    public static class Label_quartetContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Label_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_label_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterLabel_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitLabel_quartet(this);
        }
    }

    public final Label_quartetContext label_quartet() throws RecognitionException {
        Label_quartetContext _localctx = new Label_quartetContext(_ctx, getState());
        enterRule(_localctx, 102, RULE_label_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(511);
                match(IDENTIFIER);
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

    public static class Weight_quartetContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public Weight_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weight_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterWeight_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitWeight_quartet(this);
        }
    }

    public final Weight_quartetContext weight_quartet() throws RecognitionException {
        Weight_quartetContext _localctx = new Weight_quartetContext(_ctx, getState());
        enterRule(_localctx, 104, RULE_weight_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(513);
                match(INT);
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

    public static class X_quartetContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public X_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_x_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterX_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitX_quartet(this);
        }
    }

    public final X_quartetContext x_quartet() throws RecognitionException {
        X_quartetContext _localctx = new X_quartetContext(_ctx, getState());
        enterRule(_localctx, 106, RULE_x_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(515);
                match(INT);
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

    public static class Y_quartetContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public Y_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_y_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterY_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitY_quartet(this);
        }
    }

    public final Y_quartetContext y_quartet() throws RecognitionException {
        Y_quartetContext _localctx = new Y_quartetContext(_ctx, getState());
        enterRule(_localctx, 108, RULE_y_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(517);
                match(INT);
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

    public static class U_quartetContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public U_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_u_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterU_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitU_quartet(this);
        }
    }

    public final U_quartetContext u_quartet() throws RecognitionException {
        U_quartetContext _localctx = new U_quartetContext(_ctx, getState());
        enterRule(_localctx, 110, RULE_u_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(519);
                match(INT);
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

    public static class V_quartetContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public V_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_v_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterV_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitV_quartet(this);
        }
    }

    public final V_quartetContext v_quartet() throws RecognitionException {
        V_quartetContext _localctx = new V_quartetContext(_ctx, getState());
        enterRule(_localctx, 112, RULE_v_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(521);
                match(INT);
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

    public static class Sc_quartetContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Sc_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sc_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterSc_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitSc_quartet(this);
        }
    }

    public final Sc_quartetContext sc_quartet() throws RecognitionException {
        Sc_quartetContext _localctx = new Sc_quartetContext(_ctx, getState());
        enterRule(_localctx, 114, RULE_sc_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(523);
                match(IDENTIFIER);
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

    public static class Cs_quartetContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Cs_quartetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cs_quartet;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterCs_quartet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitCs_quartet(this);
        }
    }

    public final Cs_quartetContext cs_quartet() throws RecognitionException {
        Cs_quartetContext _localctx = new Cs_quartetContext(_ctx, getState());
        enterRule(_localctx, 116, RULE_cs_quartet);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(525);
                match(IDENTIFIER);
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

    public static class Block_assumptionsContext extends ParserRuleContext {
        public Assumptions_block_headerContext assumptions_block_header() {
            return getRuleContext(Assumptions_block_headerContext.class, 0);
        }

        public Assumptions_dataContext assumptions_data() {
            return getRuleContext(Assumptions_dataContext.class, 0);
        }

        public Block_assumptionsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_assumptions;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_assumptions(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_assumptions(this);
        }
    }

    public final Block_assumptionsContext block_assumptions() throws RecognitionException {
        Block_assumptionsContext _localctx = new Block_assumptionsContext(_ctx, getState());
        enterRule(_localctx, 118, RULE_block_assumptions);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(527);
                assumptions_block_header();
                setState(528);
                match(111);
                setState(529);
                assumptions_data();
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

    public static class Assumptions_block_headerContext extends ParserRuleContext {
        public Assumptions_block_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_assumptions_block_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener)
                ((NexusFileListener) listener).enterAssumptions_block_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener)
                ((NexusFileListener) listener).exitAssumptions_block_header(this);
        }
    }

    public final Assumptions_block_headerContext assumptions_block_header() throws RecognitionException {
        Assumptions_block_headerContext _localctx = new Assumptions_block_headerContext(_ctx, getState());
        enterRule(_localctx, 120, RULE_assumptions_block_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(531);
                _la = _input.LA(1);
                if (!(((((_la - 21)) & ~0x3f) == 0 && ((1L << (_la - 21)) & ((1L << (21 - 21)) | (1L << (40 - 21)) | (1L << (70 - 21)) | (1L << (83 - 21)))) != 0))) {
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

    public static class Assumptions_dataContext extends ParserRuleContext {
        public Assumptions_data_entryContext assumptions_data_entry() {
            return getRuleContext(Assumptions_data_entryContext.class, 0);
        }

        public Assumptions_dataContext assumptions_data() {
            return getRuleContext(Assumptions_dataContext.class, 0);
        }

        public Assumptions_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_assumptions_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterAssumptions_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitAssumptions_data(this);
        }
    }

    public final Assumptions_dataContext assumptions_data() throws RecognitionException {
        Assumptions_dataContext _localctx = new Assumptions_dataContext(_ctx, getState());
        enterRule(_localctx, 122, RULE_assumptions_data);
        try {
            setState(538);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(534);
                    assumptions_data_entry();
                    setState(535);
                    match(111);
                    setState(536);
                    assumptions_data();
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

    public static class Assumptions_data_entryContext extends ParserRuleContext {
        public Key_value_pairsContext key_value_pairs() {
            return getRuleContext(Key_value_pairsContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Key_value_pairContext key_value_pair() {
            return getRuleContext(Key_value_pairContext.class, 0);
        }

        public Assumptions_data_entryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_assumptions_data_entry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterAssumptions_data_entry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitAssumptions_data_entry(this);
        }
    }

    public final Assumptions_data_entryContext assumptions_data_entry() throws RecognitionException {
        Assumptions_data_entryContext _localctx = new Assumptions_data_entryContext(_ctx, getState());
        enterRule(_localctx, 124, RULE_assumptions_data_entry);
        try {
            setState(544);
            switch (getInterpreter().adaptivePredict(_input, 23, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(540);
                    match(IDENTIFIER);
                    setState(541);
                    key_value_pairs();
                }
                break;

                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(542);
                    key_value_pair();
                }
                break;

                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(543);
                    match(IDENTIFIER);
                }
                break;
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

    public static class Key_value_pairsContext extends ParserRuleContext {
        public Key_value_pairsContext key_value_pairs() {
            return getRuleContext(Key_value_pairsContext.class, 0);
        }

        public Key_value_pairContext key_value_pair() {
            return getRuleContext(Key_value_pairContext.class, 0);
        }

        public Key_value_pairsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_key_value_pairs;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterKey_value_pairs(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitKey_value_pairs(this);
        }
    }

    public final Key_value_pairsContext key_value_pairs() throws RecognitionException {
        Key_value_pairsContext _localctx = new Key_value_pairsContext(_ctx, getState());
        enterRule(_localctx, 126, RULE_key_value_pairs);
        try {
            setState(550);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(547);
                    key_value_pair();
                    setState(548);
                    key_value_pairs();
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

    public static class Key_value_pairContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(NexusFileParser.IDENTIFIER, i);
        }

        public List<TerminalNode> IDENTIFIER() {
            return getTokens(NexusFileParser.IDENTIFIER);
        }

        public Key_value_pairContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_key_value_pair;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterKey_value_pair(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitKey_value_pair(this);
        }
    }

    public final Key_value_pairContext key_value_pair() throws RecognitionException {
        Key_value_pairContext _localctx = new Key_value_pairContext(_ctx, getState());
        enterRule(_localctx, 128, RULE_key_value_pair);
        try {
            setState(558);
            switch (getInterpreter().adaptivePredict(_input, 25, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(552);
                    match(IDENTIFIER);
                    setState(553);
                    match(78);
                    setState(554);
                    match(IDENTIFIER);
                }
                break;

                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(555);
                    match(IDENTIFIER);
                    setState(556);
                    match(78);
                    setState(557);
                    match(INT);
                }
                break;
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

    public static class Block_treesContext extends ParserRuleContext {
        public TranslateContext translate() {
            return getRuleContext(TranslateContext.class, 0);
        }

        public Newick_treeContext newick_tree() {
            return getRuleContext(Newick_treeContext.class, 0);
        }

        public Tree_block_headerContext tree_block_header() {
            return getRuleContext(Tree_block_headerContext.class, 0);
        }

        public Block_treesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_trees;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_trees(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_trees(this);
        }
    }

    public final Block_treesContext block_trees() throws RecognitionException {
        Block_treesContext _localctx = new Block_treesContext(_ctx, getState());
        enterRule(_localctx, 130, RULE_block_trees);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(560);
                tree_block_header();
                setState(561);
                match(111);
                setState(562);
                translate();
                setState(563);
                newick_tree();
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

    public static class Tree_block_headerContext extends ParserRuleContext {
        public Tree_block_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tree_block_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTree_block_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTree_block_header(this);
        }
    }

    public final Tree_block_headerContext tree_block_header() throws RecognitionException {
        Tree_block_headerContext _localctx = new Tree_block_headerContext(_ctx, getState());
        enterRule(_localctx, 132, RULE_tree_block_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(565);
                _la = _input.LA(1);
                if (!(_la == 2 || _la == 14 || _la == 66)) {
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

    public static class TranslateContext extends ParserRuleContext {
        public Translate_listContext translate_list() {
            return getRuleContext(Translate_listContext.class, 0);
        }

        public ReferenceContext reference(int i) {
            return getRuleContext(ReferenceContext.class, i);
        }

        public Translate_headerContext translate_header() {
            return getRuleContext(Translate_headerContext.class, 0);
        }

        public List<ReferenceContext> reference() {
            return getRuleContexts(ReferenceContext.class);
        }

        public TranslateContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_translate;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTranslate(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTranslate(this);
        }
    }

    public final TranslateContext translate() throws RecognitionException {
        TranslateContext _localctx = new TranslateContext(_ctx, getState());
        enterRule(_localctx, 134, RULE_translate);
        try {
            setState(574);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 32:
                case 54:
                case 69:
                case 114:
                case 115:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 51:
                case 104:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(568);
                    translate_header();
                    setState(569);
                    reference();
                    setState(570);
                    reference();
                    setState(571);
                    translate_list();
                    setState(572);
                    match(111);
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

    public static class Translate_headerContext extends ParserRuleContext {
        public Translate_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_translate_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTranslate_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTranslate_header(this);
        }
    }

    public final Translate_headerContext translate_header() throws RecognitionException {
        Translate_headerContext _localctx = new Translate_headerContext(_ctx, getState());
        enterRule(_localctx, 136, RULE_translate_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(576);
                _la = _input.LA(1);
                if (!(_la == 51 || _la == 104)) {
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

    public static class Translate_listContext extends ParserRuleContext {
        public Translate_listContext translate_list() {
            return getRuleContext(Translate_listContext.class, 0);
        }

        public ReferenceContext reference(int i) {
            return getRuleContext(ReferenceContext.class, i);
        }

        public List<ReferenceContext> reference() {
            return getRuleContexts(ReferenceContext.class);
        }

        public Translate_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_translate_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTranslate_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTranslate_list(this);
        }
    }

    public final Translate_listContext translate_list() throws RecognitionException {
        Translate_listContext _localctx = new Translate_listContext(_ctx, getState());
        enterRule(_localctx, 138, RULE_translate_list);
        try {
            setState(584);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 28:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(579);
                    match(28);
                    setState(580);
                    reference();
                    setState(581);
                    reference();
                    setState(582);
                    translate_list();
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

    public static class Newick_treeContext extends ParserRuleContext {
        public Tree_restContext tree_rest() {
            return getRuleContext(Tree_restContext.class, 0);
        }

        public Tree_headerContext tree_header() {
            return getRuleContext(Tree_headerContext.class, 0);
        }

        public Newick_treeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_newick_tree;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNewick_tree(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNewick_tree(this);
        }
    }

    public final Newick_treeContext newick_tree() throws RecognitionException {
        Newick_treeContext _localctx = new Newick_treeContext(_ctx, getState());
        enterRule(_localctx, 140, RULE_newick_tree);
        try {
            setState(590);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 32:
                case 54:
                case 69:
                case 115:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(587);
                    tree_header();
                    setState(588);
                    tree_rest();
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

    public static class Tree_headerContext extends ParserRuleContext {
        public Tree_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tree_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTree_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTree_header(this);
        }
    }

    public final Tree_headerContext tree_header() throws RecognitionException {
        Tree_headerContext _localctx = new Tree_headerContext(_ctx, getState());
        enterRule(_localctx, 142, RULE_tree_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(592);
                _la = _input.LA(1);
                if (!(_la == 32 || _la == 54 || _la == 69 || _la == 115)) {
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

    public static class Tree_restContext extends ParserRuleContext {
        public Newick_treeContext newick_tree() {
            return getRuleContext(Newick_treeContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public RootContext root() {
            return getRuleContext(RootContext.class, 0);
        }

        public StarContext star() {
            return getRuleContext(StarContext.class, 0);
        }

        public Tree_definitionContext tree_definition() {
            return getRuleContext(Tree_definitionContext.class, 0);
        }

        public Tree_restContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tree_rest;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTree_rest(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTree_rest(this);
        }
    }

    public final Tree_restContext tree_rest() throws RecognitionException {
        Tree_restContext _localctx = new Tree_restContext(_ctx, getState());
        enterRule(_localctx, 144, RULE_tree_rest);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(594);
                star();
                setState(595);
                match(IDENTIFIER);
                setState(596);
                match(78);
                setState(597);
                root();
                setState(598);
                tree_definition();
                setState(599);
                match(111);
                setState(600);
                newick_tree();
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

    public static class Tree_definitionContext extends ParserRuleContext {
        public Tree_labelContext tree_label() {
            return getRuleContext(Tree_labelContext.class, 0);
        }

        public Tree_listContext tree_list() {
            return getRuleContext(Tree_listContext.class, 0);
        }

        public Tree_label_optionalContext tree_label_optional() {
            return getRuleContext(Tree_label_optionalContext.class, 0);
        }

        public Tree_definitionContext tree_definition() {
            return getRuleContext(Tree_definitionContext.class, 0);
        }

        public Tree_definitionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tree_definition;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTree_definition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTree_definition(this);
        }
    }

    public final Tree_definitionContext tree_definition() throws RecognitionException {
        Tree_definitionContext _localctx = new Tree_definitionContext(_ctx, getState());
        enterRule(_localctx, 146, RULE_tree_definition);
        try {
            setState(609);
            switch (_input.LA(1)) {
                case 93:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(602);
                    match(93);
                    setState(603);
                    tree_definition();
                    setState(604);
                    tree_list();
                    setState(605);
                    match(15);
                    setState(606);
                    tree_label_optional();
                }
                break;
                case INT:
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(608);
                    tree_label();
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

    public static class RootContext extends ParserRuleContext {
        public RootContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_root;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterRoot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitRoot(this);
        }
    }

    public final RootContext root() throws RecognitionException {
        RootContext _localctx = new RootContext(_ctx, getState());
        enterRule(_localctx, 148, RULE_root);
        try {
            setState(614);
            switch (_input.LA(1)) {
                case 93:
                case INT:
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 17:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(612);
                    match(17);
                }
                break;
                case 25:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(613);
                    match(25);
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

    public static class Tree_listContext extends ParserRuleContext {
        public Tree_listContext tree_list() {
            return getRuleContext(Tree_listContext.class, 0);
        }

        public Tree_definitionContext tree_definition() {
            return getRuleContext(Tree_definitionContext.class, 0);
        }

        public Tree_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tree_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTree_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTree_list(this);
        }
    }

    public final Tree_listContext tree_list() throws RecognitionException {
        Tree_listContext _localctx = new Tree_listContext(_ctx, getState());
        enterRule(_localctx, 150, RULE_tree_list);
        try {
            setState(621);
            switch (_input.LA(1)) {
                case 15:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 28:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(617);
                    match(28);
                    setState(618);
                    tree_definition();
                    setState(619);
                    tree_list();
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

    public static class Tree_labelContext extends ParserRuleContext {
        public LengthContext length() {
            return getRuleContext(LengthContext.class, 0);
        }

        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Tree_labelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tree_label;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTree_label(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTree_label(this);
        }
    }

    public final Tree_labelContext tree_label() throws RecognitionException {
        Tree_labelContext _localctx = new Tree_labelContext(_ctx, getState());
        enterRule(_localctx, 152, RULE_tree_label);
        try {
            setState(627);
            switch (_input.LA(1)) {
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(623);
                    match(IDENTIFIER);
                    setState(624);
                    length();
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(625);
                    match(INT);
                    setState(626);
                    length();
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

    public static class Tree_label_optionalContext extends ParserRuleContext {
        public Tree_labelContext tree_label() {
            return getRuleContext(Tree_labelContext.class, 0);
        }

        public Tree_label_optionalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tree_label_optional;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTree_label_optional(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTree_label_optional(this);
        }
    }

    public final Tree_label_optionalContext tree_label_optional() throws RecognitionException {
        Tree_label_optionalContext _localctx = new Tree_label_optionalContext(_ctx, getState());
        enterRule(_localctx, 154, RULE_tree_label_optional);
        try {
            setState(631);
            switch (_input.LA(1)) {
                case 15:
                case 28:
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(630);
                    tree_label();
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
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
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
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterLength(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitLength(this);
        }
    }

    public final LengthContext length() throws RecognitionException {
        LengthContext _localctx = new LengthContext(_ctx, getState());
        enterRule(_localctx, 156, RULE_length);
        try {
            setState(636);
            switch (_input.LA(1)) {
                case 15:
                case 28:
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 92:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(634);
                    match(92);
                    setState(635);
                    match(INT);
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

    public static class Block_networkContext extends ParserRuleContext {
        public Edges_networkContext edges_network() {
            return getRuleContext(Edges_networkContext.class, 0);
        }

        public Vlabels_networkContext vlabels_network() {
            return getRuleContext(Vlabels_networkContext.class, 0);
        }

        public Vertices_networkContext vertices_network() {
            return getRuleContext(Vertices_networkContext.class, 0);
        }

        public Dimensions_networkContext dimensions_network() {
            return getRuleContext(Dimensions_networkContext.class, 0);
        }

        public Draw_networkContext draw_network() {
            return getRuleContext(Draw_networkContext.class, 0);
        }

        public Translate_networkContext translate_network() {
            return getRuleContext(Translate_networkContext.class, 0);
        }

        public Network_block_headerContext network_block_header() {
            return getRuleContext(Network_block_headerContext.class, 0);
        }

        public Block_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBlock_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBlock_network(this);
        }
    }

    public final Block_networkContext block_network() throws RecognitionException {
        Block_networkContext _localctx = new Block_networkContext(_ctx, getState());
        enterRule(_localctx, 158, RULE_block_network);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(638);
                network_block_header();
                setState(639);
                match(111);
                setState(640);
                dimensions_network();
                setState(641);
                draw_network();
                setState(642);
                translate_network();
                setState(643);
                vertices_network();
                setState(644);
                vlabels_network();
                setState(645);
                edges_network();
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

    public static class Network_block_headerContext extends ParserRuleContext {
        public Network_block_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_network_block_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNetwork_block_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNetwork_block_header(this);
        }
    }

    public final Network_block_headerContext network_block_header() throws RecognitionException {
        Network_block_headerContext _localctx = new Network_block_headerContext(_ctx, getState());
        enterRule(_localctx, 160, RULE_network_block_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(647);
                _la = _input.LA(1);
                if (!(_la == 1 || _la == 112 || _la == 119)) {
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

    public static class Dimensions_networkContext extends ParserRuleContext {
        public NverticesContext nvertices() {
            return getRuleContext(NverticesContext.class, 0);
        }

        public NtaxContext ntax() {
            return getRuleContext(NtaxContext.class, 0);
        }

        public NedgesContext nedges() {
            return getRuleContext(NedgesContext.class, 0);
        }

        public DimensionsContext dimensions() {
            return getRuleContext(DimensionsContext.class, 0);
        }

        public Dimensions_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dimensions_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDimensions_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDimensions_network(this);
        }
    }

    public final Dimensions_networkContext dimensions_network() throws RecognitionException {
        Dimensions_networkContext _localctx = new Dimensions_networkContext(_ctx, getState());
        enterRule(_localctx, 162, RULE_dimensions_network);
        try {
            setState(656);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 33:
                case 36:
                case 43:
                case 45:
                case 48:
                case 51:
                case 58:
                case 61:
                case 62:
                case 75:
                case 98:
                case 100:
                case 104:
                case 107:
                case 110:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 63:
                case 117:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(650);
                    dimensions();
                    setState(651);
                    ntax();
                    setState(652);
                    nvertices();
                    setState(653);
                    nedges();
                    setState(654);
                    match(111);
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

    public static class Draw_networkContext extends ParserRuleContext {
        public Draw_network_headerContext draw_network_header() {
            return getRuleContext(Draw_network_headerContext.class, 0);
        }

        public Draw_network_optionsContext draw_network_options() {
            return getRuleContext(Draw_network_optionsContext.class, 0);
        }

        public Draw_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_draw_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDraw_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDraw_network(this);
        }
    }

    public final Draw_networkContext draw_network() throws RecognitionException {
        Draw_networkContext _localctx = new Draw_networkContext(_ctx, getState());
        enterRule(_localctx, 164, RULE_draw_network);
        try {
            setState(663);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 33:
                case 43:
                case 45:
                case 48:
                case 51:
                case 58:
                case 61:
                case 75:
                case 98:
                case 104:
                case 107:
                case 110:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 36:
                case 62:
                case 100:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(659);
                    draw_network_header();
                    setState(660);
                    draw_network_options();
                    setState(661);
                    match(111);
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

    public static class Draw_network_headerContext extends ParserRuleContext {
        public Draw_network_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_draw_network_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDraw_network_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDraw_network_header(this);
        }
    }

    public final Draw_network_headerContext draw_network_header() throws RecognitionException {
        Draw_network_headerContext _localctx = new Draw_network_headerContext(_ctx, getState());
        enterRule(_localctx, 166, RULE_draw_network_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(665);
                _la = _input.LA(1);
                if (!(_la == 36 || _la == 62 || _la == 100)) {
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

    public static class Draw_network_optionsContext extends ParserRuleContext {
        public Draw_network_optionContext draw_network_option() {
            return getRuleContext(Draw_network_optionContext.class, 0);
        }

        public Draw_network_optionsContext draw_network_options() {
            return getRuleContext(Draw_network_optionsContext.class, 0);
        }

        public Draw_network_optionsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_draw_network_options;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDraw_network_options(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDraw_network_options(this);
        }
    }

    public final Draw_network_optionsContext draw_network_options() throws RecognitionException {
        Draw_network_optionsContext _localctx = new Draw_network_optionsContext(_ctx, getState());
        enterRule(_localctx, 168, RULE_draw_network_options);
        try {
            setState(671);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 88:
                case 105:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(668);
                    draw_network_option();
                    setState(669);
                    draw_network_options();
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

    public static class Draw_network_optionContext extends ParserRuleContext {
        public Rotate_networkContext rotate_network() {
            return getRuleContext(Rotate_networkContext.class, 0);
        }

        public Scale_networkContext scale_network() {
            return getRuleContext(Scale_networkContext.class, 0);
        }

        public Draw_network_optionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_draw_network_option;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDraw_network_option(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDraw_network_option(this);
        }
    }

    public final Draw_network_optionContext draw_network_option() throws RecognitionException {
        Draw_network_optionContext _localctx = new Draw_network_optionContext(_ctx, getState());
        enterRule(_localctx, 170, RULE_draw_network_option);
        try {
            setState(675);
            switch (_input.LA(1)) {
                case 105:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(673);
                    scale_network();
                }
                break;
                case 88:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(674);
                    rotate_network();
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

    public static class Scale_networkContext extends ParserRuleContext {
        public Scale_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_scale_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterScale_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitScale_network(this);
        }
    }

    public final Scale_networkContext scale_network() throws RecognitionException {
        Scale_networkContext _localctx = new Scale_networkContext(_ctx, getState());
        enterRule(_localctx, 172, RULE_scale_network);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(677);
                match(105);
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

    public static class Rotate_networkContext extends ParserRuleContext {
        public TerminalNode FLOAT() {
            return getToken(NexusFileParser.FLOAT, 0);
        }

        public Rotate_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_rotate_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterRotate_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitRotate_network(this);
        }
    }

    public final Rotate_networkContext rotate_network() throws RecognitionException {
        Rotate_networkContext _localctx = new Rotate_networkContext(_ctx, getState());
        enterRule(_localctx, 174, RULE_rotate_network);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(679);
                match(88);
                setState(680);
                match(78);
                setState(681);
                match(FLOAT);
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

    public static class Translate_networkContext extends ParserRuleContext {
        public Translate_network_dataContext translate_network_data() {
            return getRuleContext(Translate_network_dataContext.class, 0);
        }

        public Translate_network_headerContext translate_network_header() {
            return getRuleContext(Translate_network_headerContext.class, 0);
        }

        public Translate_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_translate_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTranslate_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTranslate_network(this);
        }
    }

    public final Translate_networkContext translate_network() throws RecognitionException {
        Translate_networkContext _localctx = new Translate_networkContext(_ctx, getState());
        enterRule(_localctx, 176, RULE_translate_network);
        try {
            setState(688);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 33:
                case 43:
                case 45:
                case 48:
                case 58:
                case 61:
                case 98:
                case 107:
                case 110:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 51:
                case 75:
                case 104:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(684);
                    translate_network_header();
                    setState(685);
                    translate_network_data();
                    setState(686);
                    match(111);
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

    public static class Translate_network_headerContext extends ParserRuleContext {
        public Translate_network_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_translate_network_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener)
                ((NexusFileListener) listener).enterTranslate_network_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener)
                ((NexusFileListener) listener).exitTranslate_network_header(this);
        }
    }

    public final Translate_network_headerContext translate_network_header() throws RecognitionException {
        Translate_network_headerContext _localctx = new Translate_network_headerContext(_ctx, getState());
        enterRule(_localctx, 178, RULE_translate_network_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(690);
                _la = _input.LA(1);
                if (!(((((_la - 51)) & ~0x3f) == 0 && ((1L << (_la - 51)) & ((1L << (51 - 51)) | (1L << (75 - 51)) | (1L << (104 - 51)))) != 0))) {
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

    public static class Translate_network_dataContext extends ParserRuleContext {
        public Translate_network_dataContext translate_network_data() {
            return getRuleContext(Translate_network_dataContext.class, 0);
        }

        public Translate_network_entryContext translate_network_entry() {
            return getRuleContext(Translate_network_entryContext.class, 0);
        }

        public Translate_network_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_translate_network_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTranslate_network_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTranslate_network_data(this);
        }
    }

    public final Translate_network_dataContext translate_network_data() throws RecognitionException {
        Translate_network_dataContext _localctx = new Translate_network_dataContext(_ctx, getState());
        enterRule(_localctx, 180, RULE_translate_network_data);
        try {
            setState(697);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(693);
                    translate_network_entry();
                    setState(694);
                    match(28);
                    setState(695);
                    translate_network_data();
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

    public static class Translate_network_entryContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Translate_network_entryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_translate_network_entry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener)
                ((NexusFileListener) listener).enterTranslate_network_entry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTranslate_network_entry(this);
        }
    }

    public final Translate_network_entryContext translate_network_entry() throws RecognitionException {
        Translate_network_entryContext _localctx = new Translate_network_entryContext(_ctx, getState());
        enterRule(_localctx, 182, RULE_translate_network_entry);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(699);
                match(INT);
                setState(700);
                match(99);
                setState(701);
                match(IDENTIFIER);
                setState(702);
                match(99);
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

    public static class Vertices_networkContext extends ParserRuleContext {
        public Vertices_network_dataContext vertices_network_data() {
            return getRuleContext(Vertices_network_dataContext.class, 0);
        }

        public Vertices_network_headerContext vertices_network_header() {
            return getRuleContext(Vertices_network_headerContext.class, 0);
        }

        public Vertices_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vertices_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVertices_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVertices_network(this);
        }
    }

    public final Vertices_networkContext vertices_network() throws RecognitionException {
        Vertices_networkContext _localctx = new Vertices_networkContext(_ctx, getState());
        enterRule(_localctx, 184, RULE_vertices_network);
        try {
            setState(709);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 33:
                case 45:
                case 48:
                case 61:
                case 107:
                case 110:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 43:
                case 58:
                case 98:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(705);
                    vertices_network_header();
                    setState(706);
                    vertices_network_data();
                    setState(707);
                    match(111);
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

    public static class Vertices_network_headerContext extends ParserRuleContext {
        public Vertices_network_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vertices_network_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener)
                ((NexusFileListener) listener).enterVertices_network_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVertices_network_header(this);
        }
    }

    public final Vertices_network_headerContext vertices_network_header() throws RecognitionException {
        Vertices_network_headerContext _localctx = new Vertices_network_headerContext(_ctx, getState());
        enterRule(_localctx, 186, RULE_vertices_network_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(711);
                _la = _input.LA(1);
                if (!(((((_la - 43)) & ~0x3f) == 0 && ((1L << (_la - 43)) & ((1L << (43 - 43)) | (1L << (58 - 43)) | (1L << (98 - 43)))) != 0))) {
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

    public static class Vertices_network_dataContext extends ParserRuleContext {
        public Vertices_network_dataContext vertices_network_data() {
            return getRuleContext(Vertices_network_dataContext.class, 0);
        }

        public Vertices_network_entryContext vertices_network_entry() {
            return getRuleContext(Vertices_network_entryContext.class, 0);
        }

        public Vertices_network_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vertices_network_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVertices_network_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVertices_network_data(this);
        }
    }

    public final Vertices_network_dataContext vertices_network_data() throws RecognitionException {
        Vertices_network_dataContext _localctx = new Vertices_network_dataContext(_ctx, getState());
        enterRule(_localctx, 188, RULE_vertices_network_data);
        try {
            setState(718);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(714);
                    vertices_network_entry();
                    setState(715);
                    match(28);
                    setState(716);
                    vertices_network_data();
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

    public static class Vertices_network_entryContext extends ParserRuleContext {
        public List<TerminalNode> FLOAT() {
            return getTokens(NexusFileParser.FLOAT);
        }

        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public TerminalNode FLOAT(int i) {
            return getToken(NexusFileParser.FLOAT, i);
        }

        public Vertices_2d_dataContext vertices_2d_data() {
            return getRuleContext(Vertices_2d_dataContext.class, 0);
        }

        public Vertices_3d_dataContext vertices_3d_data() {
            return getRuleContext(Vertices_3d_dataContext.class, 0);
        }

        public Vertices_network_entryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vertices_network_entry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVertices_network_entry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVertices_network_entry(this);
        }
    }

    public final Vertices_network_entryContext vertices_network_entry() throws RecognitionException {
        Vertices_network_entryContext _localctx = new Vertices_network_entryContext(_ctx, getState());
        enterRule(_localctx, 190, RULE_vertices_network_entry);
        try {
            setState(728);
            switch (getInterpreter().adaptivePredict(_input, 43, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(720);
                    match(INT);
                    setState(721);
                    match(FLOAT);
                    setState(722);
                    match(FLOAT);
                    setState(723);
                    vertices_2d_data();
                }
                break;

                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(724);
                    match(INT);
                    setState(725);
                    match(FLOAT);
                    setState(726);
                    match(FLOAT);
                    setState(727);
                    vertices_3d_data();
                }
                break;
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

    public static class Vertices_2d_dataContext extends ParserRuleContext {
        public List<TerminalNode> INT() {
            return getTokens(NexusFileParser.INT);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public TerminalNode INT(int i) {
            return getToken(NexusFileParser.INT, i);
        }

        public Vertices_2d_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vertices_2d_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVertices_2d_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVertices_2d_data(this);
        }
    }

    public final Vertices_2d_dataContext vertices_2d_data() throws RecognitionException {
        Vertices_2d_dataContext _localctx = new Vertices_2d_dataContext(_ctx, getState());
        enterRule(_localctx, 192, RULE_vertices_2d_data);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(730);
                match(95);
                setState(731);
                match(78);
                setState(732);
                match(IDENTIFIER);
                setState(733);
                match(22);
                setState(734);
                match(78);
                setState(735);
                match(INT);
                setState(736);
                match(INT);
                setState(737);
                match(INT);
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

    public static class Vertices_3d_dataContext extends ParserRuleContext {
        public List<TerminalNode> INT() {
            return getTokens(NexusFileParser.INT);
        }

        public TerminalNode INT(int i) {
            return getToken(NexusFileParser.INT, i);
        }

        public Vertices_3d_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vertices_3d_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVertices_3d_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVertices_3d_data(this);
        }
    }

    public final Vertices_3d_dataContext vertices_3d_data() throws RecognitionException {
        Vertices_3d_dataContext _localctx = new Vertices_3d_dataContext(_ctx, getState());
        enterRule(_localctx, 194, RULE_vertices_3d_data);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(739);
                match(30);
                setState(740);
                match(78);
                setState(741);
                match(INT);
                setState(742);
                match(12);
                setState(743);
                match(78);
                setState(744);
                match(INT);
                setState(745);
                match(22);
                setState(746);
                match(78);
                setState(747);
                match(INT);
                setState(748);
                match(INT);
                setState(749);
                match(INT);
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

    public static class Vlabels_networkContext extends ParserRuleContext {
        public Vlabels_network_headerContext vlabels_network_header() {
            return getRuleContext(Vlabels_network_headerContext.class, 0);
        }

        public Vlabels_network_dataContext vlabels_network_data() {
            return getRuleContext(Vlabels_network_dataContext.class, 0);
        }

        public Vlabels_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vlabels_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVlabels_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVlabels_network(this);
        }
    }

    public final Vlabels_networkContext vlabels_network() throws RecognitionException {
        Vlabels_networkContext _localctx = new Vlabels_networkContext(_ctx, getState());
        enterRule(_localctx, 196, RULE_vlabels_network);
        try {
            setState(756);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 48:
                case 61:
                case 110:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 33:
                case 45:
                case 107:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(752);
                    vlabels_network_header();
                    setState(753);
                    vlabels_network_data();
                    setState(754);
                    match(111);
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

    public static class Vlabels_network_headerContext extends ParserRuleContext {
        public Vlabels_network_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vlabels_network_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVlabels_network_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVlabels_network_header(this);
        }
    }

    public final Vlabels_network_headerContext vlabels_network_header() throws RecognitionException {
        Vlabels_network_headerContext _localctx = new Vlabels_network_headerContext(_ctx, getState());
        enterRule(_localctx, 198, RULE_vlabels_network_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(758);
                _la = _input.LA(1);
                if (!(_la == 33 || _la == 45 || _la == 107)) {
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

    public static class Vlabels_network_dataContext extends ParserRuleContext {
        public Vlabels_network_entryContext vlabels_network_entry() {
            return getRuleContext(Vlabels_network_entryContext.class, 0);
        }

        public Vlabels_network_dataContext vlabels_network_data() {
            return getRuleContext(Vlabels_network_dataContext.class, 0);
        }

        public Vlabels_network_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vlabels_network_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVlabels_network_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVlabels_network_data(this);
        }
    }

    public final Vlabels_network_dataContext vlabels_network_data() throws RecognitionException {
        Vlabels_network_dataContext _localctx = new Vlabels_network_dataContext(_ctx, getState());
        enterRule(_localctx, 200, RULE_vlabels_network_data);
        try {
            setState(765);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(761);
                    vlabels_network_entry();
                    setState(762);
                    match(28);
                    setState(763);
                    vlabels_network_data();
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

    public static class Vlabels_network_entryContext extends ParserRuleContext {
        public Vlabels_dataContext vlabels_data() {
            return getRuleContext(Vlabels_dataContext.class, 0);
        }

        public Vlabels_network_labelContext vlabels_network_label() {
            return getRuleContext(Vlabels_network_labelContext.class, 0);
        }

        public Vlabels_network_entryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vlabels_network_entry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVlabels_network_entry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVlabels_network_entry(this);
        }
    }

    public final Vlabels_network_entryContext vlabels_network_entry() throws RecognitionException {
        Vlabels_network_entryContext _localctx = new Vlabels_network_entryContext(_ctx, getState());
        enterRule(_localctx, 202, RULE_vlabels_network_entry);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(767);
                vlabels_network_label();
                setState(768);
                vlabels_data();
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

    public static class Vlabels_network_labelContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Vlabels_network_labelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vlabels_network_label;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVlabels_network_label(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVlabels_network_label(this);
        }
    }

    public final Vlabels_network_labelContext vlabels_network_label() throws RecognitionException {
        Vlabels_network_labelContext _localctx = new Vlabels_network_labelContext(_ctx, getState());
        enterRule(_localctx, 204, RULE_vlabels_network_label);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(770);
                match(INT);
                setState(771);
                match(99);
                setState(772);
                match(IDENTIFIER);
                setState(773);
                match(99);
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

    public static class Vlabels_dataContext extends ParserRuleContext {
        public List<TerminalNode> INT() {
            return getTokens(NexusFileParser.INT);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public TerminalNode INT(int i) {
            return getToken(NexusFileParser.INT, i);
        }

        public Vlabels_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vlabels_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterVlabels_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitVlabels_data(this);
        }
    }

    public final Vlabels_dataContext vlabels_data() throws RecognitionException {
        Vlabels_dataContext _localctx = new Vlabels_dataContext(_ctx, getState());
        enterRule(_localctx, 206, RULE_vlabels_data);
        try {
            setState(800);
            switch (_input.LA(1)) {
                case 59:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(775);
                    match(59);
                    setState(776);
                    match(78);
                    setState(777);
                    match(INT);
                    setState(778);
                    match(80);
                    setState(779);
                    match(78);
                    setState(780);
                    match(INT);
                    setState(781);
                    match(60);
                    setState(782);
                    match(78);
                    setState(783);
                    match(INT);
                    setState(784);
                    match(97);
                    setState(785);
                    match(78);
                    setState(786);
                    match(99);
                    setState(787);
                    match(IDENTIFIER);
                    setState(788);
                    match(99);
                }
                break;
                case 80:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(789);
                    match(80);
                    setState(790);
                    match(78);
                    setState(791);
                    match(INT);
                    setState(792);
                    match(60);
                    setState(793);
                    match(78);
                    setState(794);
                    match(INT);
                    setState(795);
                    match(97);
                    setState(796);
                    match(78);
                    setState(797);
                    match(99);
                    setState(798);
                    match(IDENTIFIER);
                    setState(799);
                    match(99);
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

    public static class Edges_networkContext extends ParserRuleContext {
        public Edges_network_dataContext edges_network_data() {
            return getRuleContext(Edges_network_dataContext.class, 0);
        }

        public Edges_network_headerContext edges_network_header() {
            return getRuleContext(Edges_network_headerContext.class, 0);
        }

        public Edges_networkContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_edges_network;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterEdges_network(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitEdges_network(this);
        }
    }

    public final Edges_networkContext edges_network() throws RecognitionException {
        Edges_networkContext _localctx = new Edges_networkContext(_ctx, getState());
        enterRule(_localctx, 208, RULE_edges_network);
        try {
            setState(807);
            switch (_input.LA(1)) {
                case 7:
                case 9:
                case 114:
                case 121:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 48:
                case 61:
                case 110:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(803);
                    edges_network_header();
                    setState(804);
                    edges_network_data();
                    setState(805);
                    match(111);
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

    public static class Edges_network_headerContext extends ParserRuleContext {
        public Edges_network_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_edges_network_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterEdges_network_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitEdges_network_header(this);
        }
    }

    public final Edges_network_headerContext edges_network_header() throws RecognitionException {
        Edges_network_headerContext _localctx = new Edges_network_headerContext(_ctx, getState());
        enterRule(_localctx, 210, RULE_edges_network_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(809);
                _la = _input.LA(1);
                if (!(((((_la - 48)) & ~0x3f) == 0 && ((1L << (_la - 48)) & ((1L << (48 - 48)) | (1L << (61 - 48)) | (1L << (110 - 48)))) != 0))) {
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

    public static class Edges_network_dataContext extends ParserRuleContext {
        public Edges_network_dataContext edges_network_data() {
            return getRuleContext(Edges_network_dataContext.class, 0);
        }

        public Edges_network_entryContext edges_network_entry() {
            return getRuleContext(Edges_network_entryContext.class, 0);
        }

        public Edges_network_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_edges_network_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterEdges_network_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitEdges_network_data(this);
        }
    }

    public final Edges_network_dataContext edges_network_data() throws RecognitionException {
        Edges_network_dataContext _localctx = new Edges_network_dataContext(_ctx, getState());
        enterRule(_localctx, 212, RULE_edges_network_data);
        try {
            setState(816);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(812);
                    edges_network_entry();
                    setState(813);
                    match(28);
                    setState(814);
                    edges_network_data();
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

    public static class Edges_network_entryContext extends ParserRuleContext {
        public TerminalNode FLOAT() {
            return getToken(NexusFileParser.FLOAT, 0);
        }

        public List<TerminalNode> INT() {
            return getTokens(NexusFileParser.INT);
        }

        public TerminalNode INT(int i) {
            return getToken(NexusFileParser.INT, i);
        }

        public Edges_network_entryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_edges_network_entry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterEdges_network_entry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitEdges_network_entry(this);
        }
    }

    public final Edges_network_entryContext edges_network_entry() throws RecognitionException {
        Edges_network_entryContext _localctx = new Edges_network_entryContext(_ctx, getState());
        enterRule(_localctx, 214, RULE_edges_network_entry);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(818);
                match(INT);
                setState(819);
                match(INT);
                setState(820);
                match(INT);
                setState(821);
                match(95);
                setState(822);
                match(78);
                setState(823);
                match(INT);
                setState(824);
                match(30);
                setState(825);
                match(78);
                setState(826);
                match(FLOAT);
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

    public static class Matrix_headerContext extends ParserRuleContext {
        public Matrix_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMatrix_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_header(this);
        }
    }

    public final Matrix_headerContext matrix_header() throws RecognitionException {
        Matrix_headerContext _localctx = new Matrix_headerContext(_ctx, getState());
        enterRule(_localctx, 216, RULE_matrix_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(828);
                _la = _input.LA(1);
                if (!(_la == 18 || _la == 90)) {
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

    public static class Matrix_dataContext extends ParserRuleContext {
        public Matrix_entry_listContext matrix_entry_list() {
            return getRuleContext(Matrix_entry_listContext.class, 0);
        }

        public Matrix_dataContext matrix_data() {
            return getRuleContext(Matrix_dataContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Matrix_dataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_data;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMatrix_data(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_data(this);
        }
    }

    public final Matrix_dataContext matrix_data() throws RecognitionException {
        Matrix_dataContext _localctx = new Matrix_dataContext(_ctx, getState());
        enterRule(_localctx, 218, RULE_matrix_data);
        try {
            setState(841);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 99:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(831);
                    match(99);
                    setState(832);
                    match(IDENTIFIER);
                    setState(833);
                    match(99);
                    setState(834);
                    matrix_entry_list();
                    setState(835);
                    matrix_data();
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(837);
                    match(IDENTIFIER);
                    setState(838);
                    matrix_entry_list();
                    setState(839);
                    matrix_data();
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

    public static class Matrix_entry_listContext extends ParserRuleContext {
        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public Matrix_entry_listContext matrix_entry_list() {
            return getRuleContext(Matrix_entry_listContext.class, 0);
        }

        public State_composed_wordContext state_composed_word() {
            return getRuleContext(State_composed_wordContext.class, 0);
        }

        public State_composed_listContext state_composed_list() {
            return getRuleContext(State_composed_listContext.class, 0);
        }

        public Matrix_entry_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix_entry_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMatrix_entry_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMatrix_entry_list(this);
        }
    }

    public final Matrix_entry_listContext matrix_entry_list() throws RecognitionException {
        Matrix_entry_listContext _localctx = new Matrix_entry_listContext(_ctx, getState());
        enterRule(_localctx, 220, RULE_matrix_entry_list);
        try {
            setState(859);
            switch (_input.LA(1)) {
                case 99:
                case 111:
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                case FLOAT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(844);
                    number();
                    setState(845);
                    matrix_entry_list();
                }
                break;
                case 93:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(847);
                    match(93);
                    setState(848);
                    state_composed_word();
                    setState(849);
                    state_composed_list();
                    setState(850);
                    match(15);
                    setState(851);
                    matrix_entry_list();
                }
                break;
                case 101:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(853);
                    match(101);
                    setState(854);
                    state_composed_word();
                    setState(855);
                    state_composed_list();
                    setState(856);
                    match(13);
                    setState(857);
                    matrix_entry_list();
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

    public static class State_composed_wordContext extends ParserRuleContext {
        public List<State_complex_wordContext> state_complex_word() {
            return getRuleContexts(State_complex_wordContext.class);
        }

        public State_complex_wordContext state_complex_word(int i) {
            return getRuleContext(State_complex_wordContext.class, i);
        }

        public State_composed_wordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_state_composed_word;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterState_composed_word(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitState_composed_word(this);
        }
    }

    public final State_composed_wordContext state_composed_word() throws RecognitionException {
        State_composed_wordContext _localctx = new State_composed_wordContext(_ctx, getState());
        enterRule(_localctx, 222, RULE_state_composed_word);
        try {
            setState(866);
            switch (getInterpreter().adaptivePredict(_input, 51, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(861);
                    state_complex_word();
                }
                break;

                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(862);
                    state_complex_word();
                    setState(863);
                    match(92);
                    setState(864);
                    state_complex_word();
                }
                break;
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

    public static class State_complex_wordContext extends ParserRuleContext {
        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public State_complex_wordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_state_complex_word;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterState_complex_word(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitState_complex_word(this);
        }
    }

    public final State_complex_wordContext state_complex_word() throws RecognitionException {
        State_complex_wordContext _localctx = new State_complex_wordContext(_ctx, getState());
        enterRule(_localctx, 224, RULE_state_complex_word);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(868);
                number();
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

    public static class State_composed_listContext extends ParserRuleContext {
        public State_composed_wordContext state_composed_word() {
            return getRuleContext(State_composed_wordContext.class, 0);
        }

        public State_composed_listContext state_composed_list() {
            return getRuleContext(State_composed_listContext.class, 0);
        }

        public State_composed_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_state_composed_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterState_composed_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitState_composed_list(this);
        }
    }

    public final State_composed_listContext state_composed_list() throws RecognitionException {
        State_composed_listContext _localctx = new State_composed_listContext(_ctx, getState());
        enterRule(_localctx, 226, RULE_state_composed_list);
        try {
            setState(874);
            switch (_input.LA(1)) {
                case 13:
                case 15:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case INT:
                case FLOAT:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(871);
                    state_composed_word();
                    setState(872);
                    state_composed_list();
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

    public static class Boolean_optionContext extends ParserRuleContext {
        public Boolean_optionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_boolean_option;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterBoolean_option(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitBoolean_option(this);
        }
    }

    public final Boolean_optionContext boolean_option() throws RecognitionException {
        Boolean_optionContext _localctx = new Boolean_optionContext(_ctx, getState());
        enterRule(_localctx, 228, RULE_boolean_option);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(876);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 20) | (1L << 38))) != 0) || _la == 71)) {
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

    public static class DimensionsContext extends ParserRuleContext {
        public DimensionsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dimensions;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterDimensions(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitDimensions(this);
        }
    }

    public final DimensionsContext dimensions() throws RecognitionException {
        DimensionsContext _localctx = new DimensionsContext(_ctx, getState());
        enterRule(_localctx, 230, RULE_dimensions);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(878);
                _la = _input.LA(1);
                if (!(_la == 63 || _la == 117)) {
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

    public static class FormatContext extends ParserRuleContext {
        public FormatContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_format;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterFormat(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitFormat(this);
        }
    }

    public final FormatContext format() throws RecognitionException {
        FormatContext _localctx = new FormatContext(_ctx, getState());
        enterRule(_localctx, 232, RULE_format);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(880);
                _la = _input.LA(1);
                if (!(_la == 77 || _la == 113)) {
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

    public static class Identifier_listContext extends ParserRuleContext {
        public Identifier_listContext identifier_list() {
            return getRuleContext(Identifier_listContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public Identifier_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_identifier_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterIdentifier_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitIdentifier_list(this);
        }
    }

    public final Identifier_listContext identifier_list() throws RecognitionException {
        Identifier_listContext _localctx = new Identifier_listContext(_ctx, getState());
        enterRule(_localctx, 234, RULE_identifier_list);
        try {
            setState(889);
            switch (_input.LA(1)) {
                case 111:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(883);
                    match(IDENTIFIER);
                    setState(884);
                    identifier_list();
                }
                break;
                case 99:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(885);
                    match(99);
                    setState(886);
                    match(IDENTIFIER);
                    setState(887);
                    match(99);
                    setState(888);
                    identifier_list();
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

    public static class MissingContext extends ParserRuleContext {
        public TerminalNode LETTER_US() {
            return getToken(NexusFileParser.LETTER_US, 0);
        }

        public MissingContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_missing;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterMissing(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitMissing(this);
        }
    }

    public final MissingContext missing() throws RecognitionException {
        MissingContext _localctx = new MissingContext(_ctx, getState());
        enterRule(_localctx, 236, RULE_missing);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(891);
                match(96);
                setState(892);
                match(78);
                setState(893);
                match(LETTER_US);
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

    public static class NtaxContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public NtaxContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ntax;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNtax(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNtax(this);
        }
    }

    public final NtaxContext ntax() throws RecognitionException {
        NtaxContext _localctx = new NtaxContext(_ctx, getState());
        enterRule(_localctx, 238, RULE_ntax);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(895);
                match(5);
                setState(896);
                match(78);
                setState(897);
                match(INT);
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

    public static class NewtaxaContext extends ParserRuleContext {
        public NtaxContext ntax() {
            return getRuleContext(NtaxContext.class, 0);
        }

        public NewtaxaContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_newtaxa;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNewtaxa(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNewtaxa(this);
        }
    }

    public final NewtaxaContext newtaxa() throws RecognitionException {
        NewtaxaContext _localctx = new NewtaxaContext(_ctx, getState());
        enterRule(_localctx, 240, RULE_newtaxa);
        try {
            setState(902);
            switch (_input.LA(1)) {
                case 81:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(899);
                    match(81);
                    setState(900);
                    ntax();
                }
                break;
                case 5:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(901);
                    ntax();
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

    public static class Newtaxa_optionalContext extends ParserRuleContext {
        public NewtaxaContext newtaxa() {
            return getRuleContext(NewtaxaContext.class, 0);
        }

        public Newtaxa_optionalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_newtaxa_optional;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNewtaxa_optional(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNewtaxa_optional(this);
        }
    }

    public final Newtaxa_optionalContext newtaxa_optional() throws RecognitionException {
        Newtaxa_optionalContext _localctx = new Newtaxa_optionalContext(_ctx, getState());
        enterRule(_localctx, 242, RULE_newtaxa_optional);
        try {
            setState(906);
            switch (_input.LA(1)) {
                case EOF:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 5:
                case 81:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(905);
                    newtaxa();
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

    public static class NsplitsContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public NsplitsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nsplits;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNsplits(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNsplits(this);
        }
    }

    public final NsplitsContext nsplits() throws RecognitionException {
        NsplitsContext _localctx = new NsplitsContext(_ctx, getState());
        enterRule(_localctx, 244, RULE_nsplits);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(908);
                match(118);
                setState(909);
                match(78);
                setState(910);
                match(INT);
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

    public static class NverticesContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public NverticesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nvertices;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNvertices(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNvertices(this);
        }
    }

    public final NverticesContext nvertices() throws RecognitionException {
        NverticesContext _localctx = new NverticesContext(_ctx, getState());
        enterRule(_localctx, 246, RULE_nvertices);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(912);
                match(8);
                setState(913);
                match(78);
                setState(914);
                match(INT);
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

    public static class NedgesContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public NedgesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nedges;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNedges(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNedges(this);
        }
    }

    public final NedgesContext nedges() throws RecognitionException {
        NedgesContext _localctx = new NedgesContext(_ctx, getState());
        enterRule(_localctx, 248, RULE_nedges);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(916);
                match(87);
                setState(917);
                match(78);
                setState(918);
                match(INT);
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

    public static class PropertiesContext extends ParserRuleContext {
        public PropertiesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_properties;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterProperties(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitProperties(this);
        }
    }

    public final PropertiesContext properties() throws RecognitionException {
        PropertiesContext _localctx = new PropertiesContext(_ctx, getState());
        enterRule(_localctx, 250, RULE_properties);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(920);
                _la = _input.LA(1);
                if (!(_la == 68 || _la == 91)) {
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

    public static class ReferenceContext extends ParserRuleContext {
        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public ReferenceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_reference;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterReference(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitReference(this);
        }
    }

    public final ReferenceContext reference() throws RecognitionException {
        ReferenceContext _localctx = new ReferenceContext(_ctx, getState());
        enterRule(_localctx, 252, RULE_reference);
        try {
            setState(924);
            switch (_input.LA(1)) {
                case INT:
                case FLOAT:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(922);
                    number();
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(923);
                    match(IDENTIFIER);
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

    public static class StarContext extends ParserRuleContext {
        public StarContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_star;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterStar(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitStar(this);
        }
    }

    public final StarContext star() throws RecognitionException {
        StarContext _localctx = new StarContext(_ctx, getState());
        enterRule(_localctx, 254, RULE_star);
        try {
            setState(928);
            switch (_input.LA(1)) {
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(927);
                    match(3);
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

    public static class TaxlabelsContext extends ParserRuleContext {
        public Taxlabels_headerContext taxlabels_header() {
            return getRuleContext(Taxlabels_headerContext.class, 0);
        }

        public Identifier_listContext identifier_list() {
            return getRuleContext(Identifier_listContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(NexusFileParser.IDENTIFIER, 0);
        }

        public TaxlabelsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_taxlabels;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTaxlabels(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTaxlabels(this);
        }
    }

    public final TaxlabelsContext taxlabels() throws RecognitionException {
        TaxlabelsContext _localctx = new TaxlabelsContext(_ctx, getState());
        enterRule(_localctx, 256, RULE_taxlabels);
        try {
            setState(942);
            switch (getInterpreter().adaptivePredict(_input, 58, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(930);
                    taxlabels_header();
                    setState(931);
                    match(IDENTIFIER);
                    setState(932);
                    identifier_list();
                    setState(933);
                    match(111);
                }
                break;

                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(935);
                    taxlabels_header();
                    setState(936);
                    match(99);
                    setState(937);
                    match(IDENTIFIER);
                    setState(938);
                    match(99);
                    setState(939);
                    identifier_list();
                    setState(940);
                    match(111);
                }
                break;
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

    public static class Taxlabels_optionalContext extends ParserRuleContext {
        public TaxlabelsContext taxlabels() {
            return getRuleContext(TaxlabelsContext.class, 0);
        }

        public Taxlabels_optionalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_taxlabels_optional;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTaxlabels_optional(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTaxlabels_optional(this);
        }
    }

    public final Taxlabels_optionalContext taxlabels_optional() throws RecognitionException {
        Taxlabels_optionalContext _localctx = new Taxlabels_optionalContext(_ctx, getState());
        enterRule(_localctx, 258, RULE_taxlabels_optional);
        try {
            setState(946);
            switch (_input.LA(1)) {
                case 18:
                case 90:
                    enterOuterAlt(_localctx, 1);
                {
                }
                break;
                case 73:
                case 102:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(945);
                    taxlabels();
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

    public static class Taxlabels_headerContext extends ParserRuleContext {
        public Taxlabels_headerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_taxlabels_header;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterTaxlabels_header(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitTaxlabels_header(this);
        }
    }

    public final Taxlabels_headerContext taxlabels_header() throws RecognitionException {
        Taxlabels_headerContext _localctx = new Taxlabels_headerContext(_ctx, getState());
        enterRule(_localctx, 260, RULE_taxlabels_header);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(948);
                _la = _input.LA(1);
                if (!(_la == 73 || _la == 102)) {
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

    public static class NumberContext extends ParserRuleContext {
        public TerminalNode FLOAT() {
            return getToken(NexusFileParser.FLOAT, 0);
        }

        public TerminalNode INT() {
            return getToken(NexusFileParser.INT, 0);
        }

        public NumberContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_number;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).enterNumber(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NexusFileListener) ((NexusFileListener) listener).exitNumber(this);
        }
    }

    public final NumberContext number() throws RecognitionException {
        NumberContext _localctx = new NumberContext(_ctx, getState());
        enterRule(_localctx, 262, RULE_number);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(950);
                _la = _input.LA(1);
                if (!(_la == INT || _la == FLOAT)) {
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
            "\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\u0083\u03bb\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
                    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
                    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=" +
                    "\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I" +
                    "\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT" +
                    "\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4" +
                    "`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t" +
                    "k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv\4" +
                    "w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t\u0080" +
                    "\4\u0081\t\u0081\4\u0082\t\u0082\4\u0083\t\u0083\4\u0084\t\u0084\4\u0085" +
                    "\t\u0085\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\5\4\u0115\n\4\3\5\3\5" +
                    "\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u0127\n\b" +
                    "\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3" +
                    "\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u0145\n\16\3" +
                    "\17\3\17\3\17\3\17\5\17\u014b\n\17\3\20\3\20\3\20\3\20\3\20\5\20\u0152" +
                    "\n\20\3\21\3\21\3\21\3\21\5\21\u0158\n\21\3\22\3\22\3\22\3\22\3\22\5\22" +
                    "\u015f\n\22\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\5\26" +
                    "\u016c\n\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31" +
                    "\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u0184\n\33\3\34" +
                    "\3\34\3\34\3\34\3\34\5\34\u018b\n\34\3\35\3\35\3\35\3\35\5\35\u0191\n" +
                    "\35\3\36\3\36\3\36\3\36\5\36\u0197\n\36\3\37\3\37\3\37\5\37\u019c\n\37" +
                    "\3 \3 \3 \3 \3!\3!\3\"\3\"\3\"\3\"\3#\3#\3$\3$\3$\3$\3%\3%\3&\3&\3&\3" +
                    "&\3&\5&\u01b5\n&\3\'\3\'\3\'\3\'\5\'\u01bb\n\'\3(\3(\3(\5(\u01c0\n(\3" +
                    ")\3)\3*\3*\3*\3*\3*\5*\u01c9\n*\3+\3+\3,\3,\3,\3,\5,\u01d1\n,\3-\3-\3" +
                    ".\3.\3.\3.\3.\3.\3.\5.\u01dc\n.\3/\3/\3/\3/\3/\5/\u01e3\n/\3\60\3\60\3" +
                    "\60\5\60\u01e8\n\60\3\61\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\63\3\63" +
                    "\3\63\3\63\3\63\5\63\u01f7\n\63\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64" +
                    "\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3" +
                    "=\3=\3=\3>\3>\3?\3?\3?\3?\3?\5?\u021d\n?\3@\3@\3@\3@\5@\u0223\n@\3A\3" +
                    "A\3A\3A\5A\u0229\nA\3B\3B\3B\3B\3B\3B\5B\u0231\nB\3C\3C\3C\3C\3C\3D\3" +
                    "D\3E\3E\3E\3E\3E\3E\3E\5E\u0241\nE\3F\3F\3G\3G\3G\3G\3G\3G\5G\u024b\n" +
                    "G\3H\3H\3H\3H\5H\u0251\nH\3I\3I\3J\3J\3J\3J\3J\3J\3J\3J\3K\3K\3K\3K\3" +
                    "K\3K\3K\5K\u0264\nK\3L\3L\3L\5L\u0269\nL\3M\3M\3M\3M\3M\5M\u0270\nM\3" +
                    "N\3N\3N\3N\5N\u0276\nN\3O\3O\5O\u027a\nO\3P\3P\3P\5P\u027f\nP\3Q\3Q\3" +
                    "Q\3Q\3Q\3Q\3Q\3Q\3Q\3R\3R\3S\3S\3S\3S\3S\3S\3S\5S\u0293\nS\3T\3T\3T\3" +
                    "T\3T\5T\u029a\nT\3U\3U\3V\3V\3V\3V\5V\u02a2\nV\3W\3W\5W\u02a6\nW\3X\3" +
                    "X\3Y\3Y\3Y\3Y\3Z\3Z\3Z\3Z\3Z\5Z\u02b3\nZ\3[\3[\3\\\3\\\3\\\3\\\3\\\5\\" +
                    "\u02bc\n\\\3]\3]\3]\3]\3]\3^\3^\3^\3^\3^\5^\u02c8\n^\3_\3_\3`\3`\3`\3" +
                    "`\3`\5`\u02d1\n`\3a\3a\3a\3a\3a\3a\3a\3a\5a\u02db\na\3b\3b\3b\3b\3b\3" +
                    "b\3b\3b\3b\3c\3c\3c\3c\3c\3c\3c\3c\3c\3c\3c\3c\3d\3d\3d\3d\3d\5d\u02f7" +
                    "\nd\3e\3e\3f\3f\3f\3f\3f\5f\u0300\nf\3g\3g\3g\3h\3h\3h\3h\3h\3i\3i\3i" +
                    "\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\3i\5i" +
                    "\u0323\ni\3j\3j\3j\3j\3j\5j\u032a\nj\3k\3k\3l\3l\3l\3l\3l\5l\u0333\nl" +
                    "\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3n\3n\3o\3o\3o\3o\3o\3o\3o\3o\3o\3o\3o" +
                    "\5o\u034c\no\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\5p\u035e" +
                    "\np\3q\3q\3q\3q\3q\5q\u0365\nq\3r\3r\3s\3s\3s\3s\5s\u036d\ns\3t\3t\3u" +
                    "\3u\3v\3v\3w\3w\3w\3w\3w\3w\3w\5w\u037c\nw\3x\3x\3x\3x\3y\3y\3y\3y\3z" +
                    "\3z\3z\5z\u0389\nz\3{\3{\5{\u038d\n{\3|\3|\3|\3|\3}\3}\3}\3}\3~\3~\3~" +
                    "\3~\3\177\3\177\3\u0080\3\u0080\5\u0080\u039f\n\u0080\3\u0081\3\u0081" +
                    "\5\u0081\u03a3\n\u0081\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082" +
                    "\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082\5\u0082\u03b1\n\u0082" +
                    "\3\u0083\3\u0083\5\u0083\u03b5\n\u0083\3\u0084\3\u0084\3\u0085\3\u0085" +
                    "\3\u0085\2\u0086\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62" +
                    "\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088" +
                    "\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0" +
                    "\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8" +
                    "\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0" +
                    "\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8" +
                    "\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe\u0100" +
                    "\u0102\u0104\u0106\u0108\2 \4\2$$\61\61\4\2\25\25\64\64\6\2\t\t\13\13" +
                    "tt{{\5\2\6\6QQ[[\5\2\35\35\'\'XX\7\2\31\32!!LLWWii\6\2BBEE``ll\4\2CCT" +
                    "T\f\2\b\b\r\r\26\26()+,\67\6799;;IIvv\5\2%%JJVV\4\2..zz\4\2\34\34\66\66" +
                    "\6\2\22\22\60\60::nn\6\2\27\27**HHUU\5\2\4\4\20\20DD\4\2\65\65jj\6\2\"" +
                    "\"88GGuu\5\2\3\3rryy\5\2&&@@ff\5\2\65\65MMjj\5\2--<<dd\5\2##//mm\5\2\62" +
                    "\62??pp\4\2\24\24\\\\\6\2\b\b\26\26((II\4\2AAww\4\2OOss\4\2FF]]\4\2KK" +
                    "hh\3\2}~\u0383\2\u010a\3\2\2\2\4\u010e\3\2\2\2\6\u0114\3\2\2\2\b\u0116" +
                    "\3\2\2\2\n\u011b\3\2\2\2\f\u011d\3\2\2\2\16\u0126\3\2\2\2\20\u0128\3\2" +
                    "\2\2\22\u012d\3\2\2\2\24\u012f\3\2\2\2\26\u0133\3\2\2\2\30\u013c\3\2\2" +
                    "\2\32\u0144\3\2\2\2\34\u014a\3\2\2\2\36\u0151\3\2\2\2 \u0157\3\2\2\2\"" +
                    "\u015e\3\2\2\2$\u0160\3\2\2\2&\u0164\3\2\2\2(\u0166\3\2\2\2*\u0168\3\2" +
                    "\2\2,\u016d\3\2\2\2.\u016f\3\2\2\2\60\u0171\3\2\2\2\62\u017b\3\2\2\2\64" +
                    "\u0183\3\2\2\2\66\u018a\3\2\2\28\u0190\3\2\2\2:\u0196\3\2\2\2<\u0198\3" +
                    "\2\2\2>\u019d\3\2\2\2@\u01a1\3\2\2\2B\u01a3\3\2\2\2D\u01a7\3\2\2\2F\u01a9" +
                    "\3\2\2\2H\u01ad\3\2\2\2J\u01b4\3\2\2\2L\u01ba\3\2\2\2N\u01bc\3\2\2\2P" +
                    "\u01c1\3\2\2\2R\u01c8\3\2\2\2T\u01ca\3\2\2\2V\u01d0\3\2\2\2X\u01d2\3\2" +
                    "\2\2Z\u01db\3\2\2\2\\\u01e2\3\2\2\2^\u01e7\3\2\2\2`\u01e9\3\2\2\2b\u01ef" +
                    "\3\2\2\2d\u01f6\3\2\2\2f\u01f8\3\2\2\2h\u0201\3\2\2\2j\u0203\3\2\2\2l" +
                    "\u0205\3\2\2\2n\u0207\3\2\2\2p\u0209\3\2\2\2r\u020b\3\2\2\2t\u020d\3\2" +
                    "\2\2v\u020f\3\2\2\2x\u0211\3\2\2\2z\u0215\3\2\2\2|\u021c\3\2\2\2~\u0222" +
                    "\3\2\2\2\u0080\u0228\3\2\2\2\u0082\u0230\3\2\2\2\u0084\u0232\3\2\2\2\u0086" +
                    "\u0237\3\2\2\2\u0088\u0240\3\2\2\2\u008a\u0242\3\2\2\2\u008c\u024a\3\2" +
                    "\2\2\u008e\u0250\3\2\2\2\u0090\u0252\3\2\2\2\u0092\u0254\3\2\2\2\u0094" +
                    "\u0263\3\2\2\2\u0096\u0268\3\2\2\2\u0098\u026f\3\2\2\2\u009a\u0275\3\2" +
                    "\2\2\u009c\u0279\3\2\2\2\u009e\u027e\3\2\2\2\u00a0\u0280\3\2\2\2\u00a2" +
                    "\u0289\3\2\2\2\u00a4\u0292\3\2\2\2\u00a6\u0299\3\2\2\2\u00a8\u029b\3\2" +
                    "\2\2\u00aa\u02a1\3\2\2\2\u00ac\u02a5\3\2\2\2\u00ae\u02a7\3\2\2\2\u00b0" +
                    "\u02a9\3\2\2\2\u00b2\u02b2\3\2\2\2\u00b4\u02b4\3\2\2\2\u00b6\u02bb\3\2" +
                    "\2\2\u00b8\u02bd\3\2\2\2\u00ba\u02c7\3\2\2\2\u00bc\u02c9\3\2\2\2\u00be" +
                    "\u02d0\3\2\2\2\u00c0\u02da\3\2\2\2\u00c2\u02dc\3\2\2\2\u00c4\u02e5\3\2" +
                    "\2\2\u00c6\u02f6\3\2\2\2\u00c8\u02f8\3\2\2\2\u00ca\u02ff\3\2\2\2\u00cc" +
                    "\u0301\3\2\2\2\u00ce\u0304\3\2\2\2\u00d0\u0322\3\2\2\2\u00d2\u0329\3\2" +
                    "\2\2\u00d4\u032b\3\2\2\2\u00d6\u0332\3\2\2\2\u00d8\u0334\3\2\2\2\u00da" +
                    "\u033e\3\2\2\2\u00dc\u034b\3\2\2\2\u00de\u035d\3\2\2\2\u00e0\u0364\3\2" +
                    "\2\2\u00e2\u0366\3\2\2\2\u00e4\u036c\3\2\2\2\u00e6\u036e\3\2\2\2\u00e8" +
                    "\u0370\3\2\2\2\u00ea\u0372\3\2\2\2\u00ec\u037b\3\2\2\2\u00ee\u037d\3\2" +
                    "\2\2\u00f0\u0381\3\2\2\2\u00f2\u0388\3\2\2\2\u00f4\u038c\3\2\2\2\u00f6" +
                    "\u038e\3\2\2\2\u00f8\u0392\3\2\2\2\u00fa\u0396\3\2\2\2\u00fc\u039a\3\2" +
                    "\2\2\u00fe\u039e\3\2\2\2\u0100\u03a2\3\2\2\2\u0102\u03b0\3\2\2\2\u0104" +
                    "\u03b4\3\2\2\2\u0106\u03b6\3\2\2\2\u0108\u03b8\3\2\2\2\u010a\u010b\5\4" +
                    "\3\2\u010b\u010c\5\6\4\2\u010c\u010d\7\2\2\3\u010d\3\3\2\2\2\u010e\u010f" +
                    "\t\2\2\2\u010f\5\3\2\2\2\u0110\u0115\3\2\2\2\u0111\u0112\5\b\5\2\u0112" +
                    "\u0113\5\6\4\2\u0113\u0115\3\2\2\2\u0114\u0110\3\2\2\2\u0114\u0111\3\2" +
                    "\2\2\u0115\7\3\2\2\2\u0116\u0117\5\n\6\2\u0117\u0118\5\16\b\2\u0118\u0119" +
                    "\5\f\7\2\u0119\u011a\7q\2\2\u011a\t\3\2\2\2\u011b\u011c\t\3\2\2\u011c" +
                    "\13\3\2\2\2\u011d\u011e\t\4\2\2\u011e\r\3\2\2\2\u011f\u0127\5\20\t\2\u0120" +
                    "\u0127\5\26\f\2\u0121\u0127\5\60\31\2\u0122\u0127\5`\61\2\u0123\u0127" +
                    "\5x=\2\u0124\u0127\5\u0084C\2\u0125\u0127\5\u00a0Q\2\u0126\u011f\3\2\2" +
                    "\2\u0126\u0120\3\2\2\2\u0126\u0121\3\2\2\2\u0126\u0122\3\2\2\2\u0126\u0123" +
                    "\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0125\3\2\2\2\u0127\17\3\2\2\2\u0128" +
                    "\u0129\5\22\n\2\u0129\u012a\7q\2\2\u012a\u012b\5\24\13\2\u012b\u012c\5" +
                    "\u0102\u0082\2\u012c\21\3\2\2\2\u012d\u012e\t\5\2\2\u012e\23\3\2\2\2\u012f" +
                    "\u0130\5\u00e8u\2\u0130\u0131\5\u00f0y\2\u0131\u0132\7q\2\2\u0132\25\3" +
                    "\2\2\2\u0133\u0134\5\30\r\2\u0134\u0135\7q\2\2\u0135\u0136\5\32\16\2\u0136" +
                    "\u0137\5\36\20\2\u0137\u0138\5\u0104\u0083\2\u0138\u0139\5\u00dan\2\u0139" +
                    "\u013a\5\u00dco\2\u013a\u013b\7q\2\2\u013b\27\3\2\2\2\u013c\u013d\t\6" +
                    "\2\2\u013d\31\3\2\2\2\u013e\u0145\3\2\2\2\u013f\u0140\5\u00e8u\2\u0140" +
                    "\u0141\5\u00f2z\2\u0141\u0142\5\34\17\2\u0142\u0143\7q\2\2\u0143\u0145" +
                    "\3\2\2\2\u0144\u013e\3\2\2\2\u0144\u013f\3\2\2\2\u0145\33\3\2\2\2\u0146" +
                    "\u014b\3\2\2\2\u0147\u0148\7o\2\2\u0148\u0149\7P\2\2\u0149\u014b\7}\2" +
                    "\2\u014a\u0146\3\2\2\2\u014a\u0147\3\2\2\2\u014b\35\3\2\2\2\u014c\u0152" +
                    "\3\2\2\2\u014d\u014e\5\u00eav\2\u014e\u014f\5 \21\2\u014f\u0150\7q\2\2" +
                    "\u0150\u0152\3\2\2\2\u0151\u014c\3\2\2\2\u0151\u014d\3\2\2\2\u0152\37" +
                    "\3\2\2\2\u0153\u0158\3\2\2\2\u0154\u0155\5\"\22\2\u0155\u0156\5 \21\2" +
                    "\u0156\u0158\3\2\2\2\u0157\u0153\3\2\2\2\u0157\u0154\3\2\2\2\u0158!\3" +
                    "\2\2\2\u0159\u015f\5$\23\2\u015a\u015f\5(\25\2\u015b\u015f\5*\26\2\u015c" +
                    "\u015f\5\u00eex\2\u015d\u015f\7N\2\2\u015e\u0159\3\2\2\2\u015e\u015a\3" +
                    "\2\2\2\u015e\u015b\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015d\3\2\2\2\u015f" +
                    "#\3\2\2\2\u0160\u0161\7\37\2\2\u0161\u0162\7P\2\2\u0162\u0163\5&\24\2" +
                    "\u0163%\3\2\2\2\u0164\u0165\t\7\2\2\u0165\'\3\2\2\2\u0166\u0167\t\b\2" +
                    "\2\u0167)\3\2\2\2\u0168\u016b\5,\27\2\u0169\u016a\7P\2\2\u016a\u016c\5" +
                    ".\30\2\u016b\u0169\3\2\2\2\u016b\u016c\3\2\2\2\u016c+\3\2\2\2\u016d\u016e" +
                    "\t\t\2\2\u016e-\3\2\2\2\u016f\u0170\t\n\2\2\u0170/\3\2\2\2\u0171\u0172" +
                    "\5\62\32\2\u0172\u0173\7q\2\2\u0173\u0174\5\64\33\2\u0174\u0175\5\66\34" +
                    "\2\u0175\u0176\5J&\2\u0176\u0177\5R*\2\u0177\u0178\5\u00dan\2\u0178\u0179" +
                    "\5Z.\2\u0179\u017a\7q\2\2\u017a\61\3\2\2\2\u017b\u017c\t\13\2\2\u017c" +
                    "\63\3\2\2\2\u017d\u0184\3\2\2\2\u017e\u017f\5\u00e8u\2\u017f\u0180\5\u00f0" +
                    "y\2\u0180\u0181\5\u00f6|\2\u0181\u0182\7q\2\2\u0182\u0184\3\2\2\2\u0183" +
                    "\u017d\3\2\2\2\u0183\u017e\3\2\2\2\u0184\65\3\2\2\2\u0185\u018b\3\2\2" +
                    "\2\u0186\u0187\5\u00eav\2\u0187\u0188\58\35\2\u0188\u0189\7q\2\2\u0189" +
                    "\u018b\3\2\2\2\u018a\u0185\3\2\2\2\u018a\u0186\3\2\2\2\u018b\67\3\2\2" +
                    "\2\u018c\u0191\3\2\2\2\u018d\u018e\5:\36\2\u018e\u018f\58\35\2\u018f\u0191" +
                    "\3\2\2\2\u0190\u018c\3\2\2\2\u0190\u018d\3\2\2\2\u01919\3\2\2\2\u0192" +
                    "\u0197\5<\37\2\u0193\u0197\5> \2\u0194\u0197\5B\"\2\u0195\u0197\5F$\2" +
                    "\u0196\u0192\3\2\2\2\u0196\u0193\3\2\2\2\u0196\u0194\3\2\2\2\u0196\u0195" +
                    "\3\2\2\2\u0197;\3\2\2\2\u0198\u019b\5,\27\2\u0199\u019a\7P\2\2\u019a\u019c" +
                    "\5.\30\2\u019b\u0199\3\2\2\2\u019b\u019c\3\2\2\2\u019c=\3\2\2\2\u019d" +
                    "\u019e\5@!\2\u019e\u019f\7P\2\2\u019f\u01a0\5\u00e6t\2\u01a0?\3\2\2\2" +
                    "\u01a1\u01a2\7\63\2\2\u01a2A\3\2\2\2\u01a3\u01a4\5D#\2\u01a4\u01a5\7P" +
                    "\2\2\u01a5\u01a6\5\u00e6t\2\u01a6C\3\2\2\2\u01a7\u01a8\7\f\2\2\u01a8E" +
                    "\3\2\2\2\u01a9\u01aa\5H%\2\u01aa\u01ab\7P\2\2\u01ab\u01ac\5\u00e6t\2\u01ac" +
                    "G\3\2\2\2\u01ad\u01ae\7|\2\2\u01aeI\3\2\2\2\u01af\u01b5\3\2\2\2\u01b0" +
                    "\u01b1\5\u00fc\177\2\u01b1\u01b2\5L\'\2\u01b2\u01b3\7q\2\2\u01b3\u01b5" +
                    "\3\2\2\2\u01b4\u01af\3\2\2\2\u01b4\u01b0\3\2\2\2\u01b5K\3\2\2\2\u01b6" +
                    "\u01bb\3\2\2\2\u01b7\u01b8\5N(\2\u01b8\u01b9\5L\'\2\u01b9\u01bb\3\2\2" +
                    "\2\u01ba\u01b6\3\2\2\2\u01ba\u01b7\3\2\2\2\u01bbM\3\2\2\2\u01bc\u01bf" +
                    "\5P)\2\u01bd\u01be\7P\2\2\u01be\u01c0\5\u0108\u0085\2\u01bf\u01bd\3\2" +
                    "\2\2\u01bf\u01c0\3\2\2\2\u01c0O\3\2\2\2\u01c1\u01c2\t\f\2\2\u01c2Q\3\2" +
                    "\2\2\u01c3\u01c9\3\2\2\2\u01c4\u01c5\5T+\2\u01c5\u01c6\5V,\2\u01c6\u01c7" +
                    "\7q\2\2\u01c7\u01c9\3\2\2\2\u01c8\u01c3\3\2\2\2\u01c8\u01c4\3\2\2\2\u01c9" +
                    "S\3\2\2\2\u01ca\u01cb\t\r\2\2\u01cbU\3\2\2\2\u01cc\u01d1\3\2\2\2\u01cd" +
                    "\u01ce\5X-\2\u01ce\u01cf\5V,\2\u01cf\u01d1\3\2\2\2\u01d0\u01cc\3\2\2\2" +
                    "\u01d0\u01cd\3\2\2\2\u01d1W\3\2\2\2\u01d2\u01d3\7}\2\2\u01d3Y\3\2\2\2" +
                    "\u01d4\u01dc\3\2\2\2\u01d5\u01d6\5\\/\2\u01d6\u01d7\7~\2\2\u01d7\u01d8" +
                    "\5^\60\2\u01d8\u01d9\7\36\2\2\u01d9\u01da\5Z.\2\u01da\u01dc\3\2\2\2\u01db" +
                    "\u01d4\3\2\2\2\u01db\u01d5\3\2\2\2\u01dc[\3\2\2\2\u01dd\u01e3\3\2\2\2" +
                    "\u01de\u01df\7e\2\2\u01df\u01e0\7}\2\2\u01e0\u01e3\7e\2\2\u01e1\u01e3" +
                    "\7}\2\2\u01e2\u01dd\3\2\2\2\u01e2\u01de\3\2\2\2\u01e2\u01e1\3\2\2\2\u01e3" +
                    "]\3\2\2\2\u01e4\u01e8\3\2\2\2\u01e5\u01e6\7}\2\2\u01e6\u01e8\5^\60\2\u01e7" +
                    "\u01e4\3\2\2\2\u01e7\u01e5\3\2\2\2\u01e8_\3\2\2\2\u01e9\u01ea\5b\62\2" +
                    "\u01ea\u01eb\7q\2\2\u01eb\u01ec\5\u00dan\2\u01ec\u01ed\5d\63\2\u01ed\u01ee" +
                    "\7q\2\2\u01eea\3\2\2\2\u01ef\u01f0\t\16\2\2\u01f0c\3\2\2\2\u01f1\u01f7" +
                    "\3\2\2\2\u01f2\u01f3\5f\64\2\u01f3\u01f4\7\36\2\2\u01f4\u01f5\5Z.\2\u01f5" +
                    "\u01f7\3\2\2\2\u01f6\u01f1\3\2\2\2\u01f6\u01f2\3\2\2\2\u01f7e\3\2\2\2" +
                    "\u01f8\u01f9\5h\65\2\u01f9\u01fa\5j\66\2\u01fa\u01fb\5l\67\2\u01fb\u01fc" +
                    "\5n8\2\u01fc\u01fd\5t;\2\u01fd\u01fe\5p9\2\u01fe\u01ff\5v<\2\u01ff\u0200" +
                    "\5r:\2\u0200g\3\2\2\2\u0201\u0202\7\177\2\2\u0202i\3\2\2\2\u0203\u0204" +
                    "\7}\2\2\u0204k\3\2\2\2\u0205\u0206\7}\2\2\u0206m\3\2\2\2\u0207\u0208\7" +
                    "}\2\2\u0208o\3\2\2\2\u0209\u020a\7}\2\2\u020aq\3\2\2\2\u020b\u020c\7}" +
                    "\2\2\u020cs\3\2\2\2\u020d\u020e\7\177\2\2\u020eu\3\2\2\2\u020f\u0210\7" +
                    "\177\2\2\u0210w\3\2\2\2\u0211\u0212\5z>\2\u0212\u0213\7q\2\2\u0213\u0214" +
                    "\5|?\2\u0214y\3\2\2\2\u0215\u0216\t\17\2\2\u0216{\3\2\2\2\u0217\u021d" +
                    "\3\2\2\2\u0218\u0219\5~@\2\u0219\u021a\7q\2\2\u021a\u021b\5|?\2\u021b" +
                    "\u021d\3\2\2\2\u021c\u0217\3\2\2\2\u021c\u0218\3\2\2\2\u021d}\3\2\2\2" +
                    "\u021e\u021f\7\177\2\2\u021f\u0223\5\u0080A\2\u0220\u0223\5\u0082B\2\u0221" +
                    "\u0223\7\177\2\2\u0222\u021e\3\2\2\2\u0222\u0220\3\2\2\2\u0222\u0221\3" +
                    "\2\2\2\u0223\177\3\2\2\2\u0224\u0229\3\2\2\2\u0225\u0226\5\u0082B\2\u0226" +
                    "\u0227\5\u0080A\2\u0227\u0229\3\2\2\2\u0228\u0224\3\2\2\2\u0228\u0225" +
                    "\3\2\2\2\u0229\u0081\3\2\2\2\u022a\u022b\7\177\2\2\u022b\u022c\7P\2\2" +
                    "\u022c\u0231\7\177\2\2\u022d\u022e\7\177\2\2\u022e\u022f\7P\2\2\u022f" +
                    "\u0231\7}\2\2\u0230\u022a\3\2\2\2\u0230\u022d\3\2\2\2\u0231\u0083\3\2" +
                    "\2\2\u0232\u0233\5\u0086D\2\u0233\u0234\7q\2\2\u0234\u0235\5\u0088E\2" +
                    "\u0235\u0236\5\u008eH\2\u0236\u0085\3\2\2\2\u0237\u0238\t\20\2\2\u0238" +
                    "\u0087\3\2\2\2\u0239\u0241\3\2\2\2\u023a\u023b\5\u008aF\2\u023b\u023c" +
                    "\5\u00fe\u0080\2\u023c\u023d\5\u00fe\u0080\2\u023d\u023e\5\u008cG\2\u023e" +
                    "\u023f\7q\2\2\u023f\u0241\3\2\2\2\u0240\u0239\3\2\2\2\u0240\u023a\3\2" +
                    "\2\2\u0241\u0089\3\2\2\2\u0242\u0243\t\21\2\2\u0243\u008b\3\2\2\2\u0244" +
                    "\u024b\3\2\2\2\u0245\u0246\7\36\2\2\u0246\u0247\5\u00fe\u0080\2\u0247" +
                    "\u0248\5\u00fe\u0080\2\u0248\u0249\5\u008cG\2\u0249\u024b\3\2\2\2\u024a" +
                    "\u0244\3\2\2\2\u024a\u0245\3\2\2\2\u024b\u008d\3\2\2\2\u024c\u0251\3\2" +
                    "\2\2\u024d\u024e\5\u0090I\2\u024e\u024f\5\u0092J\2\u024f\u0251\3\2\2\2" +
                    "\u0250\u024c\3\2\2\2\u0250\u024d\3\2\2\2\u0251\u008f\3\2\2\2\u0252\u0253" +
                    "\t\22\2\2\u0253\u0091\3\2\2\2\u0254\u0255\5\u0100\u0081\2\u0255\u0256" +
                    "\7\177\2\2\u0256\u0257\7P\2\2\u0257\u0258\5\u0096L\2\u0258\u0259\5\u0094" +
                    "K\2\u0259\u025a\7q\2\2\u025a\u025b\5\u008eH\2\u025b\u0093\3\2\2\2\u025c" +
                    "\u025d\7_\2\2\u025d\u025e\5\u0094K\2\u025e\u025f\5\u0098M\2\u025f\u0260" +
                    "\7\21\2\2\u0260\u0261\5\u009cO\2\u0261\u0264\3\2\2\2\u0262\u0264\5\u009a" +
                    "N\2\u0263\u025c\3\2\2\2\u0263\u0262\3\2\2\2\u0264\u0095\3\2\2\2\u0265" +
                    "\u0269\3\2\2\2\u0266\u0269\7\23\2\2\u0267\u0269\7\33\2\2\u0268\u0265\3" +
                    "\2\2\2\u0268\u0266\3\2\2\2\u0268\u0267\3\2\2\2\u0269\u0097\3\2\2\2\u026a" +
                    "\u0270\3\2\2\2\u026b\u026c\7\36\2\2\u026c\u026d\5\u0094K\2\u026d\u026e" +
                    "\5\u0098M\2\u026e\u0270\3\2\2\2\u026f\u026a\3\2\2\2\u026f\u026b\3\2\2" +
                    "\2\u0270\u0099\3\2\2\2\u0271\u0272\7\177\2\2\u0272\u0276\5\u009eP\2\u0273" +
                    "\u0274\7}\2\2\u0274\u0276\5\u009eP\2\u0275\u0271\3\2\2\2\u0275\u0273\3" +
                    "\2\2\2\u0276\u009b\3\2\2\2\u0277\u027a\3\2\2\2\u0278\u027a\5\u009aN\2" +
                    "\u0279\u0277\3\2\2\2\u0279\u0278\3\2\2\2\u027a\u009d\3\2\2\2\u027b\u027f" +
                    "\3\2\2\2\u027c\u027d\7^\2\2\u027d\u027f\7}\2\2\u027e\u027b\3\2\2\2\u027e" +
                    "\u027c\3\2\2\2\u027f\u009f\3\2\2\2\u0280\u0281\5\u00a2R\2\u0281\u0282" +
                    "\7q\2\2\u0282\u0283\5\u00a4S\2\u0283\u0284\5\u00a6T\2\u0284\u0285\5\u00b2" +
                    "Z\2\u0285\u0286\5\u00ba^\2\u0286\u0287\5\u00c6d\2\u0287\u0288\5\u00d2" +
                    "j\2\u0288\u00a1\3\2\2\2\u0289\u028a\t\23\2\2\u028a\u00a3\3\2\2\2\u028b" +
                    "\u0293\3\2\2\2\u028c\u028d\5\u00e8u\2\u028d\u028e\5\u00f0y\2\u028e\u028f" +
                    "\5\u00f8}\2\u028f\u0290\5\u00fa~\2\u0290\u0291\7q\2\2\u0291\u0293\3\2" +
                    "\2\2\u0292\u028b\3\2\2\2\u0292\u028c\3\2\2\2\u0293\u00a5\3\2\2\2\u0294" +
                    "\u029a\3\2\2\2\u0295\u0296\5\u00a8U\2\u0296\u0297\5\u00aaV\2\u0297\u0298" +
                    "\7q\2\2\u0298\u029a\3\2\2\2\u0299\u0294\3\2\2\2\u0299\u0295\3\2\2\2\u029a" +
                    "\u00a7\3\2\2\2\u029b\u029c\t\24\2\2\u029c\u00a9\3\2\2\2\u029d\u02a2\3" +
                    "\2\2\2\u029e\u029f\5\u00acW\2\u029f\u02a0\5\u00aaV\2\u02a0\u02a2\3\2\2" +
                    "\2\u02a1\u029d\3\2\2\2\u02a1\u029e\3\2\2\2\u02a2\u00ab\3\2\2\2\u02a3\u02a6" +
                    "\5\u00aeX\2\u02a4\u02a6\5\u00b0Y\2\u02a5\u02a3\3\2\2\2\u02a5\u02a4\3\2" +
                    "\2\2\u02a6\u00ad\3\2\2\2\u02a7\u02a8\7k\2\2\u02a8\u00af\3\2\2\2\u02a9" +
                    "\u02aa\7Z\2\2\u02aa\u02ab\7P\2\2\u02ab\u02ac\7~\2\2\u02ac\u00b1\3\2\2" +
                    "\2\u02ad\u02b3\3\2\2\2\u02ae\u02af\5\u00b4[\2\u02af\u02b0\5\u00b6\\\2" +
                    "\u02b0\u02b1\7q\2\2\u02b1\u02b3\3\2\2\2\u02b2\u02ad\3\2\2\2\u02b2\u02ae" +
                    "\3\2\2\2\u02b3\u00b3\3\2\2\2\u02b4\u02b5\t\25\2\2\u02b5\u00b5\3\2\2\2" +
                    "\u02b6\u02bc\3\2\2\2\u02b7\u02b8\5\u00b8]\2\u02b8\u02b9\7\36\2\2\u02b9" +
                    "\u02ba\5\u00b6\\\2\u02ba\u02bc\3\2\2\2\u02bb\u02b6\3\2\2\2\u02bb\u02b7" +
                    "\3\2\2\2\u02bc\u00b7\3\2\2\2\u02bd\u02be\7}\2\2\u02be\u02bf\7e\2\2\u02bf" +
                    "\u02c0\7\177\2\2\u02c0\u02c1\7e\2\2\u02c1\u00b9\3\2\2\2\u02c2\u02c8\3" +
                    "\2\2\2\u02c3\u02c4\5\u00bc_\2\u02c4\u02c5\5\u00be`\2\u02c5\u02c6\7q\2" +
                    "\2\u02c6\u02c8\3\2\2\2\u02c7\u02c2\3\2\2\2\u02c7\u02c3\3\2\2\2\u02c8\u00bb" +
                    "\3\2\2\2\u02c9\u02ca\t\26\2\2\u02ca\u00bd\3\2\2\2\u02cb\u02d1\3\2\2\2" +
                    "\u02cc\u02cd\5\u00c0a\2\u02cd\u02ce\7\36\2\2\u02ce\u02cf\5\u00be`\2\u02cf" +
                    "\u02d1\3\2\2\2\u02d0\u02cb\3\2\2\2\u02d0\u02cc\3\2\2\2\u02d1\u00bf\3\2" +
                    "\2\2\u02d2\u02d3\7}\2\2\u02d3\u02d4\7~\2\2\u02d4\u02d5\7~\2\2\u02d5\u02db" +
                    "\5\u00c2b\2\u02d6\u02d7\7}\2\2\u02d7\u02d8\7~\2\2\u02d8\u02d9\7~\2\2\u02d9" +
                    "\u02db\5\u00c4c\2\u02da\u02d2\3\2\2\2\u02da\u02d6\3\2\2\2\u02db\u00c1" +
                    "\3\2\2\2\u02dc\u02dd\7a\2\2\u02dd\u02de\7P\2\2\u02de\u02df\7\177\2\2\u02df" +
                    "\u02e0\7\30\2\2\u02e0\u02e1\7P\2\2\u02e1\u02e2\7}\2\2\u02e2\u02e3\7}\2" +
                    "\2\u02e3\u02e4\7}\2\2\u02e4\u00c3\3\2\2\2\u02e5\u02e6\7 \2\2\u02e6\u02e7" +
                    "\7P\2\2\u02e7\u02e8\7}\2\2\u02e8\u02e9\7\16\2\2\u02e9\u02ea\7P\2\2\u02ea" +
                    "\u02eb\7}\2\2\u02eb\u02ec\7\30\2\2\u02ec\u02ed\7P\2\2\u02ed\u02ee\7}\2" +
                    "\2\u02ee\u02ef\7}\2\2\u02ef\u02f0\7}\2\2\u02f0\u00c5\3\2\2\2\u02f1\u02f7" +
                    "\3\2\2\2\u02f2\u02f3\5\u00c8e\2\u02f3\u02f4\5\u00caf\2\u02f4\u02f5\7q" +
                    "\2\2\u02f5\u02f7\3\2\2\2\u02f6\u02f1\3\2\2\2\u02f6\u02f2\3\2\2\2\u02f7" +
                    "\u00c7\3\2\2\2\u02f8\u02f9\t\27\2\2\u02f9\u00c9\3\2\2\2\u02fa\u0300\3" +
                    "\2\2\2\u02fb\u02fc\5\u00ccg\2\u02fc\u02fd\7\36\2\2\u02fd\u02fe\5\u00ca" +
                    "f\2\u02fe\u0300\3\2\2\2\u02ff\u02fa\3\2\2\2\u02ff\u02fb\3\2\2\2\u0300" +
                    "\u00cb\3\2\2\2\u0301\u0302\5\u00ceh\2\u0302\u0303\5\u00d0i\2\u0303\u00cd" +
                    "\3\2\2\2\u0304\u0305\7}\2\2\u0305\u0306\7e\2\2\u0306\u0307\7\177\2\2\u0307" +
                    "\u0308\7e\2\2\u0308\u00cf\3\2\2\2\u0309\u030a\7=\2\2\u030a\u030b\7P\2" +
                    "\2\u030b\u030c\7}\2\2\u030c\u030d\7R\2\2\u030d\u030e\7P\2\2\u030e\u030f" +
                    "\7}\2\2\u030f\u0310\7>\2\2\u0310\u0311\7P\2\2\u0311\u0312\7}\2\2\u0312" +
                    "\u0313\7c\2\2\u0313\u0314\7P\2\2\u0314\u0315\7e\2\2\u0315\u0316\7\177" +
                    "\2\2\u0316\u0323\7e\2\2\u0317\u0318\7R\2\2\u0318\u0319\7P\2\2\u0319\u031a" +
                    "\7}\2\2\u031a\u031b\7>\2\2\u031b\u031c\7P\2\2\u031c\u031d\7}\2\2\u031d" +
                    "\u031e\7c\2\2\u031e\u031f\7P\2\2\u031f\u0320\7e\2\2\u0320\u0321\7\177" +
                    "\2\2\u0321\u0323\7e\2\2\u0322\u0309\3\2\2\2\u0322\u0317\3\2\2\2\u0323" +
                    "\u00d1\3\2\2\2\u0324\u032a\3\2\2\2\u0325\u0326\5\u00d4k\2\u0326\u0327" +
                    "\5\u00d6l\2\u0327\u0328\7q\2\2\u0328\u032a\3\2\2\2\u0329\u0324\3\2\2\2" +
                    "\u0329\u0325\3\2\2\2\u032a\u00d3\3\2\2\2\u032b\u032c\t\30\2\2\u032c\u00d5" +
                    "\3\2\2\2\u032d\u0333\3\2\2\2\u032e\u032f\5\u00d8m\2\u032f\u0330\7\36\2" +
                    "\2\u0330\u0331\5\u00d6l\2\u0331\u0333\3\2\2\2\u0332\u032d\3\2\2\2\u0332" +
                    "\u032e\3\2\2\2\u0333\u00d7\3\2\2\2\u0334\u0335\7}\2\2\u0335\u0336\7}\2" +
                    "\2\u0336\u0337\7}\2\2\u0337\u0338\7a\2\2\u0338\u0339\7P\2\2\u0339\u033a" +
                    "\7}\2\2\u033a\u033b\7 \2\2\u033b\u033c\7P\2\2\u033c\u033d\7~\2\2\u033d" +
                    "\u00d9\3\2\2\2\u033e\u033f\t\31\2\2\u033f\u00db\3\2\2\2\u0340\u034c\3" +
                    "\2\2\2\u0341\u0342\7e\2\2\u0342\u0343\7\177\2\2\u0343\u0344\7e\2\2\u0344" +
                    "\u0345\5\u00dep\2\u0345\u0346\5\u00dco\2\u0346\u034c\3\2\2\2\u0347\u0348" +
                    "\7\177\2\2\u0348\u0349\5\u00dep\2\u0349\u034a\5\u00dco\2\u034a\u034c\3" +
                    "\2\2\2\u034b\u0340\3\2\2\2\u034b\u0341\3\2\2\2\u034b\u0347\3\2\2\2\u034c" +
                    "\u00dd\3\2\2\2\u034d\u035e\3\2\2\2\u034e\u034f\5\u0108\u0085\2\u034f\u0350" +
                    "\5\u00dep\2\u0350\u035e\3\2\2\2\u0351\u0352\7_\2\2\u0352\u0353\5\u00e0" +
                    "q\2\u0353\u0354\5\u00e4s\2\u0354\u0355\7\21\2\2\u0355\u0356\5\u00dep\2" +
                    "\u0356\u035e\3\2\2\2\u0357\u0358\7g\2\2\u0358\u0359\5\u00e0q\2\u0359\u035a" +
                    "\5\u00e4s\2\u035a\u035b\7\17\2\2\u035b\u035c\5\u00dep\2\u035c\u035e\3" +
                    "\2\2\2\u035d\u034d\3\2\2\2\u035d\u034e\3\2\2\2\u035d\u0351\3\2\2\2\u035d" +
                    "\u0357\3\2\2\2\u035e\u00df\3\2\2\2\u035f\u0365\5\u00e2r\2\u0360\u0361" +
                    "\5\u00e2r\2\u0361\u0362\7^\2\2\u0362\u0363\5\u00e2r\2\u0363\u0365\3\2" +
                    "\2\2\u0364\u035f\3\2\2\2\u0364\u0360\3\2\2\2\u0365\u00e1\3\2\2\2\u0366" +
                    "\u0367\5\u0108\u0085\2\u0367\u00e3\3\2\2\2\u0368\u036d\3\2\2\2\u0369\u036a" +
                    "\5\u00e0q\2\u036a\u036b\5\u00e4s\2\u036b\u036d\3\2\2\2\u036c\u0368\3\2" +
                    "\2\2\u036c\u0369\3\2\2\2\u036d\u00e5\3\2\2\2\u036e\u036f\t\32\2\2\u036f" +
                    "\u00e7\3\2\2\2\u0370\u0371\t\33\2\2\u0371\u00e9\3\2\2\2\u0372\u0373\t" +
                    "\34\2\2\u0373\u00eb\3\2\2\2\u0374\u037c\3\2\2\2\u0375\u0376\7\177\2\2" +
                    "\u0376\u037c\5\u00ecw\2\u0377\u0378\7e\2\2\u0378\u0379\7\177\2\2\u0379" +
                    "\u037a\7e\2\2\u037a\u037c\5\u00ecw\2\u037b\u0374\3\2\2\2\u037b\u0375\3" +
                    "\2\2\2\u037b\u0377\3\2\2\2\u037c\u00ed\3\2\2\2\u037d\u037e\7b\2\2\u037e" +
                    "\u037f\7P\2\2\u037f\u0380\7\u0080\2\2\u0380\u00ef\3\2\2\2\u0381\u0382" +
                    "\7\7\2\2\u0382\u0383\7P\2\2\u0383\u0384\7}\2\2\u0384\u00f1\3\2\2\2\u0385" +
                    "\u0386\7S\2\2\u0386\u0389\5\u00f0y\2\u0387\u0389\5\u00f0y\2\u0388\u0385" +
                    "\3\2\2\2\u0388\u0387\3\2\2\2\u0389\u00f3\3\2\2\2\u038a\u038d\3\2\2\2\u038b" +
                    "\u038d\5\u00f2z\2\u038c\u038a\3\2\2\2\u038c\u038b\3\2\2\2\u038d\u00f5" +
                    "\3\2\2\2\u038e\u038f\7x\2\2\u038f\u0390\7P\2\2\u0390\u0391\7}\2\2\u0391" +
                    "\u00f7\3\2\2\2\u0392\u0393\7\n\2\2\u0393\u0394\7P\2\2\u0394\u0395\7}\2" +
                    "\2\u0395\u00f9\3\2\2\2\u0396\u0397\7Y\2\2\u0397\u0398\7P\2\2\u0398\u0399" +
                    "\7}\2\2\u0399\u00fb\3\2\2\2\u039a\u039b\t\35\2\2\u039b\u00fd\3\2\2\2\u039c" +
                    "\u039f\5\u0108\u0085\2\u039d\u039f\7\177\2\2\u039e\u039c\3\2\2\2\u039e" +
                    "\u039d\3\2\2\2\u039f\u00ff\3\2\2\2\u03a0\u03a3\3\2\2\2\u03a1\u03a3\7\5" +
                    "\2\2\u03a2\u03a0\3\2\2\2\u03a2\u03a1\3\2\2\2\u03a3\u0101\3\2\2\2\u03a4" +
                    "\u03a5\5\u0106\u0084\2\u03a5\u03a6\7\177\2\2\u03a6\u03a7\5\u00ecw\2\u03a7" +
                    "\u03a8\7q\2\2\u03a8\u03b1\3\2\2\2\u03a9\u03aa\5\u0106\u0084\2\u03aa\u03ab" +
                    "\7e\2\2\u03ab\u03ac\7\177\2\2\u03ac\u03ad\7e\2\2\u03ad\u03ae\5\u00ecw" +
                    "\2\u03ae\u03af\7q\2\2\u03af\u03b1\3\2\2\2\u03b0\u03a4\3\2\2\2\u03b0\u03a9" +
                    "\3\2\2\2\u03b1\u0103\3\2\2\2\u03b2\u03b5\3\2\2\2\u03b3\u03b5\5\u0102\u0082" +
                    "\2\u03b4\u03b2\3\2\2\2\u03b4\u03b3\3\2\2\2\u03b5\u0105\3\2\2\2\u03b6\u03b7" +
                    "\t\36\2\2\u03b7\u0107\3\2\2\2\u03b8\u03b9\t\37\2\2\u03b9\u0109\3\2\2\2" +
                    ">\u0114\u0126\u0144\u014a\u0151\u0157\u015e\u016b\u0183\u018a\u0190\u0196" +
                    "\u019b\u01b4\u01ba\u01bf\u01c8\u01d0\u01db\u01e2\u01e7\u01f6\u021c\u0222" +
                    "\u0228\u0230\u0240\u024a\u0250\u0263\u0268\u026f\u0275\u0279\u027e\u0292" +
                    "\u0299\u02a1\u02a5\u02b2\u02bb\u02c7\u02d0\u02da\u02f6\u02ff\u0322\u0329" +
                    "\u0332\u034b\u035d\u0364\u036c\u037b\u0388\u038c\u039e\u03a2\u03b0\u03b4";
    public static final ATN _ATN =
            ATNSimulator.deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}