package de.zebrajaeger.equirectangular.ui;

import java.io.File;
import java.util.List;

/**
 * @author Lars Brandt
 */
public interface IFileDropListener {

  /**
   * return false if we dont want this stuff. the mouse icon change for that
   */
  boolean onAcceptDrop(List<File> files);

  /**
   * is called when all listeners says true see{@link #onAcceptDrop(java.util.List)}
   */
  void onDrop(List<File> files);
}
