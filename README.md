### Przygotowanie środowiska
1. Pobranie jar-a z antrl:
`wget https://www.antlr.org/download/antlr-4.11.1-complete.jar`
2. Utworzenie gramatyki kalkulatora: `nano Calcualtor.g4`
```
grammar Calculator;


expression: multiplyingExpression ((PLUS | MINUS) multiplyingExpression)*;
multiplyingExpression: integralExpression ((TIMES | DIV) integralExpression)*;
integralExpression: MINUS INT | INT;


INT: [0-9]+ ;
DOT: '.';
TIMES: '*' ;
DIV: '/' ;
PLUS: '+' ;
MINUS: '-' ;
INTEGRAL: 'cal';
WS : [ \t\r\n]+ -> skip ;
```
3. Utworzenie przykładowych danych wejściowych do kalkulatora: `nano example.txt`
```
1 - 3 * 4 / 2 - 2 + 3 * 4 / 4
```
4. Wygenerowanie klas Lexera i Parsera dla Kalkulatora: `java -jar ./antlr-4.11.1-complete.jar Calculator.g4`
5. Kompilacja klas ```javac -cp ./antlr-4.11.1-complete.jar Calculator*.java```
5. Sprawdzenie poprawności gramatyki poprzez wygenerowanie drzewa syntaktycznego dla przykładowych danych wejściowych: `java -cp .:antlr-4.11.1-complete.jar org.antlr.v4.gui.TestRig Calculator expression -tree -gui example.txt`
![](assets/antlr4_parse_tree.png)


### Praca w IDE
1. Utworzenie klasy _Calculator_ rozszerzającej _CalculatorBaseListener_, z której nadpisane metody będą służyły do wyliczenia wartości z pliku, który zostanie podany na wejściu.
```java
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Calculator extends CalculatorBaseListener {

    public Integer getResult() {
        return 0;
    }

    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        System.out.println("Expression: \"" + ctx.getText() + "\" ");
    }


    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        System.out.println("MultiplyingExpression: \"" + ctx.getText() + "\" ");
    }

    @Override
    public void exitIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        System.out.println("IntegralExpression: \"" + ctx.getText() + "\" ");
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
```
Linia `walker.walk(calculatorListener, tree);` jest kluczowa w metodzie `main()` ze względu na to, że po jej wykonaniu  `ParseTreeWalker` przechodzi przez wszystkie węzły w drzewie syntaktycznym,  i wykonuje metody nadpisane przez klasę Calculator, konkretnie te ,które są rozszerzane z klasy CalculatorBaseListener (została ona wygenerowana przez antlr-a w kroku nr 4 w poprzedniej sekcji)
Do zrealizowania pełnej funkcjonalności kalkulatora należy nadpisać metody: `exitExpression`, `exitMultiplyingExpression`, `exitIntegralExpression`.
W zaprezentowanej implementacji `ParseTreeWalker` po wyjściu z każdego węzła wypisze w konsoli jego reprezentację tekstow.
Pozwala to zobrazować, w jakiej kolejności są odwiedzane kolejne węzły. 
Zwracany wynik przez kalkulator jest zmockowaną wartością.
Wykonanie tego programu zaprezentuje następujący wynik w konsoli:
```
IntegralExpression: "1" 
MultiplyingExpression: "1" 
IntegralExpression: "3" 
IntegralExpression: "4" 1
IntegralExpression: "2" 
MultiplyingExpression: "3*4/2" 
IntegralExpression: "2" 
MultiplyingExpression: "2" 
IntegralExpression: "3" 
IntegralExpression: "4" 
IntegralExpression: "4" 
MultiplyingExpression: "3*4/4" 
Expression: "1-3*4/2-2+3*4/4" 
Result = 0

Process finished with exit code 0
```
2. Korzystanie z debuggera Intelij.
Przydatne linki:
- [Uruchomienie debuggera](https://www.jetbrains.com/help/idea/starting-the-debugger-session.html)
- [Breakpoints](https://www.jetbrains.com/help/idea/using-breakpoints.html)
- [Ewaluacja wyrażeń](https://www.jetbrains.com/help/idea/examining-suspended-program.html#evaluating-expressions)
3. Zdejmowanie i ładowanie liczb ze stosu.
```java
    private final Stack<Integer> stack = new Stack<>();

    public Integer getResult() {
        return stack.pop();
    }


    @Override
    public void exitIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        int value = Integer.parseInt(ctx.INT().getText());
        if (ctx.MINUS() != null) {
            stack.push(-1 * value);
        } else {
            stack.push(value);
        }
        System.out.println("IntegralExpression: \"" + ctx.getText() + "\" ");
    }

```
Kalkulator w trakcie swojego działania będzie korzystał ze struktury stosu do przechowywania liczb, 
na których będą wykonywane działania matematyczne. W powyższym przykładzie dodano cała implementacje
węzła _IntegralExpression_. Tekst, który pasuje do tokena _INT_, jest rzutowany na typ całkowitoliczbowy.
Następnie w zależności czy w wyrażeniu znajduje się token _MINUS_, na stos trafia liczba przeciwna, w 
przeciwnym wypadku bez modyfikacji.
4. Operacja mnożenia i dzielenia:
```java
    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        Integer result = stack.pop();
        for (int i = ctx.getChildCount() - 2; i >= 1; i = i - 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.TIMES)) {
                result = stack.pop() * result;
            } else {
                result = stack.pop() / result;
            }
        }
        stack.push(result);
        System.out.println("MultiplyingExpression: \"" + ctx.getText() + "\" -> "+result);
    }

    private boolean symbolEquals(ParseTree child, int symbol) {
        return ((TerminalNode) child).getSymbol().getType() == symbol;
    }
```
Powyżej zaprezentowano implementacje _exitMultiplyingExpression_.
Pętla for iteruje po kolejnych symbolach mnożenia i dzielenie w wyrażeniu.
Jeżeli w wyrażeniu nie znajduje się żaden symbol (w przykładzie jest to np. pierwsza lewa gałąź w drzewie)
pętla nie wykona się ani razu i na stos trafi ta sama liczba, która została pobrana.
Metoda _symbolEquals_ służy do sprawdzania jakiego typu jest obsługiwany węzeł, w zależności od tego wykonywana
jest instrukcja warunkowa, która realizuje operacje mnożenia lub dzielenia i zapisuje wynik do zmiennej _result_.
Na koniec wynik całego wyrażenia trafia na stos.
5. Operacja dodawania i odejmowania:
```java
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
        System.out.println("Expression: \"" + ctx.getText() + "\" -> "+result);
    }
```
Główna różnica między _exitExpression_, a _exitMultiplyingExpression_ znajduje się w pętli for.
Ze względu na to, w jaki sposób na stos trafiają i są zdejmowane kolejne wartości. Pętla _for_ iteruje w przeciwnym kierunku.
Również strony wykonywania działania są w związku z tym odwrócone.
Skorzystanie z innej struktury, która pozwala na dodawanie i zdejmowanie wartości w dowolny sposób np. _LinkedList_
pozwoliłoby iterowanie w pętli _for_ w taki sam sposób jak w _exitMultiplyingExpression_.

6. Wynik działania.
```
IntegralExpression: "1" 
MultiplyingExpression: "1" -> 1
IntegralExpression: "3" 
IntegralExpression: "4" 
IntegralExpression: "2" 
MultiplyingExpression: "3*4/2" -> 6
IntegralExpression: "2" 
MultiplyingExpression: "2" -> 2
IntegralExpression: "3" 
IntegralExpression: "4" 
IntegralExpression: "4" 
MultiplyingExpression: "3*4/4" -> 3
Expression: "1-3*4/2-2+3*4/4" -> -4
Result = -4

Process finished with exit code 0
```