package de.zebrajaeger.equirectangular.psd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PsdImageResourceBlock implements IPsdMetaDataPart {

  private byte[] signature = {'8', 'B', 'I', 'M'};
  private short uid;
  private PascalString name;
  private int size;
  private transient byte[] data;
  private Object decodedData = null;

  @Override
  public long read(FileImageInputStream is) throws IOException {
    // System.out.println("#### DEBUG start stream at: " + is.getStreamPosition());
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
    // System.out.println("#### DEBUG END stream at: " + is.getStreamPosition());
    return res;
  }

  protected void decodeData() {
    if (uid == 1058) {
      // EXIF
      // decodedData = new String(data, StandardCharsets.US_ASCII);
    }
    if (uid == 1060) {
      // XMP
      decodedData = new String(data, StandardCharsets.US_ASCII);
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

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
