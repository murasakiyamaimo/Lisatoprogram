grammar Lisatoprogram;

options { package = 'net.murasakiyamaimo.Lisatoprogram.parser'; } // 生成されるJavaコードのパッケージ

// プログラムの開始ルール
program : statement+ EOF;

// 各ステートメント
statement : variableDeclaration ';'
          | printStatement ';'
          | pilikeStatement
          ;

// ループ(for): pilike [カウンタ変数] pas [ループ回数]
pilikeStatement : PILIKE IDENTIFIER PAS expression '{' statement+ '}';

// 変数宣言: [変数名] kate [初期値] sis [型名]
variableDeclaration : IDENTIFIER KATE expression SIS type;

// print文: print [式]
printStatement : 'janase' expression;

// 式 (リテラルまたは識別子をサポート)
expression : STRING_LITERAL  # StringLiteral
           | INTEGER_LITERAL # IntegerLiteral
           | BOOLEAN_LITERAL # BooleanLiteral
           | IDENTIFIER      # Identifier
           ;

// 型名
type : 'String'  # StringType
     | 'int'     # IntegerType
     | 'boolean' # BooleanType
     ;

// 字句ルール
PILIKE          : 'pilike';
PAS             : 'pas';
KATE            : 'kate';
SIS             : 'sis';
STRING_LITERAL  : '"' (~["\\] | '\\' .)* '"';
INTEGER_LITERAL : [0-9]+;
BOOLEAN_LITERAL : 'tuni' | 'jatuni';
IDENTIFIER      : [a-zA-Z_][a-zA-Z0-9_]*;
WHITESPACE      : [ \t\r\n]+ -> skip; // スペースやタブは無視