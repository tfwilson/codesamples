package client;

import java.io.Serializable;

public class RemoveUser implements Serializable{
	public static final long serialVersionUID = 1;
	private String removedUser;
	private String filename;
	private String owner;
	public RemoveUser(String removedUser, String owner, String filename) {
		this.removedUser = removedUser;
		this.filename = filename;
		this.owner = owner;
	}
	public String getRemoved() {
		return removedUser;
	}
	public String getFilename() {
		return filename;
	}
	public String getOwner() {
		return owner;
	}
}
