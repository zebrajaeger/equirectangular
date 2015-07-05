package de.zebrajaeger.equirectangular.util;

/**
 * 
 * @author Lars Brandt
 *
 */
public class ZJFileUtils {

  /**
   * Make a String from a filesize in a human readable manner.<br>
   * Got this snipped from here:
   * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
   * 
   * @param size the size of a file (or something else)
   * @param si (the type 100 or 1024 based)
   */
  public static String humanReadableByteCount(long size, boolean si) {
    final int unit = si ? 1000 : 1024;
    if (size < unit) {
      return size + " B";
    }
    final int exp = (int) (Math.log(size) / Math.log(unit));
    final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
    return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
  }

}
