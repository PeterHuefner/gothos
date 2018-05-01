package gothos.FormCore;

import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.DatabaseCore.SqliteConnection;
import gothos.Application;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

abstract public class DataForm {

	protected String primaryKey = "";
	protected String mainTable;
	protected ArrayList<DataFormElement> columns;
	protected JPanel panel;

	public JPanel getPanel() {
		return panel;
	}

	public DataForm(String table){
		this.columns = new ArrayList<DataFormElement>();
		this.mainTable = table;
	}

	public DataForm(String table, String primaryKey){
		this.columns = new ArrayList<DataFormElement>();
		this.mainTable = table;
		this.primaryKey = primaryKey;
	}

	public void load(){
		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(this.primaryKey));

		String cols = "";
		String comma = "";
		for(DataFormElement ele: columns){
			cols += comma + ele.name;
			comma = ", ";
		}

		ResultSet rs = Application.database.query("SELECT " + cols + " FROM " + mainTable + " WHERE ROWID = ?", params);

		try{
			if(rs.next()){
				for(DataFormElement ele: columns){
					ele.setValue(rs.getString(ele.getName()));
				}
			}
			rs.close();
		}catch (SQLException e){
			Common.printError(e);
		}
	}

	public boolean save(){
		boolean status = true;

		if(check()){
			LinkedHashMap<String, LinkedHashMap<String, DatabaseParameter>> params = this.createParams();

			for(Map.Entry<String, LinkedHashMap<String, DatabaseParameter>> tableParams: params.entrySet()){
				String sql = "";
				if(this.primaryKey == null || this.primaryKey.isEmpty()){
					long key = Application.database.insertData(tableParams.getKey(), tableParams.getValue());
					if(key != 0){
						this.primaryKey = Long.toString(key);
					}
				}else{
					//Common.printError("No Update Feature available");
					Application.database.updateData(tableParams.getKey(), tableParams.getValue(), this.primaryKey, "ROWID");
				}

				System.out.println(sql);
			}
		}else{
			status = false;
		}

		return status;
	}

	public boolean check(){
		return true;
	}

	protected LinkedHashMap<String, LinkedHashMap<String, DatabaseParameter>> createParams(){

		LinkedHashMap<String, LinkedHashMap<String, DatabaseParameter>> params = new LinkedHashMap<>();

		for(DataFormElement formElement: this.columns){
			String table = formElement.getTable();
			if(table == null || table.isEmpty()){
				table = this.mainTable;
			}

			LinkedHashMap<String, DatabaseParameter> tableParams = params.get(table);
			if(tableParams == null){
				tableParams = new LinkedHashMap<>();
				params.put(table, tableParams);
			}

			String value = formElement.getValue();
			DatabaseParameter param = new DatabaseParameter(value);

			try {
				param = new DatabaseParameter(Double.parseDouble(value));
			}catch (NumberFormatException e){}

			try{
				param = new DatabaseParameter(Integer.parseInt(value));
			}catch (NumberFormatException e){}

			tableParams.put(formElement.getName(), param);
		}

		return params;
	}

	abstract protected void connectPanel();
	abstract protected void defineColumns();

}
