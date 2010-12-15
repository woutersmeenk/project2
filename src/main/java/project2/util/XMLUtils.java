package project2.util;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtils {
    private static final XPath path = XPathFactory.newInstance().newXPath();

    public static Node load(URL file) throws XMLException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file.openStream());
        } catch (ParserConfigurationException e) {
            throw new XMLException(e);
        } catch (SAXException e) {
            throw new XMLException(e);
        } catch (IOException e) {
            throw new XMLException(e);
        }
    }

    public static NodeIterable findNodes(String expression, Node node)
            throws XMLException {
        try {
            NodeList nodes = (NodeList) path.evaluate(expression, node,
                    XPathConstants.NODESET);
            return new NodeIterable(nodes);
        } catch (XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static Node findNode(String expression, Node node)
            throws XMLException {
        try {
            return (Node) path.evaluate(expression, node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static String parseString(String expression, Node node)
            throws XMLException {
        try {
            return path.evaluate(expression, node);
        } catch (XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static double parseNumber(String expression, Node node)
            throws XMLException {
        return parseNumber(expression, node, Double.NaN);
    }

    public static double parseNumber(String expression, Node node,
            double defaultValue) throws XMLException {
        try {
            Double result = (Double) path.evaluate(expression, node,
                    XPathConstants.NUMBER);
            if (result == null) {
                result = defaultValue;
            }
            return result;
        } catch (XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static boolean parseBoolean(String expression, Node node,
            boolean defaultValue) throws XMLException {
        try {
            Boolean result = (Boolean) path.evaluate(expression, node,
                    XPathConstants.BOOLEAN);
            if (result == null) {
                result = defaultValue;
            }
            return result;
        } catch (XPathExpressionException e) {
            throw new XMLException(e);
        }
    }
}
