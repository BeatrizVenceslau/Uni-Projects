%option c++ prefix="fir_scanner_" outfile="fir_scanner.cpp"
%option stack noyywrap yylineno 8bit
%{ 
// make relevant includes before including the parser's tab file
#include <string>
#include <cstring>
#include <sstream>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include "fir_parser.tab.h"

static std::ostringstream strlit;

// don't change this
#define yyerror LexerError
%}
%x X_STRING X_COMMENT X_SPECIALCASE
%%
  //{yydebug=1;}

 /*Comments*/
"!!".*\n               ; /* ignore comments */

"(*"                   yy_push_state(X_COMMENT);
<X_COMMENT>"*)"        yy_pop_state();
<X_COMMENT>.|\n        ; /* ignore content */

 /*Operators*/
">="                   return tGE; 
"<="                   return tLE; 
"=="                   return tEQ; 
"!="                   return tNE; 
"&&"                   return tAND;
"||"                   return tOR;
"sizeof"			         return tSIZEOF;

 /*teste pratico - apply*/
"with"                 return tWITH;
"apply"                return tAPPLY;
"in"                   return tIN;
".."                   return tTO;

 /*Read and Write*/
"write"				         return tWRITE;
"writeln"			         return tWRITELN;

 /*Cicles*/
"while"                return tWHILE; 
"do"                   return tDO;
"finally"              return tFINALLY;
"leave"                return tLEAVE;
"restart"              return tRESTART;
"return"               return tRETURN;

 /*Conditions*/
"if"                   return tIF;
"then"                 return tTHEN;
"else"                 return tELSE;

 /*Types*/
"int"                  return tINT;
"float"                return tFLOAT;
"string"               return tSTRING;
"void"                 return tVOID;
"null"                 return tNULL;

 /*Identifiers*/
[A-Za-z][A-Za-z0-9_]*  yylval.s = new std::string(yytext); return tIDENTIFIER;

 /*Strings*/
"\'"                    yy_push_state(X_STRING);
<X_STRING>"~"           yy_push_state(X_SPECIALCASE);
<X_STRING>"\'"          { yylval.s = new std::string(strlit.str());
                          strlit.str("");
                          yy_pop_state();
                          return tSTRING;
                        }

<X_STRING>\n            yyerror("Error in string");
<X_STRING>.             strlit << yytext;

<X_SPECIALCASE>n        {strlit << '\n'; yy_pop_state(); }
<X_SPECIALCASE>t        {strlit << '\t'; yy_pop_state(); }
<X_SPECIALCASE>r        {strlit << '\r'; yy_pop_state(); }
<X_SPECIALCASE>"~"      {strlit << '~'; yy_pop_state(); }
<X_SPECIALCASE>\'       {strlit << '\''; yy_pop_state(); }

<X_SPECIALCASE>[0-9A-Fa-f]{1,2} { strlit << (char)(unsigned char)std::stoul(yytext, NULL, 16);
                                  yy_pop_state();
                                }

<X_SPECIALCASE>.		    { strlit << *yytext; yy_pop_state(); }

 /*Floats*/
[0-9]+"."[0-9]+  { try { yylval.f = std::stod(yytext, NULL); 
                         return tFLOAT; 
                       } catch (std::out_of_range& e) { yyerror("Real out of range."); }
                 }

[0-9]+"."        { try { yylval.f = std::stod(yytext, NULL); 
                         return tFLOAT; 
                       } catch (std::out_of_range& e) { yyerror("Real out of range."); }
                 }

"."[0-9]+        { try { yylval.f = std::stod(yytext, NULL); 
                         return tFLOAT; 
                       } catch (std::out_of_range& e) { yyerror("Real out of range."); }
                 }

[0-9]+("."[0-9]+)?[Ee][-+]?[0-9]+  { try { yylval.f = std::stod(yytext, NULL); 
                                           return tFLOAT; 
                                         } catch (std::out_of_range& e) { yyerror("Real out of range."); }
                                   }


 /*Integers*/
0                       yylval.i = 0; return tINTEGER;
0[0]+                   yyerror("Invalid integer 0.");
[1-9][0-9]*             {  try { 
                            yylval.i = std::stoi(yytext, nullptr, 10);
                            return tINTEGER;
                         } catch (std::out_of_range& e) { yyerror("Integer out of range.");}
                        }



0[0-7]*                 {  try { 
                            yylval.i = std::stoi(yytext, nullptr, 8);
                            return tINTEGER;
                         } catch (std::out_of_range& e) { yyerror("Integer out of range.");}
                        } 

0[0-9]+                 yyerror("Invalid base 8 number.");

 /*Known symbols*/
[-()<>=+*/%;{}.,?\[\]@]       return *yytext;

 /*Whitespace*/
[ \t\n\r]+               ; /* ignore whitespace */

 /*Unknown character*/
.                      yyerror("Unknown character");

%%
