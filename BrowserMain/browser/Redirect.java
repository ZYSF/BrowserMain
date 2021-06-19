package browser;

public class Redirect {
	private String url;
	private String mode;
	
	public Redirect(String url, String mode) {
		this.url = url;
		this.mode = mode;
	}

	public String getURL() {
		return url;
	}
	
	public String getMode() {
		return mode;
	}

}
