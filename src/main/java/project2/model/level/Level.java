package project2.model.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.EventListener;

import com.jme3.math.Vector3f;

public class Level {
    private static final Log LOG = LogFactory.getLog(Level.class);

    private final Map<Vector3f, Box> boxes;
    private final List<SwitchBox> switches;
    private final Vector3f start;
    private final List<Vector3f> checkpoints;

    private final List<EventListener<BoxMoveEvent>> boxMoveListeners;

    public Level(final Map<Vector3f, Box> boxes,
            final List<SwitchBox> switches, final Vector3f start,
            final List<Vector3f> checkpoints) {
        this.boxes = boxes;
        this.switches = switches;
        this.start = start;
        this.checkpoints = checkpoints;

        boxMoveListeners = new ArrayList<EventListener<BoxMoveEvent>>();

        // register the level with the switches
        for (SwitchBox switchBox : switches) {
            switchBox.setLevel(this);
        }
    }

    public Map<Vector3f, Box> getBoxes() {
        return boxes;
    }

    public List<SwitchBox> getSwitches() {
        return switches;
    }

    public Vector3f getStart() {
        return start;
    }

    public List<Vector3f> getCheckpoints() {
        return checkpoints;
    }

    public Box boxFromId(long id) {
        for (Box box : boxes.values()) {
            if (id == box.getId()) {
                return box;
            }
        }

        return null;
    }

    // TODO: maybe do with id instead of current position?
    public void moveBox(Vector3f curPos, Vector3f newPos) {
        // move the box
        Box box = boxes.get(curPos);

        if (box == null) {
            LOG.warn("No box found at" + curPos);
            return;
        }

        // replace the box in the map and set the new position
        boxes.put(newPos, boxes.remove(curPos));
        box.setLocation(newPos);

        for (EventListener<BoxMoveEvent> listener : boxMoveListeners) {
            listener.onEvent(new BoxMoveEvent(box.getId(), newPos));
        }
    }

    public boolean addBoxMoveListener(EventListener<BoxMoveEvent> listener) {
        return boxMoveListeners.add(listener);
    }
}
