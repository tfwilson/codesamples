package client;

import java.io.Serializable;

public class RemoveCheck implements Serializable{
	public static final long serialVersionUID = 1;
	private String owner;
	private String filename;
	public RemoveCheck(String owner, String filename) {
		this.owner = owner;
		this.filename = filename;
	}
	public String getOwner() {
		return owner;
	}
	public String getFilename() {
		return filename;
	}
}
