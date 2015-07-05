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
import java.util.LinkedList;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import de.zebrajaeger.equirectangular.autopano.GPanoData;

public class PsdImageResourceSection implements IPsdMetaDataPart {

  private int size = 0;
  private final LinkedList<PsdImageResourceBlock> blocks = new LinkedList<>();

  // private byte[] data = new byte[0];

  public PsdImageResourceSection() {

  }

  public PsdImageResourceSection(PsdImageResourceSection source) {
    this.size = source.size;
    // this.data = ZJArrayUtils.copy(source.data);
  }

  @Override
  public long read(FileImageInputStream is) throws IOException {
    long res = 0;
    size = is.readInt();
    res += 4;

    if (size > 0) {
      long temp = 1;
      for (; temp < size;) {
        final PsdImageResourceBlock irb = new PsdImageResourceBlock();
        final long bytes = irb.read(is);
        blocks.add(irb);
        res += bytes;
        temp += bytes;
      }
    }

    return res;
  }

  @Override
  public void write(FileImageOutputStream os) throws IOException {
    int size = 0;
    for (final PsdImageResourceBlock b : blocks) {
      size += b.getSize();
    }
    os.writeInt(size);

    for (final PsdImageResourceBlock b : blocks) {
      b.write(os);
    }
  }

  public GPanoData getGPanoData() {
    for (final PsdImageResourceBlock b : blocks) {
      if (b.getDecodedData() instanceof GPanoData) {
        return (GPanoData) b.getDecodedData();
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
