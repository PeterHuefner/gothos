package gothos.FormCore;

import gothos.DatabaseCore.DatabaseParameter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

abstract public class DataForm {

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

	public boolean save(){
		boolean status = true;



		return status;
	}

	protected HashMap<String, DatabaseParameter> createParams(){
		HashMap<String, DatabaseParameter> params = new HashMap<String, DatabaseParameter>();

		return params;
	}

	abstract protected void connectPanel();
	abstract protected void defineColumns();

}
