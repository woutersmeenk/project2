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
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.level.Level;
import project2.level.model.SwitchCube;

import com.jme3.math.Vector3f;

public class GameState {
    private static final Log LOG = LogFactory.getLog(GameState.class);
    
    private final List<Integer> switchStates;
    private final List<Vector3f> playerMoves;
    private int moveIndex;

    GameState(final Level level) {
        switchStates = new ArrayList<Integer>(level.getSwitches().size());
        playerMoves = new ArrayList<Vector3f>();

        // save all the states
        for (final SwitchCube switchCube : level.getSwitches()) {
            switchStates.add(switchCube.getCurrentStateID());
        }

        moveIndex = 0;
    }

    public List<Integer> getSwitchStates() {
        return switchStates;
    }

    public void addPlayerMove(Vector3f position) {
        playerMoves.add(position);

        //moveIndex = playerMoves.size() - 1;
    }

    public void addPlayerMoves(Collection<Vector3f> position) {
        playerMoves.addAll(position);
       // moveIndex = playerMoves.size() - 1;
    }

    /**
     * Use only for reference. Do not add anything.
     * 
     * @return List of player moves
     */
    public List<Vector3f> getPlayerMoves() {
        return playerMoves;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    /**
     * Reverts the state.
     * 
     * @return Returns true when the state is fully played back. Else false.
     */
    public boolean revert() {
        if (moveIndex > 0) {
            moveIndex--;
            return false;
        } else {
            return true;
        }
    }

    public void resetIndex() {
        moveIndex = playerMoves.size() - 1;
    }

    public Vector3f getOlderPlayerPos() {
        if (moveIndex < 0) {
            LOG.error("Player move history out of bounds");
        }
        
        return playerMoves.get(moveIndex);
    }
}
