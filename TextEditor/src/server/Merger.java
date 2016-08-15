package server;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

import client.SaveObject;
import server.diff_match_patch.Patch;

public class Merger {
	private Vector<String> merges;
	private Vector<String> users;
	private String owner;
	private String filename;
	private String currentState;
	//private boolean fromSQL;
	public Merger(String currentState, String owner, String filename) {
		this.currentState = currentState;
		this.owner = owner;
		this.filename = filename;
		//this.fromSQL = fromSQL;
		merges = new Vector<String>();
		users = new Vector<String>();
		
	}
	public synchronized void addMerge(String merge) {
		//this.fromSQL = false;
		merges.add(merge);
	}
	public void addUser(String user) {
		users.add(user);
	}
	public synchronized void merge(Vector<ServerThread> serverThreads) {
		//iterate through merges and merge with initialState
		//update currentstate each time
		diff_match_patch dmp = new diff_match_patch();
		String newState = currentState;
		for (String merge : merges) {
			LinkedList<Patch> patch = dmp.patch_make(currentState, merge);
			newState = (String)(dmp.patch_apply(patch, newState)[0]);
		}
		currentState = newState;
		for (ServerThread st : serverThreads) {
			if (users.contains(st.getUsername())) {
				System.out.println("Sending merge to user: " + st.getUsername());
				st.sendUpdate(new MergedText(currentState, filename, owner));
			}
		}
		//send this new state to all users that have this file open
		//reset merges and users
		merges.clear();
		users.clear();
	}
	public synchronized void saveFile() {
		//if (fromSQL == false) {
			JTextArea jta = new JTextArea();
			jta.setText(currentState);
			FileWriter fw;
			try {
				fw = new FileWriter("SavedFiles/" + owner + "/" + filename, false);
				jta.write(fw);
				TEditServerGUI.displayMessage("File saved User: " + owner + " File: " + filename);
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
	}
}
