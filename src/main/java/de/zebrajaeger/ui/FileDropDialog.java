package de.zebrajaeger.ui;

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

import de.zebrajaeger.common.FileUtils;
import de.zebrajaeger.common.PanoNameUtils;
import de.zebrajaeger.common.ZipUtils;
import de.zebrajaeger.equirectagular.EquirectagularConverter;
import de.zebrajaeger.imgremove.PanoTilesCleaner;
import de.zebrajaeger.krpano.KrPanoConfigFile;
import de.zebrajaeger.krpano.KrPanoExecutor;
import de.zebrajaeger.panosnippet.PanoSnippetGenerator;
import de.zebrajaeger.panosnippet.ViewRange;
import de.zebrajaeger.psdpreview.PreviewGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main Drop target
 * <p>
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

    protected JButton createAllInOneButton() {
        JButton button = createButton("<html>All</html>");
        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // make Snippet
                            PanoSnippetGenerator snippet = new PanoSnippetGenerator(file);
                            ViewRange viewRange = snippet.process();

                            // make preview
                            PreviewGenerator preview = new PreviewGenerator(file);
                            if (!preview.previewExists()) {
                                preview.process();
                            }

                            // make equirectangular image
                            EquirectagularConverter equirectangular = new EquirectagularConverter(file);

                            File equiRectFile = equirectangular.getTargetFile();
                            if (!equirectangular.euirectangularFileExists()) {
                                equirectangular.process(false);
                            }

                            // delete previous output
                            String fileName = FileUtils.getFileNameWithoutExtension(equiRectFile);
                            File oldArtefactDir = FileUtils.findDirectoryThatNameContains(equiRectFile.getParentFile(), fileName);
                            if (oldArtefactDir != null) {
                                FileUtils.deleteRecursive(oldArtefactDir);
                            }

                            // exec krpano
                            // TODO use properties
                            File krPano = new File("C:\\portableapps\\krpano-1.18.4\\krpanotools64.exe");
                            File krpanoConfigFile = new File("C:\\portableapps\\krpano-1.18.4\\templates\\multires.config");
                            KrPanoExecutor krpano = new KrPanoExecutor(krPano, krpanoConfigFile);
                            krpano.processImage(equiRectFile);

                            // delete unneeded tiles
                            File artefactDir = FileUtils.findDirectoryThatNameContains(equiRectFile.getParentFile(), fileName);
                            PanoTilesCleaner cleaner = new PanoTilesCleaner(artefactDir);
                            cleaner.process();

                            // change pano config xml
                            //File artefactDir = new File("R:\\TEMP\\result-test_equirectagular");
                            File config = FileUtils.findFileThatNameContains(artefactDir, ".xml");
                            if (config != null) {
                                KrPanoConfigFile krPanoConfigFile = new KrPanoConfigFile(config);
                                krPanoConfigFile.load();
                                krPanoConfigFile.setShowErrors(false);
                                krPanoConfigFile.setView(viewRange);
                                krPanoConfigFile.save();
                            }

                            // copy preview#
                            Files.copy(preview.getTargetFile().toPath(), new File(artefactDir, "preview.jpg").toPath());

                            // zip dir
                            String simpleName = PanoNameUtils.extractFirstImageName(artefactDir.getName());
                            File zipFile = new File(artefactDir.getParentFile(), simpleName + ".zip");
                            ZipUtils.compressDirectory(artefactDir, zipFile, simpleName);
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


    protected JButton createEquirectButton() {
        JButton button = createButton("<html>Equi<br>rect</html>");
        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EquirectagularConverter converter = new EquirectagularConverter(file);
                            converter.process(false);
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

    protected JButton createCleanButton() {
        JButton button = createButton("<html>Clean<br>Tiles</html>");
        button.addActionListener(new CloseListener(this));
        final FolderDropTarget dropTarget = new FolderDropTarget() {
            @Override
            public void onDropFolder(final File folder) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PanoTilesCleaner cleaner = new PanoTilesCleaner(folder);
                            cleaner.process();
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

    protected JButton createSnippetButton() {
        JButton button = createButton("<html>Snippet<br>File</html>");
        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PanoSnippetGenerator converter = new PanoSnippetGenerator(file);
                            converter.process();
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

    protected JButton createPreviewButton() {
        JButton button = createButton("<html>Preview</html>");

        button.addActionListener(new CloseListener(this));
        PsdDropTarget dropTarget = new PsdDropTarget() {
            @Override
            public void onPsdFile(final File file) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PreviewGenerator converter = new PreviewGenerator(file);
                            converter.process();
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
}
