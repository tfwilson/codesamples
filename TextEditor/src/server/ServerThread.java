package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTextArea;

import client.AddCheck;
import client.FileObject;
import client.MergeMapInfo;
import client.OpenShared;
import client.RemoveCheck;
import client.RemoveUser;
import client.SaveObject;
import client.SharedFiles;
import client.UpdateObject;
import client.UserInfo;


public class ServerThread extends Thread {
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ServerListener sl;
	private String username;
	public ServerThread(Socket s, ServerListener sl) {
		try {
			this.sl = sl;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public void sendMessage(String message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendMessage ioe: " + ioe.getMessage()); 
		}
	}
	public void sendValidity(boolean isValid) {
		try {
			oos.writeObject(isValid);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendValidity ioe: " + ioe.getMessage()); 
		}
	}
	public void sendRemoveList(Vector<String> users) {
		try {
			oos.writeObject(users);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendMessage ioe: " + ioe.getMessage()); 
		}
	}
	public void sendRemoveUser(RemoveUser removeinfo) {
		try {
			oos.writeObject(removeinfo);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendMessage ioe: " + ioe.getMessage()); 
		}
	}
	private void sendOwnerSet(Set ownerSet) {
		try {
			oos.writeObject(ownerSet);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendMessage ioe: " + ioe.getMessage()); 
		}
	}
	private void sendSharedFiles(SharedFiles shared) {
		try {
			oos.writeObject(shared);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendSharedFiles ioe: " + ioe.getMessage()); 
		}
	}
	public void sendFiles(FileObject files) {
		try {
			oos.writeObject(files);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendFiles ioe: " + ioe.getMessage()); 
		}
	}
	public void sendUpdate(MergedText merge) {
		try {
			oos.writeObject(merge);
			oos.flush();
		} catch(IOException ioe) {
			System.out.println("sendFiles ioe: " + ioe.getMessage()); 
		}
	}
	public String getUsername() {
		return username;
	}
	
	public void run() {
		try {
			while (true) {
				Object obj = ois.readObject();
				if (obj instanceof UserInfo) {
					if (((UserInfo) obj).getType() == false) {
						TEditServerGUI.displayMessage("Sign-up attempt User: " + ((UserInfo) obj).getUsername() + " Password: " + ((UserInfo) obj).getPassword().toString());
						if (TEditServerGUI.hasUsername(((UserInfo) obj).getUsername()) == false) {
								TEditServerGUI.displayMessage("Sign-up success User: " + ((UserInfo) obj).getUsername());
								//add user to map
								TEditServerGUI.addUser((UserInfo) obj);
								File newuserfile = new File("SavedFiles/" + ((UserInfo) obj).getUsername());
								newuserfile.mkdir();
								username = ((UserInfo) obj).getUsername();
								sendMessage("free");
							} else {
								TEditServerGUI.displayMessage("Sign-up failure User: " + ((UserInfo) obj).getUsername());
								sendMessage("taken");
							}
					} else {
						//login
						TEditServerGUI.displayMessage("Log-in attempt User: " + ((UserInfo) obj).getUsername() + " Password: " + ((UserInfo) obj).getPassword().toString());
						if (TEditServerGUI.getValidity((UserInfo) obj) == false) {
							TEditServerGUI.displayMessage("Log-in failure User: " + ((UserInfo) obj).getUsername());
							sendValidity(false);
						} else {
							TEditServerGUI.displayMessage("Log-in success User: " + ((UserInfo) obj).getUsername());
							username = ((UserInfo) obj).getUsername();
							sendValidity(true);
						}
					}
				} else if(obj instanceof String){
					if (((String) obj).charAt(0) == '#') {
						//save username make new savechooser
						String username = "";
						for (int i = 1; i < ((String)obj).length(); i++ ) {
							username += ((String) obj).charAt(i);
						}
						File folder = new File("SavedFiles/" + username);
						File [] files = folder.listFiles();
						sendFiles(new FileObject(files, true));
						
					} else if (((String) obj).charAt(0) == '@') {
						//open username make new openchooser
						String username = "";
						for (int i = 1; i < ((String)obj).length(); i++ ) {
							username += ((String) obj).charAt(i);
						}
						//jdbc
						Connection conn = null;
						Set<String> fileOwners = new HashSet<String>();
						try {
							Class.forName("com.mysql.jdbc.Driver");
							conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
							String query = "SELECT Filepath FROM FileShare WHERE Shareduser = ?";
							PreparedStatement ps = conn.prepareStatement(query);
							ps.setString(1, username);
							ResultSet rs; 
							rs = ps.executeQuery();
							
							while (rs.next()) {
								String filepath = rs.getString("Filepath");
								String ownerquery = "SELECT Owner FROM Files WHERE Filepath = ?";
								PreparedStatement ownerps = conn.prepareStatement(ownerquery);
								ownerps.setString(1, filepath);
								ResultSet ownerrs; 
								ownerrs = ownerps.executeQuery();
								while(ownerrs.next()) {
									String owner = ownerrs.getString("Owner");
									fileOwners.add(owner);
									System.out.println("owner: " + owner);
								}
							}
						} catch (SQLException sqle){
							System.out.println("sqle: " + sqle.getMessage());
						} catch(ClassNotFoundException cnfe) {
							System.out.println("cnfe: " + cnfe.getMessage());
						} finally {
							try {
								if (conn != null) {
									conn.close();
								} 
							} catch (SQLException sqle) {
									System.out.println("sqle clsoing conn: " + sqle.getMessage());
								}
						}
							
						sendOwnerSet(fileOwners);
					} else if (((String) obj).charAt(0) == '$') {
						String username = "";
						for (int i = 1; i < ((String)obj).length(); i++ ) {
							username += ((String) obj).charAt(i);
						}
						File folder = new File("SavedFiles/" + username);
						File [] files = folder.listFiles();
						sendFiles(new FileObject(files, false));
					} else if (((String) obj).equals("*")) {
						//terminator, update terminator count in sl
						sl.updateTerminatorCount();
					}
				} else if (obj instanceof SaveObject) {
					//if (TEditServerGUI.getRunning()) {
					String file = ((SaveObject)obj).getFilename().getName();
					String username = ((SaveObject)obj).getUsername();
					sl.appendMergeMap("SavedFiles/" + username + "/" + file, ((SaveObject)obj).getText(), username, file);
					System.out.println("ABSOLUTE: " + "SavedFiles/" + username + "/" + file);
					sl.saveFile("SavedFiles/" + username + "/" + file);
					sendMessage("saved");
//							if (((SaveObject)obj).getisPassive() == false) {
//								sendMessage("saved");
//							}
					//}
					/*else {
						sendMessage("failed");
						String file = ((SaveObject)obj).getFilename().getName();
						String username = ((SaveObject)obj).getUsername();
						TEditServerGUI.displayMessage("File save failed User: " + username + " File: " + file);
						
					}*/
				}
				else if (obj instanceof AddCheck) {
					// checlk
					String filename = ((AddCheck)obj).getFilename();
					String username = ((AddCheck)obj).getOwner();
					String other = ((AddCheck)obj).getAddedName();
					String absolute = "SavedFiles/" + username + "/" + filename;
					File file = new File(absolute);
					if (file.isFile() == false) {
						sendMessage("offline");
					}
					else if (TEditServerGUI.hasUsername(other) == false) {
						sendMessage("ghost");
					}
					else {
						//check SQL if they are already shared
						Connection conn = null;
						try {
							Class.forName("com.mysql.jdbc.Driver");
							conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
							String query = "SELECT Shareduser FROM FileShare WHERE Filepath = ?";
							PreparedStatement ps = conn.prepareStatement(query);
							ps.setString(1, absolute);
							System.out.println("Path of file adding to: " + absolute);
							ResultSet rs; 
							rs = ps.executeQuery();
							System.out.println("selecting");
							boolean isShared = false;
							boolean isEmpty = false;
							while (rs.next()) {
								String shareduser = rs.getString("Shareduser");
								if (shareduser.equals(other)) {
									System.out.println("Found: " + shareduser);
									sendMessage("shared");
									isShared = true;
								}
							}
							String ownercheck = "SELECT Owner FROM Files WHERE Filepath = ?";
							PreparedStatement st = conn.prepareStatement(ownercheck);
							st.setString(1, absolute);
							System.out.println("Checking owner: " + absolute);
							ResultSet rs1; 
							rs1 = st.executeQuery();
							if (!rs1.isBeforeFirst()) {
								isEmpty = true;
							}
							if (isShared == false) {
								//add info to SQL
								if (isEmpty == true) {
									System.out.println("inserting owner");
									String updateFile = "INSERT INTO Files ("
								    + "Filepath,"
								    + "Owner ) VALUES ("
								    + "?, ?)";
									PreparedStatement insertFile = conn.prepareStatement(updateFile);
									insertFile.setString(1, absolute);
									insertFile.setString(2, username);
									insertFile.executeUpdate();
									insertFile.close();
								}
								String updateFileShare = "INSERT INTO FileShare ("
									    + "Filepath,"
									    + "Shareduser ) VALUES ("
									    + "?, ?)";
										PreparedStatement insertFileShare = conn.prepareStatement(updateFileShare);
										insertFileShare.setString(1, absolute);
										insertFileShare.setString(2, other);
										insertFileShare.executeUpdate();
										insertFileShare.close();
	
							}
						} catch (SQLException sqle){
							System.out.println("sqle: " + sqle.getMessage());
						} catch(ClassNotFoundException cnfe) {
							System.out.println("cnfe: " + cnfe.getMessage());
						} finally {
							try {
								if (conn != null) {
									conn.close();
								} 
							} catch (SQLException sqle) {
									System.out.println("sqle clsoing conn: " + sqle.getMessage());
								}
						}
					}
				}
				//give editor list of users it has shared that file with
				else if (obj instanceof RemoveCheck) {
					String filename = ((RemoveCheck)obj).getFilename();
					String username = ((RemoveCheck)obj).getOwner();
					String absolute = "SavedFiles/" + username + "/" + filename;
					Connection conn = null;
					try {
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
						String query = "SELECT Shareduser FROM FileShare WHERE Filepath = ?";
						PreparedStatement st = conn.prepareStatement(query);
						st.setString(1, absolute);
						System.out.println("Getting all users that share: " + absolute);
						ResultSet rs; 
						rs = st.executeQuery();
						Vector<String> users = new Vector<String>();
						while (rs.next()) {
							String shareduser = rs.getString("Shareduser");
							System.out.println("User that shares: " + shareduser);
							users.add(shareduser);
						}
						sendRemoveList(users);
					} catch (SQLException sqle){
						System.out.println("sqle: " + sqle.getMessage());
					} catch(ClassNotFoundException cnfe) {
						System.out.println("cnfe: " + cnfe.getMessage());
					} finally {
						try {
							if (conn != null) {
								conn.close();
							} 
						} catch (SQLException sqle) {
								System.out.println("sqle clsoing conn: " + sqle.getMessage());
							}
					}
				} else if (obj instanceof RemoveUser) {
					String filename = ((RemoveUser)obj).getFilename();
					String remove = ((RemoveUser)obj).getRemoved();
					String owner = ((RemoveUser)obj).getOwner();
					String absolute = "SavedFiles/" + owner + "/" + filename;
					Connection conn = null;
					try {
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
						String query = "DELETE FROM FileShare WHERE Filepath = ? AND Shareduser = ?";
						PreparedStatement st = conn.prepareStatement(query);
						st.setString(1, absolute);
						st.setString(2, remove);
						System.out.println("Removing user: " + remove + "from file: " + absolute);
						st.execute();
					} catch (SQLException sqle){
						System.out.println("sqle: " + sqle.getMessage());
					} catch(ClassNotFoundException cnfe) {
						System.out.println("cnfe: " + cnfe.getMessage());
					} finally {
						try {
							if (conn != null) {
								conn.close();
							} 
						} catch (SQLException sqle) {
								System.out.println("sqle clsoing conn: " + sqle.getMessage());
							}
					}
					//send remove message to correct serverthread
					ServerThread removedUserThread = sl.findThread(remove);
					removedUserThread.sendRemoveUser(new RemoveUser(remove, owner, filename));
				} else if (obj instanceof OpenShared) {
					String owner = ((OpenShared) obj).getOwner();
					String username = ((OpenShared)obj).getUsername();
					//look at owner
					//get all files he owns and for each, check if its shared with username
					//if it is, send filepath (TOPARSE) along with owner name
					//jdbc
					Connection conn = null;
					Vector<File> files= new Vector<File>();
					try {
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
						String query = "SELECT Filepath FROM Files WHERE Owner = ?";
						PreparedStatement ps = conn.prepareStatement(query);
						ps.setString(1, owner);
						ResultSet rs; 
						rs = ps.executeQuery();
						
						while (rs.next()) {
							String filepath = rs.getString("Filepath");
							String sharedquery = "SELECT Filepath FROM FileShare WHERE Filepath = ? AND Shareduser = ?";
							PreparedStatement sharedps = conn.prepareStatement(sharedquery);
							sharedps.setString(1, filepath);
							sharedps.setString(2, username);
							ResultSet sharedrs; 
							sharedrs = sharedps.executeQuery();
							while(sharedrs.next()) {
								String f = sharedrs.getString("Filepath");
								//instead add the actual file
								File temp = new File(f);
								files.add(temp);
							}
						}
					} catch (SQLException sqle){
						System.out.println("sqle: " + sqle.getMessage());
					} catch(ClassNotFoundException cnfe) {
						System.out.println("cnfe: " + cnfe.getMessage());
					} finally {
						try {
							if (conn != null) {
								conn.close();
							} 
						} catch (SQLException sqle) {
								System.out.println("sqle clsoing conn: " + sqle.getMessage());
							}
					}
						
					sendSharedFiles(new SharedFiles(owner, files));
					
				} else if (obj instanceof UpdateObject) {
					//find the merge in mergmap
					//update the vectors in mergemap
					String filename = ((UpdateObject)obj).getFilename();
					String owner = ((UpdateObject)obj).getOwner();
					String filepath = "SavedFiles/" + owner + "/" + filename;
					String username = ((UpdateObject)obj).getUsername();
					String text = ((UpdateObject)obj).getText();
					sl.prepareMerger(filepath, text, username);
				} else if (obj instanceof MergeMapInfo) {
					String file = ((MergeMapInfo)obj).getFilename();
					String owner = ((MergeMapInfo)obj).getOwner();
					sl.appendMergeMap("SavedFiles/" + owner + "/" + file, ((MergeMapInfo)obj).getText(), owner, file);
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch(IOException ioe) {
			System.out.println("Client disconnected");
			//ioe.printStackTrace();
			TEditServerGUI.updateDatabase();
		} finally {
			sl.removeServerThread(this);
		}
	}
	
}
