package client;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

public class UserInfo implements Serializable {
	public static final long serialVersionUID = 1;
	private String username;
	private String password;
	private boolean isLogin;
	private Vector<File> files;
	public UserInfo(String username, String password, boolean isLogin) {
		this.username = username;
		this.password = password;
		this.isLogin = isLogin;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public boolean getType() {
		return isLogin;
	}
}
