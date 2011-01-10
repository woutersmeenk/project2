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
package project2.model.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.EventListener;

import com.jme3.math.Vector3f;

public class Level {
    private static final Log LOG = LogFactory.getLog(Level.class);

    private final Map<Vector3f, Box> boxes;
    private final List<SwitchBox> switches;
    private final Vector3f start;
    private final List<Vector3f> checkpoints;

    private final List<EventListener<LocationEvent>> locationListeners;

    public Level(final Map<Vector3f, Box> boxes,
            final List<SwitchBox> switches, final Vector3f start,
            final List<Vector3f> checkpoints) {
        this.boxes = boxes;
        this.switches = switches;
        this.start = start;
        this.checkpoints = checkpoints;

        locationListeners = new ArrayList<EventListener<LocationEvent>>();

        // register the level with the switches
        for (final SwitchBox switchBox : switches) {
            switchBox.setLevel(this);
        }
    }

    public Map<Vector3f, Box> getBoxes() {
        return boxes;
    }

    public List<SwitchBox> getSwitches() {
        return switches;
    }

    public Vector3f getStart() {
        return start;
    }

    public List<Vector3f> getCheckpoints() {
        return checkpoints;
    }

    public Box boxFromId(final long id) {
        for (final Box box : boxes.values()) {
            if (id == box.getId()) {
                return box;
            }
        }

        return null;
    }

    // TODO: maybe do with id instead of current position?
    public void moveBox(final Vector3f curPos, final Vector3f newPos) {
        // move the box
        final Box box = boxes.get(curPos);

        if (box == null) {
            LOG.warn("No box found at" + curPos);
            return;
        }

        // replace the box in the map and set the new position
        boxes.put(newPos, boxes.remove(curPos));
        box.setLocation(newPos);

        for (final EventListener<LocationEvent> listener : locationListeners) {
            listener.onEvent(new LocationEvent(box.getId(), newPos));
        }
    }

    public boolean addLocationListener(final EventListener<LocationEvent> listener) {
        return locationListeners.add(listener);
    }
}
