package client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class TTextArea extends JTextArea implements Serializable {
	//create text area with undomanager
	public static final long serialVersionUID = 1;
	
	public TTextArea() {
		this.setSelectionColor(Color.ORANGE);
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	
	public void readFromFile(File file) throws IOException {
		    BufferedReader buffer = new BufferedReader(new FileReader(file));
		    try {
		        StringBuilder builder = new StringBuilder();
		        String text = buffer.readLine();
		        while (text != null) {
		            builder.append(text);
		            builder.append("\n");
		            text = buffer.readLine();
		        }
		        this.append(builder.toString());
		    } 
		    finally {
		        buffer.close();
		    }
		}
	}
