public class Variable extends Expression {
    private final Character value;

    public Variable(Character value) {
        super(BiOperator.ATOM, null);
        this.value = value;
    }

    public static Variable from(char value) {
        return new Variable(value);
    }

    @Override
    Expression concat(Expression that) {
        throw new UnsupportedOperationException();
    }


    @Override
    public ExpressionList getExpressions() {
        return new ExpressionList(this);
    }

    @Override
    public Integer getLevel() {
        return Level.ATOM;
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
