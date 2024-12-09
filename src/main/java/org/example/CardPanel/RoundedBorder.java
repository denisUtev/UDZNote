package org.example.CardPanel;


import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorder implements Border {
    private final int radius;
    private Color color = Color.GRAY;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    public void setColor(Color col) {
        color = col;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}
