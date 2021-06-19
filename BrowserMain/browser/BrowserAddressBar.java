package browser;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BrowserAddressBar extends JPanel implements BrowserModelChangedListener {
	JTextField addressField;
	JButton goButton;
	private ArrayList<BrowserAddressActivationListener> listeners = new ArrayList<BrowserAddressActivationListener>();
	
	public BrowserAddressBar() {
		super(new BorderLayout());
		addressField = new JTextField(30);
		goButton = new JButton("Go");
		
		ActionListener activator = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (BrowserAddressActivationListener l: listeners) {
					l.activate(BrowserAddressBar.this, addressField.getText().startsWith("http")?addressField.getText():"http://"+addressField.getText(), "simple-html");
				}
			}
		};
		addressField.addActionListener(activator);
		goButton.addActionListener(activator);
		
		add(addressField, BorderLayout.CENTER);
		add(goButton, BorderLayout.EAST);
	}

	@Override
	public void modelChanged(BrowserComponent component) {
		addressField.setText(component.getModel().getURL());
	}
	
	public void addAddressActivatedListener(BrowserAddressActivationListener l) {
		listeners.add(l);
	}
	
	public void removeAddressActivatedListener(BrowserAddressActivationListener l) {
		listeners.remove(l);
	}
}
