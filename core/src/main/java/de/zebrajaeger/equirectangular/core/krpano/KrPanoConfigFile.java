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
import java.io.File;
import java.io.IOException;

/**
 * @author Lars Brandt on 22.05.2016.
 */
public class KrPanoConfigFile {
    private File configFile;
    private Document doc;

    public static KrPanoConfigFile of(File configFile) throws IOException, SAXException, ParserConfigurationException {
        KrPanoConfigFile krPanoConfigFile = new KrPanoConfigFile(configFile);
        krPanoConfigFile.load();
        return krPanoConfigFile;
    }

    private KrPanoConfigFile(File configFile) {
        this.configFile = configFile;
    }

    private void load() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        doc = builder.parse(configFile);
    }

    private Element getViewElement() {
        Element krPano = (Element) doc.getFirstChild();
        NodeList viewList = krPano.getElementsByTagName("view");
        return (Element) viewList.item(0);
    }

    public KrPanoConfigFile showErrors(boolean showErrors) {
        Element krPano = (Element) doc.getFirstChild();
        krPano.setAttribute("showerrors", Boolean.toString(showErrors));
        return this;
    }

    public KrPanoConfigFile limitView(LimitView limitView) {
        getViewElement().setAttribute("limitview", limitView.getName());
        return this;
    }

    public KrPanoConfigFile hLookAtMin(double value) {
        getViewElement().setAttribute("hlookatmin", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile hLookAtMax(double value) {
        getViewElement().setAttribute("hlookatmax", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile vLookAtMin(double value) {
        getViewElement().setAttribute("vlookatmin", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile vLookAtMax(double value) {
        getViewElement().setAttribute("vlookatmax", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile hLookAt(double value) {
        getViewElement().setAttribute("hlookat", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile vLookAt(double value) {
        getViewElement().setAttribute("vlookat", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile maxPixelzoom(double value) {
        getViewElement().setAttribute("maxpixelzoom", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile fovMax(double value) {
        getViewElement().setAttribute("fovmax", Double.toString(value));
        return this;
    }

    public KrPanoConfigFile save() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        transformer.transform(source, new StreamResult(configFile));

        return this;
    }

    public enum LimitView {
        AUTO("auto"), RANGE("range");

        private String name;

        LimitView(String type) {
            this.name = type;
        }

        public String getName() {
            return name;
        }
    }
}
