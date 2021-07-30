package Sintatico;
import Lexer;
import lexicalAnalyzer.Lexer;
import lexicalAnalyzer.Tag;

public class AnalisadorSintatico {
    private Lexer lex;

    public AnalisadorSintatico(Lexer lex){
        lex = lex;
    }
    public void eat(int tag) {

    }
    public void mulop(){
        switch (token.tag){
            case '*':
                eat('*');
                break;
            case '/':
                eat('/');
                break;
            case Tag.AND:
                eat(Tag.AND);
                break;
            default:    ;//TO DO
        }
    }
    public void addop(){
        switch (token.tag){
            case '+':
                eat('+');
                break;
            case '-':
                eat('-');
                break;
            case Tag.OR:
                eat(Tag.OR);
                break;
            default:    ;//TO DO
        }
    }
    public void relop(){
        switch (token.tag){
            case '>':
                eat('>');
                break;
            case '<':
                eat('<');
                break;
            case Tag.GE:
                eat(Tag.GE);
                break;
            case Tag.LE:
                eat(Tag.LE);
                break;
            case Tag.EQ:
                eat(Tag.EQ);
                break;
            case Tag.NE:
                eat(Tag.NE);
                break;
            default:    ;//TO DO
        }
    }
    public void factor(){
        switch (token.tag){
            case Tag.ID:
                eat(Tag.ID);
                break;
            case Tag.NUMBER:
                eat(Tag.NUMBER);
                break;
            case '(':
                eat('(');
                expression();
                eat(')');
                break;
            default:    ;//TO DO
        }
    }
     ::= term | simple-expr addop term
    public void simpleExpr(){
        switch (token.tag){
            case Tag.ID:
                eat(Tag.ID);
                break;
            case Tag.NUMBER:
                eat(Tag.NUMBER);
                break;
            case '(':
                eat('(');
                expression();
                eat(')');
                break;
            default:    ;//TO DO
        }
    }
    term ::= factor-a | term mulop factor-a
    public void term(){
        switch (token.tag){
            case factorA:
                factor();
                eat(Tag.ID);
                break;
            case Tag.NUMBER:
                eat(Tag.NUMBER);
                break;
            case '(':
                eat('(');
                expression();
                eat(')');
                break;
            default:    ;//TO DO
        }
    }
    factor-a ::= factor | "!" factor | "-" factor
    public void factorA(){
        switch (token.tag){
            case factor():
                eat(Tag.ID);
                break;
            case Tag.NUMBER:
                eat(Tag.NUMBER);
                break;
            case '(':
                eat('(');
                expression();
                eat(')');
                break;
            default:    ;//TO DO
        }
    }

    public void expression(){ expression ::= simple-expr | simple-expr relop simple-expr
        switch (token.tag){
            case Tag.ID:
                eat(Tag.ID);
                break;
            case Tag.NUMBER:
                eat(Tag.NUMBER);
                break;
            case '(':
                eat('(');
                expression();
                eat(')');
                break;
            default:    ;//TO DO
        }
    }

}
