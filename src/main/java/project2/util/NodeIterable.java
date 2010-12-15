package project2.util;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeIterable implements Iterable<Node>, Iterator<Node> {
    private final NodeList nodes;
    private int index;

    public static NodeIterable wrap(NodeList nodes) {
        return new NodeIterable(nodes);
    }

    public NodeIterable(NodeList nodes) {
        this.nodes = nodes;
    }

    @Override
    public Iterator<Node> iterator() {
        index = 0;
        return this;
    }

    @Override
    public boolean hasNext() {
        return index < nodes.getLength();
    }

    @Override
    public Node next() {
        return nodes.item(index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
