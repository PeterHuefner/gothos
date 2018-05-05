package gothos.competitionMainForm;

import com.opencsv.CSVReader;
import gothos.Application;
import gothos.Common;
import gothos.FormCore.TableNavigator;
import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
	private JButton searchButton;

	protected GymnastTableModel tableModel;
	protected TableNavigator navigator;

	public CompetitionMainForm() {

		if(Common.emptyString(Application.selectedCompetition)){
			JOptionPane.showMessageDialog(null, "Kein Wettkampf ausgewählt.");
			WindowManager.closeCompetition();
		}

		competitionNameLabel.setText("Wettkampf: " + Application.selectedCompetition);
		tableModel = new GymnastTableModel();
		gymnastsTable.setModel(tableModel);

		navigator = new TableNavigator(gymnastsTable);

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
				int[] selectedRows = gymnastsTable.getSelectedRows();
				if(selectedRows.length > 0){
					tableModel.deleteRows(selectedRows);
				}else{
					Common.showError("Selektieren Sie zunächst einen Teilnehmer.");
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!Common.emptyString(searchField.getText())){
					tableModel.searchFor(searchField.getText());
				}
			}
		});
		clearSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchField.setText("");
				tableModel.searchFor("");
			}
		});

		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					searchButton.doClick();
				}
			}
		});
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showImport();
			}
		});
	}

	public JPanel getPanel(){
		return panel;
	}

	public void close(){

	}
}
