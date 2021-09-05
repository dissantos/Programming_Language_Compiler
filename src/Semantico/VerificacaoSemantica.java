package Semantico;

import TabelaDeSimbolos.TabelaDeSimbolos;
import lexicalAnalyzer.Word;

public class VerificacaoSemantica {
    public static String unicidade(TabelaDeSimbolos tabelaDeSimbolos, Word palavra){
        if (tabelaDeSimbolos.get(palavra.getLexeme()) != null){
            return "tipo_erro";
        }
        else{
            return "tipo_vazio";
        }
    }

    public static String verificaTipo (String tipo1, String tipo2){
        if(tipo1.equals(tipo2)){
            return tipo1;
        }
        else{
            return "tipo_erro";
        }
    }
}
