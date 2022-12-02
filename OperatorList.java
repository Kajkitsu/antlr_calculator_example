import java.util.ArrayList;
import java.util.List;

public class OperatorList extends ForwardingList<Operator> {

    public OperatorList(List<Operator> operatorList) {
        super(new ArrayList<>(operatorList));
    }

    public OperatorList() {
        super(new ArrayList<>());
    }

    public static OperatorList of(Operator operator) {
        return new OperatorList(List.of(operator));
    }

    public static OperatorList emptyList() {
        return new OperatorList();
    }

    public static OperatorList of(List<Operator> operatorList) {
        return new OperatorList(operatorList);
    }

    public Integer maxLevel() {
        return this.stream()
                .map(Operator::getLevel)
                .max(Integer::compareTo)
                .orElse(0);
    }
}

