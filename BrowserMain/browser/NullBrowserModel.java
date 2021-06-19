package browser;

import java.awt.Color;
import java.awt.Graphics2D;

public class NullBrowserModel implements BrowserModel {
	int mouseX, mouseY;
	boolean mouseIn;
	public static final NullBrowserModel INSTANCE = new NullBrowserModel();
	private Redirect redirect;
	
	@Override
	public int paint(Graphics2D g2d, int x, int y, int w, int h) {
		System.out.println("painting " + x + " " + y + " " + w + " " + h);
		g2d.setColor(Color.RED);
		g2d.fillRect(x, y, w, h);
		return h;
	}
	
	@Override
	public void mouseIn(int x, int y) {
		mouseIn = true;
		mouseX = x;
		mouseY = y;
	}
	
	@Override
	public void mouseOut(int x, int y) {
		mouseIn = false;
		mouseX = x;
		mouseY = y;
	}
	
	@Override
	public void mouseMove(int x, int y) {
		if (mouseIn) {
			mouseX = x;
			mouseY = y;
			System.out.println("Mouse " + x + " " + y);
		}
	}
	@Override
	public void mouseClick(int button, int x, int y) {
		mouseX = x;
		mouseY = y;
		System.out.println("Click " + x + " " + y + " button " + button);
	}
	@Override
	public String getURL() {
		return "null://null";
	}
	@Override
	public String getMode() {
		return "null";
	}
	@Override
	public Redirect getRedirect() {
		return redirect;
	}
	@Override
	public void setRedirect(Redirect redirect) {
		this.redirect = redirect;
	}
	
	public void redirect(String url) {
		setRedirect(new Redirect(url, getMode()));
	}
	
	@Override
	public void keyType(int keyChar) {
		System.out.println("Key " + keyChar + " typed ('" + new String(new char[] {(char)keyChar}) + "')");
	}
}
