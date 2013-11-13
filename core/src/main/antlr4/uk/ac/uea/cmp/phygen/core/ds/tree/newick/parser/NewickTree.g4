grammar NewickTree;

// antlr will generate java lexer and parser
options
{
  language = Java;
}


// ************ Tokens ***********

REAL :  DIGIT+ '.' DIGIT*   // match 1. 39. 3.14159 etc... (but no negative numbers)
     |  DIGIT* '.' DIGIT+
     |  DIGIT+
     ;

fragment DIGIT : [0-9];     // match single digit
fragment LETTER : [a-zA-Z];

// A String must consist of either lower or uppercase A-z characters, nothing fancy.  So we don't allow numbers or whitespace.
WORD : LETTER+
     | '"' DIGIT+ '"'
     | '"' LETTER+ '"';


// We're going to ignore all white space characters
WS : [ \t\r\n]+ -> skip;




// ************ Parser Rules *************

// This will be the entry point of our parser.
parse : branch ';' weight EOF;

// A Subtree can be a leaf node or an internal node
subtree : leaf | internal;

// A leaf node can just contain a name, doesn't make sense to have a length here
leaf : name;

// If there are brackets then we must have a name after (although the name can just be an empty string)
internal : '(' branchset ')' name;

// Can have a collection of comma separated branches in a single node, must ahve at least one branch)
branchset : branch (',' branch)*;

// A branch starts another subtree and can have a length at the end
branch : subtree length;

name    : // Empty
        | WORD;

// The length of this branch of the Newick Tree
length  : // Empty
        | ':' REAL;

weight  : // Empty
        | REAL;