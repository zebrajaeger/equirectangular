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

package de.zebrajaeger.equirectangular;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.zebrajaeger.equirectangular.autopano.GPanoData;

/**
 * This class encapsulates (and creates) the needed parameters to render the target image
 * 
 * @author Lars Brandt
 */
public class RenderParameters {
  private int source_w = 0;
  private double source_w_deg = 0;
  private int source_h = 0;
  private double source_h_deg = 0;
  private double source_off_y_deg = 0;

  private int target_w = 0;

  private int target_h = 0;
  private int source_off_x = 0;
  private int source_off_y = 0;
  private int source_off_y_top = 0;
  private int source_off_y_bot = 0;

  /**
   * @param src_w source image width (in pixel)
   * @param src_h source image height (in pixel)
   * @param src_w_deg source image width (in percent from a 360 degree sphere, also 0.0 ... 100.0)
   * @param src_y_off_deg the vertical offset from middle-line (in percent, -90.0 .. 90.0)
   */
  protected RenderParameters(int src_w, int src_h, double src_w_deg, double src_y_off_deg) {
    this.source_w = src_w;
    this.source_h = src_h;
    this.source_w_deg = src_w_deg;
    this.source_off_y_deg = src_y_off_deg;

    this.target_w = (int) ((360.0 * src_w) / src_w_deg);
    this.target_h = target_w / 2;

    this.source_h_deg = ((source_h) * 180.0) / target_h;

    this.source_off_x = (target_w - source_w) / 2;
    this.source_off_y = (target_h - source_h) / 2;
    this.source_off_y_top = source_off_y;
    this.source_off_y_top += (int) ((src_y_off_deg * target_h) / 180);

    this.source_off_y_bot = target_h - source_off_y_top - source_h;
  }

  /**
   * 
   * @param target_w the target image width
   * @param target_h the target image heigth
   * @param src_w the source image width
   * @param src_h the source image height
   * @param src_off_x the source image _TOP_-left-position in target-image
   * @param src_off_y the source image top-_LEFT_-position in target-image
   */
  protected RenderParameters(int target_w, int target_h, int src_w, int src_h, int src_off_x, int src_off_y) {
    this.source_w = src_w;
    this.source_h = src_h;
    this.source_w_deg = (360.0 * src_w) / target_w;


    this.target_w = target_w;
    this.target_h = target_h;

    this.source_h_deg = (180.0 * src_h) / target_h;

    this.source_off_x = (target_w - source_w) / 2;
    this.source_off_y = (target_h - src_h) / 2;
    this.source_off_y_bot = src_off_y;
    final double src_h_middle_off = (target_h - src_h) / 2;
    final double src_h_middle_off_diff = src_h_middle_off - source_off_y_top;
    this.source_off_y_deg = (180.0 * src_h_middle_off_diff) / target_h;

    this.source_off_y_top = target_h - source_off_y_bot - source_h;
  }

  public int getSource_w() {
    return this.source_w;
  }

  public double getSource_w_deg() {
    return this.source_w_deg;
  }

  public int getSource_h() {
    return this.source_h;
  }

  public double getSource_off_y_deg() {
    return this.source_off_y_deg;
  }

  public int getTarget_w() {
    return this.target_w;
  }

  public int getTarget_h() {
    return this.target_h;
  }

  public int getSource_off_x() {
    return this.source_off_x;
  }

  public int getSource_off_y_top() {
    return this.source_off_y_top;
  }

  public int getSource_off_y_bot() {
    return source_off_y_bot;
  }

  private static String CRLF = "\n";
  private static String INDENT = "    ";

  /**
   * create a config xml snippet to use in krpano xml config for panoraam
   * 
   * @return
   */
  public String krPanoSnippt() {
    final StringBuilder sb = new StringBuilder();
    sb.append("<view").append(CRLF);
    sb.append(makeArg("limitview", "range"));

    final double w2 = source_w_deg / 2.0;
    sb.append(makeArg("hlookatmin", Double.toString(-w2)));
    sb.append(makeArg("hlookatmax", Double.toString(w2)));

    final double h2 = source_h_deg / 2.0;
    sb.append(makeArg("vlookatmin", Double.toString(-h2 + source_off_y_deg)));
    sb.append(makeArg("vlookatmax", Double.toString(h2 + source_off_y_deg)));

    sb.append(makeArg("hlookat", "0"));
    sb.append(makeArg("vlookat", "0"));
    sb.append(makeArg("maxpixelzoom", "2.0"));
    sb.append(makeArg("fovmax", "150"));
    sb.append("/>");

    return sb.toString();
  }

  /**
   * Little helper to create xml attribute string
   * 
   * @param name name of value
   * @param arg value
   */
  private String makeArg(String name, String arg) {
    final StringBuilder sb = new StringBuilder();
    sb.append(INDENT);
    sb.append(name);
    sb.append("=\"");
    sb.append(arg);
    sb.append("\"");
    sb.append(CRLF);

    return sb.toString();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  /**
   * 'Factory' to create a RenderParameter-instance
   * 
   * @author Lars Brandt
   */
  public static class Builder {
    public static RenderParameters buildWithWH(int src_w, int src_h, double src_w_deg, double src_y_off_deg) {
      return new RenderParameters(src_w, src_h, src_w_deg, src_y_off_deg);
    }

    public static RenderParameters buildFromAutopano(GPanoData pd) {
      return new RenderParameters(pd.getFullPanoWidthPixels(), pd.getFullPanoHeightPixels(),
          pd.getCroppedAreaImageWidthPixels(), pd.getCroppedAreaImageHeightPixels(), pd.getCroppedAreaLeftPixels(),
          pd.getCroppedAreaTopPixels());
    }
  }
}
