package lexicalAnalyzer;

public class Number extends Token{
    public final double value;
    private String tipo;

    public Number(double value, String tipo) {
        super(Tag.NUMBER);
        this.value = value;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Number{" +
                "value=" + value +
                '}';
    }

    public String getTipo() {
        return tipo;
    }
}
