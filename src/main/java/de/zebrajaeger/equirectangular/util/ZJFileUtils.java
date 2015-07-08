/*
 * Copyright (c) 2015, Lars Brandt. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package de.zebrajaeger.equirectangular.util;

/**
 * @author Lars Brandt
 */
public class ZJFileUtils {

  /**
   * Make a String from a filesize in a human readable manner.<br> Got this snipped from here: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
   *
   * @param size the size of a file (or something else)
   * @param si   (the type 100 or 1024 based)
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
