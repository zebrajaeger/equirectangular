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
  public void read(FileImageInputStream is) throws IOException {
    length = is.readInt();
    colorData = new byte[length];
    is.read(colorData);
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
