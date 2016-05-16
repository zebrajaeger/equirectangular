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

        // 51727 x 23345, 8bit, 3.113.096.231 Bytes
        File source = new File("R:\\!pano\\2011\\2011-07-16/(IMG_0363-IMG_0873-461)-{d=S-360.00x59.63(-12.03)}-{p=IMG_0363_IMG_0873-461 (2011-07-16)}.psb");
        PreviewGenerator g = new PreviewGenerator(source);
        g.process();
    }


}
