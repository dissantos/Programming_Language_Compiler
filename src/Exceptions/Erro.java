package Exceptions;

import lexicalAnalyzer.Token;

import java.util.Arrays;

public class Erro {
    private int line;
    private Token errado;
    private int [] tokenEsperado;

    public Erro(int line, Token errado, int [] tokenEsperado) {
        this.line = line;
        this.errado = errado;
        this.tokenEsperado = tokenEsperado;
    }

    public int getLine() {
        return line;
    }

    public Token getErrado() {
        return errado;
    }

    public int[] getTokenEsperado() {
        return tokenEsperado;
    }

    @Override
    public String toString() {
        String [] tags = {"'class'", "'int'", "'string'", "'float'", "'init'", "'stop'",
                "'if'", "'else'", "'do'", "'while'", "'read'", "'write'",
                "!=","==","<=",">=","||","&&","LITERAL", "NUMBER", "ID"};
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        for (int tag: tokenEsperado){
            if(tag < 255){
                sb.append((char) tag);
            }else{
                sb.append(tags[tag - 256]);
            }
            sb.append(" ");
        }
        sb.append("]");
        char ch;
        if(this.errado.TAG <= 255) {
            ch = (char) this.errado.TAG;
            return "--------------------------------------------\n" +
                    "ERRO SINTATICO:\n\tlinha " +
                    line +
                    ".\n Token na entrada: " + ch +
                    ".\n Tokens Esperados =" + sb.toString();
        }else{
            return "--------------------------------------------\n" +
                    "ERRO SINTATICO:\n\tlinha " +
                    line +
                    ".\n Token na entrada: " + errado +
                    " .\n Tokens Esperados = " + sb.toString();
        }
    }
}
