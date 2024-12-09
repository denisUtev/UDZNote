package org.example;

import org.example.TabPaneActions.AddDescriptionAction;
import org.example.TabPaneActions.AddInBookMarkAction;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class DnDTabbedPane extends JTabbedPane {
    private static final int LINEWIDTH = 3;
    private static final String NAME = "test";
    private final GhostGlassPane glassPane = new GhostGlassPane();
    private final Rectangle lineRect = new Rectangle();
    private final Color lineColor = new Color(0, 100, 255);
    private int dragTabIndex = -1;

    private ButtonEditorTabComponent choosingTab;

    private void clickArrowButton(String actionKey) {
        ActionMap map = getActionMap();
        if(map != null) {
            Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(
                        this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }

    public void setChoosingTab(ButtonEditorTabComponent tab) {
        choosingTab = tab;
    }

    public ButtonEditorTabComponent getChoosingTab() {
        return choosingTab;
    }

    //private static Rectangle rBackward = new Rectangle();
    //private static Rectangle rForward  = new Rectangle();
    private static int rwh = 20;
    private static int buttonsize = 300;//XXX: magic number of scroll button size
    private void autoScrollTest(Point glassPt) {
        Rectangle r = getTabAreaBounds();
        int tabPlacement = getTabPlacement();
        if(tabPlacement==TOP || tabPlacement==BOTTOM) {
//            rBackward.setBounds(r.x, r.y, rwh, r.height);
//            rForward.setBounds(
//                    r.x+r.width-rwh-buttonsize, r.y, rwh+buttonsize, r.height);
        }else if(tabPlacement==LEFT || tabPlacement==RIGHT) {
//            rBackward.setBounds(r.x, r.y, r.width, rwh);
//            rForward.setBounds(
//                    r.x, r.y+r.height-rwh-buttonsize, r.width, rwh+buttonsize);
        }
//        rBackward = SwingUtilities.convertRectangle(
//                getParent(), rBackward, glassPane);
//        rForward  = SwingUtilities.convertRectangle(
//                getParent(), rForward,  glassPane);
//        if(rBackward.contains(glassPt)) {
//            //System.out.println(new java.util.Date() + "Backward");
//            clickArrowButton("scrollTabsBackwardAction");
//        }else if(rForward.contains(glassPt)) {
//            //System.out.println(new java.util.Date() + "Forward");
//            clickArrowButton("scrollTabsForwardAction");
//        }
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        JMenuItem saveTabAction = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (choosingTab != null) {
                    choosingTab.saveTab();
                }
            }
        });
        saveTabAction.setText("Сохранить");
        JMenuItem copyPath = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (choosingTab != null) {
                    choosingTab.copyPathToClipboard();
                }
            }
        });
        copyPath.setText("Скопировать путь");
        JMenuItem addDescription = new JMenuItem();
        addDescription.setAction(new AddDescriptionAction(this));
        pm.add(addDescription);
        JMenuItem addInBookMark = new JMenuItem();
        addInBookMark.setAction(new AddInBookMarkAction(this));
        pm.add(addDescription);
        pm.add(addInBookMark);
        pm.add(saveTabAction);
        pm.add(copyPath);
        return pm;
    }

    public DnDTabbedPane(int x, int y) {
        super(x, y);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tabIndex = indexAtLocation(e.getX(), e.getY());
                if (tabIndex != -1) {
                    setChoosingTab((ButtonEditorTabComponent) getTabComponentAt(tabIndex));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int tabIndex = indexAtLocation(e.getX(), e.getY());
                if (tabIndex != -1) {
                    setChoosingTab((ButtonEditorTabComponent) getTabComponentAt(tabIndex));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int tabIndex = indexAtLocation(e.getX(), e.getY());
                if (tabIndex != -1) {
                    setChoosingTab((ButtonEditorTabComponent) getTabComponentAt(tabIndex));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });


        setComponentPopupMenu(createPopupMenu());
        final DragSourceListener dsl = new DragSourceListener() {
            @Override public void dragEnter(DragSourceDragEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            }
            @Override public void dragExit(DragSourceEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                lineRect.setRect(0,0,0,0);
                glassPane.setPoint(new Point(-1000,-1000));
                glassPane.repaint();
            }
            @Override public void dragOver(DragSourceDragEvent e) {
                Point glassPt = e.getLocation();
                SwingUtilities.convertPointFromScreen(glassPt, glassPane);
                int targetIdx = getTargetTabIndex(glassPt);
                //if(getTabAreaBounds().contains(tabPt) && targetIdx>=0 &&
                if(getTabAreaBounds().contains(glassPt) && targetIdx>=0 &&
                        targetIdx!=dragTabIndex && targetIdx!=dragTabIndex+1) {
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
                    glassPane.setCursor(DragSource.DefaultMoveDrop);
                }else{
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                    glassPane.setCursor(DragSource.DefaultMoveNoDrop);
                }
            }
            @Override public void dragDropEnd(DragSourceDropEvent e) {
                lineRect.setRect(0,0,0,0);
                dragTabIndex = -1;
                glassPane.setVisible(false);
                if(hasGhost()) {
                    glassPane.setVisible(false);
                    glassPane.setImage(null);
                }
            }
            @Override public void dropActionChanged(DragSourceDragEvent e) {}
        };
        final Transferable t = new Transferable() {
            private final DataFlavor FLAVOR = new DataFlavor(
                    DataFlavor.javaJVMLocalObjectMimeType, NAME);
            @Override public Object getTransferData(DataFlavor flavor) {
                return DnDTabbedPane.this;
            }
            @Override public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] f = new DataFlavor[1];
                f[0] = this.FLAVOR;
                return f;
            }
            @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.getHumanPresentableName().equals(NAME);
            }
        };
        final DragGestureListener dgl = new DragGestureListener() {
            @Override public void dragGestureRecognized(DragGestureEvent e) {
                if(getTabCount() <= 1) return;
                Point tabPt = e.getDragOrigin();
                dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
                // "disabled tab problem".
                if(dragTabIndex < 0 || !isEnabledAt(dragTabIndex)) return;
                initGlassPane(e.getComponent(), e.getDragOrigin());
                try{
                    e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
                }catch(InvalidDnDOperationException idoe) {
                    idoe.printStackTrace();
                }
            }
        };
        new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE,
                new CDropTargetListener(), true);
        new DragSource().createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
    }

    class CDropTargetListener implements DropTargetListener{
        @Override public void dragEnter(DropTargetDragEvent e) {
            if(isDragAcceptable(e)) e.acceptDrag(e.getDropAction());
            else e.rejectDrag();
        }
        @Override public void dragExit(DropTargetEvent e) {}
        @Override public void dropActionChanged(DropTargetDragEvent e) {}

        private Point _glassPt = new Point();
        @Override public void dragOver(final DropTargetDragEvent e) {
            Point glassPt = e.getLocation();
            if(getTabPlacement()==JTabbedPane.TOP ||
                    getTabPlacement()==JTabbedPane.BOTTOM) {
                initTargetLeftRightLine(getTargetTabIndex(glassPt));
            }else{
                initTargetTopBottomLine(getTargetTabIndex(glassPt));
            }
            if(hasGhost()) {
                glassPane.setPoint(glassPt);
            }
            if(!_glassPt.equals(glassPt)) glassPane.repaint();
            _glassPt = glassPt;
            autoScrollTest(glassPt);
        }

        @Override public void drop(DropTargetDropEvent e) {
            if(isDropAcceptable(e)) {
                convertTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
                e.dropComplete(true);
            }else{
                e.dropComplete(false);
            }
            repaint();
        }
        private boolean isDragAcceptable(DropTargetDragEvent e) {
            Transferable t = e.getTransferable();
            if(t==null) return false;
            DataFlavor[] f = e.getCurrentDataFlavors();
            if(t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }
        private boolean isDropAcceptable(DropTargetDropEvent e) {
            Transferable t = e.getTransferable();
            if(t==null) return false;
            DataFlavor[] f = t.getTransferDataFlavors();
            if(t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }
    }

    private boolean hasGhost = true;
    public void setPaintGhost(boolean flag) {
        hasGhost = flag;
    }
    public boolean hasGhost() {
        return hasGhost;
    }
    private boolean isPaintScrollArea = true;
    public void setPaintScrollArea(boolean flag) {
        isPaintScrollArea = flag;
    }
    public boolean isPaintScrollArea() {
        return isPaintScrollArea;
    }

    private int getTargetTabIndex(Point glassPt) {
        Point tabPt = SwingUtilities.convertPoint(
                glassPane, glassPt, DnDTabbedPane.this);
        boolean isTB = getTabPlacement()==JTabbedPane.TOP ||
                getTabPlacement()==JTabbedPane.BOTTOM;
        for(int i=0;i < getTabCount();i++) {
            Rectangle r = getBoundsAt(i);
            if(isTB) r.setRect(r.x-r.width/2, r.y,  r.width, r.height);
            else   r.setRect(r.x, r.y-r.height/2, r.width, r.height);
            if(r.contains(tabPt)) return i;
        }
        Rectangle r = getBoundsAt(getTabCount()-1);
        if(isTB) r.setRect(r.x+r.width/2, r.y,  r.width, r.height);
        else   r.setRect(r.x, r.y+r.height/2, r.width, r.height);
        return   r.contains(tabPt)?getTabCount():-1;
    }
    private void convertTab(int prev, int next) {
        if(next < 0 || prev==next) {
            return;
        }
        Component cmp = getComponentAt(prev);
        Component tab = getTabComponentAt(prev);
        String str  = getTitleAt(prev);
        Icon icon   = getIconAt(prev);
        String tip  = getToolTipTextAt(prev);
        boolean flg   = isEnabledAt(prev);
        int tgtindex  = prev>next ? next : next-1;
        remove(prev);
        insertTab(str, icon, cmp, tip, tgtindex);
        setEnabledAt(tgtindex, flg);
        //When you drag'n'drop a disabled tab, it finishes enabled and selected.
        //pointed out by dlorde
        if(flg) setSelectedIndex(tgtindex);

        //I have a component in all tabs (jlabel with an X to close the tab)
        //and when i move a tab the component disappear.
        //pointed out by Daniel Dario Morales Salas
        setTabComponentAt(tgtindex, tab);
    }

    private void initTargetLeftRightLine(int next) {
        if(next < 0 || dragTabIndex==next || next-dragTabIndex==1) {
            lineRect.setRect(0,0,0,0);
        }else if(next==0) {
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(0), glassPane);
            lineRect.setRect(r.x-LINEWIDTH/2,r.y,LINEWIDTH,r.height);
        }else{
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(next-1), glassPane);
            lineRect.setRect(r.x+r.width-LINEWIDTH/2,r.y,LINEWIDTH,r.height);
        }
    }
    private void initTargetTopBottomLine(int next) {
        if(next < 0 || dragTabIndex == next || next-dragTabIndex==1) {
            lineRect.setRect(0,0,0,0);
        }else if (next == 0) {
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(0), glassPane);
            lineRect.setRect(r.x,r.y-LINEWIDTH/2,r.width,LINEWIDTH);
        }else{
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(next-1), glassPane);
            lineRect.setRect(r.x,r.y+r.height-LINEWIDTH/2,r.width,LINEWIDTH);
        }
    }

    private void initGlassPane(Component c, Point tabPt) {
        getRootPane().setGlassPane(glassPane);
        if(hasGhost()) {
            /////////////////////////////////////////////////////////////////////////
            c = getTabComponentAt(dragTabIndex);
            Component copy = Optional.ofNullable(c)
                    .orElseGet(() -> new JLabel(getTitleAt(dragTabIndex)));
            Dimension d = copy.getPreferredSize();
            BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            SwingUtilities.paintComponent(g2, copy, glassPane, 0, 0, d.width, d.height);
            g2.dispose();
            glassPane.setImage(image);
            if (c != null) {
                // pointed out by idarwin
                // https://github.com/aterai/java-swing-tips/issues/11
                // SwingUtilities.paintComponent(...) method adds a tab component to the GlassPane
                // to draw the tab, and the tab component being dragged is removed from the tab.
                // Therefore, the tab component needs to be re-configured.
                setTabComponentAt(dragTabIndex, c);
            }
        }
        Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
        glassPane.setPoint(glassPt);
        glassPane.setVisible(true);
    }

    private Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        // pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
        // Rectangle compRect   = getSelectedComponent().getBounds();
        Component comp = getSelectedComponent();
        int idx = 0;
        while(comp==null && idx < getTabCount()) comp = getComponentAt(idx++);
        Rectangle compRect = (comp==null)?new Rectangle():comp.getBounds();
        int tabPlacement = getTabPlacement();
        if(tabPlacement==TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        }else if(tabPlacement==BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        }else if(tabPlacement==LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        }else if(tabPlacement==RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        tabbedRect.grow(2, 2);
        return tabbedRect;
    }
    class GhostGlassPane extends JPanel {
        private final AlphaComposite composite;
        private Point location = new Point(0, 0);
        private BufferedImage draggingGhost = null;
        public GhostGlassPane() {
            setOpaque(false);
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            //[JDK-6700748] Cursor flickering during D&D when using CellRendererPane with validation - Java Bug System
            //https://bugs.openjdk.java.net/browse/JDK-6700748
            //setCursor(null);
        }
        public void setImage(BufferedImage draggingGhost) {
            this.draggingGhost = draggingGhost;
        }
        public void setPoint(Point location) {
            this.location = location;
        }
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(composite);
            if(isPaintScrollArea() && getTabLayoutPolicy()==SCROLL_TAB_LAYOUT) {
                g2.setPaint(Color.RED);
//                g2.fill(rBackward);
//                g2.fill(rForward);
            }
            if(draggingGhost != null) {
                double xx = location.getX() - (draggingGhost.getWidth(this) /2d);
                double yy = location.getY() - (draggingGhost.getHeight(this)/2d);
                g2.drawImage(draggingGhost, (int)xx, (int)yy , null);
            }
            if(dragTabIndex>=0) {
                g2.setPaint(lineColor);
                g2.fill(lineRect);
            }
        }
    }
}