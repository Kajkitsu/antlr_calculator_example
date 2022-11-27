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

    public static Integer calc(CharStream charStream) {
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

    public static Integer calc(String expression) {
        return calc(CharStreams.fromString(expression));
    }

    public static void main(String[] args) throws Exception {
        CharStream charStreams = CharStreams.fromFileName("./example.txt");
        Integer result = calc(charStreams);
        System.out.println("Result = " + result);
    }
}
```
Linia `walker.walk(calculatorListener, tree);` jest kluczowa w metodzie `calc()` ze względu na to, że po jej wykonaniu  `ParseTreeWalker` przechodzi 
przez wszystkie węzły w drzewie syntaktycznym,  i wykonuje metody nadpisane przez klasę Calculator, konkretnie te, 
które są rozszerzane z klasy CalculatorBaseListener (została ona wygenerowana przez antlr-a w kroku nr 4 w poprzedniej sekcji)
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
    private final LinkedList<Integer> firstStack = new LinkedList<>();
    private final LinkedList<Integer> secondStack = new LinkedList<>();


    public Integer getResult() {
        return stack.pop();
    }


    @Override
    public void exitIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        int value = Integer.parseInt(ctx.INT().getText());
        if (ctx.MINUS() != null) {
            firstStack.push(-1 * value);
        } else {
            firstStack.push(value);
        }
        System.out.println("IntegralExpression: \"" + ctx.getText() + "\" ");
    }


```
Kalkulator w trakcie swojego działania będzie korzystał ze struktury listy do przechowywania liczb, 
na których będą wykonywane działania matematyczne. W powyższym przykładzie dodano cała implementacje
węzła _IntegralExpression_. Tekst, który pasuje do tokena _INT_, jest rzutowany na typ całkowitoliczbowy.
Następnie w zależności czy w wyrażeniu znajduje się token _MINUS_, na liste trafia liczba przeciwna, w 
przeciwnym wypadku bez modyfikacji.

Na _firstStack_ trafiają liczby na których będą wykonywane operacje mnożenia i dzielenia, a na _secondStack_ liczby na których będą wykonywane operacje dodawania i odejmowania.
4. Operacja mnożenia i dzielenia:
```java
    @Override
    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        Integer result = firstStack.removeLast();
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

    private boolean symbolEquals(ParseTree child, int symbol) {
        return ((TerminalNode) child).getSymbol().getType() == symbol;
    }
```
Powyżej zaprezentowano implementacje _exitMultiplyingExpression_.
Pętla for iteruje po kolejnych symbolach mnożenia i dzielenie w wyrażeniu.
Jeżeli w wyrażeniu nie znajduje się żaden symbol (w przykładzie jest to np. pierwsza lewa gałąź w drzewie)
pętla nie wykona się ani razu i na druga liste trafa ta sama liczba, która została pobrana. 
Metoda _symbolEquals_ służy do sprawdzania jakiego typu jest obsługiwany węzeł, w zależności od tego wykonywana
jest instrukcja warunkowa, która realizuje operacje mnożenia lub dzielenia i zapisuje wynik do zmiennej _result_.
Na koniec wynik całego wyrażenia trafia na druga liste _secondStack_.
5. Operacja dodawania i odejmowania:
```java
    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        Integer result = secondStack.removeLast();
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
```
Metody _exitExpression_ i _exitMultiplyingExpression_ róznią sie minimalanie.

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