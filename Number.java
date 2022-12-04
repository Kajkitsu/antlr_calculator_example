import java.util.function.BinaryOperator;

public class Number extends Expression {
    public static final Number PI = Number.from(Math.PI);
    public static final Number E = Number.from(Math.E);
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final Number ZERO = Number.from(0.0);
    public static final Number ONE = Number.from(1.0);
    public static final Number TWO = Number.from(2.0);
    public static final Number MINUS_ONE = Number.from(-1.0);

    private final Double value;

    public Number(Double value) {
        super();
        this.value = value;
    }

    public static BinaryOperator<Number> getMethod(BiOperator operator) {
        return switch (operator) {
            case DIV -> Number::div;
            case MINUS -> Number::minus;
            case PLUS -> Number::plus;
            case TIMES -> Number::times;
//            case POW -> Number::pow;
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number getInsignificant(BiOperator operator) {
        return switch (operator) {
            case PLUS -> Number.ZERO;
            case TIMES -> Number.ONE;
//            case POW -> Number::pow;
            default -> throw new UnsupportedOperationException();
        };
    }

    @Override
    public BiOperator getOperator() {
        return BiOperator.ATOM;
    }

    @Override
    public ExpressionList getExpressions() {
        return ExpressionList.of(this);
    }

    @Override
    public Expression plus(Expression expression) {
        if(expression instanceof Number) {
            return this.plus((Number) expression);
        }
        return super.plus(expression);
    }

    public Number plus(Number number) {
        return new Number(this.value + number.value);
    }

    @Override
    public Expression minus(Expression expression) {
        if(expression instanceof Number) {
            return this.minus((Number) expression);
        }
        return super.minus(expression);
    }

    public Number minus(Number number) {
        return new Number(this.value - number.value);
    }

    @Override
    public Expression times(Expression expression) {
        if(expression instanceof Number) {
            return this.times((Number) expression);
        }
        return super.times(expression);
    }

    public Number times(Number number) {
        return new Number(this.value * number.value);
    }

    @Override
    public Expression div(Expression expression) {
        if(expression instanceof Number) {
            return this.div((Number) expression);
        }
        return super.div(expression);
    }

    private Number div(Number number) {
        return new Number(this.value / number.value);
    }

//    public Operable pow(Number number) {
//        return new Number(Math.pow(this.value, number.value));
//    }

    @Override
    public Integer getLevel() {
        return Level.ATOM;
    }

    public static Number from(double value) {
        return new Number(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Number number = (Number) o;

        return value.equals(number.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public Double getValue() {
        return this.value;
    }
}
