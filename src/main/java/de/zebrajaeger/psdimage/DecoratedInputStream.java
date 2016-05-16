package de.zebrajaeger.psdimage;

import java.io.IOException;
import java.io.InputStream;

/**
 * this decorates an input stream with some helper methods to read different data types
 * <p>
 * Created by lars on 08.05.2016.
 */
public class DecoratedInputStream {
    private InputStream inputStream;

    public DecoratedInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] buf = new byte[length];
        int count = inputStream.read(buf);
        if (count < length) {
            byte[] res = new byte[count];
            System.arraycopy(buf, 0, res, 0, count);
            return res;
        } else {
            return buf;
        }
    }

    public int readByte() throws IOException {
        return inputStream.read();
    }

    public int readShort() throws IOException {
        int i1 = inputStream.read();
        int i2 = inputStream.read();
        int res = ((0xff & i1) << 8) | i2;

        return res;
    }

    public long readInt() throws IOException {
        return ((((((
                (0x0ff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read());
    }

    public long[] readInts(int length) throws IOException {
        long[] buf = new long[length];
        for (int i = 0; i < length; ++i) {
            buf[i] = (int) readInt();
        }
        return buf;
    }


    public long readLong() throws IOException {
        return ((((((((((((((
                (0x0ff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read()) << 8)
                | 0xff & inputStream.read());
    }

    public String readString(int length) throws IOException {
        return new String(readBytes(length));
    }

    public long skipBytes(long length) throws IOException {
        return inputStream.skip(length);
    }

    public int available() throws IOException {
        return inputStream.available();
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public int read(byte[] b) throws IOException {
        return inputStream.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }
}
