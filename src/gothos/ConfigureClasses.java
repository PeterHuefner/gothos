package gothos;

import javax.swing.*;
import java.util.ArrayList;

public class ConfigureClasses {
	private JPanel panel;
	private JTable classesTable;

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
	}
}
