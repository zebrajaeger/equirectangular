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

import de.zebrajaeger.equirectangular.autopano.GPanoData;
import de.zebrajaeger.equirectangular.psd.PsdImageData;
import de.zebrajaeger.equirectangular.psd.PsdImg;
import de.zebrajaeger.equirectangular.ui.FileDropper;
import de.zebrajaeger.equirectangular.ui.IFileDropListener;
import de.zebrajaeger.equirectangular.util.ZJFileUtils;
import de.zebrajaeger.equirectangular.util.ZJLogUtil;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

public class App {

  public final static String TARGET_FILE_POSTFIX = "_full";

  // http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/

  // http://www.fileformat.info/format/psd/egff.htm
  private static Logger LOG = LogManager.getLogger(App.class);

  public static void main(String[] args) throws IOException, ParseException {
    new App().perform(args);
  }

  private CLIArgs cli;

  public void perform(String[] args) throws IOException, ParseException {

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

    if (cli.isUseUi()) {
      FileDropper fd = new FileDropper();
      fd.addListener(new IFileDropListener() {
        @Override
        public boolean onAcceptDrop(List<File> files) {
          for (File f : files) {
            String name = f.getName().toLowerCase();
            if (!(name.endsWith(".psd") || name.endsWith(".psb"))) {
              return false;
            }
          }
          return true;
        }

        @Override
        public void onDrop(List<File> files) {
          for (File f : files) {
            try {
              File target = chooseTargetFile(f, TARGET_FILE_POSTFIX, cli.isDeleteIfExists());
              LOG.info(String.format("start processing file '%s' to '%s'",f, target));
              processImage(dry, f, target);
            } catch (ParseException | IOException e) {
              String msg = String.format("could not process image '%s'", e);
              LOG.error(msg, e);
            }
          }
        }
      });
    } else {
      // check target file
      File source = cli.getSource();
      File target = cli.getTarget();
      if (target == null) {
        target = chooseTargetFile(source, TARGET_FILE_POSTFIX, cli.isDeleteIfExists());
      }

      if (target.exists()) {
        if (cli.isDeleteIfExists()) {
          LOG.info(String.format("Delete file '%s'", target.getAbsolutePath()));
          if (dry) {
            LOG.info("  But while dry-run nothing happens...");
          }
        } else {
          final String msg =
              String.format("Target file '%s' exists but option for overwriting is false", target
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

      processImage(dry, source, target);
    }
  }

  protected void processImage(boolean dry, File source, File target) throws IOException {
    try (FileImageInputStream is = new FileImageInputStream(source);
         FileImageOutputStream os = (dry) ? null : new FileImageOutputStream(target)) {

      // create source image
      final PsdImg sourceImg = new PsdImg();
      sourceImg.prepareRead(is);
      LOG.debug(String.format("Source-Image: %n%s", sourceImg));

      final int source_w = sourceImg.getHeader().getColumns();
      final int source_h = sourceImg.getHeader().getRows();
      LOG.info(String.format("Source-Image w:%s, h:%s", source_w, source_h));

      // prepare render positions
      RenderParameters rp = null;

      // prepare render positions - CLI
      if (cli.getW() != null) {
        rp = RenderParameters.Builder.buildWithWH(source_w, source_h, cli.getW(), cli.getY());
        LOG.info("Taking command-line-arguments for target rendering");
      }

      // prepare render positions - AUTOPANO
      final GPanoData panoData = sourceImg.getImageResourceSection().getGPanoData();
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
      final PsdImg targetImg = new PsdImg(sourceImg);
      targetImg.getHeader().setColumns(rp.getTarget_w());
      targetImg.getHeader().setRows(rp.getTarget_h());
      if (!dry) {
        targetImg.prepareWrite(os);
      }

      LOG.debug(String.format("Target image: %n%s", targetImg));

      final int lineSize = targetImg.getHeader().getColumns();
      final int imgSize = targetImg.getHeader().getRows() * targetImg.getHeader().getPixelSize() * lineSize;
      LOG.info(String.format("resultimgSize ~ %sM", ZJFileUtils.humanReadableByteCount(imgSize, false)));

      final byte[] buffer = new byte[lineSize];
      LOG.debug(String.format("Linebuffer.length=%s", buffer.length));

      final int channels = targetImg.getHeader().getChannels();
      if (!dry) {
        for (int i = 1; i <= channels; ++i) {
          final byte spaceValue = (byte) ((i == channels) ? 255 : 0);
          LOG.info(String.format("WRITE LAYER %s with space %s", i, spaceValue));
          writeSpaceLines(buffer, targetImg.getImgData(), spaceValue, rp.getSource_off_y_top());
          copyLines(buffer, sourceImg.getImgData(), targetImg.getImgData(), spaceValue, rp.getTarget_w(),
                    rp.getSource_off_x(), rp.getSource_w(), rp.getSource_h());
          writeSpaceLines(buffer, targetImg.getImgData(), spaceValue, rp.getSource_off_y_bot());
        }
      }

      LOG.info(String.format("krPano view snippet: %n%s", rp.krPanoSnippt()));
      LOG.info("finished");
    }
  }

  protected static void copyLines(byte[] buffer, PsdImageData source, PsdImageData target, byte spaceValue, int targetW,
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

  protected static void writeSpaceLines(byte[] buffer, PsdImageData target, byte value, int n) throws IOException {
    LOG.info(String.format("write SPACE LINES: %s", n));
    Arrays.fill(buffer, value);
    for (int i = 0; i < n; ++i) {
      target.write(buffer);
    }
  }

  /**
   * if no target file is given, compute one that not exists from input file name
   */
  protected File chooseTargetFile(File source, String postfix, boolean deleteIfExists) throws ParseException {
    File target = null;
    final String filename = source.getName();
    final int pos = filename.lastIndexOf('.');
    String name = filename;
    String ext = "";
    if (pos != -1) {
      ext = filename.substring(pos);
      name = filename.substring(0, pos);
    }

    for (int nr = -1; (target == null) || (!deleteIfExists && target.exists()); nr++) {
      final String n =
          (nr == -1) ? String.format("%s%s%s", name, postfix, ext) : String.format("%s%s_%04d%s", name,
                                                                                   postfix, nr, ext);
      target = new File(source.getParent(), n);
    }

    return target;
  }

}
