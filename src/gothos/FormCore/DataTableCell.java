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

		if(value.getClass() == Boolean.class || value.getClass() == Integer.class || value.getClass() == Long.class){
			params.put(columnName, new DatabaseParameter(integerValue()));
		}else if(value.getClass() == Double.class){
			params.put(columnName, new DatabaseParameter(doubleValue()));
		}else{
			params.put(columnName, new DatabaseParameter(toString()));
		}

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
		return Common.objectToString(value);
	}

	public Boolean booleanValue(){
		Boolean bool;

		if(value != null && value.getClass() != Boolean.class){
			String val = toString();

			if(val == null || val == "" || val.toLowerCase() == "false" || val == "0"){
				bool = false;
			}else{
				bool = true;
			}
		}else{
			bool = (Boolean) value;
		}

		return bool;
	}

	public Integer integerValue(){
		Integer number;

		if(value != null && value.getClass() != Integer.class){
			String string = toString();
			if(Common.emptyString(string)){
				string = "0";
			}
			number = Integer.parseInt(string);
		}else{
			number = (Integer) value;
		}

		return number;
	}

	public Double doubleValue(){
		Double number;

		if(value != null && value.getClass() != Double.class){
			String string = toString();
			if(Common.emptyString(string)){
				string = "0";
			}
			number = Double.parseDouble(string);
		}else{
			number = (Double) value;
		}

		return number;
	}
}
