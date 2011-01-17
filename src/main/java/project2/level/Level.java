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
package project2.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.EventListener;
import project2.LocationEvent;
import project2.level.model.Checkpoint;
import project2.level.model.Cube;
import project2.level.model.SwitchCube;

import com.jme.math.Vector3f;

public class Level {
    private static final Log LOG = LogFactory.getLog(Level.class);

    private final Map<Vector3f, Cube> cubes;
    private final List<SwitchCube> switches;
    private final Vector3f start;
    private final Vector3f end;
    private final Map<Vector3f, Checkpoint> checkpoints;
    private final List<EventListener<LocationEvent>> locationListeners;

    public Level(final Map<Vector3f, Cube> cubes,
            final List<SwitchCube> switches, final Vector3f start,
            final Vector3f end, final Map<Vector3f, Checkpoint> checkpoints) {
        this.cubes = cubes;
        this.switches = switches;
        this.start = start;
        this.end = end;
        this.checkpoints = checkpoints;

        locationListeners = new ArrayList<EventListener<LocationEvent>>();

        // register the level with the switches
        for (final SwitchCube switchCube : switches) {
            switchCube.setLevel(this);
        }
    }

    public Map<Vector3f, Cube> getCubes() {
        return cubes;
    }

    public List<SwitchCube> getSwitches() {
        return switches;
    }

    public Vector3f getStart() {
        return start;
    }
    
    public Vector3f getEnd() {
        return end;
    }

    public Map<Vector3f, Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public Cube cubeFromId(final long id) {
        for (final Cube cube : cubes.values()) {
            if (id == cube.getId()) {
                return cube;
            }
        }

        return null;
    }

    // TODO: maybe do with id instead of current position?
    public void moveCube(final Vector3f curPos, final Vector3f newPos) {
        // move the cube
        final Cube cube = cubes.get(curPos);

        if (cube == null) {
            LOG.warn("No cube found at" + curPos);
            return;
        }

        // replace the cube in the map and set the new position
        cubes.put(newPos, cubes.remove(curPos));
        cube.setLocation(newPos);

        for (final EventListener<LocationEvent> listener : locationListeners) {
            listener.onEvent(new LocationEvent(cube.getId(), newPos));
        }
    }

    public boolean addLocationListener(
            final EventListener<LocationEvent> listener) {
        return locationListeners.add(listener);
    }
}
