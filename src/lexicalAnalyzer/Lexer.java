package lexicalAnalyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
    public int line;
    private char ch;
    private FileReader fr;
    private Hashtable<String, Word> words;

    public Lexer(String fileName) throws FileNotFoundException {
        this.ch = ' ';
        this.line = 1;
        try {
            this.fr = new FileReader(fileName);
        }catch (FileNotFoundException e){
            System.out.println("File not found: "+e);
            throw e;
        }

        this.words = new Hashtable<>();

        //insert reserved words in HashTable
        words.put("class", new Word("class", Tag.CLASS));
        words.put("int", new Word("int", Tag.INT));
        words.put("string", new Word("string", Tag.STRING));
        words.put("float", new Word("float", Tag.FLOAT));
        words.put("init", new Word("init", Tag.INIT));
        words.put("stop", new Word("stop", Tag.STOP));
        words.put("if", new Word("if", Tag.IF));
        words.put("else", new Word("else", Tag.ELSE));
        words.put("do", new Word("do", Tag.DO));
        words.put("while", new Word("while", Tag.WHILE));
        words.put("read", new Word("read", Tag.READ));
        words.put("write", new Word("write", Tag.WRITE));
    }

    private void readch() throws IOException {
        this.ch = (char) this.fr.read();
    }

    public boolean readch(char ch) throws IOException {
        readch();
        if (this.ch == ch){
            this.ch =  ' ';
        }

        return this.ch == ch;
    }


}
