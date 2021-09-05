package Sintatico;
import Exceptions.Erro;
import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import TabelaDeSimbolos.TabelaDeSimbolos;
import lexicalAnalyzer.Lexer;
import lexicalAnalyzer.Number;
import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.Word;

import java.io.IOException;
import java.util.ArrayList;

import static Semantico.VerificacaoSemantica.unicidade;
import static Semantico.VerificacaoSemantica.verificaTipo;

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
    private String factor() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
                this.eat(Tag.ID);
                tipo = ((Word)token).getTipo();
                break;
            case Tag.NUMBER:
                this.eat(Tag.NUMBER);
                tipo = ((Number)token).getTipo();
                break;
            case '(':
                this.eat('(');
                tipo = this.expression();
                this.eat(')');
                break;
            case Tag.LITERAL:
                tipo = "string";
                this.eat(Tag.LITERAL);
                break;
            default:
                int [] tokens = {Tag.ID, Tag.NUMBER, Tag.LITERAL, '('};
                error(tokens);
        }
        return tipo;
    }

    // factor -> -factor|  !factor | factor
    private String factora() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case '-':
                this.eat('-');
                tipo = this.factor();
                break;
            case '!':
                eat('!');
                tipo = this.factor();
                break;
            case Tag.ID:
            case Tag.NUMBER:
            case Tag.LITERAL:
            case '(':
                tipo = this.factor();
                break;
            default:
                int [] tokens = {'-', '!', Tag.ID, Tag.NUMBER, Tag.LITERAL, '('};
                error(tokens);
        }
        return tipo;
    }

    // z -> mulop factora Z| lambda
    private String z() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case '*':
            case '/':
            case Tag.AND:
                this.mulop();
                tipo1 = this.factora();
                tipo2 = this.z();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
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
                tipo = "nulo";
                break;
            default:
                int [] tokens = {'*', '/', Tag.AND, ';',')', '-', '>', Tag.GE, '<', Tag.LE, Tag.NE, Tag.EQ, '+', Tag.OR};
                error(tokens);
        }
        return tipo;
    }

    // term -> factora Z
    private String term() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                tipo1 = this.factora();
                tipo2 = this.z();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            default:
                int [] tokens = {'(', '!','-', Tag.NUMBER, Tag.ID, Tag.LITERAL};
                error(tokens);
        }
        return tipo;
    }

    // a -> addop term A | lambda
    private String a() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case '+':
            case '-':
            case Tag.OR:
                this.addop();
                tipo1 = this.term();
                tipo2 = this.a();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            case ';':
            case ')':
            case '>':
            case Tag.GE:
            case '<':
            case Tag.LE:
            case Tag.NE:
            case Tag.EQ:
                tipo = "nulo";
                break;
            default:
                int [] tokens = {';',')', '-', '>', Tag.GE, '<', Tag.LE, Tag.NE, Tag.EQ, '+', Tag.OR};
                error(tokens);
        }
        return tipo;
    }

    // simpleexpr -> term A
    private String simpleexpr() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                tipo1 = this.term();
                tipo2 = this.a();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            default:
                int [] tokens = {'(', '!','-', Tag.NUMBER, Tag.ID, Tag.LITERAL};
                error(tokens);
        }
        return tipo;
    }

    // expression -> simpleexpr expression’
    private String expression() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                tipo1 = this.simpleexpr();
                tipo2 = this.expressionL();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            default:
                int [] tokens = {'(', '!', '-', Tag.NUMBER, Tag.ID, Tag.LITERAL};
                error(tokens);
        }
        return tipo;
    }

    // expression' -> relop simpleexpr | lambda
    private String expressionL() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case '>':
            case '<':
            case Tag.GE:
            case Tag.LE:
            case Tag.EQ:
            case Tag.NE:
                this.relop();
                tipo = this.simpleexpr();
                break;
            case ')':
                tipo = "nulo";
                break;
            default:
                int [] tokens = { '>', Tag.GE, '<', Tag.LE, Tag.NE, Tag.EQ, ')'};
                error(tokens);
        }
        return tipo;
    }

    // writable -> simpleexpr
    private String writable() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                tipo = this.simpleexpr();
                break;
            default:
                int [] tokens = {'(', '!', '-', Tag.NUMBER, Tag.ID, Tag.LITERAL};
                error(tokens);
        }
        return tipo;
    }

    // writestmt -> write (writable)
    private String writestmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.WRITE:
                this.eat(Tag.WRITE);
                this.eat('(');
                tipo = this.writable();
                if (!tipo.equals("tipo_erro")){
                    tipo = "tipo_vazio";
                }
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.WRITE};
                error(tokens);
        }
        return tipo;
    }

    // readstmt -> read (identifier)
    private String readstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.READ:
                this.eat(Tag.READ);
                this.eat('(');
                tipo = lex.getWords().obterTipo(((Word)token).getLexeme());
                if (!tipo.equals("tipo_erro")){
                    tipo = "tipo_vazio";
                }
                this.eat(Tag.ID);
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.READ};
                error(tokens);
        }
        return tipo;
    }

    // dosuffix -> while ( condition )
    private String dosuffix() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.WHILE:
                this.eat(Tag.WHILE);
                this.eat('(');
                tipo = this.condition();
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.WHILE};
                error(tokens);
        }
        return tipo;
    }

    // dostmt -> do { stmtlist } dosuffix
    private String dostmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.DO:
                this.eat(Tag.DO);
                this.eat('{');
                tipo = this.stmtlist();
                this.eat('}');
                this.dosuffix();
                break;
            default:
                int [] tokens = {Tag.DO};
                error(tokens);
        }
        return tipo;
    }

    // condition -> expression
    private String condition() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
            case '(':
            case '!':
            case '-':
            case Tag.NUMBER:
            case Tag.LITERAL:
                tipo = this.expression();
                if (!tipo.equals("int")){
                    tipo = "tipo_erro";
                }
                break;
            default:
                int [] tokens = {Tag.ID, '(', '!', '-', Tag.NUMBER,  Tag.LITERAL};
                error(tokens);
        }
        return tipo;
    }

    // ifstmt' -> else { stmtlist } | lambda
    private String ifstmtPrime() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.ELSE:
                this.eat(Tag.ELSE);
                this.eat('{');
                tipo = this.stmtlist();
                this.eat('}');
                break;
            case ';':
                tipo = "nulo";
                break;
            default:
                int [] tokens = {Tag.ELSE, ';'};
                error(tokens);
        }
        return tipo;
    }

    // ifstmt -> if ( condition ) { stmtlist } ifstmt'
    private String ifstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        String tipo3 = "tipo_erro";
        switch (token.TAG){
            case Tag.IF:
                this.eat(Tag.IF);
                this.eat('(');
                tipo1 = this.condition();
                this.eat(')');
                this.eat('{');
                tipo2 = this.stmtlist();
                this.eat('}');
                tipo3 = this.ifstmtPrime();
                if (tipo1.equals("int") && tipo2.equals("tipo_vazio") && (tipo3.equals("nulo") || tipo3.equals("tipo_vazio"))){
                    tipo = "tipo_vazio";
                }
                else{
                    tipo = "tipo_erro";
                }
                break;
            default:
                int [] tokens = {Tag.IF};
                error(tokens);
        }
        return tipo;
    }

    // assignstmt -> identifier = simple_expr
    private String assignstmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
                this.eat(Tag.ID);
                tipo1 = lex.getWords().obterTipo(((Word)token).getLexeme());
                this.eat('=');
                tipo2 = this.simpleexpr();
                tipo = verificaTipo(tipo1, tipo2);
                if (!tipo.equals("tipo_erro")){
                    tipo = "tipo_vazio";
                }
                break;
            default:
                int [] tokens = {Tag.ID};
                error(tokens);
        }
        return tipo;
    }

    // stmt -> writestmt | readstmt | dostmt | ifstmt | assignstmt
    private String stmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.WRITE:
                tipo = this.writestmt();
                break;
            case Tag.READ:
                tipo = this.readstmt();
                break;
            case Tag.DO:
                tipo = this.dostmt();
                break;
            case Tag.IF:
                tipo = this.ifstmt();
                break;
            case Tag.ID:
                tipo = this.assignstmt();
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID};
                error(tokens);
        }
        return tipo;
    }

    // stmtaux -> stmt ; stmtaux | lambda
    private String stmtaux() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.WRITE:
            case Tag.READ:
            case Tag.DO:
            case Tag.IF:
            case Tag.ID:
                tipo1 = this.stmt();
                this.eat(';');
                tipo2 = this.stmtaux();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            case '}':
            case Tag.STOP:
                tipo = "nulo";
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID, '}', Tag.STOP};
                error(tokens);
        }
        return tipo;
    }

    // stmtlist -> stmt ; stmtaux
    private String stmtlist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.WRITE:
            case Tag.READ:
            case Tag.DO:
            case Tag.IF:
            case Tag.ID:
                tipo1 = this.stmt();
                this.eat(';');
                tipo2 = this.stmtaux();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID};
                error(tokens);
        }
        return tipo;
    }

    // body ->  init stmtlist stop
    private String body() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.INIT:
                this.eat(Tag.INIT);
                tipo = this.stmtlist();
                this.eat(Tag.STOP);
                break;
            default:
                int [] tokens = {Tag.INIT};
                error(tokens);
        }
        return tipo;
    }

    // type -> float | int | string
    private String type() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG){
            case Tag.FLOAT:
                this.eat(Tag.FLOAT);
                return "float";
            case Tag.INT:
                this.eat(Tag.INT);
                return "int";
            case Tag.STRING:
                this.eat(Tag.STRING);
                return "string";
            default:
                int [] tokens = {Tag.FLOAT, Tag.INT, Tag.STRING};
                error(tokens);
                return "tipo_erro";
        }
    }

    // identlistaux -> , identifier identlistaux | lambda
    private String identlistaux(String tipoAux) throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case ',':
                this.eat(',');
                if (unicidade(lex.getWords(), (Word)token).equals("tipo_vazio")){
                    TabelaDeSimbolos aux = lex.getWords();
                    aux.incluirTipo(((Word)token).getLexeme(), tipoAux);
                    lex.setWords(aux);
                    tipo1 = "tipo_vazio";
                }
                this.eat(Tag.ID);
                tipo2 = this.identlistaux(tipoAux);
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            case ';':
                tipo = "nulo";
                break;
            default:
                int [] tokens = {',',';'};
                error(tokens);
        }
        return tipo;
    }

    // identlist -> identifier identlistaux
    private String identlist(String tipoAux) throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
                if (unicidade(lex.getWords(), (Word)token).equals("tipo_vazio")){
                    TabelaDeSimbolos aux = lex.getWords();
                    aux.incluirTipo(((Word)token).getLexeme(), tipoAux);
                    lex.setWords(aux);
                    tipo1 = "tipo_vazio";
                }
                this.eat(Tag.ID);
                tipo2 = this.identlistaux(tipoAux);
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            default:
                int [] tokens = {Tag.ID};
                error(tokens);
        }
        return tipo;
    }

    // decl -> type identlist
    private String decl() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        switch (token.TAG){
            case Tag.FLOAT:
            case Tag.INT:
            case Tag.STRING:
                tipo1 = this.type();
                tipo = this.identlist(tipo1);
                break;
            default:
                int [] tokens = {Tag.FLOAT, Tag.INT, Tag.STRING};
                error(tokens);
        }
        return tipo;
    }

    // decllist -> decl ; decllist | lambda
    private String decllist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.FLOAT:
            case Tag.INT:
            case Tag.STRING:
                tipo1 = this.decl();
                this.eat(';');
                tipo2 = this.decllist();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            case Tag.INIT:
                tipo = "nulo";
                break;
            default:
                int [] tokens = {Tag.FLOAT, Tag.INT, Tag.STRING, Tag.INIT};
                error(tokens);
        }
        return tipo;
    }

    // program ->  class identifier decllist body
    public String program() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        String tipo1 = "tipo_erro";
        String tipo2 = "tipo_erro";
        switch (token.TAG){
            case Tag.CLASS:
                this.eat(Tag.CLASS);
                this.eat(Tag.ID);
                tipo1 = this.decllist();
                tipo2 = this.body();
                if (tipo2.equals("nulo")){
                    tipo2 = tipo1;
                }
                tipo = verificaTipo(tipo1, tipo2);
                break;
            default:
                int [] tokens = {Tag.CLASS};
                error(tokens);
        }
        return tipo;
    }

    public ArrayList<Erro> getErros() {
        return erros;
    }
}
