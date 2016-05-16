package de.zebrajaeger.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Accepts all droped Files that ends with .psd or .psb (not case sensitive)
 * <p>
 * Created by lars on 16.05.2016.
 */
public abstract class PsdDropTarget extends DropTarget {
    @Override
    public synchronized void dragEnter(DropTargetDragEvent event) {
        checkDrag(event);
    }

    @Override
    public synchronized void dragOver(DropTargetDragEvent event) {
        checkDrag(event);
    }

    public void checkDrag(DropTargetDragEvent event) {
        List<DataFlavor> flavors = event.getCurrentDataFlavorsAsList();

        if ((flavors.size() == 1) && DataFlavor.javaFileListFlavor.equals(flavors.get(0))) {
            event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        } else {
            event.acceptDrag(DnDConstants.ACTION_NONE);
        }
    }

    @Override
    public synchronized void drop(DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        List<File> droppedFiles = null;
        try {
            droppedFiles = castFileList(event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
        } catch (UnsupportedFlavorException ignore) {
        } catch (IOException ignore) {
        }

        for (File f : droppedFiles) {
            String name = f.getName().toLowerCase();
            if (f.isFile() && (name.endsWith(".psd") || name.endsWith(".psb"))) {
                onPsdFile(f);
            }
        }
    }

    public abstract void onPsdFile(File file);

    @SuppressWarnings("unchecked")
    private List<File> castFileList(Object o) {
        return (List<File>) o;
    }
}
