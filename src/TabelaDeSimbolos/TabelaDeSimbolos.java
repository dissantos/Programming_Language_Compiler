package TabelaDeSimbolos;

import lexicalAnalyzer.Word;

import java.util.Hashtable;

public class TabelaDeSimbolos {
    private Hashtable<String, Object> tabelaDeSimbolos;
    protected TabelaDeSimbolos prev;

    public TabelaDeSimbolos(TabelaDeSimbolos n){
        tabelaDeSimbolos = new Hashtable();
        prev = n; //ultima tabela no topo
    }

    // Insere lexema e word na TS
    public void put(String lexema, Word word){
        tabelaDeSimbolos.put(lexema, word);
    }

    public Word get(String lexema){
        for (TabelaDeSimbolos e = this; e!=null; e = e.prev){
            Word found = (Word) e.tabelaDeSimbolos.get(lexema);
            if (found != null) //se Token existir em uma das TS
                return found;
        }
        return null; //caso Token não exista em uma das TS
    }

    public Hashtable<String, Object> getTabelaDeSimbolos() {
        return tabelaDeSimbolos;
    }

}
