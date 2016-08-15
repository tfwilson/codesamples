package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import client.TextEditor.FileSaver;
import server.MergedText;

public class ClientThread extends Thread implements Serializable{
	public static final long serialVersionUID = 1;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket s;
	public ClientThread(Socket s) {
		this.s = s;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe){
			System.out.println("ioe: " + ioe.getMessage());
		} 
	}
	public void sendAddCheck(AddCheck addInfo) {
		try {
			oos.writeObject(addInfo);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendRemoveCheck(RemoveCheck removeInfo) {
		try {
			oos.writeObject(removeInfo);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendRemoveUser(RemoveUser removeInfo) {
		try {
			oos.writeObject(removeInfo);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendOpenShared(OpenShared sharedRequest){
		try {
			oos.writeObject(sharedRequest);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendUpdateData(UpdateObject merge) {
		try {
			oos.writeObject(merge);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendTerminator(String t) {
		try {
			oos.writeObject(t);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendUserInfo(UserInfo info) {
		try {
			oos.writeObject(info);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMergeMapInfo(MergeMapInfo info) {
		try {
			oos.writeObject(info);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendSaveData(SaveObject data) {
		//System.out.println("Saving");
		try {
			oos.writeObject(data);
			oos.flush();
		} catch (IOException e) {
			System.out.println("Cannot connect to server: " + e.getMessage());
			JOptionPane.showMessageDialog(TextEditor.getTEditFrame(), "File failed to save" ,"Connection Failed", JOptionPane.ERROR_MESSAGE);
			//TextEditor.setIsOnline(false);
		}
	}
	
	public void sendString(String string) {
		try {
			oos.writeObject(string);
			oos.flush();
		} catch (IOException e) {
			System.out.println("Cannot connect to server: " + e.getMessage());
			JOptionPane.showMessageDialog(TextEditor.getTEditFrame(), "Connection to server failed. \n Continuing in offline mode" ,"Connection Failed", JOptionPane.ERROR_MESSAGE);
			TextEditor.setIsOnline(false);
		}
	}
	
	public void run() {
		try {
			while (true) {
				Object obj = ois.readObject();
				if (obj instanceof String) {
					if (obj.equals("free")) {
						//user info is for signup
						TEditSignUp.getResponse(false);
						//make sure GUI knows?
					} else if (obj.equals("taken")){
						TEditSignUp.getResponse(true);
					} else if (obj.equals("saved")){
						TextEditor.getResponse(true);
					} else if (obj.equals("failed")){
						TextEditor.getResponse(false);
					} else if (obj.equals("close")) {
						TextEditor.setIsOnline(false);
						s.close();
						JOptionPane.showMessageDialog(null, "Connection to server lost. \n Continuing in offline mode" ,"Message", JOptionPane.WARNING_MESSAGE);
					} else if (obj.equals("offline")) {
						JOptionPane.showMessageDialog(null, "You cannot share a file unless it is saved online", "Error", JOptionPane.ERROR_MESSAGE);
					} else if (obj.equals("ghost")) {
						JOptionPane.showMessageDialog(null, "That user cannot be found!", "Error", JOptionPane.ERROR_MESSAGE);
					} else if (obj.equals("shared")) {
						JOptionPane.showMessageDialog(null, "You have already shared this file with that user", "Information", JOptionPane.INFORMATION_MESSAGE);
					} 
//					else if (obj.equals("passave")) {
//						//call a function in texteditor that saves all online files to correct path
//						TextEditor.passave();
//					} 
					else if (obj.equals("update")) {
						//get all open files that are online
						TextEditor.update();
					}
				} else if (obj instanceof Boolean) {
					if ((Boolean) obj == true) {
						TEditLogin.getResponse(true);
					} else if ((Boolean) obj == false) {
						TEditLogin.getResponse(false);
					}
				} else if (obj instanceof FileObject) {
					if (((FileObject) obj).getIsSave() == true) {
						//open up save dialog
						
						TextEditor.OpenFileSaver(((FileObject) obj).getFiles());
					} else {
						TextEditor.OpenFileOpener(((FileObject) obj).getFiles());//open up open dialog
					}
 				} else if (obj instanceof Vector) {
 					TextEditor.OpenUserRemover((Vector<String>) obj);
 				} else if (obj instanceof RemoveUser) {
 					String owner = ((RemoveUser)obj).getOwner();
 					String filename = ((RemoveUser)obj).getFilename();
 					TextEditor.Removed(filename, owner);
 				} else if (obj instanceof Set) {
 					TextEditor.OpenOpenChooser((Set) obj);
 				} else if (obj instanceof SharedFiles) {
 					TextEditor.OpenSharedChooser(((SharedFiles)obj).getOwner(), ((SharedFiles)obj).getFilenames());
 				} else if (obj instanceof MergedText) {
 					TextEditor.UpdateOnlineFiles(((MergedText)obj).getText(),((MergedText)obj).getOwner(), ((MergedText)obj).getFilename());
 				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch(IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
			TextEditor.setIsOnline(false);
		}
	}
}
