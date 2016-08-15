package client;

import java.io.File;
import java.io.Serializable;

import javax.swing.JTextArea;

public class SaveObject implements Serializable {
	public static final long serialVersionUID = 2;
	private String text;
	private File filename;
	private String username;
	private boolean isPassive;
	public SaveObject(String text, File filename, String username, boolean isPassive) {
		this.text = text;
		this.filename = filename;
		this.username = username;
		this.isPassive = isPassive;
	}
	
	public String getText() {
		return text;
	}
	public File getFilename() {
		return filename;
	}
	public String getUsername() {
		return username;
	}
	public boolean getisPassive() {
		return isPassive;
	}
}
