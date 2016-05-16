package de.zebrajaeger.psdimage.linewriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by lars on 07.05.2016.
 */
public abstract class LineWriter {
    private OutputStream outputStream;
    private int lineSize;

    public LineWriter(OutputStream outputStream, int lineSize) {
        this.outputStream = outputStream;
        this.lineSize = lineSize;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public int getLineSize() {
        return lineSize;
    }

    public abstract void writeLine(ByteBuffer buffer) throws IOException;
}
