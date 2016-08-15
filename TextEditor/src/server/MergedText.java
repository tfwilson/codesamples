package server;

import java.io.File;
import java.io.Serializable;

import javax.swing.JTextArea;

public class MergedText implements Serializable {
	public static final long serialVersionUID = 2;
	private String text;
	private String filename;
	private String owner;
	public MergedText(String text, String filename, String owner) {
		this.text = text;
		this.filename = filename;
		this.owner = owner;
	}
	
	public String getText() {
		return text;
	}
	public String getFilename() {
		return filename;
	}
	public String getOwner() {
		return owner;
	}
	
}