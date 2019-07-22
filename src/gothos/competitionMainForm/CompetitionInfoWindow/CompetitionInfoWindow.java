package gothos.competitionMainForm.CompetitionInfoWindow;

import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompetitionInfoWindow {
	private JPanel  panel;
	private JButton backButton;
	private JTable  infoTable;

	protected CompetitionInfoWindowTableModel tableModel;

	public JPanel getPanel() {
		return panel;
	}

	public CompetitionInfoWindow() {

		tableModel = new CompetitionInfoWindowTableModel();
		infoTable.setModel(tableModel);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				WindowManager.disposeChildFrame();
			}
		});
	}
}
