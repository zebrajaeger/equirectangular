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

import de.zebrajaeger.equirectangular.core.ProgressSource;
import de.zebrajaeger.equirectangular.core.psdimage.ReadablePsdImage;
import de.zebrajaeger.equirectangular.core.psdimage.linereader.LineReader;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Makes a preview Image from a Psd image
 * <p>
 *
 * @author Lars Brandt on 14.05.2016.
 */
public class PreviewGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(PreviewGenerator.class);

    private File sourceImage;

    private long maxPreviewWidth = 2048;
    private long maxPreviewHeight = 2048;
    private float compressionQuality = 0.8f;

    private Consumer<Progress> progressConsumer;

    public static PreviewGenerator of(File sourceImage) {
        return new PreviewGenerator(sourceImage);
    }

    private PreviewGenerator(File sourceImage) {
        this.sourceImage = sourceImage;
    }

    public PreviewGenerator renderPreview(File targetImage) throws IOException {
        renderPreview_(targetImage);
        return this;
    }

    public PreviewGenerator maxPreviewWidth(long maxPreviewWidth) {
        this.maxPreviewWidth = maxPreviewWidth;
        return this;
    }

    public PreviewGenerator maxPreviewHeight(long maxPreviewHeight) {
        this.maxPreviewHeight = maxPreviewHeight;
        return this;
    }

    public PreviewGenerator maxPreviewSize(long maxPreviewWidth, long maxPreviewHeight) {
        this.maxPreviewHeight = maxPreviewHeight;
        this.maxPreviewHeight = maxPreviewHeight;
        return this;
    }

    public PreviewGenerator progressConsumer(Consumer<Progress> progressConsumer) {
        this.progressConsumer = progressConsumer;
        return this;
    }

    public PreviewGenerator compressionQuality(float compressionQuality) {
        if (compressionQuality < 0f || compressionQuality > 1.0f) {
            throw new RuntimeException("compressionQuality must between 0..1");
        }
        this.compressionQuality = compressionQuality;
        return this;
    }

    private void renderPreview_(File targetImageFile) throws IOException {
        // source
        LOG.info("open source image: '{}'", sourceImage.getAbsolutePath());
        ReadablePsdImage source = ReadablePsdImage.of(sourceImage);

        LineReader lineReader = source.getLineReader();

        int height = source.getHeight();
        int width = source.getWidth();
        ByteBuffer line = ByteBuffer.allocate(width);
        byte[] rawLine = line.array();

        // destination
        float aspectW = (float) width / (float) maxPreviewWidth;
        float aspectH = (float) height / (float) maxPreviewHeight;
        float aspect = Math.max(aspectW, aspectH);
        float targetW = (float) width / aspect;
        float targetH = (float) height / aspect;

        LOG.info("Create Target Structure " + targetW + " x " + targetH);
        ScaledPreviewData target = new ScaledPreviewData(width, height, (int) targetW, (int) targetH);

        int index = 0;
        long lines = height * Math.max(3, source.getChannels());
        long currentLine = 0;

        // copy Data
        LOG.info("Copy Data R");
        if (source.getChannels() > 0) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);

                LOG.debug("R({}) Available: {}", index++, lineReader.getInputStream().available());
                for (int x = 0; x < width; ++x) {
                    target.addToR(x, y, rawLine[x] & 0xff);
                }
                emitProgres(Color.R,lines,++currentLine);
            }
        }

        LOG.info("Copy Data G");
        if (source.getChannels() > 1) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);
                LOG.debug("G({}) Available: {}", index++, lineReader.getInputStream().available());
                for (int x = 0; x < width; ++x) {
                    target.addToG(x, y, rawLine[x] & 0xff);
                }
                emitProgres(Color.G,lines,++currentLine);
            }
        }
        LOG.info("Copy Data B");
        if (source.getChannels() > 2) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);
                LOG.debug("B({}) Available: {}", index++, lineReader.getInputStream().available());
                for (int x = 0; x < width; ++x) {
                    target.addToB(x, y, rawLine[x] & 0xff);
                }
                emitProgres(Color.B,lines,++currentLine);
            }
        }
        /*
        System.out.println("Read Data A");
        if(source.getChannels()>2) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);
                System.out.println("A(" + index++ + ")" + " Available: " + lineReader.getInputStream().available());
            }
            emitProgres(Color.A,lines,++currentLine);
        }*/

        source.close();

        // write to file
        LOG.info("Store preview image to '{}'", targetImageFile.getAbsolutePath());
        BufferedImage img = target.createImage();

        try (FileOutputStream os = new FileOutputStream(targetImageFile)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam writerParams = writer.getDefaultWriteParam();
            writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writerParams.setCompressionQuality(compressionQuality);
            writer.setOutput(ImageIO.createImageOutputStream(os));
            writer.write(null, new IIOImage(img, null, null), writerParams);
        }
    }

    private void emitProgres(Color color, long lines, long currentLine) {
        if (progressConsumer != null) {
            progressConsumer.accept(new Progress(color, lines, currentLine));
        }
    }

    public enum Color {
        R, G, B, A
    }

    public static class Progress implements ProgressSource {
        private Color color;
        private long lines;
        private long currentLine;
        private int percent;

        public Progress(Color color, long lines, long currentLine) {
            this.color = color;
            this.lines = lines;
            this.currentLine = currentLine;
            this.percent = (int) (currentLine * 100 / lines);
        }

        public Color getColor() {
            return color;
        }

        public long getLines() {
            return lines;
        }

        public long getCurrentLine() {
            return currentLine;
        }

        public int getPercent() {
            return percent;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
