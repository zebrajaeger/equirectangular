package de.zebrajaeger.psdimage;

import de.zebrajaeger.psdimage.linewriter.LineWriter;
import de.zebrajaeger.psdimage.linewriter.RAWLineWriter;

import java.io.*;

/**
 * a psd image that can be stored in a file
 * <p>
 * Created by lars on 13.05.2016.
 */
public class WritablePsdImage extends PsdImage {
    private File file;
    private DecoratedOutputStream outputStream;

    public WritablePsdImage(File file) {
        this.file = file;
    }

    public void open() throws FileNotFoundException {
        if (outputStream == null) {
            outputStream = new DecoratedOutputStream(new BufferedOutputStream(new FileOutputStream(file), 1024 * 1024));
        } else {
            throw new IllegalStateException("already opened");
        }
    }

    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.getOutputStream().flush();
            outputStream.getOutputStream().close();
        } else {
            throw new IllegalStateException("already closed");
        }
    }

    public void writeHeader() throws IOException {

        outputStream.writeString(getId());
        outputStream.writeUnsignedShort(getVersion());
        outputStream.writeBytes(getReserved());
        outputStream.writeUnsignedShort(getChannels());
        outputStream.writeUnsignedInt(getHeigth());
        outputStream.writeUnsignedInt(getWidth());
        outputStream.writeUnsignedShort(getDepth());
        outputStream.writeUnsignedShort(getColorMode());
        outputStream.writeUnsignedInt(0); // color data size = 0
        outputStream.writeUnsignedInt(0); // resourcesection

        if (isPSB()) {
            outputStream.writeLong(getLayerMaskSize());
        } else {
            outputStream.writeUnsignedInt(getLayerMaskSize());
        }

        outputStream.writeUnsignedShort(getCompression());
        if (getCompression() == 0) {
            lineWriter = new RAWLineWriter(outputStream.getOutputStream(), getWidth());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private LineWriter lineWriter;

    public LineWriter getLineWriter() {
        return lineWriter;
    }
}
