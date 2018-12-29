package de.zebrajaeger.equirectangular.core;

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

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.equirectangular.core.psdimage.ReadablePsdImage;
import de.zebrajaeger.equirectangular.core.psdimage.WritablePsdImage;
import de.zebrajaeger.equirectangular.core.psdimage.linereader.LineReader;
import de.zebrajaeger.equirectangular.core.psdimage.linewriter.LineWriter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * creates a new psd image that is equirectangular where the source content is embedded on the correct position
 *
 * @author Lars Brandt on 14.05.2016.
 */
public class EquirectagularConverter {

    private static final Logger LOG = LoggerFactory.getLogger(EquirectagularConverter.class);

    private File sourceImageFile;
    private ViewCalculator viewData;

    private Consumer<Progress> progressConsumer;
//    private boolean overwriteExistingImage = false;

    private boolean dontAddTopAndBottomBorder = false;

//    private String equirectangularImageFilePostfix = "_equirectangular";
//    private File targetImage;

    public static EquirectagularConverter of(File sourceImage) throws IOException, ImageProcessingException {
        return of(sourceImage, ViewCalculator.of(sourceImage));
    }

    public static EquirectagularConverter of(File sourceImageFile, ViewCalculator viewData) {
        return new EquirectagularConverter(sourceImageFile, viewData);
    }

    private EquirectagularConverter(File sourceImageFile, ViewCalculator viewData) {
        this.sourceImageFile = sourceImageFile;
        this.viewData = viewData;
    }

//    public EquirectagularConverter overwriteExistingImage(boolean overwriteExistingImage) {
//        this.overwriteExistingImage = overwriteExistingImage;
//        return this;
//    }

    public EquirectagularConverter dontAddTopAndBottomBorder(boolean dontAddTopAndBottomBorder) {
        this.dontAddTopAndBottomBorder = dontAddTopAndBottomBorder;
        return this;
    }

//    public EquirectagularConverter equirectangularImageFilePostfix(String equirectangularImageFilePostfix) {
//        this.equirectangularImageFilePostfix = equirectangularImageFilePostfix;
//        return this;
//    }

    public EquirectagularConverter progressConsumer(Consumer<Progress> progressConsumer) {
        this.progressConsumer = progressConsumer;
        return this;
    }

//    public File getTargetImage() {
//        return targetImage;
//    }

//    public EquirectagularConverter renderEquirectangularImage() throws IOException {
//        String name = FilenameUtils.removeExtension(sourceImageFile.getName())
//                + equirectangularImageFilePostfix
//                + "."
//                + FilenameUtils.getExtension(sourceImageFile.getName());
//        return renderEquirectangularImage(new File(sourceImageFile.getParentFile(), name));
//    }

    public EquirectagularConverter renderEquirectangularImage(File targetImage) throws IOException {
//        this.targetImage = targetImage;

//        if (targetImage.exists()) {
//            if (overwriteExistingImage) {
//                LOG.info("Euirectangular image already exists, overwrite: '{}'", targetImage.getAbsolutePath());
//            } else {
//                LOG.info("Euirectangular image already exists, skip: '{}'", targetImage.getAbsolutePath());
//                return this;
//            }
//        }
//
//        if (targetImage.exists()) {
//            targetImage.delete();
//        }

        renderEquirectangularImage_(targetImage);

        return this;
    }

    private void renderEquirectangularImage_(File targetImage) throws IOException {
        ReadablePsdImage source = ReadablePsdImage.of(sourceImageFile);

        LineReader lineReader = source.getLineReader();
        ByteBuffer readBuffer = ByteBuffer.allocate(source.getWidth());
        if (readBuffer.array().length != source.getWidth()) {
            String msg = String.format("Readbuffer array length should be '%s' but is '%s'", source.getWidth(), readBuffer.array().length);
            throw new IllegalStateException(msg);
        }

        // compute V Border size (top,bottom)
        long topBorderLines = viewData.getBorderTop();
        long imageLines  = source.getHeight();
        long bottomBorderLines = viewData.getBorderTop();

        if(dontAddTopAndBottomBorder){
            Long offset = viewData.getFovYOffsetPx();
            if(offset!=null) {
                if (offset >= 0) {
                    topBorderLines = offset;
                    bottomBorderLines = 0;
                } else {
                    topBorderLines = 0;
                    bottomBorderLines = -offset;
                }
            }
        }
        long lines = topBorderLines + imageLines + bottomBorderLines;

        // create target image
        WritablePsdImage destination = new WritablePsdImage(targetImage);
        destination.open();
        destination.readValuesFrom(source);
        destination.setWidth(viewData.getTargetWidth());
        destination.setHeight(lines);
        destination.setCompression(0);
        destination.writeHeader();
        LineWriter lineWriter = destination.getLineWriter();

        ByteBuffer writeBuffer = ByteBuffer.allocate(viewData.getTargetWidthAsInteger()); // ok, int should be enough for a single line...
        if (writeBuffer.array().length != destination.getWidth()) {
            String msg = String.format("Writebuffer array length should be '%s' but is '%s'", destination.getWidth(), writeBuffer.array().length);
            throw new IllegalStateException(msg);
        }

        // copy image data
        long linesToWrite = lines * destination.getChannels();
        long currentLine = 0;

        for (int channel = 0; channel < destination.getChannels(); ++channel) {
            // write top margin
            Arrays.fill(writeBuffer.array(), (byte) 0);
            for (long i = 0; i < topBorderLines; ++i) {
                ++currentLine;
                lineWriter.writeLine(writeBuffer);
                emitProgress(linesToWrite, currentLine);
            }

            // copy content
            for (long i = 0; i < imageLines; ++i) {
                ++currentLine;
                lineReader.readLine(readBuffer);
                Arrays.fill(writeBuffer.array(), (byte) 0);
                System.arraycopy(readBuffer.array(), 0, writeBuffer.array(), viewData.getBorderLeftAsInteger(), readBuffer.array().length);
                lineWriter.writeLine(writeBuffer);
                emitProgress(linesToWrite, currentLine);
            }

            // write bottom margin
            Arrays.fill(writeBuffer.array(), (byte) 0);
            for (long i = 0; i < bottomBorderLines; ++i) {
                ++currentLine;
                lineWriter.writeLine(writeBuffer);
                emitProgress(linesToWrite, currentLine);
            }
        }
        destination.close();
        source.close();
    }

    private void emitProgress(long lines, long currentLine) {
        if (progressConsumer != null) {
            progressConsumer.accept(new Progress(lines, currentLine));
        }
    }

    public static class Progress implements ProgressSource {
        long lines;
        long currentLine;
        int percent;

        public Progress(long lines, long currentLine) {
            this.lines = lines;
            this.currentLine = currentLine;
            this.percent = (int) (100 * lines / currentLine);
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
            return "Progress{"
                    + "lines=" + lines
                    + ", currentLine=" + currentLine
                    + ", percent=" + (100 * currentLine / lines)
                    + '}';
        }
    }
}
