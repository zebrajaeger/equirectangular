package de.zebrajaeger.equirectangular.psd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

public class PascalString implements IPsdMetaDataPart {

  private int size;
  private String string;

  @Override
  public long read(FileImageInputStream is) throws IOException {
    long res = 0;

    size = is.readByte() & 0xff;
    size += 1 - (size % 2);
    res += 1;

    final byte[] buffer = new byte[size];
    is.read(buffer);
    this.string = new String(buffer, StandardCharsets.US_ASCII);
    res += buffer.length;

    return res;
  }

  @Override
  public void write(FileImageOutputStream os) throws IOException {
    os.writeByte(size);
    os.write(string.getBytes());
  }

  @Override
  public String toString() {
    return string;
  }
}
