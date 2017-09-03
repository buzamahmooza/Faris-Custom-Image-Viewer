package FarisImageViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;



//import org.jdesktop.jdic.filetypes.Association;
//import org.jdesktop.jdic.filetypes.AssociationAlreadyRegisteredException;
//import org.jdesktop.jdic.filetypes.AssociationNotRegisteredException;
//import org.jdesktop.jdic.filetypes.AssociationService;
//import org.jdesktop.jdic.filetypes.RegisterFailedException;

@SuppressWarnings("unused")

public class FViewer extends FFrame
{
	// Settings
	int SCROLL_SPEED = 200;
	double MIN_ZOOM_FACTOR = 0.5, MAX_ZOOM_FACTOR = 10;
	int imagePropertiesSize = 20;
	private double zoom = 100;
	boolean zoomTowardMouse = false, loopImages = true, ShowHiddenFiles = true, startMaximized = false, includeDirectories = true;

	private final String SETTINGS_PATH = "C:/Users/faris/Dropbox/_Folder_/Workspace Academic/ICS201/src/FarisImageViewer/Settings.txt";
	private FDimension imgDim;
	private PicPanel picPanel;
	private JPanel controlsPanel;
	private JMenuItem mi_GoToPath;
	private JLabel propertiesLabel;
	private File file;

	private ArrayList<File> fileList;

	private final String[] DEFAULT_IMAGES = {
			"C:/Users/faris/OneDrive/Pictures/vlc snap (2).png", "C:/Users/faris/Pictures/League Of Legends Jinx Wallpaper/league-of-legends-jinx-wallpaper-15.jpg",
			"D:/Buza/Torrents/collexion/7160aa238968e174e0a1c4a1f09833cc30c49a305da0a7a665b6ba9b5a2faa92.gif", "G:/collexion/DGGs0TfUQAA7n_D.jpg"
	};

	Point target;
	static Point mousePoint;
	private int idx = 0;

	private static FileNameExtensionFilter imageFileNameExtensionFilter = new FileNameExtensionFilter("jpg", "JPEG file", "jpeg", "gif", "bmp", "tiff", "png");
	private static final String[] EXTENSIONS_ARRAY = {
			"jpg", "JPEG file", "jpeg", "gif", "bmp", "tiff", "png"
	};
	private static final String EXTENSIONS_STRING = "jpg, JPEG file, jpeg, gif, bmp, tiff, png";
	private static final String IMAGE_PATTERN = "((//.(?i)(jpg|png|gif|bmp))$)";
	private static final int IMAGE_DUPLICATE_THRESHOLD = 1000;
	private static final String FOLDER_ICON = "C:/Users/faris/OneDrive/Pictures/Folder IconPNG.PNG";
	private static FViewer window;
	private String defaultImagePath = DEFAULT_IMAGES[idx];

	public static void main(String[] args) {
		System.out.println("Args: " + Arrays.toString(args));
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			setUIFont(new FontUIResource("Calibri", Font.PLAIN, 25));
		} catch (Exception e) {
			System.err.println("Look and feel not set.");
		}
		for (int i = 0; i < args.length; i++) {
			window.defaultImagePath = args[i];
			break;
		}
		window = new FViewer("Faris'es awesome imageLabel viewer", SCREEN_DIMENSION_HALF);
		window.picPanel.fitImage();
	}

	public FViewer(String title, Dimension d) {
		super(title, d);
		centerPosition();
		file = new File(defaultImagePath);
		imgDim = new FDimension(this.getSize());
		readSettings();
		readFiles();
		System.out.println("Default image: " + file.getPath());
		target = getWindowCenter(); // By default the zoom target is the center
		createAndShowGUI();

		// File drop
		new FileDrop(System.out, picPanel, new FileDrop.Listener() {
			public void filesDropped(java.io.File[] files) {
				int i;
				for (i = 0; i < files.length; i++) {
					if (!fileList.contains(files[i])) fileList.add(files[i]);
				}
				if (i > 0) {
					if (file.isDirectory()) {
						File childFile = new File(file.list()[0]);
						if (childFile != null) file = childFile;
					} else file = fileList.get(fileList.size() - 1);

					readFiles();
				}
			} // end filesDropped
		});
		// end FileDrop.Listener

		new Thread() {
			public void run() {
				clearAllFromDuplicates();
			}
		}.start();
	}

	public void createAndShowGUI() {
		if (startMaximized) winMaximize();
		this.getContentPane().setLayout(new BorderLayout());

		picPanel = new PicPanel();

		this.getContentPane().setBackground(Color.BLACK);
		picPanel.setBackground(Color.BLACK);
		controlsPanel = new JPanel();

		// Menu
		JMenuBar bar = new JMenuBar();
		JMenuItem mi_OpenPhoto = new JMenuItem("Open photo");
		JMenuItem mi_ThumbView = new JMenuItem("ThumbView");
		propertiesLabel = new JLabel("Image Properties");
		mi_GoToPath = new JMenuItem();
		mi_GoToPath.setActionCommand("Open File Location");

		for (int i = 0; i < bar.getMenuCount(); i++){
			bar.getMenu(i).setHorizontalAlignment((int) Component.CENTER_ALIGNMENT);
			
		}

		bar.add(mi_GoToPath);
		bar.add(mi_OpenPhoto);
		bar.add(mi_ThumbView);
		this.setJMenuBar(bar);

		picPanel.add(propertiesLabel);
		this.getContentPane().add(picPanel, BorderLayout.CENTER);

		// If you want a scrollable thumb view
		// add(new JScrollPane(thumbViewPanel), BorderLayout.CENTER);

		this.addKeyListener(this);
		this.addComponentListener(this);
		picPanel.addMouseWheelListener(this);
		mi_ThumbView.addActionListener(this);
		mi_OpenPhoto.addActionListener(this);
		mi_GoToPath.addActionListener(this);

		this.setVisible(true);
		picPanel.loadPic();
	}

	private void readFiles() {
		try {
			fileList = new ArrayList<File>();

			System.out.println("ReadFiles()");
			for (File f : file.getParentFile().listFiles()) {
				// String mimetype = new
				// MimetypesFileTypeMap().getContentType(f);
				// String type = mimetype.split("/")[0];
				// if (type.equals("image")) {
				if (!fileList.contains(f)) {
					if (f.isHidden() && !ShowHiddenFiles) continue;
					if ((f.isDirectory() && includeDirectories) || isImage(f)) fileList.add(f);
					System.out.println("File added:\t" + f.getName());
				}

				// If image
				// if (f.isFile()) {
				// if (!f.getName().matches(IMAGE_PATTERN)) {
				// fileList.add(f);
				// System.out.println("File added:/t " + f.getAbsolutePath());
				// } else System.out.println("[Not a file]:/t " +
				// f.getAbsolutePath());
				// } else if (f.isDirectory()) {
				// System.out.println("Directory:/t " + f.getAbsolutePath());
				// }

				// for (String extension : EXTENSIONS_ARRAY) {
				// if (!f.getName().matches(extension)) continue;
				// fileList.add(f);
				// System.out.println(f.getAbsolutePath());
				// }
				// System.out.println("[Not a file] Excluded: " +
				// f.getAbsolutePath());
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private static boolean isImage(File f) {
		for (String ext : EXTENSIONS_ARRAY) {
			if (f.getAbsolutePath().endsWith(ext)) return true;
		}
		return false;
	}

	class PicPanel extends JPanel
	{
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			updateImage(imgDim.width);
			Image og;
			if (file.isFile())
				og = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
			else og = Toolkit.getDefaultToolkit().getImage(FOLDER_ICON);
			g2.drawImage(og, target.x - imgDim.width / 2, target.y - imgDim.height / 2, imgDim.width, imgDim.height, Color.BLACK, null);

			// g2.setColor(Color.pink);
			// g2.setFont(new Font(g2.getFont().getFamily(), Font.PLAIN,
			// imagePropertiesSize));
			// g2.drawString(getImageProperties() ,10,
			// (int)(getHeight()*0.1));

			g2.finalize();

			revalidate();
			repaint();
		}

		private void fitImage() {
			imgDim.fitToContainer(this.getSize());
			updateImage(imgDim.width);
		}

		private void loadPic() {

			int lastIndex = fileList.size() - 1;
			if (idx > lastIndex) {
				idx = 0;
			} else if (idx < 0) {
				idx = lastIndex;
			}
			file = fileList.get(idx);

			mi_GoToPath.setText(file.getPath());

			updateImage(Integer.max(imgDim.width, imgDim.height));
			// TODO: make a specific default action to happen when loading
			// images
			// (Fit, Stretch or whatever)
			fitImage();

			propertiesLabel.setText(getImageProperties());
			revalidate();
			repaint();
			System.out.println("LoadedPic: " + file.getAbsolutePath());
		}

		private void nextPic() {
			idx++;
			loadPic();
		}

		private void prevPic() {
			idx--;
			loadPic();
		}

		private void deleteImage() {
			try {
				boolean result = file.delete();
				if (!result) {
					System.out.println("Could not delete");
					return;
				}
				readFiles();
				nextPic();
				// TODO: make a pop-up to inform the user that deletion failed
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		/**
		 * @param the
		 *            parameter is what stays constant ie. the other dimension
		 *            will
		 *            be changed
		 */
		public void updateImage(int preserve) {
			if (file == null) {
				System.err.println("Icon is null!!");
				return;
			}

			final ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
			Image img = imageIcon.getImage();
			double aspect = FDimension.getAspect(img.getWidth(null), img.getHeight(null));

			// Preserve width or height
			if (imgDim.width == preserve) imgDim.height = (int) (imgDim.width / aspect);
			if (imgDim.height == preserve) imgDim.width = (int) (imgDim.height * aspect);

			// Clamp zoom
			if (imgDim.width < MIN_ZOOM_FACTOR * imageIcon.getIconWidth()) imgDim.fitToWidth((int) MIN_ZOOM_FACTOR * imageIcon.getIconWidth());
			if (imgDim.width > MAX_ZOOM_FACTOR * imageIcon.getIconWidth()) imgDim.fitToWidth((int) MAX_ZOOM_FACTOR * imageIcon.getIconWidth());

			// Updating target
			if (zoomTowardMouse)
				target = mousePoint;
			else target = getWindowCenter();

			picPanel.revalidate();
			picPanel.repaint();
		}

	}

	class ThumbnailWindow extends FFrame
	{
		final int C = 4;
		ThumbnailWindow thisWindow = this;

		public ThumbnailWindow() {
			super("Thumbnail View");
			this.getContentPane().setLayout(new BorderLayout());
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JScrollPane scroll = new JScrollPane(new ThumbPanel());
			scroll.getVerticalScrollBar().setUnitIncrement(20);

			this.add(scroll, BorderLayout.CENTER);
			this.setVisible(true);
		}

		class ThumbPanel extends JPanel
		{
			public ThumbPanel() {
				super();
				setLayout(new GridLayout(0, C));
				this.setSize(FFrame.SCREEN_DIMENSION_HALF);
				createThumbnails();
				this.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent ke) {
						switch (ke.getKeyCode())
						{
							case KeyEvent.VK_ESCAPE:
								dispose();
							break;
						}
					}
				});
			}

			private void createThumbnails() {
				final ThumbPanel it = this;
				// new Thread() {
				// public void run() {
				for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext();) {
					final String path = ((File) iterator.next()).getAbsolutePath();
					// a seperate thread for each thumbnail
					new Thread() {
						public void run() {
							int x = getWidth() / C;
							add(new Thumbnail(path, x, x));
						}
					}.start();
				}
				// }
				// }.start();
				// Just adding the thumbnails
			}

			class Thumbnail extends JPanel
			{
				int w, h;
				ImageIcon icon;
				JLabel label;
				String path;

				public Thumbnail(String path, int w, int h) {
					super();
					icon = new ImageIcon(path);
					this.w = w;
					this.h = h;
					this.path = path;
					label = new JLabel(new ImageIcon(path));
					add(label);
					fixLabelSize();
					System.out.println("Thumbnail label (w:h) " + label.getWidth() + ":" + label.getHeight());
					setVisible(true);
				}

				public void fixLabelSize() {
					// new Thread() {
					// public void run() {
					double labelAspect = (double) icon.getIconWidth() / icon.getIconHeight();
					int x = thisWindow.getWidth() / C;
					h = w = x;

					int max = Integer.max(icon.getIconWidth(), icon.getIconHeight());
					if (max == icon.getIconWidth()) {
						h = (int) (w / labelAspect);
					} else if (max == icon.getIconHeight()) {
						w = (int) (h * labelAspect);
					}

					label.setSize(w, h);
					setSize(w, h);
					label.setIcon(getSizedImage(path, label.getWidth(), label.getHeight()));
					// revalidate();
					// repaint();
					// }
					// }.start();
				}

				public void paint(Graphics g) {
					fixLabelSize();
					super.paint(g);
				}
			}
		}

	}

	static ImageIcon getSizedImage(String path, int w, int h) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
			return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void clearAllFromDuplicates() {
		for (Iterator<File> iter1 = fileList.iterator(); iter1.hasNext();) {
			File f1 = (File) iter1.next();
			try {
				for (Iterator<File> iter2 = fileList.iterator(); iter2.hasNext();) {
					File f2 = (File) iter2.next();
					if (areDuplicates(f1, f2)) {
						iter1.remove();
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (ConcurrentModificationException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@SuppressWarnings("resource") public static boolean areDuplicates(File f1, File f2) throws FileNotFoundException, IOException {
		// Scanner sc = new Scanner(f1);
		if (f1.isDirectory() || f2.isDirectory()) return false;
		if (f1.getAbsolutePath().equals(f2.getAbsolutePath())) return false;

		int diffPoints = 0;
		FileReader fr1 = new FileReader(f1);
		FileReader fr2 = new FileReader(f2);
		char[] array1 = new char[400];
		char[] array2 = new char[400];

		fr1.read(array1);
		fr2.read(array2);

		for (int i = 0; i < array1.length; i++) {
			diffPoints += Math.abs(Character.compare(array1[i], array2[i]));
			if (diffPoints > IMAGE_DUPLICATE_THRESHOLD) return false;
		}
		System.out.println("\"" + f1.getName() + "\" and \"" + f2.getName() + "\" difference points: " + diffPoints);
		// sc.close();
		fr1.close();
		fr2.close();

		if (diffPoints < IMAGE_DUPLICATE_THRESHOLD) return true;
		return false;
	}

	private String getImageProperties() {
		ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
		return file.getName() + "  (" + imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight() + ")";
	}

	public String getPathFromFileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("C:/Users/faris/Pictures"));

		fc.setFileFilter(imageFileNameExtensionFilter);

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			readFiles();
			picPanel.loadPic();
			return file.getAbsolutePath();
		}
		System.out.println("No pic chosen");
		return null;
	}

	/** {@cod}} */
	public void showFiles(File[] files) {
		File folder = new File("your/path");
		File[] listOfFiles = folder.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				System.out.println("Directory: " + f.getName());
				showFiles(f.listFiles()); // Calls same method again.
			} else {
				System.out.println("File: " + f.getName());
			}
		}
	}

	public static void setUIFont(FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof FontUIResource) UIManager.put(key, f);
		}
	}

	// Listeners

	private void showThumbView() {
		new ThumbnailWindow();
	}

	@Override public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand())
		{
			case "Open photo":
				String path = getPathFromFileChooser();
				if (path == null) {
					System.out.println("Null path!!");
					return;
				}
				file = new File(path);
				picPanel.fitImage();
			break;

			case "Open File Location":
				System.out.println("Open file location");
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			break;

			case "ThumbView":
				showThumbView();
			break;

			default:
				System.err.println("Unknown action command");
		}
	}

	@Override public void keyPressed(KeyEvent ke) {
		final File[] listFiles = file.listFiles();
		switch (ke.getKeyCode())
		{
			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_RIGHT:
				picPanel.nextPic();
			break;
			case KeyEvent.VK_PAGE_UP:
			case KeyEvent.VK_LEFT:
				picPanel.prevPic();
			break;
			case KeyEvent.VK_F:
				picPanel.fitImage();
			break;
			case KeyEvent.VK_DELETE:
				picPanel.deleteImage();
			break;
			case KeyEvent.VK_UP:
				File parentFile = file.getParentFile();
				if (parentFile == null) break;
				file = parentFile;
				readFiles();
				picPanel.nextPic();
			break;
			case KeyEvent.VK_T:
				showThumbView();
			break;
			case KeyEvent.VK_P:
				showFiles(listFiles);
			break;
			case KeyEvent.VK_ENTER:
				if (file.isDirectory()) {
					for (int i = 0; i < listFiles.length; i++) {
						if (listFiles[i].isFile()) {
							file = listFiles[i];
							readFiles();
							return;
						}
					}
				}
			break;
			case KeyEvent.VK_I:
				System.out.println("Open settings");
				try {
					Runtime.getRuntime().exec("explorer.exe /open," + SETTINGS_PATH);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
			break;
		}
	}

	@Override public void mouseWheelMoved(MouseWheelEvent me) {
		double d = me.getPreciseWheelRotation() * SCROLL_SPEED;
		// double d = me.getPreciseWheelRotation() * imgDim.width;
		// imgDim.width -= d;
		imgDim.fitToWidth((int) (imgDim.width - d));
		// mousePoint = me.getPoint();
		// picPanel.updateImage(imgDim.width);

		revalidate();
		repaint();
	}

	@Override public void componentResized(ComponentEvent ce) {
		picPanel.fitImage();
	}

	@Override public void windowClosing(WindowEvent we) {
		writeSettings();
	}

	private void readSettings() {
		try {
			Scanner reader = new Scanner(new File(SETTINGS_PATH));
			System.out.println("Read settings");
			if (!reader.hasNextLine()) writeSettings();
			// SCROLL_SPEED 200
			SCROLL_SPEED = reader.nextInt();
			reader.nextLine();
			// MIN_ZOOM 400
			MIN_ZOOM_FACTOR = reader.nextDouble();
			reader.nextLine();
			// MAX_ZOOM 20000
			MAX_ZOOM_FACTOR = reader.nextDouble();
			reader.nextLine();
			// imagePropertiesSize 20
			imagePropertiesSize = reader.nextInt();
			reader.nextLine();
			// zoom 100
			zoom = reader.nextDouble();
			reader.nextLine();
			// zoomTowardMouse false
			zoomTowardMouse = Boolean.parseBoolean(reader.next());
			reader.nextLine();
			// loopImages true
			loopImages = Boolean.parseBoolean(reader.next());
			reader.nextLine();
			// ShowHiddenFiles true
			ShowHiddenFiles = Boolean.parseBoolean(reader.next());
			reader.nextLine();
			// startMaximized
			startMaximized = Boolean.parseBoolean(reader.next());
			reader.nextLine();
			// includeDirectories
			includeDirectories = Boolean.parseBoolean(reader.next());
			reader.nextLine();

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			writeSettings();
		} catch (InputMismatchException e) {
			e.printStackTrace();
			writeSettings();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			writeSettings();
		}
	}

	private void writeSettings() {
		try {
			PrintWriter pw = new PrintWriter(new File(SETTINGS_PATH));
			System.out.println("Save settings");
			// SCROLL_SPEED 200
			pw.print(SCROLL_SPEED + " SCROLL_SPEED");
			pw.println();
			// MIN_ZOOM 400
			pw.print(MIN_ZOOM_FACTOR + " MIN_ZOOM");
			pw.println();
			// MAX_ZOOM 20000
			pw.print(MAX_ZOOM_FACTOR + " MAX_ZOOM");
			pw.println();
			// imagePropertiesSize 20
			pw.print(imagePropertiesSize + " imagePropertiesSize");
			pw.println();
			// zoom 100
			pw.print(zoom + " zoom");
			pw.println();
			// zoomTowardMouse false
			pw.print(zoomTowardMouse + " zoomTowardMouse");
			pw.println();
			// loopImages true
			pw.print(loopImages + " loopImages");
			pw.println();
			// ShowHiddenFiles true
			pw.print(ShowHiddenFiles + " ShowHiddenFiles");
			pw.println();
			// startMaximized false
			pw.print(startMaximized + " startMaximized");
			pw.println();
			// includeDirectories
			pw.print(includeDirectories + " includeDirectories");
			pw.println();

			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				System.out.println(fileEntry.getName());
			}
		}
	}
}
