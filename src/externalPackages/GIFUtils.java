package externalPackages;
/*
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import oracle.*;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;

import com.madgag.gif.fmsware.AnimatedGifEncoder;*/


/*
 * As for GIF speed playback - I've encountered this problem too.
 * If I remember correctly this was caused by the "default" (or not provided?)
 * value for frame rate in GIF file. Some web browsers "overrided" that frame rate so that GIF played correctly.
 * As a result I created a class that converts GIF (read GIF -> write GIF) and give frame rate 
 * provided by the user.com.madgag.gif.fmsware.AnimatedGifEncoder class is an external library 
 * that I link to the project via Maven: animated-gif-lib-1.0.jar
*/
public final class GIFUtils {

	private GIFUtils() {
	}
/*
	public static List<BufferedImage> extractFrames(String filePath) throws IOException {
		return extractFrames(new File(filePath));
	}

	public static List<BufferedImage> extractFrames(File file) throws IOException {
		List<BufferedImage> imgs = new LinkedList<BufferedImage>();
		ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
		ImageInputStream in = null;
		try {
			in = ImageIO.createImageInputStream(new FileInputStream(file));
			reader.setInput(in);
			BufferedImage img = null;
			int count = reader.getNumImages(true);
			for (int i = 0; i < count; i++) {
				Node tree = reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0");
				int x = Integer.valueOf(tree.getChildNodes().item(0).getAttributes().getNamedItem("imageLeftPosition").getNodeValue());
				int y = Integer.valueOf(tree.getChildNodes().item(0).getAttributes().getNamedItem("imageTopPosition").getNodeValue());
				BufferedImage image = reader.read(i);
				if (img == null) {
					img = new BufferedImage(image.getWidth() + x, image.getHeight() + y, BufferedImage.TYPE_4BYTE_ABGR);
				}

				Graphics2D g = img.createGraphics();
				ImageUtils.setBestRenderHints(g);
				g.drawImage(image, x, y, null);
				imgs.add(ImageUtils.copy(img));
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return imgs;
	}

	public static void writeGif(List<BufferedImage> images, File gifFile, int millisForFrame) throws FileNotFoundException, IOException {
		BufferedImage firstImage = images.get(0);
		int type = firstImage.getType();

		ImageOutputStream output = new FileImageOutputStream(gifFile);

		// create a gif sequence with the type of the first image, 1 second
		// between frames, which loops continuously
		GifSequenceWriter writer = new GifSequenceWriter(output, type, 100, false);

		// write out the first image to our sequence...
		writer.writeToSequence(firstImage);
		for (int i = 1; i < images.size(); i++) {
			BufferedImage nextImage = images.get(i);
			writer.writeToSequence(nextImage);
		}

		writer.close();
		output.close();
	}

	public static Image createGif(List<BufferedImage> images, int millisForFrame) {
		AnimatedGifEncoder g = new AnimatedGifEncoder();
		ByteArrayOutputStream out = new ByteArrayOutputStream(5 * 1024 * 1024);
		g.start(out);
		g.setDelay(millisForFrame);
		g.setRepeat(1);
		for (BufferedImage i : images) {
			g.addFrame(i);
		}
		g.finish();
		byte[] bytes = out.toByteArray();
		return Toolkit.getDefaultToolkit().createImage(bytes);
	}


	// And GifSequenceWriter looks like this:

	class GifSequenceWriter {
		protected ImageWriter gifWriter;
		protected ImageWriteParam imageWriteParam;
		protected IIOMetadata imageMetaData;

		public GifSequenceWriter(ImageOutputStream outputStream, int imageType, int timeBetweenFramesMS, boolean loopContinuously) throws IIOException, IOException {
			gifWriter = getWriter();
			imageWriteParam = gifWriter.getDefaultWriteParam();
			ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);

			imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

			String metaFormatName = imageMetaData.getNativeMetadataFormatName();

			IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

			IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

			graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
			graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
			graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
			graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
			graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

			IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
			commentsNode.setAttribute("CommentExtension", "Created by MAH");

			IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");

			IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

			child.setAttribute("applicationID", "NETSCAPE");
			child.setAttribute("authenticationCode", "2.0");

			int loop = loopContinuously ? 0 : 1;

			child.setUserObject(new byte[] { 0x1, (byte) (loop & 0xFF), (byte) (loop >> 8 & 0xFF) });
			appEntensionsNode.appendChild(child);

			imageMetaData.setFromTree(metaFormatName, root);

			gifWriter.setOutput(outputStream);

			gifWriter.prepareWriteSequence(null);
		}

		public void writeToSequence(RenderedImage img) throws IOException {
			gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
		}

		public void close() throws IOException {
			gifWriter.endWriteSequence();
		}

		private ImageWriter getWriter() throws IIOException {
			Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
			if (!iter.hasNext()) {
				throw new IIOException("No GIF Image Writers Exist");
			}
			return iter.next();
		}

		private IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
			int nNodes = rootNode.getLength();
			for (int i = 0; i < nNodes; i++) {
				if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
					return (IIOMetadataNode) rootNode.item(i);
				}
			}
			IIOMetadataNode node = new IIOMetadataNode(nodeName);
			rootNode.appendChild(node);
			return node;
		}
	}
*/}
