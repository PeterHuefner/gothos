package gothos.competitionMainForm;

import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImportForm {
	private JButton backButton;
	private JButton chooseFileButton;
	private JTable table1;
	private JButton importButton;
	private JPanel panel;

	public ImportForm() {
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});
	}

	public void finalize(){
		System.out.println("finalize");
	}

	public JPanel getPanel(){
		return panel;
	}
}
