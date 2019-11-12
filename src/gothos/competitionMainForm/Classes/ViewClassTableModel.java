package gothos.competitionMainForm.Classes;

import gothos.Common;
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
					String apparatusValue = String.format("%2.3f", apparatus.getValue());

					if (gymnast.getAdditionalApparatiValues().containsKey(apparatus.getKey()) && gymnast.getAdditionalApparatiValues().get(apparatus.getKey()).containsKey("d_value")) {
						Double dValue = gymnast.getAdditionalApparatiValues().get(apparatus.getKey()).get("d_value");

						if (dValue != null && dValue > 0) {
							apparatusValue = String.format("%2.3f", apparatus.getValue()) + " [" + String.format("%2.3f", dValue) + "]";
						}
					}

					row.add(
							new DataTableCell("0", "", String.format("%2.3f", apparatus.getValue()), "")
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
