package Tests;

import Exceptions.Erro;
import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import Sintatico.AnalisadorSintatico;
import TabelaDeSimbolos.TabelaDeSimbolos;
import lexicalAnalyzer.Lexer;
import lexicalAnalyzer.Token;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        String file = "";


        // Recebe o path do arquivo como argumento
        for (String arg : args) {
            file = arg;
        }
        Lexer lex = new Lexer(file);
        AnalisadorSintatico sintatico = new AnalisadorSintatico(lex);

        sintatico.program();

        ArrayList<Erro> erros = sintatico.getErros();

        if(erros.isEmpty()){
            System.out.println("------------------------------------");
            System.out.println("Arquivo "+ file + " compilado com sucesso!!!");
            System.out.println("-------------------------------------");
        } else {
            System.out.println("-------------------------------------");
            System.out.println("Arquivo "+ file +" apresenta "+erros.size()+" erros");
            System.out.println("-------------------------------------");
            for (Erro e : erros) {
                System.out.println(e);
            }
        }


    }
}
