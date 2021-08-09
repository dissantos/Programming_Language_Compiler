package Sintatico;
import Exceptions.Erro;
import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import lexicalAnalyzer.Lexer;
import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

import java.io.IOException;
import java.util.ArrayList;

public class AnalisadorSintatico {
    private Lexer lex;
    private Token token;
    private ArrayList<Erro> erros;

    public AnalisadorSintatico(Lexer lex) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        this.lex = lex;
        this.advance();
        this.erros = new ArrayList<Erro>();
    }

    private void eat(int tag) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        if (this.token.TAG == tag){
            advance();
        } else {
            int [] token = {tag};
            error(token);
        }
    }

    private void advance() throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        this.token = lex.scan(); //lê próximo token
    }

    private void error(int [] tokensEsperados) throws NotANumberException, LiteralWrongFormatException, IOException, WrongFormatException {
        Erro erro = new Erro(lex.line, this.token, tokensEsperados);
        erros.add(erro);
        advance();
    }

    // mulop -> * | / | &&
    private void mulop() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case '*':
                this.eat('*');
                break;
            case '/':
                this.eat('/');
                break;
            case Tag.AND:
                this.eat(Tag.AND);
                break;
            default:
                int [] tokens = {'*', '/', Tag.AND};
                error(tokens);
        }
    }

    // addop -> + | - | ||
    private void addop() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case '+':
                this.eat('+');
                break;
            case '-':
                this.eat('-');
                break;
            case Tag.OR:
                this.eat(Tag.OR);
                break;
            default:
                int [] tokens = {'+', '-', Tag.OR};
                error(tokens);
        }
    }

    // relop -> > | >= |  < | <= | != | ==
    private void relop() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case '>':
                this.eat('>');
                break;
            case '<':
                this.eat('<');
                break;
            case Tag.GE:
                this.eat(Tag.GE);
                break;
            case Tag.LE:
                this.eat(Tag.LE);
                break;
            case Tag.EQ:
                this.eat(Tag.EQ);
                break;
            case Tag.NE:
                this.eat(Tag.NE);
                break;
            default:
                int [] tokens = {'>', '<', Tag.GE, Tag.LE, Tag.EQ, Tag.NE};
                error(tokens);
        }
    }

    // factor -> ID |  NUMBER | (expression)
    private void factor() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
                this.eat(Tag.ID);
                break;
            case Tag.NUMBER:
                this.eat(Tag.NUMBER);
                break;
            case '(':
                this.eat('(');
                this.expression();
                this.eat(')');
                break;
            case Tag.LITERAL:
                this.eat(Tag.LITERAL);
                break;
            default:
                int [] tokens = {Tag.ID, Tag.NUMBER, '('};
                error(tokens);
        }
    }

    // factor -> -factor|  !factor | factor
    private void factora() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case '-':
                this.eat('-');
                this.factor();
                break;
            case '!':
                eat('!');
                this.factor();
                break;
            case Tag.ID:
            case Tag.NUMBER:
            case Tag.LITERAL:
            case '(':
                this.factor();
                break;
            default:
                int [] tokens = {'-', '!', Tag.ID, Tag.NUMBER, '('};
                error(tokens);
        }
    }

    // z -> mulop factora Z| lambda
    private void z() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case '*':
            case '/':
            case Tag.AND:
                this.mulop();
                this.factora();
                this.z();
                break;
            case ';':
            case ')':
            case '-':
            case '>':
            case Tag.GE:
            case '<':
            case Tag.LE:
            case Tag.NE:
            case Tag.EQ:
            case '+':
            case Tag.OR:
                break;
            default:
                int [] tokens = {'*', '/', Tag.AND, ';',')', '-', '>', Tag.GE, '<', Tag.LE, Tag.NE, Tag.EQ, '+', Tag.OR};
                error(tokens);
        }
    }

    // term -> factora Z
    private void term() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                this.factora();
                this.z();
                break;
            default:
                int [] tokens = {'(', '!','-', Tag.NUMBER};
                error(tokens);
        }
    }

    // a -> addop term A | epsilon
    private void a() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case '+':
            case '-':
            case Tag.OR:
                this.addop();
                this.term();
                this.a();
                break;
            case ';':
            case ')':
            case '>':
            case Tag.GE:
            case '<':
            case Tag.LE:
            case Tag.NE:
            case Tag.EQ:
                break;
            default:
                int [] tokens = {';',')', '-', '>', Tag.GE, '<', Tag.LE, Tag.NE, Tag.EQ, '+', Tag.OR};
                error(tokens);
        }
    }

    // simpleexpr -> term A
    private void simpleexpr() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                this.term();
                this.a();
                break;
            default:
                int [] tokens = {'(', '!','-', Tag.NUMBER};
                error(tokens);
        }
    }

    // expression -> simpleexpr expression’
    private void expression() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                this.simpleexpr();
                this.expressionL();
                break;
            default:
                int [] tokens = {'(', '!', '-', Tag.NUMBER};
                error(tokens);
        }
    }

    // expression' -> relop simpleexpr | epsilon
    private void expressionL() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case '>':
            case '<':
            case Tag.GE:
            case Tag.LE:
            case Tag.EQ:
            case Tag.NE:
                this.relop();
                this.simpleexpr();
                break;
            case ')':
                break;
            default:
                int [] tokens = { '>', Tag.GE, '<', Tag.LE, Tag.NE, Tag.EQ, ')'};
                error(tokens);
        }
    }

    // writable -> simpleexpr
    private void writable() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                this.simpleexpr();
                break;
            default:
                int [] tokens = {'(', '!', '-', Tag.NUMBER};
                error(tokens);
        }
    }

    // writestmt -> write (writable)
    private void writestmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.WRITE:
                this.eat(Tag.WRITE);
                this.eat('(');
                this.writable();
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.WRITE};
                error(tokens);
        }
    }

    // readstmt -> read (identifier)
    private void readstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.READ:
                this.eat(Tag.READ);
                this.eat('(');
                this.eat(Tag.ID);
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.READ};
                error(tokens);
        }
    }

    // dosuffix -> while ( condition )
    private void dosuffix() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.WHILE:
                this.eat(Tag.WHILE);
                this.condition();
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.WHILE};
                error(tokens);
        }
    }

    // dostmt -> do { stmtlist } dosuffix
    private void dostmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.DO:
                this.eat(Tag.DO);
                this.eat('{');
                this.stmtlist();
                this.eat('}');
                this.dosuffix();
                break;
            default:
                int [] tokens = {Tag.DO};
                error(tokens);
        }
    }

    // condition -> expression
    private void condition() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                this.expression();
                break;
            default:
                int [] tokens = {Tag.ID, '(', '!', '-', Tag.NUMBER};
                error(tokens);
        }
    }

    // ifstmt' -> else { stmtlist } | epsilon
    private void ifstmtPrime() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ELSE:
                this.eat(Tag.ELSE);
                this.eat('{');
                this.stmtlist();
                this.eat('}');
                break;
            case ';':
                break;
            default:
                int [] tokens = {Tag.ELSE, ';'};
                error(tokens);
        }
    }

    // ifstmt -> if ( condition ) { stmtlist } ifstmt'
    private void ifstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.IF:
                this.eat(Tag.IF);
                this.eat('(');
                this.condition();
                this.eat(')');
                this.eat('{');
                this.stmtlist();
                this.eat('}');
                this.ifstmtPrime();
                break;
            default:
                int [] tokens = {Tag.IF};
                error(tokens);
        }
    }

    // assignstmt -> identifier = simple_expr
    private void assignstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
                this.eat(Tag.ID);
                this.eat('=');
                this.simpleexpr();
                break;
            default:
                int [] tokens = {Tag.ID};
                error(tokens);
        }
    }

    // stmt -> writestmt | readstmt | dostmt | ifstmt | assignstmt
    private void stmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.WRITE:
                this.writestmt();
                break;
            case Tag.READ:
                this.readstmt();
                break;
            case Tag.DO:
                this.dostmt();
                break;
            case Tag.IF:
                this.ifstmt();
                break;
            case Tag.ID:
                this.assignstmt();
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID};
                error(tokens);
        }
    }

    // stmtaux -> stmt ; stmtaux | epsilon
    private void stmtaux() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.WRITE:
            case Tag.READ:
            case Tag.DO:
            case Tag.IF:
            case Tag.ID:
                this.stmt();
                this.eat(';');
                this.stmtaux();
                break;
            case '}':
            case Tag.STOP:
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID, '}', Tag.STOP};
                error(tokens);
        }
    }

    // stmtlist -> stmt ; stmtaux
    private void stmtlist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.WRITE:
            case Tag.READ:
            case Tag.DO:
            case Tag.IF:
            case Tag.ID:
                this.stmt();
                this.eat(';');
                this.stmtaux();
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID};
                error(tokens);
        }
    }

    // body ->  init stmtlist stop
    private void body() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.INIT:
                this.eat(Tag.INIT);
                this.stmtlist();
                this.eat(Tag.STOP);
                break;
            default:
                int [] tokens = {Tag.INIT};
                error(tokens);
        }
    }

    // type -> float | int | string
    private void type() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.FLOAT:
                this.eat(Tag.FLOAT);
                break;
            case Tag.INT:
                this.eat(Tag.INT);
                break;
            case Tag.STRING:
                this.eat(Tag.STRING);
                break;
            default:
                int [] tokens = {Tag.FLOAT, Tag.INT, Tag.STRING};
                error(tokens);
        }
    }

    // identlistaux -> , identifier identlistaux | epsilon
    private void identlistaux() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case ',':
                this.eat(',');
                this.eat(Tag.ID);
                this.identlistaux();
                break;
            case ';':
                break;
            default:
                int [] tokens = {',',';'};
                error(tokens);
        }
    }

    // identlist -> identifier identlistaux
    private void identlist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.ID:
                this.eat(Tag.ID);
                this.identlistaux();
                break;
            default:
                int [] tokens = {Tag.ID};
                error(tokens);
        }
    }

    // decl -> type identlist
    private void decl() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.FLOAT:
            case Tag.INT:
            case Tag.STRING:
                this.type();
                this.identlist();
                break;
            default:
                int [] tokens = {Tag.FLOAT, Tag.INT, Tag.STRING};
                error(tokens);
        }
    }

    // decllist -> decl ; decllist | epsilon
    private void decllist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.FLOAT:
            case Tag.INT:
            case Tag.STRING:
                this.decl();
                this.eat(';');
                this.decllist();
                break;
            case Tag.INIT:
                break;
            default:
                int [] tokens = {Tag.FLOAT, Tag.INT, Tag.STRING, Tag.INIT};
                error(tokens);
        }
    }

    // program ->  class identifier decllist body
    public void program() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.CLASS:
                this.eat(Tag.CLASS);
                this.eat(Tag.ID);
                this.decllist();
                this.body();
                break;
            default:
                int [] tokens = {Tag.CLASS};
                error(tokens);
        }
    }

    public ArrayList<Erro> getErros() {
        return erros;
    }
}
