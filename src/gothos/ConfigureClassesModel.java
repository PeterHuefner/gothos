package gothos;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;

import java.sql.ResultSet;
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

	protected boolean checkValue(Object value, int row, int col) {
		boolean check = false;

		if (col == 3 || col == 4) {
			String sql;
			CompetitionData competitionData = new CompetitionData();

			competitionData.setAllApparaties(true);

			if (col == 3) {
				competitionData.setCalculation(value.toString());

				sql = competitionData.getSql();

				ResultSet resultSet = Application.database.query(sql);

				if (resultSet != null) {
					check = true;
				} else {
					check = false;
					Common.showMessage("Der Ausdruck '" + value.toString() + "' ist fehlerhaft. Entweder kann er mathematisch nicht interpretiert werden oder es wurden Geräte verwendet die nicht angelegt sind.");
				}
			} else if (col == 4) {
				competitionData.setColums(new String[]{value.toString()});

				sql = competitionData.getSql();

				ResultSet resultSet = Application.database.query(sql);

				if (resultSet != null) {
					check = true;
				} else {
					check = false;
					Common.showMessage("Die Geräteliste '" + value.toString() + "' ist fehlerhaft. Entweder sind nicht angelegte Geräte verwendet worden oder ungültige Zeichen. Geben Sie nur eine mit Komma separierte Liste der Geräte an.");
				}
			}
		} else {
			check = true;
		}


		return check;
	}
}
