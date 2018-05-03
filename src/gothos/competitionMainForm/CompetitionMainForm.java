package gothos.competitionMainForm;

import gothos.Application;
import gothos.Common;
import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompetitionMainForm {
	private JButton closeCompetition;
	private JPanel panel;
	private JLabel competitionNameLabel;
	private JComboBox classSelect;
	private JButton openClass;
	private JButton printClassProtocol;
	private JButton printCertificate;
	private JComboBox squadSelect;
	private JButton openSquad;
	private JButton printSquad;
	private JComboBox teamSelect;
	private JButton openTeam;
	private JButton printTeamProtocol;
	private JButton printTeamCertificate;
	private JTable gymnastsTable;
	private JButton importButton;
	private JButton exportButton;
	private JButton exportAllButton;
	private JButton configureClassesButton;
	private JButton configureAppartiButton;
	private JButton addGymnast;
	private JButton removeGymnast;
	private JTextField searchField;
	private JButton createIdsButton;
	private JButton clearSearchButton;

	protected GymnastTableModel tableModel;

	public CompetitionMainForm() {

		if(Common.emptyString(Application.selectedCompetition)){
			JOptionPane.showMessageDialog(null, "Kein Wettkampf ausgewählt.");
			WindowManager.closeCompetition();
		}

		competitionNameLabel.setText("Wettkampf: " + Application.selectedCompetition);
		tableModel = new GymnastTableModel();
		gymnastsTable.setModel(tableModel);

		closeCompetition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.closeCompetition();
			}
		});

		addGymnast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int colIndex = tableModel.addEmptyRow();
				if(gymnastsTable.editCellAt(colIndex, 0)){
					gymnastsTable.setRowSelectionInterval(colIndex, colIndex);
					gymnastsTable.requestFocus();
					gymnastsTable.getEditorComponent().requestFocusInWindow();
				}
			}
		});

		removeGymnast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = gymnastsTable.getSelectedRow();
				if(selectedRow != -1){
					tableModel.deleteRow(selectedRow);
				}else{
					Common.showError("Selektieren Sie zunächst eine Altersklasse.");
				}
			}
		});
	}

	public JPanel getPanel(){
		return panel;
	}

	public void close(){

	}
}
