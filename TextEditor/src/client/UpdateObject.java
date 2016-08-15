package client;

import java.io.File;
import java.io.Serializable;

import javax.swing.JTextArea;

public class UpdateObject implements Serializable {
	public static final long serialVersionUID = 2;
	private String text;
	private String filename;
	private String username;
	private String owner;
	public UpdateObject(String text, String filename, String owner, String username) {
		this.text = text;
		this.filename = filename;
		this.username = username;
		this.owner = owner;
	}
	
	public String getText() {
		return text;
	}
	public String getFilename() {
		return filename;
	}
	public String getUsername() {
		return username;
	}
	public String getOwner() {
		return owner;
	}
	
}
