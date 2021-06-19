package browser;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class BrowserPanel extends JPanel {
	private JScrollPane browserScroll;
	private BrowserComponent browserComponent;
	private BrowserAddressBar addressBar;

	public BrowserPanel() {
		super(new BorderLayout());
		browserComponent = new BrowserComponent();
		browserComponent.setModel(new SimpleHTML("<html><b>foo</b> bar <p/>   <a href=\"http://wikipedia.org\">Wiki</a><p><a href=\"http://google.com/\">Google</a><b>foo</b><i>bar</i>baz</a><p> potatoes</html>"));
		browserScroll = new JScrollPane(browserComponent);
		browserScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		addressBar = new BrowserAddressBar();//new JLabel("TODO: Address bar");
		//addressBar.setSize(400, 120);
		browserComponent.addModelChangedListener(addressBar);
		
		addressBar.addAddressActivatedListener(new BrowserAddressActivationListener() {
			
			@Override
			public void activate(BrowserAddressBar bar, String url, String mode) {
				browserComponent.doRedirect(new Redirect(url, mode));
			}
		});
		
		add(addressBar, BorderLayout.NORTH);
		add(browserScroll, BorderLayout.CENTER);
	}

	public BrowserComponent getBrowserComponent() {
		return browserComponent;
	}
}
