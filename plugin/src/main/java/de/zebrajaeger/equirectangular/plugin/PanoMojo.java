package de.zebrajaeger.equirectangular.plugin;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.equirectangular.core.EquirectagularConverter;
import de.zebrajaeger.equirectangular.core.ViewCalculator;
import de.zebrajaeger.equirectangular.core.common.ZipUtils;
import de.zebrajaeger.equirectangular.core.imgremove.PanoTilesCleaner;
import de.zebrajaeger.equirectangular.core.krpano.KrPanoExecutor;
import de.zebrajaeger.equirectangular.core.panosnippet.PanoSnippetGenerator;
import de.zebrajaeger.equirectangular.core.psdpreview.PreviewGenerator;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
@Mojo(name = "pano", requiresProject = false)
public class PanoMojo extends AbstractMojo {

    @Parameter(property = "sourceImage")
    private File sourceImage = null;

    @Parameter(property = "generateSnippet", defaultValue = "true")
    private boolean generateSnippet;

    @Parameter(property = "generatePreview", defaultValue = "true")
    private boolean generatePreview;
    @Parameter(property = "previewQuality", defaultValue = "0.8")
    private float previewQuality;
    @Parameter(property = "overwriteExistingPreview", defaultValue = "false")
    private boolean overwriteExistingPreview;

    @Parameter(property = "generateEquirectangularImage", defaultValue = "true")
    private boolean generateEquirectangularImage;
    @Parameter(property = "overwriteExistingEquirectangularImage", defaultValue = "false")
    private boolean overwriteExistingEquirectangularImage;
    @Parameter(property = "dontAddTopAndBottomBorder", defaultValue = "false")
    private boolean dontAddTopAndBottomBorder;

    @Parameter(property = "execKrPano", defaultValue = "true")
    private boolean execKrPano;
    @Parameter(property = "deleteIfPanoFolderExists", defaultValue = "false")
    private boolean deleteIfPanoFolderExists;
    @Parameter(property = "krPanoExe")
    private File krPanoExe;
    @Parameter(property = "krPanoConfig")
    private File krPanoConfig;
    @Parameter(property = "krPanoRenderTimeout", defaultValue = "7200")
    private long krPanoRenderTimeout;

    @Parameter(property = "removeBlackImages", defaultValue = "true")
    private boolean removeBlackImages;

    @Parameter(property = "compressToZipArchive", defaultValue = "true")
    private boolean compressToZipArchive;
    @Parameter(property = "overwriteExistingZipFile", defaultValue = "false")
    private boolean overwriteExistingZipFile;

    public void execute() throws MojoExecutionException {
        // TODO if a step run, all the following steps have to be executed
        ViewCalculator viewCalculator = createViewCalculator();
        processGenerateSnippet(viewCalculator);
        processGeneratePreview();
        File equirectangularImageFile = processGenerateEquirectangularImage(viewCalculator);
        File panoFolder = createPanoFolder(equirectangularImageFile);
        processExecKrPano(viewCalculator, equirectangularImageFile, panoFolder);
        processRemoveBlackImages(panoFolder);
        processZipFile(panoFolder);
    }

    private File createPanoFolder(File equirectangularImageFile) {
        File panoFolder;
        if (equirectangularImageFile == null) {
            panoFolder = new File(sourceImage.getParentFile(), FilenameUtils.removeExtension(sourceImage.getName()));
        } else {
            panoFolder = new File(equirectangularImageFile.getParentFile(), FilenameUtils.removeExtension(equirectangularImageFile.getName()));
        }
        return panoFolder;
    }

    private ViewCalculator createViewCalculator() throws MojoExecutionException {
        ViewCalculator viewCalculator;
        try {
            viewCalculator = ViewCalculator.of(sourceImage);
        } catch (IOException | ImageProcessingException e) {
            String msg = String.format("Could not load viewdata from source image: '%s'", sourceImage.getAbsolutePath());
            throw new MojoExecutionException(msg, e);
        }
        return viewCalculator;
    }

    private void processGenerateSnippet(ViewCalculator viewCalculator) throws MojoExecutionException {
        if (generateSnippet) {
            try {
                PanoSnippetGenerator.of(sourceImage, viewCalculator)
                        .renderSippet()
                        .storeInFile();
            } catch (IOException e) {
                throw new MojoExecutionException("Could not create snippet", e);
            }
        }
    }

    private void processGeneratePreview() throws MojoExecutionException {
        if (generatePreview) {
            try {
                // TODO progressConsumer
                PreviewGenerator
                        .of(sourceImage)
                        .overwriteExistingPreview(overwriteExistingPreview)
                        .compressionQuality(previewQuality)
                        .renderPreview();
            } catch (IOException e) {
                throw new MojoExecutionException("Could not render preview image", e);
            }
        }
    }

    private File processGenerateEquirectangularImage(ViewCalculator viewCalculator) throws MojoExecutionException {
        File equirectangularImageFile = null;
        if (generateEquirectangularImage) {
            try {
                AtomicReference<Integer> last = new AtomicReference(new Integer(-1));
                equirectangularImageFile = EquirectagularConverter
                        .of(sourceImage, viewCalculator)
                        .overwriteExistingImage(overwriteExistingEquirectangularImage)
                        .dontAddTopAndBottomBorder(dontAddTopAndBottomBorder)
                        .progressConsumer(progress -> {
                            int lastPercent = last.get();
                            int currentPercent = progress.getPercentAsInt();
                            if (lastPercent != currentPercent) {
                                last.set(currentPercent);
                                getLog().info(progress.toString());
                            }
                        })
                        .renderEquirectangularImage()
                        .getTargetImage();
            } catch (IOException e) {
                throw new MojoExecutionException("Could not render equirectangular image", e);
            }
        }
        return equirectangularImageFile;
    }

    private void processExecKrPano(ViewCalculator viewCalculator, File equirectangularImageFile, File panoFolder) throws MojoExecutionException {
        if (execKrPano) {
            if (krPanoExe == null) {
                throw new MojoExecutionException("krPanoExe is unset but required if execKrPano=true");
            }
            if (krPanoConfig == null) {
                throw new MojoExecutionException("krPanoConfig is unset but required if execKrPano=true");
            }
            try {
                KrPanoExecutor.Type type = null;
                String projection = viewCalculator.getProjection();

                if (projection != null) {
                    projection = projection.toLowerCase();

                    if ("equirectangular".equals(projection)) {
                        type = KrPanoExecutor.Type.SPHERE;
                    }
                }

                if (projection == null) {
                    getLog().error("UNKNOWN PROJECTION: '" + projection + "'");
                    type = KrPanoExecutor.Type.AUTODETECT; // hopefully...
                }

                KrPanoExecutor
                        .of(krPanoExe, krPanoConfig)
                        .deleteIfPanoFolderExists(deleteIfPanoFolderExists)
                        .hFov(360d)
                        .type(type)
                        .renderTimeout(krPanoRenderTimeout)
                        .renderImage(equirectangularImageFile, panoFolder);

            } catch (IOException | InterruptedException e) {
                throw new MojoExecutionException("Could not execute KrPano Task", e);
            }
        }
    }

    private void processRemoveBlackImages(File panoFolder) throws MojoExecutionException {
        if (removeBlackImages) {
            try {
                PanoTilesCleaner
                        .of()
                        .clean(panoFolder);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to remove black images", e);
            }
        }
    }

    private void processZipFile(File panoFolder) throws MojoExecutionException {
        if (compressToZipArchive) {
            try {
                File zipFile = new File(panoFolder.getParentFile(), panoFolder.getName() + ".zip");
                if(zipFile.exists()){
                    if(overwriteExistingZipFile){
                        getLog().info("Zipfile already exists. Overwrite: " + zipFile.getAbsolutePath());
                        zipFile.delete();
                        ZipUtils.compressDirectory(panoFolder, zipFile);
                    }else{
                        getLog().info("Zipfile already exists. Skip: " + zipFile.getAbsolutePath());
                    }
                }else{
                    ZipUtils.compressDirectory(panoFolder, zipFile);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to create zip archive", e);
            }
        }
    }
}