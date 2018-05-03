package gothos.competitionMainForm;

import gothos.Application;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;
import gothos.FormCore.DataTableCell;

import javax.swing.event.TableModelEvent;
import java.util.ArrayList;

public class GymnastTableModel extends DataFormTableModel {

	public GymnastTableModel(){

		super();
		this.baseTable = "competition_" + Application.selectedCompetition;
		displayColumns = new String[]{"ID", "Name", "Geburtsdatum", "Alterklasse", "Verein", "Riege", "Mannschaft"};
		buildData("SELECT ROWID, ID, Name, birthdate, class, club, squad, team FROM " + baseTable + " WHERE active = 1;", new ArrayList<>());

	}

	@Override
	public void deleteRow(int row) {
		ArrayList<DataTableCell> cells = tableData.get(row);
		if(cells.size() > 0){
			ArrayList<DatabaseParameter> params = new ArrayList<>();
			params.add(new DatabaseParameter(cells.get(0).getPrimaryKey()));
			Application.database.execute("UPDATE competition_" + Application.selectedCompetition + " SET active = 0 WHERE ROWID = ?", params);
		}

		tableData.remove(row);
		newDataAvailable(new TableModelEvent(this));
	}
}
