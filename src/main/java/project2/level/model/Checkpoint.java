package project2.level.model;

import com.jme.math.Vector3f;


public class Checkpoint {
    private final long id;
    private final Vector3f location;
    private boolean visited;

    public Checkpoint(final long id, final Vector3f location) {
        this.id = id;
        this.location = location;
        visited = false;
    }

    public long getId() {
        return id;
    }

    public Vector3f getLocation() {
        return location;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(final boolean visited) {
        this.visited = visited;
    }
}
