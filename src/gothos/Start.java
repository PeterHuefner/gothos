package gothos;

import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.DatabaseCore.DatabaseStructure;
import gothos.DatabaseCore.SqliteConnection;
import gothos.competitionMainForm.Classes.PdfClassResult;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Start {
	public static Start instance;


	private JPanel  startPanel;
	private JList   competitionList;
	private JButton createCompetitionButton;
	private JButton loadSelectedCompetitionButton;
	private JButton deleteSelectedCompetitionButton;
	private JLabel  selectedDatabaseFileLabel;
	private JButton createDatabaseFileButton;
	private JButton selectDatabaseFileButton;
	private JButton configureClassesButton;
	private JButton editCompetition;
	private JButton configureCertificates;
	private JButton configureTeamCertificates;

	public Start() {

		//Für Performance bei PDFs
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

		this.createCompetitionButton.setEnabled(false);
		this.loadSelectedCompetitionButton.setEnabled(false);
		this.deleteSelectedCompetitionButton.setEnabled(false);
		this.configureClassesButton.setEnabled(false);
		this.editCompetition.setEnabled(false);
		this.configureCertificates.setEnabled(false);
		this.configureTeamCertificates.setEnabled(false);

		selectDatabaseFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Application.disconnectDatabase();
				createCompetitionButton.setEnabled(false);
				loadSelectedCompetitionButton.setEnabled(false);
				deleteSelectedCompetitionButton.setEnabled(false);
				configureClassesButton.setEnabled(false);
				editCompetition.setEnabled(false);
				configureCertificates.setEnabled(false);
				configureTeamCertificates.setEnabled(false);
				selectedDatabaseFileLabel.setText("keine Datenbankverbindung");

				JFileChooser chooser = findDatabaseDialog();

				int chooserState = chooser.showOpenDialog(WindowManager.mainFrame);
				if (chooserState == JFileChooser.APPROVE_OPTION) {
					selectedDatabaseFileLabel.setText("Datenbankverbindung wird etabliert. Bitte warten ...");
					if (Application.connectToDatabase(chooser.getSelectedFile().getAbsolutePath())) {
						selectedDatabaseFileLabel.setText("verbundene Datenbank: " + chooser.getSelectedFile().getAbsolutePath());
						createCompetitionButton.setEnabled(true);
						configureClassesButton.setEnabled(true);
						configureCertificates.setEnabled(true);
						configureTeamCertificates.setEnabled(true);
						listCompetitions();
					}
				}
			}
		});

		createDatabaseFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Application.disconnectDatabase();
				createCompetitionButton.setEnabled(false);
				loadSelectedCompetitionButton.setEnabled(false);
				deleteSelectedCompetitionButton.setEnabled(false);
				configureClassesButton.setEnabled(false);
				editCompetition.setEnabled(false);
				configureCertificates.setEnabled(false);
				configureTeamCertificates.setEnabled(false);
				selectedDatabaseFileLabel.setText("keine Datenbankverbindung");

				JFileChooser chooser = findDatabaseDialog();

				int chooserState = chooser.showSaveDialog(WindowManager.mainFrame);
				if (chooserState == JFileChooser.APPROVE_OPTION) {
					selectedDatabaseFileLabel.setText("Datenbankverbindung wird etabliert. Bitte warten ...");
					String file = chooser.getSelectedFile().getAbsolutePath();

					if (!file.matches("^.+\\.sqlite3?$")) {
						file += ".sqlite3";
					}

					if (Application.connectToDatabase(file)) {
						selectedDatabaseFileLabel.setText("verbundene Datenbank: " + file);
						createCompetitionButton.setEnabled(true);
						configureClassesButton.setEnabled(true);
						configureCertificates.setEnabled(true);
						configureTeamCertificates.setEnabled(true);
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
				if (competitionList.getSelectedValue() != null) {
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if (Common.emptyString(selectedCompetition)) {
					loadSelectedCompetitionButton.setEnabled(false);
					deleteSelectedCompetitionButton.setEnabled(false);
					editCompetition.setEnabled(false);
				} else {
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
				if (competitionList.getSelectedValue() != null) {
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if (!Common.emptyString(selectedCompetition)) {
					Application.selectedCompetition = selectedCompetition;
					WindowManager.showCompetitionPanel();
				}
			}
		});

		competitionList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (e.getClickCount() == 2 && !e.isConsumed() && competitionList.getSelectedValue() != null) {
					loadSelectedCompetitionButton.doClick();
				}
			}
		});

		deleteSelectedCompetitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedCompetition = "";
				if (competitionList.getSelectedValue() != null) {
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if (!Common.emptyString(selectedCompetition)) {
					Integer userChoice = JOptionPane.showConfirmDialog(startPanel, "Soll der Wettkampf " + selectedCompetition + " wirklich gelöscht werden?\nEs werden alle zugehörigen Daten (konfigurierte Geräte und Alterklassen, sowie alle Teilnehmer entfernt.", "Löschen bestätigen", JOptionPane.YES_NO_OPTION);

					if (userChoice == JOptionPane.YES_OPTION) {
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
				if (competitionList.getSelectedValue() != null) {
					selectedCompetition = competitionList.getSelectedValue().toString();
				}

				if (!Common.emptyString(selectedCompetition)) {
					WindowManager.showCreateCompetition(selectedCompetition);
				}
			}
		});

		Preferences preferences = Preferences.userNodeForPackage(Start.class);
		if (!Common.emptyString(preferences.get("database", ""))) {
			String databaseFile = preferences.get("database", "");
			File   file         = new File(databaseFile);
			if (file.exists()) {
				if (Application.connectToDatabase(databaseFile)) {
					selectedDatabaseFileLabel.setText("verbundene Datenbank: " + file);
					createCompetitionButton.setEnabled(true);
					configureClassesButton.setEnabled(true);
					configureCertificates.setEnabled(true);
					configureTeamCertificates.setEnabled(true);
					listCompetitions();
				}
			}
		}

		//WindowManager.showTest();
		/*if(Application.database != null){
			Application.selectedCompetition = "BMS";
			PdfClassResult test = new PdfClassResult("AK 18 m");
			test.generatePdf();
			test.save(System.getProperty("user.home") + File.separator + "testpdf.pdf");
		}*/

		configureTeamCertificates.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showConfigureCertificates("team");
			}
		});


		configureCertificates.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showConfigureCertificates("single");
			}
		});
	}

	private void listCompetitions() {
		ResultSet         result       = Application.database.query("SELECT name FROM competitions;");
		ArrayList<String> competitions = new ArrayList<>();
		try {
			while (result.next()) {
				competitions.add(result.getString("name"));
			}
			result.close();
		} catch (SQLException e) {
			Common.printError(e);
		}

		competitionList.clearSelection();
		competitionList.setListData(competitions.toArray());
	}

	private JFileChooser findDatabaseDialog() {
		JFileChooser            chooser = new JFileChooser();
		FileNameExtensionFilter filter  = new FileNameExtensionFilter("Datenbanken", "sqlite", "sqlite3");
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);

		return chooser;
	}

	public void panelShowed() {
		if (Application.database != null) {
			listCompetitions();
		}
	}

	public static void main(String[] args) {
		Application.initiate();
		Start.instance = new Start();
		WindowManager.initiateVisuals(Start.instance.startPanel);
	}
}
