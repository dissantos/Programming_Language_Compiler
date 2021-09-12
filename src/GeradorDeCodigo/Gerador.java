package GeradorDeCodigo;

import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Word;
import lexicalAnalyzer.Number;

import java.io.FileWriter;
import java.io.IOException;

public class Gerador {
    private FileWriter fw;
    private Boolean noError;

    public Gerador(String file) throws IOException {
        this.fw = new FileWriter(file);
        this.noError = true;

        this.fw.append("START\n"); //inicia o código para vm
    }

    public void declararVariavel() throws IOException {
        if(noError) {
            this.fw.append("PUSHN 1\n");
        }
    }

    public void addString(String str) throws IOException{
        if(noError){
            this.fw.append("PUSHS "+str+"\n");
        }
    }

    public void addConstante(Number token) throws IOException {
        if(noError){
            if(token.getTipo().equals("int")){
                this.fw.append("PUSHI "+((int)token.value)+"\n");
            }else{
                this.fw.append("PUSHF "+token.value+"\n");
            }
        }
    }

    public void addID(int offset) throws IOException {
        if(noError){
            this.fw.append("PUSHL "+offset+"\n");
        }
    }

    public void escrever(String tipo) throws IOException {
        if(noError){
            switch (tipo){
                case "int":
                    this.fw.append("WRITEI\n");
                    break;
                case "float":
                    this.fw.append("WRITEF\n");
                    break;
                case "string":
                    this.fw.append("WRITES\n");
                    break;
                default:
                    break;
            }
        }
    }


    public void lerVariavel(Word id) throws IOException {
        if(noError){
            this.fw.append("READ\n");
            switch (id.getTipo()){
                case "int":
                    this.fw.append("ATOI\n");
                    break;
                case "float":
                    this.fw.append("ATOF\n");
                    break;
                default:
                    break;
            }
            this.fw.append("STOREL "+id.getOffset()+"\n");
        }
    }

    public void operadores(int tag,String tipo) throws IOException {
        if(noError){
            if(tag == Tag.EQ || tag == Tag.NE){
                this.fw.append("EQUAL\n");
                if(tag == Tag.NE){
                    this.fw.append("NOT\n");
                }
            } else {
                if (tipo.equals("float") || tag == '/') {
                    this.fw.append("F");
                }

                switch (tag) {
                    case '+':
                        if (tipo.equals("string")) {
                            this.fw.append("CONCAT\n");
                        } else {
                            this.fw.append("ADD\n");
                        }
                        break;
                    case '-':
                        this.fw.append("SUB\n");
                        break;
                    case '*':
                        this.fw.append("MUL\n");
                        break;
                    case '/':
                        this.fw.append("DIV\n");
                        break;
                    case '<':
                        this.fw.append("INF\n");
                        break;
                    case Tag.LE:
                        this.fw.append("INFEQ\n");
                        break;
                    case '>':
                        this.fw.append("SUP\n");
                        break;
                    case Tag.GE:
                        this.fw.append("SUPEQ\n");
                        break;
                    default:
                        this.fw.append("NOP //nao implementado\n");
                }
            }
        }
    }

    public void convertFloat() throws IOException {
        if(noError){
            this.fw.append("ITOF\n");
        }
    }

    public void atribuicao(Word id) throws IOException {
        if(noError){
            this.fw.append("STOREL "+id.getOffset()+"\n");
        }
    }

    public void ifJump(int rotuloNumber) throws IOException {
        String rotulo = "IF"+rotuloNumber;
        if(noError){
            this.fw.append("JZ "+rotulo+"\n");
        }
    }

    public void elseJump(int rotuloNumber) throws IOException{
        String rotulo = "ELSE"+rotuloNumber;
        if(noError){
            this.fw.append("JUMP "+rotulo+"\n");
        }
    }

    public void loopJump(int rotuloNumber) throws IOException{
        String rotulo = "LOOP"+rotuloNumber;
        if(noError){
            this.fw.append("JUMP "+rotulo+"\n");
        }
    }

    public void endLoopJump(int rotuloNumber) throws IOException {
        String rotulo = "ENDLOOP"+rotuloNumber;
        if(noError){
            this.fw.append("JZ "+rotulo+"\n");
        }
    }



    public void ifLabel(int rotuloNumber) throws IOException{
        String rotulo = "IF"+rotuloNumber;
        if(noError){
            this.fw.append(rotulo+": ");
        }
    }

    public void elseLabel(int rotuloNumber) throws IOException{
        String rotulo = "ELSE"+rotuloNumber;
        if(noError){
            this.fw.append(rotulo+": ");
        }
    }

    public void loopLabel(int rotuloNumber) throws IOException{
        String rotulo = "LOOP"+rotuloNumber;
        if(noError){
            this.fw.append(rotulo+": ");
        }
    }

    public void endLoopLabel(int rotuloNumber) throws IOException{
        String rotulo = "ENDLOOP"+rotuloNumber;
        if(noError){
            this.fw.append(rotulo+": ");
        }
    }

    public void setNoError(Boolean noError) {
        this.noError = noError;
    }

    public void finaliza() throws IOException {
        if(noError){
            this.fw.append("STOP\n");
        } else {
            fw.write("ERROR\n");
        }

        fw.close();
    }



}
