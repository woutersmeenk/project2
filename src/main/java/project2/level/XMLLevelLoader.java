package project2.level;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import project2.model.level.Box;
import project2.model.level.BoxFactory;
import project2.model.level.Level;
import project2.model.level.SwitchBox;
import project2.util.XMLException;
import project2.util.XMLUtils;

import com.jme3.math.Vector3f;

public class XMLLevelLoader implements LevelLoader {
    private static final Log LOG = LogFactory.getLog(XMLLevelLoader.class);

    @Override
    public Level loadLevel(final URL url) {
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

        // Add boxes from the current switch state
        for (final SwitchBox switchBox : switches) {
            for (final Box box : switchBox.getCurrentState()) {
                boxes.put(box.getLocation(), box);
            }
        }

        // Checkpoints
        final List<Vector3f> checkpoints = new ArrayList<Vector3f>();
        for (final Node checkpointNode : XMLUtils.findNodes(
                "level/checkpoints/checkpoint", node)) {
            checkpoints.add(parseVector3f(checkpointNode));
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
        final SwitchBox result = new SwitchBox(states);
        switches.add(result);
        return result;
    }

}
