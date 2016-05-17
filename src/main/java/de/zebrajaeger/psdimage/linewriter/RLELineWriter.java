package de.zebrajaeger.psdimage.linewriter;

//import com.twelvemonkeys.io.enc.PackBitsEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by lars on 07.05.2016.
 */
public class RLELineWriter extends LineWriter {

    // private PackBitsEncoder encoder = new PackBitsEncoder();

    public RLELineWriter(OutputStream outputStream, int lineSize) {
        super(outputStream, lineSize);
    }

    @Override
    public void writeLine(ByteBuffer buffer) throws IOException {
        if (buffer.position() != getLineSize()) {
            throw new IllegalArgumentException("bufferposition (" + buffer.position() + ") must match linesize (" + getLineSize() + ")");
        }

        //   encoder.encode(getOutputStream(), buffer);

    }
}
