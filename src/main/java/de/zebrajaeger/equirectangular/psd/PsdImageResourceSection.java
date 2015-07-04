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
