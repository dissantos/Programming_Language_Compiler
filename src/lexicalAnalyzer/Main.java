package lexicalAnalyzer;

import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import TabelaDeSimbolos.TabelaDeSimbolos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        String file = "";
        ArrayList <Token> arrayDeTokens = new ArrayList<Token>();

        // Recebe o path do arquivo como argumento
        for (String arg : args) {
            file = arg;
        }
        Lexer lex = new Lexer(file);

        while(lex.getFr().ready()){
            arrayDeTokens.add(lex.scan());
        }
        for (Token t:arrayDeTokens){
            System.out.println(t.toString());
        }
        System.out.println("\n Tabela de simbolos");
        for (Map.Entry<String, Object> entry : lex.getWords().getTabelaDeSimbolos().entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
