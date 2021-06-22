package lexicalAnalyzer;

public class Number extends Token{
    public final double value;

    public Number(double value) {
        super(Tag.NUMBER);
        this.value = value;
    }

    @Override
    public String toString() {
        return "Number{" +
                "value=" + value +
                '}';
    }
}
