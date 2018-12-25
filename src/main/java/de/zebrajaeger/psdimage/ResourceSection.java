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

import de.zebrajaeger.psdimage.autopano.GPanoData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * The pds resource section, divided in resource blocks
 * @author Lars Brandt on 08.05.2016.
 */
public class ResourceSection {
    private long size = 0;
    private final LinkedList<ResourceBlock> blocks = new LinkedList<>();


    public ResourceSection(byte[] data) {
        DecoratedInputStream is = new DecoratedInputStream(new ByteArrayInputStream(data));

        try {
            // minimum header size at lease 10 bytes
            while (is.available() > 10) {
                System.out.println("red resourceblocck. available: " + is.available());
                final ResourceBlock irb = new ResourceBlock();
                irb.read(is);
                blocks.add(irb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GPanoData getGPanoData() {
        for (final ResourceBlock b : blocks) {
            if (b.getDecodedData() instanceof GPanoData) {
                return (GPanoData) b.getDecodedData();
            }
        }
        return null;
    }
}
