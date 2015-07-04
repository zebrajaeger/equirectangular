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
  public long read(FileImageInputStream is) throws IOException {
    long res = 0;
    size = is.readInt();
    res += 4;


    if (size > 0) {
      System.out.println("##### DEBUG size:" + size);
      long temp = 1;
      for (; temp < size;) {
        final PsdImageResourceBlock irb = new PsdImageResourceBlock();
        final long bytes = irb.read(is);
        System.out.println("------------------------------");
        System.out.println(irb);
        System.out.println("------------------------------");
        System.out.println("##### DEBUG ridden:" + temp);
        res += bytes;
        temp += bytes;
        System.out.println("##### DEBUG temp is:" + temp);
      }
    }


    // data = new byte[size];
    // is.read(data);
    // res += data.length;

    return res;
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
