package de.zebrajaeger.psdimage;

import de.zebrajaeger.psdimage.autopano.GPanoData;

/**
 * The basic psd data structure
 * Technical description:  https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/
 * Created by lars on 07.05.2016.
 */
public class PsdImage {

    private String id = "8BPS";
    private int version = 2;
    private byte[] reserved = new byte[6];
    private int channels = 4;
    private long heigth;
    private long width;
    private int depth = 8;
    private int colorMode = 3;
    private long colorDataSize = 0;
    private long layerMaskSize = 0;
    private int compression = 0;
    private ResourceSection resources = null;

    public PsdImage() {
    }

    public void readValuesFrom(PsdImage o) {
        id = o.id;
        version = o.version;
        reserved = o.reserved.clone();
        channels = o.channels;
        heigth = o.heigth;
        width = o.width;
        depth = o.depth;
        colorMode = o.colorMode;
        colorDataSize = o.colorDataSize;
        layerMaskSize = o.layerMaskSize;
        compression = o.compression;
        resources = null;
    }

    public int getWidth() {
        return (int) width;
    }

    public int getHeigth() {
        return (int) heigth;
    }

    public boolean isPSB() {
        return version == 2;
    }

    public GPanoData getGPanoData() {
        if (resources != null) {
            return resources.getGPanoData();
        } else {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public int getChannels() {
        return channels;
    }

    public int getDepth() {
        return depth;
    }

    public int getColorMode() {
        return colorMode;
    }

    public long getColorDataSize() {
        return colorDataSize;
    }

    public long getLayerMaskSize() {
        return layerMaskSize;
    }

    public int getCompression() {
        return compression;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setHeigth(long heigth) {
        this.heigth = heigth;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }

    public void setColorDataSize(long colorDataSize) {
        this.colorDataSize = colorDataSize;
    }

    public void setLayerMaskSize(long layerMaskSize) {
        this.layerMaskSize = layerMaskSize;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public ResourceSection getResources() {
        return resources;
    }

    public void setResources(ResourceSection resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "PsdImage{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", channels=" + channels +
                ", height=" + heigth +
                ", width=" + width +
                ", depth=" + depth +
                ", colorMode=" + colorMode +
                ", colorDataSize=" + colorDataSize +
                ", layerMaskSize=" + layerMaskSize +
                ", compression=" + compression +
                '}';
    }
}
