package de.zebrajaeger.equirectangular.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lars Brandt
 */
public class FileDropTarget extends DropTarget {

  private static final Logger LOG = LogManager.getLogger(FileDropTarget.class);

  private List<IFileDropListener> listeners = new ArrayList<>();

  public void addListener(IFileDropListener listener) {
    listeners.add(listener);
  }

  public void removeListener(IFileDropListener listener) {
    listeners.remove(listener);
  }

  @Override
  public synchronized void dragEnter(DropTargetDragEvent event) {
    checkDrag(event);
  }

  @Override
  public synchronized void dragOver(DropTargetDragEvent event) {
    checkDrag(event);
  }

  protected void checkDrag(DropTargetDragEvent event) {
    List<DataFlavor> flavors = event.getCurrentDataFlavorsAsList();

    if ((flavors.size() == 1) && DataFlavor.javaFileListFlavor.equals(flavors.get(0))) {
      event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
      //System.out.println("enter:accept");
    } else {
      event.acceptDrag(DnDConstants.ACTION_NONE);
      //System.out.println("enter:reject");
    }
  }

  @Override
  public synchronized void drop(DropTargetDropEvent event) {
    try {
      event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
      List<File> droppedFiles = castFileList(event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
      droppedFiles = Collections.unmodifiableList(droppedFiles);
      if (canAccept(droppedFiles)) {
        onAccept(droppedFiles);
      }
    } catch (Exception e) {
      LOG.error("could not deliver dropped files", e);
    }
  }

  @SuppressWarnings("unchecked")
  private static List<File> castFileList(Object o) {
    return (List<File>) o;
  }

  protected void onAccept(List<File> files) {
    for (IFileDropListener l : listeners) {
      l.onDrop(files);
    }
  }

  protected boolean canAccept(List<File> files) {
    for (IFileDropListener l : listeners) {
      if (!l.onAcceptDrop(files)) {
        return false;
      }
    }
    return true;
  }

}
