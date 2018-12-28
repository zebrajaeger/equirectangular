package de.zebrajaeger.equirectangular.core.krpano;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import de.zebrajaeger.equirectangular.core.panosnippet.ViewRange;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import java.io.File;
import java.io.IOException;

/**
 * @author Lars Brandt on 22.05.2016.
 */
public class KrPanoConfigFile {
    private File config;
    private Document doc;

    public KrPanoConfigFile(File config) {
        this.config = config;
    }

    public void load() throws ParserConfigurationException, IOException, SAXException {
        if (doc != null) {
            throw new IllegalStateException("document alread loaded");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        XPathExpression expr = null;
        builder = factory.newDocumentBuilder();
        doc = builder.parse(config);
    }

    public void setShowErrors(boolean value) {
        checkDocumetLoaded();

        Element krPano = (Element) doc.getFirstChild();
        krPano.setAttribute("showerrors", Boolean.toString(value));
    }

    private Element getViewElement() {
        checkDocumetLoaded();
        Element krPano = (Element) doc.getFirstChild();
        NodeList viewList = krPano.getElementsByTagName("view");
        return (Element) viewList.item(0);
    }

    public void setView(ViewRange range) {
        setLimitViewRange();
        setFovMax(150);
        setMaxPixelzoom(2);
        setHLookAtMin(range.getFovLeft());
        setHLookAtMax(range.getFovRight());
        setVLookAtMin(range.getFovTop());
        setVLookAtMax(range.getFovBottom());
        setHLookAt(0);
        setVLookAt(0);
    }

    public void setLimitViewAuto() {
        getViewElement().setAttribute("limitview", "auto");
    }

    public void setLimitViewRange() {
        getViewElement().setAttribute("limitview", "range");
    }

    public void setHLookAtMin(double value) {
        getViewElement().setAttribute("hlookatmin", Double.toString(value * 360.0));
    }

    public void setHLookAtMax(double value) {
        getViewElement().setAttribute("hlookatmax", Double.toString(value * 360.0));
    }

    public void setVLookAtMin(double value) {
        getViewElement().setAttribute("vlookatmin", Double.toString(value * -180.0));
    }

    public void setVLookAtMax(double value) {
        getViewElement().setAttribute("vlookatmax", Double.toString(value * -180.0));
    }

    public void setHLookAt(double value) {
        getViewElement().setAttribute("hlookat", Double.toString(value * 360.0));
    }

    public void setVLookAt(double value) {
        getViewElement().setAttribute("vlookat", Double.toString(value * -180.0));
    }

    public void setMaxPixelzoom(double value) {
        getViewElement().setAttribute("maxpixelzoom", Double.toString(value));
    }

    public void setFovMax(double value) {
        getViewElement().setAttribute("fovmax", Double.toString(value));
    }

    private void checkDocumetLoaded() {
        if (doc == null) {
            throw new IllegalStateException("no document to store");
        }
    }

    public void save() throws TransformerException {
        checkDocumetLoaded();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(config);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);
    }
}
