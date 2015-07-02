package de.zebrajaeger.equirectangular.psd;

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import de.zebrajaeger.equirectangular.util.ZJArrayUtils;

public class PsdImageResourceSection implements IPsdMetaDataPart {

  private int size = 0;
  private byte[] data = new byte[0];

  public PsdImageResourceSection() {

  }

  public PsdImageResourceSection(PsdImageResourceSection source) {
    this.size = source.size;
    this.data = ZJArrayUtils.copy(source.data);
  }

  @Override
  public void read(FileImageInputStream is) throws IOException {
    size = is.readInt();

    data = new byte[size];
    is.read(data);
  }

  @Override
  public void write(FileImageOutputStream os) throws IOException {
    os.writeInt(data.length);
    os.write(data);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
