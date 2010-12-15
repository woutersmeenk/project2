package project2.model.level;

import java.util.List;

import com.jme3.math.Vector3f;

public class Level {
    private final List<Box> boxes;
    private final List<SwitchBox> switches;
    private final Vector3f start;
    private final List<Vector3f> checkpoints;

    public Level(List<Box> boxes, List<SwitchBox> switches, Vector3f start,
            List<Vector3f> checkpoints) {
        this.boxes = boxes;
        this.switches = switches;
        this.start = start;
        this.checkpoints = checkpoints;
    }

    public List<Box> getBoxes() {
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
}
