package de.zebrajaeger.equirectangular.psd.readers;

import de.zebrajaeger.psdpreview.PreviewGenerator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by lars on 14.05.2016.
 */
public class TestPreview {
    @Test
    public void testCreatePreview() throws IOException {
//        File source = new File("R:\\!pano\\2010\\2010-01-01\\(IMG_1724-IMG_1737-10)-{d=S-168.77x73.12(-11.83)}-{p=IMG_1724_IMG_1737-10 images(Planetarium)}.psd");
//        File source = new File("R:\\!pano\\2010\\2010-08-03\\(IMG_4494-IMG_4505-12)-{d=S-180.21x61.82(8.48)}-{p=IMG_4494_IMG_4505-12 (2010-08-03)}.psd");
        File source = new File("R:\\!pano\\2010\\2010-08-08/(IMG_5218-IMG_5240-23)-{d=S-360.00x66.14(-4.22)}-{p=IMG_5218_IMG_5240-23 (2010-08-08)}.psb");
        PreviewGenerator g = new PreviewGenerator(source);
        g.process();
    }


}
