package Semantico;

import TabelaDeSimbolos.TabelaDeSimbolos;
import lexicalAnalyzer.Word;

public class VerificacaoSemantica {
    public static String unicidade(TabelaDeSimbolos tabelaDeSimbolos, Word palavra){
        if (tabelaDeSimbolos.get(palavra.getLexeme()) == null){
            return "nao-declarado";
        }
        else{
            return "declarado";
        }
    }

    public static String verificaTipo (String tipo1, String tipo2){
        if(tipo1.equals(tipo2)){
            return tipo1;
        }
        else if(tipo1.equals("tipo_erro-nao-declarado") || tipo2.equals("tipo_erro-nao-declarado")){

            return "tipo_erro-nao-declarado";
        } else {
            return "tipo_erro-tipo-invalido";
        }
    }
}
