package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.undo.CannotUndoException;


public class TextEditor extends JFrame{
	private JMenuBar menubar;
	private JMenu file;
	private JMenu edit;
	private JMenu spellcheck;
	private static JMenu users;
	private static JTabbedPane tabbedPane;
	private JMenuItem fileNew;
	private JMenuItem fileOpen;
	private JMenuItem fileSave;
	private JMenuItem fileClose;
	private JMenuItem editCut;
	private JMenuItem editCopy;
	private JMenuItem editPaste;
	private JMenuItem editSelectAll;
	private JMenuItem spellcheckRun;
	private JMenuItem spellcheckConfigure;
	private JMenuItem usersAdd;
	private JMenuItem usersRemove;
	private JFileChooser openfileChooser;
	private JFileChooser savefileChooser;
	private int platformAccel;
	private File wordlist;
	private File keyboard;
	private JPanel configureBar;
	private JLabel frameLabel;
	private boolean isMac;
	private static boolean isOnline;
	private static TextEditor TEditFrame;
	private static ClientThread ct;
	private static String username;
	
	public TextEditor(boolean isOnline, ClientThread ct, String username) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super("T~Edit");
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			
		} catch (Exception e) {
				System.out.println("Warning! Cross-platform L&F not used!");
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font myfont;
		try {
			myfont = Font.createFont(NORMAL, new File("Assignment3Resources/fonts/kenvector_future_thin.ttf"));
			myfont = myfont.deriveFont(NORMAL, 12f);
			ge.registerFont(myfont);
			FontUIResource f = new FontUIResource(myfont);
			UIManager.put("Button.font", f);
			UIManager.put("ComboBox.font", f);
			UIManager.put("Label.font", f);
			UIManager.put("MenuBar.font", f);
			UIManager.put("MenuItem.font", f);
			UIManager.put("Panel.font", f);
			UIManager.put("ScrollPane.font", f);
			UIManager.put("TabbedPane.font", f);
			UIManager.put("TextArea.font", f);
			UIManager.put("Menu.font", f);
			UIManager.put("TitledBorder.font", f);
			UIManager.put("MenuItem.acceleratorFont", f);
			UIManager.put("TabbedPane.selected", Color.yellow);
			UIManager.put("TabbedPane.borderColor", Color.orange);
			UIManager.put("TabbedPane.borderHightlightColor", Color.orange);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("windows")) {
			platformAccel = ActionEvent.CTRL_MASK;
			isMac = false;
		}
		else {
			platformAccel = ActionEvent.META_MASK;
			isMac = true;
			
		}
		TEditFrame = this;
		TextEditor.isOnline = isOnline;
		TextEditor.ct = ct;
		this.username = username;
		instantiateComponents();
		createGUI();
		addActions();
		setVisible(true);
	}
	public void instantiateComponents(){
		menubar = new JMenuBar() {	
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				BufferedImage background;
				try {
					background = ImageIO.read(new File("Assignment3Resources/img/backgrounds/red_panel.png"));
					g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		file = new JMenu("File");
		edit = new JMenu("Edit");
		spellcheck = new JMenu("Spellcheck");
		users = new JMenu("Users");
		fileNew = new JMenuItem("New", 'n');
		fileNew.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/new.png"));
		fileOpen = new JMenuItem("Open", 'o');
		fileOpen.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/open.png"));
		fileSave = new JMenuItem("Save", 's');
		fileSave.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/save.png"));
		fileClose = new JMenuItem("Close", 'c');
		fileClose.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/close.png"));
		editCut = new JMenuItem("Cut", 'c');
		editCut.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/cut.png"));
		editCopy = new JMenuItem("Copy", 'c');
		editCopy.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/copy.png"));
		editPaste = new JMenuItem("Paste", 'p');
		editPaste.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/paste.png"));
		editSelectAll = new JMenuItem("Select All");
		editSelectAll.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/select.png"));
		spellcheckRun = new JMenuItem("Run", 'r');
		spellcheckRun.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/run.png"));
		spellcheckConfigure = new JMenuItem("Configure", 'c');
		spellcheckConfigure.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/configure.png"));
		usersAdd = new JMenuItem("Add", 'a');
		usersRemove = new JMenuItem("Remove", 'r');
		tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(Color.orange);
		tabbedPane.setForeground(Color.DARK_GRAY);
		wordlist = new File("wordlist.wl");
		keyboard = new File("qwerty-us.kb");
		frameLabel = new JLabel("T~Edit", SwingConstants.CENTER);
		Font labelfont;
		try {
			labelfont = Font.createFont(NORMAL, new File("Assignment3Resources/fonts/kenvector_future_thin.ttf"));
			labelfont = labelfont.deriveFont(NORMAL, 30f);
			frameLabel.setFont(labelfont);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		frameLabel.setForeground(Color.red);
		
		ChangeListener tabchangeListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent changeEvent) {
		        JTabbedPane changedtab = (JTabbedPane) changeEvent.getSource();
		        int index = changedtab.getSelectedIndex();
		        if (index >= 0) {
			        TPane tempPane = (TPane) tabbedPane.getComponentAt(index);
			        if (tempPane.getOwner() == true && TextEditor.isOnline == true) {
		                users.setVisible(true);
			        } else {
			        	users.setVisible(false);
			        }
		        }
		     }
		 };
		 tabbedPane.addChangeListener(tabchangeListener);

	}
	public void createGUI() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		setSize(600,500);
		setLocation(100,100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.ORANGE);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage cursorimg;
		try {
			cursorimg = ImageIO.read(new File("Assignment3Resources/img/icon/cursor.png"));
			Cursor mycursor = toolkit.createCustomCursor(cursorimg , new Point(0, 0), "img");
			setCursor(mycursor);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		add(frameLabel);
		menubar.add(file);
		menubar.add(edit);
		menubar.add(spellcheck);
		menubar.add(users);
		edit.setVisible(false);
		spellcheck.setVisible(false);
		users.setVisible(false);
		file.setMnemonic('f');
		edit.setMnemonic('e');
		spellcheck.setMnemonic('s');
		users.setMnemonic('f');
		file.add(fileNew);
		file.add(fileOpen);
		file.add(fileSave);
		file.add(fileClose);
		edit.add(editCut);
		edit.add(editCopy);
		edit.add(editPaste);
		edit.addSeparator();
		edit.add(editSelectAll);
		spellcheck.add(spellcheckRun);
		spellcheck.add(spellcheckConfigure);	
		users.add(usersAdd);
		users.add(usersRemove);
		
		fileNew.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_N,
				platformAccel));
		fileOpen.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O,
				platformAccel));
		fileSave.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S,
				platformAccel));
		editCut.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_X,
				platformAccel));
		editCopy.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_C,
				platformAccel));
		editPaste.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_V,
				platformAccel));
		editSelectAll.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_A,
				platformAccel));
		spellcheckRun.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_F7,
				0));
		usersAdd.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_F8,
				0));
		usersRemove.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_F9,
				0));
		
		setJMenuBar(menubar);
		if (isMac == false) {
			try {
			    setIconImage(ImageIO.read(new File("Assignment3Resources/img/icon/office.png")));
			}
			catch (IOException e) {
			    e.printStackTrace();
			}
		}
		else {
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
			Method getApplicationMethod = applicationClass.getMethod("getApplication");
			Method setDockIconMethod = applicationClass.getMethod("setDockIconImage", java.awt.Image.class);
			Object macOSXApplication = getApplicationMethod.invoke(null);
			try {
				setDockIconMethod.invoke(macOSXApplication, ImageIO.read(new File("Assignment3Resources/img/icon/office.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void addActions() {
		//action listener for New
		fileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (tabbedPane.getTabCount() == 0) {
					edit.setVisible(true);
					spellcheck.setVisible(true);
					frameLabel.setVisible(false);
				}
				add(tabbedPane);
				TPane tpane = new TPane(false, username, false);
				tabbedPane.addTab("untitled", tpane);
			}
		});
		//action listener for Open
		fileOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {	
					boolean offline = true;
					if (isOnline == true) {
						Object [] options = {"Online", "Offline"};
						int value = JOptionPane.showOptionDialog(TextEditor.this, "Where would you like to open the file?",
						"Save...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options, options[1]);
						if (value == 0) {
							ct.sendString("@" + username);
							offline = false;
						}
					}
					if (offline == true) {
					openfileChooser = new JFileChooser();
					openfileChooser.setDialogTitle("Open File...");
					FileFilter filter = new FileNameExtensionFilter("txt files (*.txt)", "txt");
					openfileChooser.setFileFilter(filter);
		            openfileChooser.setAcceptAllFileFilterUsed(false);
					openfileChooser.setSelectedFile(null);
		            openfileChooser.setCurrentDirectory(null);
					int returnVal = openfileChooser.showOpenDialog(menubar);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				        File file = openfileChooser.getSelectedFile();
				        try {
							if (tabbedPane.getTabCount() == 0) {
								edit.setVisible(true);
								spellcheck.setVisible(true);
								frameLabel.setVisible(false);
							}
				        	add(tabbedPane);
				        	TPane tpane = new TPane(file, false, username, false);
				        	tpane.getTextArea().readFromFile(file);
				        	tabbedPane.addTab(file.getName(), tpane);
				        } catch (Exception ex) {
				          System.out.println("Cannot access file: "+ file.getAbsolutePath());
				        }
				    } else {
				        System.out.println("File access cancelled.");
				    }
					}
				}
		});
		//action listener for Save
		fileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean offline = true;
				if (tabbedPane.getTabCount() != 0)
				{
					if (isOnline == true) {
						Object [] options = {"Online", "Offline"};
						int value = JOptionPane.showOptionDialog(TextEditor.this, "Where would you like to save the file?",
						"Save...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options, options[1]);
						if (value == 0) {
							ct.sendString("#" + username);
							offline = false;
						}
					}
					if (offline == true) {
						savefileChooser = new JFileChooser();
						savefileChooser.setDialogTitle("Save As...");
						FileFilter filter = new FileNameExtensionFilter("txt files (*.txt)", "txt");
						savefileChooser.setFileFilter(filter);
						savefileChooser.setAcceptAllFileFilterUsed(false);
			            int index = tabbedPane.getSelectedIndex();
			            String tabname = tabbedPane.getTitleAt(index);
			            TPane tpane = (TPane) tabbedPane.getComponentAt(index);
						TTextArea textToSave = tpane.getTextArea();
			            if (tabname != "untitled" && tpane.getFile() != null) {
			            	File fileToSave = tpane.getFile();
			            	savefileChooser.setCurrentDirectory(fileToSave);
			            	savefileChooser.setSelectedFile(fileToSave);
			            }
			            else {
			            	savefileChooser.setCurrentDirectory(null);
			            	savefileChooser.setSelectedFile(null);
			            }
			            int returnVal = savefileChooser.showSaveDialog(menubar);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File chosenFile = savefileChooser.getSelectedFile();
							if (verifyOutput(chosenFile)) {
								fileSaver(chosenFile, textToSave);
								tpane.setFile(chosenFile);
								tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), chosenFile.getName());
								tpane.setOwner(false);
								tpane.setOwnerName(username);
								tpane.setOnline(false);
							}
							else {
								savefileChooser.cancelSelection();
							}
			            }
					}
	            }
			}
		});
		
		fileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int index = tabbedPane.getSelectedIndex();
				if (index != -1) {
					tabbedPane.remove(index);
					if (tabbedPane.getTabCount() == 0) {
						edit.setVisible(false);
						spellcheck.setVisible(false);
						users.setVisible(false);
						frameLabel.setVisible(true);
					}
				}
			}
		});
		
		
		editCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
				TPane tpane = (TPane) tabbedPane.getComponentAt(index);
				TTextArea tta = tpane.getTextArea();
				tta.copy();
			}
		});
		
		editCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
				TPane tpane = (TPane) tabbedPane.getComponentAt(index);
				TTextArea tta = tpane.getTextArea();
				tta.cut();
			}
		});
		
		editPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
				TPane tpane = (TPane) tabbedPane.getComponentAt(index);
				TTextArea tta = tpane.getTextArea();
				tta.paste();
			}
		});
		
		
		editSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
				TPane tpane = (TPane) tabbedPane.getComponentAt(index);
				TTextArea tta = tpane.getTextArea();
				tta.selectAll();
			}
		});
		
		spellcheckRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
				TPane tpane = (TPane) tabbedPane.getComponentAt(index);
				try {
					tpane.runSpellcheck(wordlist, keyboard);
				} catch (IOException e1) {
					System.out.println("Error running spellcheck");
				}
			}
		});
		
		spellcheckConfigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configure();
			}
		});
		usersAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String addedUser = JOptionPane.showInputDialog(TextEditor.this, 
						"Add User:",
						"Add user to file",
						JOptionPane.QUESTION_MESSAGE);
				//add user to sql for this file owned by creator
				//check if that user exists in database
				//if they do, add them to be able to access file
				if (addedUser != null) {
					int index = tabbedPane.getSelectedIndex();
		            String tabname = tabbedPane.getTitleAt(index);
					ct.sendAddCheck(new AddCheck(username, addedUser,tabname));
				}
			}
		});
		usersRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
	            String tabname = tabbedPane.getTitleAt(index);
				ct.sendRemoveCheck(new RemoveCheck(username, tabname));
				
			}
		});
		
		
	}
	
	public void configure() {
		
		if (configureBar != null) {
			configureBar.setVisible(false);
		}
		int index = tabbedPane.getSelectedIndex();
		TPane tpane = (TPane) tabbedPane.getComponentAt(index);
		if (tpane.getSpellToolbar() != null) {
			tpane.getSpellToolbar().setVisible(false);
		}
		configureBar = new JPanel();
		configureBar.setBackground(Color.gray);
		configureBar.setLayout(new BorderLayout());
		JLabel wordlistLabel = new JLabel(wordlist.getName());
		JLabel keyboardLabel = new JLabel(keyboard.getName());
		TButton changeWordlist = new TButton("Select Wordlist...");
		TButton changeKeyboard = new TButton("Select Keyboard...");
		TButton closeButton = new TButton("Close");
		JPanel buttonBox = new JPanel();
		buttonBox.setBackground(Color.gray);
		buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
		buttonBox.add(wordlistLabel);
		buttonBox.add(changeWordlist);
		buttonBox.add(Box.createRigidArea(new Dimension(0,10)));
		buttonBox.add(keyboardLabel);
		buttonBox.add(changeKeyboard);
		buttonBox.add(Box.createVerticalGlue());
		buttonBox.add(closeButton);
		buttonBox.setBorder(new TitledBorder("Configure"));
		configureBar.add(buttonBox, BorderLayout.CENTER);
		tpane.add(configureBar, BorderLayout.EAST);
		changeWordlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select File...");
				FileFilter filter = new FileNameExtensionFilter("wl files (*.wl)", "wl");
				fileChooser.setFileFilter(filter);
		        fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setSelectedFile(null);
		        fileChooser.setCurrentDirectory(null);
				int returnVal = fileChooser.showOpenDialog(configureBar);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			        File file = fileChooser.getSelectedFile();
			         wordlist = file;
			         wordlistLabel.setText(file.getName());
			    }
			    else {
			        System.out.println("File access cancelled.");
			    }
			}
		});
		changeKeyboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select File...");
				FileFilter filter = new FileNameExtensionFilter("kb files (*.kb)", "kb");
				fileChooser.setFileFilter(filter);
		        fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setSelectedFile(null);
		        fileChooser.setCurrentDirectory(null);
				int returnVal = fileChooser.showOpenDialog(configureBar);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			        File file = fileChooser.getSelectedFile();
			         keyboard = file;
			         keyboardLabel.setText(file.getName());
			    }
			    else {
			        System.out.println("File access cancelled.");
			    }
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				configureBar.setVisible(false);
			}
		});
	}
	
	
	private boolean verifyOutput(File filename) {
		boolean isVerified = false;
		if (filename.exists()) {
			int input = JOptionPane.showConfirmDialog(menubar, filename.getName() + " already exists. \n Do you want to replace it?", "Confirm Save As", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
			if (input == JOptionPane.YES_OPTION) {
				isVerified = true;
			}
			else if(input == JOptionPane.NO_OPTION) {
				isVerified = false;
			}
		}
		else {
			isVerified = true;
		}
		return isVerified;
	}
	
	private void fileSaver(File filename, TTextArea jta) {
		try {
			FileWriter fw = new FileWriter(filename.getAbsolutePath(), false);
			jta.write(fw);
			fw.close();
		} catch (IOException ex){
			System.out.println("Problem writing to file");
		}
	}
	
	public static void OpenFileSaver(File [] files) {
		TEditFrame.new FileSaver(files, ct);
	}
	
	public static void OpenFileOpener(File [] files) {
		TEditFrame.new FileOpener(files, ct);
	}
	public static void OpenUserRemover(Vector<String> users) {
		//open the user remover
		TEditFrame.new UserRemover(users, ct);
	}
	public static void OpenOpenChooser(Set<String> owners) {
		TEditFrame.new OpenChooser(owners, ct);
	}
	public static void OpenSharedChooser(String owner, Vector<File> files) {
		TEditFrame.new SharedChooser(owner, files, ct);
	}
	
	public static void setIsOnline(boolean bool) {
		isOnline = bool;
		System.out.println("Setting is online");
	}
	
	public static TextEditor getTEditFrame() {
		return TEditFrame;
	}
//	public static void passave() {
//		//iterate through open tabs
//		//save if they are online, save to ownerfilepath
//		int totalTabs = tabbedPane.getTabCount();
//		for (int i = 0 ; i < totalTabs; i++) {
//			TPane tpane = (TPane) tabbedPane.getComponentAt(i);
//			if (tpane.getOnline() == true) {
//				TTextArea textToSave = tpane.getTextArea();
//				String text = textToSave.getText();
//				String tabname = tabbedPane.getTitleAt(i);
//				ct.sendSaveData(new SaveObject(text, new File(tabname), tpane.getOwnerName(), true));
//				//System.out.println("Saving " + tabname + " to " + tpane.getOwnerName() + "'s file");
//			}
//		}
//	}
	public static void update()  {
		int totalTabs = tabbedPane.getTabCount();
		for (int i = 0 ; i < totalTabs; i++) {
			TPane tpane = (TPane) tabbedPane.getComponentAt(i);
			if (tpane.getOnline() == true) {
				TTextArea textToSave = tpane.getTextArea();
				String text = textToSave.getText();
				String tabname = tabbedPane.getTitleAt(i);
				String owner = tpane.getOwnerName();
				ct.sendUpdateData(new UpdateObject(text, tabname, owner, username));
			}
		}
		ct.sendTerminator("*");
	}
	public static void UpdateOnlineFiles(String text, String owner, String filename) {
		//find correct tpane and replace text
		int totalTabs = tabbedPane.getTabCount();
		for (int i = 0 ; i < totalTabs; i++) {
			TPane tpane = (TPane) tabbedPane.getComponentAt(i);
			if (tpane.getOwnerName().equals(owner) && tabbedPane.getTitleAt(i).equals(filename)) {
				tpane.getTextArea().setText(text);
				int length = tpane.getTextArea().getDocument().getLength();
				tpane.getTextArea().setCaretPosition(length);
			}
		}
	}
	public static void Removed(String filename, String owner) {
		JOptionPane.showMessageDialog(null, "You have been removed from " + filename + " by user " + owner, "Removed from file", JOptionPane.INFORMATION_MESSAGE);
		int totalTabs = tabbedPane.getTabCount();
		for (int i = 0 ; i < totalTabs; i++) {
			TPane tpane = (TPane) tabbedPane.getComponentAt(i);
			if (tpane.getOwnerName().equals(owner) && tabbedPane.getTitleAt(i).equals(filename)) {
				tpane.setOnline(false);
			}
		}
	}

	public static void getResponse(Boolean isSaved) {
		if (isSaved == true) {
			JOptionPane.showMessageDialog(TEditFrame, "File saved successfully" ,"Message", JOptionPane.INFORMATION_MESSAGE);
			//get jtabbed pane and set it to online
			users.setVisible(true);
			int index = tabbedPane.getSelectedIndex();
            TPane tpane = (TPane) tabbedPane.getComponentAt(index);
            tpane.setOwner(true);
            tpane.setOwnerName(username);
            tpane.setOnline(true);
		} else {
			JOptionPane.showMessageDialog(TEditFrame, "File failed to save." ,"Message", JOptionPane.ERROR_MESSAGE);
			isOnline = false;
		}
	}

	class UserRemover extends JDialog {
		private JPanel mainpanel;
		private JPanel listpanel;
		private Vector<String> users;
		private JList<String> userlist;
		private JLabel removeLabel;
		private JButton removeButton;
		private ClientThread ct;
		private String selectedUser;
		public UserRemover(Vector<String> users, ClientThread ct) {
			super(getTEditFrame(), "Remove User", true);
			this.users = users;
			this.ct = ct;
			instantiate();
			GUI();
			actions();
			setVisible(true);
		}
		public void instantiate() {
			setSize(200,300);
			setLocation(100, 100);
			mainpanel = new JPanel();
			listpanel = new JPanel();
			removeLabel = new JLabel("Choose a user:");
			removeButton = new JButton("Remove");
		}
		public void GUI() {
			mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
			mainpanel.add(removeLabel);
			mainpanel.add(Box.createVerticalGlue());
			listpanel.setLayout(new BorderLayout());
			userlist = new JList<String>(users);
			userlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			userlist.setLayoutOrientation(JList.VERTICAL_WRAP);
			userlist.setVisibleRowCount(-1);
			userlist.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					selectedUser = (userlist.getSelectedValue());
				}
			});
			JScrollPane listScroller = new JScrollPane(userlist);
			listpanel.add(listScroller, BorderLayout.CENTER);
			mainpanel.add(listpanel);
			mainpanel.add(removeButton, BorderLayout.SOUTH);
			add(mainpanel);
		}
		public void actions() {
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					//remove selected user from database
					//send username and filepath to server
					//delete from fileshare table
					//send message to serverthread for that username "removed"
					if (selectedUser != null) {
						int index = tabbedPane.getSelectedIndex();
			            String tabname = tabbedPane.getTitleAt(index);
						ct.sendRemoveUser(new RemoveUser(selectedUser, username, tabname));
						dispose();
					}
					
				}
			});
		}
	}
	
	class OpenChooser extends JDialog {
		private JPanel mainpanel;
		private JPanel listpanel;
		private Set<String> owners;
		private JList<String> ownerlist;
		private JLabel chooseLabel;
		private JButton myfilesButton;
		private JButton selectuserButton;
		private ClientThread ct;
		private String selectedUser;
		private JPanel buttonPanel;
		public OpenChooser(Set<String> owners, ClientThread ct) {
			super(getTEditFrame(), null, true);
			this.owners = owners;
			this.ct = ct;
			instantiate();
			GUI();
			actions();
			setVisible(true);
		}
		public void instantiate() {
			setSize(300,300);
			setLocation(100, 100);
			mainpanel = new JPanel();
			listpanel = new JPanel();
			chooseLabel = new JLabel("Choose a user:");
			myfilesButton = new JButton("My files...");
			selectuserButton = new JButton("Select user...");
			buttonPanel = new JPanel();
		}
		public void GUI() {
			mainpanel.setLayout(new BorderLayout());
			mainpanel.add(chooseLabel, BorderLayout.NORTH);
			mainpanel.add(Box.createVerticalGlue());
			listpanel.setLayout(new BorderLayout());
			Vector<String> ownerVector = new Vector<String>();
			for (String o : owners) {
				ownerVector.add(o);
			}
			ownerlist = new JList<String>(ownerVector);
			ownerlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			ownerlist.setLayoutOrientation(JList.VERTICAL_WRAP);
			ownerlist.setVisibleRowCount(-1);
			ownerlist.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					selectedUser = (ownerlist.getSelectedValue());
				}
			});
			JScrollPane listScroller = new JScrollPane(ownerlist);
			listpanel.add(listScroller, BorderLayout.CENTER);
			mainpanel.add(listpanel, BorderLayout.CENTER);
			buttonPanel.setLayout(new GridLayout(1,2));
			buttonPanel.add(myfilesButton);
			buttonPanel.add(selectuserButton);
			mainpanel.add(buttonPanel, BorderLayout.SOUTH);
			add(mainpanel);
		}
		public void actions() {
			selectuserButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					//remove selected user from database
					//send username and filepath to server
					//delete from fileshare table
					//send message to serverthread for that username "removed"
					if (selectedUser != null) {
						ct.sendOpenShared(new OpenShared(selectedUser, username));
						dispose();
					}
				}
			});
			myfilesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					ct.sendString("$" + username);
					dispose();
				}
			});
		}
	}
	
	class SharedChooser extends JDialog {
		private JPanel mainpanel;
		private JPanel listpanel;
		private JList<String> filearea;
		private JTextField textfield;
		private JLabel filelabel;
		private JLabel selectlabel;
		private JButton cancelButton;
		private JButton openButton;
		private JPanel filetextpanel;
		private JPanel buttonpanel;
		private JPanel toppanel;
		private Vector<File> files;
		private ClientThread ct;
		private int selectedIndex;
		private String owner;
		public SharedChooser(String owner, Vector<File> files, ClientThread ct) {
			super(getTEditFrame(), "Shared File Chooser", true);
			this.files = files;
			this.ct = ct;
			this.owner = owner;
			instantiate();
			GUI();
			actions();
			setVisible(true);
		}
		void instantiate() {
			setSize(400,200);
			setLocation(100, 100);
			toppanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
			mainpanel = new JPanel(); 
			textfield = new JTextField(35);
			textfield.setEditable(false);
			filelabel = new JLabel("File:");
			filetextpanel = new JPanel();
			selectlabel = new JLabel("Choose a file:");
			cancelButton = new JButton("Cancel");
			openButton = new JButton("Open");
			listpanel = new JPanel();
			buttonpanel = new JPanel();
		}
		void GUI() {
			mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
			toppanel.add(selectlabel);
			mainpanel.add(toppanel);
			mainpanel.add(Box.createVerticalGlue());
			listpanel.setLayout(new BorderLayout());
			Vector<String> filenames = new Vector<String>();
			//add file names to list
			for (File f : files) {
				filenames.addElement(f.getName());
			}
			filearea = new JList<String>(filenames);
			filearea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			filearea.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			filearea.setVisibleRowCount(-1);
			filearea.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					textfield.setText(filearea.getSelectedValue());
					selectedIndex = filearea.getSelectedIndex();
				}
			});
			JScrollPane listScroller = new JScrollPane(filearea);
			listpanel.add(listScroller, BorderLayout.CENTER);
			mainpanel.add(listpanel);
			
			filetextpanel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			filetextpanel.add(filelabel);
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			//TTextArea textToSave = tpane.getTextArea();
			filetextpanel.add(textfield);
			mainpanel.add(filetextpanel);
			buttonpanel.add(Box.createVerticalGlue());
			buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
			buttonpanel.add(Box.createHorizontalGlue());
			buttonpanel.add(cancelButton);
			buttonpanel.add(openButton);
			mainpanel.add(buttonpanel);
			add(mainpanel);
		}
		void actions() {
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			openButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					File selectedfile = files.elementAt(selectedIndex);
					 try {
							if (tabbedPane.getTabCount() == 0) {
								edit.setVisible(true);
								spellcheck.setVisible(true);
								frameLabel.setVisible(false);
							}
				        	TEditFrame.add(tabbedPane);
				        	TPane tpane = new TPane(selectedfile, false, owner, true);
				        	tpane.getTextArea().readFromFile(selectedfile);
				        	tabbedPane.addTab(selectedfile.getName(), tpane);
				        	ct.sendMergeMapInfo(new MergeMapInfo(tpane.getTextArea().getText(), selectedfile.getName(), owner));
				        } catch (Exception ex) {
				          System.out.println("Cannot access file");
				        }
					dispose();
				}
			});
		}
}
	
	
	class FileSaver extends JDialog {
		private JPanel mainpanel;
		private JPanel listpanel;
		private JList<String> filearea;
		private JTextField textfield;
		private JLabel filelabel;
		private JLabel selectlabel;
		private JButton cancelButton;
		private JButton saveButton;
		private JPanel filetextpanel;
		private JPanel buttonpanel;
		private JPanel toppanel;
		private File[] files;
		private ClientThread ct;
		public FileSaver(File[] files, ClientThread ct) {
			super(getTEditFrame(), "File Chooser", true);
			this.files = files;
			this.ct = ct;
			instantiate();
			GUI();
			actions();
			setVisible(true);
		}
		void instantiate() {
			setSize(400,200);
			setLocation(100, 100);
			toppanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
			mainpanel = new JPanel(); 
			textfield = new JTextField(35);
			textfield.setEditable(true);
			filelabel = new JLabel("File:");
			filetextpanel = new JPanel();
			selectlabel = new JLabel("Select a file:");
			cancelButton = new JButton("Cancel");
			saveButton = new JButton("Save");
			listpanel = new JPanel();
			buttonpanel = new JPanel();
		}
		void GUI() {
			mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
			toppanel.add(selectlabel);
			mainpanel.add(toppanel);
			mainpanel.add(Box.createVerticalGlue());
			listpanel.setLayout(new BorderLayout());
			Vector<String> filenames = new Vector<String>();
			//add file names to list
			for (File f : files) {
				filenames.addElement(f.getName());
			}
			filearea = new JList<String>(filenames);
			filearea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			filearea.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			filearea.setVisibleRowCount(-1);
			filearea.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					textfield.setText(filearea.getSelectedValue());
				}
			});
			JScrollPane listScroller = new JScrollPane(filearea);
			listpanel.add(listScroller, BorderLayout.CENTER);
			mainpanel.add(listpanel);
			
			filetextpanel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			filetextpanel.add(filelabel);
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			int index = tabbedPane.getSelectedIndex();
            String tabname = tabbedPane.getTitleAt(index);
            TPane tpane = (TPane) tabbedPane.getComponentAt(index);
			//TTextArea textToSave = tpane.getTextArea();
            if (tabname != "untitled") {
            	textfield.setText(tabname);
            }
            else {
            	textfield.setText("");
            }
			filetextpanel.add(textfield);
			mainpanel.add(filetextpanel);
			buttonpanel.add(Box.createVerticalGlue());
			buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
			buttonpanel.add(Box.createHorizontalGlue());
			buttonpanel.add(cancelButton);
			buttonpanel.add(saveButton);
			mainpanel.add(buttonpanel);
			add(mainpanel);
		}
		void actions() {
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
						int index = tabbedPane.getSelectedIndex();
			            TPane tpane = (TPane) tabbedPane.getComponentAt(index);
						TTextArea textToSave = tpane.getTextArea();
						String text = textToSave.getText();
						ct.sendSaveData(new SaveObject(text, new File(textfield.getText()), username, false));
						tabbedPane.setTitleAt(index, textfield.getText());
						dispose();
					}
			});
		}
}
	
	class FileOpener extends JDialog {
		private JPanel mainpanel;
		private JPanel listpanel;
		private JList<String> filearea;
		private JTextField textfield;
		private JLabel filelabel;
		private JLabel selectlabel;
		private JButton cancelButton;
		private JButton openButton;
		private JPanel filetextpanel;
		private JPanel buttonpanel;
		private JPanel toppanel;
		private File[] files;
		private ClientThread ct;
		private int selectedIndex;
		public FileOpener(File[] files, ClientThread ct) {
			super(getTEditFrame(), "File Chooser", true);
			this.files = files;
			this.ct = ct;
			instantiate();
			GUI();
			actions();
			setVisible(true);
		}
		void instantiate() {
			setSize(400,200);
			setLocation(100, 100);
			toppanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
			mainpanel = new JPanel(); 
			textfield = new JTextField(35);
			textfield.setEditable(false);
			filelabel = new JLabel("File:");
			filetextpanel = new JPanel();
			selectlabel = new JLabel("Select a file:");
			cancelButton = new JButton("Cancel");
			openButton = new JButton("Open");
			listpanel = new JPanel();
			buttonpanel = new JPanel();
		}
		void GUI() {
			mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
			toppanel.add(selectlabel);
			mainpanel.add(toppanel);
			mainpanel.add(Box.createVerticalGlue());
			listpanel.setLayout(new BorderLayout());
			Vector<String> filenames = new Vector<String>();
			//add file names to list
			for (File f : files) {
				filenames.addElement(f.getName());
			}
			filearea = new JList<String>(filenames);
			filearea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			filearea.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			filearea.setVisibleRowCount(-1);
			filearea.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					textfield.setText(filearea.getSelectedValue());
					selectedIndex = filearea.getSelectedIndex();
				}
			});
			JScrollPane listScroller = new JScrollPane(filearea);
			listpanel.add(listScroller, BorderLayout.CENTER);
			mainpanel.add(listpanel);
			
			filetextpanel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			filetextpanel.add(filelabel);
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			//TTextArea textToSave = tpane.getTextArea();
			filetextpanel.add(textfield);
			mainpanel.add(filetextpanel);
			buttonpanel.add(Box.createVerticalGlue());
			buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
			buttonpanel.add(Box.createHorizontalGlue());
			buttonpanel.add(cancelButton);
			buttonpanel.add(openButton);
			mainpanel.add(buttonpanel);
			add(mainpanel);
		}
		void actions() {
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			openButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					File selectedfile = files[selectedIndex];
					 try {
							if (tabbedPane.getTabCount() == 0) {
								edit.setVisible(true);
								spellcheck.setVisible(true);
								frameLabel.setVisible(false);
							}
				        	TEditFrame.add(tabbedPane);
				        	TPane tpane = new TPane(selectedfile, true, username, true);
				        	tpane.getTextArea().readFromFile(selectedfile);
				        	tabbedPane.addTab(selectedfile.getName(), tpane);
				        	ct.sendMergeMapInfo(new MergeMapInfo(tpane.getTextArea().getText(), selectedfile.getName(), username));
				        } catch (Exception ex) {
				          System.out.println("Cannot access file");
				        }
					dispose();
				}
			});
		}
}
}

class TButton extends JButton{
	private File img_un = new File("Assignment3Resources/img/menu/red_button11.png");
	private File img_sel = new File("Assignment3Resources/img/menu/red_button11_selected.png");
	
	public TButton(String text) {
		super(text);
		setRolloverEnabled(true);
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
	}
	@Override
	protected void paintComponent(Graphics g) {
		BufferedImage background;
		ButtonModel bm = this.getModel();
		if (bm.isRollover() == true) {
			try {
				background = ImageIO.read(img_un);
				g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				background = ImageIO.read(img_sel);
				g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		super.paintComponent(g);
	}
}
