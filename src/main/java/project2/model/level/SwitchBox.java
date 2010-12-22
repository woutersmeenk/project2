package project2.model.level;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SwitchBox {
    private static final Log LOG = LogFactory.getLog(SwitchBox.class);
    private final List<List<Box>> states;
    private int currentStateID;
    /**
     * Associated game level.
     */
    private Level level;

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

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Box> getCurrentState() {
        return states.get(currentStateID);
    }

    public void doSwitch() {
        // increment state
        Box[] oldPos = getCurrentState().toArray(new Box[0]);
        currentStateID = (currentStateID + 1) % states.size();
        Box[] newPos = getCurrentState().toArray(new Box[0]);

        // move boxes
        if (level != null) {
            for (int i = 0; i < oldPos.length; i++) {
                level.moveBox(oldPos[i].getLocation(), newPos[i].getLocation());
                LOG.info(oldPos[i].getLocation() + " " + newPos[i].getLocation());
            }

            LOG.info("State switched.");
        } else {
            LOG.error("No level registered to switch.");
        }
    }
}
