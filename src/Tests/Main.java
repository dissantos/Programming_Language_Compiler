package Tests;

import Exceptions.Erro;
import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import Sintatico.AnalisadorSintaticoSemantico;
import lexicalAnalyzer.Lexer;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        String file = "";


        // Recebe o path do arquivo como argumento
        for (String arg : args) {
            file = arg;
        }
        Lexer lex = new Lexer(file);
        AnalisadorSintaticoSemantico compilador = new AnalisadorSintaticoSemantico(lex);

        compilador.program();

        ArrayList<Erro> erros = compilador.getErros();

        if(erros.isEmpty() && compilador.erros_semanticos == 0){
            System.out.println("------------------------------------");
            System.out.println("Arquivo "+ file + " compilado com sucesso!!!");
            System.out.println("-------------------------------------");
        } else {
            System.out.println("-------------------------------------");
            System.out.println("Arquivo "+ file +" apresenta "+erros.size()+" erros sintáticos e "+ compilador.erros_semanticos + " erros semanticos.");
            System.out.println("-------------------------------------");

        }


    }
}
