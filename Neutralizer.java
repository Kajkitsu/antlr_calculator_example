import java.util.function.Function;

public class Neutralizer {
    private final Number number;
    private final Operator operator;

    public Neutralizer(Number number, Operator operator) {
        this.number = number;
        this.operator = operator;
    }

    public Number getNumber() {
        return number;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "Neutralizer{" +
                "number=" + number +
                ", operator=" + operator +
                '}';
    }
}
