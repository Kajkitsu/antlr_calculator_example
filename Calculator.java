import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Function;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Calculator extends CalculatorBaseListener {
    private final LinkedList<Double> firstStack = new LinkedList<>();
    private final LinkedList<Double> secondStack = new LinkedList<>();

    public Double getResult() {
        return secondStack.pop();
    }

    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        Double result = secondStack.removeLast();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.PLUS)) {
                result = result + secondStack.removeLast();
            } else {
                result = result - secondStack.removeLast();
            }
        }
        secondStack.push(result);
        System.out.println("Expression: \"" + ctx.getText() + "\" -> " + result);
    }


    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        Double result = firstStack.removeLast();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.TIMES)) {
                result = result * firstStack.removeLast();
            } else {
                result = result / firstStack.removeLast();
            }
        }
        secondStack.push(result);
        System.out.println("MultiplyingExpression: \"" + ctx.getText() + "\" -> " + result);
    }

    @Override
    public void exitPowExpression(CalculatorParser.PowExpressionContext ctx) {
        var result = firstStack.pop();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            result = Math.pow(result, firstStack.pop());
        }
        firstStack.push(result);
        System.out.println("PowExpression: \"" + ctx.getText() + "\" -> " + result);
    }

    @Override
    public void exitSignedAtom(CalculatorParser.SignedAtomContext ctx) {
        if (ctx.MINUS() != null) {
            firstStack.push(-1 * firstStack.pop());
        }

        System.out.println("SignedAtom: \"" + ctx.getText() + "\" -> " + firstStack.peek());
    }

    @Override
    public void exitAtom(CalculatorParser.AtomContext ctx) {
        System.out.println("Atom: \"" + ctx.getText() + "\" -> " + firstStack.peek());
    }

    @Override
    public void exitScientific(CalculatorParser.ScientificContext ctx) {
        String scientificNumber = ctx.getText();
        double firstNumber = Arrays.stream(scientificNumber.split("[Ee]")).findFirst().map(Double::valueOf)
                .orElseThrow(() -> new RuntimeException("first number in SCIENTIFIC token not found"));
        double secondNumber = Arrays.stream(scientificNumber.split("[Ee]"))
                .skip(1)
                .findFirst()
                .map(it -> it.replace("+", ""))
                .map(it -> it.replace("-", ""))
                .map(Double::valueOf)
                .orElse(0.0);
        if (scientificNumber.contains("-")) {
            firstStack.push(firstNumber * Math.pow(10, -1 * secondNumber));
        } else {
            firstStack.push(firstNumber * Math.pow(10, secondNumber));
        }
        System.out.println("Scientific: \"" + ctx.getText() + "\" -> " + firstStack.peek());
    }

    @Override
    public void exitConstant(CalculatorParser.ConstantContext ctx) {
        if (ctx.PI() != null) {
            firstStack.push(Math.PI);
        } else if (ctx.EULER() != null) {
            firstStack.push(Math.E);
        } else {
            throw new RuntimeException("Unimplemented const");
        }
        System.out.println("Constant: \"" + ctx.getText() + "\" -> " + firstStack.peek());
    }

    @Override
    public void exitFunc_(CalculatorParser.Func_Context ctx) {
        Double result = getFunction(ctx.funcname()).apply(firstStack.pop());
        firstStack.push(result);
        System.out.println("Func_: \"" + ctx.getText() + "\" -> " + result);

    }

    private Function<Double, Double> getFunction(CalculatorParser.FuncnameContext name) {
        return switch (name.getRuleIndex()) {
            case CalculatorParser.ACOS -> Math::acos;
            case CalculatorParser.ASIN -> Math::asin;
            case CalculatorParser.ATAN -> Math::atan;
            case CalculatorParser.COS -> Math::cos;
            case CalculatorParser.SIN -> Math::sin;
            case CalculatorParser.TAN -> Math::tan;
            case CalculatorParser.SQRT -> Math::sqrt;
            case CalculatorParser.LN -> Math::log;
            case CalculatorParser.LOG -> Math::log10;
            default -> throw new RuntimeException("Function not implemented");
        };
    }

    private boolean symbolEquals(ParseTree child, int symbol) {
        return ((TerminalNode) child).getSymbol().getType() == symbol;
    }

    public static Double calc(CharStream charStream) {
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        System.out.println(tokens.getText());

        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();

        ParseTreeWalker walker = new ParseTreeWalker();
        Calculator calculatorListener = new Calculator();
        walker.walk(calculatorListener, tree);
        return calculatorListener.getResult();
    }

    public static Double calc(String expression) {
        return calc(CharStreams.fromString(expression));
    }

    public static void main(String[] args) throws Exception {
        CharStream charStreams = CharStreams.fromFileName("./example.txt");
        Double result = calc(charStreams);
        System.out.println("Result = " + result);
    }
}

