package gothos.competitionMainForm.Squad;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SquadFormTableModel extends DataFormTableModel {

	protected String squad;
	protected String apparatus;

	public String getApparatus() {
		return apparatus;
	}

	public void setApparatus(String apparatus) {
		this.apparatus = apparatus;
		iniTable();
		fireTableStructureChanged();
		callListeners();
	}

	public SquadFormTableModel(String squad) {
		super();

		this.squad = squad;
		iniTable();
	}

	public void iniTable() {

		String                       sql;
		ArrayList<DatabaseParameter> parameters;
		ArrayList<String>            cols        = new ArrayList<>();
		ArrayList<String>            displayCols = new ArrayList<>();

		cols.add("competition_" + Application.selectedCompetition + ".ROWID");
		cols.add("ID");
		cols.add("name");
		cols.add("class");
		cols.add("club");
		cols.add("team");

		displayCols.add("ID");
		displayCols.add("Name");
		displayCols.add("Altersklasse");
		displayCols.add("Verein");
		displayCols.add("Mannschaft");

		CompetitionData competitionData = new CompetitionData();

		competitionData.setSquad(squad);

		if (!Common.emptyString(apparatus)) {
			ArrayList<String> apparatusList = new ArrayList<>();
			apparatusList.add(apparatus);
			competitionData.setApparaties(apparatusList);

			displayCols.add(apparatus);
			displayCols.add("D-Wert");
			displayCols.add("Wertung für Mannschaft");

			cols.add(apparatus);
			cols.add(apparatus + "_d_value");
			cols.add("IFNULL(isTeamMember, 1) AS isTeamMember");
		}

		competitionData.setColums(cols.toArray(new String[cols.size()]));

		sql = competitionData.getSql();
		parameters = competitionData.getParameters();

		displayColumns = displayCols.toArray(new String[displayCols.size()]);

		buildData(sql, parameters);
	}

	@Override
	protected boolean editableColumn(int row, int col) {
		boolean editable = super.editableColumn(row, col);

		if (editable && col > 5) {
			editable = true;
		} else {
			editable = false;
		}

		return editable;
	}

	@Override
	protected String getTableForColumn(Integer col) {
		if (col < 5) {
			return baseTable;
		} else {
			return "competition_" + Application.selectedCompetition + "_apparati_" + apparatus;
		}
	}

	@Override
	protected String getPrimaryKeyColumnForColumn(Integer col) {
		if (col < 5) {
			return "ROWID";
		} else {
			return "gymnast";
		}

	}

	@Override
	protected Object handleValuePreInsert(Object value, int row, int col) {

		if (col == 6 || col == 7) {
			String apparatiValue = value.toString();
			apparatiValue = apparatiValue.replaceAll(",", ".");
			if(Common.emptyString(apparatiValue)){
				apparatiValue = "0";
			}
			value = apparatiValue;
		}

		return value;
	}

	@Override
	protected boolean checkValue(Object value, int row, int col) {
		boolean status = true;

		if (col == 6 || col == 7) {
			Matcher matcher = Pattern.compile("^\\d+(\\.\\d+)?$").matcher(value.toString());
			if(!matcher.find()){
				status = false;
				Common.showError("'" + value.toString() + "' ist keine gültige Wertung.");
			}
		}

		return status;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 7) {
			return Boolean.class;
		} else if (columnIndex == 5) {
			return String.class;
		} else {
			return String.class;
		}
	}

}
