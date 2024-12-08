package org.example;

import javax.swing.*;
import java.awt.*;

public class ImageLabel extends JLabel {

    public ImageLabel(ImageIcon icon) {
        super(icon);
    }

    public ImageLabel(String text) {
        super(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Включение высокого качества рендеринга
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем изображение
        Image img = ((ImageIcon) getIcon()).getImage();
        g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);

        g2d.dispose();
    }

//    @Override
//    public Dimension getPreferredSize() {
//        // Поддерживаем размер изображения как размер компонента
//        Image img = ((ImageIcon) getIcon()).getImage();
//        return new Dimension(img.getWidth(null), img.getHeight(null));
//    }
}
