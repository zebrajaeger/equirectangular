package de.zebrajaeger.psdimage;

import de.zebrajaeger.psdimage.autopano.GPanoData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * The pds resource section, divided in resource blocks
 * Created by lars on 08.05.2016.
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
