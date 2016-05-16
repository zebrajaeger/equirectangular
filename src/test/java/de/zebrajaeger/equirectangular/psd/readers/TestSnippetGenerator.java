package de.zebrajaeger.equirectangular.psd.readers;

import de.zebrajaeger.panosnippet.PanoSnippetGenerator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by lars on 15.05.2016.
 */
public class TestSnippetGenerator {
    @Test
    public void createSnippet() throws IOException {
        File source = new File("R:\\!pano\\2009\\2009-08-19-Stubaifall/(IMG_3377-IMG_3379-3)-{d=S-73.86x47.46(6.53)}-{p=IMG_3377_IMG_3379-3 (2009-08-19)}.psd");
        PanoSnippetGenerator g = new PanoSnippetGenerator(source);
        g.process();
    }
}
