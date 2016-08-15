package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import tfwilson_201L_Assignment1.WordHelper;

public class TPane extends JPanel {
	private TTextArea _tta;
	private JScrollPane _jsp; 
	private File _file;
	private SpellCheckBar spellToolbar;
	private TScroll customScroll;
	private boolean isOwner;
	private String ownerName;
	private boolean isOnline;
	
	
	TPane(boolean isOwner, String ownerName, boolean isOnline) {
		setLayout(new BorderLayout());
		_tta = new TTextArea();
		_jsp = new JScrollPane(_tta);
		customScroll = new TScroll();
		_jsp.getVerticalScrollBar().setUI(customScroll);
		add(_jsp, BorderLayout.CENTER);
		this.isOwner = isOwner;
		this.ownerName = ownerName;
		this.isOnline = isOnline;
		_file = null;
	}
	TPane(File file, boolean isOwner, String ownerName, boolean isOnline) {
		setLayout(new BorderLayout());
		_tta = new TTextArea();
		_jsp = new JScrollPane(_tta);
		customScroll = new TScroll();
		_jsp.getVerticalScrollBar().setUI(customScroll);
		add(_jsp, BorderLayout.CENTER);
		_file = file;
		this.isOwner = isOwner;
		this.ownerName = ownerName;
		this.isOnline = isOnline;
	}
	
	public void runSpellcheck(File wordlist, File keyboard) throws IOException {
		if (spellToolbar != null) {
			spellToolbar.setVisible(false);
		}
		BorderLayout layout = (BorderLayout) this.getLayout();
		if (layout.getLayoutComponent(this, BorderLayout.EAST) != null) {
			layout.getLayoutComponent(this, BorderLayout.EAST).setVisible(false);
		}
		spellToolbar = new SpellCheckBar(wordlist.getAbsolutePath(), keyboard.getAbsolutePath());
		add(spellToolbar, BorderLayout.EAST);
	}
	
	public TTextArea getTextArea() {
		return _tta;
	}
	public File getFile() {
		return _file;
	}
	public void setFile(File file) {
		_file = file;
	}
	public SpellCheckBar getSpellToolbar() {
		return spellToolbar;
	}
	public boolean getOwner() {
		return isOwner;
	}
	public boolean getOnline() {
		return isOnline;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	class SpellCheckBar extends JPanel {
		private JLabel currwordLabel;
		private JComboBox<String> optionComboBox;
		private JButton ignoreButton;
		private TButton addButton;
		private TButton closeButton;
		private TButton changeButton;
		private String textword;
		private Integer start;
		private Integer end;
		private ArrayList<String> fixesArray;
		private WordHelper WordHelper;
		private String wordlistpath;
		private String keyboardpath;
		private Font myfont;
		
		SpellCheckBar(String wordlist, String keyboard) throws IOException { 
			wordlistpath = wordlist;
			keyboardpath = keyboard;
			initialize();
			createGUI();
			addActions();
			start = 0;
			end = 0;
			next();
		}
		public void initialize() throws IOException {
			WordHelper = new WordHelper(wordlistpath, keyboardpath);
			currwordLabel = new JLabel("Spelling: ");
			closeButton = new TButton("Close");
			ignoreButton = new TButton("Ignore");
			addButton = new TButton("Add");
			changeButton = new TButton("Change");
			optionComboBox = new JComboBox<String>();
			optionComboBox.setUI(TComboBoxUI.createUI(optionComboBox));
			fixesArray = new ArrayList<String>();
			try {
				myfont = Font.createFont(0 ,new File("Assignment3Resources/fonts/kenvector_future_thin.ttf"));
				myfont = myfont.deriveFont(0,  20f);
			} catch (FontFormatException e) {
				e.printStackTrace();
			}
		}
		public void createGUI() {
			setBackground(Color.gray);
			setLayout(new BorderLayout());			
			JPanel buttonBox = new JPanel();
			buttonBox.setBackground(Color.gray);
			buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
			JPanel labelpanel = new JPanel();
			labelpanel.setBackground(Color.gray);
			labelpanel.setLayout(new BorderLayout());
			currwordLabel.setFont(myfont);
			labelpanel.add(currwordLabel);
			buttonBox.add(labelpanel);
			buttonBox.add(Box.createRigidArea(new Dimension(0,10)));
			JPanel firstrow = new JPanel();
			firstrow.setBackground(Color.gray);
			firstrow.setLayout(new BoxLayout(firstrow, BoxLayout.LINE_AXIS));
			firstrow.add(Box.createGlue());
			firstrow.add(ignoreButton);
			firstrow.add(Box.createGlue());
			firstrow.add(addButton);
			firstrow.add(Box.createGlue());
			buttonBox.add(firstrow);
			buttonBox.add(Box.createRigidArea(new Dimension(0,10)));
			JPanel secondrow = new JPanel();
			secondrow.setBackground(Color.gray);
			secondrow.setLayout(new BoxLayout(secondrow, BoxLayout.LINE_AXIS));
			secondrow.add(Box.createGlue());
			secondrow.add(optionComboBox);
			secondrow.add(Box.createGlue());
			secondrow.add(changeButton);
			secondrow.add(Box.createGlue());
			buttonBox.add(secondrow);
			buttonBox.add(Box.createGlue());
			buttonBox.setBorder(new TitledBorder("Spell Check"));
			add(buttonBox, BorderLayout.NORTH);
			add(closeButton, BorderLayout.SOUTH);
		}
		public void addActions() {
			
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (textword.equals("") == false) {
						//add word to .wl file
						PrintWriter output;
						try {
							output = new PrintWriter(new FileWriter(wordlistpath, true));
							output.println();
							output.println(textword);
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						//add word to trie
						WordHelper.getTrie().add(textword);
					}
					//move on to next word if error exists
					next();
				}
			});
			ignoreButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					//move on to next word if error exists
					next();
				}
			});
			changeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					//change error word
					String solution = (String) optionComboBox.getSelectedItem();
					_tta.replaceRange(solution, start, end);
					end = start + solution.length();
					next();
					
					//move on to next word if error exists
				}
			});
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					closeToolbar();	
				}
			});
		}
		public void setWordHelper(String wordlist, String keyboard) throws IOException {
			WordHelper newhelper = new WordHelper(wordlist, keyboard);
			WordHelper = newhelper;
		}
		private boolean errorExists() {
			String ttaString = _tta.getText();
			getnextWord(ttaString);
			while (end < ttaString.length() && WordHelper.getTrie().contains(textword) == true){
				getnextWord(ttaString);
			}
			if (textword.equals("") == false && WordHelper.getTrie().contains(textword) == false) {
				return true;
			}
			return false;
		}
		private void getnextWord(String ttaString) {
			textword = "";
			start = end;
			while (start < ttaString.length() && Character.isLetter(ttaString.charAt(start)) == false) {
				start++;
			}
			end = start;
			while (end < ttaString.length() && ttaString.charAt(end) != ' ' && ttaString.charAt(end) != '\n') {
				if (Character.isLetter(ttaString.charAt(end))) {
					textword += ttaString.charAt(end);
				}
				end++;
			}
			textword = textword.toLowerCase();
		}
		
		private void findFixes() throws IOException {
			currwordLabel.setText("Spelling: " + textword);
			fixesArray = WordHelper.generateSolutions(textword);
			if (fixesArray.isEmpty() == false){
				for (int i = 0; i < fixesArray.size(); i++) {
					optionComboBox.addItem(fixesArray.get(i));
				}
			}
			
			//replace word based on start and end
		}
		private void closeToolbar() {
			//spellToolbar.removeAll();
			setVisible(false);
			fixesArray.clear();
			optionComboBox.removeAllItems();
			start = 0;
			end = 0;
			textword = "";
		}
		private void next() {
			optionComboBox.removeAllItems();
			fixesArray.clear();
			if (errorExists() == true) {
				try {
					findFixes();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				//close
				closeToolbar();
			}	
		}
	}

	
	class TScroll extends BasicScrollBarUI{	 
		@Override
		protected JButton createDecreaseButton(int orientation) {
			JButton upbutton = new JButton();
			upbutton.setOpaque(false);
			upbutton.setContentAreaFilled(false);
			upbutton.setBorderPainted(false);
			upbutton.setBorder(BorderFactory.createEmptyBorder());
		    upbutton.setIcon(new ImageIcon("Assignment3Resources/img/scrollbar/red_sliderUp.png"));
		    return upbutton;
		}
	 
		@Override
		protected JButton createIncreaseButton(int orientation) {
			JButton downbutton = new JButton();
			downbutton.setOpaque(false);
			downbutton.setContentAreaFilled(false);
			downbutton.setBorderPainted(false);
			downbutton.setBorder(BorderFactory.createEmptyBorder());
			downbutton.setIcon(new ImageIcon("Assignment3Resources/img/scrollbar/red_sliderDown.png"));
		    return downbutton;
		}
	 
	    @Override
	    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
	    	BufferedImage thumbimg;
			try {
				thumbimg = ImageIO.read(new File("Assignment3Resources/img/backgrounds/red_panel.png"));
				g.drawImage(thumbimg, (int) thumbBounds.getX(), (int) thumbBounds.getY(), (int) thumbBounds.getWidth(), (int) thumbBounds.getHeight(), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    @Override
	    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
	    	BufferedImage trackimg;
	    	try {
				trackimg = ImageIO.read(new File("Assignment3Resources/img/backgrounds/darkgrey_panel.png"));
				g.drawImage(trackimg, (int) trackBounds.getX(), (int) trackBounds.getY(), (int) trackBounds.getWidth(), (int) trackBounds.getHeight(), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
}
	class TComboBoxUI extends BasicComboBoxUI {
		public static ComboBoxUI createUI(JComponent jcomp) {
	        return new TComboBoxUI();
	    }
	    protected JButton createArrowButton() {
	    	
	    	JButton arrowbutton = new JButton() {
	    		public void paintComponent(Graphics g) {
	    			BufferedImage background = null;
						try {
							background = ImageIO.read(new File("Assignment3Resources/img/menu/red_sliderDown.png"));
						} catch (IOException e) {
							e.printStackTrace();
						}
						g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);
	    		}
	    	};
	    	arrowbutton.setOpaque(false);
			arrowbutton.setContentAreaFilled(false);
			arrowbutton.setBorderPainted(false);
			//arrowbutton.setBorder(BorderFactory.createEmptyBorder());
			//ImageIcon img = new ImageIcon("Assignment3Resources/img/menu/red_sliderDown.png");
			//arrowbutton.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
		    //arrowbutton.setIcon(img);
	    	arrowbutton.setBorder(BorderFactory.createEmptyBorder());
		    return arrowbutton;
	    }
	}
	




