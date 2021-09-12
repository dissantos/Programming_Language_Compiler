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
        String fileMachine = "./src/Tests/out.vm";


        // Recebe o path do arquivo como argumento
        if(args.length != 2){
            System.out.println("Falta argumentos:");
            System.out.println("java -jar <path para o compiler>.jar <path para o codigo> <path to vm file generate>.vm");
            System.exit(0);
        } else {
            file = args[0];
            fileMachine = args[1];
        }
        Lexer lex = new Lexer(file);
        AnalisadorSintaticoSemantico compilador = new AnalisadorSintaticoSemantico(lex,fileMachine);

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
