package gothos;

import gothos.FormCore.DataFormTableModel;

import java.util.ArrayList;

public class ConfigureCertificatesTableModel extends DataFormTableModel {

	public ConfigureCertificatesTableModel(String table) {
		super();

		this.baseTable = table;
			displayColumns = new String[]{"Zeile", "Schrift", "Größe", "Stil", "Ausrichtung"};
		buildData("SELECT ROWID, * FROM " + baseTable + ";", new ArrayList<>());
	}
}
