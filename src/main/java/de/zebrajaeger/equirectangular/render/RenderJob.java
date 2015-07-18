package de.zebrajaeger.equirectangular.render;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;

/**
 * Created by lars on 11.07.2015.
 */
public class RenderJob {

  private File source;
  private File target;
  private boolean overrideTarget;
  private boolean dry;
  private RenderParameters renderParameters;
  private IJobListener jobListener;

  public File getSource() {
    return source;
  }

  public void setSource(File source) {
    this.source = source;
  }

  public File getTarget() {
    return target;
  }

  public void setTarget(File target) {
    this.target = target;
  }

  public boolean isDry() {
    return dry;
  }

  public void setDry(boolean dry) {
    this.dry = dry;
  }

  public RenderParameters getRenderParameters() {
    return renderParameters;
  }

  public void setRenderParameters(RenderParameters renderParameters) {
    this.renderParameters = renderParameters;
  }

  public IJobListener getJobListener() {
    return jobListener;
  }

  public void setJobListener(IJobListener jobListener) {
    this.jobListener = jobListener;
  }

  public boolean isOverrideTarget() {
    return overrideTarget;
  }

  public void setOverrideTarget(boolean overrideTarget) {
    this.overrideTarget = overrideTarget;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}