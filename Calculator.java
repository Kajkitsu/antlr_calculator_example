import java.util.Arrays;
import java.util.Stack;
import java.util.function.Function;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Calculator extends CalculatorBaseListener {
    private final Stack<Double> stack = new Stack<>();

    public Double getResult() {
        return stack.peek();
    }

    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        var result = stack.pop();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.PLUS)) {
                result += stack.pop();
            } else {
                result -= stack.pop();
            }
        }
        stack.push(result);
        System.out.println("Expression: \"" + ctx.getText() + "\" -> " + result);
    }


    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        var result = stack.pop();
        for (int i = ctx.getChildCount() - 2; i >= 1; i = i - 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.TIMES)) {
                result = stack.pop() * result;
            } else {
                result = stack.pop() / result;
            }
        }
        stack.push(result);
        System.out.println("MultiplyingExpression: \"" + ctx.getText() + "\" -> " + result);
    }

    @Override
    public void exitPowExpression(CalculatorParser.PowExpressionContext ctx) {
        var result = stack.pop();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            result = Math.pow(result, stack.pop());
        }
        stack.push(result);
        System.out.println("PowExpression: \"" + ctx.getText() + "\" -> " + result);
    }

    @Override
    public void exitSignedAtom(CalculatorParser.SignedAtomContext ctx) {
        if (ctx.MINUS() != null) {
            stack.push(-1 * stack.pop());
        }

        System.out.println("SignedAtom: \"" + ctx.getText() + "\" -> " + stack.peek());
    }

    @Override
    public void exitAtom(CalculatorParser.AtomContext ctx) {
        System.out.println("Atom: \"" + ctx.getText() + "\" -> " + stack.peek());
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
            stack.push(firstNumber * Math.pow(10, -1 * secondNumber));
        } else {
            stack.push(firstNumber * Math.pow(10, secondNumber));
        }
        System.out.println("Scientific: \"" + ctx.getText() + "\" -> " + stack.peek());
    }

    @Override
    public void exitConstant(CalculatorParser.ConstantContext ctx) {
        if (ctx.PI() != null) {
            stack.push(Math.PI);
        } else if (ctx.EULER() != null) {
            stack.push(Math.E);
        } else {
            throw new RuntimeException("Unimplemented const");
        }
        System.out.println("Constant: \"" + ctx.getText() + "\" -> " + stack.peek());
    }

    @Override
    public void exitFunc_(CalculatorParser.Func_Context ctx) {
        Double result = getFunction(ctx.funcname()).apply(stack.pop());
        stack.push(result);
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

    public static void main(String[] args) throws Exception {
        CharStream charStreams = CharStreams.fromFileName("./example.txt");
        CalculatorLexer lexer = new CalculatorLexer(charStreams);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        System.out.println(tokens.getText());

        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();

        ParseTreeWalker walker = new ParseTreeWalker();
        Calculator calculatorListener = new Calculator();
        walker.walk(calculatorListener, tree);
        System.out.println("Result = " + calculatorListener.getResult());
    }
}

