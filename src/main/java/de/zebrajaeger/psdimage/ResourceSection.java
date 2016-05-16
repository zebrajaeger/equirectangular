package de.zebrajaeger.psdimage;

import de.zebrajaeger.psdimage.autopano.GPanoData;

import java.io.IOException;
import java.util.LinkedList;

/**
 * The pds resource section, divided in resource blocks
 * Created by lars on 08.05.2016.
 */
public class ResourceSection {
    private long size = 0;
    private final LinkedList<ResourceBlock> blocks = new LinkedList<>();

    public long read(DecoratedInputStream is) throws IOException {
        long res = 0;
        size = is.readInt();
        res += 4;

        if (size > 0) {
            long temp = 1;
            for (; temp < size; ) {
                final ResourceBlock irb = new ResourceBlock();
                final long bytes = irb.read(is);
                blocks.add(irb);
                res += bytes;
                temp += bytes;
            }
        }

        return res;
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
