import org.antlr.v4.runtime.atn.PlusBlockStartState;
import org.junit.jupiter.api.Test;


public class ExpressionEqualsTest {

    @Test
    public void test1() {
        //given
        var firstExpressionList = new ExpressionList(Number.from(1), Variable.from('x'), Number.from(3));
        var secondExpressionList = new ExpressionList(Variable.from('x'), Number.from(3), Number.from(1));
        //then
        var first = new PlusExpression(firstExpressionList);
        var second  = new PlusExpression(secondExpressionList);
        //expect
        assert first.equals(second);
    }

    @Test
    public void test2() {
        //given
        var firstExpressionList = new ExpressionList(Number.from(1), Variable.from('x'), Number.from(3));
        var secondExpressionList = new ExpressionList(Variable.from('x'), Number.from(3), Number.from(1));
        //then
        var first = new MinusExpression(firstExpressionList);
        var second  = new PlusExpression(secondExpressionList);
        //expect
        assert !first.equals(second);
    }

    @Test
    public void test3() {
        //given
        var firstExpressionList = new ExpressionList(Number.from(1), Variable.from('x'), Number.from(3));
        var secondExpressionList = new ExpressionList(Variable.from('x'), Number.from(3), Number.from(1));
        //then
        var first = new PlusExpression(firstExpressionList);
        var second  = new MinusExpression(secondExpressionList);
        //expect
        assert !first.equals(second);
    }

    @Test
    public void test4() {
        //given
        var firstExpressionList = new ExpressionList(Number.from(1), Variable.from('x'), Number.from(3));
        var secondExpressionList = new ExpressionList(Variable.from('x'), Number.from(3), Number.from(1));
        //then
        var first = new MinusExpression(firstExpressionList);
        var second  = new PlusExpression(secondExpressionList);
        //expect
        assert !first.equals(second);
    }


}
