package org.example;

import javax.swing.*;

public class SettingsFrame {

    private JFrame frame;

    public SettingsFrame() {
        initFrame();
    }

    private void initFrame() {
        frame = new JFrame("Settings");
        frame.setSize(400, 400);
        frame.setTitle("Settings");
        frame.setVisible(true);

        //Display the window.
        //frame.pack();
        frame.setVisible(true);
    }
}
