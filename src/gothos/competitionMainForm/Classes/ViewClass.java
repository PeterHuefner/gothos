package gothos.competitionMainForm.Classes;

import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewClass {
	private JButton backButton;
	private JTable  classTable;
	private JButton printProtocol;
	private JButton printCertificates;
	private JPanel  panel;

	protected String className;

	public JPanel getPanel() {
		return panel;
	}

	public ViewClass (String className) {
		this.className = className;





		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});
	}
}
