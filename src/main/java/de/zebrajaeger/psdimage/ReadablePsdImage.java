package de.zebrajaeger.psdimage;

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

import de.zebrajaeger.psdimage.linereader.LineReader;
import de.zebrajaeger.psdimage.linereader.RLELineReader;
import de.zebrajaeger.psdimage.linereader.RawLineReader;

import java.io.*;

/**
 * A psd image that can be ridden from a file
 * <p>
 * @author Lars Brandt on 13.05.2016.
 */
public class ReadablePsdImage extends PsdImage {
    private File file;
    private DecoratedInputStream inputStream = null;
    private LineReader lineReader;
    private long[] compressionLineSizes;

    public ReadablePsdImage(File file) {
        this.file = file;
    }

    public void open() throws FileNotFoundException {
        if (inputStream == null) {
            inputStream = new DecoratedInputStream(new BufferedInputStream(new FileInputStream(file), 1024 * 1024));
        } else {
            throw new IllegalStateException("already opened");
        }
    }

    public void close() throws IOException {
        if (inputStream == null) {
            throw new IllegalStateException("already closed");
        } else {
            try {
                inputStream.close();
            } finally {
                inputStream = null;
            }
        }
    }

    public long[] getCompressionLineSizes() {
        return compressionLineSizes;
    }

    public void readHeader() throws IOException {
        DecoratedInputStream is = getInputStream();

        setId(is.readString(4));
        setVersion(is.readShort());
        setReserved(is.readBytes(6));
        setChannels(is.readShort());
        setHeight(is.readInt());
        setWidth(is.readInt());
        setDepth(is.readShort());
        setColorMode(is.readShort());

        setColorDataSize(is.readInt());
        is.skipBytes(getColorDataSize());

        int size = (int) is.readInt();
        byte[] resourceSection = new byte[size];
        is.read(resourceSection);
        setResources(new ResourceSection(resourceSection));

        //setResources(new ResourceSection());
        //getResources().read(is);

        if (isPSB()) {
            setLayerMaskSize(is.readLong());
        } else {
            setLayerMaskSize(is.readInt());
        }
        is.skipBytes(getLayerMaskSize());

        setCompression(is.readShort());

        if (getCompression() == 0) {
            lineReader = new RawLineReader(getInputStream(), (int) getWidth());
        } else if (getCompression() == 1) {
            compressionLineSizes = is.readInts((int) (getChannels() * getHeight()));
            lineReader = new RLELineReader(getInputStream(), (int) getWidth());
        } else {
            lineReader = null;
        }
    }

    public File getFile() {
        return file;
    }

    public DecoratedInputStream getInputStream() {
        return inputStream;
    }

    public LineReader getLineReader() {
        return lineReader;
    }

    public long getExpectedSize() {
        long result = 0;
        if (compressionLineSizes != null) {
            for (long s : compressionLineSizes) {
                result += s;
            }
        } else {
            result = getWidth() * getHeight() * getChannels();
        }

        return result;
    }
}
