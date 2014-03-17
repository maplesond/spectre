grammar NewickTree;

// antlr will generate java lexer and parser
options
{
  language = Java;
}


// ************ Tokens ***********

WORD : (LETTER | OTHER | DIGIT)+;

LENGTH  : ':' DIGIT+ '.' DIGIT*   // match 1. 39. 3.14159 etc... (but no negative numbers)
        | ':' DIGIT* '.' DIGIT+
        | ':' DIGIT+
        ;

WEIGHT  : ';' DIGIT+ '.' DIGIT+   // match 1. 39. 3.14159 etc... (but no negative numbers)
        | ';' DIGIT* '.' DIGIT*
        | ';' DIGIT+
        ;


fragment DIGIT : [0-9];     // match single digit
fragment LETTER : [a-zA-Z];
fragment OTHER : [_-@#~];

// We're going to ignore all white space characters
WS : [ \t\r\n]+ -> skip;




// ************ Parser Rules *************

// This will be the entry point of our parser.
parse : branch weight EOF;

// A Subtree can be a leaf node or an internal node
subtree : leaf | internal;

// A leaf node can just contain a name, doesn't make sense to have a length here
leaf : name;

// If there are brackets then we must have a name after (although the name can just be an empty string)
internal : '(' branchset ')' name;

// Can have a collection of comma separated branches in a single node, must have at least one branch)
branchset : branch (',' branch)*;

// A branch starts another subtree and can have a length at the end
branch : subtree length;

name    : // Empty
        | '\'' WORD '\''
        | '\"' WORD '\"'
        | WORD
        ;

// The length of this branch of the Newick Tree
length  : // Empty
        | LENGTH;

weight  : ';'
        | WEIGHT;
