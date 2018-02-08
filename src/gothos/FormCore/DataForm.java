package gothos.FormCore;

import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.DatabaseCore.SqliteConnection;
import gothos.Start;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

abstract public class DataForm {

	protected String primaryKey;
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

	public boolean save(){
		boolean status = true;

		HashMap<String, HashMap<String, DatabaseParameter>> params = this.createParams();

		for(Map.Entry<String, HashMap<String, DatabaseParameter>> tableParams: params.entrySet()){
			String sql = "";
			if(this.primaryKey == null || this.primaryKey.isEmpty()){
				long key = Start.database.insertData(tableParams.getKey(), tableParams.getValue());
				if(key != 0){
					this.primaryKey = Long.toString(key);
				}
			}else{
				//Common.printError("No Update Feature available");
				Start.database.updateData(tableParams.getKey(), tableParams.getValue(), this.primaryKey, "ROWID");
			}

			System.out.println(sql);
		}

		return status;
	}

	protected HashMap<String, HashMap<String, DatabaseParameter>> createParams(){

		HashMap<String, HashMap<String, DatabaseParameter>> params = new HashMap<>();

		for(DataFormElement formElement: this.columns){
			String table = formElement.getTable();
			if(table == null || table.isEmpty()){
				table = this.mainTable;
			}

			HashMap<String, DatabaseParameter> tableParams = params.get(table);
			if(tableParams == null){
				tableParams = new HashMap<>();
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
