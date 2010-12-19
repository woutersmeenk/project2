package project2;

import project2.triggers.Condition;

import com.jme3.renderer.Camera;

public abstract class CameraCondition implements Condition {
    private final Camera camera;

    public CameraCondition(final Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }
}
