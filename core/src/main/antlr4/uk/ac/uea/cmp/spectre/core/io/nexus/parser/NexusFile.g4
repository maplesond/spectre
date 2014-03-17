grammar NexusFile;

// antlr will generate java lexer and parser
options
{
  language = Java;
}


// ----------------------------------------------------------------------
// TOKENS
// ----------------------------------------------------------------------

INT : ('-')? DIGIT+;
FLOAT : ('-')? DIGIT* '.' DIGIT+ ('E' ('-')? DIGIT+)?;


// A token satisfing the regular expression [_\w]+[\d\w\._]*. Note that an single
//  _ is considered a valid identifier. In most contexts a single _ means a
//  "don't care identifier", simmilar to the _ meaning in prolog.
IDENTIFIER : (LETTER_US | DIGIT | '.' | '-' )+;

fragment DIGIT : [0-9];     // match single digit
fragment NZ_DIGIT : [1-9];
fragment LETTER : [a-zA-Z];
LETTER_US : [a-zA-Z_];

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

nexus_header : '#NEXUS' | '#nexus';

blocks  : // Empty
        | block blocks
        ;

block   : begin block_declaration end ';';

begin   : 'begin' | 'BEGIN';

end     : 'end' | 'END' | 'endblock' | 'ENDBLOCK';

// we haven't implemented everything yet
block_declaration :
      block_taxa
  //| block_characters
  //| block_unaligned
    | block_distances
    | block_splits
    | block_quartets
  //| block_data
  //| block_codons
  //| block_sets
    | block_assumptions
    | block_trees
    | block_network
  //| block_notes
  //| block_unknown
    ;





// ----------------------------------------------------------------------
// TAXA
// ----------------------------------------------------------------------

block_taxa :
    taxa_header ';'
    dimensions_taxa
    taxlabels;

taxa_header : 'taxa' | 'Taxa' | 'TAXA';

dimensions_taxa : dimensions ntax ';';




// ----------------------------------------------------------------------
// DISTANCES
// ----------------------------------------------------------------------

block_distances :
    distances_header ';'
    dimensions_distances
    format_distances
    taxlabels_optional
    matrix_header matrix_data ';';

distances_header : 'distances' | 'Distances' | 'DISTANCES';

dimensions_distances :
    // Empty
    | dimensions newtaxa nchar ';'
    ;

nchar :
    // Empty
    | 'nchar' '=' INT
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
    | 'interleave'
    ;

triangle : 'triangle' '=' triangle_option;

triangle_option :
      'lower' | 'LOWER'
    | 'upper' | 'UPPER'
    | 'both' | 'BOTH'
    ;

diagonal :
      'diagonal' | 'DIAGONAL'
    | 'nodiagonal' | 'NODIAGONAL'
    ;

labels : labels_header ('=' labels_option)?;

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

labels_splits : labels_header ('=' labels_option)?;

weights_splits : weights_header '=' boolean_option;

weights_header : 'weights';

confidences_splits : confidences_header '=' boolean_option;

confidences_header : 'confidences';

intervals_splits : intervals_header '=' boolean_option;

intervals_header : 'intervals';


properties_splits :
    | properties properties_splits_list ';'
    ;

properties_splits_list :
    // Empty
    | properties_splits_item properties_splits_list
    ;

properties_splits_item : properties_splits_name ('=' number)?;

properties_splits_name :
      'fit'
    | 'cyclic'
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

cycle_item : INT;

matrix_splits_data :
      // Empty
    | matrix_split_identifier FLOAT matrix_splits_list ',' matrix_splits_data
    ;

matrix_split_identifier :
      // Empty
    |  '\'' INT '\''
    | INT
    ;


matrix_splits_list :
    // Empty
    | INT matrix_splits_list
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
    | matrix_quartet ',' matrix_splits_data
    ;

matrix_quartet : label_quartet weight_quartet x_quartet y_quartet sc_quartet u_quartet cs_quartet v_quartet;

label_quartet : IDENTIFIER;

weight_quartet : INT;

x_quartet : INT;

y_quartet : INT;

u_quartet : INT;

v_quartet : INT;

sc_quartet : IDENTIFIER;

cs_quartet: IDENTIFIER;


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
      IDENTIFIER key_value_pairs
    | key_value_pair
    | IDENTIFIER
    ;

key_value_pairs :
      // Empty
    | key_value_pair key_value_pairs
    ;

key_value_pair :
      IDENTIFIER '=' IDENTIFIER
    | IDENTIFIER '=' INT
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

tree_rest : star IDENTIFIER '=' root tree_definition ';' newick_tree;

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
      IDENTIFIER length
    | INT length
    ;

tree_label_optional :
    // Empty
    | tree_label
    ;

length :
    // Empty
    | ':' INT
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

rotate_network : 'rotateAbout' '=' FLOAT;

translate_network :
      // Empty
    | translate_network_header translate_network_data ';'
    ;

translate_network_header : 'translate' | 'Translate' | 'TRANSLATE';

translate_network_data :
      // Empty
    | translate_network_entry ',' translate_network_data
    ;

translate_network_entry : INT '\'' IDENTIFIER '\'';

vertices_network :
      // Empty
    | vertices_network_header vertices_network_data ';'
    ;

vertices_network_header : 'vertices' | 'Vertices' | 'VERTICES';

vertices_network_data :
      // Empty
    | vertices_network_entry ',' vertices_network_data
    ;

vertices_network_entry :
      INT FLOAT FLOAT vertices_2d_data
    | INT FLOAT FLOAT vertices_3d_data
    ;

vertices_2d_data : 's' '=' IDENTIFIER 'b' '=' INT INT INT;
vertices_3d_data : 'w' '=' INT 'h' '=' INT 'b' '=' INT INT INT;


vlabels_network :
     // Empty
   | vlabels_network_header vlabels_network_data ';'
   ;

vlabels_network_header : 'vlabels' | 'Vlabels' | 'VLABELS';

vlabels_network_data :
     // Empty
   | vlabels_network_entry ',' vlabels_network_data
   ;

vlabels_network_entry : vlabels_network_label vlabels_data;

vlabels_network_label : INT '\'' IDENTIFIER '\'';

vlabels_data :
      'l' '=' INT 'x' '=' INT 'y' '=' INT 'f' '=' '\'' IDENTIFIER '\''
    | 'x' '=' INT 'y' '=' INT 'f' '=' '\'' IDENTIFIER '\'';

edges_network :
      // Empty
    | edges_network_header edges_network_data ';'
    ;

edges_network_header : 'edges' | 'Edges' | 'EDGES';

edges_network_data :
      // Empty
    | edges_network_entry ',' edges_network_data
    ;

edges_network_entry : INT INT INT 's' '=' INT 'w' '=' FLOAT;


// ----------------------------------------------------------------------------
// Matrix definition rules
// ----------------------------------------------------------------------------


matrix_header : 'matrix' | 'MATRIX';


matrix_data :
    // Empty
    | '\'' IDENTIFIER '\'' matrix_entry_list matrix_data
    | IDENTIFIER matrix_entry_list matrix_data
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




// ----------------------------------------------------------------------
// MISC RULES
// ----------------------------------------------------------------------

boolean_option : 'no' | 'yes' | 'false' | 'true';

dimensions  : 'dimensions' | 'DIMENSIONS';

format : 'format' | 'FORMAT';

identifier_list :
    // Empty
    | IDENTIFIER identifier_list
    | '\'' IDENTIFIER '\'' identifier_list
    ;

// Might be that this is not expressive enough... original description:
// Any character except any of the following: \n\s()[]{}<>/\,;:=*^'"
missing : 'missing' '=' LETTER_US;

ntax : 'ntax' '=' INT;

newtaxa :
      'newtaxa' ntax
    | ntax
    ;

newtaxa_optional :
    // Empty
    | newtaxa
    ;

nsplits : 'nsplits' '=' INT;

nvertices : 'nvertices' '=' INT;

nedges : 'nedges' '=' INT;

properties : 'properties' | 'PROPERTIES';

reference :
    number
  | IDENTIFIER
  ;

star :
    // Empty
    | '*'
    ;

taxlabels :
      taxlabels_header IDENTIFIER identifier_list ';'
    | taxlabels_header '\'' IDENTIFIER '\'' identifier_list ';'
    ;

taxlabels_optional :
    // Empty
    | taxlabels
    ;

taxlabels_header : 'taxlabels' | 'TAXLABELS';


number : INT | FLOAT;