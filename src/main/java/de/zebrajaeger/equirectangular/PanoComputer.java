package de.zebrajaeger.equirectangular;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PanoComputer {
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

  public PanoComputer(int src_w, int src_h, double src_w_deg) {
    this(src_w, src_h, src_w_deg, 0.0);
  }

  public PanoComputer(int src_w, int src_h, double src_w_deg, double src_y_off_deg) {
    source_w = src_w;
    source_h = src_h;
    source_w_deg = src_w_deg;
    source_off_y_deg = src_y_off_deg;

    target_w = (int) ((360.0 * src_w) / src_w_deg);
    target_h = target_w / 2;

    source_h_deg = ((source_h) * 180.0) / target_h;

    source_off_x = (target_w - source_w) / 2;
    source_off_y = (target_h - source_h) / 2;
    source_off_y_top = source_off_y;
    source_off_y_top += (int) ((src_y_off_deg * target_h) / 180);

    source_off_y_bot = target_h - source_off_y_top - source_h;
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

  public String krPanoSnippt() {
    final StringBuilder sb = new StringBuilder();
    sb.append("<view").append(CRLF);
    sb.append(makeArg("limitview", "range", true));

    final double w2 = source_w_deg / 2.0;
    sb.append(makeArg("hlookatmin", Double.toString(-w2), true));
    sb.append(makeArg("hlookatmax", Double.toString(w2), true));

    final double h2 = source_h_deg / 2.0;
    sb.append(makeArg("vlookatmin", Double.toString(-h2 + source_off_y_deg), true));
    sb.append(makeArg("vlookatmax", Double.toString(h2 + source_off_y_deg), true));

    sb.append(makeArg("hlookat", "0", true));
    sb.append(makeArg("vlookat", "0", true));
    sb.append(makeArg("maxpixelzoom", "2.0", true));
    sb.append(makeArg("fovmax", "150", true));
    sb.append("/>");

    return sb.toString();
  }

  private String makeArg(String name, String arg, boolean komma) {
    final StringBuilder sb = new StringBuilder();
    sb.append(INDENT);
    sb.append(name);
    sb.append("=\"");
    sb.append(arg);
    sb.append("\"");
    if (komma) {
      sb.append(",");
    }
    sb.append(CRLF);

    return sb.toString();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}
