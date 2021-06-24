package lexicalAnalyzer;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String file = "";
        // Recebe o path do arquivo como argumento
        for (String arg : args) {
            file = arg;
        }
        Lexer lex = new Lexer(file);

    }
}
