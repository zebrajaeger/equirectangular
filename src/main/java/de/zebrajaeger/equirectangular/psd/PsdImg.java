package de.zebrajaeger.equirectangular.psd;

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.zebrajaeger.equirectangular.psd.PsdHeader.MODE;
import de.zebrajaeger.equirectangular.psd.PsdHeader.VERSION;
import de.zebrajaeger.equirectangular.psd.PsdImageData.TYPE;


/**
 * see https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/
 * 
 * @author Lars Brandt
 *
 */
public class PsdImg {

  private PsdHeader header;
  private PsdColorModeData colorModeData;
  private PsdImageResourceSection imageResourceSection;
  private PsdLayerAndMaskInformation layerAndMask;
  private PsdImageData imgData;

  public PsdImg() {}

  public PsdImg(FileImageInputStream is) throws IOException {
    prepareRead(is);
  }


  public PsdImg(PsdImg source) {
    this.header = new PsdHeader(source.header);
    this.colorModeData = new PsdColorModeData(source.colorModeData);
    this.imageResourceSection = new PsdImageResourceSection(source.imageResourceSection);
    this.layerAndMask = new PsdLayerAndMaskInformation(source.layerAndMask);
    this.imgData = new PsdImageData(source.imgData);
  }

  public void prepareRead(FileImageInputStream is) throws IOException {

    header = new PsdHeader();
    header.read(is);

    colorModeData = new PsdColorModeData();
    colorModeData.read(is);

    imageResourceSection = new PsdImageResourceSection();
    imageResourceSection.read(is);
    System.out.println("####debug: streampos after PsdImageResourceSection: " + is.getStreamPosition());

    layerAndMask = new PsdLayerAndMaskInformation(header.getVersion());
    layerAndMask.read(is);

    imgData = new PsdImageData();
    imgData.prepareRead(is);
  }

  public void prepareWrite(FileImageOutputStream os) throws IOException {
    header.write(os);
    colorModeData.write(os);
    imageResourceSection.write(os);
    layerAndMask.write(os);
    imgData.prepareWrite(os);
  }

  public void close() throws IOException {
    imgData.close();
  }

  public PsdHeader getHeader() {
    return this.header;
  }

  public void setHeader(PsdHeader header) {
    this.header = header;
  }

  public PsdColorModeData getColorModeData() {
    return this.colorModeData;
  }

  public void setColorModeData(PsdColorModeData colorModeData) {
    this.colorModeData = colorModeData;
  }

  public PsdImageResourceSection getImageResourceSection() {
    return this.imageResourceSection;
  }

  public void setImageResourceSection(PsdImageResourceSection imageResourceSection) {
    this.imageResourceSection = imageResourceSection;
  }

  public PsdLayerAndMaskInformation getLayerAndMask() {
    return this.layerAndMask;
  }

  public void setLayerAndMask(PsdLayerAndMaskInformation layerAndMask) {
    this.layerAndMask = layerAndMask;
  }

  public PsdImageData getImgData() {
    return this.imgData;
  }

  public void setImgData(PsdImageData imgData) {
    this.imgData = imgData;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  public static class Builder {
    public static PsdImg buildPsd(int w, int h) {
      final VERSION v = VERSION.PSD;
      final PsdImg img = new PsdImg();
      final PsdHeader header = new PsdHeader();

      img.setHeader(header);
      header.setChannels((short) 4);
      header.setDepth((short) 8);
      header.setColumns(w);
      header.setRows(h);
      header.setMode(MODE.RGB_COLOR);
      header.setVersion(v);

      img.setColorModeData(new PsdColorModeData());
      img.setImageResourceSection(new PsdImageResourceSection());

      img.setLayerAndMask(new PsdLayerAndMaskInformation(v));
      final PsdImageData id = new PsdImageData();
      id.setType(TYPE.RAW);
      img.setImgData(id);

      return img;
    }
  }

}
