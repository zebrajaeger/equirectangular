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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is to make code smaller if there are much components that closes a dialog on ActionListeners.actionPerformed
 * @author Lars Brandt on 15.05.2016.
 */
class CloseListener implements ActionListener {
    private JDialog toClose;

    public CloseListener(JDialog toClose) {
        this.toClose = toClose;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toClose.dispose();
    }
}
