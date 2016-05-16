package de.zebrajaeger.psdimage.definitions;

/**
 * Created by lars on 16.05.2016.
 */
public enum PsdVersion {
    PSD(1), PSB(2), UNKNOWN(-1);

    int id;

    PsdVersion(int id) {
        this.id = id;
    }

    static PsdVersion get(int id) {
        switch (id) {
            case 1:
                return PSD;
            case 2:
                return PSB;
            default:
                return UNKNOWN;
        }
    }

    int getId() {
        return id;
    }
}