package client;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

public class SharedFiles implements Serializable{
	public static final long serialVersionUID = 1;
	private String owner;
	private Vector<File> filenames;
	public SharedFiles(String owner, Vector<File> filenames) {
		this.owner = owner;
		this.filenames = filenames;
	}
	public String getOwner() {
		return owner;
	}
	public Vector<File> getFilenames() {
		return filenames;
	}
}
