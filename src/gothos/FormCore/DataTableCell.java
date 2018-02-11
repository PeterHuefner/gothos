package gothos.FormCore;

public class DataTableCell {

	protected String primaryKeyColumn = "ROWID";
	protected String primaryKey = "";
	protected String table = "";
	protected Object value = "";
	protected boolean selfUpdate = true;

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

	public DataTableCell(String primaryKey, String table, Object value) {
		this.primaryKey = primaryKey;
		this.table = table;
		this.value = value;
	}

	public boolean updateDataToDatabase() {
		boolean status = true;


		return status;
	}
}
