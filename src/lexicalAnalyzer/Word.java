package lexicalAnalyzer;

public class Word extends Token{
    private String lexeme;
    public static final Word NEQ = new Word("!=", Tag.NE);
    public static final Word EQ = new Word("==", Tag.EQ);
    public static final Word LE = new Word("<=", Tag.LE);
    public static final Word GE = new Word(">=", Tag.GE);
    public static final Word OR = new Word("||", Tag.OR);
    public static final Word AND = new Word("&&", Tag.AND);

    public Word(String s, int TAG) {
        super(TAG);
        this.lexeme = s;
    }

    @Override
    public String toString() {
        return "Word{" +
                "lexeme='" + lexeme + '\'' +
                '}';
    }


}
