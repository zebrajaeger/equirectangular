package de.zebrajaeger.equirectangular.psd.readers;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by lars on 07.05.2016.
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

