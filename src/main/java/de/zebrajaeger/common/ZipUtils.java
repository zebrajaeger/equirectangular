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

    public static void compressZipFile(File sourceDir, File outputFile) throws IOException {
        compressZipFile(sourceDir.getAbsolutePath(), outputFile.getAbsolutePath());
    }

    public static void compressZipFile(String sourceDir, String outputFile) throws IOException {
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
        compressDirectoryToZipFile(sourceDir, sourceDir, zipFile);
        IOUtils.closeQuietly(zipFile);
    }

    private static void compressDirectoryToZipFile(String rootDir, String sourceDir, ZipOutputStream out) throws IOException {
        for (File file : new File(sourceDir).listFiles()) {
            if (file.isDirectory()) {
                System.out.println("Add Directory: " + file.getAbsolutePath());
                compressDirectoryToZipFile(rootDir, sourceDir + file.getName() + File.separator, out);

            } else if (file.isFile()) {
                System.out.println("Add File:      " + file.getAbsolutePath());

                ZipEntry entry = new ZipEntry(sourceDir.replace(rootDir, "") + file.getName());
                out.putNextEntry(entry);

                FileInputStream in = new FileInputStream(sourceDir + file.getName());
                IOUtils.copy(in, out);
                IOUtils.closeQuietly(in);
            }
        }
    }
}
