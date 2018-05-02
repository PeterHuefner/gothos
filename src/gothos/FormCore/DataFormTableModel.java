package gothos.FormCore;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
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

	protected String[] displayColumns;

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
					DataTableCell cell = new DataTableCell(this.getPrimaryKeyForRow(result), getTableForColumn(i), result.getObject(i + 1), getDatabaseNameForColumn(i));
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

	public int addEmptyRow(){
		ArrayList<DataTableCell> row = new ArrayList<>();
		for(Integer i = 0; i < columns.size(); i++){
			DataTableCell cell = new DataTableCell("", getTableForColumn(i), null, getDatabaseNameForColumn(i));
			cell.setPrimaryKeyColumn(this.getPrimaryKeyColumnForColumn(i));
			row.add(cell);
		}
		tableData.add(row);
		newDataAvailable(new TableModelEvent(this));
		return tableData.size() - 1;
	}

	public void deleteRow(int row){

		ArrayList<String> handledTables = new ArrayList<>();

		for(DataTableCell cell: tableData.get(row)){
			if(!handledTables.contains(cell.getTable())){
				ArrayList<DatabaseParameter> params = new ArrayList<>();
				params.add(new DatabaseParameter(cell.getPrimaryKey()));
				Application.database.execute("DELETE FROM `" + cell.getTable() + "` WHERE `" + cell.getPrimaryKeyColumn() + "` = ?;", params);
				handledTables.add(cell.getTable());
			}
		}

		tableData.remove(row);
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

	protected String getDatabaseNameForColumn(Integer col){
		return columns.get(col);
	}

	protected boolean editableColumn(int row, int col){
		if(Common.emptyString(tableData.get(row).get(col).getPrimaryKey())){
			return true;
		}else if(columns.get(col).equals(primaryKeyColumn)){
			return false;
		}
		return true;
	}

	protected String getTableForColumn(Integer col){
		return baseTable;
	}

	protected String getPrimaryKeyColumnForColumn(Integer col){
		return "ROWID";
	}

	public String getColumnName(int col) {
		if(rowidSkipped && col >= rowidIndex){
			col++;
		}

		String name = columns.get(col);

		if(this.displayColumns != null && this.displayColumns.length > 0){
			if(rowidSkipped && col >= rowidIndex){
				col--;
			}

			name = this.displayColumns[col];
		}

		return name;
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
			int displayCol = col;
			if (rowidSkipped && col >= rowidIndex) {
				col++;
			}
			DataTableCell cell = tableData.get(row).get(col);
			if(getColumnClass(displayCol) == Boolean.class){
				return cell.booleanValue();
			}else if(getColumnClass(displayCol) == Integer.class){
				return cell.integerValue();
			}else if(getColumnClass(displayCol) == Double.class){
				return cell.doubleValue();
			}else{
				return cell.getValue();
			}
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
			DataTableCell thisCell = tableData.get(row).get(col);
			String key = thisCell.getPrimaryKey();
			tableData.get(row).get(col).setValue(value);

			if(Common.emptyString(key)){
				for(DataTableCell cell: tableData.get(row)){
					cell.setPrimaryKey(thisCell.getPrimaryKey());
				}
			}

			fireTableCellUpdated(row, updateCol);
		}

	}

	protected boolean checkValue(Object value, int row, int col){
		return true;
	}
}
