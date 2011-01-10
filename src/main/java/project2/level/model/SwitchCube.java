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
package project2.level.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.GameStateManager;
import project2.level.Level;

public class SwitchCube {
    private static final Log LOG = LogFactory.getLog(SwitchCube.class);
    private final List<List<Cube>> states;
    private int currentStateID;
    private final boolean loop;

    private final GameStateManager gameStateManager;

    /**
     * Associated game level.
     */
    private Level level;

    public SwitchCube(final GameStateManager gameStateManager,
            final List<List<Cube>> states, final boolean loop) {
        currentStateID = 0;
        this.states = states;
        this.loop = loop;
        this.gameStateManager = gameStateManager;
    }

    public List<List<Cube>> getStates() {
        return states;
    }

    public int getCurrentStateID() {
        return currentStateID;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    public List<Cube> getCurrentState() {
        return states.get(currentStateID);
    }

    public void doSwitch(final int id, final boolean addToHistory) {
        if (level == null) {
            LOG.error("No level registered to switch.");
            return;
        }

        // increment state
        final Cube[] oldPos = getCurrentState().toArray(new Cube[0]);
        currentStateID = id;
        final Cube[] newPos = getCurrentState().toArray(new Cube[0]);

        // move boxes
        for (int i = 0; i < oldPos.length; i++) {
            level.moveBox(oldPos[i].getLocation(), newPos[i].getLocation());
        }

        if (addToHistory) {
            gameStateManager.makeHistory(); // register the action
        }
    }

    public void doSwitch() {
        int newId = currentStateID;

        if (loop) {
            newId = (currentStateID + 1) % states.size();
        } else {
            if (newId + 1 < states.size()) {
                newId++;
            }
        }

        if (newId != currentStateID) {
            doSwitch(newId, true);
        }
    }
}
