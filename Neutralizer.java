public record Neutralizer(Number number, BiOperator operator) {

    @Override
    public String toString() {
        return "Neutralizer{" +
                "number=" + number +
                ", operator=" + operator +
                '}';
    }
}
