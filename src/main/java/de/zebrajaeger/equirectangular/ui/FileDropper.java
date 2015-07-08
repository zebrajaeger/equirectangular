package de.zebrajaeger.equirectangular.ui;

import java.io.File;
import java.util.List;

import javax.swing.*;

/**
 * @author Lars Brandt
 */
public class FileDropper extends JDialog {

  private final FileDropTarget dropTarget;

  public FileDropper() {
    super();
    setTitle("equirectangular");
    setSize(200, 200);
    setAlwaysOnTop(true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    add(createLabel(" "));
    add(createLabel("drop"));
    add(createLabel(" "));
    add(createLabel("files"));
    add(createLabel(" "));
    add(createLabel("here"));

    dropTarget = new FileDropTarget();
    setDropTarget(dropTarget);
    dropTarget.addListener(new IFileDropListener() {
      @Override
      public boolean onAcceptDrop(List<File> files) {
        // accept all
        return true;
      }

      @Override
      public void onDrop(List<File> files) {
        for (File f : files) {
          System.out.println(f);
        }
      }
    });

    setVisible(true);
  }

  protected JLabel createLabel(String title) {
    JLabel res = new JLabel(title);
    res.setHorizontalAlignment(SwingConstants.CENTER);
    res.setAlignmentX(0.5f);
    return res;
  }


  /**
   * For debugging
   */
  public static void main(String[] args) {
    new FileDropper();
  }

  public void addListener(IFileDropListener listener) {
    dropTarget.addListener(listener);
  }

  public void removeListener(IFileDropListener listener) {
    dropTarget.removeListener(listener);
  }
}
