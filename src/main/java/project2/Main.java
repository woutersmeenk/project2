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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.model.level.Box;
import project2.triggers.TriggerManager;
import project2.util.JavaLoggingToCommonLoggingRedirector;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

public class Main extends SimpleApplication implements AnalogListener {
    private static final Log LOG = LogFactory.getLog(Main.class);

    private final TriggerManager triggerManager;
    private final GameStateManager gameStateManager;
    private final ViewManager viewManager;

    public static void main(final String[] args) {
        JavaLoggingToCommonLoggingRedirector.activate();
        final Main app = new Main();
        app.start();
    }

    public Main() {
        triggerManager = new TriggerManager();
        gameStateManager = new GameStateManager();
        viewManager = new ViewManager(rootNode, assetManager);
    }

    @Override
    public void update() {
        // update game logic
        triggerManager.update();

        super.update();
    }

    @Override
    public void simpleInitApp() {
        gameStateManager.buildGameState("simple.xml");

        viewManager.initialize();
        viewManager.createViewFromGameState(gameStateManager.getCurrentState());

        inputManager.addMapping("Action", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addListener(this, "Action", "Left", "Right");
    }

    @Override
    public void onAnalog(String name, float isPressed, float tpf) {
        Vector3f playerPos = gameStateManager.getCurrentState().getPlayer()
                .getLocation();
        Map<Vector3f, Box> levelMap = gameStateManager.getCurrentState()
                .getLevel().getBoxes();

        if (name.equals("Left")) {
            Box beside = levelMap.get(playerPos
                    .subtract(new Vector3f(-1, 0, 0)));
            Box below = levelMap.get(playerPos
                    .subtract(new Vector3f(-1, 0, -1)));

            if (below != null && beside == null) {
                LOG.info("Player moving left...");
                // TODO: update player position
            }
        }
    }
}
