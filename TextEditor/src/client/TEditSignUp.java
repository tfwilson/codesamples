package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class TEditSignUp extends JFrame {
	private TButton loginButton;
	private JLabel username;
	private JLabel password;
	private JLabel repeat;
	private JLabel titleLabel;
	private JTextField usernamefield;
	private JPasswordField passwordfield;
	private JPasswordField repeatfield;
	private JPanel jp;
	private JPanel titlepanel;
	private JPanel main;
	private Socket s;
	private static ClientThread ct;
	private static JFrame thisframe;
	private String hostname;
	private int port;
	private static String user;
	
	public TEditSignUp(String hostname, int port) {
		super("T~Edit");
		thisframe = this;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font myfont;
		this.hostname = hostname;
		this.port = port;
		try {
			myfont = Font.createFont(NORMAL, new File("Assignment3Resources/fonts/kenvector_future_thin.ttf"));
			myfont = myfont.deriveFont(NORMAL, 12f);
			ge.registerFont(myfont);
			FontUIResource f = new FontUIResource(myfont);
			UIManager.put("Button.font", f);
			UIManager.put("Label.font", f);
			UIManager.put("TextField.font", f);
			UIManager.put("OptionPane.messageFont", f);
			UIManager.put("OptionPane.buttonFont", f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		instantiateComponents();
		createGUI();
		addActions();
		setVisible(true);
	}
	
	public void instantiateComponents() {
		loginButton = new TButton("LOGIN");
		username = new JLabel("Username: ");
		password = new JLabel("Password: ");
		repeat = new JLabel("Repeat: ");
		passwordfield = new JPasswordField(10);
		usernamefield = new JTextField(12);
		repeatfield = new JPasswordField(10);
		titleLabel = new JLabel("T~EDit");
		main = new JPanel();
		jp = new JPanel();
		titlepanel = new JPanel();
		Font labelfont;
		try {
			labelfont = Font.createFont(NORMAL, new File("Assignment3Resources/fonts/kenvector_future_thin.ttf"));
			labelfont = labelfont.deriveFont(NORMAL, 50f);
			titleLabel.setFont(labelfont);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		titleLabel.setForeground(Color.red);
	}
	public void createGUI() {
		setResizable(false);
		setSize(600,500);
		setLocation(100,100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setLayout(new GridLayout(4,1));
		add(main);
		titlepanel.setBackground(Color.ORANGE);
		titlepanel.add(titleLabel, SwingConstants.CENTER);
		jp.setBackground(Color.ORANGE);
		jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1,1,1,1);
		gbc.gridx = 0;
		gbc.gridy = 0;
		jp.add(username, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		jp.add(usernamefield, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		jp.add(password, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		jp.add(passwordfield, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		jp.add(repeat, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		jp.add(repeatfield, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		jp.add(loginButton, gbc);
		
		//jp.add(offlineButton, gbc);
		JPanel fuck = new JPanel();
		JPanel you = new JPanel();
		fuck.setBackground(Color.ORANGE);
		you.setBackground(Color.ORANGE);
		main.add(fuck);
		main.add(titlepanel);
		main.add(jp);
		main.add(you);
	}
	public static void getResponse(boolean isTaken) {
		if (isTaken == false) {
			try {
				new TextEditor(true, ct, user);
				thisframe.setVisible(false);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(thisframe, "That username is already taken", "Sign-up Failed", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public String encrypt(String s) {
		String e = "";
		for (int i = s.length()-1; i >= 0; i--) {
			e += s.charAt(i);
		}
		return e;
	}
	
	public void addActions() {
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernamefield.getText();
				user = username;
				char[] password = passwordfield.getPassword();
				char[] repeat = repeatfield.getPassword();
				boolean hasUpper = false;
				boolean hasNumber = false;
				boolean isDifferent = false;
				
				if (Arrays.equals(repeat, password) == false) {
					isDifferent = true;
				}
				for (int i = 0 ; i < password.length; i++) {
					Character ch = password[i];
					if (Character.isDigit(ch) == true) {
						hasNumber = true;
					}
					if (Character.isUpperCase(ch) == true) {
						hasUpper = true;
					}
				}
				//check if username is taken
				if (username.length() == 0) {
					JOptionPane.showMessageDialog(TEditSignUp.this, "Username is invalid" ,"Log-in Failed", JOptionPane.ERROR_MESSAGE);
				} else if (isDifferent == true) {
					JOptionPane.showMessageDialog(TEditSignUp.this, "Your passwords do not match" ,"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
				} else if(hasUpper == false && hasNumber == true) {
					JOptionPane.showMessageDialog(TEditSignUp.this, "Password must contain at least: \n 1-Uppercase Letter" ,"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
				} else if(hasUpper == false && hasNumber == false) {
					JOptionPane.showMessageDialog(TEditSignUp.this, "Password must contain at least: \n 1-Uppercase Letter and 1-Number" ,"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
				} else if(hasUpper == true && hasNumber == false) {
					JOptionPane.showMessageDialog(TEditSignUp.this, "Password must contain at least: \n 1-Number", "Sign-up Failed", JOptionPane.WARNING_MESSAGE);
				} else if (hasUpper == true && hasNumber == true) {
					String passwordstring = new String(password);
					passwordstring = encrypt(passwordstring);
					UserInfo ui = new UserInfo(username, passwordstring, false);
					Socket s = null;
					try {
						s = new Socket(hostname, port);
						ct = new ClientThread(s);
						ct.sendUserInfo(ui);
					} catch (UnknownHostException uhe) {
						System.out.println("uhe: " + uhe.getMessage());
					} catch (IOException ioe) {
						//create option pane
						JOptionPane.showMessageDialog(TEditSignUp.this, "Server cannot be reached. \n Program in offline mode.", "Sign-up Failed", JOptionPane.WARNING_MESSAGE);
						thisframe.setVisible(false);
						try {
							new TextEditor(false, null, null);
						} catch (ClassNotFoundException | NoSuchMethodException | SecurityException
								| IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
}
