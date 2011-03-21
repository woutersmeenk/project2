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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import project2.GameStateManager;
import project2.level.model.Checkpoint;
import project2.level.model.Cube;
import project2.level.model.CubeFactory;
import project2.level.model.SwitchCube;
import project2.level.model.Text;
import project2.util.IdFactory;
import project2.util.XMLException;
import project2.util.XMLUtils;

import com.jme3.math.Vector3f;

public class XMLLevelLoader implements LevelLoader {
    private static final Log LOG = LogFactory.getLog(XMLLevelLoader.class);
    private GameStateManager gameStateManager;

    private Vector3f currentOffset;

    @Override
    public List<Level> loadLevelSet(final URL url,
            final GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        final List<Level> levelSet = new ArrayList<Level>();

        try {
            final Node node = XMLUtils.load(url);

            for (final Node levelNode : XMLUtils
                    .findNodes("levels/level", node)) {
                levelSet.add(parseLevel(levelNode));
            }
        } catch (final XMLException e) {
            LOG.warn("Could not load level: " + url, e);
        }

        return levelSet;
    }

    private Level parseLevel(final Node node) throws XMLException {
        final List<SwitchCube> switches = new ArrayList<SwitchCube>();

        // read offset
        currentOffset = new Vector3f(); // required for parseVector3f
        currentOffset = parseVector3f(node);

        // Cubes
        final Map<Vector3f, Cube> cubes = new HashMap<Vector3f, Cube>();
        for (final Node cubeNode : XMLUtils.findNodes("cubes/cube", node)) {
            final Cube cube = parseCube(cubeNode, switches);
            cubes.put(cube.getLocation(), cube);
        }

        // Add clones of the cubes from the current switch state
        for (final SwitchCube switchCube : switches) {
            for (final Cube cube : switchCube.getCurrentState()) {
                cubes.put(cube.getLocation(), CubeFactory.getInstance()
                        .createCube(cube.getLocation(), cube.getSize(), true, cube.getName()));
            }
        }

        // Checkpoints
        final Map<Vector3f, Checkpoint> checkpoints = new HashMap<Vector3f, Checkpoint>();
        for (final Node checkpointNode : XMLUtils.findNodes(
                "checkpoints/checkpoint", node)) {
            final Vector3f pos = parseVector3f(checkpointNode);
            checkpoints.put(pos, new Checkpoint(IdFactory.generateID(), pos));
        }

        // Start
        final Node startNode = XMLUtils.findNode("start", node);
        final Vector3f start = parseVector3f(startNode);

        // End
        final Node endNode = XMLUtils.findNode("end", node);
        final Vector3f end = parseVector3f(endNode);
        final SwitchCube endSwitch = parseSwitchCube(endNode, null);

        // Add clones for the end switch boxes
        if (endSwitch != null) {
            for (final Cube cube : endSwitch.getCurrentState()) {
                cubes.put(cube.getLocation(), CubeFactory.getInstance()
                        .createCube(cube.getLocation(), cube.getSize(), true, cube.getName()));
            }
        }

        final float maxFall = (float) XMLUtils.parseNumber("@maxfall", node)
                + currentOffset.getZ();

        Set<Text> texts = new HashSet<Text>();
        // Parse the text for the level
        for (final Node textNode : XMLUtils.findNodes("text", node)) {
            texts.add(parseText(textNode));
        }

        return new Level(cubes, switches, start, end, endSwitch, maxFall,
                checkpoints,texts);
    }

    private Text parseText(Node node) throws XMLException {
        final String text = XMLUtils.parseString("@text", node);
        final Node positionNode = XMLUtils.findNode("position", node);
        final Vector3f position = parseVector3f(positionNode);
        final Node rotationNode = XMLUtils.findNode("rotation", node);
        final Vector3f rotation = parseVector3f(rotationNode);
        return new Text(position, rotation, text);
    }

    private Vector3f parseVector3f(final Node node) throws XMLException {
        final float x = (float) XMLUtils.parseNumber("@x", node);
        final float y = (float) XMLUtils.parseNumber("@y", node);
        final float z = (float) XMLUtils.parseNumber("@z", node);
        return new Vector3f(x, y, z).add(currentOffset);
    }

    private Cube parseCube(final Node node, final List<SwitchCube> switches)
            throws XMLException {
        final boolean isNull = XMLUtils.parseBoolean("@null", node, false);
        if (isNull) {
            return null;
        }
        final Vector3f location = parseVector3f(node);
        final int size = (int) XMLUtils.parseNumber("@size", node, 1);
        final String name = XMLUtils.parseString("@name", node);

        final SwitchCube switchCube = parseSwitchCube(node, switches);
        final Vector3f teleporter = parseTeleporter(node);
        return CubeFactory.getInstance().createCube(location, size, switchCube,
                teleporter, false, name);
    }

    private Vector3f parseTeleporter(final Node node) throws XMLException {
        final Node teleportNode = XMLUtils.findNode("teleport", node);

        if (teleportNode == null) {
            return null;
        }

        return parseVector3f(teleportNode);
    }

    private SwitchCube parseSwitchCube(final Node node,
            final List<SwitchCube> switches) throws XMLException {
        final Node switchNode = XMLUtils.findNode("switch", node);

        if (switchNode == null) {
            return null;
        }

        final List<List<Cube>> states = new ArrayList<List<Cube>>();
        for (final Node stateNode : XMLUtils.findNodes("state", switchNode)) {
            final List<Cube> state = new ArrayList<Cube>();
            for (final Node cubeNode : XMLUtils.findNodes("cube", stateNode)) {
                state.add(parseCube(cubeNode, switches));
            }
            states.add(state);
        }

        final SwitchCube result = new SwitchCube(gameStateManager, states,
                XMLUtils.parseBoolean("@loop", switchNode, false));

        if (switches != null) {
            switches.add(result);
        }

        return result;
    }

}
