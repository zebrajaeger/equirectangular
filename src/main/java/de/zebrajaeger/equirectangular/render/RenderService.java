package de.zebrajaeger.equirectangular.render;

import de.zebrajaeger.equirectangular.CLIArgs;
import de.zebrajaeger.equirectangular.autopano.GPanoData;
import de.zebrajaeger.equirectangular.psd.PsdImg;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.stream.FileImageInputStream;

/**
 * Created by Lars Brandt
 */
public class RenderService {

  private LinkedList<RenderJob> jobs = new LinkedList<>();

  protected void putJob(RenderJob job) {
    jobs.addFirst(job);
  }

  protected RenderJob getJob() {
    return jobs.pollLast();
  }

  public void createJob(CLIArgs cli, File source, File target, IJobListener listener) throws IOException {
    PsdImg imageHeader = getImageHeader(source);

    RenderJob job = null;

    // ob from command line
    if (cli.getW() != null) {
      final int source_w = imageHeader.getHeader().getColumns();
      final int source_h = imageHeader.getHeader().getRows();
      RenderParameters rp = RenderParameters.Builder.buildWithWH(source_w, source_h, cli.getW(), cli.getY());
      job = new RenderJob();
      job.setDry(cli.isDryRun());
      job.setJobListener(listener);
      job.setRenderParameters(rp);
      job.setSource(source);
      job.setTarget(target);
    }

    if (cli == null) {
      job = createAutopanoJob(false, source, target, listener);
    } else {

    }
  }

  public void createCLIJob(boolean dry, File source, File target, IJobListener listener) throws IOException {
    PsdImg imageHeader = getImageHeader(source);
    final int source_w = imageHeader.getHeader().getColumns();
    final int source_h = imageHeader.getHeader().getRows();
    LOG.info(String.format("Source-Image w:%s, h:%s", source_w, source_h));

    // prepare render positions
    RenderParameters rp = null;

    // prepare render positions - CLI
    if (cli.getW() != null) {
      rp = RenderParameters.Builder.buildWithWH(source_w, source_h, cli.getW(), cli.getY());
    }

  public RenderJob createAutopanoJob(boolean dry, File source, File target, IJobListener listener) throws IOException {
    PsdImg imageHeader = getImageHeader(source);
    final GPanoData panoData = imageHeader.getImageResourceSection().getGPanoData();
    if (panoData != null) {
      RenderParameters rp = RenderParameters.Builder.buildFromAutopano(panoData);
      RenderJob job = new RenderJob();
      job.setDry(dry);
      job.setJobListener(listener);
      job.setRenderParameters(rp);
      job.setSource(source);
      job.setTarget(target);
      return job;
    }
    return null;
  }

  protected PsdImg getImageHeader(File source) throws IOException {
    try (FileImageInputStream is = new FileImageInputStream(source)) {
      final PsdImg sourceImg = new PsdImg();
      sourceImg.prepareRead(is);
      return sourceImg;
    }
  }
}

}
