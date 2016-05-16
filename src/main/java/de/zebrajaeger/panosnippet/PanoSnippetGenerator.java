package de.zebrajaeger.panosnippet;


import de.zebrajaeger.common.FileUtils;
import de.zebrajaeger.psdimage.ReadablePsdImage;
import de.zebrajaeger.psdimage.autopano.GPanoData;

import java.io.File;
import java.io.IOException;

/**
 * a generator that creates a file with a view snippet for krpano view control
 * <p>
 * Created by lars on 15.05.2016.
 */
public class PanoSnippetGenerator {
    private static String CRLF = "\n";
    private static String INDENT = "    ";

    private File sourceFile;
    private File targetFile;

    public PanoSnippetGenerator(File sourceFile, File targetFile) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public PanoSnippetGenerator(File sourceFile) {
        this.sourceFile = sourceFile;
        this.targetFile = FileUtils.replaceDotAndExtension(sourceFile, "_snippet.txt");
    }

    public void process() throws IOException {
        ReadablePsdImage source = new ReadablePsdImage(sourceFile);
        source.open();
        source.readHeader();
        source.close();

        GPanoData panoData = source.getGPanoData();

        // Width calculation
        int fullWidth = panoData.getFullPanoWidthPixels();
        double fovWidth = source.getWidth();
        fovWidth /= fullWidth;
        double fovLeft = fovWidth / 2;
        double fovRight = -(fovWidth - fovLeft);
        int marginLeft = (fullWidth - source.getWidth()) / 2;
        int marginRight = fullWidth - marginLeft - source.getWidth();

        // HeigthCalculatio
        int fullHeigth = fullWidth / 2;
        double fovHeight = source.getHeigth();
        fovHeight /= fullHeigth;
        double fovHeightOffset = panoData.getCroppedAreaTopPixels();
        fovHeightOffset /= panoData.getFullPanoHeightPixels();
        double fovTop = ((double) 0.5) - fovHeightOffset;
        double fovBottom = -(fovHeight - fovTop);
        int marginTop = (int) (fullHeigth * fovHeightOffset);
        //int marginTop = (fullHeigth - source.getHeigth()) / 2;
        int marginBottom = fullHeigth - source.getHeigth() - marginTop;

        String snippet = createSnippet(fovLeft, fovRight, fovTop, fovBottom);

        FileUtils.storeInFile(targetFile, snippet);

        System.out.println(snippet);
    }

    protected String createSnippet(double fovLeft, double fovRight, double fovTop, double fovBottom) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<krpano version=\"1.18\" showerrors=\"false\">").append(CRLF);
        sb.append(CRLF);
        sb.append("<view").append(CRLF);
        sb.append(makeArg("limitview", "range"));

        sb.append(makeArg("hlookatmin", Double.toString(fovLeft * 360)));
        sb.append(makeArg("hlookatmax", Double.toString(fovRight * 360)));

        // TODO why it has to be negative? something is wrong but result is ok
        sb.append(makeArg("vlookatmin", Double.toString(-fovTop * 180)));
        sb.append(makeArg("vlookatmax", Double.toString(-fovBottom * 180)));

        sb.append(makeArg("hlookat", "0"));
        sb.append(makeArg("vlookat", "0"));
        sb.append(makeArg("maxpixelzoom", "2.0"));
        sb.append(makeArg("fovmax", "150"));
        sb.append("/>");
        return sb.toString();
    }

    private String makeArg(String name, String arg) {
        final StringBuilder sb = new StringBuilder();
        sb.append(INDENT);
        sb.append(name);
        sb.append("=\"");
        sb.append(arg);
        sb.append("\"");
        sb.append(CRLF);

        return sb.toString();
    }
}
