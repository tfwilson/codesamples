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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class TEditLauncher extends JFrame{
	private TButton loginButton;
	private TButton signupButton;
	private TButton offlineButton;
	private JLabel titleLabel;
	private JPanel jp;
	private JPanel titlepanel;
	private JPanel main;
	private String hostname;
	private int port;
	
	
	public TEditLauncher(String hostname, int port) {
		super("T~Edit");
		this.hostname = hostname;
		this.port = port;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font myfont;
		try {
			myfont = Font.createFont(NORMAL, new File("Assignment3Resources/fonts/kenvector_future_thin.ttf"));
			myfont = myfont.deriveFont(NORMAL, 12f);
			ge.registerFont(myfont);
			FontUIResource f = new FontUIResource(myfont);
			UIManager.put("Button.font", f);
			UIManager.put("Label.font", f);
			UIManager.put("TextField.font", f);
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
		signupButton = new TButton("SIGNUP");
		offlineButton = new TButton("OFFLINE");
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
		jp.add(loginButton, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		jp.add(signupButton, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		jp.add(offlineButton, gbc);
		JPanel fuck = new JPanel();
		JPanel you = new JPanel();
		fuck.setBackground(Color.ORANGE);
		you.setBackground(Color.ORANGE);
		main.add(fuck);
		main.add(titlepanel);
		main.add(jp);
		main.add(you);
	}
	public void addActions() {
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new TEditLogin(hostname, port);
			}
		});
		signupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new TEditSignUp(hostname, port);
			}
		});
		offlineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				try {
					new TextEditor(false, null, null);
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
			}
		});
		
	}
}


