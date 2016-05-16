package de.zebrajaeger.psdimage.linewriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by lars on 07.05.2016.
 */
public class RAWLineWriter extends LineWriter {

    public RAWLineWriter(OutputStream outputStream, int lineSize) {
        super(outputStream, lineSize);
    }

    @Override
    public void writeLine(ByteBuffer buffer) throws IOException {
        if (buffer.array().length != getLineSize()) {
            throw new IllegalArgumentException("bufferposition (" + buffer.position() + ") must match linesize (" + getLineSize() + ")");
        }

        getOutputStream().write(buffer.array());
    }
}
