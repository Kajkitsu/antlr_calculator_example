import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Expression {

    private final OperatorList operatorList;
    private final ExpressionList expressionList;

    public Expression(List<Operator> operatorList, List<Expression> expressionList) {
        assert expressionList.size() >= 1;
        assert expressionList.size() - 1 == operatorList.size();
        this.operatorList = OperatorList.of(operatorList);
        this.expressionList = ExpressionList.of(expressionList);
    }

    protected Expression() {
        this.expressionList = null;
        this.operatorList = null;
    }

    public OperatorList getOperators() {
        return operatorList;
    }

    public ExpressionList getExpressions() {
        return expressionList;
    }

//    public static Expression from(Expression leftOperand, Operator operator, Expression rightExpression) {
//        return leftOperand.concat(operator, rightExpression);
//    }

    public Expression concat(Operator operator, Expression that) {
        if (this.canConcat(operator, that)) {
            return new Expression(
                    OperatorList.of(operator).concat(that.getOperators()),
                    ExpressionList.of(this).concat(that.getExpressions())
            );
        }
        return new Expression(
                OperatorList.of(operator),
                ExpressionList.of(this, that)
        );


    }

    private boolean canConcat(Operator operator, Expression that) {
        return operator.getLevel().equals(that.getLevel());
    }

    private Expression with(Operator operator, OperatorList operatorList, ExpressionList expressionList) { //TODO
        var newOperatorList = this.operatorList
                .concat(operator)
                .concat(operatorList);
        var newOperandList = this.expressionList
                .concat(expressionList);
        return new Expression(
                newOperatorList,
                newOperandList
        );
    }

    protected Expression with(Operator operator, Expression expression) { //TODO
        var operatorList = this.operatorList
                .concat(operator);
        var newExpressionList = this.expressionList.concat(expression);
        return new Expression(
                operatorList,
                newExpressionList
        );
    }

    Integer getLevel() {
        return operatorList.maxLevel();
    }

    public Expression times(Expression expression) {
        if (expression instanceof Number) {
            return ((Number) expression).times(this);
        }
        return this.concat(Operator.TIMES, expression);
    }

    public Expression div(Expression expression) {
        if (expression instanceof Number) {
            return ((Number) expression).div(this);
        }
        return this.concat(Operator.DIV, expression);
    }

    private Expression inverse() {
        return Number.ONE.div(this);
    }

    public Expression plus(Expression expression) {
        if (expression instanceof Number) {
            return ((Number) expression).plus(this);
        }
        return this.concat(Operator.PLUS, expression);
    }

    public Expression minus(Expression expression) {
        if (expression instanceof Number) {
            return ((Number) expression.negative()).plus(this);
        }
        return this.concat(Operator.MINUS, expression);
    }

    public Expression pow(Expression expression) {
//        if(expression instanceof Number) {
//            return ((Number)expression).pov(this);
//        }
        return this.concat(Operator.POW, expression);
    }

    public Expression negative() {
        return Number.ZERO.minus(this);
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(expressionList.get(0).toString());
        for (int i = 0; i < operatorList.size(); i++) {
            stringBuilder.append(operatorList.get(0).getValue());
            stringBuilder.append(expressionList.get(i + 1).toString());
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expression that = (Expression) o;

        if (!operatorList.equals(that.operatorList)) return false;
        return expressionList.equals(that.expressionList);
    }

    @Override
    public int hashCode() {
        int result = operatorList.hashCode();
        result = 31 * result + expressionList.hashCode();
        return result;
    }

    public Optional<Neutralizer> getNeutralizer() {
        if(this.getExpressions().size() == 0) {
            return Optional.empty();
        }
        Expression expression = this.getExpressions().get(0);
        if (expression instanceof Number) {
            Operator function = this.getOperators().get(0).getNeutralizer();
            return Optional.of(new Neutralizer((Number) expression, function));
        }
        if (this instanceof Number || this instanceof Variable) {
            return Optional.empty();
        }
        throw new RuntimeException("Unsupported operation");
    }

    public Function<Expression, Expression> getMethod(Operator operator) {
        return switch (operator) {
            case DIV -> this::div;
            case MINUS -> this::minus;
            case PLUS -> this::plus;
            case TIMES -> this::times;
            case POW -> this::pow;
        };
    }

    protected Expression withoutFirst() {
        var newExpressionList = this.expressionList.withoutFirst();
        var newOperatorList = this.operatorList.withoutFirst();
        if(newExpressionList.size() == 1) {
            return newExpressionList.get(0);
        }
        return new Expression(
                newOperatorList,
                newExpressionList
        );
    }


    //    public Operable acos() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.acos(this.getValue()));
//        }
//        return Atom.from("acos" + LPAREN + this + RPAREN);
//    }
//
//    public Operable asin() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.asin(this.getValue()));
//        }
//        return Atom.from("asin" + LPAREN + this + RPAREN);
//    }
//
//    public Operable atan() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.atan(this.getValue()));
//        }
//        return Atom.from("atan" + LPAREN + this + RPAREN);
//    }
//
//    public Operable cos() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.cos(this.getValue()));
//        }
//        return Atom.from("cos" + LPAREN + this + RPAREN);
//    }
//
//    public Operable sin() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.sin(this.getValue()));
//        }
//        return Atom.from("sin" + LPAREN + this + RPAREN);
//    }
//
//    public Operable tan() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.tan(this.getValue()));
//        }
//        return Atom.from("tan" + LPAREN + this + RPAREN);
//    }
//
//    public Operable sqrt() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.sqrt(this.getValue()));
//        }
//        return Atom.from("sqrt" + LPAREN + this + RPAREN);
//    }
//
//    public Operable log() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.log(this.getValue()));
//        }
//        return Atom.from("log" + LPAREN + this + RPAREN);
//    }
//
//    public Operable log10() {
//        if (isNumber()) {
//            return Atom.fromValue(Math.log(this.getValue()));
//        }
//        return Atom.from("log10" + LPAREN + this + RPAREN);
//    }

//    Operable acos();
//
//    Operable asin();
//
//    Operable atan();
//
//    Operable cos();
//
//    Operable sin();
//
//    Operable tan();
//
//    Operable sqrt();
//
//    Operable log();
//
//    Operable log10();


}
