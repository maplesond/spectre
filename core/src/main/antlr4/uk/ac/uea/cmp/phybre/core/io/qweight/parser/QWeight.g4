grammar QWeight;

// antlr will generate java lexer and parser
options
{
  language = Java;
}


// ************ Tokens ***********

NUMERIC :
      ('-')? DIGIT+ '.' DIGIT*   // match 1. 39. 3.14159 etc...
    | ('-')? DIGIT* '.' DIGIT+
    | ('-')? DIGIT+
    ;

// A token satisfing the regular expression [_\w]+[\d\w\._]*. Note that an single
//  _ is considered a valid identifier. In most contexts a single _ means a
//  "don't care identifier", simmilar to the _ meaning in prolog.
IDENTIFIER : (LETTER_US | DIGIT | '.' | '-' )+;

fragment DIGIT : [0-9];     // match single digit
fragment NZ_DIGIT : [1-9];
fragment LETTER : [a-zA-Z];
LETTER_US : [a-zA-Z_];

// We're going to ignore all excessive spaces and tabs and disregard newline characters completely
WS : [ \t]+ -> skip;
NL : [\r\n] -> skip;




// ************ Parser Rules *************

// This will be the entry point of our parser.
parse : nbtaxa description sense taxa quartets EOF;

nbtaxa : 'taxanumber' ':' NUMERIC ';';

description : 'description' ':' words ';';

words :
    // Empty
    | IDENTIFIER words
    ;


sense : 'sense' ':' sense_option ';';

sense_option : 'min' | 'max';

taxa :
    // Empty
    | taxon taxa
    ;

taxon : 'taxon' ':' NUMERIC 'name' ':' IDENTIFIER ';';

quartets :
    // Empty
    | quartet quartets
    ;

quartet : 'quartet' ':' x y u v 'weights' ':' w1 w2 w3 ';';

x : NUMERIC;
y : NUMERIC;
u : NUMERIC;
v : NUMERIC;
w1 : NUMERIC;
w2 : NUMERIC;
w3 : NUMERIC;

