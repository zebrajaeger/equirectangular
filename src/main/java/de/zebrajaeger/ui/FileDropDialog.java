package de.zebrajaeger.ui;

import de.zebrajaeger.equirectagular.EquirectagularConverter;
import de.zebrajaeger.imgremove.PanoTilesCleaner;
import de.zebrajaeger.panosnippet.PanoSnippetGenerator;
import de.zebrajaeger.psdpreview.PreviewGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main Drop target
 * <p>
 * Created by lars on 14.05.2016.
 */
public class FileDropDialog extends JDialog {
    public static void main(String[] args) {
        new FileDropDialog().setVisible(true);
    }

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public FileDropDialog() {
        setUndecorated(true);
        setSize(100, 100);
        setLayout(new GridLayout(2, 2));
        setAlwaysOnTop(true);
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        add(createEquirectButton());
        add(createPreviewButton());
        add(createCleanButton());
        add(createSnippetButton());
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.PLAIN | Font.BOLD, 10));
        button.setMargin(new Insets(1, 1, 1, 1));
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
