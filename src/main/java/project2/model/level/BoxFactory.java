package project2.model.level;

import com.jme3.math.Vector3f;

public class BoxFactory {
    private static BoxFactory instance;

    public long currentID = 0;

    private BoxFactory() {
    }

    public static BoxFactory getInstance() {
        if (instance == null)
            instance = new BoxFactory();
        return instance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public Box createBox(Vector3f location, int size) {
        return createBox(location, size, null);
    }

    public Box createBox(Vector3f location, int size, SwitchBox switchBox) {
        long id = currentID;
        currentID++;
        return new Box(id, location, size, switchBox);
    }
}
