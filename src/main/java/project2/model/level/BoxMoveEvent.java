package project2.model.level;

import com.jme3.math.Vector3f;

public class BoxMoveEvent {
    private final long id;
    private final Vector3f newPos;

    BoxMoveEvent(long id, Vector3f newPos) {
        this.id = id;
        this.newPos = newPos;
    }

    public long getId() {
        return id;
    }

    public Vector3f getNewPos() {
        return newPos;
    }

}
