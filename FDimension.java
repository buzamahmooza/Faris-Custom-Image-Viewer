package FarisImageViewer;

import java.awt.Dimension;



public class FDimension extends Dimension
{

	// tester
	public static void main(String[] args) {
		FDimension d = new FDimension(4, 4);
		System.out.println(d);
		d.fitToContainer(new Dimension(3, 4));
		System.out.println("After fit: " + d);
	}

	public FDimension(Dimension dimension) {
		super((Dimension) dimension);
	}

	public FDimension(int w, int h) {
		super(w, h);
	}
	// End of constructors

	public double getAspect() {
		return getAspect(this);
	}

	public void fitToContainer(Dimension containerDimension) {
		int hDiff = (int) (containerDimension.width - this.getWidth());
		int wDiff = (int) (containerDimension.height - this.getHeight());

		if (wDiff > hDiff)
			fitToWidth(containerDimension.width);
		else fitToHeight(containerDimension.height);
	}

	public void fitToWidth(int newWidth) {
		if (newWidth <= 0) return;
		final int newHeight = (int) (newWidth / getAspect());
		this.setSize(new FDimension(newWidth, newHeight));
	}

	public void fitToHeight(int newHeight) {
		if (newHeight <= 0) return;
		final int newWidth = (int) (newHeight * getAspect());
		this.setSize(new FDimension(newWidth, newHeight));
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

	// ////////////////
	public static double getAspect(double w, double h) {
		return w / h;
	}

	/***
	 * @param d
	 * @return returns the aspect (width/height)
	 */
	public static double getAspect(Dimension d) {
		return d.getWidth() / d.getHeight();
	}

	/**
	 * @Overload
	 * @param width
	 * @param height
	 * @param containerDimension
	 * @return
	 */
	public static FDimension fitToContainer(int width, int height, Dimension containerDimension) {
		return fitToContainer(new FDimension(width, height), containerDimension);
	}

	/**
	 * @param itemDimension
	 * @param containerDimension
	 * @return
	 */
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

}
