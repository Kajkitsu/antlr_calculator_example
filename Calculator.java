import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Calculator extends CalculatorBaseListener {
    private final Stack<Integer> stack = new Stack<>();

    public Integer getResult() {
        return stack.pop();
    }


    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        System.out.print(ctx + " -> ");
        for (int i = 0; i < ctx.getChildCount(); i++) {
            System.out.print(ctx.getChild(i));
        }
        System.out.println(" : \"" + ctx.getText() + "\"");
    }

    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        Integer result = stack.pop();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.PLUS)) {
                result += stack.pop();
            } else {
                result -= stack.pop();
            }
        }
        stack.push(result);
    }


    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        Integer result = stack.pop();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.TIMES)) {
                result *= stack.pop();
            } else {
                result /= stack.pop();
            }
        }
        stack.push(result);
    }

    private boolean symbolEquals(ParseTree child, int symbol) {
        return ((TerminalNode) child).getSymbol().getType() == symbol;
    }

    @Override
    public void exitIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        int value = Integer.parseInt(ctx.INT().getText());
        if (ctx.MINUS() != null) {
            stack.push(-1 * value);
        } else {
            stack.push(value);
        }
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
        System.out.println(calculatorListener.getResult());
    }
}

