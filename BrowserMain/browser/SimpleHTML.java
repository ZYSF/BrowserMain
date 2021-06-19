package browser;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class SimpleHTML extends NullBrowserModel {
	private Document document;
	private HashMap<String,BufferedImage> imgcache = new HashMap<String, BufferedImage>();
	private boolean doImages = true;

	private HashMap<Element, Form> formmap = new HashMap<Element, SimpleHTML.Form>();
	private HashMap<Element, Input> inputmap = new HashMap<Element, SimpleHTML.Input>();
	private Input focusedInput = null;
	
	public SimpleHTML(String source) {
		document = Jsoup.parse(source);
	}
	public SimpleHTML(Document document) {
		this.document = document;
	}
	
	private class Context {
		Graphics2D g2d;
		int x, y, w, h;
		int cx, cy;
		boolean click = false;
		boolean needsDraw(int partw, int parth) {
			return true;
		}
	}
	
	private class Form {
		Element e;
		ArrayList<Input> inputs = new ArrayList<SimpleHTML.Input>();
		Form(Element e) {
			this.e = e;
			System.out.println("Got form attrs: " + e.attributes());
		}
		
		public String getActionString() {
			return e.attr("action");
		}
	}
	
	private class Input {
		Form f;
		Element e;
		String editedValue = null;
		Input(Form f, Element e) {
			this.f = f;
			this.e = e;
			System.out.println("Got input attrs: " + e.attributes());
		}
		
		public String getType() {
			if (e.hasAttr("type")) {
				return e.attr("type");
			} else {
				return "text";
			}
		}
		
		boolean isSubmit() {
			return getType().equals("submit");
		}
		
		boolean isText() {
			return getType().equals("text");
		}
		
		String getName() {
			return e.attr("name");
		}
		
		String getValue() {
			if (editedValue != null) {
				return editedValue;
			} else {
				return e.hasAttr("value") ? e.attr("value") : "";
			}
		}

		public void keyType(int key) {
			if (key == 8) { // Backspace
				if (editedValue.length() < 2) {
					editedValue = "";
				} else {
					editedValue = editedValue.substring(0, editedValue.length() - 1);
				}
				return;
			}
			String s = new String(new char[] {(char) key});
			if (editedValue == null) {
				editedValue = s;
			} else {
				editedValue += s;
			}
		}
	}
	
	public Form formForElement(Element e) {
		if (!formmap.containsKey(e)) {
			formmap.put(e, new Form(e));
		}
		return formmap.get(e);
	}
	
	public Input inputForElement(Form f, Element e) {
		if (!inputmap.containsKey(e)) {
			inputmap.put(e, new Input(f, e));
			f.inputs.add(inputmap.get(e));
		}
		return inputmap.get(e);
	}
	
	@Override
	public int paint(Graphics2D g2d, int x, int y, int w, int h) {
		System.out.println("painting " + x + " " + y + " " + w + " " + h);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(x, y, w, h);
		g2d.setColor(Color.BLACK);
		Context c = new Context();
		c.g2d = g2d;
		c.x = x;
		c.y = y;
		c.w = w;
		c.h = h;
		c.cx = 0;
		c.cy = 0;
		/*for (Element sube: document.getAllElements()) {
			paint(sube, c);
		}*/
		paint(null, document, c);
		if (c.cy > y + h) {
			return c.cy;
		} else {
			return y + h;
		}
	}
	
	private void paintSubnodes(Form outerForm, Element e, Context c) {
		for (Node n: e.childNodes()) {
			paint(outerForm, n, c);
		}
	}
	
	public static String enc(String x) {
		x = x.replace(' ', '+');
		return x;
	}
	
	private void paint(Form outerForm, Element e, Context c) {
		/*if (e.toString().contains("doctype")) {
			System.out.println("Doctype tag is '" + e.tagName() + "'");
		}*/
		switch (e.tagName()) {
		case "img": {
			if (doImages && e.hasAttr("src")) {
				String imgurl = e.attr("abs:src");
				BufferedImage img = null;
				if (imgcache.containsKey(imgurl)) {
					img = imgcache.get(imgurl);
				} else {
					try {
						HttpClient cl = new HttpClient();
						HttpMethod m = new GetMethod(imgurl);
						cl.executeMethod(m);
						byte[] result = m.getResponseBody();
						img = ImageIO.read(new ByteArrayInputStream(result));
						imgcache.put(imgurl, img);
						m.releaseConnection();
					} catch (Exception ex) {
						throw new Error("Shit happens", ex);
					}
				}
				if (img != null) {
					c.g2d.drawImage(img, null, c.cx, c.cy);
					c.cy += img.getHeight();
				}
			} else if (e.hasAttr("alt")) {

				c.g2d.drawString(e.attr("alt"), c.cx, c.cy + 20);
				c.cy += 20;
			}
		} break;
		case "head":
		case "script":
		case "!doctype html":
			break; // Ignore.
		case "div":
		case "br":
		case "p":
			c.cy += 15; // Add paragraph break and paint subnodes
			paintSubnodes(outerForm, e, c);
			break;
		case "h1": {
			Font f = c.g2d.getFont();
			Font bold = f.deriveFont(25);
			c.g2d.setFont(bold);
			paintSubnodes(outerForm, e, c);
			c.g2d.setFont(f);
		} break;
		case "b": {
			Font f = c.g2d.getFont();
			Font bold = f.deriveFont(Font.BOLD);
			c.g2d.setFont(bold);
			paintSubnodes(outerForm, e, c);
			c.g2d.setFont(f);
		} break;
		case "i": {
			Font f = c.g2d.getFont();
			Font bold = f.deriveFont(Font.ITALIC);
			c.g2d.setFont(bold);
			paintSubnodes(outerForm, e, c);
			c.g2d.setFont(f);
		} break;
		case "a": {
			
			Color col = c.g2d.getColor();

			if (mouseIn && mouseY >= c.cy && mouseY < c.cy + 20) {
				c.g2d.setColor(Color.RED);
				if (c.click) {
					String l = "?";
					if (e.hasAttr("href")) {
						l = e.attr("abs:href"); // abs: makes JSoup expand the URL for us
					}
					System.out.println("Goint to link '" + l + "'");
					setRedirect(new Redirect(l, getMode()));
				}
			} else {
				c.g2d.setColor(Color.BLUE);
			}
			
			paintSubnodes(outerForm, e, c);
			c.g2d.setColor(col);
		} break;
		case "#root":
		case "html":
		case "body":
		case "center":
		case "nobr":
		case "span":
		case "table":
		case "tbody":
		case "tr":
		case "td":
		case "ul":
		case "li":
			paintSubnodes(outerForm, e, c);
			break;
		case "form": {
			Form f = formForElement(e);
			paintSubnodes(f, e, c);
			break;
		}
		case "input": {
			if (outerForm != null) {
				Input i = inputForElement(outerForm, e);
				if (i.isSubmit()) {
					Color col = c.g2d.getColor();

					c.g2d.setColor(Color.GREEN);
					c.g2d.fillRect(c.cx, c.cy, 200, 20);
					if (mouseIn && mouseY >= c.cy && mouseY < c.cy + 20) {
						c.g2d.setColor(Color.RED);
						if (c.click) {
							String l = "?";
							if (i.f.e.hasAttr("action")) {
								l = i.f.e.attr("abs:action"); // abs: makes JSoup expand the URL for us
							}
							System.out.println("Using form action '" + l + "'");
							for (int x = 0; x < i.f.inputs.size(); x++) {
								Input xi = i.f.inputs.get(x);
								if (x == 0) {
									l += "?";
								} else {
									l += "&";
								}
								l += enc(xi.getName()) + "=" + enc(xi.getValue());
							}
							System.out.println("Goint to form link '" + l + "'");
							setRedirect(new Redirect(l, getMode()));
							System.out.println("TODO...");
						}
					} else {
						c.g2d.setColor(Color.BLUE);
					}
					c.g2d.drawRect(c.cx, c.cy, 200, 20);

					c.g2d.drawString(i.getValue(), c.cx, c.cy + 17);
					c.cy += 20;
					paintSubnodes(outerForm, e, c);
					c.g2d.setColor(col);
				} else if (i.isText()) {
					Color col = c.g2d.getColor();
					Color outCol = Color.BLUE;

					if (mouseIn && mouseY >= c.cy && mouseY < c.cy + 20) {
						outCol = Color.RED;
						if (c.click) {
							focusedInput = i;
						}
					}
					c.g2d.setColor((i == focusedInput) ? Color.YELLOW : Color.LIGHT_GRAY);
					c.g2d.fillRect(c.cx, c.cy, 200, 20);
					c.g2d.setColor(outCol);
					c.g2d.drawRect(c.cx, c.cy, 200, 20);

					c.g2d.drawString(i.getValue(), c.cx, c.cy + 17);
					c.cy += 20;
					paintSubnodes(outerForm, e, c);
					c.g2d.setColor(col);
				} else {
					paintSubnodes(outerForm, e, c);
				}
			} else {
				paintSubnodes(outerForm, e, c);
			}
			break;
		}
		default:
			System.out.println("Unrecognised tag '" + e.tagName() + "'");
			c.g2d.drawString(e.toString(), c.cx, c.cy + 20);
			c.cy += 20;
		}
	}
	private void paint(Form outerForm, Node n, Context c) {
		if (n instanceof Element) {
			paint(outerForm, (Element) n, c);
		} else if (n instanceof DocumentType || n instanceof Comment) {
			// Ignore
		} else {
			/*
			if (n.toString().contains("doctype")) {
				System.out.println("Doctype node '" + n.getClass() + "'");
			}*/
			c.g2d.drawString(n.toString(), c.cx, c.cy + 20);
			c.cy += 20;
		}
	}

	@Override
	public void mouseClick(int button, int x, int y) {
		mouseX = x;
		mouseY = y;
		if (button == 1) {
			BufferedImage i = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Context c = new Context();
			c.g2d = i.createGraphics();
			c.x = x;
			c.y = y;
			c.w = 0;
			c.h = 0;
			c.cx = 0;
			c.cy = 0;
			c.click = true;
			paint(null, document, c);
		} else {
			super.mouseClick(button, x, y);
		}
	}
	
	@Override
	public void keyType(int keyChar) {
		if (focusedInput != null) {
			focusedInput.keyType(keyChar);
		}// else {
			super.keyType(keyChar);
		//}
	}
	
	@Override
	public String getMode() {
		return "simple-html";
	}
	
	@Override
	public String getURL() {
		return document.location();
	}
}
