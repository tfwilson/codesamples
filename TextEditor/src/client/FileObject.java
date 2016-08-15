package client;

import java.io.File;
import java.io.Serializable;

public class FileObject implements Serializable{
	public static final long serialVersionUID = 1;
	private File[] files;
	private boolean isSave;
	public FileObject(File[] files, boolean isSave) {
		this.files = files;
		this.isSave = isSave;
	}
	
	public File[] getFiles() {
		return files;
	}
	public boolean getIsSave() {
		return isSave;
	}
}
