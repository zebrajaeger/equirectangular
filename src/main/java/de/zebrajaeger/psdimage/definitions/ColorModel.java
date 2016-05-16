package de.zebrajaeger.psdimage.definitions;

/**
 * Created by lars on 16.05.2016.
 */
public enum ColorModel {
    BITMAP_MONOCHROME(0),
    GRAY_SCALE(1),
    INDEXED_COLOR(2),
    RGB_COLOR(3),
    CYMK_COLOR(4),
    MULTICHANNEL_COLOR(5),
    DUOTONE(8),
    LAB_COLOR(9),
    UNKNOWN(-1);

    int id;

    ColorModel(int id) {
        this.id = id;
    }

    static ColorModel get(int id) {
        switch (id) {
            case 0:
                return BITMAP_MONOCHROME;
            case 1:
                return GRAY_SCALE;
            case 2:
                return INDEXED_COLOR;
            case 3:
                return RGB_COLOR;
            case 4:
                return CYMK_COLOR;
            case 5:
                return MULTICHANNEL_COLOR;
            case 6:
                return DUOTONE;
            case 7:
                return LAB_COLOR;
            default:
                return UNKNOWN;
        }
    }

    int getId() {
        return id;
    }

}
