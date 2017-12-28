package farisImageViewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class Popup extends FFrame {

	JScrollPane scr;
	Dimension minimumDimension;
	String longestStr;

	public static void main(String[] args) {
		new Popup("Message");
	}

	public Popup(String message) {
		this("Message", message, 600, 400);
	}

	/** Makes a long String with the information of the array elements on
	 * JButtons using {@link #toString()}toString(). ActionCommand will return
	 * the string for the clicked button
	 * 
	 * @param al */
	public Popup(List<?> al) {
		this("Message");
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 1));

		for (Object o : al) {
			String string = o.toString();
			longestStr = longestStr == null ? string : (string.length() > longestStr.length() ? string : longestStr);
			JButton button = new JButton(string/*, new ImageIcon(string)*/);
			button.addActionListener(this);
			button.setHorizontalAlignment(SwingConstants.LEFT);
			p.add(button);
		}

		setScrollPane(p);
		addExitKeyListenerToAll();
		// this = new Popup("Message", message, 600, 400);
		pack();
		centerPosition();
	}

	public Popup(String title, String message, int width, int height) {
		this(title, new JTextField(message), width, height);
	}

	public Popup(String title, JComponent msgComp, int width, int height) {
		super(title, width, height);
		this.setMinimumSize(new Dimension(300, 300));
		this.setPreferredSize(new Dimension(500, 700));
		this.setMaximumSize(new Dimension(800, 800));

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		setLayout(new BorderLayout());

		msgComp.setAlignmentX(SwingConstants.CENTER);
		setScrollPane(msgComp);

		addExitKeyListenerToAll();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		pack();
		centerPosition();
	}

	private void addExitKeyListenerToAll() {
		for (Component comp : getAllComponents(this))
			comp.addKeyListener(this);
	}

	private void setScrollPane(JComponent msgComp) {
		if (scr != null)
			remove(scr);
		scr = new JScrollPane(msgComp);
		scr.getVerticalScrollBar().setUnitIncrement(20);
		scr.setViewportView(msgComp);
		scr.addKeyListener(this);
		this.getContentPane().add(scr, BorderLayout.CENTER);
		try {
			int stringWidth = this.getGraphics().getFontMetrics().stringWidth(longestStr);
			this.setMinimumSize(new Dimension(stringWidth, this.getMinimumSize().height));
			this.setPreferredSize(new Dimension(stringWidth, this.getPreferredSize().height));
		} catch (NullPointerException e) {
		}
	}

	public static void showFileInExplorer(String str) {
		System.out.println("Open file location");
		try {
			Runtime.getRuntime().exec("explorer.exe /select," + str);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override public void keyPressed(KeyEvent ke) {
		switch (ke.getKeyCode())
		{
			case KeyEvent.VK_W:
				if (!ke.isControlDown())
					break;
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				this.dispose();
			break;
		}
	}

	@Override public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			showFileInExplorer(e.getActionCommand());
		}
	}

}
