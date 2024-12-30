package org.example;

import org.example.PDFPanel.PDFViewerPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;

public class ButtonEditorTabComponent extends JPanel {
    protected final DnDTabbedPane pane;
    protected UTextPane textPane;
    protected PDFViewerPanel pdfViewerPanel;
    protected ButtonEditorTabComponent thisTabComponent = this;

    public ButtonEditorTabComponent(final DnDTabbedPane pane, JLabel name, UTextPane textPane) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        this.textPane = textPane;
        setOpaque(false);

        name.setFont(Params.TAB_TITLE_FONT);
        add(name);
        //addMouseListener(tabMouseListener);
        //add more space between the label and the button
        name.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }


    public void copyPathToClipboard() {
        try {
            StringSelection selection = new StringSelection(textPane.filePath.getPath());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        } catch (Exception e) {
            System.err.println("Ошибка при копировании в буфер обмена: " + e.getMessage());
        }
    }

    public void saveTab() {
        if (textPane != null) {
            textPane.saveText();
        }
    }

    private final MouseListener tabMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            System.out.println(textPane.fileName);
            pane.setChoosingTab(thisTabComponent);
        }

        public void mouseExited(MouseEvent e) {

        }
    };

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonEditorTabComponent.this);
            if (i != -1) {
                if (textPane != null) {
                    textPane.stopCheckUpdatingFile();
                }
                if (pdfViewerPanel != null) {
                    try {
                        pdfViewerPanel.closeDocument();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                pane.remove(i);
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            if (Params.THEME.equals("Темная")) {
                g2.setColor(new Color(215, 215, 215));
            } else {
                g2.setColor(new Color(35, 35, 35));
            }
            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

    public void setPDFViewerPanel(PDFViewerPanel pdfViewerPanel) {
        this.pdfViewerPanel =  pdfViewerPanel;
    }

    public UTextPane getTextPane() {
        return textPane;
    }
}