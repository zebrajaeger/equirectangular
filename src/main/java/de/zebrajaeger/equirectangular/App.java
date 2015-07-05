/*
 * Copyright (c) 2015, Lars Brandt. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
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
import de.zebrajaeger.equirectangular.util.ZJLogUtil;

public class App {

  // http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/

  // http://www.fileformat.info/format/psd/egff.htm
  private static Logger LOG = LogManager.getLogger(App.class);

  public static void main(String[] args) throws IOException {
    new App().perform(args);
  }

  private CLIArgs cli;

  public void perform(String[] args) throws IOException {

    // parse args
    try {
      cli = CLIArgs.Builder.build(args);
    } catch (final ParseException e) {
      LOG.error(e.getMessage());
      return;
    }

    if (cli == null) {
      return;
    }

    final boolean dry = cli.isDryRun();
    // config LOG4j
    if (cli.getLevel() != null) {
      ZJLogUtil.changeLogLevel(cli.getLevel());
    }

    // check target file
    if (cli.getTarget().exists()) {
      if (cli.isDeleteIfExists()) {
        LOG.info(String.format("Delete file '%s'", cli.getTarget().getAbsolutePath()));
        if (dry) {
          LOG.info("  But while dry-run nothing happens...");
        }
      } else {
        final String msg =
            String.format("Target file '%s' exists but option for overwriting is false", cli.getTarget()
                .getAbsolutePath());
        LOG.error(msg);
        if (dry) {
          LOG.info("  But while dry-run we can proceed nothing happens...");
        } else {
          LOG.error("Exiting");
          return;
        }
      }
    }

    try (FileImageInputStream is = new FileImageInputStream(cli.getSource());
        FileImageOutputStream os = (dry) ? null : new FileImageOutputStream(cli.getTarget())) {

      // create source image
      final PsdImg source = new PsdImg();
      source.prepareRead(is);
      LOG.debug(String.format("Source-Image: %n%s", source));

      final int source_w = source.getHeader().getColumns();
      final int source_h = source.getHeader().getRows();
      LOG.info(String.format("Source-Image w:%s, h:%s", source_w, source_h));

      // prepare render positions
      RenderParameters rp = null;

      // prepare render positions - CLI
      if (cli.getW() != null) {
        rp = RenderParameters.Builder.buildWithWH(source_w, source_h, cli.getW(), cli.getY());
        LOG.info("Taking command-line-arguments for target rendering");
      }

      // prepare render positions - AUTOPANO
      final GPanoData panoData = source.getImageResourceSection().getGPanoData();
      if (panoData != null) {
        LOG.info(String.format("Found Render data from Autopano:%n %s", panoData));
        if (cli.getW() == null) {
          LOG.info("No command line option for target rendering avaliable. Use Autopano XMP data");
          rp = RenderParameters.Builder.buildFromAutopano(panoData);
        }
      } else {
        LOG.info("No Render data from Autopano available");
      }

      if (rp == null) {
        LOG.error("Neither w options is given nor Autopano XMP-Data is available. Exiting");
        return;
      }

      LOG.info(String.format("Computeted render values: %n%s", rp));

      // create target image
      final PsdImg target = new PsdImg(source);
      target.getHeader().setColumns(rp.getTarget_w());
      target.getHeader().setRows(rp.getTarget_h());
      if (!dry) {
        target.prepareWrite(os);
      }

      LOG.debug(String.format("Target image: %n%s", target));

      final int lineSize = target.getHeader().getColumns();
      final int imgSize = target.getHeader().getRows() * target.getHeader().getPixelSize() * lineSize;
      LOG.info(String.format("resultimgSize ~ %sM", ZJFileUtils.humanReadableByteCount(imgSize, false)));

      final byte[] buffer = new byte[lineSize];
      LOG.debug(String.format("Linebuffer.length=%s", buffer.length));

      final int channels = target.getHeader().getChannels();
      if (!dry) {
        for (int i = 1; i <= channels; ++i) {
          final byte spaceValue = (byte) ((i == channels) ? 255 : 0);
          LOG.info(String.format("WRITE LAYER %s with space %s", i, spaceValue));
          writeSpaceLines(buffer, target.getImgData(), spaceValue, rp.getSource_off_y_top());
          copyLines(buffer, source.getImgData(), target.getImgData(), spaceValue, rp.getTarget_w(),
              rp.getSource_off_x(), rp.getSource_w(), rp.getSource_h());
          writeSpaceLines(buffer, target.getImgData(), spaceValue, rp.getSource_off_y_bot());
        }
      }

      LOG.info(String.format("krPano view snippet: %n%s", rp.krPanoSnippt()));
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
