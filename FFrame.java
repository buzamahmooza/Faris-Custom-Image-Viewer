package FarisImageViewer;

import java.awt.Dimension;
import java.awt.FlowLayout;
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

import javax.swing.JFrame;



public class FFrame extends JFrame
		implements ActionListener, KeyListener, MouseListener, MouseWheelListener, WindowListener, ComponentListener
{
	public static final Dimension SCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
	public static final Dimension SCREEN_DIMENSION_HALF = new Dimension(SCREEN_DIMENSION.width / 2,
			SCREEN_DIMENSION.height / 2);

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
		this.setLocation(getScreenCenter().x - getWindowCenter().x, getScreenCenter().y - getWindowCenter().y);
	}

	public static Point getScreenCenter() {
		return new Point(SCREEN_DIMENSION_HALF.width, SCREEN_DIMENSION_HALF.height);
	}

	public Point getWindowCenter() {
		return new Point(this.getWidth() / 2, this.getHeight() / 2);
	}

	public void addToSize(int increase) {
		addToSize(increase, false);
	}

	/**
	 * @param increase
	 * @param recenter
	 */
	public void addToSize(int increase, boolean recenter) {
		this.setSize(getWidth() + increase, (getHeight() + increase) * getAspect());
		if (recenter) centerPosition();
	}

	public void multiplySize(double factor) {
		this.setSize((int) (getWidth() * factor), (int) (getHeight() * factor));
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
		// Maximize window
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	@Override public void mouseWheelMoved(MouseWheelEvent arg0) {

	}

	@Override public void mouseClicked(MouseEvent e) {

	}

	@Override public void mouseEntered(MouseEvent e) {

	}

	@Override public void mouseExited(MouseEvent e) {

	}

	@Override public void mousePressed(MouseEvent e) {

	}

	@Override public void mouseReleased(MouseEvent e) {

	}

	@Override public void keyPressed(KeyEvent e) {

	}

	@Override public void keyReleased(KeyEvent e) {

	}

	@Override public void keyTyped(KeyEvent e) {

	}

	@Override public void actionPerformed(ActionEvent arg0) {

	}

	@Override public void windowActivated(WindowEvent arg0) {

	}

	@Override public void windowClosed(WindowEvent arg0) {

	}

	@Override public void windowClosing(WindowEvent arg0) {

	}

	@Override public void windowDeactivated(WindowEvent arg0) {

	}

	@Override public void windowDeiconified(WindowEvent arg0) {

	}

	@Override public void windowIconified(WindowEvent arg0) {

	}

	@Override public void windowOpened(WindowEvent arg0) {

	}

	@Override public void componentHidden(ComponentEvent arg0) {

	}

	@Override public void componentMoved(ComponentEvent arg0) {

	}

	@Override public void componentResized(ComponentEvent arg0) {

	}

	@Override public void componentShown(ComponentEvent arg0) {

	}
}
