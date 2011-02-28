package project2.level.model;

import com.jme3.math.Vector3f;

public class Text {
    private final String text;
    private final Vector3f position;
    private final Vector3f rotation;
    
    public Text(Vector3f position, Vector3f rotation, String text) {
        this.position = position;
        this.rotation = rotation;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
