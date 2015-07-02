package de.zebrajaeger.equirectangular.psd;

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PsdImageData {
  private TYPE type;
  private FileImageInputStream is = null;
  private FileImageOutputStream os = null;

  public static enum TYPE {
    RAW(0), RLE(1), ZIP_WITHOUT_PREDICTION(2), ZIP_WITH_PREDICTION(3), UNKNOWN(-1);

    int id;

    TYPE(int id) {
      this.id = id;
    }

    static TYPE get(int id) {
      switch (id) {
        case 0:
          return RAW;
        case 1:
          return RLE;
        case 2:
          return ZIP_WITHOUT_PREDICTION;
        case 3:
          return ZIP_WITH_PREDICTION;
        default:
          return UNKNOWN;
      }
    }

    int getId() {
      return id;
    }
  }

  public PsdImageData() {

  }

  public PsdImageData(PsdImageData source) {
    this.type = source.type;
  }

  public void prepareWrite(FileImageOutputStream os) throws IOException {
    this.os = os;
    os.writeShort(type.getId());
  }

  public void prepareRead(FileImageInputStream is) throws IOException {
    this.is = is;
    type = TYPE.get(is.readShort());
  }

  public int read(byte[] val) throws IOException {
    return is.read(val);
  }

  public int read(byte[] val, int off, int len) throws IOException {
    return is.read(val, off, len);
  }

  public void write(byte[] val) throws IOException {
    os.write(val);
  }

  public void write(byte[] val, int off, int len) throws IOException {
    os.write(val, off, len);
  }

  void close() throws IOException {
    if (is != null) {
      is.close();
    }
    if (os != null) {
      os.flush();
      os.close();
    }
  }



  public TYPE getType() {
    return this.type;
  }

  public void setType(TYPE type) {
    if (type != TYPE.RAW) {
      throw new IllegalArgumentException(String.format("The type '%s' in not supported", type));
    }
    this.type = type;
  }

  public FileImageInputStream getIs() {
    return this.is;
  }

  public FileImageOutputStream getOs() {
    return this.os;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
