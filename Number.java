public class Number extends Expression {
    public static final Number PI = Number.from(Math.PI);
    public static final Number E = Number.from(Math.E);
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final Number ZERO = Number.from(0.0);
    public static final Number ONE = Number.from(1.0);

    private final Double value;

    public Number(Double value) {
        super();
        this.value = value;
    }

    @Override
    public OperatorList getOperators() {
        return OperatorList.emptyList();
    }

    @Override
    public ExpressionList getExpressions() {
        return ExpressionList.of(this);
    }

    @Override
    public Expression times(Expression that) {
        if (that instanceof Number) {
            return new Number(this.value * ((Number) that).value);
        }
        if(that.getLevel().equals(Level.MULTI_DIV) && that.getExpressions().get(0) instanceof Number firstNumber) {
            var newFirstNumber = new Number(this.value * firstNumber.value);
            if(newFirstNumber.equals(Number.ONE)) {
                return that.withoutFirst();
            }
            return new Expression(
                    that.getOperators(),
                    that.getExpressions().withFirstAs(newFirstNumber)
            );
        }
        return this.concat(Operator.TIMES, that);
    }

    @Override
    public Expression div(Expression that) {
        if (that instanceof Number) {
            return new Number(this.value / ((Number) that).value);
        }
        if(that.getLevel().equals(Level.MULTI_DIV) && that.getExpressions().get(0) instanceof Number firstNumber) {
            var newFirstNumber = new Number(this.value / firstNumber.value);
            if(newFirstNumber.equals(Number.ONE)) {
                return that.withoutFirst();
            }
            return new Expression(
                    that.getOperators(),
                    that.getExpressions().withFirstAs(newFirstNumber)
            );
        }
        return this.concat(Operator.DIV, that);
    }

    @Override
    public Expression plus(Expression that) {
        if (that instanceof Number) {
            return new Number(this.value + ((Number) that).value);
        }
        if(that.getLevel().equals(Level.ADD_SUB) && that.getExpressions().get(0) instanceof Number firstNumber) {
            var newFirstNumber = new Number(this.value + firstNumber.value);
            if(newFirstNumber.equals(Number.ZERO)) {
                return that.withoutFirst();
            }
            return new Expression(
                    that.getOperators(),
                    that.getExpressions().withFirstAs(newFirstNumber)
            );

        }
        return this.concat(Operator.PLUS, that);
    }

    @Override
    public Expression minus(Expression that) {
        if (that instanceof Number) {
            return new Number(this.value - ((Number) that).value);
        }
        if(that.getLevel().equals(Level.ADD_SUB) && that.getExpressions().get(0) instanceof Number firstNumber) {
            var newFirstNumber = new Number(this.value - firstNumber.value);
            if(newFirstNumber.equals(Number.ZERO)) {
                return that.withoutFirst();
            }
            return new Expression(
                    that.getOperators(),
                    that.getExpressions().withFirstAs(newFirstNumber)
            );
        }
        return this.concat(Operator.DIV, that);
    }

    @Override
    public Expression pow(Expression that) {
        if (that instanceof Number) {
            return new Number(this.value - ((Number) that).value);
        }
        return this.concat(Operator.DIV, that);
    }

    @Override
    public Expression negative() {
        return super.negative();
    }

    @Override
    Integer getLevel() {
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
