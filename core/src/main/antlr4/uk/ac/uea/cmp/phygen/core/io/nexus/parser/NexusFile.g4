grammar NexusFile;

// antlr will generate java lexer and parser
options
{
  language = Java;
}

// ----------------------------------------------------------------------
// TOKENS
// ----------------------------------------------------------------------

REAL : ('-')? DIGIT+ '.' DIGIT*   // match 1. 39. 3.14159 etc...
     | ('-')? DIGIT* '.' DIGIT+
     | ('-')? DIGIT+
     ;

INT  : ('-')? DIGIT+;

POSITIVE_INT : [1-9] DIGIT*;

NUMERIC : INT | REAL;

fragment DIGIT : [0-9];     // match single digit
fragment LETTER : [a-zA-Z];
LETTER_US : [a-zA-Z_];

// A String must consist of either lower or uppercase A-z characters, nothing fancy.  So we don't allow numbers or whitespace.
WORD : LETTER+
     | '"' DIGIT+ '"'
     | '"' LETTER+ '"';

// A token satisfing the regular expression [_\w]+[\d\w\._]*. Note that an single
//  _ is considered a valid identifier. In most contexts a single _ means a
//  "don't care identifier", simmilar to the _ meaning in prolog.
IDENTIFIER : LETTER_US (LETTER_US | DIGIT | '.')*;

// We're going to ignore all white space characters
WS : [ \t\r\n]+ -> skip;

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

block   : begin block_declaration ';' end;

begin   : 'begin' | 'BEGIN';

end     : 'end' | 'END' | 'endblock' | 'ENDBLOCK';


block_declaration :
      block_taxa
  //| block_characters
  //| block_unaligned
    | block_distances
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
    dimensions ntax ';'
    taxlabels;

taxa_header : 'taxa' | 'Taxa' | 'TAXA';





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
    | 'nchar' '=' POSITIVE_INT
    ;

format_distances :
    // Empty
    | 'format' format_distances_list ';'
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
    | IDENTIFIER matrix_entry_list matrix_data_rest
    ;

matrix_data_rest :
    // Empty
    | '\n' matrix_data
    ;

matrix_entry_list :
    // Empty
    | WORD matrix_entry_list
    | '(' state_composed_word state_composed_list ')' matrix_entry_list
    | '{' state_composed_word state_composed_list '}' matrix_entry_list
    ;

state_composed_word :
      state_complex_word
    | state_complex_word ':' state_complex_word
    ;

state_complex_word :
      POSITIVE_INT
    | WORD
    ;

state_composed_list :
    // Empty
    | state_composed_word state_composed_list
    ;




// ----------------------------------------------------------------------
// MISC RULES
// ----------------------------------------------------------------------


dimensions  : 'dimensions' | 'DIMENSIONS';

identifier_list :
    // Empty
    | IDENTIFIER identifier_list
    ;

labels :
      'labels'
    | 'nolabels'
    ;

// Might be that this is not expressive enough... original description:
// Any character except any of the following: \n\s()[]{}<>/\,;:=*^'"
missing : 'missing' '=' LETTER_US;

ntax : 'ntax' '=' POSITIVE_INT;

newtaxa :
      'newtaxa' ntax
    | ntax
    ;

newtaxa_optional :
    // Empty
    | newtaxa
    ;

reference :
    POSITIVE_INT
  | IDENTIFIER
  ;

star :
    // Empty
    | '*'
    ;

taxlabels : taxlabels_header IDENTIFIER identifier_list ';';

taxlabels_optional :
    // Empty
    | taxlabels
    ;

taxlabels_header : 'taxlabels' | 'TAXLABELS';


