package de.zebrajaeger.equirectangular.core.psdpreview;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Keeps all the pixels of the preview image
 * <p>
 * @author Lars Brandt on 14.05.2016.
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
                pixBuf[0] = pixel.getR().getNormalizedIntValue();
                pixBuf[1] = pixel.getG().getNormalizedIntValue();
                pixBuf[2] = pixel.getB().getNormalizedIntValue();
                raster.setPixel(x, y, pixBuf);
            }
        }
        return img;
    }
}
