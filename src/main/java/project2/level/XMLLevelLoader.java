package project2.level;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import project2.model.level.Box;
import project2.model.level.Level;
import project2.model.level.SwitchBox;
import project2.util.XMLException;
import project2.util.XMLUtils;

import com.jme3.math.Vector3f;

public class XMLLevelLoader {
    private static final Log LOG = LogFactory.getLog(XMLLevelLoader.class);

    public Level loadLevel(URL file) {
        try {
            Node node = XMLUtils.load(file);
            return parseLevel(node);
        } catch (XMLException e) {
            LOG.warn("Could not load level: " + file, e);
        }
        return null;
    }

    private Level parseLevel(Node node) throws XMLException {
        // Boxes
        List<Box> boxes = new ArrayList<Box>();
        for (Node boxNode : XMLUtils.findNodes("level/boxes/box", node)) {
            boxes.add(parseBox(boxNode));
        }

        // Checkpoints
        List<Vector3f> checkpoints = new ArrayList<Vector3f>();
        for (Node checkpointNode : XMLUtils.findNodes(
                "level/checkpoints/checkpoint", node)) {
            checkpoints.add(parseVector3f(checkpointNode));
        }

        // Start
        final Node startNode = XMLUtils.findNode("level/start", node);
        final Vector3f start = parseVector3f(startNode);
        return new Level(boxes, start, checkpoints);
    }

    private Vector3f parseVector3f(Node node) throws XMLException {
        float x = (float) XMLUtils.parseNumber("@x", node);
        float y = (float) XMLUtils.parseNumber("@y", node);
        float z = (float) XMLUtils.parseNumber("@z", node);
        return new Vector3f(x, y, z);
    }

    private Box parseBox(Node node) throws XMLException {
        Vector3f location = parseVector3f(node);
        int size = (int) XMLUtils.parseNumber("@size", node);
        final Node switchNode = XMLUtils.findNode("switch", node);
        final SwitchBox switchBox = parseSwitchBox(switchNode);
        return new Box(location, size, switchBox);
    }

    private SwitchBox parseSwitchBox(Node switchNode) throws XMLException {
        // TODO Auto-generated method stub
        return null;
    }

}