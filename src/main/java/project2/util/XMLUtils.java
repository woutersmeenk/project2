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

public final class XMLUtils {
    private static final XPath PATH = XPathFactory.newInstance().newXPath();

    private XMLUtils() {
    }

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
            final NodeList nodes = (NodeList) PATH.evaluate(expression, node,
                    XPathConstants.NODESET);
            return new NodeIterable(nodes);
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static Node findNode(final String expression, final Node node)
            throws XMLException {
        try {
            return (Node) PATH.evaluate(expression, node, XPathConstants.NODE);
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    public static String parseString(final String expression, final Node node)
            throws XMLException {
        try {
            return PATH.evaluate(expression, node);
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
            final Object test = PATH.evaluate(expression, node,
                    XPathConstants.NODE);
            Double result = (Double) PATH.evaluate(expression, node,
                    XPathConstants.NUMBER);
            if (test == null) {
                result = defaultValue;
            }
            return result;
        } catch (final XPathExpressionException e) {
            throw new XMLException(e);
        }
    }

    /** FIXME: does not work. Always returns true when the expression exists. */
    public static boolean parseBoolean(final String expression,
            final Node node, final boolean defaultValue) throws XMLException {
        try {
            final Object test = PATH.evaluate(expression, node,
                    XPathConstants.NODE);
            Boolean result = (Boolean) PATH.evaluate(expression, node,
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
