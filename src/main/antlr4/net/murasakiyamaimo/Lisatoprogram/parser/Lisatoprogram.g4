grammar Lisatoprogram;

options { language = Java; }

// プログラムの開始ルール
program : (statement | functionDeclaration)+ EOF;

// 各ステートメント
statement : variableDeclaration ';'
          | printStatement ';'
          | forStatement
          | variableAssignment ';'
          | ifStatement
          | functionCall ';'
          ;

// if else: palusta (条件) {} vista (条件) {} vista {}
ifStatement
    : PALUSTA '(' ifCondition=expression ')' '{' ifStatements+=statement+ '}'
      ( elseIfBlock+=elseIfStatement )*
      ( VISTA '{' elseStatements+=statement+ '}' )?
    ;
elseIfStatement
    : VISTA PALUSTA '(' elseIfCondition=expression ')' '{' elseIfStatements+=statement+ '}'
    ;

// 関数定義
functionDeclaration : SATI IDENTIFIER '('(parameter (',' parameter)*)? ')' (SIS type)? KATE '{' statement* (JASEPE expression ';')? '}';
parameter : IDENTIFIER SIS type;

// 関数呼び出し
functionCall : IDENTIFIER '(' (expression (',' expression)*)? ')';

// ループ(for): pilike [カウンタ変数] pas [ループ回数]
forStatement : PILIKE IDENTIFIER PAS expression '{' statement+ '}';

// 変数宣言: [変数名] kate [初期値] sis [型名]
variableDeclaration : IDENTIFIER KATE expression SIS type;

// 変数代入: [変数名] kate [値]
variableAssignment : IDENTIFIER KATE expression;

// print文: print [式]
printStatement : 'janase' expression;

// 式 (リテラルまたは識別子をサポート)
expression : STRING_LITERAL  # StringLiteral
           | INTEGER_LITERAL # IntegerLiteral
           | BOOLEAN_LITERAL # BooleanLiteral
           | IDENTIFIER      # Identifier
           | functionCall    # FunctionCallExpression
           | expression (comparisonOperator) expression # ComparisonExpression
           ;

// 型名
type : 'String'  # StringType
     | 'int'     # IntegerType
     | 'boolean' # BooleanType
     ;

// 字句ルール
JASEPE          : 'jasepe';
PALUSTA         : 'palusta';
VISTA           : 'vista';
PILIKE          : 'pilike';
PAS             : 'pas';
KATE            : 'kate';
SATI            : 'sati';
SIS             : 'sis';
STRING_LITERAL  : '"' (~["\\] | '\\' .)* '"';
INTEGER_LITERAL : [0-9]+;
BOOLEAN_LITERAL : 'tuni' | 'jatuni';
IDENTIFIER      : [a-zA-Z_][a-zA-Z0-9_]*;
comparisonOperator : '=='|'!='|'<'|'>'|'<='|'>=';
WHITESPACE      : [ \t\r\n]+ -> skip; // スペースやタブは無視