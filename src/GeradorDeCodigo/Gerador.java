package GeradorDeCodigo;

import lexicalAnalyzer.Word;

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

    public void escreverTexto() throws IOException {
        if(noError){
            this.fw.append("WRITES\n");
        }
    }
    
    public void escreveVariavel(Word id) throws IOException{
        // TODO: 11/09/2021  
    }
    
    public  void escreveExpressao(){
        // TODO: 11/09/2021  
    }

    public void leVariavel(Word id) throws IOException {
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
            this.fw.append("STOREL "+id.getOffset());
        }
    }

    public void escreverExpr(String txt) throws IOException {
        if(noError){
            this.fw.append("PUSHS "+txt+"\n");
            this.fw.append("WRITES\n");
        }
    }



    public void finaliza() throws IOException {
        if(noError){
            this.fw.append("STOP\n");
        } else {
            fw.write("ERROR");
        }

        fw.close();
    }

}
