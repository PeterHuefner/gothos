package gothos.competitionMainForm;

import com.opencsv.CSVReader;
import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.WindowManager;
import sun.security.krb5.internal.APOptions;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImportForm {
	private JButton backButton;
	private JButton chooseFileButton;
	private JTable  importDataTable;
	private JButton importButton;
	private JPanel  panel;
	private JLabel helpLabel;

	protected String         csvFilePath;
	protected List<String[]> fileData;

	protected String[]                        allowedCols = new String[]{"id", "name", "geburtsdatum", "altersklasse", "verein", "riege", "mannschaft"};
	protected String[]                        dbCols      = new String[]{"id", "name", "birthdate", "class", "club", "squad", "team"};
	protected LinkedHashMap<Integer, Integer> colNames    = new LinkedHashMap<>();

	public ImportForm() {

		importButton.setEnabled(false);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});

		chooseFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser            chooser = new JFileChooser();
				FileNameExtensionFilter filter  = new FileNameExtensionFilter("CSV-Dateien", "csv");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);

				int chooserState = chooser.showOpenDialog(WindowManager.childFrame);
				if (chooserState == JFileChooser.APPROVE_OPTION) {
					csvFilePath = chooser.getSelectedFile().getAbsolutePath();

					if (readData()) {
						showData();
						importButton.setEnabled(true);
					} else {
						importButton.setEnabled(false);
					}
				}
			}
		});
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importData();
			}
		});
	}

	protected boolean readData() {
		Boolean status = false;

		try {
			CSVReader reader = new CSVReader(new FileReader(csvFilePath));
			fileData = reader.readAll();

			if (fileData.size() > 1) {
				String[] firstRow = fileData.get(0);

				for (int i = 0; i < firstRow.length; i++) {
					firstRow[i] = firstRow[i].trim();
					Integer colKey = Arrays.asList(allowedCols).indexOf(firstRow[i].toLowerCase());
					if (colKey > -1) {
						colNames.put(i, colKey);
					}
				}

				if (colNames.size() > 0) {
					status = true;
				}else{
					Common.printError("Aus der Datei konnten keine Spalten zum Importieren zugeordnet werden.\nÜberprüfen Sie die Spaltennamen der CSV-Datei mit denen aus dem unten stehenden Hilfe-Text.");
				}
			}

		} catch (FileNotFoundException e) {
			Common.printError("Datei " + csvFilePath + " wurde nicht gefunden");
		} catch (IOException e) {
			Common.printError("Datei " + csvFilePath + " konnte nicht gelesen werden");
		}

		return status;
	}

	protected void showData() {

		DefaultTableModel model = new DefaultTableModel();

		for (Map.Entry<Integer, Integer> colIndizies : colNames.entrySet()) {
			model.addColumn(allowedCols[colIndizies.getValue()]);
		}

		for (int i = 1; i < fileData.size(); i++) {
			int j = 0;

			Object[] row = new Object[colNames.size()];

			for (Map.Entry<Integer, Integer> colIndizies : colNames.entrySet()) {
				row[j] = fileData.get(i)[colIndizies.getKey()];
				j++;
			}

			model.addRow(row);
		}

		importDataTable.setModel(model);
		model.newDataAvailable(new TableModelEvent(model));
	}

	protected void importData() {

		for (int i = 1; i < fileData.size(); i++) {

			LinkedHashMap<String, DatabaseParameter> columns = new LinkedHashMap<>();

			for (Map.Entry<Integer, Integer> colIndizies : colNames.entrySet()) {
				columns.put(
						dbCols[colIndizies.getValue()],
						new DatabaseParameter(fileData.get(i)[colIndizies.getKey()])
				);
			}

			Application.database.insertData("competition_" + Application.selectedCompetition, columns);
		}

		CompetitionMainForm.reloadData();
		WindowManager.disposeChildFrame();
	}

	public void finalize() {
		System.out.println("finalize");
	}

	public JPanel getPanel() {
		return panel;
	}
}
