package gothos;

import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;

import java.util.ArrayList;

public class ConfigureClassesModel extends DataFormTableModel {

	public void setBaseTable(String table){
		this.baseTable = table;
	}

	public ConfigureClassesModel(){
		super();
		this.baseTable = "global_classes";
		buildData("SELECT ROWID, * FROM global_classes;", new ArrayList<>());
	}
}
