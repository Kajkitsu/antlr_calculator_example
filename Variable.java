import java.util.List;

public class Variable extends Expression {
    private final Character value;

    public Variable(Character value) {
        super();
        this.value = value;
    }

    public static Variable from(char value) {
        return new Variable(value);
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
    Integer getLevel() {
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
