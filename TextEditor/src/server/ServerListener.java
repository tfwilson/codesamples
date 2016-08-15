package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class ServerListener extends Thread {

	private ServerSocket ss;
	private int port;
	private Vector<ServerThread> serverThreads;
	private Vector<Socket> sockets;
	private Map<String, Merger> mergeMap;
	private int updatenum; 
	private int tcount;
	
	public ServerListener(ServerSocket ss, int port, int updatenum) {
		this.ss = ss;
		this.port = port;
		this.updatenum = updatenum;
		tcount = 0;
		serverThreads = new Vector<ServerThread>();
		sockets = new Vector<Socket>();
		mergeMap = new HashMap<String, Merger>();
		//initialize mergemap need owner, filename, filepath
//		Connection conn = null;
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM Files");
//			while (rs.next()) {
//				String filepath = rs.getString("Filepath");
//				String owner = rs.getString("Owner");
//				String filename = "";	
//				for (int i = 12 + owner.length(); i < filepath.length(); i++ ) {
//					filename += filepath.charAt(i);
//				}
//				System.out.println("Filename: " + filename);
//				mergeMap.put(filepath, new Merger("", owner, filename));
//			}
//		} catch (SQLException sqle){
//			System.out.println("sqle: " + sqle.getMessage());
//		} catch(ClassNotFoundException cnfe) {
//			System.out.println("cnfe: " + cnfe.getMessage());
//		} finally {
//			try {
//				if (conn != null) {
//					conn.close();
//				} 
//			} catch (SQLException sqle) {
//					System.out.println("sqle clsoing conn: " + sqle.getMessage());
//				}
//		}
		Timer savetimer = new Timer();
		savetimer.schedule(new SaveAllFiles(), 0 , 5000);
		Timer updatetimer = new Timer();
		updatetimer.schedule(new UpdateAllFiles(), 0, updatenum);
	}
	public void sendStringToAllClients(String message) {
		
		for (ServerThread st : serverThreads) {
			st.sendMessage(message);
		}
	}
	
	public void removeServerThread(ServerThread st) {
		serverThreads.remove(st);
	}
	public ServerThread findThread(String username) {
		for (ServerThread s : serverThreads) {
			System.out.println("User of serverthread: " + s.getUsername());
			if (s.getUsername().equals(username)){
				System.out.println("found");
				return s;
			}
		}
		return null;
	}
	public void appendMergeMap(String filepath, String text, String owner, String filename) {
		if (mergeMap.containsKey(filepath) == false) {
			mergeMap.put(filepath, new Merger(text, owner, filename));
			System.out.println("adding: " + filename + " owned by: " + owner);
		}
	}
	public synchronized void mergeAll() {
		//for every merger object, call merge on it
		if (mergeMap.isEmpty() == false) {
			for(Map.Entry<String, Merger> entry : mergeMap.entrySet()) {
				entry.getValue().merge(serverThreads);
			}
		}
		
	}
	public synchronized void prepareMerger(String filepath, String text, String user) {
		Merger temp = mergeMap.get(filepath);
		temp.addMerge(text);
		//so you know what to merge
		temp.addUser(user);
		//so you know who to send it to after
	}
	public synchronized void updateTerminatorCount() {
		tcount++;
		if (tcount == serverThreads.size()) {
			//merge all
			mergeAll();
			tcount = 0;
		}
	}
	public synchronized void saveFile(String filepath) {
		if (mergeMap.isEmpty() == false) {
			mergeMap.get(filepath).saveFile();
		}
	}
 	
	public void run() {
		Socket s = null;
		try {
			TEditServerGUI.displayMessage("Server started on Port: " + port);
			while (TEditServerGUI.getRunning() == true) {
				s = ss.accept();
				sockets.add(s);
				TEditServerGUI.displayMessage("Connection from: " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
			}
		} catch(IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("ioe closing ss: " + ioe.getMessage());
				}
			}
			for (Socket sock : sockets) {
				if (sock != null) {
					try {
						sock.close();
					} catch (IOException ioe) {
						System.out.println("ioe closing s: " + ioe.getMessage());
					}
				}
			}
		}
	}
	class SaveAllFiles extends TimerTask {
		public void run() {
			//needs to iterate through all mergers and call save on them
			//this will save all of files in their current state so long as they arent null
			if (mergeMap.isEmpty() == false) {
				for(Map.Entry<String, Merger> entry : mergeMap.entrySet()) {
					entry.getValue().saveFile();
				}
			}
			
		}
	}
	class UpdateAllFiles extends TimerTask {
		public void run() {
			sendStringToAllClients("update");
		}
	}
}

