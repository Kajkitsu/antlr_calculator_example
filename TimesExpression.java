public class TimesExpression extends Expression {

    public TimesExpression(ExpressionList expressionList) {
        super(BiOperator.TIMES, expressionList);
    }

    public Expression concat(Expression that){
        if (that.equals(this)) {
            return that.pow(Number.TWO);
        }
        if(getExpressions().contains(that)) {
            return new TimesExpression(
                    getExpressions().without(that).with(that.pow(Number.TWO)));
        }
        if(that instanceof TimesExpression) {
            return internalConcat((TimesExpression) that);
        }
        if(that instanceof Number) {
            return internalConcat((Number) that);
        }
        return internalConcat(that);

    }

    private Expression internalConcat(Expression that) {
        return new TimesExpression(getExpressions().with(that));

    }

    private Expression internalConcat(TimesExpression that) {
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
                .reduce(Number::times)
                .filter(it -> !it.equals(Number.ZERO))
                .map(newNumber -> new TimesExpression(getExpressions().withoutNumber().with(newNumber)))
                .orElseGet( () ->  new TimesExpression(getExpressions().withoutNumber()));
    }

}
