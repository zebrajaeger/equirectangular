package de.zebrajaeger.equirectangular.plugin;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.equirectangular.core.EquirectagularConverter;
import de.zebrajaeger.equirectangular.core.ProgressSource;
import de.zebrajaeger.equirectangular.core.ViewCalculator;
import de.zebrajaeger.equirectangular.core.common.NoDataException;
import de.zebrajaeger.equirectangular.core.common.ZipUtils;
import de.zebrajaeger.equirectangular.core.imgremove.PanoTilesCleaner;
import de.zebrajaeger.equirectangular.core.krpano.KrPanoConfigFile;
import de.zebrajaeger.equirectangular.core.krpano.KrPanoExecutor;
import de.zebrajaeger.equirectangular.core.panosnippet.PanoSnippetGenerator;
import de.zebrajaeger.equirectangular.core.psdpreview.PreviewGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
@Mojo(name = "pano", requiresProject = false, defaultPhase = LifecyclePhase.COMPILE)
public class PanoMojo extends AbstractMojo {

    @Component
    private MojoExecution execution;

    @Parameter(property = "sourceDirectory", defaultValue = "${project.basedir}/src")
    private File sourceDirectory;
    @Parameter(property = "sourceGlob", defaultValue = "*.{psd,psb}")
    private String sourceGlob;

    @Parameter(property = "generateSnippet", defaultValue = "true")
    private boolean generateSnippet;
    @Parameter(property = "overwriteSnippetIfExists", defaultValue = "false")
    private boolean overwriteSnippetIfExists;
    @Parameter(property = "snippetTargetFolder", defaultValue = "${project.build.directory}")
    private File snippetTargetFolder;

    @Parameter(property = "generatePreview", defaultValue = "true")
    private boolean generatePreview;
    @Parameter(property = "overwritePreviewIfExists", defaultValue = "false")
    private boolean overwritePreviewIfExists;
    @Parameter(property = "previewTargetFolder", defaultValue = "${project.build.directory}")
    private File previewTargetFolder;
    @Parameter(property = "previewQuality", defaultValue = "80")
    private int previewQuality;

    @Parameter(property = "generateEquirectangularImage", defaultValue = "true")
    private boolean generateEquirectangularImage;
    @Parameter(property = "overwriteEquirectangularImageIfExists", defaultValue = "false")
    private boolean overwriteEquirectangularImageIfExists;
    @Parameter(property = "equirectangularImageTargetFolder", defaultValue = "${project.build.directory}")
    private File equirectangularImageTargetFolder;
    @Parameter(property = "dontAddTopAndBottomBorder", defaultValue = "false")
    private boolean dontAddTopAndBottomBorder;

    @Parameter(property = "generatePano", defaultValue = "true")
    private boolean generatePano;
    @Parameter(property = "overwritePanoIfExists", defaultValue = "false")
    private boolean overwritePanoIfExists;
    @Parameter(property = "panoTargetFolder", defaultValue = "${project.build.directory}")
    private File panoTargetFolder;
    @Parameter(property = "krPanoExe")
    private File krPanoExe;
    @Parameter(property = "krPanoConfig")
    private File krPanoConfig;
    @Parameter(property = "krPanoRenderTimeout", defaultValue = "7200")
    private long krPanoRenderTimeout;

    @Parameter(property = "removeBlackImages", defaultValue = "true")
    private boolean removeBlackImages;

    @Parameter(property = "generateZip", defaultValue = "true")
    private boolean generateZip;
    @Parameter(property = "overwriteZipIfExists", defaultValue = "false")
    private boolean overwriteZipIfExists;
    @Parameter(property = "zipTargetFolder", defaultValue = "${project.build.directory}")
    private File zipTargetFolder;

    @Parameter(property = "modifyPanoConfig", defaultValue = "true")
    private boolean modifyPanoConfig;

    public void execute() throws MojoExecutionException {

        List<File> sourceImages;
        try {
            sourceImages = findSourceImages(sourceDirectory, sourceGlob);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to resolve source(s)", e);
        }

        for (File sourceImage : sourceImages) {
            handleSourceImage(sourceImage);
        }
    }

    private void handleSourceImage(File sourceImage) throws MojoExecutionException {
        // TODO if a step run, all the following steps have to be executed
        ViewCalculator viewCalculator = createViewCalculator(sourceImage);

        // snippet
        Optional<File> snippetFile = generateSnippet(
                generateSnippet,
                overwriteSnippetIfExists,
                viewCalculator,
                new File(snippetTargetFolder, FilenameUtils.removeExtension(sourceImage.getName()) + "_snippet.txt")
        );

        // preview
        Optional<File> previewFile = generatePreview(
                generatePreview,
                overwritePreviewIfExists,
                sourceImage,
                previewQuality,
                new File(previewTargetFolder, FilenameUtils.removeExtension(sourceImage.getName()) + "_preview.jpg")
        );

        // equirectangular
        Optional<File> equirectangularImageFile = generateEquirectangularImage(
                generateEquirectangularImage,
                overwriteEquirectangularImageIfExists,
                sourceImage,
                viewCalculator,
                dontAddTopAndBottomBorder,
                new File(previewTargetFolder, FilenameUtils.removeExtension(sourceImage.getName()) + "_equirectangular.psb"));

        // krpano
        File panoSourceImage = (equirectangularImageFile.isPresent()) ? equirectangularImageFile.get() : sourceImage;
        Optional<File> panoFolder = execKrPano(
                generatePano,
                overwritePanoIfExists,
                panoSourceImage,
                viewCalculator,
                new File(panoTargetFolder, FilenameUtils.removeExtension(panoSourceImage.getName())));

        if (panoFolder.isPresent()) {
            removeBlackImages(
                    removeBlackImages,
                    panoFolder.get());

            Optional<File> zip = generateZipFile(
                    generateZip,
                    overwriteZipIfExists,
                    panoFolder.get(),
                    new File(zipTargetFolder, FilenameUtils.removeExtension(panoSourceImage.getName()) + ".zip"));

            modifyPanoConfig(
                    modifyPanoConfig,
                    viewCalculator,
                    new File(panoFolder.get(), "pano.xml"));
        }
    }

    private ViewCalculator createViewCalculator(File sourceImage) throws MojoExecutionException {
        ViewCalculator viewCalculator;
        try {
            viewCalculator = ViewCalculator.of(sourceImage);
        } catch (IOException | ImageProcessingException e) {
            String msg = String.format("Could not load viewdata from source image: '%s'", sourceImage.getAbsolutePath());
            throw new MojoExecutionException(msg, e);
        }
        return viewCalculator;
    }

    private Optional<File> generateSnippet(
            boolean generate,
            boolean overwrite,
            ViewCalculator viewCalculator,
            File target) throws MojoExecutionException {

        if (generate) {
            try {
                if (checkCreation(overwrite, target, "Snippet")) {
                    target.getParentFile().mkdirs();

                    PanoSnippetGenerator.of()
                            .renderSnippet(viewCalculator)
                            .storeInFile(target);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Could not create snippet", e);
            } catch (NoDataException e) {
                getLog().error("Could not generate snippet");
            }
        }

        return asOptionalFile(target);
    }

    private Optional<File> generatePreview(
            boolean generate,
            boolean overwrite,
            File sourceImage,
            int previewQuality,
            File target) throws MojoExecutionException {

        if (generate) {
            getLog().info("generatePreview");
            try {
                if (checkCreation(overwrite, target, "Preview Image ")) {
                    target.getParentFile().mkdirs();

                    AtomicReference<Integer> last = new AtomicReference(new Integer(-1));
                    PreviewGenerator
                            .of(sourceImage)
                            .progressConsumer(progress -> displayProgress(last, progress))
                            .compressionQuality((float) previewQuality / 100f)
                            .renderPreview(target);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Could not render preview image", e);
            }
        }

        return asOptionalFile(target);
    }

    private Optional<File> generateEquirectangularImage(
            boolean generate,
            boolean overwrite,
            File sourceImage,
            ViewCalculator viewCalculator,
            boolean dontAddTopAndBottomBorder,
            File target) throws MojoExecutionException {

        if (generate) {
            getLog().info("generateEquirectangularImage");
            try {
                if (checkCreation(overwrite, target, "Equirectangular Image")) {
                    target.getParentFile().mkdirs();

                    AtomicReference<Integer> last = new AtomicReference(new Integer(-1));
                    EquirectagularConverter
                            .of(sourceImage, viewCalculator)
                            .dontAddTopAndBottomBorder(dontAddTopAndBottomBorder)
                            .progressConsumer(progress -> displayProgress(last, progress))
                            .renderEquirectangularImage(target);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Could not render equirectangular image", e);
            }
        }
        return asOptionalFile(target);
    }

    private Optional<File> execKrPano(
            boolean generate,
            boolean overwrite,
            File krPanoSourceImage,
            ViewCalculator viewCalculator,
            File target) throws MojoExecutionException {

        if (generate) {
            getLog().info("execKrPano");
            if (krPanoExe == null) {
                throw new MojoExecutionException("krPanoExe is unset but required if execKrPano=true");
            }
            if (krPanoConfig == null) {
                throw new MojoExecutionException("krPanoConfig is unset but required if execKrPano=true");
            }

            try {
                if (checkCreation(overwrite, target, "Pano")) {
                    target.mkdirs();

                    KrPanoExecutor.Type type = null;
                    String projection = viewCalculator.getProjection();

                    if (projection != null) {
                        projection = projection.toLowerCase();

                        // TODO implement other than spherical
                        if ("equirectangular".equals(projection) || "spherical".equals(projection)) {
                            type = KrPanoExecutor.Type.SPHERE;
                        }
                    }

                    if (projection == null) {
                        getLog().error("UNKNOWN PROJECTION: '" + projection + "'");
                        type = KrPanoExecutor.Type.AUTODETECT; // hopefully...
                    }

                    KrPanoExecutor
                            .of(krPanoExe, krPanoConfig)
                            .hFov(360d) // TODO maybe not always 360 deg (sourceImage instead equirectangular image)
                            .type(type)
                            .renderTimeout(krPanoRenderTimeout)
                            .renderImage(krPanoSourceImage, target);
                }
            } catch (IOException | InterruptedException e) {
                throw new MojoExecutionException("Could not execute KrPano Task", e);
            }
        }

        return asOptionalFile(target);
    }

    private void removeBlackImages(boolean generate, File panoFolder) throws MojoExecutionException {
        if (generate) {
            getLog().info("removeBlackImages");
            try {
                PanoTilesCleaner
                        .of()
                        .clean(panoFolder);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to remove black images", e);
            }
        }
    }

    private Optional<File> generateZipFile(
            boolean generate,
            boolean overwrite,
            File panoFolder,
            File target) throws MojoExecutionException {
        if (generate) {
            getLog().info("generateZipFile");
            try {
                if (checkCreation(overwrite, target, "Zip")) {
                    ZipUtils.compressDirectory(panoFolder, target);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to create zip archive", e);
            }
        }
        return asOptionalFile(target);
    }

    private Optional<File> modifyPanoConfig(
            boolean modifyPanoConfig,
            ViewCalculator viewCalculator,
            File target) throws MojoExecutionException {

        if (modifyPanoConfig) {
            getLog().info("modifyPanoConfig");
            try {
                KrPanoConfigFile
                        .of(target)
                        .limitView(KrPanoConfigFile.LimitView.RANGE)
                        .fovMax(150d)
                        .maxPixelzoom(2d)
                        .hLookAtMin(180d - viewCalculator.getFovX1())
                        .hLookAtMax(180d - viewCalculator.getFovX2())
                        .vLookAtMin(90d - viewCalculator.getFovY1())
                        .vLookAtMax(90d - viewCalculator.getFovY2())
                        .hLookAt(0d)
                        .vLookAt(0d)
                        .showErrors(false)
                        .save();
            } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
                throw new MojoExecutionException("Could not modify pano.xml", e);
            }
        }
        return asOptionalFile(target);
    }

    private boolean checkCreation(boolean overwrite, File file, String name) throws IOException {
        boolean create = !file.exists();
        if (file.exists()) {
            if (overwrite) {
                getLog().info(name + " already exists. Force Overwrite. File: " + file.getAbsolutePath());
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                }
                create = true;
            } else {
                getLog().info(name + " already exists. Skip. File: " + file.getAbsolutePath());
            }
        }
        return create;
    }

    private Optional<File> asOptionalFile(File snippetFile) {
        if (snippetFile != null && snippetFile.exists()) {
            return Optional.of(snippetFile);
        } else {
            return Optional.empty();
        }
    }

    private void displayProgress(AtomicReference<Integer> last, ProgressSource progress) {
        int lastPercent = last.get();
        int currentPercent = progress.getPercent();
        if (lastPercent != currentPercent) {
            last.set(currentPercent);
            getLog().info(progress.toString());
        }
    }

    private List<File> findSourceImages(File sourceDir, String glob) throws IOException {
        List<File> result = new LinkedList<>();

        if (!glob.startsWith("glob:") && !glob.startsWith("regex:")) {
            glob = "glob:" + glob;
        }

        Path sourcepath = Paths.get(sourceDir.toURI());
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
        Files.walkFileTree(Paths.get(sourceDir.toURI()), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                Path relPath = path.subpath(sourcepath.getNameCount(), path.getNameCount());
                if (pathMatcher.matches(relPath)) {
                    result.add(path.toFile());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        return result;
    }
}