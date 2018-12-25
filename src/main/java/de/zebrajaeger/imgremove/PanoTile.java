package de.zebrajaeger.imgremove;

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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Implementation to deal with a simple tile
 * <p>
 * @author Lars Brandt on 04.05.2016.
 */
public class PanoTile {
    private File file;
    private FilePosition filePosition;
    private String fileHash = null;
    private Boolean isBlack = null;

    public PanoTile(File file, FilePosition filePosition) {
        this.file = file;
        this.filePosition = filePosition;
    }

    public String getFileHash() throws IOException {
        if (fileHash == null) {
            FileInputStream fis = new FileInputStream(file);
            fileHash = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            fis.close();
        }
        return fileHash;
    }

    private Boolean checkBlack() throws IOException {
        Boolean result = null;

        BufferedImage img = ImageIO.read(file);
        DataBuffer db = img.getRaster().getDataBuffer();
        if (db.getClass().equals(DataBufferByte.class)) {
            byte[] data = ((DataBufferByte) db).getData();
            for (byte b : data) {
                if (b != 0) {
                    result = false;
                    break;
                }
            }
            if (result == null) result = true;

        } else if (db.getClass().equals(DataBufferInt.class)) {
            int[] data = ((DataBufferInt) db).getData();
            for (int i : data) {
                if (i != 0) {
                    result = false;
                    break;
                }
            }
            if (result == null) result = true;
        }
        return result;
    }

    public boolean getIsBlack() throws IOException {
        if (isBlack == null) {
            isBlack = checkBlack();
        }
        return isBlack;
    }

    public FilePosition getFilePosition() {
        return filePosition;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public static class Builder {
        private File file;

        private Builder() {
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder file(String file) {
            this.file = new File(file);
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder file(Path file) {
            this.file = file.toFile();
            return this;
        }

        public PanoTile build() {
            if (file == null) {
                throw new IllegalArgumentException("before build you have to set a file");
            }

            FilePosition fpos = FilePosition.Builder.of().file(file).build();
            return new PanoTile(file, fpos);
        }
    }

}
