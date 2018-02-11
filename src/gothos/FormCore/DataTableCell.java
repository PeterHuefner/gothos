package gothos.FormCore;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;

import java.util.LinkedHashMap;

public class DataTableCell {

	protected String primaryKeyColumn = "ROWID";
	protected String primaryKey = "";
	protected String table = "";
	protected String columnName = "";
	protected Object value = "";
	protected boolean selfUpdate = true;

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}

	public void setSelfUpdate(boolean selfUpdate) {
		this.selfUpdate = selfUpdate;
	}

	public void setValue(Object value) {
		this.value = value;

		if (this.selfUpdate) {
			this.updateDataToDatabase();
		}
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public Object getValue() {
		return value;
	}

	public String getTable() {
		return table;
	}

	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}

	public DataTableCell(String primaryKey, String table, Object value, String columnName) {
		this.primaryKey = primaryKey;
		this.table = table;
		this.value = value;
		this.columnName = columnName;
	}

	public boolean updateDataToDatabase() {
		boolean status = true;

		LinkedHashMap<String, DatabaseParameter> params = new LinkedHashMap<>();
		params.put(columnName, new DatabaseParameter(toString()));

		if(Common.emptyString(primaryKey)){
			long lastId = Application.database.insertData(table, params);
			if(lastId != 0){
				primaryKey = Long.toString(lastId);
			}else{
				status = false;
			}
		}else{
			params.put(primaryKeyColumn, new DatabaseParameter(primaryKey));
			Application.database.insertOrIgnoreData(table, params);
			status = Application.database.updateData(table, params, primaryKey, primaryKeyColumn);
		}

		return status;
	}

	@Override
	public String toString() {
		return (String) getValue();
	}
}
