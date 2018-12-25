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
 * Accepts all file dropt that are folders
 * <p>
 * @author Lars Brandt on 16.05.2016.
 */
public abstract class FolderDropTarget extends DropTarget {
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
            //System.out.println("enter:accept");
        } else {
            event.acceptDrag(DnDConstants.ACTION_NONE);
            //System.out.println("enter:reject");
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
            if (f.isDirectory()) {
                onDropFolder(f);
            }
        }
    }

    public abstract void onDropFolder(File folder
    );

    @SuppressWarnings("unchecked")
    private List<File> castFileList(Object o) {
        return (List<File>) o;
    }
}
