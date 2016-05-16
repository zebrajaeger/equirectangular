package de.zebrajaeger.psdpreview;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Keeps all the pixels of the preview image
 * <p>
 * Created by lars on 14.05.2016.
 */
public class PreviewData {
    private int width;
    private int height;
    private PreviewPixel[] data;

    public PreviewData(int width, int height) {
        this.width = width;
        this.height = height;

        data = new PreviewPixel[width * height];
        for (int i = 0; i < data.length; ++i) {
            data[i] = new PreviewPixel();
        }
    }

    public PreviewPixel get(int x, int y) {
        return data[(y * width) + x];
    }

    public void addToR(int x, int y, int value) {
        get(x, y).addToR(value);
    }

    public void addToG(int x, int y, int value) {
        get(x, y).addToG(value);
    }

    public void addToB(int x, int y, int value) {
        get(x, y).addToB(value);
    }

    public BufferedImage createImage() {
        BufferedImage img = new BufferedImage(width, height, ColorSpace.TYPE_RGB);
        WritableRaster raster = img.getRaster();
        int[] pixBuf = new int[3];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                PreviewPixel pixel = data[(y * width) + x];
                ;
                pixBuf[0] = pixel.getR().getNormalizedIntValue();
                pixBuf[1] = pixel.getG().getNormalizedIntValue();
                pixBuf[2] = pixel.getB().getNormalizedIntValue();
                raster.setPixel(x, y, pixBuf);
            }
        }
        return img;
    }
}
