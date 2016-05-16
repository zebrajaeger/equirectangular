package de.zebrajaeger.psdimage;

import java.io.IOException;
import java.io.OutputStream;

/**
 * this decorates an output stream with some helper methods to write different data types
 * <p>
 * Created by lars on 13.05.2016.
 */
public class DecoratedOutputStream {
    private OutputStream outputStream;

    public DecoratedOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeString(String value) throws IOException {
        writeBytes(value.getBytes());
    }

    public void writeByte(int value) throws IOException {
        outputStream.write(value);
    }

    public void writeUnsignedShort(int value) throws IOException {
        outputStream.write((value >> 8) & 0xff);
        outputStream.write(value & 0xff);
    }

    public void writeUnsignedInt(long value) throws IOException {
        outputStream.write((int) ((value >> 24) & 0xff));
        outputStream.write((int) ((value >> 16) & 0xff));
        outputStream.write((int) ((value >> 8) & 0xff));
        outputStream.write((int) (value & 0xff));
    }

    public void writeLong(long value) throws IOException {
        outputStream.write((int) ((value >> 56) & 0xff));
        outputStream.write((int) ((value >> 48) & 0xff));
        outputStream.write((int) ((value >> 40) & 0xff));
        outputStream.write((int) ((value >> 32) & 0xff));
        outputStream.write((int) ((value >> 24) & 0xff));
        outputStream.write((int) ((value >> 16) & 0xff));
        outputStream.write((int) ((value >> 8) & 0xff));
        outputStream.write((int) (value & 0xff));
    }

    public void writeBytes(byte[] value) throws IOException {
        outputStream.write(value);
    }

    public void writeBytes(byte[] value, int offset, int length) throws IOException {
        outputStream.write(value, offset, length);
    }

    public void writeUnsignedShorts(int[] value) throws IOException {
        for (int i : value) {
            writeUnsignedInt(i);
        }
    }

    public void writeUnsignedInts(long[] value) throws IOException {
        for (long l : value) {
            writeUnsignedInt(l);
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
