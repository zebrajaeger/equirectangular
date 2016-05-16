package de.zebrajaeger.psdpreview;

/**
 * this pixel implementation keeps the R,G,B parts (no alpha channel supported)
 * <p>
 * Created by lars on 14.05.2016.
 */
public class PreviewPixel {
    private PixelSection r = new PixelSection();
    private PixelSection g = new PixelSection();
    private PixelSection b = new PixelSection();

    public void addToR(int value) {
        r.addValue(value);
    }

    public void addToG(int value) {
        g.addValue(value);
    }

    public void addToB(int value) {
        b.addValue(value);
    }

    public PixelSection getR() {
        return r;
    }

    public PixelSection getG() {
        return g;
    }

    public PixelSection getB() {
        return b;
    }
}
