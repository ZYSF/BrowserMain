package browser;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class BrowserWindow extends JFrame {
	private BrowserPanel browserPanel;

	public BrowserWindow() throws HeadlessException {
		super("Browser");
		browserPanel = new BrowserPanel();
		setContentPane(browserPanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BrowserWindow f = new BrowserWindow();
				f.setSize(500, 400);
				f.setDefaultCloseOperation(EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}
}
