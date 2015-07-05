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

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import de.zebrajaeger.equirectangular.util.ZJArrayUtils;

public class PsdColorModeData implements IPsdMetaDataPart {

  private int length = 0;
  private byte[] colorData = new byte[0];

  public PsdColorModeData() {

  }

  public PsdColorModeData(PsdColorModeData source) {
    this.length = source.length;

    this.colorData = ZJArrayUtils.copy(source.colorData);
  }

  @Override
  public long read(FileImageInputStream is) throws IOException {
    long res = 0;

    length = is.readInt();
    res += 4;

    colorData = new byte[length];
    is.read(colorData);

    res += colorData.length;

    return res;
  }

  @Override
  public void write(FileImageOutputStream os) throws IOException {
    os.writeInt(colorData.length);
    os.write(colorData);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
