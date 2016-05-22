package de.zebrajaeger.panosnippet;

/**
 * Created by lars on 22.05.2016.
 */
public class ViewRange {
    private final double fovLeft;
    private final double fovRight;
    private final double fovTop;
    private final double fovBottom;

    public ViewRange(double fovLeft, double fovRight, double fovTop, double fovBottom) {
        this.fovLeft = fovLeft;
        this.fovRight = fovRight;
        this.fovTop = fovTop;
        this.fovBottom = fovBottom;
    }

    public double getFovLeft() {
        return fovLeft;
    }

    public double getFovRight() {
        return fovRight;
    }

    public double getFovTop() {
        return fovTop;
    }

    public double getFovBottom() {
        return fovBottom;
    }
}
