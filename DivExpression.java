public class DivExpression extends Expression {

    public DivExpression(ExpressionList expressionList) {
        super(BiOperator.DIV, expressionList);
    }
    public Expression concat(Expression that){ //TODO
        if (that.equals(this)) {
            return Number.ONE;
        }
        if (that.getExpressions().contains(that)) {
            return new DivExpression(getExpressions().without(that));
        }

        Expression newPlusExpression = new PlusExpression(new ExpressionList(getExpressions().get(0).orElseThrow()));

        for (var child: this.getExpressions().stream().skip(1).toList()) {
            newPlusExpression = newPlusExpression.concat(Number.MINUS_ONE.times(child));
        }
        return newPlusExpression;
    }

}
