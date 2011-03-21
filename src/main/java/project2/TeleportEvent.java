package project2;

import project2.level.model.Cube;

public class TeleportEvent {
    private final Cube teleporter;
    
    public TeleportEvent(Cube teleporter) {
        this.teleporter = teleporter;
    }
    
    public Cube getTeleporter() {
        return teleporter;
    }
}
