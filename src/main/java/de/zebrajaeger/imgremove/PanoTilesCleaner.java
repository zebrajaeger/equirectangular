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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

/**
 * cleans a htl5 pano, that has been rendered with krpano from the most black images
 * it keeps the first layer due to panoramic viewer errors
 * <p>
 * @author Lars Brandt on 14.05.2016.
 */
public class PanoTilesCleaner {
    private File toClean;

    public PanoTilesCleaner(File toClean) {
        this.toClean = toClean;
    }

    public void process() throws IOException {

        //final PanoLayers.Builder builder = PanoLayers.Builder.of();
        final LinkedList<PanoTile> tiles = new LinkedList<>();
        Files.walkFileTree(toClean.toPath(), new FileVisitorAdapter() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    PanoTile tile = PanoTile.Builder.of().file(file).build();
                    //builder.tile(tile);
                    tiles.add(tile);
                } catch (FileNameNotMatchException ignore) {
                    // we want to ignore a image that filename doesn't match
                }
                return FileVisitResult.CONTINUE;
            }
        });

        int count = 1;
        for (PanoTile t : tiles) {
            int percent = count * 100 / tiles.size();
            System.out.print(nrToString(percent, 3));
            System.out.print("% ");
            System.out.print(nrToString(count++, 6));
            System.out.print("/");
            System.out.print(nrToString(tiles.size(), 6));
            System.out.print(" ");

            if (t.getFilePosition().getLevel() != 1 && t.getIsBlack()) {
                System.out.print("REMOVE: ");
                t.getFile().delete();
            } else {
                System.out.print("KEEP:   ");
            }

            System.out.println(t);
        }
    }

    private static String nrToString(int nr, int length) {
        String res = Integer.toString(nr);
        int missingSpaces = length - res.length();
        if (missingSpaces > 0) {
            return getSpaces(missingSpaces) + res;
        } else {
            return res;
        }
    }

    private static String spacea = " ";

    private static String getSpaces(int count) {
        while (count > spacea.length()) spacea = spacea + spacea;
        return spacea.substring(0, count);
    }
}
