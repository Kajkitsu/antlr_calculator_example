import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Calculator extends CalculatorBaseListener {
    private final Stack<Integer> stack = new Stack<>();

    public Integer getResult() {
        return stack.peek();
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
        Integer left = stack.peek();
        Integer right = stack.peek();

        Integer result = calc(ctx, 0);
        stack.push(result);

        if (ctx.PLUS().size() > 0) {
            result = left + right;
        } else {
            result = left - right;
        }
        stack.push(result);
    }

    private Integer calc(CalculatorParser.ExpressionContext ctx, int i) {
        if(ctx.getChildCount() == (i+1)) {
            return stack.pop();
        }
        if(CalculatorParser.TIMES == ctx.getRuleIndex()) {
            /////
        }
        CalculatorParser.TIMES == ctx.getRuleIndex()
        ctx.getChild(i+1)()
        if(ctx.getChild(i+1) )

        return null;
    }


    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        Integer left = stack.peek();
        Integer right = stack.peek();
        Integer result;
        if (ctx.TIMES().size() > 0) {
            result = left * right;
        } else {
            result = left / right;
        }
        stack.push(result);
    }

    @Override
    public void exitIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        Integer value = Integer.valueOf(ctx.INT().getText());
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

