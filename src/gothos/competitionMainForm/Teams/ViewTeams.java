package gothos.competitionMainForm.Teams;

import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewTeams {
	private JPanel  panel;
	private JButton backButton;
	private JTable  teamsTable;
	private JButton button2;
	private JButton button3;
	private JButton button4;
	private JButton button5;
	private JButton button6;
	private JButton button7;
	private JLabel  certificateLabel;
	private JLabel  protocolLabel;

	protected ViewTeamsTableModel tableModel;

	public JPanel getPanel() {
		return panel;
	}

	public ViewTeams() {

		tableModel = new ViewTeamsTableModel();
		teamsTable.setModel(tableModel);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});
	}
}
