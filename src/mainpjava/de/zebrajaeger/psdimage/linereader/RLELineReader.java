package de.zebrajaeger.psdimage.linereader;

import com.twelvemonkeys.io.enc.PackBitsDecoder;
import de.zebrajaeger.psdimage.DecoratedInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by lars on 07.05.2016.
 */
public class RLELineReader extends LineReader {
    private PackBitsDecoder dec = new PackBitsDecoder();

    public RLELineReader(DecoratedInputStream inputStream, int lineSize) {
        super(inputStream, lineSize);
    }

    @Override
    public void readLine(ByteBuffer buffer) throws IOException {
        if (buffer.capacity() != getLineSize()) {
            throw new IllegalArgumentException("buffersize (" + buffer.capacity() + ") must match linesize (" + getLineSize() + ")");
        }

        buffer.clear();
        dec.decode(getInputStream().getInputStream(), buffer);
    }
}
