import java.util.Optional;

public class PlusExpression extends Expression {

    public PlusExpression(ExpressionList expressionList) {
        super(BiOperator.PLUS, expressionList);
    }

    public Expression concat(Expression that){
        if (that.equals(this)) {
            return Number.TWO.times(that);
        }
        if(getExpressions().contains(that)) {
            return new PlusExpression(
                    getExpressions().without(that).with(Number.TWO.times(that)));
        }
        if(that instanceof PlusExpression) {
            return internalConcat((PlusExpression) that);
        }
        if(that instanceof Number) {
            return internalConcat((Number) that);
        }
        return internalConcat(that);

    }

    private Expression internalConcat(Expression that) {
        return new PlusExpression(getExpressions().with(that));

    }

    private Expression internalConcat(PlusExpression that) {
        Expression newExpression = this;
        for (Expression child: that.getExpressions().stream().toList()) {
            newExpression = this.concat(child);
        }
        return newExpression;

    }

    private Expression internalConcat(Number number) {
        return getExpressions()
                .with(number)
                .getNumbers()
                .stream()
                .reduce(Number::plus)
                .filter(it -> !it.equals(Number.ZERO))
                .map(newNumber -> new PlusExpression(getExpressions().withoutNumber().with(newNumber)))
                .orElseGet( () ->  new PlusExpression(getExpressions().withoutNumber()));
    }

}
