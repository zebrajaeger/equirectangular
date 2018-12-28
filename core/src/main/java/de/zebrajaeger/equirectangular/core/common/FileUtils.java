package de.zebrajaeger.equirectangular.core.common;

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Lars Brandt on 15.05.2016.
 */
public class FileUtils {

    public static String normalizeName(String toNormalize) {
        return toNormalize.replaceAll("[\\s=\\.]", "_");
    }

    public static String normalizeFileName(String toNormalize) {
        int pos = toNormalize.lastIndexOf(".");
        String name = toNormalize;
        String ext = "";
        if (pos != -1) {
            name = toNormalize.substring(0, pos);
            ext = toNormalize.substring(pos);
        }
        return normalizeName(name) + ext;
    }

    public static File normalizeName(File toNormalize) {
        String name = toNormalize.getName();
        name = normalizeFileName(name);
        return new File(toNormalize.getParentFile(), name);
    }

    public static void deleteRecursive(File toDelete) {
        if (toDelete.isFile()) {
            toDelete.delete();
        } else if (toDelete.isDirectory()) {
            for (File f : toDelete.listFiles()) {
                deleteRecursive(f);
            }
        }
    }

    public static File findDirectoryThatNameContains(File parent, String part) {
        for (File f : parent.listFiles()) {
            if (f.isDirectory()) {
                String name = f.getName();
                if (name.toLowerCase().contains(part.toLowerCase())) {
                    return f;
                }
            }
        }
        return null;
    }

    public static File findFileThatNameContains(File parent, String part) {
        for (File f : parent.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                if (name.toLowerCase().contains(part.toLowerCase())) {
                    return f;
                }
            }
        }
        return null;
    }

    public static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            return name.substring(0, dot);
        } else {
            return name;
        }
    }

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
