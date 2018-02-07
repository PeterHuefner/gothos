package gothos.FormCore;

import gothos.DatabaseCore.DatabaseParameter;

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
			if(this.primaryKey.isEmpty()){
				sql = this.createInsertStatement(tableParams.getKey(), tableParams.getValue());
			}else{

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

	protected String createInsertStatement(String table, HashMap<String, DatabaseParameter> params){
		String sql = "INSERT INTO `" + table + "`";
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		String comma = "";

		for(Map.Entry<String, DatabaseParameter> param: params.entrySet()){
			String columnName = param.getKey();
			//DatabaseParameter val = param.getValue();

			columns.append(comma);
			columns.append(columnName);

			values.append(comma);
			values.append("?");

			comma = ", ";
		}

		sql += " (" + columns.toString() + ") VALUES (" + values + ");";

		return sql;
	}

	abstract protected void connectPanel();
	abstract protected void defineColumns();

}
