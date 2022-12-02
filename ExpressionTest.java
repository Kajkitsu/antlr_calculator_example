import org.junit.jupiter.api.Test;


public class ExpressionTest {

    @Test
    public void test1() {
        //given
        var left = Number.from(1);
        var right = Number.from(2);
        //then
        var expression = left.plus(right);
        //expect
        assert expression instanceof Number;
        assert ((Number)expression).getValue().equals(3.0);
    }

    @Test
    public void test2() {
        //given
        var left = Number.from(1.0);
        var right = Variable.from('x');
        //then
        var expression = left.plus(right);
        //expect
        assert expression.getExpressions().get(0) instanceof Number;
        assert ((Number) expression.getExpressions().get(0)).getValue().equals(1.0);
        assert expression.getExpressions().get(1) instanceof Variable;
        assert ((Variable) expression.getExpressions().get(1)).getValue().equals('x');
        assert expression.getOperators().get(0).equals(Operator.PLUS);
    }

    @Test
    public void test3() {
        //given
        var first = Number.from(1.0);
        var second = Number.from(1.0);
        var third = Variable.from('x');
        //then
        var expression = first.plus(second).plus(third);
        //expect
        assert expression.getExpressions().get(0) instanceof Number;
        assert ((Number) expression.getExpressions().get(0)).getValue().equals(2.0);
        assert expression.getExpressions().get(1) instanceof Variable;
        assert ((Variable) expression.getExpressions().get(1)).getValue().equals('x');
        assert expression.getOperators().get(0).equals(Operator.PLUS);
    }

    @Test
    public void test4() {
        //given
        var first = Number.from(1.0);
        var second = Number.from(2.0);
        var third = Number.from(3.0);
        var forth = Number.from(4.0);
        //then
        var expression = first.plus(second).times(third.plus(forth));
        //expect
        assert expression instanceof Number;
        assert ((Number)expression).getValue().equals(21.0);
    }


//
//    @Test
//    public void test2() {
//        //given
//        var left = Expression.from(Number.fromValue(1), Operator.MINUS, Number.fromValue(2));
//        var right = Number.fromValue(3);
//        var operator = Operator.PLUS;
//        //then
//        var expression = (Expression)Expression.from(left, operator, right);
//        //expect
//        assert expression.getOperators().get(0).equals(Operator.MINUS);
//        assert expression.getOperators().get(1).equals(Operator.PLUS);
//        assert expression.getExpressions().get(0).equals(Number.fromValue(1));
//        assert expression.getExpressions().get(1).equals(Number.fromValue(2));
//        assert expression.getExpressions().get(2).equals(Number.fromValue(3));
//    }
//
//    @Test
//    public void test3() {
//        //given
//        var left = Expression.from(Number.fromValue(1), Operator.MINUS, Number.fromValue(2));
//        var right = Expression.from(Number.fromValue(3), Operator.MINUS, Number.fromValue(4));
//        var operator = Operator.PLUS;
//        //then
//        var expression = (Expression)Expression.from(left, operator, right);
//        //expect
//        assert expression.getOperators().get(0).equals(Operator.MINUS);
//        assert expression.getOperators().get(1).equals(Operator.PLUS);
//        assert expression.getOperators().get(2).equals(Operator.MINUS);
//        assert expression.getExpressions().get(0).equals(Number.fromValue(1));
//        assert expression.getExpressions().get(1).equals(Number.fromValue(2));
//        assert expression.getExpressions().get(2).equals(Number.fromValue(3));
//        assert expression.getExpressions().get(3).equals(Number.fromValue(4));
//    }
//
//    @Test
//    public void test4() {
//        //given
//        var left = Expression.from(Number.fromValue(1), Operator.TIMES, Number.fromValue(2));
//        var right = Expression.from(Number.fromValue(3), Operator.MINUS, Number.fromValue(4));
//        var operator = Operator.PLUS;
//        //then
//        var expression = (Expression)Expression.from(left, operator, right);
//        //expect
//        assert expression.getOperators().get(0).equals(Operator.PLUS);
//        assert expression.getOperators().get(1).equals(Operator.MINUS);
//        assert expression.getExpressions().get(0).equals(Expression.from(Number.fromValue(1), Operator.TIMES, Number.fromValue(2)));
//        assert expression.getExpressions().get(1).equals(Number.fromValue(3));
//        assert expression.getExpressions().get(2).equals(Number.fromValue(4));
//    }

}
