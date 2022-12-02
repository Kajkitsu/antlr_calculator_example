//import org.junit.jupiter.api.Test;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.util.List;
//import java.util.function.Function;
//
//class SpeedTest {
//
//    @Test
//    void test() {
//        //given
//        List<String> expressionList = List.of(
//                "2 + 2 * 2",
//                "2 + -2 * 2",
//                "2 - 2 * 2",
//                "-2 - 3 * 2 + -1 - 3 * -2",
//                "-2 + -2 - 0 / 2 + -1 - 3 * -2",
//                "-40 * 1 / 2 * 1 / 5",
//                "4 ^ 3 ^ 2 + 1",
//                "-2 - ln(1) * sin(0.75) + 2",
//                "-2 * (-2 + -5)",
//                "-2 + (-40 * 1 / 2 * 1 / 5) + 6");
//
//        //then
//        long visitorDuration = measureTime(MainVisit::evaluate, expressionList);
//        long listenerDuration = measureTime(MainListener::calc, expressionList);
//        System.out.println("visitor duration:"+visitorDuration);
//        System.out.println("listener duration:"+listenerDuration);
//        //expect
//        assert listenerDuration < visitorDuration;
//    }
//
//
//    private long measureTime(Function<String,Double> calcFunction, List<String> expressionList) {
//        Instant start = Instant.now();
//        for (var expression: expressionList) {
//            calcFunction.apply(expression);
//        }
//        Instant end = Instant.now();
//        return Duration.between(start, end).toNanos();
//    }
//
//
//
//}