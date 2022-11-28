import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class MainListener extends CalculatorBaseListener {
    private final MyStack<Double> stack = new MyStack<>();

    public Double getResult() {
        return stack.pop();
    }

    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        List<Token> tokens = getSymbols(ctx);
        Iterator<Double> numbers = stack.pop(ctx.getChildCount() - tokens.size()).iterator();
        Double result = numbers.next();
        for(Token token: tokens) {
            if(token.getType() == CalculatorParser.PLUS) {
                result = result + numbers.next();
            } else {
                result = result - numbers.next();
            }
        }
        stack.push(result);
        System.out.println("Expression: \"" + ctx.getText() + "\" -> " + result);
    }


    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        List<Token> tokens = getSymbols(ctx);
        Iterator<Double> numbers = stack.pop(ctx.getChildCount() - tokens.size()).iterator();
        Double result = numbers.next();
        for(Token token: tokens) {
            if(token.getType() == CalculatorParser.TIMES) {
                result = result * numbers.next();
            } else {
                result = result / numbers.next();
            }
        }
        stack.push(result);
        System.out.println("MultiplyingExpression: \"" + ctx.getText() + "\" -> " + result);
    }

    @Override
    public void exitPowExpression(CalculatorParser.PowExpressionContext ctx) {
        List<Token> tokens = getSymbols(ctx);
        Iterator<Double> numbers = stack.popReverted(ctx.getChildCount() - tokens.size()).iterator();
        Double result = numbers.next();
        for(Token token: tokens) {
            result = Math.pow(numbers.next(), result);
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

    private List<Token> getSymbols(ParseTree ctx) {
        LinkedList<Token> list = new LinkedList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if(ctx.getChild(i) instanceof TerminalNode) {
                list.add(((TerminalNode)ctx.getChild(i)).getSymbol());
            }
        }
        return list;
    }


    public static Double calc(CharStream charStream) {
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        System.out.println(tokens.getText());

        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();

        ParseTreeWalker walker = new ParseTreeWalker();
        MainListener mainListener = new MainListener();
        walker.walk(mainListener, tree);
        return mainListener.getResult();
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

