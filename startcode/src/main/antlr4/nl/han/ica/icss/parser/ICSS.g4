grammar ICSS;
stylesheet: styleRule+ EOF | variableAssignment+ styleRule+  EOF;

styleRule: selector body;

// the first symbols.

selector: tagSelector | idSelector | classSelector;
body: OPEN_BRACE declaration+ CLOSE_BRACE;

tagSelector: LOWER_IDENT;
idSelector: ID_IDENT;
classSelector: CLASS_IDENT;

// defining the content
declaration: propertyName COLON expression SEMICOLON;
propertyName: LOWER_IDENT;

expression: multiplyOperation | addOperation | subtractOperation | literal;
literal: COLOR | PIXELSIZE | TRUE | FALSE | PERCENTAGE | SCALAR | variableReference;


variableAssignment:  variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;
variableReference: CAPITAL_IDENT;


addOperation: literal PLUS expression;
multiplyOperation: literal MUL expression;
subtractOperation: literal MIN expression;


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

