
public enum Operator {
    TIMES("*", Level.MULTI_DIV),
    PLUS("+", Level.ADD_SUB),
    MINUS("-", Level.ADD_SUB),
    DIV("/", Level.MULTI_DIV),
    POW("^", Level.POW);
    private final String value;
    private final Integer level;

    Operator(String value, Integer level) {
        this.value = value;
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public Integer getLevel() {
        return level;
    }

    public Operator getNeutralizer() {
        return switch (this) {
            case TIMES -> DIV;
            case PLUS -> MINUS;
            case MINUS -> PLUS;
            case DIV -> TIMES;
            default -> throw new RuntimeException("Not implemented yet");
        };
    }
}
