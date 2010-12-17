package project2.model.level;

import java.util.List;
import java.util.Map;

import com.jme3.math.Vector3f;

public class Level {
    private final Map<Vector3f, Box> boxes;
    private final List<SwitchBox> switches;
    private final Vector3f start;
    private final List<Vector3f> checkpoints;

    public Level(Map<Vector3f, Box> boxes, List<SwitchBox> switches, Vector3f start,
	    List<Vector3f> checkpoints) {
	this.boxes = boxes;
	this.switches = switches;
	this.start = start;
	this.checkpoints = checkpoints;
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
}
