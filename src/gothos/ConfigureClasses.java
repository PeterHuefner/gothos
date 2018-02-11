package gothos;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ConfigureClasses {
	private JPanel panel;
	private JTable classesTable;
	private JButton backButton;
	private JButton newButton;
	private JButton deleteButton;

	protected String databaseTable = "";
	protected ConfigureClassesModel model;

	public JPanel getPanel() {
		return panel;
	}

	public ConfigureClasses(String databaseTable){
		this.databaseTable = databaseTable;
		model = new ConfigureClassesModel();
		model.setBaseTable(databaseTable);

		classesTable.setModel(model);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.showStartPanel();
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
				int selectedRow = classesTable.getSelectedRow();
				if(selectedRow != -1){
					model.deleteRow(selectedRow);
				}else{
					Common.showError("Selektieren Sie zunächst eine Altersklasse.");
				}
			}
		});
	}
}
