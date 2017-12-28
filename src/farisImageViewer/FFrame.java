package farisImageViewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

/** @author Buza */
public class FFrame extends JFrame implements ActionListener, KeyListener, MouseListener, MouseWheelListener, WindowListener, ComponentListener {

	public static final Dimension SCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
	public static final Dimension SCREEN_DIMENSION_HALF = new Dimension(SCREEN_DIMENSION.width / 2, SCREEN_DIMENSION.height / 2);

	public static void main(String[] args) {
		new TestFrame();
	}

	private static class TestFrame extends FFrame {

		int x1 = 80, x2 = 400, y1 = 90, y2 = 300;

		public TestFrame() {
			super("Test FFrame", 600, 600);
			this.setVisible(true);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.red);
			g.drawRect(x1, y1, x2, y2);
			g.finalize();
			revalidate();
		}
	}

	public FFrame() {
		this("FirstWindow");
	}
	public FFrame(String title) {
		this(title, getScreenCenter().x, getScreenCenter().y);
	}
	public FFrame(String title, Dimension dimension) {
		this(title, dimension.width, dimension.height);
	}
	public FFrame(String title, int width, int height) {
		super(title);
		this.setSize(width, height);
		centerPosition();
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void centerPosition() {
		this.setLocation(getScreenCenter().x - getHalfWindowDimension().x, getScreenCenter().y - getHalfWindowDimension().y);
	}

	public static Point getScreenCenter() {
		return new Point(SCREEN_DIMENSION_HALF.width, SCREEN_DIMENSION_HALF.height);
	}
	public Point getHalfWindowDimension() {
		return new Point(this.getWidth() / 2, this.getHeight() / 2);
	}
	public Point getWindowCenterPoint() {
		return new Point(this.getLocation().x + this.getWidth() / 2, this.getLocation().y + this.getHeight() / 2);
	}

	public int getAspect() {
		return (getWidth() / getHeight());
	}

	/** @inherit pack + add padding */
	public void pack() {
		super.pack();
		this.multiplySize(1.3);
	}

	public void winMaximize() {
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	public void addToSize(int increase) {
		addToSize(increase, false);
	}
	/** @param increase
	 * @param recenter
	 */
	public void addToSize(int increase, boolean recenter) {
		FDimension d = FDimension.addToSize(getSize(), increase);
		this.setSize(d);
		if (recenter)
			centerPosition();
	}
	public void multiplySize(double factor) {
		this.setSize((int) (getWidth() * factor), (int) (getHeight() * factor));
	}

	// TODO: fix
	/** if the anchor is null, the center will be the anchor Sets the size with specifying the anchor */
	public void setSize(Dimension newSize, Point a) {
		a = a == null ? this.getWindowCenterPoint() : a;
		Dimension oldSize = this.getSize();
		Point loc = this.getLocation();
		Point relPos = new Point(a.x - loc.x, a.y - loc.y);// relative position
														   // of the anchor

		System.out.println("Relative position: " + relPos.toString());
		this.setSize(newSize);
		// Point newPos = new Point((newSize.width / oldSize.width) * relPos.x,
		// (newSize.height / oldSize.height) * relPos.y);
		// this.setLocation(newPos);
		this.getLocation().translate(relPos.x, relPos.y);
	}
	public void setSizeFromCenter(Dimension newSize) {
		if (newSize.getWidth() <= 0 || newSize.getHeight() <= 0) {
			System.err.println("new size is zero or less!");
			return;
		}

		Dimension relativePos = FDimension.subtract(newSize, this.getSize());
		// Point offset = FDimension.add(newSize, ).toPoint();

		// System.out.println("Border dimensions:\t" + borderDim.toString() +
		// "\n" + "newSize:\t" + newSize + "\n" + "resultSize:\t" + result);
		// System.out.println("Old contentpane size:\t" +
		// this.getContentPane().getSize().toString());
		// System.out.println("Old size:\t"+this.getSize());
		this.setSize(newSize);
		Point p = this.getLocation();
		p.translate(relativePos.width, relativePos.height);
		this.setLocation(p);
		// System.out.println("New contentpane size:\t" +
		// this.getContentPane().getSize().toString());
		// System.out.println("New size:\t"+this.getSize());
	}
	public void setSizeBorderless(Dimension newSize) {
		if (newSize.getWidth() <= 0 || newSize.getHeight() <= 0) {
			System.err.println("new size is zero or less!");
			return;
		}

		Dimension borderDim = FDimension.subtract(this.getSize(), this.getContentPane().getSize());
		Dimension result = FDimension.add(newSize, borderDim);
		// System.out.println("Border dimensions:\t" + borderDim.toString() +
		// "\n" + "newSize:\t" + newSize + "\n" + "resultSize:\t" + result);
		// System.out.println("Old contentpane size:\t" +
		// this.getContentPane().getSize().toString());
		// System.out.println("Old size:\t"+this.getSize());
		this.setSize(result);
		// System.out.println("New contentpane size:\t" +
		// this.getContentPane().getSize().toString());
		// System.out.println("New size:\t"+this.getSize());
	}
	public void setSizeBorderless(Dimension newSize, Point anchor) {
		if (newSize.getWidth() <= 0 || newSize.getHeight() <= 0) {
			System.err.println("new size is zero or less!");
			return;
		}

		Dimension borderDim = FDimension.subtract(this.getSize(), this.getContentPane().getSize());
		Dimension result = FDimension.add(newSize, borderDim);
		// System.out.println("Border dimensions:\t" + borderDim.toString() +
		// "\n" + "newSize:\t" + newSize + "\n" + "resultSize:\t" + result);
		// System.out.println("Old contentpane size:\t" +
		// this.getContentPane().getSize().toString());
		// System.out.println("Old size:\t"+this.getSize());
		this.setSize(result, anchor);
		// System.out.println("New contentpane size:\t" +
		// this.getContentPane().getSize().toString());
		// System.out.println("New size:\t"+this.getSize());
	}

	/** @param container
	 * @return returns an array of all the components attached to this <code>FFrame</code> */
	public List<Component> getAllComponents() {
		return getAllComponents(this);
	}
	/** @param container
	 * @return returns an array of all the components attached to the <code>container</code> */
	public static List<Component> getAllComponents(final Container container) {
		Component[] comps = container.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Container)
				compList.addAll(getAllComponents((Container) comp));
		}
		return compList;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void actionPerformed(ActionEvent arg0) {}
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) {}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
	@Override
	public void componentHidden(ComponentEvent arg0) {}
	@Override
	public void componentMoved(ComponentEvent arg0) {}
	@Override
	public void componentResized(ComponentEvent arg0) {}
	@Override
	public void componentShown(ComponentEvent arg0) {}
}