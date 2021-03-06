/*
 * Copyright (c) 2015, Lars Brandt. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

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

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Pascal String used in psd images
 */
public class PascalString {

    private int size;
    private String string;

    public long read(FileImageInputStream is) throws IOException {
        long res = 0;

        size = is.readByte() & 0xff;
        size += 1 - (size % 2);
        res += 1;

        final byte[] buffer = new byte[size];
        is.read(buffer);
        this.string = new String(buffer, StandardCharsets.US_ASCII);
        res += buffer.length;

        return res;
    }

    public long read(DecoratedInputStream is) throws IOException {
        long res = 0;

        size = is.readByte() & 0xff;
        size += 1 - (size % 2);
        res += 1;

        final byte[] buffer = new byte[size];
        is.read(buffer);
        this.string = new String(buffer, StandardCharsets.US_ASCII);
        res += buffer.length;

        return res;
    }

    public void write(FileImageOutputStream os) throws IOException {
        os.writeByte(size);
        os.write(string.getBytes());
    }

    @Override
    public String toString() {
        return string;
    }
}
