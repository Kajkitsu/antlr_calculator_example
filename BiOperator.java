
public enum BiOperator {
    TIMES("*", Level.MULTI_DIV),
    PLUS("+", Level.ADD_SUB),
    MINUS("-", Level.ADD_SUB),
    DIV("/", Level.MULTI_DIV),
    POW("^", Level.POW),
    ATOM("atom", Level.ATOM);
    private final String value;
    private final Integer level;

    BiOperator(String value, Integer level) {
        this.value = value;
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public Integer getLevel() {
        return level;
    }

    public BiOperator getNeutralizer() {
        return switch (this) {
            case TIMES -> DIV;
            case PLUS -> MINUS;
            case MINUS -> PLUS;
            case DIV -> TIMES;
            default -> throw new RuntimeException("Not implemented yet");
        };
    }

    public boolean isNonOrdered() {
        return this.equals(BiOperator.PLUS) || this.equals(BiOperator.TIMES);
    }
}
