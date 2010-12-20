package project2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.model.level.Box;
import project2.triggers.Response;

import com.jme3.scene.Geometry;

public class ChangePositionResponse implements Response {
    private static final Log LOG = LogFactory.getLog(ChangePositionResponse.class);
    private final Geometry geometry;
    private final Box player;

    public ChangePositionResponse(Geometry geometry, Box player) {
        super();
        this.geometry = geometry;
        this.player = player;
    }

    @Override
    public void execute() {
        geometry.setLocalTranslation(player.getLocation());
        player.setChanged(false);
    }

}
