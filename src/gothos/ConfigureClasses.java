package gothos;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ConfigureClasses {
	private JPanel panel;
	private JTable classesTable;
	private JButton newButton;
	private JButton deleteButton;
	private JButton backButton;
	private JLabel infoLabel;

	protected String databaseTable = "";
	protected ConfigureClassesModel model;
	protected Boolean openAsChildPanel = false;

	public void setOpenAsChildPanel(Boolean openAsChildPanel) {
		this.openAsChildPanel = openAsChildPanel;
	}

	public JPanel getPanel() {
		return panel;
	}

	public ConfigureClasses(String databaseTable){
		this.databaseTable = databaseTable;
		model = new ConfigureClassesModel(databaseTable);

		classesTable.setModel(model);

		if (databaseTable == "global_classes") {
			infoLabel.setText("Sie bearbeiten die Vorlagen der Altersklassen für die ausgewählte Datenbank. Änderungen wirken sich nur auf neu angelegte Wettkämpfe aus.");
		} else {
			infoLabel.setText("Sie bearbeiten die Altersklassen für den aktuell ausgewähöten Wettkampf. Änderungen wirken sich nur auf diesen Wettkampf aus.");
		}

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (openAsChildPanel) {
					WindowManager.disposeChildFrame();
				}else{
					WindowManager.showStartPanel();
				}
			}
		});


		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int colIndex = model.addEmptyRow();
				if(classesTable.editCellAt(colIndex, 0)){
					classesTable.setRowSelectionInterval(colIndex, colIndex);
					classesTable.requestFocus();
					classesTable.getEditorComponent().requestFocusInWindow();
				}
			}
		});
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = classesTable.getSelectedRows();
				if(selectedRows.length > 0){
					model.deleteRows(selectedRows);
				}else{
					Common.showError("Selektieren Sie zunächst eine Altersklasse.");
				}
			}
		});
	}
}
