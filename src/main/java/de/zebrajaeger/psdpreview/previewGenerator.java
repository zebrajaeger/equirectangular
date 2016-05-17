package de.zebrajaeger.psdpreview;

import de.zebrajaeger.common.FileUtils;
import de.zebrajaeger.psdimage.ReadablePsdImage;
import de.zebrajaeger.psdimage.linereader.LineReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Makes a preview Image from a Psd image
 * <p>
 * Created by lars on 14.05.2016.
 */
public class PreviewGenerator {
    private File sourceFile;
    private File targetFile;

    private int maxTargetWidth = 2048;
    private int maxTargetheight = 2048;

    public PreviewGenerator(File sourceFile) {
        this.sourceFile = sourceFile;
        this.targetFile = FileUtils.replaceDotAndExtension(sourceFile, "_preview.jpg");
    }

    public PreviewGenerator(File sourceFile, File targetFile) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public void process() throws IOException {

        // source
        System.out.println("open source File");
        ReadablePsdImage source = new ReadablePsdImage(sourceFile);
        source.open();
        source.readHeader();
        long expectedSize = source.getExpectedSize();

        LineReader lineReader = source.getLineReader();

        int height = source.getHeigth();
        int width = source.getWidth();
        ByteBuffer line = ByteBuffer.allocate(width);
        byte[] rawLine = line.array();

        // destination
        float aspectW = (float) width / (float) maxTargetWidth;
        float aspectH = (float) height / (float) maxTargetheight;
        float aspect = Math.max(aspectW, aspectH);
        float targetW = (float) width / aspect;
        float targetH = (float) height / aspect;

        System.out.println("Create Target Structure " + targetW + "x" + targetH);
        ScaledPreviewData target = new ScaledPreviewData(width, height, (int) targetW, (int) targetH);

        int index = 0;
        // copy Data
        System.out.println("Copy Data R");
        if (source.getChannels() > 0) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);
                System.out.println("R(" + index++ + ")" + " Available: " + lineReader.getInputStream().available());
                for (int x = 0; x < width; ++x) {
                    target.addToR(x, y, rawLine[x] & 0xff);
                }
            }
        }
        System.out.println("Copy Data G");
        if (source.getChannels() > 1) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);
                System.out.println("G(" + index++ + ")" + " Available: " + lineReader.getInputStream().available());
                for (int x = 0; x < width; ++x) {
                    target.addToG(x, y, rawLine[x] & 0xff);
                }
            }
        }
        System.out.println("Copy Data B");
        if (source.getChannels() > 2) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);
                System.out.println("B(" + index++ + ")" + " Available: " + lineReader.getInputStream().available());
                for (int x = 0; x < width; ++x) {
                    target.addToB(x, y, rawLine[x] & 0xff);
                }
            }
        }
        /*
        System.out.println("Read Data A");
        if(source.getChannels()>2) {
            for (int y = 0; y < height; ++y) {
                lineReader.readLine(line);
                System.out.println("A(" + index++ + ")" + " Available: " + lineReader.getInputStream().available());
            }
        }*/

        source.close();

        // write to file
        System.out.println("Create Image");
        BufferedImage img = target.createImage();
        ImageIO.write(img, "JPEG", targetFile);
    }
}
