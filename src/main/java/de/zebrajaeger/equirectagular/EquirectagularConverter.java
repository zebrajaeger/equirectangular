package de.zebrajaeger.equirectagular;

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

import de.zebrajaeger.common.FileUtils;
import de.zebrajaeger.psdimage.ReadablePsdImage;
import de.zebrajaeger.psdimage.WritablePsdImage;
import de.zebrajaeger.psdimage.autopano.GPanoData;
import de.zebrajaeger.psdimage.linereader.LineReader;
import de.zebrajaeger.psdimage.linewriter.LineWriter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * creates a new psd image that is equirectangular where the source content is embedded on the correct position
 * @author Lars Brandt on 14.05.2016.
 */
public class EquirectagularConverter {
    private File sourceFile;
    private File targetFile;

    public EquirectagularConverter(File sourceFile, File targetFile) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public EquirectagularConverter(File sourceFile) {
        this.sourceFile = sourceFile;
        this.targetFile = FileUtils.addPostfix(sourceFile, "_equirectangular");
        this.targetFile = FileUtils.normalizeName(this.targetFile);
    }

    public boolean euirectangularFileExists() {
        return targetFile.exists();
    }

    public void process(boolean dryRun) throws IOException {

        ReadablePsdImage source = new ReadablePsdImage(sourceFile);
        source.open();
        source.readHeader();

        GPanoData panoData = source.getGPanoData();

        // Width calculation
        int fullWidth = panoData.getFullPanoWidthPixels();
        double fovWidth = source.getWidth();
        fovWidth /= fullWidth;
        double fovLeft = fovWidth / 2;
        double fovRight = -(fovWidth - fovLeft);
        int marginLeft = (fullWidth - source.getWidth()) / 2;
        int marginRight = fullWidth - marginLeft - source.getWidth();

        // HeigthCalculatio
        int fullHeigth = fullWidth / 2;
        double fovHeight = source.getHeight();
        fovHeight /= fullHeigth;
        double fovHeightOffset = panoData.getCroppedAreaTopPixels();
        fovHeightOffset /= panoData.getFullPanoHeightPixels();
        double fovTop = ((double) 0.5) - fovHeightOffset;
        double fovBottom = -(fovHeight - fovTop);
        int marginTop = (int) (fullHeigth * fovHeightOffset);
        //int marginTop = (fullHeigth - source.getHeight()) / 2;
        int marginBottom = fullHeigth - source.getHeight() - marginTop;

        if (!dryRun) {
            LineReader lineReader = source.getLineReader();
            ByteBuffer readBuffer = ByteBuffer.allocate(source.getWidth());
            if (readBuffer.array().length != source.getWidth()) {
                String msg = String.format("Readbuffer array length should be '%s' but is '%s'", source.getWidth(), readBuffer.array().length);
                throw new IllegalStateException(msg);
            }

            if (targetFile.exists()) {
                targetFile.delete();
            }
            WritablePsdImage destination = new WritablePsdImage(targetFile);
            destination.open();
            destination.readValuesFrom(source);
            destination.setWidth(fullWidth);
            destination.setHeight(fullHeigth);
            destination.setCompression(0);
            destination.writeHeader();
            LineWriter lineWriter = destination.getLineWriter();

            ByteBuffer writeBuffer = ByteBuffer.allocate(fullWidth);
            if (writeBuffer.array().length != destination.getWidth()) {
                String msg = String.format("Writebuffer array length should be '%s' but is '%s'", destination.getWidth(), writeBuffer.array().length);
                throw new IllegalStateException(msg);
            }

            // copy image data
            int lines = destination.getHeight() * destination.getChannels();
            int currentLine = 0;
            for (int channel = 0; channel < source.getChannels(); ++channel) {

                // write top margin
                Arrays.fill(writeBuffer.array(), (byte) 0);
                for (int i = 0; i < marginTop; ++i) {
                    lineWriter.writeLine(writeBuffer);
                    ++currentLine;
                    printState(lines, currentLine);
                }

                // copy content
                for (int i = 0; i < source.getHeight(); ++i) {
                    lineReader.readLine(readBuffer);
                    Arrays.fill(writeBuffer.array(), (byte) 0);
                    System.arraycopy(readBuffer.array(), 0, writeBuffer.array(), marginLeft, readBuffer.array().length);
                    lineWriter.writeLine(writeBuffer);
                    ++currentLine;
                    printState(lines, currentLine);
                }

                // write bottom margin
                Arrays.fill(writeBuffer.array(), (byte) 0);
                for (int i = 0; i < marginBottom; ++i) {
                    lineWriter.writeLine(writeBuffer);
                    ++currentLine;
                    printState(lines, currentLine);
                }

            }
            destination.close();
        }
        source.close();
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public File getTargetFile() {
        return targetFile;
    }

    protected void printState(int lines, int currentLine) {
        double percent = currentLine;
        percent /= lines;
        percent *= 100;
        System.out.println("" + currentLine + "/" + lines + " (" + (int) percent + "%)");
    }

}
