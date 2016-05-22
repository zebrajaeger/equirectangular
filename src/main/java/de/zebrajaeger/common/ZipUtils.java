package de.zebrajaeger.common;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by lars on 22.05.2016.
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
