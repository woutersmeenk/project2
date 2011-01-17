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

import java.awt.event.ActionListener;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.level.model.Cube;
import project2.util.JavaLoggingToCommonLoggingRedirector;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * Main entry point for the application. Initializes everything.
 * 
 * @author Ben Ruijl & Wouter Smeenk
 * 
 */
public class Main extends GameApplication implements ActionListener {
    private static final Log LOG = LogFactory.getLog(Main.class);

    private final GameStateManager gameStateManager;
    private final ViewManager viewManager;

    /**
     * Default Constructor
     * */
    public Main() {
        gameStateManager = new GameStateManager();
        viewManager = new ViewManager(rootNode);
    }

    /**
     * main method
     * 
     * @param args
     *            The command line arguments. These are not used currently.
     */
    public static void main(final String[] args) {
        JavaLoggingToCommonLoggingRedirector.activate();
        final Main app = new Main();
        app.start();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void init() {
        viewPort.setBackgroundColor(ColorRGBA.Black);
        cam.setLocation(new Vector3f(1, 1, 10));

        gameStateManager.buildInitialGameState("levels.xml");

        viewManager.initialize(assetManager);
        viewManager.createViewFromGameState(gameStateManager);
        gameStateManager.registerViewManager(viewManager);

        gameStateManager.getLevel().addLocationListener(viewManager);

        for (final PlayerAction action : PlayerAction.values()) {
            inputManager.addMapping(action.toString(), action.trigger);
            inputManager.addListener(this, action.toString());
        }
    }

    @Override
    public void onAction(final String name, final boolean isPressed,
            final float tpf) {
        final PlayerAction action = PlayerAction.valueOf(name);

        switch (action) {
        case LEFT:
            processMoveAction(new Vector3f(-1, 0, 0), isPressed);
            break;
        case UP:
            processMoveAction(new Vector3f(0, 1, 0), isPressed);
            break;
        case RIGHT:
            processMoveAction(new Vector3f(1, 0, 0), isPressed);
            break;
        case DOWN:
            processMoveAction(new Vector3f(0, -1, 0), isPressed);
            break;
        case REVERT:
            if (isPressed) {
                break;
            }
            gameStateManager.revert();
            break;
        case ACTION:
            processSwitch(isPressed);
            break;
        default:
            break;
        }
    }

    private void processSwitch(final boolean isPressed) {
        if (isPressed) {
            return;
        }
        final Vector3f playerPos = gameStateManager.getPlayer().getLocation();
        final Map<Vector3f, Cube> levelMap = gameStateManager.getLevel()
                .getCubes();
        // check if there is a switch below the player
        final Cube below = levelMap.get(playerPos.add(0, 0, -1));
        if (below != null && below.getSwitchCube() != null) {
            below.getSwitchCube().doSwitch();
        }
    }

    private void processMoveAction(final Vector3f direction,
            final boolean isPressed) {
        if (isPressed) {
            return;
        }
        final Vector3f playerPos = gameStateManager.getPlayer().getLocation();
        final Map<Vector3f, Cube> levelMap = gameStateManager.getLevel()
                .getCubes();
        final Cube beside = levelMap.get(playerPos.add(direction));
        final Cube below = levelMap.get(playerPos.add(direction).add(
                new Vector3f(0, 0, -1)));

        if (below != null && beside == null) {
            gameStateManager.movePlayer(direction);
        }
    }
}
