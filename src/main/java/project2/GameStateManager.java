/*  Copyright 2010 Ben Ruijl, Wouter Smeenk

This file is part of project2

project2 is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3, or (at your option)
any later version.

project2 is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with project2; see the file LICENSE.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

 */
package project2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.level.Level;
import project2.level.XMLLevelLoader;
import project2.level.model.Checkpoint;
import project2.level.model.Cube;
import project2.level.model.CubeFactory;

import com.jme3.math.Vector3f;

public class GameStateManager {
    private static final Log LOG = LogFactory.getLog(GameStateManager.class);

    private GameState currentState;
    private final List<GameState> history;
    /** Current level. */
    private Level level;
    /** Index of current level. */
    private int levelIndex;
    /** Level list. */
    private List<Level> levelSet;
    /** Player. */
    private Player player;
    private ViewManager viewManager;

    public GameStateManager() {
        history = new ArrayList<GameState>();
    }

    public void registerViewManager(final ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public Level getLevel() {
        return level;
    }

    public List<Level> getLevelSet() {
        return levelSet;
    }

    public Player getPlayer() {
        return player;
    }

    public void update() {
        /* Get the location below the player. */
        Vector3f currentGridPos = level.roundToGridPoint(player
                .getWorldLocation());
        final Cube below = level.getCubes().get(
                currentGridPos.add(new Vector3f(0, 0, -1)));

        /* Fall! */
        if (below == null) {
            player.setWorldLocation(player.getWorldLocation()
                    .add(0, 0, -0.005f));
            player.setFalling(true);
            viewManager.onEvent(new LocationEvent(player.getModel().getId(),
                    player.getWorldLocation()));

            /* Does the player fall off the map? */
            if (player.getWorldLocation().getZ() < getLevel().getMaxFall()) {
                LOG.info("Player died.");
                reset();
            }
        } else {
            player.getModel().setLocation(currentGridPos);
            player.setWorldLocation(currentGridPos);
            player.setFalling(false);
        }
    }

    /**
     * Resets the gamestate to the start.
     */
    // FIXME: this will also revert changes in completed levels
    public void reset() {
        if (history.size() > 0) {
            revertTo(0);
        }

        for (Checkpoint cp : level.getCheckpoints().values()) {
            if (cp.isVisited()) {
                viewManager.addCheckpoint(cp);
                cp.setVisited(false);
            }
        }

        movePlayer(level.getStart().subtract(player.getModel().getLocation()));
    }

    public void movePlayer(final Vector3f displacement) {
        final Vector3f newPos = player.getModel().getLocation()
                .add(displacement);

        final Checkpoint checkpoint = level.getCheckpoints().get(newPos);

        if (checkpoint != null && !checkpoint.isVisited()) {
            checkpoint.setVisited(true);
            viewManager.deleteById(checkpoint.getId());

            LOG.info("Checkpoint reached.");
        }

        // check if done
        if (level.getEnd().equals(newPos) && level.allCheckpointsVisited()) {
            LOG.info("Level finished!");

            if (level.getEndSwitch() != null) {
                level.getEndSwitch().doSwitch(1, false); // switch up
            }

            if (levelIndex + 1 < levelSet.size()) {
                levelIndex++;
                final Level newLevel = levelSet.get(levelIndex);
                newLevel.merge(level); // merge so you can go back
                level = newLevel;
            }

            currentState = new GameState(level);
            history.clear();
        }

        player.getModel().setLocation(newPos);
        player.setWorldLocation(newPos); // set the world location too!
        player.setFalling(false);
        viewManager
                .onEvent(new LocationEvent(player.getModel().getId(), newPos));

    }

    public void buildInitialGameState(final String levelFile) {
        final XMLLevelLoader loader = new XMLLevelLoader();

        levelSet = loader.loadLevelSet(
                ClassLoader.getSystemResource(levelFile), this);

        if (levelSet.size() == 0) {
            LOG.info("Could not load level 0 from the levelset.");
            return;
        }

        levelIndex = 0;
        level = levelSet.get(levelIndex);

        player = new Player(CubeFactory.getInstance().createCube(
                level.getStart(), 1, false));

        // generate a gamestate from the level
        currentState = new GameState(level);
        history.clear();
    }

    /**
     * Saves a state to history and creates a new state. This function should be
     * called every time a switchCube state is switched.
     */
    public void makeHistory() {
        history.add(currentState);
        currentState = new GameState(level);

        viewManager.showHistory(this);
    }

    /**
     * Goes one step back into history.
     */
    public void revert() {
        if (history.size() > 0) {
            revertTo(history.size() - 1);
        }
    }

    public void revertTo(int index) {
        if (index < 0 || index >= history.size()) {
            LOG.info("Error: " + index + " is not within history bounds (0/"
                    + history.size() + ")");
            return;
        }

        LOG.info("Going back in time...");

        final GameState old = history.get(index);
        currentState = old;

        for (int i = 0; i < old.getSwitchStates().size(); i++) {
            level.getSwitches().get(i)
                    .doSwitch(old.getSwitchStates().get(i), false);
        }

        for (int i = history.size() - 1; i >= index; i--) {
            history.remove(i);
        }

        viewManager.showHistory(this);
    }

    public void forwardToLevel(int index) {
        final Level newLevel = levelSet.get(index);
        newLevel.merge(level); // merge so you can go back
        level = newLevel;
        currentState = new GameState(level);
        history.clear();
        reset();

        // open up the level, FIXME: not working yet
        if (index > 0) {
            // level.merge(levelSet.get(index - 1));
            if (levelSet.get(index - 1).getEndSwitch() != null) {
                levelSet.get(index - 1).getEndSwitch().doSwitch(1, false);
            }
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public List<GameState> getHistory() {
        return history;
    }
}
