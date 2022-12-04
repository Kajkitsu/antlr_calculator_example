public enum Operator {
    NEGATIVE("neg",Level.ATOM),
    INVERSE("inv",Level.ATOM);
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
            case NEGATIVE -> NEGATIVE;
            case INVERSE -> INVERSE;
            default -> throw new NotImplementedException();
        };
    }
}
