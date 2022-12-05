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
        assert expression != null;
        assert expression.getValue().equals(3.0);
    }

    @Test
    public void test2() {
        //given
        var left = Number.from(1.0);
        var right = Variable.from('x');
        //then
        var expression = left.plus(right);
        //expect
        assert expression.getExpressions().get(0).get() instanceof Number;
        assert ((Number) expression.getExpressions().get(0).get()).getValue().equals(1.0);
        assert expression.getExpressions().get(1).get() instanceof Variable;
        assert ((Variable) expression.getExpressions().get(1).get()).getValue().equals('x');
        assert expression.getOperator().equals(BiOperator.PLUS);
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
        assert expression.getExpressions().get(0).get() instanceof Number;
        assert ((Number) expression.getExpressions().get(0).get()).getValue().equals(2.0);
        assert expression.getExpressions().get(1).get() instanceof Variable;
        assert ((Variable) expression.getExpressions().get(1).get()).getValue().equals('x');
        assert expression.getOperator().equals(BiOperator.PLUS);
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
        assert expression != null;
        assert expression.getValue().equals(21.0);
    }

    @Test
    public void test5() {
        //given
        var first = Variable.from('x');
        var second = Number.from(2.0);
        var third = Variable.from('x');
        var forth = Number.from(4.0);
        //then
        var expression = first.plus(second).plus(third).plus(forth);
        //expect
        assert expression != null;
        assert expression.getExpressions().get(0).get().equals(Variable.from('x').times(Number.TWO));
        assert expression.getExpressions().get(1).get().equals(Number.from(6.0));
    }


}
