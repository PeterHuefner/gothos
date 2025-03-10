package gothos.competitionMainForm.CompetitionInfoWindow;

import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.CompetitionStatistics;
import gothos.FormCore.DataFormTableModel;
import gothos.FormCore.DataTableCell;
import org.apache.pdfbox.debugger.ui.MapEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompetitionInfoWindowTableModel extends DataFormTableModel {

	String[] cols;

	CompetitionData competitionData;
	CompetitionStatistics statistics;
	LinkedHashMap<String, LinkedHashMap<String, Integer>> stats;
	LinkedHashMap<String, Integer> counts;

	public CompetitionInfoWindowTableModel() {
		super();

		iniTable();
	}

	void iniTable() {
		tableData = new ArrayList<>();
		columns = new ArrayList<>();

		cols = new String[]{"class", "club", "squad", "team"};

		this.competitionData = new CompetitionData();
		this.statistics = new CompetitionStatistics(competitionData);

		this.stats = statistics.getStatsForCols(cols);
		this.counts = statistics.getCountsForCols(cols);

		columns.add("Entität");
		columns.add("Anzahl Entitäten/Aktive pro Entität");

		addGymnasts();

		addEmptyRow();
		addEmptyRow();

		addClubs();

		addEmptyRow();
		addEmptyRow();

		addClasses();

		addEmptyRow();
		addEmptyRow();

		addSquads();

		addEmptyRow();
		addEmptyRow();

		addTeams();

	}

	void addCategory(String category, String key) {
		ArrayList<DataTableCell> row = new ArrayList<>();
		row.add(
				new DataTableCell("0", "", category, "")
		);
		row.add(
				new DataTableCell("0", "", counts.getOrDefault(key, 0), "")
		);
		tableData.add(row);

		//addEmptyRow();
		row = new ArrayList<>();
		row.add(
				new DataTableCell("0", "", "---------------------", "")
		);
		row.add(
				new DataTableCell("0", "", "---------------------", "")
		);
		tableData.add(row);

		for(Map.Entry<String, Integer> club : stats.get(key).entrySet()) {
			row = new ArrayList<>();

			row.add(
					new DataTableCell("0", "", club.getKey(), "")
			);
			row.add(
					new DataTableCell("0", "", club.getValue(), "")
			);

			tableData.add(row);
		}
	}

	void addGymnasts() {
		ArrayList<DataTableCell> row = new ArrayList<>();

		row.add(
				new DataTableCell("0", "", "Aktive", "")
		);
		row.add(
				new DataTableCell("0", "", statistics.getGymnastCountInCurrentQuery(), "")
		);

		tableData.add(row);
	}

	void addClubs() {
		addCategory("Vereine", "club");
	}

	void addClasses() {
		addCategory("Alterklassen", "class");
	}

	void addSquads() {
		addCategory("Riegen", "squad");
	}

	void addTeams() {
		addCategory("Mannschaften", "team");
	}
}
