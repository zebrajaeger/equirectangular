package de.zebrajaeger.psdpreview;

/**
 * Created by lars on 14.05.2016.
 */
public class ScaledPreviewData extends PreviewData {

    private int sourceWidth;
    private int sourceHeight;
    private double xFactor;
    private double yFactor;

    public ScaledPreviewData(int sourceWidth, int sourceHeight, int width, int height) {
        super(width, height);
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        xFactor = sourceWidth;
        xFactor /= width;
        yFactor = sourceHeight;
        yFactor /= height;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void addToR(int sourceX, int sourceY, int value) {
        get((int) (((float) sourceX) / xFactor), (int) (((float) sourceY) / yFactor)).addToR(value);
    }

    public void addToG(int sourceX, int sourceY, int value) {
        get((int) (((float) sourceX) / xFactor), (int) (((float) sourceY) / yFactor)).addToG(value);
    }

    public void addToB(int sourceX, int sourceY, int value) {
        get((int) (((float) sourceX) / xFactor), (int) (((float) sourceY) / yFactor)).addToB(value);
    }
}
