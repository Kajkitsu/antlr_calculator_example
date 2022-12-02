

public class Atom {
    public static final Atom PI = Atom.from(Math.PI);
    public static final Atom E = Atom.from(Math.E);
    public static final String TIMES = "*";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String DIV = "/";
    public static final String POW = "^";
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";

    private final Double value;
    private final String expression;

    public Atom(Double value, String expression) {
        this.value = value;
        this.expression = expression;
    }

    public static Atom from(double value) {
        return new Atom(value, null);
    }

    public static Atom from(String variable) {
        return new Atom(null, variable);
    }

    public Atom times(Atom operand) {
        if (this.expression != null || operand.expression != null) {
            return Atom.from(this + TIMES + operand);
        }
        return Atom.from(this.value * operand.value);
    }

    public Atom div(Atom operand) {
        if (this.expression != null || operand.expression != null) {
            return Atom.from(this + DIV + operand);
        }
        return Atom.from(this.value / operand.value);
    }

    public Atom plus(Atom operand) {
        if (this.expression != null || operand.expression != null) {
            return Atom.from(this + PLUS + operand);
        }
        return Atom.from(this.value + operand.value);
    }

    public Atom minus(Atom operand) {
        if (this.expression != null || operand.expression != null) {
            return Atom.from(this + MINUS + operand);
        }
        return Atom.from(this.value - operand.value);
    }

    public Atom pov(Atom operand) {
        if (this.expression != null || operand.expression != null) {
            return Atom.from(this + POW + LPAREN + operand + RPAREN);
        }
        return Atom.from(Math.pow(this.value, operand.value));
    }

    public Atom negative() {
        if (this.expression != null) {
            return Atom.from(MINUS + LPAREN + this + RPAREN);
        }
        return Atom.from(-1 * this.value);
    }

    public Atom acos() {
        if (this.expression != null) {
            return Atom.from("acos" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.acos(this.value));
    }

    public Atom asin() {
        if (this.expression != null) {
            return Atom.from("asin" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.asin(this.value));
    }

    public Atom atan() {
        if (this.expression != null) {
            return Atom.from("atan" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.atan(this.value));
    }

    public Atom cos() {
        if (this.expression != null) {
            return Atom.from("cos" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.cos(this.value));
    }

    public Atom sin() {
        if (this.expression != null) {
            return Atom.from("sin" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.sin(this.value));
    }

    public Atom tan() {
        if (this.expression != null) {
            return Atom.from("tan" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.tan(this.value));
    }

    public Atom sqrt() {
        if (this.expression != null) {
            return Atom.from("sqrt" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.sqrt(this.value));
    }

    public Atom log() {
        if (this.expression != null) {
            return Atom.from("log" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.log(this.value));
    }

    public Atom log10() {
        if (this.expression != null) {
            return Atom.from("log10" + LPAREN + this + RPAREN);
        }
        return Atom.from(Math.log(this.value));
    }

    @Override
    public String toString() {
        if (this.value != null) {
            return this.value.toString();
        }
        return this.expression;
    }
}
