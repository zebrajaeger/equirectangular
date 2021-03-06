package de.zebrajaeger.equirectangular.core.equirectangular.psd.readers;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lars Brandt on 07.05.2016.
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
