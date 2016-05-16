package de.zebrajaeger.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is to make code smaller if there are much components that closes a dialog on ActionListeners.actionPerformed
 * Created by lars on 15.05.2016.
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
