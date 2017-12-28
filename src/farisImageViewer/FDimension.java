package farisImageViewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Scanner;



public class FDimension extends Dimension {
	static final boolean debug = true;
	
	/** Tester */
//	public static void main(String[] args) {
//		TestFrame t = new TestFrame();
//		Scanner kb = new Scanner(System.in);
//		kb.nextLine();
//		
//	}
	
	private static class TestFrame extends FFrame {
		private static final long serialVersionUID = 1L;
		int x1 = 80, x2 = 400, y1 = 90, y2 = 300;
		
		public TestFrame() {
			super("Test FFrame", 600, 600);
			this.setVisible(true);
		}
		
		@Override public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.red);
			g.drawRect(x1, y1, x2, y2);
			revalidate();
		}
	}
	
	public FDimension(Dimension dimension) {
		super((Dimension) dimension);
	}
	
	/** The double values are casted to integers
	 * 
	 * @param w
	 * @param h */
	public FDimension(double w, double h) {
		super((int) w, (int) h);
	}
	
	public FDimension(int w, int h) {
		super(w, h);
	}
	// End of constructors
	
	public double getAspect() {
		return getAspect(this);
	}
	
	public void fitToContainer(Dimension containerDimension) {
		log("FitToContainer:\n" + this.width + ", " + this.height + "\n" + containerDimension.width + ", " + containerDimension.height);
		int hDiff = (int) (containerDimension.width - this.getWidth());
		int wDiff = (int) (containerDimension.height - this.getHeight());
		
		double wd = (wDiff / containerDimension.getWidth());
		double hd = (hDiff / containerDimension.getHeight());
		
		log("" + hd);
		log("" + wd);
		if (Math.abs(Math.min(hd, wd)) < 0.2 )
		    return;
		
		if (wDiff > hDiff) {
			fitToWidth(containerDimension.width);
		}
		if (hDiff > wDiff) {
			fitToHeight(containerDimension.height);
		}
	}
	
	public void fitToWidth(int newWidth) {
		// log("fitToWidth");
		if (newWidth <= 0) {
			// System.err.println("fitToWidth(): newWidth is " + newWidth);
			return;
		}
		final int newHeight = (int) (newWidth / getAspect());
		this.setSize(new FDimension(newWidth, newHeight));
	}
	
	public void fitToHeight(int newHeight) {
		log("fitToHeight");
		if (newHeight <= 0) {
			// System.err.println("fitToWidth(): newHeight is " + newHeight);
			return;
		}
		final int newWidth = (int) (newHeight * getAspect());
		this.setSize(new FDimension(newWidth, newHeight));
	}
	
	public double getMagnitude() {
		return Math.sqrt(Math.pow(this.getWidth(), 2) + Math.pow(this.getHeight(), 2));
	}
	
	public void stretch(double wFactor, double hFactor) {
		setSize(getWidth() * wFactor, getHeight() * hFactor);
	}
	
	public void stretch(double factor) {
		stretch(factor, factor);
	}
	
	public void addToSize(double w, double h) {
		setSize(getWidth() + w, getHeight() + h);
	}
	
	public void addToSize(double increase) {
		addToSize(increase, increase * getAspect());
	}
	
	public boolean isLargerThan(Dimension bDimension) {
		return this.getWidth() > bDimension.getWidth() || this.getHeight() > bDimension.getHeight();
	}
	
	/** @return returns an equivalent dimension object with the same height and
	 *         width */
	public Dimension toDim() {
		return new Dimension(width, height);
	}
	
	public Point toPoint() {
		return FDimension.toPoint(this);
	}
	// public FDimension getFitWidth(int newWidth) {
	// final int newHeight = (int) (newWidth / getAspect());
	// return new FDimension(newWidth, newHeight);
	// }
	//
	// public FDimension getFitHeight(int newHeight) {
	// final int newWidth = (int) (newHeight * getAspect());
	// return new FDimension(newWidth, newHeight);
	// }
	
	// public void getScaledAndPreservedAspectRatio() {
	// double aspect = getAspect(this);
	//
	// double preserve = Double.max(this.getWidth(), this.getHeight());
	// // Width is to be preserved
	// if (this.width == preserve)
	// this.height = (int) (this.getWidth() / aspect);
	// // Height is to be preserved
	// else if (this.getHeight() == preserve) this.width = (int)
	// (this.getHeight() * aspect);
	// }
	private static void log(String s) {
		if (debug)
		    System.out.println(s);
	}
	// ////////////////
	
	public static Point toPoint(Dimension d) {
		return new Point(d.width, d.height);
	}
	
	public static double getAspect(double w, double h) {
		return w / h;
	}
	
	/*** @param d
	 * @return returns the aspect (width/height) */
	public static double getAspect(Dimension d) {
		return d.getWidth() / d.getHeight();
	}
	
	/** @Overload
	 * @param width
	 * @param height
	 * @param containerDimension
	 * @return */
	public static FDimension fitToContainer(int width, int height, Dimension containerDimension) {
		return fitToContainer(new FDimension(width, height), containerDimension);
	}
	
	/** @param itemDimension
	 * @param containerDimension
	 * @return */
	public static FDimension fitToContainer(Dimension itemDimension, Dimension containerDimension) {
		int hDiff = (int) (containerDimension.width - itemDimension.getWidth());
		int wDiff = (int) (containerDimension.height - itemDimension.getHeight());
		
		if (wDiff > hDiff) {
			fitToWidth(itemDimension, containerDimension.width);
		} else {
			fitToHeight(itemDimension, containerDimension.height);
		}
		return new FDimension(itemDimension);
	}
	
	public static FDimension fitToHeight(Dimension d, int newHeight) {
		final FDimension fDimension = new FDimension((int) (newHeight * getAspect(d)), newHeight);
		d.setSize(fDimension);
		return fDimension;
	}
	
	public static FDimension fitToWidth(Dimension d, int newWidth) {
		final FDimension fDimension = new FDimension(newWidth, (int) (newWidth / getAspect(d)));
		d.setSize(fDimension);
		return fDimension;
	}
	
	/** @param aDimension
	 * @param bDimension
	 * @return return true if the width or height of (a) exceeds those of (b) */
	public static boolean isLargerThan(Dimension aDimension, Dimension bDimension) {
		return aDimension.getWidth() > bDimension.getWidth() || aDimension.getHeight() > bDimension.getHeight();
	}
	
	/** Ensures d is at least the size of the item. Fits d to item if d is too
	 * small
	 * 
	 * @param d
	 * @param item
	 * @return returns the FDimension object */
	public static FDimension ensureSize(Dimension d, Dimension item) {
		if (isLargerThan(item, d))
			return new FDimension(item);
		else
		    return new FDimension(d);
	}
	
	/** @param a
	 * @param b
	 * @return a-b */
	public static FDimension subtract(Dimension a, Dimension b) {
		return new FDimension(a.getWidth() - b.getWidth(), a.getHeight() - b.getHeight());
	}
	
	/** @param a
	 * @param b
	 * @return a+b */
	public static FDimension add(Dimension a, Dimension b) {
		return new FDimension(a.getWidth() + b.getWidth(), a.getHeight() + b.getHeight());
	}
	
	public static FDimension multiplyByFactor(Dimension dimension, double factor) {
		if (factor <= 0)
		    System.err.println("Cannot multiply dimension by a zero factor");
		return new FDimension(dimension.getWidth() * factor, dimension.getHeight() * factor);
	}
	
	public static FDimension addToSize(Dimension dimension, int increase) {
		return new FDimension(dimension.getWidth() + increase, (dimension.getHeight() + increase) * getAspect(dimension));
	}
}
