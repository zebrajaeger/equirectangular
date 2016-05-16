package de.zebrajaeger.equirectangular.psd.readers;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by lars on 07.05.2016.
 */
public class ImageDataRLELineReaderTest {
    @Test
    public void readIntFromBytes() {
        byte[] val = {0, 0, 6, -92};
        int i = 0xff & val[0];
        i <<= 8;
        i |= 0xff & val[1];
        i <<= 8;
        i |= 0xff & val[2];
        i <<= 8;
        i |= 0xff & val[3];
        System.out.println(Integer.toHexString(i));
        Assert.assertEquals(1700, i);


        int i2 = (((((((0xff & val[0]) << 8) | 0xff & val[1]) << 8) | 0xff & val[2]) << 8) | 0xff & val[3]);
        Assert.assertEquals(1700, i2);
    }

}