package org.example;


import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel(new FlatLightLaf());
            //UIManager.setLookAndFeel(new FlatMacLightLaf());
            //UIManager.setLookAndFeel(new FlatDarkLaf());

            UIManager.setLookAndFeel(new FlatIntelliJLaf());

            //UIManager.setLookAndFeel(new MetalLookAndFeel());
            //UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //JFrame.setDefaultLookAndFeelDecorated(true);
                new UDZNote();
            }
        });
    }
}