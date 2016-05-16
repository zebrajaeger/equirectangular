package de.zebrajaeger.psdpreview;

/**
 * A color channel of a Pixel i.e. the Red-Channel
 * Accumulates the values and the count how often a value is added so we can later read the value of sum/count
 * <p>
 * Created by lars on 14.05.2016.
 */
public class PixelSection {
    private int value = 0;
    private int count = 0;

    public void addValue(int value) {
        this.value += value;
        ++count;
    }

    public int getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public int getNormalizedIntValue() {
        if (count == 0) return 0;
        else return value / count;
    }
}
