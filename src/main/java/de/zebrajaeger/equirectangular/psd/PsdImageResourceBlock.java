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
import java.nio.charset.StandardCharsets;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import de.zebrajaeger.equirectangular.autopano.GPanoData;

public class PsdImageResourceBlock implements IPsdMetaDataPart {

  private static Logger LOG = LogManager.getLogger(PsdImageResourceBlock.class);
  private transient byte[] signature = {'8', 'B', 'I', 'M'};
  private short uid;
  private PascalString name;
  private int size;
  private transient byte[] data;
  private Object decodedData = null;

  @Override
  public long read(FileImageInputStream is) throws IOException {
    long res = 0;

    this.signature = new byte[4];
    is.read(this.signature);
    res += 4;

    this.uid = is.readShort();
    res += 2;

    name = new PascalString();
    res += name.read(is);

    size = is.readInt();
    res += 4;

    data = new byte[size];
    is.read(data);
    res += data.length;

    decodeData();
    return res;
  }

  protected void decodeData() {
    if (uid == 1058) {
      // EXIF
      // decodedData = new String(data, StandardCharsets.US_ASCII);
    } else if (uid == 1060) {
      // XMP see http://www.w3.org/RDF/Validator/
      decodedData = new String(data, StandardCharsets.US_ASCII);
      try {
        decodedData = GPanoData.Builder.buildFrombytes(data);
      } catch (final SAXException | IOException | ParserConfigurationException e) {
        LOG.error("could not parse XMP-Pano Data", e);
      }
    }
  }

  @Override
  public void write(FileImageOutputStream os) throws IOException {
    os.write(signature);
    os.writeShort(uid);
    name.write(os);
    os.writeInt(size);
    os.write(data);
  }

  public byte[] getSignature() {
    return this.signature;
  }

  public short getUid() {
    return this.uid;
  }

  public PascalString getName() {
    return this.name;
  }

  public int getSize() {
    return this.size;
  }

  public byte[] getData() {
    return this.data;
  }

  public Object getDecodedData() {
    return this.decodedData;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
