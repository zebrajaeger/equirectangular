package de.zebrajaeger.equirectagular;

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
 * Created by lars on 14.05.2016.
 */
public class EquirectagularConverter {
    private File sourceFile;
    private File destinationFile;

    public EquirectagularConverter(File sourceFile, File destinationFile) {
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
    }

    public EquirectagularConverter(File sourceFile) {
        this.sourceFile = sourceFile;
        this.destinationFile = FileUtils.addPostfix(sourceFile, "_equirectagular");
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
        double fovHeight = source.getHeigth();
        fovHeight /= fullHeigth;
        double fovHeightOffset = panoData.getCroppedAreaTopPixels();
        fovHeightOffset /= panoData.getFullPanoHeightPixels();
        double fovTop = ((double) 0.5) - fovHeightOffset;
        double fovBottom = -(fovHeight - fovTop);
        int marginTop = (int) (fullHeigth * fovHeightOffset);
        //int marginTop = (fullHeigth - source.getHeigth()) / 2;
        int marginBottom = fullHeigth - source.getHeigth() - marginTop;

        if (!dryRun) {
            LineReader lineReader = source.getLineReader();
            ByteBuffer readBuffer = ByteBuffer.allocate(source.getWidth());
            if (readBuffer.array().length != source.getWidth()) {
                String msg = String.format("Readbuffer array length should be '%s' but is '%s'", source.getWidth(), readBuffer.array().length);
                throw new IllegalStateException(msg);
            }

            if (destinationFile.exists()) {
                destinationFile.delete();
            }
            WritablePsdImage destination = new WritablePsdImage(destinationFile);
            destination.open();
            destination.readValuesFrom(source);
            destination.setWidth(fullWidth);
            destination.setHeigth(fullHeigth);
            destination.setCompression(0);
            destination.writeHeader();
            LineWriter lineWriter = destination.getLineWriter();

            ByteBuffer writeBuffer = ByteBuffer.allocate(fullWidth);
            if (writeBuffer.array().length != destination.getWidth()) {
                String msg = String.format("Writebuffer array length should be '%s' but is '%s'", destination.getWidth(), writeBuffer.array().length);
                throw new IllegalStateException(msg);
            }

            // copy image data
            int lines = destination.getHeigth() * destination.getChannels();
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
                for (int i = 0; i < source.getHeigth(); ++i) {
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

    protected void printState(int lines, int currentLine) {
        double percent = currentLine;
        percent /= lines;
        percent *= 100;
        System.out.println("" + currentLine + "/" + lines + " (" + (int) percent + "%)");
    }

}
