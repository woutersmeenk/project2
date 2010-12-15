package project2.model.level;

import java.util.List;

public class SwitchBox {
    private final List<List<Box>> states;
    private int currentState;

    public SwitchBox(List<List<Box>> states) {
        currentState = 0;
        this.states = states;
    }

    public List<List<Box>> getStates() {
        return states;
    }

    public int getCurrentState() {
        return currentState;
    }

}
