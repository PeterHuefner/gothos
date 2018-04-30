package gothos;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;

import java.util.ArrayList;

public class ConfigureClassesModel extends DataFormTableModel {

	public ConfigureClassesModel(String table){
		super();
		this.baseTable = table;
		displayColumns = new String[]{"Altersklasse", "Anzeigename", "Berechnung", "Geräte", "Summe aller Geräte", "maximal zu werten"};
		buildData("SELECT ROWID, * FROM " + baseTable + ";", new ArrayList<>());
	}

	public Class<?> getColumnClass(int columnIndex){
		if(columnIndex == 4) {
			return Boolean.class;
		}else if(columnIndex == 5){
			return Integer.class;
		}else{
			return String.class;
		}
	}
}
