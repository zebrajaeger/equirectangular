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

package de.zebrajaeger.equirectangular.psd;

import de.zebrajaeger.equirectangular.util.ZJArrayUtils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

public class PsdHeader implements IPsdMetaDataPart {

  public static enum MODE {
    BITMAP_MONOCHROME(0), GRAY_SCALE(1), INDEXED_COLOR(2), RGB_COLOR(3), CYMK_COLOR(4), MULTICHANNEL_COLOR(5), DUOTONE(
        8), LAB_COLOR(9), UNKNOWN(-1);

    int id;

    MODE(int id) {
      this.id = id;
    }

    static MODE get(int id) {
      switch (id) {
        case 0:
          return BITMAP_MONOCHROME;
        case 1:
          return GRAY_SCALE;
        case 2:
          return INDEXED_COLOR;
        case 3:
          return RGB_COLOR;
        case 4:
          return CYMK_COLOR;
        case 5:
          return MULTICHANNEL_COLOR;
        case 6:
          return DUOTONE;
        case 7:
          return LAB_COLOR;
        default:
          return UNKNOWN;
      }
    }

    int getId() {
      return id;
    }

  }

  public static enum VERSION {
    PSD(1), PSB(2), UNKNOWN(-1);

    int id;

    VERSION(int id) {
      this.id = id;
    }

    static VERSION get(int id) {
      switch (id) {
        case 1:
          return PSD;
        case 2:
          return PSB;
        default:
          return UNKNOWN;
      }
    }

    int getId() {
      return id;
    }
  }

  private String signature = "8BPS";
  private VERSION version;
  private byte[] reserved = new byte[]{0, 0, 0, 0, 0, 0};
  private short channels;
  private int rows;
  private int columns;
  private short depth;
  private MODE mode;

  public PsdHeader() {

  }

  public PsdHeader(PsdHeader source) {
    this.signature = source.signature;
    this.version = source.version;
    this.reserved = ZJArrayUtils.copy(source.reserved);
    this.channels = source.channels;
    this.rows = source.rows;
    this.columns = source.columns;
    this.depth = source.depth;
    this.mode = source.mode;
  }

  public int getPixelSize() {
    return (depth / 8) * channels;
  }

  @Override
  public long read(FileImageInputStream is) throws IOException {
    long res = 0;

    final byte[] temp = new byte[4];
    is.read(temp);
    signature = new String(temp);
    res += 4;

    version = VERSION.get(is.readShort());
    res += 2;

    is.read(reserved);
    res += reserved.length;

    channels = is.readShort();
    res += 2;

    rows = is.readInt();
    res += 4;

    columns = is.readInt();
    res += 4;

    depth = is.readShort();
    res += 2;

    mode = MODE.get(is.readShort());
    res += 2;

    return res;
  }

  @Override
  public void write(FileImageOutputStream os) throws IOException {
    os.write(signature.getBytes());
    os.writeShort(version.getId());
    os.write(reserved);
    os.writeShort(channels);
    os.writeInt(rows);
    os.writeInt(columns);
    os.writeShort(depth);
    os.writeShort(mode.getId());
  }

  public String getSignature() {
    return this.signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public VERSION getVersion() {
    return this.version;
  }

  public void setVersion(VERSION version) {
    this.version = version;
  }

  public byte[] getReserveds() {
    return this.reserved;
  }

  public void setReserveds(byte[] reserveds) {
    this.reserved = reserveds;
  }

  public short getChannels() {
    return this.channels;
  }

  public void setChannels(short channels) {
    this.channels = channels;
  }

  public int getRows() {
    return this.rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getColumns() {
    return this.columns;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public short getDepth() {
    return this.depth;
  }

  public void setDepth(short depth) {
    this.depth = depth;
  }

  public MODE getMode() {
    return this.mode;
  }

  public void setMode(MODE mode) {
    this.mode = mode;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
