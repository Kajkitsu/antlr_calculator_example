import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ExpressionList {

    private final ArrayList<Expression> list;

    public ExpressionList(Expression... expressions) {
        this(List.of(expressions));
    }

    public ExpressionList(List<Expression> expressions) {
        list = new ArrayList<>(expressions);
    }


    public int maxLevel() {
        return this.list
                .stream()
                .map(Expression::getLevel)
                .max(Integer::compareTo)
                .orElse(0);
    }


    public int size() {
        return list.size();
    }

    public Optional<Expression> get(int i) {
        if (size() > i) {
            return Optional.of(list.get(i));
        }
        return Optional.empty();
    }

    public Optional<Variable> get(Variable variable) {
        return this.list.stream()
                .filter(it -> it.equals(variable))
                .findFirst()
                .map(it -> (Variable) it);
    }


    public ExpressionList without(Expression expression) {
        var newList = new ArrayList<>(list);
        if(!newList.remove(expression)){
            throw new IllegalArgumentException();
        }
        return new ExpressionList(newList);
    }

    public ExpressionList with(Expression expression) {
        var newList = new ArrayList<>(list);
        newList.add(expression);
        return new ExpressionList(newList);
    }

    public List<Number> getNumbers() {
        return this.list.stream()
                .filter(it -> it instanceof Number)
                .map(it -> (Number) it)
                .toList();

    }

    public ExpressionList withoutNumber() {
        return new ExpressionList(
                this.list.stream()
                        .filter(it -> !(it instanceof Number))
                        .toList());
    }

    public boolean contains(Expression expression) {
        return this.list.contains(expression);
    }

    public ExpressionList flatter() {
        if (this.size() == 1) {
            return new ExpressionList(this.list.get(0));
        }
        return new ExpressionList(this.list);
    }

    public Stream<Expression> stream() {
        return list.stream();
    }

    public boolean containsNumbers() {
        return this.list.stream()
                .anyMatch(it -> it instanceof Number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionList that = (ExpressionList) o;

        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return list != null ? list.hashCode() : 0;
    }
}
