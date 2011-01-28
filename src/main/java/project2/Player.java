package project2;

import project2.level.model.Cube;

import com.jme3.math.Vector3f;

public class Player {
    private final Cube model;
    private boolean isFalling;
    /** Location in the world instead of the grid position. */
    private Vector3f worldLocation;

    public Player(final Cube model) {
        this.model = model;

        isFalling = false;
        worldLocation = model.getLocation();
    }

    public Cube getModel() {
        return model;
    }

    public Vector3f getWorldLocation() {
        return worldLocation;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public void setFalling(boolean isFalling) {
        this.isFalling = isFalling;
    }

    public void setWorldLocation(Vector3f worldLocation) {
        this.worldLocation = worldLocation;
    }

}
