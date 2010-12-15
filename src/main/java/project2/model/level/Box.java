package project2.model.level;

import com.jme3.math.Vector3f;


public class Box {
    private final Vector3f location;
    private final int size;
    private final SwitchBox switchBox;

    public Box(Vector3f location, int size) {
        this(location, size, null);
    }

    public Box(Vector3f location2, int size, SwitchBox switchBox) {
        this.location = location2;
        this.size = size;
        this.switchBox = switchBox;
    }

    public Vector3f getLocation() {
        return location;
    }

    public int getSize() {
        return size;
    }

    public SwitchBox getSwitchBox() {
        return switchBox;
    }
}
