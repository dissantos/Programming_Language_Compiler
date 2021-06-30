package lexicalAnalyzer;

public class Literal extends Token{
    public final String str;

    public Literal(int TAG, String str) {
        super(TAG);
        this.str = str;
    }

    @Override
    public String toString() {
        return "Literal{" +
                "str='" + this.str + "'" +
                '}';
    }
}
