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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.util.JavaLoggingToCommonLoggingRedirector;
import triggers.Condition;
import triggers.ConditionAnd;
import triggers.TriggerManager;

import com.jme3.app.SimpleApplication;

public class Main extends SimpleApplication {
    private static final Log LOG = LogFactory.getLog(Main.class);

    TriggerManager triggerManager;

    public static void main(final String[] args) {
        JavaLoggingToCommonLoggingRedirector.activate();
        final Main app = new Main();
        app.start();
    }

    public Main() {
        triggerManager = new TriggerManager();
    }

    @Override
    public void update() {
        // update game logic
        triggerManager.update();

        super.update();
    }

    @Override
    public void simpleInitApp() {
        // create a custom condition
        final Condition condition = new ConditionAnd(new Condition() {

            @Override
            public boolean isTrue() {
                return getCamera().getDirection().x > 0;
            }
        }, new Condition() {

            @Override
            public boolean isTrue() {
                return getCamera().getDirection().y > 0;
            }
        });

        // triggerManager.addTrigger(new Trigger(condition, new DebugResponse(
        // "You are looking the the positive x and y direction!")));

        final GameStateManager gameStateManager = new GameStateManager();
        gameStateManager.buildGameState("simple.xml");

        final ViewManager viewManager = new ViewManager(rootNode, assetManager);
        viewManager.initialize();
        viewManager.createViewFromGameState(gameStateManager.getCurrentState());
    }
}
