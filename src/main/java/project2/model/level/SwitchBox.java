package project2.model.level;

public class SwitchBox {
    private final Box[][] states;
    private int currentState;

    public SwitchBox(Box[][] states) {
        currentState = 0;
        this.states = states;
    }

    /**
     * Returns the states. For each box this switch controls there is an array
     * with a boxes for each state of the switch.
     * 
     * @return
     */
    public Box[][] getStates() {
        return states;
    }

    public int getCurrentState() {
        return currentState;
    }

}
