package lexicalAnalyzer;

import Exceptions.LiteralWrongFormatException;
import Exceptions.NotANumberException;
import Exceptions.WrongFormatException;
import TabelaDeSimbolos.TabelaDeSimbolos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Lexer {
    public int line;
    private char ch;
    private FileReader fr;
    private TabelaDeSimbolos words;

    public Lexer(String fileName) throws FileNotFoundException {
        this.ch = ' ';
        this.line = 1;

        // Le o arquivo
        try {
            this.fr = new FileReader(fileName);
        }catch (FileNotFoundException e){
            System.out.println("File not found: "+e);
            throw e;
        }

        this.words = new TabelaDeSimbolos(null);

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
            return true;
        }
        return false;
    }

    public void ignorarDelimitadores() throws IOException {
        for (;; readch()) {
            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b'){
                continue;
            }
            else if (ch == '\n'){
                line++;
            }
            else break;
        }
    }

    public Token scan() throws IOException, NotANumberException, WrongFormatException, LiteralWrongFormatException {
        //Ignora os delimitadores na entrada
        ignorarDelimitadores();

        // Comentarios
        while (ch == '/'){
            readch();
            switch (ch){
                case '/':
                    do{
                        readch();
                    }while( ch != '\n');
                    ignorarDelimitadores();
                    break;
                case '*':
                    StringBuffer sb = new StringBuffer();
                    int linha_comentario = 0;
                    while(true){
                        if (!this.fr.ready()){
                            String msg = String.format("\nERRO NO COMENTÁRIO: \nFormato inválido, " +
                                    "comentario iniciado na linha %d não foi fechado", line);
                            throw new WrongFormatException(msg);
                        }

                        readch();
                        sb.append(ch);
                        if(sb.toString().contains("*/")){
                            readch();
                            line += linha_comentario;
                            ignorarDelimitadores();
                            break;
                        } else if(ch == '\n'){
                            linha_comentario++; //quantidade de linhas usadas no comentario
                        }
                    }
                    break;
                default:
                    return new Token('/');
            }
        }

        switch(ch){
            case '&':
                if (readch('&')){
                    return Word.AND;
                }
                else return new Token('&');
            case '|':
                if (readch('|')) return Word.OR;
                else return new Token('|');
            case '=':
                if (readch('=')) return Word.EQ;
                else return new Token('=');
            case '<':
                if (readch('=')) return Word.LE;
                else return new Token('<');
            case '>':
                if (readch('=')) return Word.GE;
                else return new Token('>');
            case '!':
                if (readch('=')) return Word.NEQ;
                else return new Token('>');
            // CONFERIR
        }

        //Números
        if (Character.isDigit(ch)){
            double value = 0;
            double aux = -1;
            do{
                value = 10*value + Character.digit(ch,10);
                readch();
            }while(Character.isDigit(ch));
            if ( ch == '.'){
                readch();
                while ( Character.isDigit(ch) ){
                    value = value + Character.digit(ch,10)*Math.pow(10, aux);
                    readch();
                    aux --;
                }
            }
            if ( Character.isLetter(ch)){
                String msg = String.format("\nERRO LEXICO: \nFormato de número inválido, com a presença do caractere %c na linha %d", ch, line);
                throw new NotANumberException(msg);
            }
            return new Number(value);
        }

        //Identificadores
        if (Character.isLetter(ch)){
            StringBuffer sb = new StringBuffer();
            do{
                sb.append(ch);
                readch();
            }while(Character.isLetterOrDigit(ch) || ch == '_');
            String s = sb.toString();
            Word w = (Word)words.get(s);
            if (w != null) {
                return w;
            }
            w = new Word (s, Tag.ID);
            words.put(s, w);
            return w;
        }

        //Reconhecer literal
        if(ch == '"'){
            int linha_string = line;
            StringBuffer sb = new StringBuffer();
            do{
                if(ch == '\n') {
                    break;
                }
                sb.append(ch);
                readch();
            }while (ch != '\"');

            if(ch == '\n'){
                String msg = String.format("\nERRO LEXICO: \nFormato de string inválido. String iniciada na linha %d, nao foi fechada com \"", line);
                throw new LiteralWrongFormatException(msg);
            }else{
                sb.append(ch);
                readch();
                String s = sb.toString();
                return new Literal(Tag.LITERAL, s);
            }
        }

        //Caracteres nao existente - cria token
        Token t = new Token(ch);
        ch = ' ';
        return t;
    }


    public FileReader getFr() {
        return fr;
    }
    public TabelaDeSimbolos getWords() {
        return this.words;
    }

}
