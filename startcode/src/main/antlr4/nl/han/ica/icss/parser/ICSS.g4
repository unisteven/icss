grammar ICSS;
stylesheet: item+ | EOF;

item: tagSelector+ | idSelector+ | classSelector+;

// the first symbols.
tagSelector: LOWER_IDENT OPEN_BRACE decleration+ CLOSE_BRACE;
idSelector: ID_IDENT OPEN_BRACE decleration+ CLOSE_BRACE;
classSelector: CLASS_IDENT OPEN_BRACE decleration+ CLOSE_BRACE;

// defining the content
decleration: property expression SEMICOLON;
property: LOWER_IDENT COLON;
expression: COLOR | PIXELSIZE | TRUE | FALSE;




//--- LEXER: ---
// IF support:
IF: 'if';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z0-9\-]+;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---

