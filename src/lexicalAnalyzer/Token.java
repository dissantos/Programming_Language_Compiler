package lexicalAnalyzer;

public class Token {
    public final int TAG;

    public Token(int TAG) {
        this.TAG = TAG;
    }

    @Override
    public String toString() {
        return "Token{" +
                "TAG=" + TAG +
                '}';
    }
}
