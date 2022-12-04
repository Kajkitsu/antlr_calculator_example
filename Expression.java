import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Expression {

    private final BiOperator operator;
    private final ExpressionList expressionList;

    private static Expression of(Expression left, BiOperator operator, Expression right) {
        return new Expression(
                operator,
                ExpressionList.of(left, right)
        );
    }

    private Expression(BiOperator operator, List<Expression> expressionList) {
        if (expressionList.size() <= 1) {
            throw new IllegalArgumentException("ExpressionList is not valid!");
        }
        this.operator = operator;
        this.expressionList = ExpressionList.of(expressionList);
    }

    protected Expression() {
        this.expressionList = null;
        this.operator = null;
    }

    public BiOperator getOperator() {
        return operator;
    }

    public ExpressionList getExpressions() {
        return expressionList;
    }

    public Integer getLevel() {
        return operator.getLevel();
    }

    private boolean isAtom() {
        return this.getLevel().equals(Level.ATOM);
    }

    public Expression plus(Variable variable) {
        if (getOperator().equals(BiOperator.PLUS)) {
            Optional<Variable> optionalVariable = getVariable(variable);
            if (optionalVariable.isEmpty()) {
                return this.merge(BiOperator.PLUS, variable);
            }
            Expression newVariable = variable.plus(optionalVariable.get());
            List<Expression> newExpressionList = new ArrayList<>(this.getExpressions());
            newExpressionList.remove(variable);

            if(newExpressionList.size() == 1) {
                return newVariable.plus(newExpressionList.get(0));
            }
            return new Expression(BiOperator.PLUS, newExpressionList).plus(newVariable);
        }
        return this.merge(BiOperator.PLUS, variable);
    }

    private Optional<Variable> getVariable(Variable variable) {
        return expressionList.stream()
                .filter(it -> it.equals(variable))
                .map(it -> (Variable) it)
                .findFirst();
    }

    public Expression plus(Number number) {
        if (getOperator().equals(BiOperator.PLUS)) {
            List<Number> numbers = getNumbers();
            if (numbers.isEmpty()) {
                return this.merge(BiOperator.PLUS,number);
            }
            List<Expression> newExpressionList = new ArrayList<>(getNotNumbers());
            numbers
                    .stream()
                    .reduce(Number::plus)
                    .map(it -> it.plus(number))
                    .filter(it -> !it.equals(Number.getInsignificant(getOperator())))
                    .ifPresent(newExpressionList::add);
            if(newExpressionList.size() == 1) {
                return newExpressionList.get(0);
            }
            return new Expression(this.getOperator(), newExpressionList);
        }
        return this.merge(BiOperator.PLUS, number);
    }

    public Expression plus(Expression expression) {
        if(this.getOperator().equals(BiOperator.PLUS)) {
            return this.join(expression);
        }
        return this.merge(BiOperator.PLUS, expression);
    }
    public Expression minus(Expression expression) {
        return this.merge(BiOperator.MINUS, expression);
    }

    public Expression times(Expression expression) {
        return this.merge(BiOperator.TIMES, expression);
    }

    public Expression div(Expression expression) {
        return this.merge(BiOperator.DIV, expression);
    }

    public Expression pow(Expression expression) {
        return this.merge(BiOperator.POW, expression);
    }


    public Expression negative() {
        return Number.MINUS_ONE.times(this);
    }

    public Expression inverse() {
        return Number.ONE.div(this);
    }

    private Expression merge(BiOperator biOperator, Expression that) {
        return Expression.of(
                this,
                biOperator,
                that
        );
    }


    private Expression join(Expression expression) {
        List<Expression> list = new ArrayList<>(this.getExpressions());
        list.add(expression);
        return new Expression(getOperator(), list);
    }


    public static Expression simplifyAll(Expression expressionToSimplify) {
        Expression oldExpression;
        Expression expression = expressionToSimplify;
        do {
            oldExpression = expression;
            expression = oldExpression.simplify();
        } while (!(expression.equals(oldExpression)));
        return expression.simplifyChilds();
    }

    private Expression simplifyChilds() {
        if(this.isAtom()) {
            return this;
        }
        List<Expression> newExpressionList = new ArrayList<>();
        for (var child : expressionList) {
            newExpressionList.add(Expression.simplifyAll(child));
        }
        return new Expression(this.getOperator(), newExpressionList);
    }

    private static Expression flatterAll(Expression expressionToFlatter) {
        Expression oldExpression;
        Expression expression = expressionToFlatter;
        do {
            oldExpression = expression;
            expression = oldExpression.flatterNonOrdered();
        } while (!(expression.equals(oldExpression)));
        return expression;
    }

    private Expression simplifyMinus() {
        List<Expression> newExpressionList = new ArrayList<>();
        newExpressionList.add(expressionList.get(0));
        for (var child : expressionList.subList(1, expressionList.size())) {
            newExpressionList.add(Number.MINUS_ONE.times(child));
        }
        return new Expression(BiOperator.PLUS, newExpressionList);
    }

    private Expression simplifyDiv() {
        List<Expression> newExpressionList = new ArrayList<>();
        newExpressionList.add(expressionList.get(0));
        for (var child : expressionList.subList(1, expressionList.size())) {
            newExpressionList.add(Number.ONE.div(child));
        }
        return new Expression(BiOperator.TIMES, newExpressionList);
    }

    private Expression flatterNonOrdered() {
        List<Expression> newExpressionList = new ArrayList<>();
        for (var child : expressionList) {
            if (this.getOperator().equals(child.getOperator())) {
                newExpressionList.addAll(child.getExpressions());
            } else {
                newExpressionList.add(child);
            }
        }
        return new Expression(this.getOperator(), newExpressionList);
    }

    private Expression simplify() {
        if(this.isAtom()) {
            return this;
        }
        if (this.getOperator().equals(BiOperator.MINUS)) {
            return this.simplifyMinus();
        }
        if (this.getOperator().equals(BiOperator.DIV)) {
            return this.simplifyDiv();
        }
        if (this.getOperator().equals(BiOperator.TIMES) || this.getOperator().equals(BiOperator.PLUS)) {
            return flatterAll(this)
                    .simplifyNonOrdered();
        }

        return this;
    }

    private Expression simplifyNonOrdered() {

        List<Number> numbers = getNumbers();
        if (numbers.isEmpty()) {
            return this;
        }
        List<Expression> newExpressionList = new ArrayList<>(getNotNumbers());
        numbers
                .stream()
                .reduce(Number.getMethod(getOperator()))
                .filter(it -> !it.equals(Number.getInsignificant(getOperator())))
                .ifPresent(newExpressionList::add);
        if(newExpressionList.size() == 1) {
            return newExpressionList.get(0);
        }
        return new Expression(this.getOperator(), newExpressionList);
    }

    private List<Expression> getNotNumbers() {
        return this.getExpressions()
                .stream()
                .filter(it -> !(it instanceof Number))
                .toList();
    }

    private List<Number> getNumbers() {
        return this.getExpressions()
                .stream()
                .filter(it -> it instanceof Number)
                .map(it -> (Number) it)
                .toList();
    }

    @Override
    public String toString() {
        return expressionList.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(operator.getValue().toString(), "(", ")"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expression that = (Expression) o;

        if (operator != that.operator) return false;
        return Objects.equals(expressionList, that.expressionList);
    }

    @Override
    public int hashCode() {
        int result = operator != null ? operator.hashCode() : 0;
        result = 31 * result + (expressionList != null ? expressionList.hashCode() : 0);
        return result;
    }

    public Optional<Neutralizer> getNeutralizer() {
        return getNumbers()
                .stream()
                .findFirst()
                .map(it -> new Neutralizer(it, getOperator().getNeutralizer()));
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


}
