grammar NexusFile;

// antlr will generate java lexer and parser
options
{
  language = Java;
}


// ----------------------------------------------------------------------
// TOKENS
// ----------------------------------------------------------------------

NUMERIC :
      ('-')? DIGIT+ '.' DIGIT*   // match 1. 39. 3.14159 etc...
    | ('-')? DIGIT* '.' DIGIT+
    | ('-')? DIGIT+
    ;

fragment DIGIT : [0-9];     // match single digit
fragment NZ_DIGIT : [1-9];
fragment LETTER : [a-zA-Z];
LETTER_US : [a-zA-Z_];

// A String must consist of either lower or uppercase A-z characters, nothing fancy.  So we don't allow numbers or whitespace.
//WORD : LETTER+
//     | '"' DIGIT+ '"'
//     | '"' LETTER+ '"';

// A token satisfing the regular expression [_\w]+[\d\w\._]*. Note that an single
//  _ is considered a valid identifier. In most contexts a single _ means a
//  "don't care identifier", simmilar to the _ meaning in prolog.
IDENTIFIER : (LETTER_US | DIGIT | '.' | '-' )+;

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
  //| block_assumptions
    | block_trees
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
    | 'nchar' '=' NUMERIC
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
      'lower'
    | 'upper'
    | 'both'
    ;

diagonal :
      'diagonal'
    | 'nodiagonal'
    ;

labels : labels_header ('=' labels_option)?;

labels_header :
      'labels'
    | 'nolabels';

labels_option :
      'no'
    | 'yes'
    | 'false'
    | 'true'
    | 'left'
    | 'right';



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

nsplits : 'nsplits' '=' NUMERIC;

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

properties_splits_item : properties_splits_name ('=' NUMERIC)?;

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

cycle_item : NUMERIC;

matrix_splits_data :
    // Empty
    |  NUMERIC matrix_splits_list ',' matrix_splits_data
    ;

matrix_splits_list :
    // Empty
    | NUMERIC matrix_splits_list
    ;


// ----------------------------------------------------------------------
// QUARTETS
// ----------------------------------------------------------------------

block_quartets :
    quartets_block_header ';'
    matrix_header matrix_quartets_data ';';

quartets_block_header : 'quartets' | 'Quartets' | 'QUARTETS';

matrix_quartets_data :
    // Empty
    | matrix_quartet ',' matrix_splits_data
    ;

matrix_quartet : label_quartet weight_quartet x_quartet y_quartet sc_quartet u_quartet cs_quartet v_quartet;

label_quartet : IDENTIFIER;

weight_quartet : NUMERIC;

x_quartet : NUMERIC;

y_quartet : NUMERIC;

u_quartet : NUMERIC;

v_quartet : NUMERIC;

sc_quartet : IDENTIFIER;

cs_quartet: IDENTIFIER;



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
    | NUMERIC length
    ;

tree_label_optional :
    // Empty
    | tree_label
    ;

length :
    // Empty
    | ':' NUMERIC
    ;



// ----------------------------------------------------------------------------
// Matrix definition rules
// ----------------------------------------------------------------------------


matrix_header : 'matrix' | 'MATRIX';


matrix_data :
    // Empty
    | IDENTIFIER matrix_entry_list matrix_data
    | '\'' IDENTIFIER '\'' matrix_entry_list matrix_data
    ;

matrix_entry_list :
    // Empty
    | NUMERIC matrix_entry_list
    | '(' state_composed_word state_composed_list ')' matrix_entry_list
    | '{' state_composed_word state_composed_list '}' matrix_entry_list
    ;

state_composed_word :
      state_complex_word
    | state_complex_word ':' state_complex_word
    ;

state_complex_word :
      NUMERIC
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

ntax : 'ntax' '=' NUMERIC;

newtaxa :
      'newtaxa' ntax
    | ntax
    ;

newtaxa_optional :
    // Empty
    | newtaxa
    ;

properties : 'properties' | 'PROPERTIES';

reference :
    NUMERIC
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


