import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void test1() {
        //given
        String expression = "2 + 2 * 2";
        //then
        Integer result = Calculator.calc(expression);
        //expect
        assert result == 6;
    }
    @Test
    void test2() {
        //given
        String expression = "2 + -2 * 2";
        //then
        Integer result = Calculator.calc(expression);
        //expect
        assert result == -2;
    }
    @Test
    void test3() {
        //given
        String expression = "2 - 2 * 2";
        //then
        Integer result = Calculator.calc(expression);
        //expect
        assert result == -2;
    }

    @Test
    void test4() {
        //given
        String expression = "-2 - 3 * 2 + -1 - 3 * -2";
        //then
        Integer result = Calculator.calc(expression);
        //expect
        assert result == -3;
    }

    @Test
    void test5() {
        //given
        String expression = "-2 + -2 - 0 / 2 + -1 - 3 * -2";
        //then
        Integer result = Calculator.calc(expression);
        //expect
        assert result == 1;
    }

    @Test
    void test6() {
        //given
        String expression = "-40 * 1 / 2 * 1 / 5";
        //then
        Integer result = Calculator.calc(expression);
        //expect
        assert result == -4;
    }

}