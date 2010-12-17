package project2;

import project2.model.level.Box;
import project2.model.level.Level;

public class GameState {
    /** Current level. */
    private final Level level;
    /** Player. */
    private final Box player;

    public GameState(Level level, Box player) {
	super();
	this.level = level;
	this.player = player;
    }

    public Level getLevel() {
	return level;
    }

    public Box getPlayer() {
	return player;
    }

}
