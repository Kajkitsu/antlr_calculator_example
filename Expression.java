import com.sun.nio.file.ExtendedOpenOption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Expression {
    abstract Expression concat(Expression that) ;

    private final BiOperator operator;

    private final ExpressionList expressionList;

    Expression(BiOperator operator, ExpressionList expressionList) {
        if(operator.equals(BiOperator.ATOM)) {
            this.operator = BiOperator.ATOM;
            this.expressionList = null;
            return;
        }
        if (expressionList.size() <= 1) {
            throw new IllegalArgumentException("ExpressionList is not valid!");
        }
        if(operator.isNonOrdered()) {
            for (Expression child: expressionList.stream().toList()) {
                 if(child.operator.equals(operator)) {
                     throw new IllegalArgumentException("Child contains unordered operator same as in parent");
                 }
            }
        }
        this.operator = operator;
        this.expressionList = expressionList;
    }

    public final BiOperator getOperator() {
        return operator;
    }

    public ExpressionList getExpressions() {
        return expressionList;
    }

    public Integer getLevel() {
        return operator.getLevel();
    }

    private boolean isAtom() {
        return this.getLevel().equals(Level.ATOM);
    }


    public Expression minus(Expression that) {
        if(this instanceof MinusExpression) {
            return this.concat(that);
        }
        if(this instanceof Number && that instanceof Number) {
            return ((Number) this).minus((Number)that);
        }
        return new MinusExpression(new ExpressionList(this, that));
    }

    public Expression times(Expression that) {
        if(this instanceof TimesExpression) {
            return this.concat(that);
        }
        if(that instanceof TimesExpression) {
            return that.concat(this);
        }
        if(this instanceof Number && that instanceof Number) {
            return ((Number) this).times((Number)that);
        }
        return new TimesExpression(new ExpressionList(this, that));
    }

    public Expression plus(Expression that) {
        if(this instanceof PlusExpression) {
            return this.concat(that);
        }
        if(that instanceof PlusExpression) {
            return that.concat(this);
        }
        if(this instanceof Number && that instanceof Number) {
            return ((Number) this).plus((Number)that);
        }
        return new PlusExpression(new ExpressionList(this, that));
    }

    public Expression div(Expression that) {
        if(this instanceof DivExpression) {
            return this.concat(that);
        }
        if(this instanceof Number && that instanceof Number) {
            return ((Number) this).div((Number)that);
        }
        return new DivExpression(new ExpressionList(this, that));
    }

    public Expression pow(Expression that) { //TODO
        if(this instanceof Number && that instanceof Number) {
            return ((Number) this).pow((Number)that);
        }
        return null;
    }


    public Expression negative() {
        return Number.MINUS_ONE.times(this);
    }

    public Expression inverse() {
        return Number.ONE.div(this);
    }



    @Override
    public String toString() {
        return expressionList.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(operator.getValue().toString(), "(", ")"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expression that = (Expression) o;

        if (operator != that.operator) return false;
        if(operator.equals(BiOperator.TIMES) || operator.equals(BiOperator.PLUS)){
            return Objects.equals(getExpressions().stream().sorted(Comparator.comparing(Expression::hashCode)).toList(),
                    that.getExpressions().stream().sorted(Comparator.comparing(Expression::hashCode)).toList());
        } else {
            return Objects.equals(getExpressions(), that.getExpressions());
        }
    }

    @Override
    public int hashCode() {
        int result = operator != null ? operator.hashCode() : 0;
        result = 31 * result + (expressionList != null ? expressionList.hashCode() : 0);
        return result;
    }

    public Optional<Neutralizer> getNeutralizer() {
        return this.getExpressions()
                .getNumbers()
                .stream()
                .findFirst()
                .map(it -> new Neutralizer(it, getOperator().getNeutralizer()));
    }

}
