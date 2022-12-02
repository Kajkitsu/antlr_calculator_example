import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public class MainVisit extends CalculatorBaseVisitor<Atom> {


    private String getEquation(CalculatorParser.EquationContext ctx) {
        return "0 " + ctx.relop().getText() + visitExpression(ctx.expression(1)).minus(visitExpression(ctx.expression(0)));
    }

    @Override
    public Atom visitEquation(CalculatorParser.EquationContext ctx) {
        throw new RuntimeException("Unsupported operation");
    }

    @Override
    public Atom visitVariable(CalculatorParser.VariableContext ctx) {
        return Atom.from(ctx.getText());
    }

    @Override
    public Atom visitRelop(CalculatorParser.RelopContext ctx) {
        throw new RuntimeException("Unsupported operation");
    }

    @Override
    public Atom visitExpression(CalculatorParser.ExpressionContext ctx) {
        System.out.println("start visitExpression");
        Iterator<Atom> numbers = ctx.multiplyingExpression().stream().map(this::visit).iterator();
        List<Token> tokens = getSymbols(ctx);
        Atom result = numbers.next();
        for (Token token : tokens) {
            if (token.getType() == CalculatorParser.PLUS) {
                result = result.plus(numbers.next());
            } else {
                result = result.minus(numbers.next());
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
    public Atom visitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        System.out.println("start visitMultiplyingExpression");
        Iterator<Atom> numbers = ctx.powExpression().stream().map(this::visit).iterator();
        List<Token> tokens = getSymbols(ctx);
        Atom result = numbers.next();
        for (Token token : tokens) {
            if (token.getType() == CalculatorParser.TIMES) {
                result = result.times(numbers.next());
            } else {
                result = result.div(numbers.next());
            }
        }
        System.out.println("visitMultiplyingExpression \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Atom visitPowExpression(CalculatorParser.PowExpressionContext ctx) {
        System.out.println("start visitPowExpression");
        List<Token> tokens = getSymbols(ctx);
        List<Atom> doubleList = ctx.signedAtom()
                .stream()
                .map(this::visit)
                .toList();
        ListIterator<Atom> numbers = doubleList.listIterator(doubleList.size());
        Atom result = numbers.previous();
        for (Token token : tokens) {
            result = numbers.previous().pov(result);
        }
        System.out.println("visitPowExpression \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Atom visitSignedAtom(CalculatorParser.SignedAtomContext ctx) {
        System.out.println("start visitSignedAtom");
        Atom result = super.visitSignedAtom(ctx);
        if (ctx.MINUS() != null) {
            result = result.negative();
        }
        System.out.println("visitSignedAtom \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Atom visitAtom(CalculatorParser.AtomContext ctx) {
        System.out.println("start visitAtom");
        Atom result;
        if (ctx.expression() != null) {
            result = visit(ctx.expression());
        } else {
            result = super.visitAtom(ctx);
        }
        System.out.println("visitAtom \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Atom visitScientific(CalculatorParser.ScientificContext ctx) {
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
        return Atom.from(result);
    }

    @Override
    public Atom visitConstant(CalculatorParser.ConstantContext ctx) {
        System.out.println("start visitConstant");
        Atom result;
        if (ctx.PI() != null) {
            result = Atom.PI;
        } else if (ctx.EULER() != null) {
            result = Atom.E;
        } else {
            throw new RuntimeException("Unimplemented const");
        }
        System.out.println("visitConstant \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    @Override
    public Atom visitFunc_(CalculatorParser.Func_Context ctx) {
        System.out.println("start visitFunc_");
        Atom result = getFunction(ctx.funcname()).apply(visit(ctx.expression()));
        System.out.println("visitFunc_ \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    private Function<Atom, Atom> getFunction(CalculatorParser.FuncnameContext name) {
        return switch (name.getRuleIndex()) {
            case CalculatorParser.ACOS -> Atom::acos;
            case CalculatorParser.ASIN -> Atom::asin;
            case CalculatorParser.ATAN -> Atom::atan;
            case CalculatorParser.COS -> Atom::cos;
            case CalculatorParser.SIN -> Atom::sin;
            case CalculatorParser.TAN -> Atom::tan;
            case CalculatorParser.SQRT -> Atom::sqrt;
            case CalculatorParser.LN -> Atom::log;
            case CalculatorParser.LOG -> Atom::log10;
            default -> throw new RuntimeException("Function not implemented");
        };
    }

    @Override
    public Atom visitFuncname(CalculatorParser.FuncnameContext ctx) {
        System.out.println("start visitFuncname");
        var result = super.visitFuncname(ctx);
        System.out.println("visitFuncname \"" + ctx.getText() + "\" -> " + result);
        return result;
    }

    public static String evaluate(CharStream charStream) {
        CommonTokenStream tokens = new CommonTokenStream(new CalculatorLexer(charStream));
        var tree = new CalculatorParser(tokens).equation();
        return new MainVisit().getEquation(tree);
    }

    public static String evaluate(String expression) {
        return evaluate(CharStreams.fromString(expression));
    }

    public static void main(String[] args) throws Exception {
        CharStream charStreams = CharStreams.fromFileName("./example.txt");
        var result = evaluate(charStreams);
        System.out.println("Result = " + result);
    }
}
