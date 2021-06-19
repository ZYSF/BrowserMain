package browser;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;

public class BrowserComponent extends JComponent {
	private BrowserModel model;
	boolean mouseIn = false;
	private ArrayList<BrowserModelChangedListener> listeners = new ArrayList<BrowserModelChangedListener>();
	
	public BrowserComponent() {
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if (model != null) {
					model.keyType(e.getKeyChar());
				}
				checkRedirect();
				repaint();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.setRequestFocusEnabled(true);
		//this.enableInputMethods(false);
		this.setFocusable(true);
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				getModel().mouseMove(e.getX(), e.getY());
				checkRedirect();
				repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				mouseIn = false;
				getModel().mouseOut(e.getX(), e.getY());
				checkRedirect();
				repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseIn = true;
				getModel().mouseIn(e.getX(), e.getY());
				checkRedirect();
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
				getModel().mouseClick(e.getButton(), e.getX(), e.getY());
				checkRedirect();
				repaint();
			}
		});
	}

	public BrowserModel getModel() {
		if (model == null) {
			return NullBrowserModel.INSTANCE;
		}
		return model;
	}
	
	public void setModel(BrowserModel model) {
		this.model = model;
		if (mouseIn) {
			model.mouseIn(0, 0);
		}
		repaint();
		checkRedirect();
		for (BrowserModelChangedListener l: listeners) {
			l.modelChanged(this);
		}
	}
	
	boolean justResized = false;
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Graphics2D inner = (Graphics2D) g2d.create();
		int h = getModel().paint(inner, 0, 0, getWidth(), getHeight());
		if (h != getHeight()/* && !justResized*/) {
			System.out.println("Resizing from " + getHeight() + " to " + h);
			setMaximumSize(new Dimension(2000, h));
			setMinimumSize(new Dimension(100, h));
			setPreferredSize(new Dimension(getWidth(), h));
			setSize(getWidth(), h);
			invalidate();
			/*if (getParent() instanceof JScrollPane) {
				System.out.println("Scroll");
				JScrollPane p = ((JScrollPane)getParent());
				p.removeAll();
				p.add(this);
				p.setViewportView(this);
			}*/
			justResized = true;
		} else {
			justResized = false;
		}
		inner.dispose();

	}
	
	public void checkRedirect() {
		Redirect r = getModel().getRedirect();
		if (r != null) {
			doRedirect(r);
		}
	}
	
	public void doRedirect(Redirect r) {
		System.out.println("Redirecting to '" + r.getURL() + "'");
		try {
			HttpClient c = new HttpClient();
			HttpMethod m = new GetMethod(r.getURL());
			c.executeMethod(m);
			byte[] result = m.getResponseBody();
			String rt = new String(result, "UTF-8");
			System.out.println("Got '" + rt + "'");
			setModel(new SimpleHTML(Jsoup.parse(rt, r.getURL())));
			m.releaseConnection();
		} catch (Exception e) {
			throw new Error("Shit happens", e);
		}
	}
	
	public void redirect(String url) {
		doRedirect(new Redirect(url, "simple-html"));
	}
	
	public void addModelChangedListener(BrowserModelChangedListener l) {
		listeners.add(l);
	}
	
	public void removeModelChangedListener(BrowserModelChangedListener l) {
		listeners.remove(l);
	}
}
