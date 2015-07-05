package de.zebrajaeger.equirectangular.autopano;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.zebrajaeger.equirectangular.util.ZJXmlUtils;

/**
 * A wrapper for the XMP-Data that Autopano normally puts into a rendered panoramic image
 * 
 * @author Lars Brandt
 *
 */
public class GPanoData {

  private static Logger LOG = LogManager.getLogger(GPanoData.class);

  private Boolean usePanoramaViewer = null;
  private String projectionType = null;
  private Integer croppedAreaLeftPixels = null;
  private String stitchingSoftware = null;
  private Integer croppedAreaImageWidthPixels = null;
  private Integer sourcePhotosCount = null;
  private Integer croppedAreaTopPixels = null;
  private Integer croppedAreaImageHeightPixels = null;
  private Integer fullPanoWidthPixels = null;
  private Integer fullPanoHeightPixels = null;

  /**
   * Changes the bytes into ascii string and pares it as a XML-document
   * 
   * @param data the data to parse
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  protected void parse(byte[] data) throws ParserConfigurationException, SAXException, IOException {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document document = builder.parse(new ByteArrayInputStream(data));

    final Node root = ZJXmlUtils.find(document, "x:xmpmeta");
    if (root == null) {
      return;
    }

    final Node rdf = ZJXmlUtils.find(root, "rdf:RDF");
    if (rdf == null) {
      return;
    }

    final Node desc = ZJXmlUtils.find(rdf, "rdf:Description");
    if (rdf == desc) {
      return;
    }

    final HashMap<String, String> a = ZJXmlUtils.getAttributes(desc, "GPano:", true);

    usePanoramaViewer = parseBoolean(a, "UsePanoramaViewer");
    projectionType = parseString(a, "ProjectionType");
    croppedAreaLeftPixels = parseInt(a, "CroppedAreaLeftPixels");
    stitchingSoftware = parseString(a, "StitchingSoftware");;
    croppedAreaImageWidthPixels = parseInt(a, "CroppedAreaImageWidthPixels");;
    sourcePhotosCount = parseInt(a, "SourcePhotosCount");;
    croppedAreaTopPixels = parseInt(a, "CroppedAreaTopPixels");;
    croppedAreaImageHeightPixels = parseInt(a, "CroppedAreaImageHeightPixels");;
    fullPanoWidthPixels = parseInt(a, "FullPanoWidthPixels");;
    fullPanoHeightPixels = parseInt(a, "FullPanoHeightPixels");;
  }

  protected static String parseString(HashMap<String, String> map, String name) {
    return map.get(name);
  }


  protected static Integer parseInt(HashMap<String, String> map, String name) {
    final String val = map.get(name);
    if (val == null) {
      return null;
    }
    try {
      return Integer.parseInt(val);
    } catch (final NumberFormatException e) {
      final String msg = String.format("can not convert parameter '%s' with value '%s' to long", name, val);
      LOG.error(msg);
    }
    return null;
  }

  protected static Boolean parseBoolean(HashMap<String, String> map, String name) {
    final String val = map.get(name);
    if (val == null) {
      return null;
    }
    try {
      return Boolean.parseBoolean(val);
    } catch (final NumberFormatException e) {
      final String msg = String.format("can not convert parameter '%s' with value '%s' to long", name, val);
      LOG.error(msg);
    }
    return null;
  }



  public Boolean getUsePanoramaViewer() {
    return this.usePanoramaViewer;
  }

  public String getProjectionType() {
    return this.projectionType;
  }

  public Integer getCroppedAreaLeftPixels() {
    return this.croppedAreaLeftPixels;
  }

  public String getStitchingSoftware() {
    return this.stitchingSoftware;
  }

  public Integer getCroppedAreaImageWidthPixels() {
    return this.croppedAreaImageWidthPixels;
  }

  public Integer getSourcePhotosCount() {
    return this.sourcePhotosCount;
  }

  public Integer getCroppedAreaTopPixels() {
    return this.croppedAreaTopPixels;
  }

  public Integer getCroppedAreaImageHeightPixels() {
    return this.croppedAreaImageHeightPixels;
  }

  public Integer getFullPanoWidthPixels() {
    return this.fullPanoWidthPixels;
  }

  public Integer getFullPanoHeightPixels() {
    return this.fullPanoHeightPixels;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  public static class Builder {
    public static GPanoData buildFrombytes(byte[] content) throws ParserConfigurationException, SAXException,
        IOException {
      final GPanoData res = new GPanoData();
      res.parse(content);
      return res;
    }
  }
}
