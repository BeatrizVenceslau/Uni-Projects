%{
//-- don't change *any* of these: if you do, you'll break the compiler.
#include <algorithm>
#include <memory>
#include <cstring>
#include <cdk/compiler.h>
#include <cdk/types/types.h>
#include "ast/all.h"
#define LINE                         compiler->scanner()->lineno()
#define yylex()                      compiler->scanner()->scan()
#define yyerror(compiler, s)         compiler->scanner()->error(s)
//-- don't change *any* of these --- END!
%}

%parse-param {std::shared_ptr<cdk::compiler> compiler}

%union {
  //--- don't change *any* of these: if you do, you'll break the compiler.
  YYSTYPE() : type(cdk::primitive_type::create(0, cdk::TYPE_VOID)) {}
  ~YYSTYPE() {}
  YYSTYPE(const YYSTYPE &other) { *this = other; }
  YYSTYPE& operator=(const YYSTYPE &other) { type = other.type; return *this; }

  std::shared_ptr<cdk::basic_type> type;        /* expression type */
  //-- don't change *any* of these --- END!

  int                   i;	/* integer value */
  float                 f;    /* float value */
  std::string          *s;	/* symbol name or string literal */
  cdk::basic_node      *node;	/* node pointer */
  cdk::sequence_node   *sequence;
  cdk::expression_node *expression; /* expression nodes */
  cdk::lvalue_node     *lvalue;
  fir::block_node      *block;
  fir::prologue_node   *prologue;
  fir::body_node       *body;
};

%token <i> tINTEGER
%token <f> tFLOAT
%token <s> tIDENTIFIER tSTRING
%token tWHILE tDO tFINALLY 
%token tIF tTHEN tELSE
%token tWRITELN tWRITE tOR tAND
%token tRETURN tRESTART tLEAVE tSIZEOF
%token tNULL tVOID tINT
%token tFLOAT_TYPE tSTRING_TYPE
%token tWITH tAPPLY tIN tTO

%nonassoc tIF
%nonassoc tTHEN
%nonassoc tELSE

%nonassoc tWHILE
%nonassoc tDO
%nonassoc tFINALLY

%right '='
%left tOR
%left tAND
%nonassoc '~'
%left tEQ tNE
%left tGE tLE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc '?' tUNARY
%nonassoc '[' ']' '(' ')'
%nonassoc tSTRING


%type <node> if while declaration variable variableDecl privatevarDecl function functionDecl apply
%type <node> privatevar blockdecl argument instruction finallyInstr
%type <sequence> file declarations arguments blockdecls
%type <sequence> expressions instructions receiveArgs
%type <expression> retval expr functioncall
%type <block> block epilogue 
%type <prologue> prologue
%type <s> string
%type <lvalue> lval
%type <body> body
%type <type> type void

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%
                
file	: declarations                          { compiler->ast($$ = $1); }
      | /*empty*/                            { compiler->ast($$ = new cdk::sequence_node(LINE)); }
	;

declarations : declaration                   { $$ = new cdk::sequence_node(LINE, $1); }
             | declarations declaration      { $$ = new cdk::sequence_node(LINE, $2, $1); }
             ;

declaration : variable ';'                   { $$ = $1; }
            | variableDecl ';'               { $$ = $1; }
            | function                       { $$ = $1; }
            | functionDecl                   { $$ = $1; }
            ;

variable : type '*' tIDENTIFIER '=' expr        { $$ = new fir::variable_decl_node(LINE, $1, std::string("public"), *$3, $5); delete $3; }
         | type '?' tIDENTIFIER '=' expr        { $$ = new fir::variable_decl_node(LINE, $1, std::string("external"), *$3, $5); delete $3; }
         | privatevar                           { $$ = $1; } 
         ;

privatevar: type tIDENTIFIER '=' expr           { $$ = new fir::variable_decl_node(LINE, $1, std::string("private"), *$2, $4); delete $2; }
          ;

variableDecl : type '*' tIDENTIFIER             { $$ = new fir::variable_decl_node(LINE, $1, std::string("public"), *$3, nullptr); delete $3; }
             | type '?' tIDENTIFIER             { $$ = new fir::variable_decl_node(LINE, $1, std::string("external"), *$3, nullptr); delete $3; }
             | privatevarDecl                   { $$ = $1; }   
             ;

privatevarDecl: type tIDENTIFIER                { $$ = new fir::variable_decl_node(LINE, $1, std::string("private"), *$2, nullptr); delete $2; }
              ;

function : type '*' tIDENTIFIER '(' arguments ')' retval body    { $$ = new fir::function_definition_node(LINE, $1, std::string("public"), *$3, $5, $7, $8); delete $3; }
         | type '?' tIDENTIFIER '(' arguments ')' retval body    { $$ = new fir::function_definition_node(LINE, $1, std::string("external"), *$3, $5, $7, $8); delete $3;}
         | type tIDENTIFIER '(' arguments ')' retval body        { $$ = new fir::function_definition_node(LINE, $1, std::string("private"), *$2, $4, $6, $7); delete $2;}
         | void '*' tIDENTIFIER '(' arguments ')' body           { $$ = new fir::function_definition_node(LINE, $1, std::string("public"), *$3, $5, nullptr, $7); delete $3; }
         | void '?' tIDENTIFIER '(' arguments ')' body           { $$ = new fir::function_definition_node(LINE, $1, std::string("external"), *$3, $5, nullptr, $7); delete $3;}
         | void tIDENTIFIER '(' arguments ')' body               { $$ = new fir::function_definition_node(LINE, $1, std::string("private"), *$2, $4, nullptr, $6); delete $2;}
         ;

functionDecl : type tIDENTIFIER '(' arguments ')'                { $$ = new fir::function_definition_node(LINE, $1, std::string("private"), *$2, $4, nullptr, nullptr); delete $2;}
             | type '*' tIDENTIFIER '(' arguments ')'            { $$ = new fir::function_definition_node(LINE, $1, std::string("public"), *$3, $5, nullptr, nullptr); delete $3;}
             | type '?' tIDENTIFIER '(' arguments ')'            { $$ = new fir::function_definition_node(LINE, $1, std::string("external"), *$3, $5, nullptr, nullptr); delete $3;}
             | void tIDENTIFIER '(' arguments ')'                { $$ = new fir::function_definition_node(LINE, $1, std::string("private"), *$2, $4, nullptr, nullptr); delete $2;}
             | void '*' tIDENTIFIER '(' arguments ')'            { $$ = new fir::function_definition_node(LINE, $1, std::string("public"), *$3, $5, nullptr, nullptr); delete $3;}
             | void '?' tIDENTIFIER '(' arguments ')'            { $$ = new fir::function_definition_node(LINE, $1, std::string("external"), *$3, $5, nullptr, nullptr); delete $3;}
             ;

functioncall : tIDENTIFIER '(' receiveArgs ')'                   { $$ = new fir::function_call_node(LINE, *$1, $3); delete $1;}
             ;

arguments : /*empty*/                        { $$ = nullptr; }
          | argument                         { $$ = new cdk::sequence_node(LINE, $1); }
          | arguments ',' argument           { $$ = new cdk::sequence_node(LINE, $3, $1); }
          ;

argument : type tIDENTIFIER                  { $$ = new fir::variable_decl_node(LINE, $1, std::string("private"), *$2, nullptr); }
         ;

receiveArgs : expressions                    { $$ = $1; }
            | /* empty */                    { $$ = nullptr; }
            ;

retval : '-' '>' tINTEGER                    { $$ = new cdk::integer_node(LINE,$3); }
       | '-' '>' string                      { $$ = new cdk::string_node(LINE,$3); }
       | '-' '>' tFLOAT                      { $$ = new cdk::double_node(LINE,$3); }
       | '-' '>' tNULL                       { $$ = new fir::null_node(LINE); }
       | /* empty */                         { $$ = nullptr; }
       ;

body : prologue block epilogue               { $$ = new fir::body_node(LINE,$1,$2,$3); }
     ;

prologue : '@' block                         { $$ = new fir::prologue_node(LINE,$2); }
         | /* empty */                       { $$ = nullptr; }
         ;

epilogue : '>' '>' block                     { $$ = $3; }
         | /* empty */                       { $$ = nullptr; }

block : '{' blockdecls instructions '}'      { $$ = new fir::block_node(LINE, $2, $3); }
      | '{' blockdecls '}'                   { $$ = new fir::block_node(LINE, $2, nullptr); }
      | '{' instructions '}'                 { $$ = new fir::block_node(LINE, nullptr, $2); }
      | '{' '}'                              { $$ = nullptr; }
      ;

blockdecls: blockdecls blockdecl             { $$ = new cdk::sequence_node(LINE, $2, $1); }
          | blockdecl                        { $$ = new cdk::sequence_node(LINE, $1); }
          ;

blockdecl: privatevar ';'                    { $$ = $1; }
         | privatevarDecl ';'                { $$ = $1; }
         ;

instructions : instruction                   { $$ = new cdk::sequence_node(LINE, $1); }
             | instructions instruction      { $$ = new cdk::sequence_node(LINE, $2, $1); }
             ;

instruction : expressions ';'                { $$ = new cdk::sequence_node(LINE, $1); }
            | tWRITE expressions ';'         { $$ = new fir::write_node(LINE, $2, false); }
            | tWRITELN expressions ';'       { $$ = new fir::write_node(LINE, $2, true); }
            | tLEAVE tINTEGER ';'            { $$ = new fir::leave_node(LINE, $2); }
            | tRESTART tINTEGER ';'          { $$ = new fir::restart_node(LINE, $2); }
            | tLEAVE ';'                     { $$ = new fir::leave_node(LINE, 1); }
            | tRESTART ';'                   { $$ = new fir::restart_node(LINE, 1); }
            | tRETURN                        { $$ = new fir::return_node(LINE); }
            | if                             { $$ = $1; }
            | while                          { $$ = $1; }
            | block                          { $$ = $1; }  
            | apply ';'                      { $$ = $1; }
            ;

finallyInstr : expressions ';'               { $$ = new cdk::sequence_node(LINE, $1); }
             | tWRITE expressions ';'        { $$ = new fir::write_node(LINE, $2, false); }
             | tWRITELN expressions ';'      { $$ = new fir::write_node(LINE, $2, true); }
             | tRETURN                       { $$ = new fir::return_node(LINE); }
             | if                            { $$ = $1; }
             | while                         { $$ = $1; }
             | block                         { $$ = $1; }  
             | apply ';'                     { $$ = $1; }  
             ;

apply: tWITH expr tAPPLY tIDENTIFIER tIN expr tTO expr   { $$ = new fir::apply_node(LINE, $2, *$4, $6, $8); }
     ;

if : tIF expr tTHEN instruction tELSE instruction     { $$ = new fir::if_else_node(LINE, $2, $4, $6); }
   | tIF expr tTHEN instruction                       { $$ = new fir::if_node(LINE, $2, $4); }
   ;

while : tWHILE expr tDO instruction tFINALLY finallyInstr  { $$ = new fir::while_finally_node(LINE, $2, $4, $6); }
      | tWHILE expr tDO instruction                        { $$ = new fir::while_node(LINE, $2, $4); }
      ;

expressions : expr                                { $$ = new cdk::sequence_node(LINE, $1); }
            | expressions ',' expr                { $$ = new cdk::sequence_node(LINE, $3, $1); } 
            ;

expr : tINTEGER                         { $$ = new cdk::integer_node(LINE, $1); }
     | tFLOAT                           { $$ = new cdk::double_node(LINE, $1); }
     | string %prec tSTRING             { $$ = new cdk::string_node(LINE, $1); }
     | tSIZEOF '(' expr ')'             { $$ = new fir::sizeof_node(LINE, $3); }
     | '@'                              { $$ = new fir::read_node(LINE); }
     | tNULL                            { $$ = new fir::null_node(LINE); }
     | '+' expr %prec tUNARY            { $$ = new fir::identity_node(LINE, $2); }
     | '-' expr %prec tUNARY            { $$ = new cdk::neg_node(LINE, $2); }
     | expr '+' expr	               { $$ = new cdk::add_node(LINE, $1, $3); }
     | expr '-' expr	               { $$ = new cdk::sub_node(LINE, $1, $3); }
     | expr '*' expr	               { $$ = new cdk::mul_node(LINE, $1, $3); }
     | expr '/' expr	               { $$ = new cdk::div_node(LINE, $1, $3); }
     | expr '%' expr	               { $$ = new cdk::mod_node(LINE, $1, $3); }
     | expr '<' expr	               { $$ = new cdk::lt_node(LINE, $1, $3); }
     | expr '>' expr	               { $$ = new cdk::gt_node(LINE, $1, $3); }
     | expr tGE expr	               { $$ = new cdk::ge_node(LINE, $1, $3); }
     | expr tLE expr                    { $$ = new cdk::le_node(LINE, $1, $3); }
     | expr tNE expr	               { $$ = new cdk::ne_node(LINE, $1, $3); }
     | expr tEQ expr	               { $$ = new cdk::eq_node(LINE, $1, $3); }
     | expr tOR expr                    { $$ = new cdk::or_node(LINE, $1, $3); }
     | expr tAND expr                   { $$ = new cdk::and_node(LINE, $1, $3); }
     | '(' expr ')'                     { $$ = $2; }
     | '~' expr                         { $$ = new cdk::not_node(LINE, $2); }
     | lval                             { $$ = new cdk::rvalue_node(LINE, $1); } 
     | lval '?'                         { $$ = new fir::address_of_node(LINE, $1); }
     | lval '=' expr                    { $$ = new cdk::assignment_node(LINE, $1, $3); }
     |'[' expr ']'                      { $$ = new fir::alloc_node(LINE, $2); }
     | functioncall                     { $$ = $1; } 
     ;

lval : lval '[' expr ']' %prec tUNARY   { $$ = new fir::index_node(LINE, new cdk::rvalue_node(LINE, $1), $3); }
     | '(' expr ')' '[' expr ']'        { $$ = new fir::index_node(LINE, $2, $5); }
     | tIDENTIFIER %prec tUNARY         { $$ = new cdk::variable_node(LINE, $1); delete $1;}
     ;

type : tINT                 { $$ = cdk::primitive_type::create(4, cdk::TYPE_INT); }
     | tFLOAT               { $$ = cdk::primitive_type::create(8, cdk::TYPE_DOUBLE); }
     | tSTRING              { $$ = cdk::primitive_type::create(4, cdk::TYPE_STRING); }
     | '<' type '>'         { $$ = cdk::reference_type::create(4, $2); }      
     ;

void : tVOID                { $$ = cdk::primitive_type::create(0, cdk::TYPE_VOID); }
     ;

string: tSTRING                         { $$ = $1; }
      | string tSTRING  %prec tUNARY    { $$ = $1; $$->append(*$2); delete $2; }
      ;
%%
