package Sintatico;
import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import lexicalAnalyzer.Lexer;
import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

public class AnalisadorSintatico {
    private Lexer lex;
    private Token token;

    public AnalisadorSintatico(Lexer lex) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        lex = lex;
        this.advance();
    }

    private void eat(int tag) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        if (this.token.TAG == tag){
            advance();
        } else{
            //TO DO
        }
    }

    private void advance() throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        this.token = lex.scan(); //lê próximo token
    }

    // mulop -> * | / | &&
    private void mulop() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default:   //TO DO
        }
    }

    // addop -> + | - | ||
    private void addop() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default:   //TO DO
        }
    }

    // relop -> > | >= |  < | <= | != | ==
    private void relop() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default:   //TO DO
        }
    }

    // factor -> ID |  NUMBER | (expression)
    private void factor() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default:   //TO DO
        }
    }

    // factor -> -factor|  !factor | factor
    private void factora() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            case '(':
                this.factor();
                break;
            default: //TO DO
        }
    }

    // z -> mulop factora Z| epsilon
    private void z() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case '*':
            case '/':
            case Tag.AND:
                this.mulop();
                this.factora();
                this.z();
                break;
            default:
                this.advance();
        }
    }

    // term -> factora Z
    private void term() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
                this.factora();
                this.z();
                break;
            default: //TO DO
        }
    }

    // a -> addop term A | epsilon
    private void a() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case '+':
            case '-':
            case Tag.OR:
                this.addop();
                this.term();
                this.a();
                break;
            default:
                this.advance();
        }
    }

    // simpleexpr -> term A
    private void simpleexpr() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
                this.term();
                this.a();
                break;
            default: //TO DO
        }
    }

    // expression -> simpleexpr expression’
    private void expression() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
                this.simpleexpr();
                this.expressionL();
                break;
            default: //TO DO
        }
    }

    // expression' -> relop simpleexpr | epsilon
    private void expressionL() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default:
                this.advance();
        }
    }

    // writable -> simpleexpr
    private void writable() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
                this.writable();
                break;
            default: //TO DO
        }
    }

    // writestmt -> write (writable)
    private void writestmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.WRITE:
                this.eat(Tag.WRITE);
                this.eat('(');
                this.writable();
                this.eat(')');
                break;
            default: //TO DO
        }
    }

    // readstmt -> read (identifier)
    private void readstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.READ:
                this.eat(Tag.READ);
                this.eat('(');
                this.eat(Tag.NUMBER);
                this.eat(')');
                break;
            default: //TO DO
        }
    }

    // dosuffix -> while ( condition )
    private void dosuffix() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.WHILE:
                this.eat(Tag.WHILE);
                this.condition();
                this.eat(')');
                break;
            default: //TO DO
        }
    }

    // dostmt -> do { stmtlist } dosuffix
    private void dostmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.DO:
                this.eat(Tag.DO);
                this.eat('{');
                this.stmtlist();
                this.eat('}');
                this.dosuffix();
                break;
            default: //TO DO
        }
    }

    // condition -> expression
    private void condition() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
                this.expression();
                break;
            default: //TO DO
        }
    }

    // ifstmt' -> else { stmtlist } | epsilon
    private void ifstmtL() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ELSE:
                this.eat(Tag.ELSE);
                this.eat('{');
                this.stmtlist();
                this.eat('}');
                break;
            default:
                this.advance();
        }
    }

    // ifstmt -> if ( condition ) { stmtlist } ifstmt
    private void ifstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.IF:
                this.eat(Tag.IF);
                this.eat('(');
                this.condition();
                this.eat(')');
                this.eat('{');
                this.stmtlist();
                this.eat('}');
                this.ifstmt();
                break;
            default: //TO DO
        }
    }

    // assignstmt -> identifier = simple_expr
    private void assignstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ID:
                this.eat(Tag.ID);
                this.eat('=');
                this.simple_expr();
                break;
            default: //TO DO
        }
    }

    // stmt -> writestmt | readstmt | dostmt | ifstmt | assignstmt
    private void stmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default: //TO DO
        }
    }

    // stmtaux -> stmt ; stmtaux | epsilon
    private void stmtaux() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
                this.advance();
        }
    }

    // stmtlist -> stmt ; stmtaux
    private void stmtlist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default: //TO DO
        }
    }

    // body ->  init stmtlist stop
    private void body() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.INIT:
                this.eat(Tag.INIT);
                this.stmtlist();
                this.eat(Tag.STOP);
                break;
            default: //TO DO
        }
    }

    // type -> float | int | string
    private void type() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
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
            default: //TO DO
        }
    }

    // identlistaux -> , identifier identlistaux | epsilon
    private void identlistaux() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case ',':
                this.eat(',');
                this.eat(Tag.ID);
                this.identlistaux();
                break;
            default:
                this.advance();
        }
    }

    // identlist -> identifier identlistaux
    private void identlist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.ID:
                this.eat(Tag.ID);
                this.identlistaux();
                break;
            default: //TO DO
        }
    }

    // decl -> type identlist
    private void decl() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.FLOAT:
            case Tag.INT:
            case Tag.STRING:
                this.type();
                this.identlist();
                break;
            default: //TO DO
        }
    }

    // decllist -> decl ; decllist | epsilon
    private void decllist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.FLOAT:
            case Tag.INT:
            case Tag.STRING:
                this.decl();
                this.eat(';');
                this.decllist();
                break;
            default:
                this.advance();
        }
    }

    // program ->  class identifier decllist body
    private void program() throws NotANumberException, LiteralWrongFormatException, WrongFormatException {
        switch (token.TAG){
            case Tag.CLASS:
                this.eat(Tag.CLASS);
                this.eat(Tag.ID);
                this.decllist();
                this.body();
                break;
            default:
                this.advance();
        }
    }
}
