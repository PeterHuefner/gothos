package gothos.competitionMainForm;

import com.opencsv.CSVReader;
import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.FormCore.TableNavigator;
import gothos.WindowManager;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CompetitionMainForm {
	private JButton    closeCompetition;
	private JPanel     panel;
	private JLabel     competitionNameLabel;
	private JComboBox  classSelect;
	private JButton    openClass;
	private JButton    printClassProtocol;
	private JButton    printCertificate;
	private JComboBox  squadSelect;
	private JButton    openSquad;
	private JButton    printSquad;
	private JComboBox  teamSelect;
	private JButton    openTeam;
	private JButton    printTeamProtocol;
	private JButton    printTeamCertificate;
	private JTable     gymnastsTable;
	private JButton    importButton;
	private JButton    exportButton;
	private JButton    exportAllButton;
	private JButton    configureClassesButton;
	private JButton    configureAppartiButton;
	private JButton    addGymnast;
	private JButton    removeGymnast;
	private JTextField searchField;
	private JButton    createIdsButton;
	private JButton    clearSearchButton;
	private JButton    searchButton;

	protected static CompetitionMainForm instance;

	protected GymnastTableModel tableModel;
	protected TableNavigator    navigator;
	protected CompetitionData   competitionData;

	public CompetitionMainForm() {

		if (instance != null) {
			return;
		}

		instance = this;
		competitionData = new CompetitionData();

		if (Common.emptyString(Application.selectedCompetition)) {
			JOptionPane.showMessageDialog(null, "Kein Wettkampf ausgewählt.");
			WindowManager.closeCompetition();
		}

		competitionNameLabel.setText("Wettkampf: " + Application.selectedCompetition);
		tableModel = new GymnastTableModel();
		gymnastsTable.setModel(tableModel);

		navigator = new TableNavigator(gymnastsTable);
		setDataToCombos();

		tableModel.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDataToCombos();
			}
		});

		closeCompetition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.closeCompetition();
			}
		});

		addGymnast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowIndex = tableModel.addEmptyRow();
				if (gymnastsTable.editCellAt(rowIndex, 1)) {
					gymnastsTable.setRowSelectionInterval(rowIndex, rowIndex);
					gymnastsTable.requestFocus();
					gymnastsTable.getEditorComponent().requestFocusInWindow();
				}
			}
		});

		removeGymnast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = gymnastsTable.getSelectedRows();
				if (selectedRows.length > 0) {
					tableModel.deleteRows(selectedRows);
				} else {
					Common.showError("Selektieren Sie zunächst einen Teilnehmer.");
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Common.emptyString(searchField.getText())) {
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
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

		createIdsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.shwoSetIds();
			}
		});

		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showExportForm("simple");
			}
		});

		exportAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showExportForm("extended");
			}
		});

		configureClassesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showLocalClasses();
			}
		});

		configureAppartiButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showConfigureApparati();
			}
		});
		openClass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				CompetitionData cd = new CompetitionData();
				cd.setAllApparaties(true);
				cd.setApparatiesAsCols(true);
				cd.setClassName("AK 18 m");

				ArrayList<Gymnast> result = cd.calculateClassResult();

			}
		});
		openSquad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (squadSelect.getSelectedItem() != null) {
					WindowManager.showSquadForm(squadSelect.getSelectedItem().toString());
				}
			}
		});
	}

	protected void setDataToCombos() {
		classSelect.removeAllItems();
		for (String className : competitionData.listClasses()) {
			classSelect.addItem(className);
		}

		squadSelect.removeAllItems();
		for (String squad : competitionData.listSquads()) {
			squadSelect.addItem(squad);
		}

		teamSelect.removeAllItems();
		for (String team : competitionData.listTeams()) {
			teamSelect.addItem(team);
		}
	}

	public JPanel getPanel() {
		return panel;
	}

	public void close() {

	}

	public void refresh() {
		tableModel.reloadTableData();
		setDataToCombos();

	}

	public static void reloadData() {
		instance.refresh();
	}
}
