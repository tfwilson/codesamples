package client;

import java.io.Serializable;

public class AddCheck implements Serializable{
	public static final long serialVersionUID = 1;
	private String owner;
	private String addedname;
	private String filename;
	public AddCheck(String owner, String addedname, String filename) {
		this.owner = owner;
		this.addedname = addedname;
		this.filename = filename;
	}
	public String getOwner() {
		return owner;
	}
	public String getAddedName() {
		return addedname;
	}
	public String getFilename() {
		return filename;
	}
}
