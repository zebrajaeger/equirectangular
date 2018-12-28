package de.zebrajaeger.equirectangular.core.psdimage;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;

/**
 * this decorates an input stream with some helper methods to read different data types
 * <p>
 * @author Lars Brandt on 08.05.2016.
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
