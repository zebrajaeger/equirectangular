package de.zebrajaeger.krpano;

import de.zebrajaeger.panosnippet.ViewRange;
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
 * Created by lars on 22.05.2016.
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
