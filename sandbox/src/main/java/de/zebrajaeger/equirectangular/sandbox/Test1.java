package de.zebrajaeger.equirectangular.sandbox;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.equirectangular.core.ViewCalculator;
import de.zebrajaeger.equirectangular.core.kolor.KolorExifData;
import de.zebrajaeger.equirectangular.core.psdimage.ReadablePsdImage;
import de.zebrajaeger.equirectangular.core.psdimage.autopano.GPanoData;

import java.io.File;
import java.io.IOException;

public class Test1 {
    public static void main(String[] args) throws IOException, ImageProcessingException {
        printImageData(new File("D:\\wasIstDas\\pano\\2015\\6\\2010\\2010-08-18\\(IMG_6166-IMG_6272-107)-{d=S-115.57x153.26(-1.04)}-{p=IMG_6166_IMG_6272-107 (2010-08-18)}.psb"));
//        Read resourceblocck. Available: 1464
//        Read resourceblocck. Available: 650
//        (IMG_6166-IMG_6272-107)-{d=S-115.57x153.26(-1.04)}-{p=IMG_6166_IMG_6272-107 (2010-08-18)}.psb
//        PsdImage{id='8BPS', version=2, channels=4, height=30193, width=22768, depth=8, colorMode=3, colorDataSize=0, layerMaskSize=0, compression=1}
//        de.zebrajaeger.equirectangular.core.psdimage.autopano.GPanoData@30f39991[
//            usePanoramaViewer=true
//            projectionType=equirectangular
//            croppedAreaLeftPixels=24075
//            stitchingSoftware=Autopano Giga 4.0.2
//            croppedAreaImageWidthPixels=22768
//            sourcePhotosCount=107
//            croppedAreaTopPixels=2837
//            croppedAreaImageHeightPixels=30193
//            fullPanoWidthPixels=70919
//            fullPanoHeightPixels=35460
//        ]
        printImageData(new File("D:\\wasIstDas\\pano\\2015\\6\\2010\\2010-08-22\\(IMG_6553-IMG_6654-95)-{d=S-360.00x37.73(-16.14)}-{p=IMG_6553_IMG_6654-95 (2010-08-22)}.psb"));
//        Read resourceblocck. Available: 1486
//        Read resourceblocck. Available: 1458
//        Read resourceblocck. Available: 646
//        (IMG_6553-IMG_6654-95)-{d=S-360.00x37.73(-16.14)}-{p=IMG_6553_IMG_6654-95 (2010-08-22)}.psb
//        PsdImage{id='8BPS', version=2, channels=4, height=9557, width=91193, depth=8, colorMode=3, colorDataSize=0, layerMaskSize=0, compression=1}
//        de.zebrajaeger.equirectangular.core.psdimage.autopano.GPanoData@7adf9f5f[
//            usePanoramaViewer=true
//            projectionType=equirectangular
//            croppedAreaLeftPixels=0
//            stitchingSoftware=Autopano Giga 4.0.2
//            croppedAreaImageWidthPixels=91193
//            sourcePhotosCount=95
//            croppedAreaTopPixels=22105
//            croppedAreaImageHeightPixels=9557
//            fullPanoWidthPixels=91193
//            fullPanoHeightPixels=45592
//        ]
        printImageData(new File("D:\\pano\\Bremen-Hotel\\IMG_3124_S(360.00x180.00(0.00)).psb"));
    }

    private static void printImageData(File source) throws IOException, ImageProcessingException {
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println(source.getName());
        ReadablePsdImage sourceImage = ReadablePsdImage.headerOnly(source);

        System.out.println(sourceImage);
        sourceImage.getGPanoData().ifPresent(panoData ->{
            System.out.println("panoData: '" + panoData + "'");
        });

        KolorExifData.of(source).ifPresent(exif -> System.out.println(exif));

        System.out.println(ViewCalculator.of(source));
    }
}
