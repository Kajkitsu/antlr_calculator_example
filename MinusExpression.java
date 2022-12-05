import org.antlr.v4.runtime.atn.PlusLoopbackState;

public class MinusExpression extends Expression {

    public MinusExpression(ExpressionList expressionList) {
        super(BiOperator.MINUS, expressionList);
    }
    public Expression concat(Expression that){ //TODO
        if (that.equals(this)) {
            return Number.ZERO;
        }
        if (that.getExpressions().contains(that)) {
            return new MinusExpression(getExpressions().without(that));
        }

        Expression newPlusExpression = new PlusExpression(new ExpressionList(getExpressions().get(0).orElseThrow()));

        for (var child: this.getExpressions().stream().skip(1).toList()) {
            newPlusExpression = newPlusExpression.concat(Number.MINUS_ONE.times(child));
        }
        return newPlusExpression;
    }

}
