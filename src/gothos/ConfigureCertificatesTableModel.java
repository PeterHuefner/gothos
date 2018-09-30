package gothos;

import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;
import gothos.FormCore.DataTableCell;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigureCertificatesTableModel extends DataFormTableModel {

	protected String type;

	public ConfigureCertificatesTableModel(String table, String type) {
		super();

		this.baseTable = table;
		this.type      = type;

		displayColumns = new String[]{"Zeile", "Schrift", "Größe", "Zeilenhöhe", "Stil", "Ausrichtung"};

		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(type));
		buildData("SELECT ROWID, line, font, size, height, weight, align FROM " + baseTable + " WHERE type = ?;", params);
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

	@Override
	protected Object handleValuePreInsert(Object value, int row, int col) {

		if (col == 3 || col == 4) {
			if (value == null) {
				value = "";
			}
			String number = value.toString();
			number = number.replaceAll(",", ".");
			if(Common.emptyString(number)){
				number = "0";
			}
			value = number;
		}

		return value;
	}

	@Override
	protected boolean checkValue(Object value, int row, int col) {
		boolean status = true;

		if (col == 3 || col == 4) {
			if (value == null) {
				value = "";
			}

			Matcher matcher = Pattern.compile("^\\d+(\\.\\d+)?$").matcher(value.toString());
			if(!matcher.find()){
				status = false;
				Common.showError("'" + value.toString() + "' ist keine gültige Zahl.");
			}
		}

		return status;
	}
}
