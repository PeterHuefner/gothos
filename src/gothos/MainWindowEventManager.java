package gothos;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class MainWindowEventManager implements java.awt.event.WindowListener{

	@Override
	public void windowOpened(WindowEvent windowEvent) {

	}

	@Override
	public void windowClosed(WindowEvent windowEvent) {

	}

	@Override
	public void windowActivated(WindowEvent windowEvent) {

	}

	@Override
	public void windowClosing(WindowEvent windowEvent) {
		//System.out.println(WindowManager.mainFrame.getContentPane() == WindowManager.startPanel);


		if (!Common.emptyString(Application.selectedCompetition)) {

			int selectedOption = JOptionPane.showConfirmDialog(WindowManager.mainFrame, "Wollen Sie die Anwendung wirklich schließen? Es ist noch ein Wettkampf ausgewählt.", "Gothos wirklich beenden?", JOptionPane.YES_NO_OPTION);

			if (selectedOption == 0) {
				Application.exitApplication();
			}
		} else if((WindowManager.childFrame != null && WindowManager.childFrame.isActive()) || WindowManager.mainFrame.getContentPane() != WindowManager.startPanel) {
			int selectedOption = JOptionPane.showConfirmDialog(WindowManager.mainFrame, "Wollen Sie die Anwendung wirklich schließen? Sie bearbeiten gerade noch Daten.", "Gothos wirklich beenden?", JOptionPane.YES_NO_OPTION);

			if (selectedOption == 0) {
				Application.exitApplication();
			}
		} else {
			Application.exitApplication();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent windowEvent) {

	}

	@Override
	public void windowDeiconified(WindowEvent windowEvent) {

	}

	@Override
	public void windowIconified(WindowEvent windowEvent) {

	}
}
