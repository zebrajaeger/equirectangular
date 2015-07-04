package de.zebrajaeger.equirectangular.util;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ZJXmlUtils {
  public static HashMap<String, String> getAttributes(Node node, String prefix, boolean removePrefix) {
    final HashMap<String, String> result = new HashMap<>();

    final NamedNodeMap atts = node.getAttributes();
    for (int i = 0; i < atts.getLength(); i++) {
      final Node att = atts.item(i);
      String name = att.getNodeName();
      if (name.startsWith(prefix)) {
        if (removePrefix) {
          name = name.substring(prefix.length());
        }
        result.put(name, att.getNodeValue());
      }
    }
    return result;
  }

  public static HashMap<String, String> getAttributes(Node node) {
    final HashMap<String, String> result = new HashMap<>();

    final NamedNodeMap atts = node.getAttributes();
    for (int i = 0; i < atts.getLength(); i++) {
      final Node att = atts.item(i);
      result.put(att.getNodeName(), att.getNodeValue());
    }
    return result;
  }

  public static Node find(Node root, String name) {
    final NodeList list = root.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      final Node subnode = list.item(i);
      if (subnode.getNodeType() == Node.ELEMENT_NODE) {
        System.out.println(subnode.getNodeName());
        if (subnode.getNodeName().equals(name)) {
          return subnode;
        }
      }
    }
    return null;
  }
}
