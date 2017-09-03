package FarisImageViewer;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

public class ImagePreviewListTest {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        JFrame frame = new JFrame("Image panel");
        frame.setSize(800, 300);
        frame.setLocationByPlatform(true);
        JList imageList = createImageList();
        frame.getContentPane().add(new JScrollPane(imageList));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static JList createImageList() {

        JList imageList = new JList(createModel());
        imageList.setCellRenderer(new ImageCellRenderer());
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.setVisibleRowCount(-1);
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageList.setFixedCellWidth(240);
        imageList.setFixedCellHeight(120);

        imageList.setDragEnabled(true);
        imageList.setDropMode(DropMode.INSERT);
        imageList.setTransferHandler(new ImageTransferHandler(imageList));

        return imageList;
}

    private static DefaultListModel createModel() {
        DefaultListModel model = new DefaultListModel();
        model.addElement(new ColorIcon(Color.RED));
        model.addElement(new ColorIcon(Color.GREEN));
        model.addElement(new ColorIcon(Color.BLACK));
        model.addElement(new ColorIcon(Color.BLUE));
        model.addElement(new ColorIcon(Color.MAGENTA));
        model.addElement(new ColorIcon(Color.PINK));
        model.addElement(new ColorIcon(Color.YELLOW));
        model.addElement(new ColorIcon(Color.ORANGE));
        return model;
    }

    static class ImageTransferHandler extends TransferHandler {

        private static final DataFlavor DATA_FLAVOUR = new DataFlavor(ColorIcon.class, "Images");

        private final JList previewList;
        private boolean inDrag;

        ImageTransferHandler(JList previewList) {
            this.previewList = previewList;
        }

        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        protected Transferable createTransferable(JComponent c) {
            inDrag = true;
            return new Transferable() {
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[] {DATA_FLAVOUR};
                }

                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(DATA_FLAVOUR);
                }

                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    return previewList.getSelectedValue();
                }
            };
        }

        public boolean canImport(TransferSupport support) {
            if (!inDrag || !support.isDataFlavorSupported(DATA_FLAVOUR)) {
                return false;
            }

            JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
            if (dl.getIndex() == -1) {
                return false;
            } else {
                return true;
            }
        }

        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable transferable = support.getTransferable();
            try {
                Object draggedImage = transferable.getTransferData(DATA_FLAVOUR);

                JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
                DefaultListModel model = (DefaultListModel)previewList.getModel();
                int dropIndex = dl.getIndex();
                if (model.indexOf(draggedImage) < dropIndex) {
                    dropIndex--;
                }
                model.removeElement(draggedImage);
                model.add(dropIndex, draggedImage);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void exportDone(JComponent source, Transferable data, int action) {
            super.exportDone(source, data, action);
            inDrag = false;
        }
    }

    static class ImageCellRenderer extends JPanel implements ListCellRenderer {

        DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
        JLabel imageLabel = new JLabel();
        JLabel descriptionLabel = new JLabel();

        ImageCellRenderer() {
            setLayout(new BorderLayout());
            Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
            imageLabel.setBorder(emptyBorder);
            descriptionLabel.setBorder(emptyBorder);
            add(imageLabel, BorderLayout.CENTER);
            add(descriptionLabel, BorderLayout.SOUTH);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            defaultListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(defaultListCellRenderer.getBorder());
            setBackground(defaultListCellRenderer.getBackground());
            imageLabel.setIcon((Icon)value);
            descriptionLabel.setText("Description");
            return this;
        }
    }

    static class ColorIcon implements Icon, Serializable {
        private Color color;

        ColorIcon(Color color) {
            this.color = color;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, getIconWidth(), getIconHeight());
        }

        public int getIconWidth() {
            return 200;
        }

        public int getIconHeight() {
            return 100;
        }

        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return color.equals(((ColorIcon)o).color);
        }

    }
}