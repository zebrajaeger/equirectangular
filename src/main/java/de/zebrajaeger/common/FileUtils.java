package de.zebrajaeger.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lars on 15.05.2016.
 */
public class FileUtils {

    /**
     * cretaes a new file with the same parent and the same name, but the name get a postfix by parameter
     *
     * @param sourceFile
     * @param postfix
     * @return
     */
    public static File addPostfix(File sourceFile, String postfix) {
        String name = sourceFile.getName();
        String newName = name;
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            String prefix = name.substring(0, dot);
            String ext = name.substring(dot + 1);
            newName = prefix + postfix + "." + ext;
        }
        return new File(sourceFile.getParent(), newName);
    }

    /**
     * create a new file with the same parent but the dot and the extension is replaced with parameter
     */
    public static File replaceDotAndExtension(File sourceFile, String replaceWith) {
        String name = sourceFile.getName();
        String newName = name;
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            String prefix = name.substring(0, dot);
            newName = prefix + replaceWith;
        }
        return new File(sourceFile.getParent(), newName);
    }

    /**
     * Writes a String into a file
     */
    public static void storeInFile(File target, String content) throws IOException {
        FileWriter w = new FileWriter(target);
        w.write(content);
        w.flush();
        w.close();
    }

    /**
     * Make a String from a filesize in a human readable manner.<br> Got this snipped from here: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     *
     * @param size the size of a file (or something else)
     * @param si   (the type 100 or 1024 based)
     */
    public static String humanReadableByteCount(long size, boolean si) {
        final int unit = si ? 1000 : 1024;
        if (size < unit) {
            return size + " B";
        }
        final int exp = (int) (Math.log(size) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
    }
}
