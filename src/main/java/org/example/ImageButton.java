package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.example.UDZNote.WORKING_DIR;

public class ImageButton extends JButton {

    private final BufferedImage image;

    public ImageButton(String pathToImage) {
        super();
        try {
            this.image = ImageIO.read(new File(pathToImage));
            Dimension preferredSize = new Dimension(30, 30);
            setMinimumSize(preferredSize);
            setMaximumSize(preferredSize);
            setPreferredSize(preferredSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();

        // Настройки для улучшения качества изображения
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем изображение
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);

        g2d.dispose();
    }
}
