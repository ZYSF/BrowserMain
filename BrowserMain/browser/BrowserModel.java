package browser;

import java.awt.Graphics2D;

public interface BrowserModel {
	int paint(Graphics2D g2d, int x, int y, int w, int h);

	void mouseIn(int x, int y);
	
	void mouseOut(int x, int y);
	
	void mouseMove(int x, int y);

	void mouseClick(int button, int x, int y);
	
	String getURL();
	String getMode();
	
	Redirect getRedirect();
	void setRedirect(Redirect redirect);

	void keyType(int keyChar);
}
