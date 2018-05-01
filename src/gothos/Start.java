package gothos;

import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.DatabaseCore.DatabaseStructure;
import gothos.DatabaseCore.SqliteConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Start {
	public static Start instance;


	private JPanel startPanel;
	private JList competitionList;
	private JButton createCompetitionButton;
	private JButton loadSelectedCompetitionButton;
	private JButton deleteSelectedCompetitionButton;
	private JLabel selectedDatabaseFileLabel;
	private JButton createDatabaseFileButton;
	private JButton selectDatabaseFileButton;
	private JButton configureClassesButton;
	private JButton editCompetition;

	public Start() {

		this.createCompetitionButton.setEnabled(false);
		this.loadSelectedCompetitionButton.setEnabled(false);
		this.deleteSelectedCompetitionButton.setEnabled(false);
		this.configureClassesButton.setEnabled(false);
		this.editCompetition.setEnabled(false);

		selectDatabaseFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = findDatabaseDialog();

				int chooserState = chooser.showOpenDialog(WindowManager.mainFrame);
				if(chooserState == JFileChooser.APPROVE_OPTION){
					if(Application.connectToDatabase(chooser.getSelectedFile().getAbsolutePath())){
						selectedDatabaseFileLabel.setText("verbundene Datenbank: " + chooser.getSelectedFile().getAbsolutePath());
						createCompetitionButton.setEnabled(true);
						configureClassesButton.setEnabled(true);
						listCompetitions();
					}
				}
			}
		});

		createDatabaseFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = findDatabaseDialog();

				int chooserState = chooser.showSaveDialog(WindowManager.mainFrame);
				if(chooserState == JFileChooser.APPROVE_OPTION){
					String file = chooser.getSelectedFile().getAbsolutePath();

					if(!file.matches("\\.sqlite3?$")){
						file += ".sqlite3";
					}

					if(Application.connectToDatabase(file)){
						selectedDatabaseFileLabel.setText("verbundene Datenbank: " + file);
						createCompetitionButton.setEnabled(true);
						configureClassesButton.setEnabled(true);
						listCompetitions();
					}
				}
			}
		});

		createCompetitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showCreateCompetition();
			}
		});

		configureClassesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showGlobalClasses();
			}
		});

		competitionList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String selectedCompetition = "";
				if(competitionList.getSelectedValue() != null){
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if(Common.emptyString(selectedCompetition)){
					loadSelectedCompetitionButton.setEnabled(false);
					deleteSelectedCompetitionButton.setEnabled(false);
					editCompetition.setEnabled(false);
				}else{
					loadSelectedCompetitionButton.setEnabled(true);
					deleteSelectedCompetitionButton.setEnabled(true);
					editCompetition.setEnabled(true);
				}
			}
		});

		loadSelectedCompetitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedCompetition = "";
				if(competitionList.getSelectedValue() != null){
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if(!Common.emptyString(selectedCompetition)){

				}
			}
		});

		deleteSelectedCompetitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedCompetition = "";
				if(competitionList.getSelectedValue() != null){
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if(!Common.emptyString(selectedCompetition)){
					Integer userChoice = JOptionPane.showConfirmDialog(startPanel, "Soll der Wettkampf " + selectedCompetition + " wirklich gelöscht werden?\nEs werden alle zugehörigen Daten (konfigurierte Geräte und Alterklassen, sowie alle Teilnehmer entfernt.", "Löschen bestätigen", JOptionPane.YES_NO_OPTION);

					if(userChoice == JOptionPane.YES_OPTION){
						DatabaseStructure.removeCompetition(selectedCompetition);
						listCompetitions();
					}
				}
			}
		});

		editCompetition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedCompetition = "";
				if(competitionList.getSelectedValue() != null){
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if(!Common.emptyString(selectedCompetition)){
					WindowManager.showCreateCompetition(selectedCompetition);
				}
			}
		});
	}

	private void listCompetitions(){
		ResultSet result = Application.database.query("SELECT name FROM competitions;");
		ArrayList<String> competitions = new ArrayList<>();
		try{
			while(result.next()){
				competitions.add(result.getString("name"));
			}
			result.close();
		}catch (SQLException e){
			Common.printError(e);
		}

		competitionList.clearSelection();
		competitionList.setListData(competitions.toArray());
	}

	private JFileChooser findDatabaseDialog(){
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Datenbanken", "sqlite", "sqlite3");
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);

		return chooser;
	}

	public void panelShowed(){
		if(Application.database != null){
			listCompetitions();
		}
	}

	public static void main(String[] args) {
		Application.initiate();
		Start.instance = new Start();
		WindowManager.initiateVisuals(Start.instance.startPanel);
	}
}
