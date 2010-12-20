package project2.model.level;

import com.jme3.math.Vector3f;

public class Box {
    private Vector3f location;
    private final int size;
    private final SwitchBox switchBox;

    private boolean changed = false;

    public Box(final Vector3f location, final int size) {
        this(location, size, null);
    }

    public Box(final Vector3f location2, final int size,
            final SwitchBox switchBox) {
        location = location2;
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

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setLocation(Vector3f location) {
        changed = true;
        this.location = location;
    }
}
