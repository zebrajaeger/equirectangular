package de.zebrajaeger.equirectangular;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import de.zebrajaeger.equirectangular.psd.PsdImg;

public class CreateTest {

  public static void main(String[] args) throws IOException {
    final File f = new File("r:/temp/create.psd");
    if (f.exists()) {
      f.delete();
      if (f.exists()) {
        throw new IllegalStateException(String.format("File '%s'", f.getAbsolutePath()));
      }
    }

    try (FileImageOutputStream os = new FileImageOutputStream(f)) {
      final PsdImg img = PsdImg.Builder.buildPsd(100, 100);
      System.out.println(img);
      img.prepareWrite(os);

      final byte[] first = new byte[1];
      first[0] = (byte) 255;


      for (int k = 0; k < 4; ++k) {
        if (k == 3) {
          first[0] = (byte) 255;
        } else {
          first[0] = 0;
        }
        for (int i = 0; i < 100; ++i) {
          for (int j = 0; j < 100; ++j) {
            img.getImgData().write(first);
          }
        }
      }

      os.flush();
    }
  }
}
