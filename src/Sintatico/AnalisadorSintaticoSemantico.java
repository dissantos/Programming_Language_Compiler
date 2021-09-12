package Sintatico;
import Exceptions.Erro;
import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import GeradorDeCodigo.Gerador;
import TabelaDeSimbolos.TabelaDeSimbolos;
import lexicalAnalyzer.*;
import lexicalAnalyzer.Number;

import java.io.IOException;
import java.util.ArrayList;

import static Semantico.VerificacaoSemantica.unicidade;
import static Semantico.VerificacaoSemantica.verificaTipo;

public class AnalisadorSintaticoSemantico {
    private Lexer lex;
    private Token token;
    private ArrayList<Erro> erros;
    public int erros_semanticos = 0;
    private int offset = 0;
    private Gerador gerador;
    private int rotuloIf = 0;
    private int rotuloElse = 0;
    private int rotuloDo = 0;
    private int rotuloWhile = 0;

    public AnalisadorSintaticoSemantico(Lexer lex, String fileMaquina) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        this.lex = lex;
        this.advance();
        this.erros = new ArrayList<Erro>();
        gerador = new Gerador(fileMaquina);
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
        System.out.println(erro);
        advance();
    }

    // mulop -> * | / | &&
    private void mulop() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        switch (token.TAG) {
            case '*':
                this.eat('*');
                break;
            case '/':
                this.eat('/');
                gerador.convertFloat();
                break;
            case Tag.AND:
                this.eat(Tag.AND);
                break;
            default:
                int[] tokens = {'*', '/', Tag.AND};
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

    // factor -> ID |  NUMBER | LITERAL | (expression)
    private String factor() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.ID:
                lex.setWord((Word) token);
                tipo = ((Word)token).getTipo();
                gerador.addID(((Word)token).getOffset());
                this.eat(Tag.ID);
                break;
            case Tag.NUMBER:
                gerador.addConstante((Number) token);
                tipo = ((Number)token).getTipo();
                this.eat(Tag.NUMBER);
                break;
            case '(':
                this.eat('(');
                tipo = this.expression();
                this.eat(')');
                break;
            case Tag.LITERAL:
                tipo = "string";
                gerador.addString(((Literal) token).str);
                this.eat(Tag.LITERAL);
                break;
            default:
                int [] tokens = {Tag.ID, Tag.NUMBER, Tag.LITERAL, '('};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // factorA -> -factor|  !factor | factor
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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // z -> mulop factora Z| lambda
    private String z() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        String tipo1;
        String tipo2;
        switch (token.TAG){
            case '*':
            case '/':
            case Tag.AND:
                Token op = token;
                this.mulop();
                tipo1 = this.factora();
                if(tipo1.equals("int") && op.TAG == '/'){
                    gerador.convertFloat();
                }
                gerador.operadores(op.TAG,tipo1);
                tipo2 = this.z();
                if (tipo2.equals("nulo")){
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                if(op.TAG == '/' && (tipo1.equals("int")) && (tipo2.equals("int") || tipo2.equals("/int") || tipo2.equals("nulo"))){
                    tipo = "/int";
                }
                if(tipo.equals("string")){
                    tipo = "tipo_erro-tipo-invalido";
                }


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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // term -> factora Z
    private String term() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        String tipo1;
        String tipo2;
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
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                if(tipo1.equals("int") && tipo2.equals("/int")){
                    tipo = "float";
                }
                break;
            default:
                int [] tokens = {'(', '!','-', Tag.NUMBER, Tag.ID, Tag.LITERAL};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // a -> addop term A | lambda
    private String a() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        String tipo1;
        String tipo2;
        switch (token.TAG){
            case '+':
            case '-':
            case Tag.OR:
                Token op = token;
                this.addop();
                tipo1 = this.term();
                gerador.operadores(op.TAG,tipo1);
                tipo2 = this.a();
                if (tipo2.equals("nulo")){
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                if(tipo1.equals("string") && (tipo2.equals("nulo") || tipo2.equals("+string")) && op.TAG == '+'){
                    tipo = "+string";
                } else if(tipo.equals("string")){
                    tipo = "tipo_erro-tipo-invalido";
                }

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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // simpleexpr -> term A
    private String simpleexpr() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        String tipo1;
        String tipo2;
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
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                if(tipo1.equals("string") && tipo2.equals("+string")){
                    tipo = "string";
                }

                break;
            default:
                int [] tokens = {'(', '!','-', Tag.NUMBER, Tag.ID, Tag.LITERAL};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // expression -> simpleexpr expression’
    private String expression() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        String tipo1;
        String tipo2;
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
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                    if(!tipo.contains("tipo_erro")){
                        tipo = "int";
                    }
                }
                break;
            default:
                int [] tokens = {'(', '!', '-', Tag.NUMBER, Tag.ID, Tag.LITERAL};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // expression' -> relop simpleexpr | lambda
    private String expressionL() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        switch (token.TAG){
            case '>':
            case '<':
            case Tag.GE:
            case Tag.LE:
            case Tag.EQ:
            case Tag.NE:
                Token op = token;
                this.relop();
                tipo = this.simpleexpr();
                gerador.operadores(op.TAG,tipo);
                break;
            case ')':
                tipo = "nulo";
                break;
            default:
                int [] tokens = { '>', Tag.GE, '<', Tag.LE, Tag.NE, Tag.EQ, ')'};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                gerador.escrever(tipo);
                if (!tipo.contains("tipo_erro")){
                    tipo = "tipo_vazio";
                }else if(tipo.contains("nao-declarado")){
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\texpressao na funcao write( ) da linha "+ lex.line + " contem identificador nao declarado.");
                }else if(tipo.contains("tipo-invalido")){
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\texpressao na funcao write( ) da linha "+ lex.line + " contem identificadores com tipos incompativeis.");
                }
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.WRITE};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                if (!tipo.contains("tipo_erro")){
                    tipo = "tipo_vazio";
                    gerador.lerVariavel((Word) token);
                }else if(tipo.contains("nao-declarado")){
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tidentificador nao declarado na funcao read( ) da linha "+ lex.line + ".");
                }
                Token aux = token;
                this.eat(Tag.ID);
                if(aux.TAG == Tag.ID)
                    lex.setWord((Word) aux);
                this.eat(')');
                break;
            default:
                int [] tokens = {Tag.READ};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // dosuffix -> while ( condition )
    private String dosuffix() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        switch (token.TAG){
            case Tag.WHILE:
                this.eat(Tag.WHILE);
                this.eat('(');
                tipo = this.condition();
                gerador.endLoopJump(rotuloWhile);
                rotuloWhile++;
                rotuloDo--;
                gerador.loopJump(rotuloDo);

                if(tipo.equals("int")){
                    tipo = "tipo_vazio";
                } else if(tipo.contains("nao-declarado")){
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tcondicao no do...while(condicao) da linha "+ lex.line + " contem identificador nao declarado.");
                }else if(tipo.contains("tipo-invalido")){
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tcondicao no do...while(condicao) da linha "+ lex.line + " contem identificadores com tipos incompativeis.");
                }else {
                    tipo = "tipo_erro-nao-inteira";
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tcondicao no do...while(condicao) da linha "+ lex.line + " deve ser do tipo inteira.");
                }
                this.eat(')');
                rotuloWhile--;
                gerador.endLoopLabel(rotuloWhile);
                break;
            default:
                int [] tokens = {Tag.WHILE};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // dostmt -> do { stmtlist } dosuffix
    private String dostmt() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.DO:
                gerador.loopLabel(rotuloDo);
                rotuloDo++;
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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                break;
            default:
                int [] tokens = {Tag.ID, '(', '!', '-', Tag.NUMBER,  Tag.LITERAL};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // ifstmt' -> else { stmtlist } | lambda
    private String ifstmtPrime() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = "tipo_erro";
        switch (token.TAG){
            case Tag.ELSE:
                gerador.elseJump(rotuloElse);
                rotuloElse++;
                this.eat(Tag.ELSE);
                rotuloIf--;
                gerador.ifLabel(rotuloIf);
                this.eat('{');
                tipo = this.stmtlist();
                this.eat('}');
                rotuloElse--;
                gerador.elseLabel(rotuloElse);
                break;
            case ';':
                tipo = "nulo";
                gerador.ifLabel(rotuloIf);
                rotuloIf--;
                break;
            default:
                int [] tokens = {Tag.ELSE, ';'};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                int line = lex.line;
                tipo1 = this.condition();
                gerador.ifJump(rotuloIf);
                rotuloIf++;
                this.eat(')');
                this.eat('{');
                tipo2 = this.stmtlist();
                this.eat('}');
                tipo3 = this.ifstmtPrime();
                if (tipo1.equals("int") && tipo2.equals("tipo_vazio") && (tipo3.equals("nulo") || tipo3.equals("tipo_vazio"))){
                    tipo = "tipo_vazio";
                } else if(tipo1.contains("nao-declarado")){
                    tipo = "tipo_erro";
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tcondicao no if(condicao) da linha "+ line + " contem identificador nao declarado.");
                }else if(tipo1.contains("tipo-invalido")){
                    tipo = "tipo_erro";
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tcondicao no if(condicao) da linha "+ line + " contem identificadores com tipos incompativeis.");
                }else {
                    tipo = "tipo_erro";
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tcondicao no if(condicao) da linha "+ line + " deve ser do tipo inteira.");
                }


                break;
            default:
                int [] tokens = {Tag.IF};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                Token aux = token;
                this.eat(Tag.ID);
                if(aux.TAG == Tag.ID)
                    lex.setWord((Word) aux);
                tipo1 = lex.getWords().obterTipo(((Word)aux).getLexeme());
                this.eat('=');
                tipo2 = this.simpleexpr();
                tipo = verificaTipo(tipo1, tipo2);
                if (!tipo.contains("tipo_erro")){
                    tipo = "tipo_vazio";
                    gerador.atribuicao((Word) aux);
                } else if(tipo.contains("nao-declarado")){
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tatribuição na linha "+ lex.line + " contem identificador nao declarado.");
                }else if(tipo.contains("tipo-invalido")){
                    erros_semanticos++;
                    System.out.println("--------------------------------------------");
                    System.out.println("ERRO SEMÂNTICO:\n" +
                            "\tatribuição da linha "+ lex.line + " contem identificadores com tipos incompativeis.");
                }


                break;
            default:
                int [] tokens = {Tag.ID};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // stmtaux -> stmt ; stmtaux | lambda
    private String stmtaux() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        String tipo1;
        String tipo2;
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
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                break;
            case '}':
            case Tag.STOP:
                tipo = "nulo";
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID, '}', Tag.STOP};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // stmtlist -> stmt ; stmtaux
    private String stmtlist() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
        String tipo1;
        String tipo2;
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
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                break;
            default:
                int [] tokens = {Tag.WRITE, Tag.READ, Tag.DO, Tag.IF, Tag.ID};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
        }
        return tipo;
    }

    // body ->  init stmtlist stop
    private String body() throws NotANumberException, LiteralWrongFormatException, WrongFormatException, IOException {
        String tipo = null;
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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                gerador.setNoError(false);
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
                Token aux1 = token;
                if(aux1.TAG == Tag.ID) {

                    if (unicidade(lex.getWords(), (Word) token).contains("nao-declarado")) {
                        TabelaDeSimbolos aux = lex.getWords();
                        aux.incluirTipoOffset(((Word) aux1), tipoAux,offset);
                        lex.setWords(aux);
                        tipo1 = "tipo_vazio";
                    } else {
                        erros_semanticos++;
                        gerador.setNoError(false);
                        System.out.println("--------------------------------------------");
                        System.out.println("ERRO SEMANTICO:\n" +
                                "\tvariavel '" + ((Word) token).getLexeme() + "' na linha " + lex.line + " ja foi declarada antes");
                    }

                    ((Word) aux1).setTipo(tipoAux);
                    ((Word) aux1).setOffset(offset);
                    offset++;
                    lex.setWord((Word) aux1);
                }
                gerador.declararVariavel();
                this.eat(Tag.ID);

                tipo2 = this.identlistaux(tipoAux);
                if (tipo2.equals("nulo")){
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                break;
            case ';':
                tipo = "nulo";
                break;
            default:
                int [] tokens = {',',';'};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                Token aux1 = token;
                if(aux1.TAG == Tag.ID) {

                    if (unicidade(lex.getWords(), (Word) token).equals("nao-declarado")) {
                        TabelaDeSimbolos aux = lex.getWords();
                        aux.incluirTipoOffset((Word) aux1, tipoAux, offset);
                        lex.setWords(aux);
                        tipo1 = "tipo_vazio";
                    } else {
                        gerador.setNoError(false);
                        erros_semanticos++;
                        System.out.println("--------------------------------------------");
                        System.out.println("ERRO SEMANTICO:\n" +
                                "\tvariavel '" + ((Word) token).getLexeme() + "' na linha " + lex.line + " ja foi declarada antes");
                    }
                    ((Word) aux1).setTipo(tipoAux);
                    ((Word) aux1).setOffset(offset);
                    offset++;
                    lex.setWord((Word) aux1);
                }
                gerador.declararVariavel();
                this.eat(Tag.ID);

                tipo2 = this.identlistaux(tipoAux);
                if (tipo2.equals("nulo")){
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                break;
            default:
                int [] tokens = {Tag.ID};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                    tipo = tipo1;
                } else {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                break;
            case Tag.INIT:
                tipo = "nulo";
                break;
            default:
                int [] tokens = {Tag.FLOAT, Tag.INT, Tag.STRING, Tag.INIT};
                error(tokens);
        }
        if ( tipo.contains("tipo_erro")){
            gerador.setNoError(false);
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
                Token aux = token;
                this.eat(Tag.ID);
                if(aux.TAG == Tag.ID)
                    lex.setWord((Word) aux);
                tipo1 = this.decllist();
                tipo2 = this.body();
                if(tipo1 != null && tipo2 != null) {
                    tipo = verificaTipo(tipo1, tipo2);
                }
                gerador.finaliza();
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
