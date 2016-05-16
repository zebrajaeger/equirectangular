package de.zebrajaeger.psdimage.linereader;

import de.zebrajaeger.psdimage.DecoratedInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by lars on 07.05.2016.
 */
public class RawLineReader extends LineReader {

    public RawLineReader(DecoratedInputStream inputStream, int lineSize) {
        super(inputStream, lineSize);
    }

    @Override
    public DecodeResult readLine(ByteBuffer buffer) throws IOException {
        if (buffer.capacity() != getLineSize()) {
            throw new IllegalArgumentException("buffersize (" + buffer.capacity() + ") must match linesize (" + getLineSize() + ")");
        }

        buffer.clear();
        int total = getInputStream().read(buffer.array());
        return new DecodeResult(total, total);
    }
}
