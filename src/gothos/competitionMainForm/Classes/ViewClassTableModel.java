package gothos.competitionMainForm.Classes;

import gothos.DatabaseCore.CompetitionData;
import gothos.FormCore.DataFormTableModel;
import gothos.FormCore.DataTableCell;
import gothos.competitionMainForm.Gymnast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewClassTableModel extends DataFormTableModel {
	protected String className;

	public ViewClassTableModel(String className) {
		super();

		this.className = className;

		iniTable();
	}

	public void iniTable() {
		tableData = new ArrayList<>();
		columns = new ArrayList<>();

		CompetitionData competitionData = new CompetitionData();
		competitionData.setClassName(className);

		LinkedHashMap<String, String> classConfig = competitionData.getClassConfig(className);

		ArrayList<Gymnast> gymnasts = competitionData.calculateClassResult();

		if (gymnasts.size() > 0) {

			columns.add("Startnr.");
			columns.add("Name");
			columns.add("Verein");

			for (Map.Entry<String, Double> apparatus : gymnasts.get(0).getApparatiValues().entrySet()) {
				columns.add(apparatus.getKey());
			}

			columns.add("Punkte");
			columns.add("Platz");

			for (Gymnast gymnast : gymnasts) {
				ArrayList<DataTableCell> row = new ArrayList<>();

				row.add(
						new DataTableCell("0", "", gymnast.getMetaData().get("ID"), "")
				);
				row.add(
						new DataTableCell("0", "", gymnast.getMetaData().get("name"), "")
				);
				row.add(
						new DataTableCell("0", "", gymnast.getMetaData().get("club"), "")
				);

				for (Map.Entry<String, Double> apparatus : gymnast.getApparatiValues().entrySet()) {
					row.add(
							new DataTableCell("0", "", apparatus.getValue(), "")
					);
				}

				row.add(
						new DataTableCell("0", "", gymnast.getSum(), "")
				);
				row.add(
						new DataTableCell("0", "", gymnast.getRanking(), "")
				);

				tableData.add(row);
			}
		}


	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
