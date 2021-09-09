package lexicalAnalyzer;

public class Word extends Token{
    private String lexeme;
    private String tipo;
    public static final Word NEQ = new Word("!=", Tag.NE);
    public static final Word EQ = new Word("==", Tag.EQ);
    public static final Word LE = new Word("<=", Tag.LE);
    public static final Word GE = new Word(">=", Tag.GE);
    public static final Word OR = new Word("||", Tag.OR);
    public static final Word AND = new Word("&&", Tag.AND);

    public Word(String s, int TAG) {
        super(TAG);
        this.lexeme = s;
        this.tipo = "tipo_erro-nao-declarado";
    }

    @Override
    public String toString() {
        return "Word{" +
                "lexeme='" + lexeme + "\', tag= " + this.TAG +
                '}';
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
