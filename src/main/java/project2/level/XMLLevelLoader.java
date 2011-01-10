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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import project2.GameStateManager;
import project2.level.model.Box;
import project2.level.model.BoxFactory;
import project2.level.model.SwitchBox;
import project2.util.XMLException;
import project2.util.XMLUtils;

import com.jme3.math.Vector3f;

public class XMLLevelLoader implements LevelLoader {
    private static final Log LOG = LogFactory.getLog(XMLLevelLoader.class);
    private GameStateManager gameStateManager;

    @Override
    public Level loadLevel(final URL url,
            final GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;

        try {
            final Node node = XMLUtils.load(url);
            return parseLevel(node);
        } catch (final XMLException e) {
            LOG.warn("Could not load level: " + url, e);
        }

        return null;
    }

    private Level parseLevel(final Node node) throws XMLException {
        final List<SwitchBox> switches = new ArrayList<SwitchBox>();

        // Boxes
        final Map<Vector3f, Box> boxes = new HashMap<Vector3f, Box>();
        for (final Node boxNode : XMLUtils.findNodes("level/boxes/box", node)) {
            final Box box = parseBox(boxNode, switches);
            boxes.put(box.getLocation(), box);
        }

        // Add clones of the boxes from the current switch state
        for (final SwitchBox switchBox : switches) {
            for (final Box box : switchBox.getCurrentState()) {
                boxes.put(box.getLocation(), BoxFactory.getInstance()
                        .createBox(box.getLocation(), box.getSize()));
            }
        }

        // Checkpoints
        final Map<Vector3f, Boolean> checkpoints = new HashMap<Vector3f, Boolean>();
        for (final Node checkpointNode : XMLUtils.findNodes(
                "level/checkpoints/checkpoint", node)) {
	    checkpoints.put(parseVector3f(checkpointNode), Boolean.FALSE);
        }

        // Start
        final Node startNode = XMLUtils.findNode("level/start", node);
        final Vector3f start = parseVector3f(startNode);
        return new Level(boxes, switches, start, checkpoints);
    }

    private Vector3f parseVector3f(final Node node) throws XMLException {
        final float x = (float) XMLUtils.parseNumber("@x", node);
        final float y = (float) XMLUtils.parseNumber("@y", node);
        final float z = (float) XMLUtils.parseNumber("@z", node);
        return new Vector3f(x, y, z);
    }

    private Box parseBox(final Node node, final List<SwitchBox> switches)
            throws XMLException {
        final boolean isNull = XMLUtils.parseBoolean("@null", node, false);
        if (isNull) {
            return null;
        }
        final Vector3f location = parseVector3f(node);
        final int size = (int) XMLUtils.parseNumber("@size", node, 1);
        final Node switchNode = XMLUtils.findNode("switch", node);
        final SwitchBox switchBox = parseSwitchBox(switchNode, switches);
        return BoxFactory.getInstance().createBox(location, size, switchBox);
    }

    private SwitchBox parseSwitchBox(final Node node,
            final List<SwitchBox> switches) throws XMLException {
        if (node == null) {
            return null;
        }
        final List<List<Box>> states = new ArrayList<List<Box>>();
        for (final Node stateNode : XMLUtils.findNodes("state", node)) {
            final List<Box> state = new ArrayList<Box>();
            for (final Node boxNode : XMLUtils.findNodes("box", stateNode)) {
                state.add(parseBox(boxNode, switches));
            }
            states.add(state);
        }

        // TODO: add loop property
        final SwitchBox result = new SwitchBox(gameStateManager, states, false);

        switches.add(result);
        return result;
    }

}
