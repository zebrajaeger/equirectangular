package de.zebrajaeger.equirectangular;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import de.zebrajaeger.equirectangular.psd.PsdImageData;
import de.zebrajaeger.equirectangular.psd.PsdImg;
import de.zebrajaeger.equirectangular.util.ZJFileUtils;

public class App {

  // http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/

  // http://www.fileformat.info/format/psd/egff.htm

  public static void main(String[] args2) throws IOException, InterruptedException {
    final boolean dryRun = true;

    final String in =
        "C:\\temp\\im\\(IMG_0833-IMG_0836-4)-{d=S-80.84x53.86(-8.27)}-{p=IMG_0833_IMG_0836-4 (2009-08-04)}.psd";
    final String out = "R:/temp/testout.psd";
    final File inFile = new File(in);
    if (!inFile.exists()) {
      throw new IllegalArgumentException(String.format("Image '%s' does not exist", inFile.getAbsolutePath()));
    }

    final File outFile = new File(out);

    if (outFile.exists()) {
      System.out.println(String.format("Delete file '%s'", outFile.getAbsolutePath()));
      if (!dryRun) {
        outFile.delete();
      }
    }

    if (!dryRun) {
      if (outFile.exists()) {
        throw new IllegalArgumentException(String.format("Image '%s' already exist", inFile.getAbsolutePath()));
      }
    }

    try (FileImageInputStream is = new FileImageInputStream(inFile);
        FileImageOutputStream os = new FileImageOutputStream(outFile)) {


      // create source image
      final PsdImg source = new PsdImg();
      source.prepareRead(is);
      System.out.println("\n#### SOURCE IMAGE ####");
      System.out.println(source);

      final int source_w = source.getHeader().getColumns();
      final int source_h = source.getHeader().getRows();
      System.out.println(String.format("source x:%s, y:%s", source_w, source_h));

      final double source_w_deg = 80.84;
      // final double source_h_deg = 53.86;
      final double source_ho_deg = -8.27;

      final PanoComputer pc = new PanoComputer(source_w, source_h, source_w_deg, source_ho_deg);
      System.out.println("\n#### COMPUTED VALUES ####");
      System.out.println(pc);

      // create target image
      final PsdImg target = new PsdImg(source);
      target.getHeader().setColumns(pc.getTarget_w());
      target.getHeader().setRows(pc.getTarget_h());
      if (!dryRun) {
        target.prepareWrite(os);
      }
      System.out.println("\n#### TARGET IMAGE ####");
      System.out.println(target);

      final int lineSize = target.getHeader().getColumns();
      final int imgSize = target.getHeader().getRows() * target.getHeader().getPixelSize() * lineSize;
      System.out.println(String.format("resultimgSize ~ %sM", ZJFileUtils.humanReadableByteCount(imgSize, false)));

      final byte[] buffer = new byte[lineSize];
      System.out.println(String.format("Linebuffer.length=%s", buffer.length));

      final int channels = target.getHeader().getChannels();
      if (!dryRun) {
        for (int i = 1; i <= channels; ++i) {
          final byte spaceValue = (byte) ((i == channels) ? 255 : 0);
          System.out.println(String.format("WRITE LAYER %s with space %s", i, spaceValue));
          writeSpaceLines(buffer, target.getImgData(), spaceValue, pc.getSource_off_y_top());
          copyLines(buffer, source.getImgData(), target.getImgData(), spaceValue, pc.getTarget_w(),
              pc.getSource_off_x(), pc.getSource_w(), pc.getSource_h());
          writeSpaceLines(buffer, target.getImgData(), spaceValue, pc.getSource_off_y_bot());
        }
      }

      System.out.println("\n#### KRPANO XML SNIPPET ####");
      System.out.println(pc.krPanoSnippt());
    }
  }

  private static void copyLines(byte[] buffer, PsdImageData source, PsdImageData target, byte spaceValue, int targetW,
      int offX, int srcW, int srcH) throws IOException {
    System.out.println(String.format("write COPY LINES: %s (start: %s)", srcH, offX));
    final byte x = spaceValue;

    // space left
    for (int i = 0; i < offX; ++i) {
      buffer[i] = x;
    }

    // space right
    for (int i = offX + srcW; i < targetW; ++i) {
      buffer[i] = x;
    }

    // copy content lines
    for (int i = 0; i < srcH; ++i) {
      source.read(buffer, offX, srcW);
      target.write(buffer);
    }
  }

  public static void writeSpaceLines(byte[] buffer, PsdImageData target, byte value, int n) throws IOException {
    System.out.println(String.format("write SPACE LINES: %s", n));
    Arrays.fill(buffer, value);
    for (int i = 0; i < n; ++i) {
      target.write(buffer);
    }
  }

}
