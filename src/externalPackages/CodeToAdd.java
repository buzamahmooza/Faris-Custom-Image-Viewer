package externalPackages;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class CodeToAdd extends JPanel {

    private JPanel picPanel;
    // make this the default application for opening specific file types
    // boolean editDefaultApplication() {
    // AssociationService serv = new AssociationService();
    //
    // // try to remove old association
    // Association logassoc = serv.getFileExtensionAssociation(".png");
    //
    // try {
    // serv.unregisterUserAssociation(logassoc);
    // } catch (java.lang.IllegalArgumentException |
    // AssociationNotRegisteredException | RegisterFailedException e) {
    // e.printStackTrace();
    // return false;
    // }
    //
    // // add your own application as default
    // logassoc.addFileExtension(".mp3");
    // logassoc.addAction(new org.jdesktop.jdic.filetypes.Action("open", "<path
    // to wrapper executable>"));
    // try {
    // serv.registerUserAssociation(logassoc);
    // } catch (java.lang.IllegalArgumentException | RegisterFailedException
    // | AssociationAlreadyRegisteredException e) {
    // e.printStackTrace();
    // return false;
    // }
    // return true;
    // }

    // data flavor for dragging
    // List<File> dropppedFiles =
    // (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);

    // public void zoomOut(Point point) {
    // this.picPanel.setZoom(((Wrapper) this.picPanel).getZoom() * 0.9f);
    // Point pos = this.getViewport().getViewPosition();
    //
    // int newX = (int) (pos.x * 0.9f);
    // int newY = (int) (pos.y * 0.9f);
    // this.getViewport().setViewPosition(new Point(newX, newY));
    //
    // this.picPanel.revalidate();
    // this.picPanel.repaint();
    // }
    //
    // public void zoomIn(Point point) {
    // this.picPanel.setZoom(this.picPanel.getZoom() * 1.1f);
    // Point pos = this.getViewport().getViewPosition();
    //
    // int newX = (int) (pos.x * 1.1f);
    // int newY = (int) (pos.y * 1.1f);
    // this.getViewport().setViewPosition(new Point(newX, newY));
    //
    // this.picPanel.revalidate();
    // this.picPanel.repaint();
    // }
    //
    // public void zoomOut(Point point) {
    // this.picPanel.setZoom(this.picPanel.getZoom() * 0.9f);
    // Point pos = this.getViewport().getViewPosition();
    //
    // int newX = (int) (pos.x * 0.9f);
    // int newY = (int) (pos.y * 0.9f);
    // this.getViewport().setViewPosition(new Point(newX, newY));
    //
    // this.picPanel.revalidate();
    // this.picPanel.repaint();
    // }
    //
    // public void zoomIn(Point point) {
    // this.picPanel.setZoom(this.picPanel.getZoom() * 1.1f);
    // Point pos = this.getViewport().getViewPosition();
    //
    // int newX = (int) (pos.x * 1.1f);
    // int newY = (int) (pos.y * 1.1f);
    // this.getViewport().setViewPosition(new Point(newX, newY));
    //
    // this.picPanel.revalidate();
    // this.picPanel.repaint();
    // }

    // How to zoom toward mouse in JAVASCRIPT
    int width = 600, height = 200, scale = 1, originx = 0, originy = 0;
    double visibleWidth = width, visibleHeight = height, zoomIntensity = 0.2;

    public void paint(Graphics g) { // Clear screen to white. context.fillStyle
				    // = "white";
	g.fillRect(originx, originy, 800 / scale, 600 / scale); // Draw
								// the
								// black
	g.fillRect(50, 50, 100, 100);
    } //
      // Draw loop at 60F PS.setInterval(draw,1000/60);

    /* public void onMouseWheelZoom3(MouseWheelEvent event) { // Get mouse
     * 
     * // offset. double mousex = event.clientX - canvas.offsetLeft; double mousey = event.clientY - canvas.offsetTop; // Normalize wheel to // +1 or -1. double wheel = event.wheelDelta / 120;
     * 
     * // Compute zoom factor. double zoom = Math.exp(wheel * zoomIntensity);
     * 
     * // Translate so the visible origin is at the context's origin. context.translate(originx, originy);
     * 
     * Compute the new visible origin. Originally the mouse is at a distance mouse/scale from the corner, we want the point under the mouse to remain in the same place after the zoom, but this is at mouse/new_scale away from the corner. Therefore we need to shift the origin (coordinates of thecorner) to account for this.
     * 
     * originx -= mousex / (scale * zoom) - mousex / scale; originy -= mousey / (scale * zoom) - mousey / scale;
     * 
     * // Scale it (centered around the origin due to the trasnslate above). context.scale(zoom, zoom); // Offset the visible origin to it's proper position.context.translate(-originx, -originy); // Update scale and others. scale *= zoom; visibleWidth = width / scale; visibleHeight = height / scale; } */

    private void saveScaledImage(String filePath, String outputFile) {
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
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }
}
