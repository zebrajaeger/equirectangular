package de.zebrajaeger.equirectangular.core.imgremove;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *
 * @author Lars Brandt on 14.05.2016.
 */
public class PanoTilesCleaner {
    private static final Logger LOG = LoggerFactory.getLogger(PanoTilesCleaner.class);

    public static PanoTilesCleaner of() {
        return new PanoTilesCleaner();
    }

    private PanoTilesCleaner() {
    }

    public void clean(File toClean) throws IOException {
        final LinkedList<PanoTile> tiles = new LinkedList<>();
        Files.walkFileTree(toClean.toPath(), new FileVisitorAdapter() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    PanoTile tile = PanoTile.Builder.of().file(file).build();
                    tiles.add(tile);
                } catch (FileNameNotMatchException ignore) {
                    // we want to ignore a image that filename doesn't match
                }
                return FileVisitResult.CONTINUE;
            }
        });

        int count = 1;
        String blackHash = null;
        for (PanoTile t : tiles) {
            int percent = count * 100 / tiles.size();
            System.out.println(String.format("%1$3s %% %1$6s / %1$6s", percent, count++, tiles.size()));

            boolean remove = false;
            if (t.getFilePosition().getLevel() != 1) {
                if (blackHash != null) {
                    if (blackHash.equals(t.getFileHash())) {
                        remove = true;
                    }
                } else if (t.getIsBlack()) {
                    remove = true;
                    blackHash = t.getFileHash();
                }
            }

            if (remove) {
                LOG.info("REMOVE: '{}'", t);
                t.getFile().delete();
            } else {
                LOG.info("KEEP:   '{}'", t);
            }
        }
    }
}
