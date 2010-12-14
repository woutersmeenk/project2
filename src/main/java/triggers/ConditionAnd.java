package triggers;

public class ConditionAnd implements Condition {
    private final Condition first, second;

    public ConditionAnd(final Condition first, final Condition second) {
	this.first = first;
	this.second = second;
    }

    @Override
    public boolean isTrue() {
	return first.isTrue() && second.isTrue();
    }
}
