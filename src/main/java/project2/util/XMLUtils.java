package project2.util;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtils {
    private static final XPath path = XPathFactory.newInstance().newXPath();

    public static Node load(final URL file) throws XMLException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file.openStream());
        } catch (final ParserConfigurationException e) {
            throw new XMLException(e);
        } catch (final SAXException e) {
            throw new XMLException(e);
        } catch (final IOException e) {
            throw new XMLException(e);
        }
    }

    public static NodeIterable findNodes(final String expression,
            final Node node) throws XMLException {
        try {
            final NodeList nodes = (NodeList) path.evaluate(expression, node,
                    XPathConstants.NODESET);
            return new NodeIterable(nodes);
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static Node findNode(final String expression, final Node node)
            throws XMLException {
        try {
            return (Node) path.evaluate(expression, node, XPathConstants.NODE);
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static String parseString(final String expression, final Node node)
            throws XMLException {
        try {
            return path.evaluate(expression, node);
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static double parseNumber(final String expression, final Node node)
            throws XMLException {
        return parseNumber(expression, node, Double.NaN);
    }

    public static double parseNumber(final String expression, final Node node,
            final double defaultValue) throws XMLException {
        try {
            final Object test = path.evaluate(expression, node,
                    XPathConstants.NODE);
            Double result = (Double) path.evaluate(expression, node,
                    XPathConstants.NUMBER);
            if (test == null) {
                result = defaultValue;
            }
            return result;
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static boolean parseBoolean(final String expression,
            final Node node, final boolean defaultValue) throws XMLException {
        try {
            final Object test = path.evaluate(expression, node,
                    XPathConstants.NODE);
            Boolean result = (Boolean) path.evaluate(expression, node,
                    XPathConstants.BOOLEAN);
            if (test == null) {
                result = defaultValue;
            }
            return result;
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }
}
