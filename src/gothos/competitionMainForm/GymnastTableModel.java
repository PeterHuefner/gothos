package gothos.competitionMainForm;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;
import gothos.FormCore.DataTableCell;

import javax.swing.event.TableModelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class GymnastTableModel extends DataFormTableModel {

	protected String   baseSql;
	protected String   orderSql;
	protected String[] searchCols;

	protected String lastSql;
	protected ArrayList<DatabaseParameter> lastParams;

	public ArrayList<DatabaseParameter> getLastParams() {
		return lastParams;
	}

	public String getLastSql() {
		return lastSql;
	}

	public GymnastTableModel() {

		super();
		this.baseTable = "competition_" + Application.selectedCompetition;
		displayColumns = new String[]{"ID", "Name", "Geburtsdatum", "Altersklasse", "Verein", "Riege", "Mannschaft"};
		searchCols = new String[]{"ID", "Name", "birthdate", "class", "club", "squad", "team"};
		baseSql = "SELECT ROWID, " + String.join(", ", searchCols) + " FROM " + baseTable + " WHERE active = 1";
		orderSql = " ORDER BY ID; ROWID";
		buildData(baseSql + orderSql + ";", new ArrayList<>());

	}

	public void searchFor(String term) {
		String                       sql    = baseSql;
		ArrayList<DatabaseParameter> params = new ArrayList<>();

		if (!Common.emptyString(term)) {
			sql += " AND (";
			String connector = "";
			for (int index = 0; index < searchCols.length; index++) {
				params.add(new DatabaseParameter("%" + term + "%"));

				sql += connector + searchCols[index] + " LIKE ?";
				connector = " OR ";
			}
			sql += ")";
		}

		buildData(sql + orderSql + ";", params);

		lastSql = sql;
		lastParams = params;
	}

	@Override
	public void deleteRow(int row) {
		ArrayList<DataTableCell> cells = tableData.get(row);
		if (cells.size() > 0) {
			ArrayList<DatabaseParameter> params = new ArrayList<>();
			params.add(new DatabaseParameter(cells.get(0).getPrimaryKey()));
			Application.database.execute("UPDATE competition_" + Application.selectedCompetition + " SET active = 0 WHERE ROWID = ?", params);
		}

		tableData.remove(row);
		fireTableRowsDeleted(row, row);
		callListeners();
	}

	@Override
	public void deleteRows(int[] rows) {
		Integer[] convertedRows = new Integer[rows.length];
		for (int i = 0; i < rows.length; i++) {
			convertedRows[i] = rows[i];
		}
		Arrays.sort(convertedRows, Collections.reverseOrder());

		for (Integer row : convertedRows) {
			ArrayList<DataTableCell> cells = tableData.get(row.intValue());
			if (cells.size() > 0) {
				ArrayList<DatabaseParameter> params = new ArrayList<>();
				params.add(new DatabaseParameter(cells.get(0).getPrimaryKey()));
				Application.database.execute("UPDATE competition_" + Application.selectedCompetition + " SET active = 0 WHERE ROWID = ?", params);
			}
		}

		for (Integer row : convertedRows) {
			tableData.remove(row.intValue());
		}
		fireTableDataChanged();
		callListeners();
	}
}
