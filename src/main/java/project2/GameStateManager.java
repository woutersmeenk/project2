package project2;

import java.util.List;

import project2.level.XMLLevelLoader;
import project2.model.level.Box;
import project2.model.level.Level;

public class GameStateManager {
    GameState currentState;
    List<GameState> history;

    public void buildGameState(final String levelFile) {
        final XMLLevelLoader loader = new XMLLevelLoader();

        final Level level = loader.loadLevel(ClassLoader
                .getSystemResource(levelFile));
        final Box player = new Box(level.getStart(), 1);

        currentState = new GameState(level, player);
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
