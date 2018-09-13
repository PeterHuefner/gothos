package gothos.competitionMainForm.Teams;

import gothos.DatabaseCore.CompetitionData;
import gothos.FormCore.DataFormTableModel;
import gothos.FormCore.DataTableCell;
import gothos.competitionMainForm.Gymnast;
import gothos.competitionMainForm.Team;

import java.util.ArrayList;
import java.util.Map;

public class ViewTeamsTableModel extends DataFormTableModel {

	public ViewTeamsTableModel() {
		super();

		iniTable();
	}

	protected void iniTable() {
		tableData = new ArrayList<>();
		columns = new ArrayList<>();

		CompetitionData competitionData = new CompetitionData();

		ArrayList<Team> teams = competitionData.calculateTeamResult();

		if (teams.size() > 0) {
			columns.add("Mannschaft");
			columns.add("Verein");
			columns.add("Startnr.");
			columns.add("Name");

			for (Map.Entry<String, Double> apparatus : teams.get(0).getApparatiValues().entrySet()) {
				columns.add(apparatus.getKey());
			}

			columns.add("Punkte");
			columns.add("Platz");

			for (Team thisTeam: teams) {

				for (Gymnast gymnast: thisTeam.getGymnasts()) {
					ArrayList<DataTableCell> row = new ArrayList<>();

					row.add(
							new DataTableCell("0", "", gymnast.getMetaData().get("team"), "")
					);
					row.add(
							new DataTableCell("0", "", gymnast.getMetaData().get("club"), "")
					);
					row.add(
							new DataTableCell("0", "", gymnast.getMetaData().get("ID"), "")
					);
					row.add(
							new DataTableCell("0", "", gymnast.getMetaData().get("name"), "")
					);

					for (Map.Entry<String, Double> apparatus : teams.get(0).getApparatiValues().entrySet()) {

						//Double apparatusValue = 0.0;
						String apparatusValue = "";

						if (gymnast.getApparatiValues().get(apparatus.getKey()) != null) {
							apparatusValue = gymnast.getApparatiValues().get(apparatus.getKey()).toString();
						}

						row.add(
								new DataTableCell("0", "", apparatusValue, "")
						);
					}

					row.add(
							new DataTableCell("0", "", gymnast.getSum(), "")
					);
					row.add(
							new DataTableCell("0", "", "", "")
					);

					tableData.add(row);
				}

				ArrayList<DataTableCell> row = new ArrayList<>();

				row.add(
						new DataTableCell("0", "", thisTeam.getName(), "")
				);
				row.add(
						new DataTableCell("0", "", "", "")
				);
				row.add(
						new DataTableCell("0", "", "", "")
				);
				row.add(
						new DataTableCell("0", "", "", "")
				);

				for (Map.Entry<String, Double> apparatus : thisTeam.getApparatiValues().entrySet()) {
					row.add(
							new DataTableCell("0", "", apparatus.getValue(), "")
					);
				}

				row.add(
						new DataTableCell("0", "", thisTeam.getSum(), "")
				);
				row.add(
						new DataTableCell("0", "", thisTeam.getRanking(), "")
				);

				tableData.add(row);

				addEmptyRow();
				addEmptyRow();
			}
		}
	}

	/*protected void addRealEmptyRow() {

	}*/

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
