package gui;

import java.util.*;

import javax.swing.*;

import exceptions.ErrorDisplay;

public class PopupManager implements ErrorDisplay{
	
	private JFrame frame;
	private GUI gui;
	
	public PopupManager(JFrame frame, GUI gui) {
		this.frame = frame;
		this.gui = gui;
	}
	
	
	public void displayError(String message, String popupType) {
		int messageType;
		popupType.toLowerCase();
		switch(popupType)
		{
		case "error":
			messageType = JOptionPane.ERROR_MESSAGE;
			break;
		case "warning":
			messageType = JOptionPane.WARNING_MESSAGE;
			break;
		case "plain":
			messageType = JOptionPane.PLAIN_MESSAGE;
			break;
		default:
			messageType = JOptionPane.YES_OPTION;
		}
		
		JOptionPane.showMessageDialog(
			    frame,
			    message,
			    popupType,
			    messageType
			);
	}
	public void displayMalformPopup() {
		JOptionPane.showMessageDialog(
			    frame,
			    "File appears malformed or packed",
			    "Warning",
			    JOptionPane.WARNING_MESSAGE
			);
		
	}
	public void displayInvalidFilePopup() {
		JOptionPane.showMessageDialog(
			    frame,
			    "Loaded File is not a valid PE file",
			    "Error",
			    JOptionPane.ERROR_MESSAGE
			);
	}
	
	public void displayWelcomePopup() {
		JOptionPane.showMessageDialog(
			    frame,
			    "<html><center><b>Welcome to PEel!</b></center> <br>Please load a file to begin (File -> Import File)</html>",
			    "Hello!",
			    JOptionPane.PLAIN_MESSAGE
			);
	}
	
	public void displayNullErrorPopup() {
		JOptionPane.showMessageDialog(
			    frame,
			    "Null section in file header detected. Malformed/Broken PE file?",
			    "Error",
			    JOptionPane.ERROR_MESSAGE
			);
	}
}
