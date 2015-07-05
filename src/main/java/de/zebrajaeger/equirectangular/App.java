package de.zebrajaeger.equirectangular;

import java.io.IOException;
import java.util.Arrays;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.zebrajaeger.equirectangular.autopano.GPanoData;
import de.zebrajaeger.equirectangular.psd.PsdImageData;
import de.zebrajaeger.equirectangular.psd.PsdImg;
import de.zebrajaeger.equirectangular.util.ZJFileUtils;

public class App {

  // http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/

  // http://www.fileformat.info/format/psd/egff.htm
  private static Logger LOG = LogManager.getLogger(App.class);

  public static void main(String[] args) throws IOException {
    new App().perform(args);
  }

  private CLIArgs cli;

  public void perform(String[] args) throws IOException {
    System.out.println(Arrays.toString(args));

    // parse args
    try {
      cli = CLIArgs.Builder.build(args);
    } catch (final ParseException e) {
      LOG.error(e.getMessage());
    }

    // config LOG4j
    if (cli.getLevel() != null) {
      final org.apache.logging.log4j.core.Logger rootLogger =
          (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
      rootLogger.setLevel(cli.getLevel());
    }

    // check target file
    if (cli.getTarget().exists()) {
      if (cli.isDeleteIfExists()) {
        LOG.info(String.format("Delete file '%s'", cli.getTarget().getAbsolutePath()));
        if (cli.isDryRun()) {
          LOG.info("  But while dry-run nothing happens...");
        }
      } else {
        final String msg =
            String.format("Target file '%s' exists but option for overwriting is false", cli.getTarget()
                .getAbsolutePath());
        LOG.error(msg);
        if (cli.isDryRun()) {
          LOG.info("  But while dry-run we can proceed nothing happens...");
        } else {
          LOG.error("Exiting");
          return;
        }
      }
    }

    try (FileImageInputStream is = new FileImageInputStream(cli.getSource());
        FileImageOutputStream os = new FileImageOutputStream(cli.getTarget())) {

      // create source image
      final PsdImg source = new PsdImg();
      source.prepareRead(is);
      LOG.info(String.format("Source-Image:\n ", source));

      final int source_w = source.getHeader().getColumns();
      final int source_h = source.getHeader().getRows();
      LOG.info(String.format("Source-Image w:%s, h:%s", source_w, source_h));

      // prepare render positions
      RenderParameters pc = null;

      // prepare render positions - CLI
      if (cli.getW() != null) {
        pc = RenderParameters.Builder.buildWithWH(source_w, source_h, cli.getW(), cli.getY());
        LOG.info("Taking command-line-arguments for target rendering");
      }

      // prepare render positions - AUTOPANO
      final GPanoData panoData = source.getImageResourceSection().getGPanoData();
      if (panoData != null) {
        LOG.info(String.format("Found Render data from Autopano:\n %s", panoData));
        if (cli.getW() == null) {
          LOG.info("No command line option for target rendering avaliable. Use Autopano XMP data");
        }
      } else {
        LOG.info("No Render data from Autopano available");
      }

      if (pc == null) {
        LOG.error("Neither w options is given nor Autopano XMP-Data is available. Exiting");
        return;
      }

      LOG.info("Computeted render values: \n %s", pc);

      // create target image
      final PsdImg target = new PsdImg(source);
      target.getHeader().setColumns(pc.getTarget_w());
      target.getHeader().setRows(pc.getTarget_h());
      if (!cli.isDryRun()) {
        target.prepareWrite(os);
      }

      LOG.info("Target image: \n %s", target);

      final int lineSize = target.getHeader().getColumns();
      final int imgSize = target.getHeader().getRows() * target.getHeader().getPixelSize() * lineSize;
      LOG.info(String.format("resultimgSize ~ %sM", ZJFileUtils.humanReadableByteCount(imgSize, false)));

      final byte[] buffer = new byte[lineSize];
      LOG.info(String.format("Linebuffer.length=%s", buffer.length));

      final int channels = target.getHeader().getChannels();
      if (!cli.isDryRun()) {
        for (int i = 1; i <= channels; ++i) {
          final byte spaceValue = (byte) ((i == channels) ? 255 : 0);
          LOG.info(String.format("WRITE LAYER %s with space %s", i, spaceValue));
          writeSpaceLines(buffer, target.getImgData(), spaceValue, pc.getSource_off_y_top());
          copyLines(buffer, source.getImgData(), target.getImgData(), spaceValue, pc.getTarget_w(),
              pc.getSource_off_x(), pc.getSource_w(), pc.getSource_h());
          writeSpaceLines(buffer, target.getImgData(), spaceValue, pc.getSource_off_y_bot());
        }
      }

      LOG.info(String.format("krPano view snippet: \n%s", pc.krPanoSnippt()));
      LOG.info("finished");
    }
  }

  private static void copyLines(byte[] buffer, PsdImageData source, PsdImageData target, byte spaceValue, int targetW,
      int offX, int srcW, int srcH) throws IOException {
    LOG.info(String.format("write COPY LINES: %s (start: %s)", srcH, offX));
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
    LOG.info(String.format("write SPACE LINES: %s", n));
    Arrays.fill(buffer, value);
    for (int i = 0; i < n; ++i) {
      target.write(buffer);
    }
  }

}
