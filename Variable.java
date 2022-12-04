public class Variable extends Expression {
    private final Character value;

    public Variable(Character value) {
        this.value = value;
    }

    public static Variable from(char value) {
        return new Variable(value);
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
    public Integer getLevel() {
        return Level.ATOM;
    }

    @Override
    public Expression plus(Expression that) {
        if(this.equals(that)) {
            return this.times(Number.TWO);
        }
        return super.plus(that);
    }

    @Override
    public Expression times(Expression that) {
        if(this.equals(that)) {
            this.pow(Number.TWO);
        }
        return super.times(that);    }

    @Override
    public Expression div(Expression that) {
        if(this.equals(that)) {
            return Number.ONE;
        }
        return super.div(that);
    }

    @Override
    public Expression minus(Expression that) {
        if(this.equals(that)) {
            return Number.ZERO;
        }
        return super.minus(that);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        return value.equals(variable.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public Character getValue() {
        return value;
    }
}
