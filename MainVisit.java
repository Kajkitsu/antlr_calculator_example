import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public class MainVisit extends CalculatorBaseVisitor<Double> {

    @Override
    public Double visitExpression(CalculatorParser.ExpressionContext ctx) {
        System.out.println("start visitExpression");
        Iterator<Double> numbers = ctx.multiplyingExpression().stream().map(this::visit).iterator();
        List<Token> tokens = getSymbols(ctx);
        Double result = numbers.next();
        for (Token token : tokens) {
            if (token.getType() == CalculatorParser.PLUS) {
                result = result + numbers.next();
            } else {
                result = result - numbers.next();
            }
        }
        System.out.println("visitExpression: \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    private List<Token> getSymbols(ParseTree ctx) {
        LinkedList<Token> list = new LinkedList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof TerminalNode) {
                list.add(((TerminalNode) ctx.getChild(i)).getSymbol());
            }
        }
        return list;
    }

    @Override
    public Double visitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        System.out.println("start visitMultiplyingExpression");
        Iterator<Double> numbers = ctx.powExpression().stream().map(this::visit).iterator();
        List<Token> tokens = getSymbols(ctx);
        Double result = numbers.next();
        for (Token token : tokens) {
            if (token.getType() == CalculatorParser.TIMES) {
                result = result * numbers.next();
            } else {
                result = result / numbers.next();
            }
        }
        System.out.println("visitMultiplyingExpression \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Double visitPowExpression(CalculatorParser.PowExpressionContext ctx) {
        System.out.println("start visitPowExpression");
        List<Token> tokens = getSymbols(ctx);
        List<Double> doubleList = ctx.signedAtom()
                .stream()
                .map(this::visit)
                .toList();
        ListIterator<Double> numbers = doubleList.listIterator(doubleList.size());
        Double result = numbers.previous();
        for (Token token : tokens) {
            result = Math.pow(numbers.previous(), result);
        }
        System.out.println("visitPowExpression \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Double visitSignedAtom(CalculatorParser.SignedAtomContext ctx) {
        System.out.println("start visitSignedAtom");
        Double result = super.visitSignedAtom(ctx);
        if (ctx.MINUS() != null) {
            result = -1 * result;
        }
        System.out.println("visitSignedAtom \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Double visitAtom(CalculatorParser.AtomContext ctx) {
        System.out.println("start visitAtom");
        Double result;
        if(ctx.expression() != null) {
            result = visit(ctx.expression());
        } else {
            result = super.visitAtom(ctx);
        }
        System.out.println("visitAtom \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Double visitScientific(CalculatorParser.ScientificContext ctx) {
        System.out.println("start visitScientific");
        double result;
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
            result = firstNumber * Math.pow(10, -1 * secondNumber);
        } else {
            result = firstNumber * Math.pow(10, secondNumber);
        }
        System.out.println("visitScientific \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Double visitConstant(CalculatorParser.ConstantContext ctx) {
        System.out.println("start visitConstant");
        double result;
        if (ctx.PI() != null) {
            result = Math.PI;
        } else if (ctx.EULER() != null) {
            result = Math.E;
        } else {
            throw new RuntimeException("Unimplemented const");
        }
        System.out.println("visitConstant \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Double visitFunc_(CalculatorParser.Func_Context ctx) {
        System.out.println("start visitFunc_");
        Double result = getFunction(ctx.funcname()).apply(visit(ctx.expression()));
        System.out.println("visitFunc_ \"" + ctx.getText() + "\" -> " + result);
        return result;
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

    @Override
    public Double visitFuncname(CalculatorParser.FuncnameContext ctx) {
        System.out.println("start visitFuncname");
        var result = super.visitFuncname(ctx);
        System.out.println("visitFuncname \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    public static Double calc(CharStream charStream) {
        CommonTokenStream tokens = new CommonTokenStream(new CalculatorLexer(charStream));
        ParseTree tree = new CalculatorParser(tokens).expression();
        return new MainVisit().visit(tree);
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
