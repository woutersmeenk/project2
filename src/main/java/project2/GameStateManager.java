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
    /** Player. */
    private Cube player;
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

    public Cube getPlayer() {
        return player;
    }

    public void movePlayer(final Vector3f displacement) {
        final Vector3f newPos = player.getLocation().add(displacement);

        final Checkpoint checkpoint = level.getCheckpoints().get(newPos);

        if (checkpoint != null) {
            checkpoint.setVisited(true);
            viewManager.deleteById(checkpoint.getId());

            LOG.info("Checkpoint reached.");
        }

        player.setLocation(newPos);
        viewManager.onEvent(new LocationEvent(player.getId(), newPos));
    }

    public void buildInitialGameState(final String levelFile) {
        final XMLLevelLoader loader = new XMLLevelLoader();

        level = loader
                .loadLevel(ClassLoader.getSystemResource(levelFile), this);
        player = CubeFactory.getInstance().createCube(level.getStart(), 1);

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
        if (history.size() == 0) {
            return;
        }

        LOG.info("Going back in time...");

        final GameState old = history.get(history.size() - 1);
        currentState = old;

        for (int i = 0; i < old.getSwitchStates().size(); i++) {
            level.getSwitches().get(i)
                    .doSwitch(old.getSwitchStates().get(i), false);
        }

        history.remove(history.size() - 1); // remove it

        viewManager.showHistory(this);
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public List<GameState> getHistory() {
        return history;
    }
}
