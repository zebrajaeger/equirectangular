package de.zebrajaeger.equirectangular.psd.readers;

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

import org.junit.Test;

import java.io.IOException;

/**
 * @author Lars Brandt on 07.05.2016.
 */
public class TestPsdImg {

    private static final String sourcePath = "R:\\TEMP\\(1E6A0255-1E6A0321-67)-{d=S-190.05x89.73(-16.47)}-{p=1E6A0255_1E6A0321-67 (aaaa)}.psb";

    @Test
    public void showSourceInfo() throws IOException {
 /*       ReadablePsdImage source = new ReadablePsdImage(new File(sourcePath));
        source.open();
        source.readHeader();
        source.close();
        GPanoData gPanoData = source.getGPanoData();
        System.out.println(gPanoData);*/
    }

    @Test
    public void foo() throws IOException {
        /*String path = "R:\\!pano_neu2\\(1E6A0219-1E6A0236-18)-{d=S-114.37x19.74(-0.76)}-{p=TimmerdorferStrand1}_0000.psb";
        String destPath = "R:\\TEMP\\!out.psb";
        EquirectagularConverter converter = new EquirectagularConverter(new File(sourcePath), new File(destPath));
        converter.process(false);*/
    }
}

