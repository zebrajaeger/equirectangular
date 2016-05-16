package de.zebrajaeger.psdimage.definitions;

/**
 * Created by lars on 16.05.2016.
 */
public enum Compression {
    RAW(0), RLE(1), ZIP_WITHOUT_PREDICTION(2), ZIP_WITH_PREDICTION(3), UNKNOWN(-1);

    int id;

    Compression(int id) {
        this.id = id;
    }

    static Compression get(int id) {
        switch (id) {
            case 0:
                return RAW;
            case 1:
                return RLE;
            case 2:
                return ZIP_WITHOUT_PREDICTION;
            case 3:
                return ZIP_WITH_PREDICTION;
            default:
                return UNKNOWN;
        }
    }

    int getId() {
        return id;
    }
}