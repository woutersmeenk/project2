package project2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.level.Level;
import project2.level.model.Cube;

public class Menu implements EventListener<LocationEvent> {
    private static final Log LOG = LogFactory.getLog(Menu.class);

    private final GameStateManager gameStateManager;
    private int selectedLevel = 0;
    private int levelSwitchInitialPos;
    private Cube levelTeleporter;

    public Menu(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
    }

    @Override
    public void onEvent(LocationEvent event) {
        LOG.info(event.getNewPos() + ": " + event.getName());
        if (event.getName().equals("level")) {
            // Switch moves in the X direction. Skip the menu
            selectedLevel = (int) event.getNewPos().getX()
                    - levelSwitchInitialPos + 1;
            Level level = gameStateManager.getLevelSet().get(selectedLevel);
            levelTeleporter.setTeleportDestination(level.getStart());
        }
    }

    public void onEvent(TeleportEvent teleportEvent) {
        if (teleportEvent.getTeleporter().getName().equals("levelTeleporter")) {
            // function bugs the teleport for some reason
            gameStateManager.forwardToLevel(selectedLevel);
        }
    }

    public void initialize() {
        gameStateManager.getLevel().addLocationListener(this);
        for (Cube cube : gameStateManager.getLevel().getCubes().values()) {
            if (cube.getName().equals("level")) {
                levelSwitchInitialPos = (int) cube.getLocation().getX();
            } else if (cube.getName().equals("levelTeleporter")) {
                levelTeleporter = cube;
            }
        }
    }
}
