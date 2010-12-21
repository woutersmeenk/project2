package project2.model.level;

import com.jme3.math.Vector3f;

public class Box {
    private Vector3f location;
    private final int size;
    private final SwitchBox switchBox;
    private final long id;

    private boolean changed = false;

    Box(final long id, final Vector3f location2, final int size,
            final SwitchBox switchBox) {
        location = location2;
        this.id = id;
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
