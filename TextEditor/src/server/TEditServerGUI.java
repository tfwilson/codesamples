package server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import client.UserInfo;

public class TEditServerGUI extends JFrame {
	private static JTextArea jta;
	private JScrollPane jsp;
	private JButton startstop;
	private static boolean isRunning;
	private ServerSocket ss;
	private int port;
	private int updatenum;
	private static ServerListener serverListener;
	private static HashMap<String, UserInfo> UserMap;
	private static Vector<UserInfo> newUsers;
	
	TEditServerGUI() {
		super("Server");
		getPortFromConfig();
		instantiateComponents();
		createGUI();
		addActions();
		setVisible(true);
	}
	public void instantiateComponents() {
		jta = new JTextArea();
		jta.setEditable(false);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		jsp = new JScrollPane(jta);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		startstop = new JButton("Start");
		isRunning = false;
		UserMap = new HashMap<String, UserInfo>(); 
		newUsers = new Vector<UserInfo>();
	}

	public void createGUI() {	
		setSize(400,300);
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(jsp,BorderLayout.CENTER);
		add(startstop, BorderLayout.SOUTH);
	}
	
	public void addActions() {	
		startstop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isRunning == false) {
					try {
						ss = new ServerSocket(port);
					} catch (IOException e1) {
						System.out.println("ioe: " + e1.getMessage());
					}
					isRunning = true;
					startstop.setText("Stop");
					//GET DATA FROM DATABASE
					getDatafromDatabase();
					listenForConnections();
				}
				else if (isRunning == true) {
					stopListening();
					updateDatabase();
				}
			}
		});
	}
	
	private void listenForConnections() {
		serverListener = new ServerListener(ss, port, updatenum);
		serverListener.start();
	}
	private void stopListening() {
		try {
			isRunning = false;
			startstop.setText("Start");
			displayMessage("Server stopped.");
			serverListener.sendStringToAllClients("close");
			isRunning = false;
			ss.close();
			serverListener = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getPortFromConfig() {
		int portnumber;
		try { //create map of char to array of char for keyboard
			FileReader fr = new FileReader("ServerConfig.txt");
			BufferedReader br = new BufferedReader(fr);
			String full;
			String port = "";
			full = br.readLine();
			for (int i = 6; i < full.length(); i++) {
				port += full.charAt(i);
			}
			portnumber = Integer.parseInt(port);
			this.port = portnumber;
			full = br.readLine();
			String update = "";
			int updatenum;
			for (int i = 17; i < full.length(); i++) {
				update += full.charAt(i);
			}
			updatenum = Integer.parseInt(update);
			this.updatenum = updatenum;
			br.close();
		} catch (FileNotFoundException fnfe){
			System.out.println("fnfe: " + fnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} 
	}
	
	public static void displayMessage(String message) {
		if (jta.getText() != null && jta.getText().trim().length() > 0) {
			jta.append("\n" + message);
		}
		else {
			jta.setText(message);
		}
	}
	public static boolean getRunning() {
		return isRunning;
	}
	
	public void getDatafromDatabase() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM UserInfo");
			while (rs.next()) {
				String user = rs.getString("Username");
				String pass = rs.getString("Password");
				UserInfo temp = new UserInfo(user, pass, true);
				UserMap.put(user, temp);
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
	
	public static void updateDatabase(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/TEditDatabase2?user=root&password=brighton");
			String query = "INSERT INTO UserInfo ("
				    + "Username,"
				    + "Password ) VALUES ("
				    + "?, ?)";
			for (int i = 0 ; i < newUsers.size(); i++) {
				//PreparedStatement insert = conn.prepareStatement("INSERT INTO UserInfo" + "VALUES(?,?)");
				PreparedStatement insert = conn.prepareStatement(query);
				String user = newUsers.get(i).getUsername();
				String pass = newUsers.get(i).getPassword();
				insert.setString(1, user);
				insert.setString(2, pass);
				insert.executeUpdate();
				insert.close();
			}
			newUsers.clear();
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
	
	public static boolean hasUsername(String username) {
		if(UserMap.get(username) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void addUser(UserInfo user) {
		UserMap.put(user.getUsername(), user);
		newUsers.add(user);
	}
	
	public static boolean getValidity(UserInfo user) {
		UserInfo temp = UserMap.get(user.getUsername());
		if (temp != null) {
			if (temp.getPassword().equals(user.getPassword())) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String [] args) {
		new TEditServerGUI();
	}

}
