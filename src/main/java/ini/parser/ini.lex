
/*
 * This source code file is the exclusive property of its author. No copy or 
 * usage of the source code is permitted unless the author contractually 
 * allows it under the terms of a well-defined agreement.
 */

package ini.parser;

import java_cup.runtime.*;
import ini.ast.Token;

%%

%class IniScanner
%unicode
%cup
%line
%column
//%state BBLOCK
//%state BLOCK
%state STRING
%state CHAR
%state USERTYPE
%state LAMBDA
%state EMBEDED_EXPRESSION1
%state EMBEDED_EXPRESSION2
%state PREDICATE

%{
	StringBuffer string=new StringBuffer();
	String fileName;
	public void setFileName(String name) {
		fileName=name;
	}
	public String getFileName() {
		return fileName;
	}
	private Symbol symbol(int type) {
		return new Symbol(type,yyline,yycolumn,
		    new Token(type,fileName,yytext(),
		                    yyline+1,yycolumn+1,
		                    yycolumn+1+yytext().length()));
	}
	private Symbol emptyString() {
		return new Symbol(sym.STRING,yyline,yycolumn,
		    new Token(sym.STRING,fileName,"",
		                    yyline+1,yycolumn+1,
		                    yycolumn+1+0));
	}
	private Symbol symbol(int type, String text) {
		return new Symbol(sym.STRING,yyline,yycolumn,
		    new Token(sym.STRING,fileName,text,
		                    yyline+1,yycolumn+1,
		                    yycolumn+1+0));
	}
	
	//private Symbol symbol(int type,Object value) {
	//	return new Symbol(type,yyline,yycolumn,value);
	//}
%}	

LineTerminator= \n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]
WhiteSpaceChar = [ \t\f]
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

WhiteSpaceOrComment = ({WhiteSpace} | {TraditionalComment})+

StringText=(\\\"|[^\n\"]|\\{WhiteSpaceChar}*\\)*

TraditionalComment = "/*" [^*] ~"*/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator} | "#" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*/"
CommentContent = ( [^*] | \*+ [^/*] )*

Identifier = [a-z_][A-Za-z0-9_]*

TypeIdentifier = [A-Z][A-Za-z0-9_]*

DecIntegerLiteral = [0-9] | [1-9][0-9]*
DecFloatLiteral = {DecIntegerLiteral}\.{DecIntegerLiteral}

O = [^()]*
//FunctionalTypeEnding = {O} ({O} "(" {O} ({O} "(" {O} ({O} "(" {O} ")" {O})* {O} ")" {O})* {O} ")" {O})* {O} ")" {WhiteSpaceChar}* "=>"
ParameterList = {Identifier} | "(" {WhiteSpace}* {Identifier} {WhiteSpace}* ("," {WhiteSpace}* {Identifier} {WhiteSpace}*)* ")" | "(" {WhiteSpace}* ")"

%% 

<YYINITIAL> {
  /* keywords */
  "import"				{ return symbol(sym.IMPORT); }
  "function"		    { return symbol(sym.FUNCTION); }
  "process"				{ return symbol(sym.PROCESS); }
  "of" 		      		{ return symbol(sym.OF); }
  "return"		        { return symbol(sym.RETURN); }
  "true"		        { return symbol(sym.TRUE); }
  "false"		        { return symbol(sym.FALSE); }
  "this"		        { return symbol(sym.THIS); }
  "case"		        { return symbol(sym.CASE); }
  "default"		        { return symbol(sym.DEFAULT); }
  "else"		        { return symbol(sym.ELSE); }
  "declare"		        { return symbol(sym.DECLARE); }
  "declare" {WhiteSpaceChar}* "channel"	    { return symbol(sym.CHANNEL); }
  "declare" {WhiteSpaceChar}* "predicate"	{ yybegin(PREDICATE); return symbol(sym.PREDICATE); }
  "declare" {WhiteSpaceChar}* "type"		{ yybegin(USERTYPE); return symbol(sym.TYPE); }

  {DecIntegerLiteral}   { return symbol(sym.INT); }
  {DecFloatLiteral}		{ return symbol(sym.NUM); }
  "->"                  { return symbol(sym.ARROW_RIGHT); }
  ":"                   { return symbol(sym.COL); }
  "{"                   { return symbol(sym.LCPAREN); }
  "}"                   { return symbol(sym.RCPAREN); }
  "["                   { return symbol(sym.LSPAREN); }
  "]"                   { return symbol(sym.RSPAREN); }
  "<"                   { return symbol(sym.LT); }
  ">"                   { return symbol(sym.GT); }
  "<="                  { return symbol(sym.LTE); }
  ">="                  { return symbol(sym.GTE); }
  ","                   { return symbol(sym.COMMA); }
  "." / {Identifier} "(" { return symbol(sym.INVDOT); }
  "."                   { return symbol(sym.DOT); }
  "="                   { return symbol(sym.ASSIGN); }
  "=="                  { return symbol(sym.EQUALS); }
  "!="                  { return symbol(sym.NOTEQUALS); }
  "~"                   { return symbol(sym.MATCHES); }
  "||"                  { return symbol(sym.OROR); }
  "?"                   { return symbol(sym.QUESTION); }
  "$"                   { return symbol(sym.DOLLAR); }
  "&"                   { return symbol(sym.AND); }
  "&&"                  { return symbol(sym.ANDAND); }
  "=>"                  { return symbol(sym.IMPLIES); }
  "!"                   { return symbol(sym.NOT); }
  "+"                   { return symbol(sym.PLUS); }
  "++"                  { return symbol(sym.PLUSPLUS); }
  "-"                   { return symbol(sym.MINUS); }
  "--"                  { return symbol(sym.MINUSMINUS); }
  "/"                   { return symbol(sym.DIV); }
  "*"                   { return symbol(sym.MULT); }
  "|"                   { return symbol(sym.TUBE); }
  ".."                  { return symbol(sym.DOTDOT); }
  \"\"                  { return emptyString(); }
  "@"		        	{ return symbol(sym.AT); }
  {LineTerminator}      { return symbol(sym.LF); }
  "'"					{ yybegin(CHAR); }
  \"                    { yybegin(STRING); }
  {TraditionalComment}  { /* ignore */ }
  {DocumentationComment} { /* ignore */ }
  {EndOfLineComment} { return symbol(sym.LF); }
  {WhiteSpaceChar}      { /* ignore */ }

  // THIS LOOK-AHEAD SHOULD BE SOLVED IN THE PARSER FOR BETTER PERFS
  {ParameterList} / {WhiteSpace}* "=>"  { Symbol s = symbol(sym.LAMBDA); yypushback(yylength()); yybegin(LAMBDA); return s; }
  {ParameterList} / {WhiteSpace}* "~>"  { Symbol s = symbol(sym.LAMBDA); yypushback(yylength()); yybegin(LAMBDA); return s; }

  {Identifier}          { return symbol(sym.IDENTIFIER); }
  {TypeIdentifier}      { return symbol(sym.TIDENTIFIER); }
  
  "("                   { return symbol(sym.LPAREN); }
  ")"                   { return symbol(sym.RPAREN); }
  
}

/*<BBLOCK> {
  {Comment}          { /* ignore */ }
  {WhiteSpace}       { /* ignore */ }
  "{"                { yybegin(BLOCK); return symbol(sym.LCPAREN); }
}

<BLOCK> {
  ([^{}]|\n)*        { return symbol(sym.BLOCK); }
  "}"                { yybegin(YYINITIAL); return symbol(sym.RCPAREN); }
}*/

<STRING> {
  "\\{"				{ string.append("{"); }
  "{" / ([^}\n])* "}"   { yybegin(EMBEDED_EXPRESSION1); yypushback(yytext().length()); Symbol s = symbol(sym.STRING, string.toString()); string.setLength(0); return s; }
  (\\\"|[^\"\n]) 		{ string.append(yytext()); }
/*  (\\\"|[^\"\n])*     { return symbol(sym.STRING); }*/
/*  {StringText}        { return symbol(sym.STRING); }*/
  "\""                { yybegin(YYINITIAL); Symbol s = symbol(sym.STRING, string.toString()); string.setLength(0); return s; }
  "\n"                { yybegin(YYINITIAL); Symbol s = symbol(sym.STRING, string.toString()); string.setLength(0); return s; }
}

<CHAR> {
  (\\\'|[^\'\n])	  { return symbol(sym.CHAR); }
  "'"                 { yybegin(YYINITIAL); }
  "\n"                { yybegin(YYINITIAL); }
}

<USERTYPE> {
  "["                   { return symbol(sym.LSPAREN); }
  "]"                   { return symbol(sym.RSPAREN); }
  "="                   { return symbol(sym.ASSIGN); }
  ","                   { return symbol(sym.COMMA); }
  "*"                   { return symbol(sym.MULT); }
  "|"                   { return symbol(sym.TUBE); }
  ":"                   { return symbol(sym.COL); }
  "<"                   { return symbol(sym.LT); }
  {Identifier}          { return symbol(sym.IDENTIFIER); }
  {TypeIdentifier}      { return symbol(sym.TIDENTIFIER); }
  {Comment}             { /* ignore */ }
  {WhiteSpaceChar}      { /* ignore */ }
  // POTENTIAL PB UNDER WINDOWS?
  {LineTerminator} {LineTerminator}           { Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s; }
  {LineTerminator} / "type"         { Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s; }
  {LineTerminator} / "function"     { Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s; }
  {LineTerminator} / "process"      { Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s; }
  {LineTerminator} / "declare"      { Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s; }
  <<EOF>>               { yybegin(YYINITIAL); return symbol(sym.END); }
  {LineTerminator}                  { return symbol(sym.LF); }
}

<LAMBDA> {
  {Identifier}          { return symbol(sym.IDENTIFIER); }
  "("                   { return symbol(sym.LPAREN); }
  ")"                   { return symbol(sym.RPAREN); }
  ","                   { return symbol(sym.COMMA); }
  {Comment}             { /* ignore */ }
  {WhiteSpaceChar}      { /* ignore */ }
  "=>"                  { yybegin(YYINITIAL); return symbol(sym.IMPLIES); }
  "~>"                  { yybegin(YYINITIAL); return symbol(sym.SWING_RIGHT_ARROW); }
  "\n"                  { return symbol(sym.LF); }
}

<EMBEDED_EXPRESSION1> {
  "{" 					{ Symbol s = symbol(sym.PLUS); yybegin(EMBEDED_EXPRESSION2); yypushback(yylength());  return s; }
  "}"					{ yybegin(STRING); return symbol(sym.PLUS); }
}

<EMBEDED_EXPRESSION2> {
  "{" 					{ return symbol(sym.LPAREN); }
  {Identifier}          { return symbol(sym.IDENTIFIER); }
  {DecIntegerLiteral}   { return symbol(sym.INT); }
  {DecFloatLiteral}		{ return symbol(sym.NUM); }
  "("                   { return symbol(sym.LPAREN); }
  ")"                   { return symbol(sym.RPAREN); }
  "["                   { return symbol(sym.LSPAREN); }
  "]"                   { return symbol(sym.RSPAREN); }
  ","                   { return symbol(sym.COMMA); }
  "." / {Identifier} "(" { return symbol(sym.INVDOT); }
  "."                   { return symbol(sym.DOT); }
  "="                   { return symbol(sym.ASSIGN); }
  "=="                  { return symbol(sym.EQUALS); }
  "!="                  { return symbol(sym.NOTEQUALS); }
  "~"                   { return symbol(sym.MATCHES); }
  "||"                  { return symbol(sym.OROR); }
  "?"                   { return symbol(sym.QUESTION); }
  "$"                   { return symbol(sym.DOLLAR); }
  "&"                   { return symbol(sym.AND); }
  "&&"                  { return symbol(sym.ANDAND); }
  "=>"                  { return symbol(sym.IMPLIES); }
  "!"                   { return symbol(sym.NOT); }
  "+"                   { return symbol(sym.PLUS); }
  "++"                  { return symbol(sym.PLUSPLUS); }
  "-"                   { return symbol(sym.MINUS); }
  "--"                  { return symbol(sym.MINUSMINUS); }
  "/"                   { return symbol(sym.DIV); }
  "*"                   { return symbol(sym.MULT); }
  "}"					{ Symbol s = symbol(sym.RPAREN); yybegin(EMBEDED_EXPRESSION1); yypushback(yylength());  return s; }
}

<PREDICATE> {
  {Identifier}          { return symbol(sym.IDENTIFIER); }
  {DecIntegerLiteral}   { return symbol(sym.INT); }
  {DecFloatLiteral}		{ return symbol(sym.NUM); }
  "true"		        { return symbol(sym.TRUE); }
  "false"		        { return symbol(sym.FALSE); }
  "("                   { return symbol(sym.LPAREN); }
  ")"                   { return symbol(sym.RPAREN); }
  "[]"                  { return symbol(sym.ALWAYS); }
  "<>"                  { return symbol(sym.EVENTUALLY); }
  "=="                  { return symbol(sym.EQUALS); }
  "!="                  { return symbol(sym.NOTEQUALS); }
  "||"                  { return symbol(sym.OROR); }
  "&&"                  { return symbol(sym.ANDAND); }
  "=>"                  { return symbol(sym.IMPLIES); }
  "!"                   { return symbol(sym.NOT); }
  "+"                   { return symbol(sym.PLUS); }
  "-"                   { return symbol(sym.MINUS); }
  "/"                   { return symbol(sym.DIV); }
  "*"                   { return symbol(sym.MULT); }
  "<"                   { return symbol(sym.LT); }
  ">"                   { return symbol(sym.GT); }
  "<="                  { return symbol(sym.LTE); }
  ">="                  { return symbol(sym.GTE); }
  {WhiteSpaceChar}      { /* ignore */ }
  {LineTerminator}      { yybegin(YYINITIAL); return symbol(sym.END); }
}


.|\n { return symbol(-1, yytext()); }
