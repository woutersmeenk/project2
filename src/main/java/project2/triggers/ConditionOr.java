package project2.triggers;

public class ConditionOr implements Condition {
    private final Condition first, second;

    public ConditionOr(final Condition first, final Condition second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isTrue() {
        return first.isTrue() || second.isTrue();
    }
}
