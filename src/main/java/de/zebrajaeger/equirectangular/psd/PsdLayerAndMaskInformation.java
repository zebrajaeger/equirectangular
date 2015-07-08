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

import de.zebrajaeger.equirectangular.psd.PsdHeader.VERSION;
import de.zebrajaeger.equirectangular.util.ZJArrayUtils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

public class PsdLayerAndMaskInformation implements IPsdMetaDataPart {

  private long size = 0;
  private byte[] data = new byte[0];
  private PsdHeader.VERSION version = VERSION.PSD;

  public PsdLayerAndMaskInformation() {
  }

  public PsdLayerAndMaskInformation(PsdHeader.VERSION version) {
    this.version = version;
  }

  public PsdLayerAndMaskInformation(PsdLayerAndMaskInformation source) {
    this.size = source.size;
    this.data = ZJArrayUtils.copy(source.data);
    this.version = source.version;
  }

  @Override
  public long read(FileImageInputStream is) throws IOException {
    long res = 0;

    if (version.equals(PsdHeader.VERSION.PSD)) {
      size = is.readInt();
      res += 4;
    } else if (version.equals(PsdHeader.VERSION.PSB)) {
      size = is.readLong();
      res += 8;
    }

    if (size > Integer.MAX_VALUE) {
      final String msg =
          String.format(
              "LayerAndMaskInformation is too long for this implementation. Max size is %s Bytes but it is %s Bytes",
              Integer.MAX_VALUE, size);
      throw new IllegalArgumentException(msg);
    }

    data = new byte[(int) size];
    is.read(data);
    res += data.length;

    return res;
  }

  @Override
  public void write(FileImageOutputStream os) throws IOException {
    if (version.equals(PsdHeader.VERSION.PSD)) {
      os.writeInt(data.length);
    } else if (version.equals(PsdHeader.VERSION.PSB)) {
      os.writeLong(data.length);
    }

    os.write(data);
  }


  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
