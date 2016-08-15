package client;

import java.io.Serializable;

public class OpenShared implements Serializable{
	public static final long serialVersionUID = 1;
	private String owner;
	private String username;
	public OpenShared(String owner, String username) {
		this.owner = owner;
		this.username = username;
	}
	public String getOwner() {
		return owner;
	}
	public String getUsername() {
		return username;
	}
}
