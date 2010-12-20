package project2.model.level;

import java.util.List;

public class SwitchBox {
    private final List<List<Box>> states;
    private final int currentStateID;

    public SwitchBox(final List<List<Box>> states) {
        currentStateID = 0;
        this.states = states;
    }

    public List<List<Box>> getStates() {
        return states;
    }

    public int getCurrentStateID() {
        return currentStateID;
    }

    public List<Box> getCurrentState() {
        return states.get(currentStateID);
    }

}
