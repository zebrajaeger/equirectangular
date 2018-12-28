package de.zebrajaeger.equirectangular.ui;

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

import de.zebrajaeger.equirectangular.core.common.FileUtils;
import de.zebrajaeger.equirectangular.core.common.PanoNameUtils;
import de.zebrajaeger.equirectangular.core.common.ZipUtils;
import de.zebrajaeger.equirectangular.core.EquirectagularConverter;
import de.zebrajaeger.equirectangular.core.panosnippet.PanoSnippetGenerator;
import de.zebrajaeger.equirectangular.core.panosnippet.ViewRange;
import de.zebrajaeger.equirectangular.core.psdpreview.PreviewGenerator;
import de.zebrajaeger.equirectangular.core.imgremove.PanoTilesCleaner;
import de.zebrajaeger.equirectangular.core.krpano.Config;
import de.zebrajaeger.equirectangular.core.krpano.KrPanoConfigFile;
import de.zebrajaeger.equirectangular.core.krpano.KrPanoExecutor;
import org.xml.sax.SAXException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main Drop target
 * <p>
 *
 * @author Lars Brandt on 14.05.2016.
 */
public class FileDropDialog extends JDialog {
    public static void main(String[] args) {
        new FileDropDialog().setVisible(true);
    }

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public FileDropDialog() {
        setUndecorated(true);
        setSize(250, 50);
        setLayout(new GridLayout(1, 5));
        setAlwaysOnTop(true);
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        add(createEquirectButton());
        add(createPreviewButton());
        add(createCleanButton());
        add(createSnippetButton());
        add(createAllInOneButton());
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.PLAIN | Font.BOLD, 10));
        button.setMargin(new Insets(1, 1, 1, 1));
        return button;
    }

    private JButton createAllInOneButton() {
        JButton button = createButton("<html>All</html>");
        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            createPanoFromPsd(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        button.setDropTarget(dropTarget);
        return button;
    }

    private void createPanoFromPsd(File file) throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
//        Config cfg = Config.of(new File("equirectangular.properties"));
//
//        // make Snippet
//        PanoSnippetGenerator snippet = new PanoSnippetGenerator(file);
//        ViewRange viewRange = snippet.process();
//
//        // make preview
//        PreviewGenerator preview = new PreviewGenerator(file);
//        if (!preview.previewExists()) {
//            preview.process();
//        }
//
//        // make equirectangular image
//        EquirectagularConverter equirectangular = new EquirectagularConverter(file);
//
//        File equiRectFile = equirectangular.getTargetFile();
//        if (!equirectangular.euirectangularFileExists()) {
//            equirectangular.process(false);
//        }
//
//        // delete previous output
//        String fileName = FileUtils.getFileNameWithoutExtension(equiRectFile);
//        File oldArtifactDir = FileUtils.findDirectoryThatNameContains(equiRectFile.getParentFile(), fileName);
//        if (oldArtifactDir != null) {
//            FileUtils.deleteRecursive(oldArtifactDir);
//        }
//
//        // exec krpano
//        KrPanoExecutor krPano = new KrPanoExecutor(cfg);
//        krPano.processImage(equiRectFile);
//
//        // delete unneeded tiles
//        File artefactDir = FileUtils.findDirectoryThatNameContains(equiRectFile.getParentFile(), fileName);
//        PanoTilesCleaner cleaner = new PanoTilesCleaner(artefactDir);
//        cleaner.process();
//
//        // change pano config xml
//        //File artifactDir = new File("R:\\TEMP\\result-test_equirectagular");
//        File config = FileUtils.findFileThatNameContains(artefactDir, ".xml");
//        if (config != null) {
//            KrPanoConfigFile krPanoConfigFile = new KrPanoConfigFile(config);
//            krPanoConfigFile.load();
//            krPanoConfigFile.setShowErrors(false);
//            krPanoConfigFile.setView(viewRange);
//            krPanoConfigFile.save();
//        }
//
//        // copy preview#
//        Files.copy(preview.getTargetFile().toPath(), new File(artefactDir, "preview.jpg").toPath());
//
//        // zip dir
//        String simpleName = PanoNameUtils.extractFirstImageName(artefactDir.getName());
//        File zipFile = new File(artefactDir.getParentFile(), simpleName + ".zip");
//        ZipUtils.compressDirectory(artefactDir, zipFile, simpleName);
    }

    private JButton createEquirectButton() {
        JButton button = createButton("<html>Equi<br>rect</html>");
        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
//                        try {
//                            EquirectagularConverter converter = new EquirectagularConverter(file);
//                            converter.process(false);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                });
            }
        };
        button.setDropTarget(dropTarget);
        return button;
    }

    private JButton createCleanButton() {
        JButton button = createButton("<html>Clean<br>Tiles</html>");
        button.addActionListener(new CloseListener(this));
        final FolderDropTarget dropTarget = new FolderDropTarget() {
            @Override
            public void onDropFolder(final File folder) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
//                        try {
//                            PanoTilesCleaner cleaner = new PanoTilesCleaner(folder);
//                            cleaner.process();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }

                });
            }
        };

        button.setDropTarget(dropTarget);
        return button;
    }

    private JButton createSnippetButton() {
        JButton button = createButton("<html>Snippet<br>File</html>");
        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
//                        try {
//                            PanoSnippetGenerator converter = new PanoSnippetGenerator(file);
//                            converter.process();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                });
            }
        };
        button.setDropTarget(dropTarget);
        return button;
    }

    private JButton createPreviewButton() {
        JButton button = createButton("<html>Preview</html>");

        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
//                        try {
//                            PreviewGenerator converter = new PreviewGenerator(file);
//                            converter.process();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                });
            }
        };
        button.setDropTarget(dropTarget);
        return button;
    }
}
