package gothos.FormCore;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

abstract public class DataFormTableModel extends DefaultTableModel {

	protected String baseTable = "";
	protected String primaryKeyColumn = "ROWID";
	protected ArrayList<ArrayList<DataTableCell>> tableData;
	protected ArrayList<String> columns;

	protected ResultSetMetaData metaData;

	protected boolean rowidSkipped = false;
	protected Integer rowidIndex = 0;

	public DataFormTableModel(){
		tableData = new ArrayList<>();
	}

	public void buildData(String sql, ArrayList<DatabaseParameter> params){
		ResultSet result = Application.database.query(sql, params);
		tableData = new ArrayList<>();
		columns = new ArrayList<>();

		try{
			this.metaData = result.getMetaData();

			for(Integer i = 0; i < metaData.getColumnCount(); i++){
				String columnName = metaData.getColumnLabel(i + 1);
				if(Common.emptyString(columnName)){
					columnName = metaData.getColumnName(i + 1);
				}
				if(columnName.equalsIgnoreCase("ROWID")){
					this.rowidSkipped = true;
					this.rowidIndex = i;
				}
				columns.add(columnName);
			}

			while(result.next()){
				ArrayList<DataTableCell> row = new ArrayList<>();
				for(Integer i = 0; i < columns.size(); i++){
					DataTableCell cell = new DataTableCell(this.getPrimaryKeyForRow(result), getTableForColumn(i), result.getObject(i));
					cell.setPrimaryKeyColumn(this.getPrimaryKeyColumnForColumn(i));
					row.add(cell);
				}
				tableData.add(row);
			}
			result.close();
		}catch (SQLException e){
			Common.printError(e);
		}

		newDataAvailable(new TableModelEvent(this));
	}

	protected String getPrimaryKeyForRow(ResultSet rs){
		try{
			return rs.getString(this.primaryKeyColumn);
		}catch (SQLException e){
			Common.printError(e);
		}
		return "";
	}

	protected String getPrimaryKeyForRow(Integer row){
		String primaryKey = "";

		for(Integer col = 0; col < columns.size(); col++){
			if(columns.get(col).equals(primaryKeyColumn)){
				primaryKey = (String) tableData.get(row).get(col).getValue();
				break;
			}
		}

		return primaryKey;
	}

	protected boolean editableColumn(int row, int col){
		if(Common.emptyString(tableData.get(row).get(col).getPrimaryKey())){
			return true;
		}else if(columns.get(col).equals(primaryKeyColumn)){
			return false;
		}
		return true;
	}

	protected String getTableForColumn(Integer i){
		return baseTable;
	}

	protected String getPrimaryKeyColumnForColumn(Integer i){
		return "ROWID";
	}

	public String getColumnName(int col) {
		if(rowidSkipped && col >= rowidIndex){
			col++;
		}
		return columns.get(col);
	}

	public int getRowCount() {
		if(tableData != null){
			return tableData.size();
		}else{
			return 0;
		}
	}

	public int getColumnCount() {
		if(columns != null) {
			Integer count = columns.size();
			if (rowidSkipped) {
				count--;
			}

			return count;
		}else{
			return 0;
		}
	}

	public Object getValueAt(int row, int col) {
		if(tableData != null) {
			if (rowidSkipped && col >= rowidIndex) {
				col++;
			}
			return tableData.get(row).get(col);
		}else{
			return 0;
		}

	}

	public boolean isCellEditable(int row, int col) {
		if(tableData != null) {
			if (rowidSkipped && col >= rowidIndex) {
				col++;
			}
			return editableColumn(row, col);
		}else{
			return false;
		}
	}

	public void setValueAt(Object value, int row, int col) {
		int updateCol = col;
		if(rowidSkipped && col >= rowidIndex){
			col++;
		}

		if(checkValue(value, row, col)){
			tableData.get(row).get(col).setValue(value);
			fireTableCellUpdated(row, updateCol);
		}

	}

	protected boolean checkValue(Object value, int row, int col){
		return true;
	}
}
