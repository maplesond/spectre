grammar NewickTree;

// antlr will generate java lexer and parser
options
{
  language = Java;
}

// ************ Tokens ***********

INT  : [0-9]+;
REAL : INT '.' INT | INT;

// A String must consist of either lower or uppercase A-z characters, nothing fancy
NAME : [a-zA-Z]+;

// We're going to ignore all white space characters
WS : [ \t\r\n]+ -> skip;

// The length of this branch of the Newick Tree
LENGTH : ':' REAL;


// ************ Parser Rules *************

// This will be the entry point of our parser.
parse : ( SubTree | Branch ) ';' (REAL)? EOF;

// A Subtree can be a leaf node or an internal node
SubTree : ('(' Branch (',' Branch)* ')')? (NAME)?;

// A Branch in the tree, may have length associated with it
Branch : SubTree (LENGTH)?;



