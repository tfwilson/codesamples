package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TEditClient {
	
	private String hostname;
	private int port;
	
	public TEditClient() {
		getHostandPort();
		new TEditLauncher(hostname, port);
	}
	
	public void getHostandPort() {
		try { //create map of char to array of char for keyboard
			FileReader fr = new FileReader("ClientConfig.txt");
			BufferedReader br = new BufferedReader(fr);
			String full;
			String ip = "";
			full = br.readLine();
			for (int i = 12; i < full.length(); i++) {
				ip += full.charAt(i);
			}
			this.hostname = ip;
			full = br.readLine();
			String port = "";
			int portnumber;
			for (int i = 6; i < full.length(); i++) {
				port += full.charAt(i);
			}
			portnumber = Integer.parseInt(port);
			br.close();
			this.port = portnumber;
		} catch (FileNotFoundException fnfe){
			System.out.println("fnfe: " + fnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} 
	}
	
	public static void main(String [] args) {
		new TEditClient();
	}
}
