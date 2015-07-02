package de.zebrajaeger.equirectangular.psd;

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

public interface IPsdMetaDataPart {
  void read(FileImageInputStream is) throws IOException;

  void write(FileImageOutputStream os) throws IOException;
}
