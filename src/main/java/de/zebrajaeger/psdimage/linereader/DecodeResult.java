package de.zebrajaeger.psdimage.linereader;

/**
 * Created by lars on 16.05.2016.
 */
public class DecodeResult {
    private int inputCount;
    private int outputCount;

    public DecodeResult(int inputCount, int outputCount) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }
}
