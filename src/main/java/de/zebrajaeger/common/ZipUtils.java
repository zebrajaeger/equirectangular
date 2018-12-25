package de.zebrajaeger.common;

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


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Lars Brandt on 22.05.2016.
 */
public class ZipUtils {

    public static void compressDirectory(File pSource, File zipFile, String zipBaseDir) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
        compressDirectory(pSource, zipBaseDir, zipStream);
        IOUtils.closeQuietly(zipStream);
    }

    public static void compressDirectory(File pSource, File zipFile) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
        compressDirectory(pSource, "", zipStream);
        IOUtils.closeQuietly(zipStream);
    }

    private static void compressDirectory(File pSource, String zipDir, ZipOutputStream zipStream) throws IOException {
        for (File f : pSource.listFiles()) {
            String zipName = (zipDir.isEmpty()) ? f.getName() : zipDir + File.separator + f.getName();
            if (f.isFile()) {
                System.out.println("Add File:      '" + f.getAbsolutePath() + "' as '" + zipName + "'");
                ZipEntry entry = new ZipEntry(zipName);
                zipStream.putNextEntry(entry);

                FileInputStream in = new FileInputStream(f);
                IOUtils.copy(in, zipStream);
                IOUtils.closeQuietly(in);

            } else if (f.isDirectory()) {
                System.out.println("Add Directory: '" + f.getAbsolutePath() + "'");
                compressDirectory(f, zipName, zipStream);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File from = new File("R:\\TEMP\\result-(IMG_0833-IMG_0836-4)-{d_S-80_84x53_86(-8_27)}-{p_IMG_0833_IMG_0836-4_(2009-08-04)}_equirectangular");
        File to = new File("R:\\TEMP\\out.zip");

        compressDirectory(from, to, PanoNameUtils.extractFirstImageName(from.getName()));
    }
}
