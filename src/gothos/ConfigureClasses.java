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

	protected String databaseTable = "";
	protected ConfigureClassesModel model;

	public JPanel getPanel() {
		return panel;
	}

	public ConfigureClasses(String databaseTable){
		this.databaseTable = databaseTable;
		model = new ConfigureClassesModel(databaseTable);

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
