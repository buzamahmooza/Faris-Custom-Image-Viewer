package farisImageViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;

import org.imgscalr.Scalr;

import fileDrop.FileDrop;

//import org.jdesktop.jdic.filetypes.Association;
//import org.jdesktop.jdic.filetypes.AssociationAlreadyRegisteredException;
//import org.jdesktop.jdic.filetypes.AssociationNotRegisteredException;
//import org.jdesktop.jdic.filetypes.AssociationService;
//import org.jdesktop.jdic.filetypes.RegisterFailedException;

/** @author Buza */
/** @author Buza */
@SuppressWarnings("serial")
public class FViewer extends FFrame {

	private static final long serialVersionUID = 4930774245758004399L;
	// Settings
	static double MIN_ZOOM_FACTOR = 2;
	static double MAX_ZOOM_FACTOR = 10;
	static double zoom = 1;
	static int SCROLL_SPEED = 40, imagePropertiesSize = 20;
	// Codes for Default_Image_Align
	// IMAGE_ALIGN_FIT = 0
	// IMAGE_ALIGN_ORIGINAL = 1
	// ENSURE_ALL_IS_SHOWING = 2
	// IMAGE_ALIGN_RESIZE_WINDOW = 4
	static int defaultImageAlign = 2;
	static boolean zoomTowardMouse = false;
	static boolean loopImages = true;
	static boolean ShowHiddenFiles = true;
	static boolean startMaximized = false;
	static boolean includeDirectories = true;

	private static FDimension imgDim;
	private static PicPanel picPanel;
	private static JMenuItem mi_GoToPath, mi_OpenPhoto;
	private static JLabel propertiesLabel;
	static ImageFunctions im;
	private static File file;
	private static boolean directionRight = true;

	private static List<File> fileList;
	private final static String PICTURES_PATH = System.getProperty("user.home") + File.separator + "Pictures";
	private final static String[] DEFAULT_IMAGES = {
			"C:/Users/faris/Pictures/!Wallpapers/540055.jpg", PICTURES_PATH, "C:/Users/faris/OneDrive/Pictures/vlc snap (2).png",
			"C:/Users/faris/Pictures/League Of Legends Jinx Wallpaper/league-of-legends-jinx-wallpaper-15.jpg",
			"D:/Buza/Torrents/collexion/7160aa238968e174e0a1c4a1f09833cc30c49a305da0a7a665b6ba9b5a2faa92.gif", "G:/collexion/DGGs0TfUQAA7n_D.jpg"
	};

	static Point target;
	private Image[] cacheList;
	static Point mousePoint;
	private static int idx = 0;

	private static FileNameExtensionFilter imageFileNameExtensionFilter = new FileNameExtensionFilter("jpg", "JPEG file", "jpeg", "gif", "bmp", "tiff", "png", "jfif");
	private static final String[] EXTENSIONS_ARRAY = {
			"jpg", "JPEG file", "jpeg", "gif", "bmp", "tiff", "png", "jfif"
	};
	/* private static final String EXTENSIONS_STRING =
	 * "jpg, JPEG file, jpeg, gif, bmp, tiff, png, jfif"; private static final
	 * String IMAGE_PATTERN = "((//.(?i)(jpg|png|gif|bmp))$)"; private static final
	 * String FOLDER_ICON =
	 * "C:/Users/faris/OneDrive/Pictures/Folder IconPNG.PNG"; */
	private static final int IMAGE_DUPLICATE_THRESHOLD = 1000;
	private static FViewer window;
	private static String defaultImagePath =
			// DEFAULT_IMAGES[0]
			// "C:/Users/faris/Pictures/emilia dancing gif _ Google Search";
			// "C:/Users/faris/Pictures/Dva/724397.jpg";
			// "C:/Users/faris/Pictures/vlc snap (3).png";
			// "C:/Users/faris/Pictures/_NSFW";
			// "tmp/3fcb1fcc2bbca8da16972ebe3658f9709e669cbe.jpg";
			"C:/Users/faris/Pictures/!Wallpapers/cyclops-2880x1800-x-men-ford-shelby-mustang-gt350r-hd-1072.jpg";
	// "c:/users/faris/pictures/58373817_p0.jpg";
	static final Dimension PICTURE_DIMENSION_TOO_SMALL = new Dimension(200, 200);

	@SuppressWarnings("static-access")

	public static void main(String[] args) {
		Dimension windowDimension = new Dimension(SCREEN_DIMENSION_HALF);
		setLookAndFeel();
		attemptToUseArgs(args);
		ImageIcon ic = new ImageIcon(defaultImagePath);
		windowDimension = FDimension.ensureSize(new Dimension(ic.getIconWidth(), ic.getIconHeight()), FDimension.multiplyByFactor(SCREEN_DIMENSION, 0.3));

		window = new FViewer("Faris'es awesome imageLabel viewer", windowDimension);
		window.im.defaultAlignOnLoad();
		window.centerPosition();
		window.addToSize(-20);
	}

	public FViewer(String title, Dimension d) {
		super(title, d);
		window = this;
		im = new ImageFunctions();
		file = new File(defaultImagePath);
		imgDim = new FDimension(this.getSize());
		fileList = new ArrayList<File>();
		readProperties();
		System.out.println("Default image: " + file.getPath());
		this.setMinimumSize(new Dimension(400, 400));
		this.setPreferredSize(new Dimension(800, 800));
		readFilesToFilesList();
		addFilesFromDirectory(file, fileList);
		createAndShowGUI();

		// File drop
		new FileDrop(System.out, picPanel, new FileDrop.Listener() {

			public void filesDropped(File[] files) {
				for (int i = 0; i < files.length; i++) {
					System.out.println("File dropped\t[" + files[i] + "]");
					readFilesToFilesList(files[i]);
					// addFile(files[i]);
					idx = fileList.indexOf(files[i]);
				}
				im.loadPic();
				/* if (i > 0) if (file.isDirectory()) { File childFile = new
				  File(file.list()[0]); if (childFile != null) file = childFile; } else { file
				  = fileList.get(fileList.size() - 1); readFilesToArrayList(); } */
			} // end filesDropped
		});
		// end FileDrop.Listener

		centerPosition();
		// clearAllFromDuplicates();
	}

	private void createAndShowGUI() {
		// if (startMaximized)
		winMaximize();
		this.getContentPane().setLayout(new BorderLayout());

		picPanel = new PicPanel();
		// By default the zoom target is the center
		target = picPanel.getCenter();
		this.getContentPane().setBackground(Color.BLACK);
		picPanel.setBackground(Color.BLACK);
		// controlsPanel = new JPanel();

		// Menu
		JMenuBar bar = new JMenuBar();
		mi_OpenPhoto = new JMenuItem("Open photo");
		JMenuItem mi_ThumbView = new JMenuItem("ThumbView");
		propertiesLabel = new JLabel("Image Properties");
		mi_GoToPath = new JMenuItem();
		mi_GoToPath.setActionCommand("Open File Location");

		mi_ThumbView.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		mi_GoToPath.setPreferredSize(new Dimension(100, 30));

		bar.add(mi_GoToPath);
		// bar.add(mi_OpenPhoto);
		// bar.add(mi_ThumbView);
		this.setJMenuBar(bar);

		picPanel.add(propertiesLabel);
		this.getContentPane().add(picPanel, BorderLayout.CENTER);

		// centerAlignJMenuItems();

		// If you want a scrollable thumb view
		// add(new JScrollPane(thumbViewPanel), BorderLayout.CENTER);

		this.addKeyListener(this);
		this.addComponentListener(this);
		this.addWindowListener(this);
		picPanel.addMouseWheelListener(this);
		mi_ThumbView.addActionListener(this);
		mi_OpenPhoto.addActionListener(this);
		mi_GoToPath.addActionListener(this);

		this.setVisible(true);
		im.loadPic();

	}

	private void centerAlignJMenuItems() {
		// Setting alignment for all menu items
		JMenuBar menubar1 = getJMenuBar();
		for (int j = 0; j < menubar1.getComponentCount(); j++) {
			java.awt.Component comp = menubar1.getComponent(j);
			if (comp instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) comp;
				adjustSizeToText(menuItem);
				menuItem.setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
	}

	// meh
	private static void adjustSizeToText(JMenuItem menuItem) {
		int stringWidth = menuItem.getFontMetrics(menuItem.getFont()).stringWidth(menuItem.getText());
		menuItem.setPreferredSize(new Dimension(stringWidth, (int) menuItem.getPreferredSize().getHeight()));
	}

	private void readFilesToFilesList() {
		// fileList = new ArrayList<File>();
		addFile(file);
		idx = fileList.indexOf(file);

		if (!file.isDirectory())
			readFilesToFilesList(file.getParentFile());
	}

	private void readFilesToFilesList(File sourceFile) {
		try {
			StackTraceElement s = Thread.currentThread().getStackTrace()[1];
			System.out.printf("%s (%s:%s)%n", "ReadFiles()", s.getFileName(), s.getLineNumber());

			addFile(sourceFile);
			List<File> list = getFilesFromBaseDirectory(sourceFile);
			// list.add(0, sourceFile);
			// file.getParentFile().listFiles();
			for (File f : list)
				if (!fileList.contains(f))
					addFile(f);

			// Safety check to make sure the file exists
			/*if (fileList.size() < 1) {
			System.err.println("No neighbor files found in: " + file.getPath() + "\nMoving DOWN one directory."); // moving
			File[] childFiles = file.listFiles();
			for (int i = 0; i < childFiles.length; i++) {
			if (childFiles[i] != null) {
			file = childFiles[i];
			readFilesToFilesList();
			return; } } }*/
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/** @param baseDirectory
	 * @return
	 * @see #addFilesFromDirectory(File, List) */
	public List<File> getFilesFromBaseDirectory(File baseDirectory) {
		List<File> list = new ArrayList<File>(50);
		addFilesFromDirectory(baseDirectory, list);
		return (list);
	}

	/** Recursive method that adds files to the <code>list</code><br>
	 * If <code>file</code> is a directory, then the method will add all the files in it.<br>
	 * If <code>file</code> is a file, it will simply be added.
	 * 
	 * @param file
	 * @param list
	 */
	private void addFilesFromDirectory(File file, List<File> list) {
		if (list.contains(file))
			return;
		try {
			if (file.isDirectory()) {
				System.out.println("Adding Directory:\t" + file.getName());
				for (File f : file.listFiles()) {
					if (!list.contains(file))
						addFilesFromDirectory(f, list);
				}
			} else { // If file
				if (!conditionsToIncludeFile(file))
					return;
				System.out.println("Adding File:\t\t" + file.getName());
				list.add(file);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void addFile(File f) {
		fileList.add(f);
		System.out.println((f.isFile() ? "File" : "Directory") + " added:\t" + f.getName());
	}

	private boolean conditionsToIncludeFile(File f) {
		/* if (f.isFile()) { // If image if (!f.getName().matches(IMAGE_PATTERN)) {
		 * fileList.add(f); System.out.println("File added:/t " + f.getAbsolutePath());
		 * } else System.out.println("[Not a file]:/t " + f.getAbsolutePath()); } else
		 * if (f.isDirectory()) { System.out.println("Directory:/t " +
		 * f.getAbsolutePath()); } */
		boolean condition = im.getImageDimension().isLargerThan(PICTURE_DIMENSION_TOO_SMALL) && (!f.isHidden() || ShowHiddenFiles) && isImage(f) || (f.isDirectory() && includeDirectories);
		if (!condition)
			System.out.println("[Rejected]:\t" + file.getName());
		return condition;
	}

	private static boolean isImage(File f) {
		/* String mimetype = new MimetypesFileTypeMap().getContentType(f); String type =
		 * mimetype.split("/")[0]; if (type.equals("image")) { return true; }
		 * 
		 * for (String extension : EXTENSIONS_ARRAY) { if
		 * (!f.getName().matches(extension)) continue; fileList.add(f);
		 * System.out.println(f.getAbsolutePath()); }
		 * System.out.println("[Not a file] Excluded: " + f.getAbsolutePath()); */

		for (String ext : EXTENSIONS_ARRAY) {
			if (f.getAbsolutePath().endsWith(ext))
				return true;
		}
		return false;
	}

	// <Classes>

	static class ImageFunctions {

		static final int IMAGE_ALIGN_FIT = 0, IMAGE_ALIGN_ORIGINAL = 1, ENSURE_ALL_IS_SHOWING = 2, IMAGE_ALIGN_RESIZE_WINDOW = 4;

		/** @param the
		 *            parameter is what stays constant ie. the other dimension will be changed */
		public void updateImage(int dimensionToPreserve) {
			if (file == null) {
				System.err.println("Icon is null!!");
				return;
			}

			final ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
			Image img = imageIcon.getImage();
			double aspect = FDimension.getAspect(img.getWidth(null), img.getHeight(null));

			// Preserve width or height
			if (imgDim.width == dimensionToPreserve)
				imgDim.height = (int) (imgDim.width / aspect);
			if (imgDim.height == dimensionToPreserve)
				imgDim.width = (int) (imgDim.height * aspect);

			// Clamp zoom
			if (imgDim.width < MIN_ZOOM_FACTOR * imageIcon.getIconWidth())
				imgDim.fitToWidth((int) MIN_ZOOM_FACTOR * imageIcon.getIconWidth());
			if (imgDim.width > MAX_ZOOM_FACTOR * imageIcon.getIconWidth())
				imgDim.fitToWidth((int) MAX_ZOOM_FACTOR * imageIcon.getIconWidth());

			// Updating target
			target = zoomTowardMouse ? mousePoint : picPanel.getCenter();

			picPanel.revalidate();
			picPanel.repaint();
		}

		// fit, original, ensureAllIsShowing, ResizeWindow
		private void alignFit() {
			imgDim.fitToContainer(picPanel.getSize());
			target = picPanel.getCenter();
			updateImage(imgDim.width);
		}

		private void alignOriginalSize() {
			ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
			imgDim = new FDimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
		}

		private void alignFitIfTooBig() {
			if (imgDim.getWidth() > picPanel.getWidth() || imgDim.getHeight() > picPanel.getHeight())
				alignFit();
		}

		private void alignResizeWindowToFitImage() {
			// window.setSizeBorderless(imgDim);
			window.setSize(imgDim, null);
			// window.centerPosition();
		}

		private void defaultAlignOnLoad() {
			switch (defaultImageAlign) {
			case IMAGE_ALIGN_FIT:
				Dimension iconDimension = getImageDimension();
				double compareVal = window.getHeight() - iconDimension.getHeight();
				if (compareVal > 700) {
					alignOriginalSize();
					// System.out.println( "Not gonna align fit cuz the
					// difference in magnitudes is large\n" +
					// "Gonna show original size instead.
					// ["+compareVal+"]");
					break;
				}
				alignFit();
				break;
			case IMAGE_ALIGN_ORIGINAL:
				alignOriginalSize();
				break;
			case ENSURE_ALL_IS_SHOWING:
				alignFitIfTooBig();
				break;
			case IMAGE_ALIGN_RESIZE_WINDOW:
				alignOriginalSize();
				alignResizeWindowToFitImage();
				break;
			default:
				System.err.println("Invalid default imageAlignOperation: " + defaultImageAlign);

				imgDim.stretch(zoom);
			}
		}

		private void defaultAlignOnResize() {
			// alignResizeWindowToFitImage();
		}

		private void loadPic() {
			final int lastIndex = fileList.size() - 1;
			// if (cacheList == null) {
			// cacheList = new Image[21];
			// cacheList[cacheList.length / 2 + 1] = createImage(1, lastIndex);
			// }
			if (idx > lastIndex) {// if went further than the end
				if (loopImages)
					idx = 0;
				else {
					idx = lastIndex;
					return;
				}
			} else if (idx < 0) { // if went before the beginning
				if (loopImages)
					idx = lastIndex;
				else {
					idx = 0;
					return;
				}
			}
			file = fileList.get(idx);

			mi_GoToPath.setText(file.getAbsolutePath());

			updateImage(Integer.max(imgDim.width, imgDim.height));

			defaultAlignOnLoad();
			imgDim.addToSize(zoom, zoom * imgDim.getAspect());
			adjustSizeToText(mi_OpenPhoto);
			propertiesLabel.setText(getImageDetails());
			window.revalidate();
			window.repaint();
			System.out.println("LoadedPic: " + file.getAbsolutePath());
		}

		private void nextPic() {
			idx++;
			directionRight = true;
			loadPic();
		}

		private void prevPic() {
			idx--;
			directionRight = false;
			loadPic();
		}

		private void recycleImage() {
			try {
				File[] fff = {
						file
				};
				new com.sun.jna.platform.win32.W32FileUtils().moveToTrash(fff);
			} catch (IOException e) {
				e.printStackTrace();
				new Popup(e.getMessage());
			}
			window.readFilesToFilesList();
			iterateInDirection();
			System.out.println("Recycled image");
		}

		private void deleteImage() {
			try {
				boolean result = file.delete();
				if (!result) {
					String msg = "Could not delete";
					System.err.println(msg);
					new Popup(msg);
					return;
				}
				window.readFilesToFilesList();
				iterateInDirection();
				String msg = "Deleted image";
				System.out.println(msg);
				new Popup(msg);
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		private void iterateInDirection() {
			if (directionRight)
				nextPic();
			else
				prevPic();
		}

		public void clearAllFromDuplicates() {
			for (Iterator<File> iter1 = fileList.iterator(); iter1.hasNext();) {
				File f1 = iter1.next();
				try {
					for (Iterator<File> iter2 = fileList.iterator(); iter2.hasNext();) {
						File f2 = iter2.next();
						if (imagesAreDuplicate(f1, f2)) {
							iter1.remove();
							continue;
						}
					}
				} catch (IOException | ConcurrentModificationException e) {
					e.printStackTrace();
				}
			}
		}

		private FDimension getImageDimension() {
			ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
			return new FDimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
		}

		private String getImageDetails() {
			ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
			return file.getName() + "  (" + imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight() + ")";
		}

	}

	static class PicPanel extends JPanel {

		private static final long serialVersionUID = -7238826941008841065L;
		private Image og = null;

		public void paint(Graphics g) {
			super.paint(g);
			og = null;
			try {
				Graphics2D g2 = (Graphics2D) g;
				FDimension dim = (FDimension) imgDim/* .clone() */;
				// dim.addToSize(zoom); // trying to keep the zoom level even
				// when changing to other images
				im.updateImage(dim.width);
				og = createImageObject(file);
				// subsampleImage(ImageIO.createImageInputStream(file), 300,
				// 300, null);
				g2.drawImage(og, target.x - dim.width / 2, target.y - dim.height / 2, dim.width, dim.height, Color.BLACK, null);

				// g.setColor(Color.pink);
				// g.setFont(new Font(g2.getFont().getFamily(), Font.PLAIN,
				// imagePropertiesSize));
				// g.drawString(im.getImageDetails(), 10, (int) (getHeight() *
				// 0.1));

				g2.dispose();
				revalidate();
			} catch (NullPointerException | IOException e) {
				System.err.println(e.getMessage());
				im.iterateInDirection();
			}
		}

		private Point getCenter() {
			return new Point(this.getWidth() / 2, this.getHeight() / 2);
		}
	}

	class ThumbnailWindow extends FFrame {

		final int C = 4;
		ThumbnailWindow thisWindow = this;

		public ThumbnailWindow() {
			super("Thumbnail View");
			this.getContentPane().setLayout(new BorderLayout());
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JScrollPane scroll = new JScrollPane(new ThumbPanel());
			scroll.getVerticalScrollBar().setUnitIncrement(20);

			this.add(scroll, BorderLayout.CENTER);

			addExitKeyListenerToAll();
			this.setVisible(true);
		}

		class ThumbPanel extends JPanel {

			public ThumbPanel() {
				super();
				setLayout(new GridLayout(0, C));
				this.setSize(FFrame.SCREEN_DIMENSION_HALF);
				createThumbnails();

				this.addKeyListener(new KeyAdapter() {

					public void keyPressed(KeyEvent ke) {
						switch (ke.getKeyCode()) {
						case KeyEvent.VK_ESCAPE:
							dispose();
							break;
						}
					}
				});
			}

			/* public void paint(final Graphics g) { super.paint(g); int w = this.getWidth()
			 * / 5, h = this.getHeight() / 5; int x = 0, y = 0; Graphics2D g2 = (Graphics2D)
			 * g; Image og;
			 * 
			 * for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext();) {
			 * final File f = (File) iterator.next(); og =
			 * Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
			 * 
			 * double labelAspect = FDimension.getAspect(og.getWidth(null),
			 * og.getHeight(null)); int thumbnailLength = thisWindow.getWidth() / C; h = w =
			 * thumbnailLength;
			 * 
			 * FDimension containerDimension = new FDimension(thumbnailLength,
			 * thumbnailLength);
			 * 
			 * int max = Integer.max(og.getWidth(null), og.getHeight(null)); if (max ==
			 * og.getWidth(null)) h = (int) (w / labelAspect); else if (max ==
			 * og.getHeight(null)) w = (int) (h * labelAspect);
			 * 
			 * g2.drawImage(og, x, y, x + w, y + h, Color.BLACK, null);
			 * 
			 * if (x < w * 5) { x += w; } else { x = 0; y += h; }
			 * 
			 * // g2.setColor(Color.pink); // g2.setFont(new Font(g2.getFont().getFamily(),
			 * Font.PLAIN, // imagePropertiesSize)); // g2.drawString(getImageProperties(),
			 * 10, (int) // (getHeight() * 0.1)); } } */

			class DrawThumbnail extends Thread {

				int w, h, x, y;

				public DrawThumbnail(int x, int y, int w, int h) {
					this.x = x;
					this.y = y;
					this.w = w;
					this.h = h;
				}

				public void run() {}
			}

			private void createThumbnails() {
				for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext();) {
					final String path = iterator.next().getAbsolutePath();
					// a separate thread for each thumbnail
					new Thread() {

						public void run() {
							int x = getWidth() / C;
							add(new Thumbnail(path, x, x));
						}
					}.start();
				}
			}

			class Thumbnail extends JPanel {

				int w, h;
				ImageIcon icon;
				JLabel label;
				String path;

				public Thumbnail(String path, int w, int h) {
					super();
					this.w = w;
					this.h = h;
					this.path = path;
					icon = new ImageIcon(path);
					label = new JLabel(icon);
					add(label);
					// this.setAlignmentY(JComponent.CENTER_ALIGNMENT);
					// this.setAlignmentX(JComponent.CENTER_ALIGNMENT);
					new FixLabelSize().start();
					System.out.println("Thumbnail label for " + path + " (w:h) " + label.getWidth() + ":" + label.getHeight());

					StackTraceElement s = Thread.currentThread().getStackTrace()[1];
					System.out.printf("(%s:%s)%n", s.getFileName(), s.getLineNumber());
					setVisible(true);
				}

				public void paint(Graphics g) {
					super.paint(g);

					// double labelAspect =
					// FDimension.getAspect(icon.getIconWidth(),
					// icon.getIconHeight());
					// int thumbnailLength = this.getWidth() / C;

					Graphics2D g2 = (Graphics2D) g;
					Image og;
					try {
						og = subsampleImage(ImageIO.createImageInputStream(new File(path)), 300, 300, null);

						// Toolkit.getDefaultToolkit().getImage(path);
						g2.drawImage(og, this.getX() - icon.getIconWidth() / 2, this.getY() - icon.getIconHeight() / 2, icon.getIconWidth(), icon.getIconHeight(), Color.BLACK, null);

						g2.setColor(Color.pink);
						g2.setFont(new Font(g2.getFont().getFamily(), Font.PLAIN, imagePropertiesSize));
						g2.drawString(im.getImageDetails(), 10, (int) (getHeight() * 0.1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					revalidate();
					repaint();
				}

				class FixLabelSize extends Thread {

					public void run() {
						if (icon == null)
							return;

						double labelAspect = FDimension.getAspect(icon.getIconWidth(), icon.getIconHeight());
						int thumbnailLength = thisWindow.getWidth() / C;
						h = w = thumbnailLength;

						// FDimension containerDimension = new
						// FDimension(thumbnailLength, thumbnailLength);

						int max = Integer.max(icon.getIconWidth(), icon.getIconHeight());
						if (max == icon.getIconWidth())
							h = (int) (w / labelAspect);
						else if (max == icon.getIconHeight())
							w = (int) (h * labelAspect);

						label.setSize(w, h);
						// ImageIcon sizedImageGetScale = getSizedImage(path,
						// label.getWidth(), label.getHeight());

						BufferedImage bufferedImage = null;
						try {
							// bufferedImage = ImageIO.read(new File(path));
							// bufferedImage = scale(bufferedImage, 0.5);
							String outputFilePath = "thb_" + path;
							// saveScaledImage(path, outputFilePath);
							bufferedImage = subsampleImage(ImageIO.createImageInputStream(new File(path)), 300, 300, null);
							// ImageIO.read(new File(outputFilePath));
						} catch (IOException e) {
							e.printStackTrace();
						}

						Icon sizedImage = new ImageIcon(bufferedImage);

						label.setIcon(sizedImage);

						try {
							this.join();
							thisWindow.revalidate();
							thisWindow.repaint();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}

			ImageIcon getSizedImage(String path, int w, int h) {
				BufferedImage img = null;
				try {
					img = ImageIO.read(new File(path));
					return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_FAST));
				} catch (IOException | IllegalArgumentException e) {
					e.printStackTrace();
				}
				return null;
			}

		}

		@Override
		public void keyPressed(KeyEvent ke) {
			switch (ke.getKeyCode()) {
			case KeyEvent.VK_W:
				if (!ke.isControlDown())
					break;
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				this.dispose();
				break;
			}
		}
		private void addExitKeyListenerToAll() {
			for (Component comp : getAllComponents(this))
				comp.addKeyListener(this);
		}

	}

	class PicFile {

		File file;
		Graphics graphics;
		Image image;

		public PicFile(File imageFile) {
			this.file = imageFile;
			if (imageFile.isFile()) {
				image = Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath());
			} else {
				image = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(imageFile)).getImage();
			}
		}
	}

	// </Classes>

	static final Image createImageObject(File imageFile) throws IOException {
		Image og;
		if (imageFile.isFile()) {
			og =
					// /*(BufferedImage)*/ ImageIO.read(imageFile);
					Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath());
		} else {
			og = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(imageFile)).getImage();
			imgDim.setSize(og.getWidth(null) * 20, og.getHeight(null) * 20);
		}
		return og;
	}

	public static boolean imagesAreDuplicate(File f1, File f2) throws FileNotFoundException, IOException {
		// Scanner sc = new Scanner(f1);
		if (f1.isDirectory() || f2.isDirectory())
			return false;
		if (f1.getAbsolutePath().equals(f2.getAbsolutePath()))
			return false;

		int diffPoints = 0;
		FileReader fr1 = new FileReader(f1);
		FileReader fr2 = new FileReader(f2);
		char[] array1 = new char[400];
		char[] array2 = new char[400];

		fr1.read(array1);
		fr2.read(array2);

		for (int i = 0; i < array1.length; i++) {
			diffPoints += Math.abs(Character.compare(array1[i], array2[i]));
			if (diffPoints > IMAGE_DUPLICATE_THRESHOLD)
				return false;
		}
		System.out.println("\"" + f1.getName() + "\" and \"" + f2.getName() + "\" difference points: " + diffPoints);
		// sc.close();
		fr1.close();
		fr2.close();

		if (diffPoints < IMAGE_DUPLICATE_THRESHOLD)
			return true;
		return false;
	}

	public String getPathFromFileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("C:/Users/faris/Pictures"));

		fc.setFileFilter(imageFileNameExtensionFilter);

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			readFilesToFilesList();
			im.loadPic();
			return file.getAbsolutePath();
		}
		System.out.println("No pic chosen");
		return null;
	}

	private void showThumbView() {
		new ThumbnailWindow();
	}

	private static void attemptToUseArgs(String[] args) {
		try {
			defaultImagePath = args[0];
		} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			System.out.println("No args passed ArrayIndexOutOfBoundsException");
		}
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			FontUIResource f = new FontUIResource("Calibri", Font.PLAIN, 25);
			Enumeration<Object> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value != null && value instanceof FontUIResource)
					UIManager.put(key, f);
			}
		} catch (Exception e) {
			System.err.println("Look and feel not set.");
		}
	}

	// Listeners

	private void enterDirectory(final File[] listFiles) {
		if (file.isDirectory() && includeDirectories)
			for (File f : listFiles)
				if (f.isFile()) {
					file = f;
					readFilesToFilesList();
					im.loadPic();
					return;
				}
	}

	private void showFileInExplorer() {
		System.out.println("Open file location");
		try {
			Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
			// Runtime.getRuntime().exec("explorer /E,/select=${" +
			// file.getAbsolutePath() + "}");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		final File[] listFiles = file.listFiles();
		switch (ke.getKeyCode()) {
		case KeyEvent.VK_PAGE_DOWN:
		case KeyEvent.VK_RIGHT:
			im.nextPic();
			break;
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_LEFT:
			im.prevPic();
			break;
		case KeyEvent.VK_F:
			im.alignFit();
			break;
		case KeyEvent.VK_DELETE:
			if (ke.isShiftDown())
				im.deleteImage();
			else
				im.recycleImage();
			break;
		case KeyEvent.VK_UP:
			if (!includeDirectories)
				break;
			File parentFile = file.getParentFile();
			if (parentFile == null)
				break;
			file = parentFile;
			readFilesToFilesList();
			im.nextPic();
			break;
		case KeyEvent.VK_L:
			new Popup(fileList);
			break;
		case KeyEvent.VK_T:
			showThumbView();
			break;
		case KeyEvent.VK_P:
			printFiles(fileList.toArray(new File[fileList.size()]));
			break;
		case KeyEvent.VK_ENTER:
			enterDirectory(listFiles);
			break;
		case KeyEvent.VK_W:
			if (!ke.isControlDown())
				break;
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		case KeyEvent.VK_O:
			showFileInExplorer();
			break;
		case KeyEvent.VK_D: // Clear all images from duplicates
			im.clearAllFromDuplicates();
			break;

		// #IMAGE_ALIGN_ORIGINAL = 1
		// #ENSURE_ALL_IS_SHOWING = 2
		// #IMAGE_ALIGN_RESIZE_WINDOW = 4
		case KeyEvent.VK_1:
			im.alignOriginalSize();
			break;
		case KeyEvent.VK_2:
			im.alignFitIfTooBig();
			break;
		case KeyEvent.VK_3:
			im.alignResizeWindowToFitImage();
			break;
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Open photo":
			String path = getPathFromFileChooser();
			if (path == null) {
				System.out.println("Null path!!");
				return;
			}
			file = new File(path);
			im.defaultAlignOnLoad();
			break;

		case "Open File Location":
			showFileInExplorer();
			break;
		case "ThumbView":
			showThumbView();
			break;

		default:
			System.err.println("Unknown action command");
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent me) {
		double input = me.getPreciseWheelRotation();
		double d = Math.exp(input * SCROLL_SPEED / 100.0);
		zoom = zoom > MAX_ZOOM_FACTOR ? MAX_ZOOM_FACTOR : zoom * d;
		zoom = zoom < MIN_ZOOM_FACTOR ? MIN_ZOOM_FACTOR : zoom * d;
		System.out.println("Zoom=" + zoom);
		imgDim.fitToWidth((int) (imgDim.width / d));
		// mousePoint = me.getPoint();
		// picPanel.updateImage(imgDim.width);
		revalidate();
		repaint();
	}
	@Override
	public void componentResized(ComponentEvent ce) {
		im.defaultAlignOnResize();
	}
	@Override
	public void windowClosing(WindowEvent we) {
		saveProperties();
	}

	private void readProperties() {
		// create and load default properties
		Properties defaultProps = new Properties();
		FileInputStream in;

		try {
			in = new FileInputStream("defaultConfig.properties");
			defaultProps.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			new File("defaultConfig.properties");
			saveProperties();
			return;
		}

		// create applicationProperties using default properties
		Properties props = new Properties(defaultProps);

		// now load properties from last invocation
		try {
			in = new FileInputStream("config.properties");
			props.load(in);
			in.close();

			SCROLL_SPEED = Integer.parseInt(props.getProperty("SCROLL_SPEED"));
			imagePropertiesSize = Integer.parseInt(props.getProperty("imagePropertiesSize"));
			defaultImageAlign = Integer.parseInt(props.getProperty("Default_Image_Align"));
			MIN_ZOOM_FACTOR = Double.parseDouble(props.getProperty("MIN_ZOOM_FACTOR"));
			MAX_ZOOM_FACTOR = Double.parseDouble(props.getProperty("MAX_ZOOM_FACTOR"));
			zoomTowardMouse = Boolean.parseBoolean(props.getProperty("zoomTowardMouse"));
			loopImages = Boolean.parseBoolean(props.getProperty("loopImages"));
			ShowHiddenFiles = Boolean.parseBoolean(props.getProperty("ShowHiddenFiles"));
			startMaximized = Boolean.parseBoolean(props.getProperty("startMaximized"));
			includeDirectories = Boolean.parseBoolean(props.getProperty("includeDirectories"));
			saveProperties();
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
			new File("config.properties");
			readProperties();
			return;
		}
	}
	private void saveProperties() {
		Properties prop = new Properties();
		FileOutputStream output = null;
		try {
			output = new FileOutputStream("config.properties");
			// set the properties value
			prop.setProperty("Default_Image_Align", Integer.toString(defaultImageAlign));
			prop.setProperty("SCROLL_SPEED", Integer.toString(SCROLL_SPEED));
			prop.setProperty("imagePropertiesSize", Integer.toString(imagePropertiesSize));
			prop.setProperty("MIN_ZOOM_FACTOR", Double.toString(MIN_ZOOM_FACTOR));
			prop.setProperty("MAX_ZOOM_FACTOR", Double.toString(MAX_ZOOM_FACTOR));
			prop.setProperty("zoomTowardMouse", Boolean.toString(zoomTowardMouse));
			prop.setProperty("loopImages", Boolean.toString(loopImages));
			prop.setProperty("ShowHiddenFiles", Boolean.toString(ShowHiddenFiles));
			prop.setProperty("startMaximized", Boolean.toString(startMaximized));
			prop.setProperty("includeDirectories", Boolean.toString(includeDirectories));

			// save properties to project root folder
			prop.store(output, "Codes for Default_Image_Align" + "\nIMAGE_ALIGN_FIT = 0" + "\nIMAGE_ALIGN_ORIGINAL = 1" + "\nENSURE_ALL_IS_SHOWING = 2" + "\nIMAGE_ALIGN_RESIZE_WINDOW = 4");

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** */
	private static void printFiles(File[] files) {
		System.out.println("Printing files:");
		for (File f : files) {
			if (f.isDirectory()) {
				System.out.println("Directory: " + f.getName());
				printFiles(f.listFiles()); // Calls same method again.
			} else {
				System.out.println("_File: " + f.getName());
			}
		}
	}

	/** https://stackoverflow.com/questions/3294388/make-a-bufferedimage-use-less-ram/3296139#3296139
	 * 
	 * @param inputStream
	 *            imageInputStream
	 * @param x
	 *            resulting image width that you want
	 * @param y
	 *            resulting image height that you want
	 * @param progressListener
	 * @return resampledImage
	 * @throws IOException
	 */
	public static BufferedImage subsampleImage(ImageInputStream inputStream, int x, int y, IIOReadProgressListener progressListener) throws IOException {
		BufferedImage resampledImage = null;

		Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);

		if (!readers.hasNext())
			throw new IOException("No reader available for supplied image stream.");

		ImageReader reader = readers.next();

		ImageReadParam imageReaderParams = reader.getDefaultReadParam();
		reader.setInput(inputStream);

		Dimension d1 = new Dimension(reader.getWidth(0), reader.getHeight(0));
		Dimension d2 = new Dimension(x, y);
		int subsampling = (int) scaleSubsamplingMaintainAspectRatio(d1, d2);
		imageReaderParams.setSourceSubsampling(subsampling, subsampling, 0, 0);

		reader.addIIOReadProgressListener(progressListener);
		resampledImage = reader.read(0, imageReaderParams);
		reader.removeAllIIOReadProgressListeners();

		return resampledImage;
	}

	@Experimental
	public static long scaleSubsamplingMaintainAspectRatio(Dimension d1, Dimension d2) {
		long subsampling = 1;

		if (d1.getWidth() > d2.getWidth())
			subsampling = Math.round(d1.getWidth() / d2.getWidth());
		else if (d1.getHeight() > d2.getHeight())
			subsampling = Math.round(d1.getHeight() / d2.getHeight());

		return subsampling;
	}

	/** @author https://stackoverflow.com/questions/13990298/compress-image-using-imagescalr
	 * @param imageData
	 * @param width
	 * @param height
	 * @param imageFormat
	 * @return returns a resized BufferedImage using the Scalr library */
	BufferedImage resizeImage(BufferedImage imageData, int width, int height, String imageFormat) {
		return Scalr.resize(imageData, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, width, height, Scalr.OP_ANTIALIAS);
	}

	@Experimental
	private static BufferedImage scale(BufferedImage source, double ratio) {
		int w = (int) (source.getWidth() * ratio);
		int h = (int) (source.getHeight() * ratio);
		BufferedImage bi = getCompatibleImage(w, h);
		Graphics2D g2d = bi.createGraphics();
		double xScale = (double) w / source.getWidth();
		double yScale = (double) h / source.getHeight();
		AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
		g2d.drawRenderedImage(source, at);
		g2d.dispose();
		return bi;
	}
	@Experimental
	private static BufferedImage getCompatibleImage(int w, int h) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(w, h);
		return image;
	}
	@Experimental
	private static void saveScaledImage(String filePath, String outputFile) {
		try {
			BufferedImage sourceImage = ImageIO.read(new File(filePath));
			int width = sourceImage.getWidth();
			int height = sourceImage.getHeight();

			if (width > height) {
				float extraSize = height - 100;
				float percentHight = (extraSize / height) * 100;
				float percentWidth = width - ((width / 100) * percentHight);
				BufferedImage img = new BufferedImage((int) percentWidth, 100, BufferedImage.TYPE_INT_RGB);
				Image scaledImage = sourceImage.getScaledInstance((int) percentWidth, 100, Image.SCALE_SMOOTH);
				img.createGraphics().drawImage(scaledImage, 0, 0, null);
				BufferedImage img2 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
				img2 = img.getSubimage((int) ((percentWidth - 100) / 2), 0, 100, 100);

				ImageIO.write(img2, "jpg", new File(outputFile));
			} else {
				float extraSize = width - 100;
				float percentWidth = (extraSize / width) * 100;
				float percentHight = height - ((height / 100) * percentWidth);
				BufferedImage img = new BufferedImage(100, (int) percentHight, BufferedImage.TYPE_INT_RGB);
				Image scaledImage = sourceImage.getScaledInstance(100, (int) percentHight, Image.SCALE_SMOOTH);
				img.createGraphics().drawImage(scaledImage, 0, 0, null);
				BufferedImage img2 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
				img2 = img.getSubimage(0, (int) ((percentHight - 100) / 2), 100, 100);

				ImageIO.write(img2, "jpg", new File(outputFile));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
