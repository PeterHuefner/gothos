package gothos;

import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;
import gothos.FormCore.DataTableCell;

import java.util.ArrayList;

public class ConfigureCertificatesTableModel extends DataFormTableModel {

	protected String type;

	public ConfigureCertificatesTableModel(String table, String type) {
		super();

		this.baseTable = table;
		this.type      = type;

		displayColumns = new String[]{"Zeile", "Schrift", "Größe", "Stil", "Ausrichtung"};

		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(type));
		buildData("SELECT ROWID, line, font, size, weight, align FROM " + baseTable + " WHERE type = ?;", params);
	}

	public int addEmptyRow() {
		int insertedIndex = super.addEmptyRow();

		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(type));
		Application.database.execute("INSERT INTO " + baseTable + " (type) VALUES (?)", params);

		long lastId = Application.database.getLastInsertId();

		if (lastId != 0) {
			ArrayList<DataTableCell> lastRow = tableData.get(insertedIndex);

			for (DataTableCell cell: lastRow) {
				cell.setPrimaryKey(Long.toString(lastId));
			}
		}

		return insertedIndex;
	}
}
