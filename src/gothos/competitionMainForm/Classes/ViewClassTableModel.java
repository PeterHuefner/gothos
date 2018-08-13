package gothos.competitionMainForm.Classes;

import gothos.DatabaseCore.CompetitionData;
import gothos.FormCore.DataFormTableModel;

import java.util.LinkedHashMap;

public class ViewClassTableModel extends DataFormTableModel {
	protected String className;

	public ViewClassTableModel(String className) {
		super();

		this.className = className;

		iniTable();
	}

	public void iniTable(){


		CompetitionData competitionData = new CompetitionData();
		LinkedHashMap<String, String> classConfig = competitionData.getClassConfig(className);


	}
}
