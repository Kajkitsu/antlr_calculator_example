import java.util.ArrayList;
import java.util.List;

public class ExpressionList extends ForwardingList<Expression> {
    public ExpressionList(List<Expression> operatorList) {
        super(new ArrayList<>(operatorList));
    }

    public ExpressionList() {
        super(new ArrayList<>());
    }

    public static ExpressionList of(Expression operable) {
        return new ExpressionList(List.of(operable));
    }

    public static ExpressionList emptyList() {
        return new ExpressionList();
    }

    public static ExpressionList of(List<Expression> operatorList) {
        return new ExpressionList(operatorList);
    }

    public static ExpressionList of(Expression leftExpression, Expression rightExpression) {
        return new ExpressionList(List.of(leftExpression, rightExpression));
    }

    public int maxLevel() {
        return this.stream()
                .map(Expression::getLevel)
                .max(Integer::compareTo)
                .orElse(0);
    }



}
