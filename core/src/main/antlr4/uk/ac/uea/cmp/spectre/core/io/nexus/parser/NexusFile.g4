grammar NexusFile;

// antlr will generate java lexer and parser
options
{
  language = Java;
}


// ----------------------------------------------------------------------
// TOKENS
// ----------------------------------------------------------------------


FLOAT : ('-')? DIGIT* '.' DIGIT+ ('E' ('-')? DIGIT+)?;
INT : ('-')? (DIGIT)+;
SQSTRING : SQUOTE ( ~('\''|'\\') | ('\\' .) )* SQUOTE;
DQSTRING : DQUOTE ( ~('\''|'\\') | ('\\' .) )* DQUOTE;
ID : (DIGIT | '_' | '.' | '?' | '-' | '/' | CHAR)+;
DIGIT : [0-9];     // match single digit
CHAR : [a-zA-Z];
SQUOTE : '\'';
DQUOTE : '\"';
EQUALS : '=';


// A String must consist of either lower or uppercase A-z characters, nothing fancy.  So we don't allow numbers or whitespace.
//WORD : LETTER+
//     | '"' DIGIT+ '"'
//     | '"' LETTER+ '"';

// We're going to ignore all excessive spaces and tabs and disregard newline characters completely
WS : [ \t]+ -> skip;
NL : [\r\n] -> skip;

// Ignore anything between and including the square brackets
COMMENT : '[' .*? ']' -> skip;



// ----------------------------------------------------------------------
// HIGH LEVEL PARSER RULES
// ----------------------------------------------------------------------


// This will be the entry point of our parser.
parse   : nexus_header blocks EOF;

nexus_header : '#NEXUS' | '#nexus' | '#Nexus';

blocks  : // Empty
        | block blocks
        ;

block   : begin block_declaration end ';';

begin   : 'begin' | 'BEGIN' | 'Begin';

end     : 'end' | 'END' | 'End' | 'endblock' | 'ENDBLOCK' | 'EndBlock';

// we haven't implemented everything yet
block_declaration :
      block_taxa
    | block_characters
  //| block_unaligned
    | block_distances
    | block_splits
    | block_quartets
    | block_quadruples
  //| block_data
  //| block_codons
  //| block_sets
    | block_locations
    | block_assumptions
    | block_trees
    | block_network
  //| block_notes
  //| block_unknown
    | block_viewer
    ;





// ----------------------------------------------------------------------
// TAXA
// ----------------------------------------------------------------------

block_taxa :
    taxa_header ';'
    dimensions_taxa
    tax_labels
    tax_info;

taxa_header : 'taxa' | 'Taxa' | 'TAXA';

dimensions_taxa : dimensions ntax ';';

tax_info :
     // Empty
    | tax_info_header tax_info_entries
    ;

tax_info_header : 'taxinfo' | 'TAXINFO';

tax_info_entries :
     //Empty
    | identifier tax_info_entries
    ;

tax_info_entry : identifier;

// ----------------------------------------------------------------------
// Characters
// ----------------------------------------------------------------------

block_characters :
    characters_header ';'
    char_dimensions
    char_format
    char_matrix
    ;

characters_header : 'chracters' | 'Characters' | 'CHARACTERS';

char_dimensions : dimensions cd_nchar ';';

cd_nchar : ('nchar'|'NCHAR') EQUALS integer;

char_format: char_format_header char_format_options ';';

char_format_header : 'format' | 'Format' | 'FORMAT';

char_format_options :
      // Empty
    | char_format_option char_format_options
    ;

char_format_option : cf_datatype | cf_missing | cf_gap | cf_symbols | cf_labels | cf_transpose | cf_interleave;

cf_datatype : ('datatype'|'DATATYPE') EQUALS identifier;
cf_missing : ('missing'|'MISSING') EQUALS missing_option;
cf_gap : ('gap'|'GAP') EQUALS gap_option;
cf_symbols : ('symbols'|'SYMBOLS') EQUALS identifier;
cf_labels : ('labels'|'LABELS') EQUALS boolean_option;
cf_transpose : ('transpose'|'TRANSPOSE') EQUALS boolean_option;
cf_interleave : ('interleave'|'INTERLEAVE') EQUALS boolean_option;

missing_option : '?';

gap_option : '?' | '-' | 'N' | 'n';

char_matrix: matrix_header char_sequences ';';

char_sequences :
      // Empty
    | char_seq_entry char_sequences
    ;

char_seq_entry : identifier;



// ----------------------------------------------------------------------
// DISTANCES
// ----------------------------------------------------------------------

block_distances :
    distances_header ';'
    dimensions_distances
    format_distances
    tax_labels_optional
    matrix_header matrix_data ';';

distances_header : 'distances' | 'Distances' | 'DISTANCES';

dimensions_distances :
    // Empty
    | dimensions newtaxa nchar ';'
    ;

nchar :
    // Empty
    | 'nchar' EQUALS integer
    ;

format_distances :
    // Empty
    | format format_distances_list ';'
    ;


format_distances_list :
    // Empty
    | format_distances_item format_distances_list
    ;

format_distances_item :
      triangle
    | diagonal
    | labels
    | missing
    | interleave
    ;

interleave : 'interleave' (EQUALS labels_option)?;

triangle : 'triangle' EQUALS triangle_option;

triangle_option :
      'lower' | 'LOWER'
    | 'upper' | 'UPPER'
    | 'both' | 'BOTH'
    ;

diagonal :
      'diagonal' | 'DIAGONAL'
    | 'nodiagonal' | 'NODIAGONAL'
    ;

labels : labels_header (EQUALS labels_option)?;

labels_header :
      'labels'
    | 'nolabels';

labels_option :
      'no' | 'NO'
    | 'yes' | 'YES'
    | 'false' | 'FALSE'
    | 'true' | 'TRUE'
    | 'left' | 'LEFT'
    | 'right' | 'RIGHT';



// ----------------------------------------------------------------------
// SPLITS
// ----------------------------------------------------------------------

block_splits :
    splits_block_header ';'
    dimensions_splits
    format_splits
    properties_splits
    cycle
    matrix_header matrix_splits_data ';';

splits_block_header : 'splits' | 'Splits' | 'SPLITS';

dimensions_splits :
    // Empty
    | dimensions ntax nsplits ';'
    ;

format_splits :
    // Empty
    | format format_splits_list ';'
    ;

format_splits_list :
    // Empty
    | format_splits_item format_splits_list
    ;

format_splits_item :
      labels_splits
    | weights_splits
    | confidences_splits
    | intervals_splits
    ;

labels_splits : labels_header (EQUALS labels_option)?;

weights_splits : weights_header EQUALS boolean_option;

weights_header : 'weights';

confidences_splits : confidences_header EQUALS boolean_option;

confidences_header : 'confidences';

intervals_splits : intervals_header EQUALS boolean_option;

intervals_header : 'intervals';


properties_splits :
    | properties properties_splits_list ';'
    ;

properties_splits_list :
    // Empty
    | properties_splits_item properties_splits_list
    ;

properties_splits_item : properties_splits_name (EQUALS number)?;

properties_splits_name :
      'fit'
    | 'cyclic'
    | 'weakly_compatible'
    ;

cycle :
    // Empty
    | cycle_header cycle_item_list ';'
    ;

cycle_header : 'cycle' | 'CYCLE';

cycle_item_list :
    // Empty
    | cycle_item cycle_item_list
    ;

cycle_item : integer;

matrix_splits_data :
      // Empty
    | (identifier)? floatingp matrix_splits_list ',' matrix_splits_data
    ;

matrix_splits_list :
    // Empty
    | integer matrix_splits_list
    ;


// ----------------------------------------------------------------------
// QUARTETS
// ----------------------------------------------------------------------

block_quartets :
    quartets_block_header ';'
    matrix_header matrix_quartets_data ';';

quartets_block_header : 'quartets' | 'Quartets' | 'QUARTETS' | 'st_quartets';

matrix_quartets_data :
    // Empty
    | matrix_quartet ',' matrix_quartets_data
    ;

matrix_quartet : identifier integer integer integer identifier integer identifier integer;

// ----------------------------------------------------------------------
// QUADRUPLES
// ----------------------------------------------------------------------

block_quadruples :
    quadruples_block_header ';'
    dimensions_quadruples ';'
    format_quadruples ';'
    matrix_header matrix_quadruples_data ';';

quadruples_block_header : 'quadruples' | 'Quadruples' | 'QUADRUPLES';

dimensions_quadruples:
    // Empty
    | dimensions ntax nquadruples
    ;

nquadruples:
    // Empty
    | nquadruples_header EQUALS integer
    ;

nquadruples_header: 'nquadruples' | 'NQUADRUPLES';

format_quadruples:
    // Empty
    | format labels
    ;


matrix_quadruples_data :
    // Empty
    | matrix_quadruple ',' matrix_quadruples_data
    ;

matrix_quadruple : identifier ':' integer integer integer integer ':' floatingp floatingp floatingp floatingp floatingp floatingp floatingp;



// ----------------------------------------------------------------------------
// Locations
// ----------------------------------------------------------------------------

block_locations:
    locations_block_header ';'
    dimensions_locations ';'
    matrix_header matrix_locations_data ';';

locations_block_header : 'locations' | 'Locations' | 'LOCATIONS';

dimensions_locations:
    // Empty
    | dimensions ntax
    ;

matrix_locations_data:
    // Empty
    | location_entry ',' matrix_locations_data
    ;

location_entry : identifier floatingp floatingp;



// ----------------------------------------------------------------------------
// Assumption definition rules
// ----------------------------------------------------------------------------

block_assumptions : assumptions_block_header ';' assumptions_data;

assumptions_block_header : 'assumptions' | 'Assumptions' | 'ASSUMPTIONS' | 'st_Assumptions';

assumptions_data :
      // Empty
    | assumptions_data_entry ';' assumptions_data
    ;

assumptions_data_entry :
      identifier key_value_pairs
    | key_value_pair
    | identifier
    ;

key_value_pairs :
      // Empty
    | key_value_pair key_value_pairs
    ;

key_value_pair :
      identifier EQUALS identifier
    | identifier EQUALS integer
    ;


// ----------------------------------------------------------------------
// TREES
// ----------------------------------------------------------------------


block_trees : tree_block_header ';' translate newick_tree;

tree_block_header : 'trees' | 'Trees' | 'TREES';

translate :
    // Empty
    | translate_header reference reference translate_list ';'
    ;

translate_header : 'translate' | 'TRANSLATE';

translate_list :
    // Empty
    | ',' reference reference translate_list
    ;

newick_tree :
    // Empty
    | tree_header tree_rest
    ;

tree_header : 'tree' | 'utree' | 'TREE' | 'UTREE';

tree_rest : star identifier EQUALS root tree_definition ';' newick_tree;

tree_definition :
      '(' tree_definition tree_list ')' tree_label_optional
    | tree_label
    ;

root :
    // Empty
    | '[&R]'
    | '[&U]'
    ;

tree_list :
    // Empty
    | ',' tree_definition tree_list
    ;

tree_label :
      identifier length
    | integer length
    | floatingp length
    ;

tree_label_optional :
    // Empty
    | tree_label
    ;

length :
    // Empty
    | ':' integer
    ;


// ----------------------------------------------------------------------------
// Network definition rules
// ----------------------------------------------------------------------------

block_network : network_block_header ';'
                dimensions_network
                draw_network
                translate_network
                vertices_network
                vlabels_network
                edges_network;

network_block_header : 'network' | 'Network' | 'NETWORK';

dimensions_network :
      // Empty
    | dimensions ntax nvertices nedges ';'
    ;

draw_network :
      // Empty
    | draw_network_header draw_network_options ';'
    ;

draw_network_header : 'draw' | 'Draw' | 'DRAW';

draw_network_options :
      // Empty
    | draw_network_option draw_network_options
    ;

draw_network_option :
      scale_network
    | rotate_network
    ;

scale_network : 'to_scale';

rotate_network : 'rotateAbout' EQUALS floatingp;

translate_network :
      // Empty
    | translate_network_header translate_network_data ';'
    ;

translate_network_header : 'translate' | 'Translate' | 'TRANSLATE';

translate_network_data :
      // Empty
    | translate_network_entry ',' translate_network_data
    ;

translate_network_entry : integer name+;

vertices_network :
      // Empty
    | vertices_network_header vertices_network_data ';'
    ;

vertices_network_header : 'vertices' | 'Vertices' | 'VERTICES';

vertices_network_data :
      // Empty
    | vertices_network_entry ',' vertices_network_data
    ;

vertices_network_entry : integer floatingp floatingp vertex_options;

vertex_options :
      // Empty
    | vertex_option vertex_options
    ;

vertex_option : nv_shape | nv_width | nv_height | nv_b | nv_color_fg | nv_color_bg;

nv_shape : 's' EQUALS shape_option;

shape_option: 'n';

nv_width : 'w' EQUALS integer;

nv_height : 'h' EQUALS integer;

nv_b : 'b' EQUALS integer integer integer;

nv_color_fg : 'fg' EQUALS integer integer integer;

nv_color_bg : 'bg' EQUALS integer integer integer;


vlabels_network :
     // Empty
   | vlabels_network_header vlabels_network_data ';'
   ;

vlabels_network_header : 'vlabels' | 'Vlabels' | 'VLABELS';

vlabels_network_data :
     // Empty
   | vlabels_network_entry ',' vlabels_network_data
   ;

vlabels_network_entry : vlabels_network_label vlabels_options;

vlabels_network_label : integer name;

vlabels_options :
      // Empty
    | vlabels_option vlabels_options
    ;

vlabels_option : nl_l | nl_x | nl_y | nl_font | nl_color_lc | nl_color_bg;

nl_l : 'l' EQUALS integer;

nl_x : 'x' EQUALS integer;

nl_y : 'y' EQUALS integer;

nl_font : 'f' EQUALS identifier;

nl_color_lc : 'lc' EQUALS integer integer integer;

nl_color_bg : 'lk' EQUALS integer integer integer;


edges_network :
      // Empty
    | edges_network_header edges_network_data ';'
    ;

edges_network_header : 'edges' | 'Edges' | 'EDGES';

edges_network_data :
      // Empty
    | edges_network_entry ',' edges_network_data
    ;

edges_network_entry : integer integer integer edges_network_options;

edges_network_options :
      //Empty
    | edges_network_option edges_network_options
    ;

edges_network_option : ne_split | ne_width | ne_color | ne_unknown;

ne_split : 's' EQUALS integer;

ne_unknown : 'w' EQUALS floatingp;

ne_width : 'l' EQUALS integer;

ne_color : 'fg' EQUALS integer integer integer;


// ----------------------------------------------------------------------------
// Matrix definition rules
// ----------------------------------------------------------------------------


matrix_header : 'matrix' | 'MATRIX' | 'Matrix';


matrix_data :
    // Empty
    | identifier matrix_entry_list matrix_data
    ;

matrix_entry_list :
    // Empty
    | number matrix_entry_list
    | '(' state_composed_word state_composed_list ')' matrix_entry_list
    | '{' state_composed_word state_composed_list '}' matrix_entry_list
    ;

state_composed_word :
      state_complex_word
    | state_complex_word ':' state_complex_word
    ;

state_complex_word :
      number
    //| WORD
    ;

state_composed_list :
    // Empty
    | state_composed_word state_composed_list
    ;

// ----------------------------------------------------------------------------
// Network definition rules
// ----------------------------------------------------------------------------

block_viewer : viewer_block_header ';'
                dimensions_viewer
                matrix_viewer;

viewer_block_header : 'viewer' | 'Viewer' | 'VIEWER';

dimensions_viewer :
      // Empty
    | dimensions vwidth vheight ';'
    ;

vwidth : 'width' EQUALS integer;

vheight : 'height' EQUALS integer;

matrix_viewer :  matrix_header matrix_viewer_options ';';

matrix_viewer_options :
      // Empty
    | matrix_viewer_option matrix_viewer_options
    ;

matrix_viewer_option :
      vm_ratio
    | vm_angle
    | vm_showtrivial
    | vm_showrange
    | vm_showlabels
    | vm_colorlabels
    | vm_leaders
    | vm_leaderstroke
    | vm_leadercolor
    ;

vm_ratio : 'ratio' EQUALS floatingp;
vm_angle : 'angle' EQUALS floatingp;

vm_showtrivial : 'showtrivial' EQUALS boolean_option;
vm_showrange : 'showrange' EQUALS boolean_option;
vm_showlabels : 'showlabels' EQUALS boolean_option;
vm_colorlabels : 'colorlabels' EQUALS boolean_option;

vm_leaders : 'leaders' EQUALS identifier;

vm_leaderstroke : 'leaderstroke' EQUALS identifier;

vm_leadercolor : 'leadercolor' EQUALS integer integer integer;

// ----------------------------------------------------------------------
// MISC RULES
// ----------------------------------------------------------------------

boolean_option : 'no' | 'yes' | 'false' | 'true';

dimensions  : 'dimensions' | 'DIMENSIONS' | 'Dimensions';

format : 'format' | 'FORMAT' | 'Format';

identifier_list :
    // Empty
    |  identifier identifier_list
    ;

name_list :
    // Empty
    |  name name_list
    ;

identifier : SQSTRING | DQSTRING | id;
id : ID | DIGIT | CHAR;

name : SQSTRING | DQSTRING;

// Might be that this is not expressive enough... original description:
// Any character except any of the following: \n\s()[]{}<>/\,;:=*^'"
missing : 'missing' EQUALS (DIGIT | '_' | '.' | '?' | '-' | CHAR);

ntax : ntax_header EQUALS integer;

ntax_header : 'ntax' | 'NTAX';

newtaxa :
      'newtaxa' ntax
    | ntax
    ;

newtaxa_optional :
    // Empty
    | newtaxa
    ;

nsplits : 'nsplits' EQUALS integer;

nvertices : 'nvertices' EQUALS integer;

nedges : 'nedges' EQUALS integer;

properties : 'properties' | 'PROPERTIES';

reference :
    number
  | identifier
  ;

star :
    // Empty
    | '*'
    ;

tax_labels : tax_labels_header identifier_list ';';

tax_labels_optional :
    // Empty
    | tax_labels
    ;

tax_labels_header : 'taxlabels' | 'TAXLABELS';

integer : INT | DIGIT;
floatingp : FLOAT | DIGIT;

number : integer | floatingp | DIGIT;

