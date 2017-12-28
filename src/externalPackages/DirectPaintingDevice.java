package externalPackages;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;

//Create a DirectPaintingDevice, and use device.graphics... to paint things using a Graphics2D object.
//Then, use device.update(); to update the windows. It creates a lot of rectangular JFrames.
//
//However, keep in mind that this code is extremely slow and laggy.

public final class DirectPaintingDevice {
	public final Graphics2D graphics;
	private BufferedImage image;
	private final ArrayList<JFrame> frames = new ArrayList<>(0);
	private final ArrayList<Runnable> actions = new ArrayList<>(0);

	public DirectPaintingDevice() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.image = new BufferedImage(screen.width, screen.height, BufferedImage.TYPE_INT_ARGB);
		this.graphics = this.image.createGraphics();
		Color color = this.graphics.getColor();
		this.graphics.setColor(new Color(0, true));
		this.graphics.fillRect(0, 0, screen.width, screen.height);
		this.graphics.setColor(color);
	}

	public synchronized void update() {
		this.frames.forEach(JFrame::dispose);
		this.frames.clear();
		ArrayList<Point> done_points = new ArrayList<>(0);
		for (int x = 0; x < this.image.getWidth(); x++) {
			for (int y = 0; y < this.image.getHeight(); y++) {
				if (this.image.getRGB(x, y) >> 24 == 0 || done_points.contains(new Point(x, y))) {
					continue;
				}
				final int cx = x;
				final int cy = y;
				int w;
				for (w = 0; w < this.image.getWidth() - x && this.image.getRGB(x + w, y) >> 24 == this.image.getRGB(x, y) >> 24; w++) {
					// Empty loop
				}
				int h;
				outer: for (h = 0; h < this.image.getHeight() - y; h++) {
					for (int cw = 0; cw < w; cw++) {
						if (this.image.getRGB(x + cw, h + y) >> 24 != this.image.getRGB(x, y) >> 24) {
							break outer;
						}
					}
					for (int i = 0; i < w; i++) {
						done_points.add(new Point(i + x, h + y));
					}
				}
				final int cw = w;
				final int ch = h;
				JFrame frame = new JFrame();
				frame.setUndecorated(true);
				frame.setDefaultCloseOperation(0);
				frame.setSize(w, h);
				frame.setLocation(x, y);
				frame.setOpacity((int) (1 - ((double) (this.image.getRGB(x, y) >> 24) / 505)));
				frame.toFront();
				frame.setAlwaysOnTop(true);
				frame.add(new JPanel() {
					private static final long serialVersionUID = 6246612303574731899L;

					public void paintComponent(Graphics g) {
						g.drawImage(DirectPaintingDevice.this.image.getSubimage(cx, cy, cw, ch), 0, 0, null);
					}
				});
				frame.setVisible(true);
				System.out.println("Created Frame : [" + x + ", " + y + ", " + w + ", " + h + "];");
				this.frames.add(frame);
			}
		}
		this.actions.forEach(Runnable::run);
	}

	public synchronized void allFrames(Consumer<JFrame> action) {
		synchronized (this) {
			this.frames.forEach(action);
		}
	}

	public synchronized void exec(Consumer<Graphics2D> action) {
		new Thread(() -> {
			synchronized (DirectPaintingDevice.this) {
				action.accept(DirectPaintingDevice.this.graphics);
			}
		}).start();
	}

	public void onUpdate(Runnable action) {
		this.actions.add(action);
	}

	public synchronized void setImage(BufferedImage image) {
		this.image = image;
	}

	public static void main(String... args) throws IOException {
		DirectPaintingDevice device = new DirectPaintingDevice();
		device.graphics.setColor(Color.BLACK);
		device.graphics.fillRect(0, 0, 50, 50);
		device.graphics.fillRect(0, 50, 50, 50);
		device.graphics.fillRect(0, 100, 50, 50);
		device.graphics.fillRect(50, 0, 50, 50);
		device.graphics.fillRect(50, 100, 50, 50);
		device.graphics.fillRect(100, 0, 50, 50);
		device.graphics.fillRect(100, 50, 50, 50);
		device.graphics.fillRect(100, 100, 50, 50);
		device.graphics.fillRect(0, 70, 100, 10);
		device.graphics.fillRect(70, 0, 10, 100);
		device.update();
	};
}
