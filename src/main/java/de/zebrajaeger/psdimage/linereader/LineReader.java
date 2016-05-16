package de.zebrajaeger.psdimage.linereader;

import de.zebrajaeger.psdimage.DecoratedInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by lars on 07.05.2016.
 */
public abstract class LineReader {
    private int lineSize;
    private DecoratedInputStream inputStream;

    public LineReader(DecoratedInputStream inputStream, int lineSize) {
        this.inputStream = inputStream;
        this.lineSize = lineSize;
    }

    public abstract void readLine(ByteBuffer buffer) throws IOException;

    public int getLineSize() {
        return lineSize;
    }

    public DecoratedInputStream getInputStream() {
        return inputStream;
    }
}
