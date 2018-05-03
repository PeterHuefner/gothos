package gothos.DatabaseCore;

import gothos.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DatabaseStructure {

	public static boolean createGlobalClasses(){
		return Application.database.execute(
				"CREATE TABLE IF NOT EXISTS global_classes (" +
						"class TEXT PRIMARY KEY," +
						"displayName TEXT," +
						"calculation TEXT," +
						"displayColumns TEXT," +
						"sumAll INTEGER," +
						"minApparati INTEGER" +
					 ");"
		);
	}

	public static boolean createSettings(){
		return Application.database.execute(
				"CREATE TABLE IF NOT EXISTS settings (" +
						"name TEXT PRIMARY KEY NOT NULL," +
						"value TEXT" +
					 ");"
		);
	}

	public static boolean createCompetitions(){
		return Application.database.execute(
				"CREATE TABLE IF NOT EXISTS competitions (" +
						"name TEXT PRIMARY KEY NOT NULL," +
						"longname TEXT," +
						"description TEXT," +
						"competitionDay TEXT," +
						"final INTEGER" +
					 ");"
		);
	}

	public static boolean createCompetition(String name) {
		Boolean competitionStatus =  Application.database.execute(
				"CREATE TABLE IF NOT EXISTS competition_" + name + " (" +
						"ID INTEGER," +
						"name TEXT," +
						"birthdate TEXT," +
						"class TEXT," +
						"club TEXT," +
						"squad TEXT," +
						"team TEXT," +
						"active INTEGER DEFAULT 1" +
						");"
		);

		Boolean classesStatus = Application.database.execute(
				"CREATE TABLE IF NOT EXISTS competition_" + name + "_classes (" +
						"class TEXT PRIMARY KEY," +
						"displayName TEXT," +
						"calculation TEXT," +
						"displayColumns TEXT," +
						"sumAll INTEGER," +
						"minApparati INTEGER" +
						");"
		);

		Boolean classesCopyStatus = Application.database.execute("INSERT INTO competition_" + name + "_classes (class, displayName, calculation, displayColumns, sumAll, minApparati) SELECT class, displayName, calculation, displayColumns, sumAll, minApparati FROM global_classes;");

		String[] apparati = new String[]{
				"Boden",
				"Pauschenpferd",
				"Ringe",
				"Sprung",
				"Barren",
				"Reck",
				"Balken",
				"Stufenbarren",
				"Pilz",
				"Minitramp"
		};

		Boolean allApparatiStatus = true;
		for(String apparatus: apparati){
			Boolean apparatusStatus = addApparatiToCompetition(name, apparatus);

			if(!apparatusStatus){
				allApparatiStatus = false;
			}
		}

		return competitionStatus && allApparatiStatus && classesStatus && classesCopyStatus;
	}

	public static boolean removeCompetition(String name) {
		DatabaseAnalyse analyse = new DatabaseAnalyse();

		Boolean allApparatiStatus = true;
		ArrayList<String> apparati = analyse.listApparatiInCompetition(name);
		for(String apparatus: apparati){
			Boolean apparatusStatus = removeApparatiFromCompetition(name, apparatus);

			if(!apparatusStatus){
				allApparatiStatus = false;
			}
		}

		Boolean classesStatus = Application.database.execute("DROP TABLE IF EXISTS competition_" + name + "_classes;");
		Boolean competitionStatus = Application.database.execute("DROP TABLE IF EXISTS competition_" + name + ";");

		ArrayList<DatabaseParameter> parameters = new ArrayList<>();
		parameters.add(new DatabaseParameter(name));
		Boolean competitionEntryStatus = Application.database.execute("DELETE FROM competitions WHERE name = ?", parameters);


		return competitionStatus && allApparatiStatus && classesStatus && competitionEntryStatus;
	}

	public static boolean addApparatiToCompetition(String competition, String apparatus) {
		return Application.database.execute(
				"CREATE TABLE IF NOT EXISTS competition_" + competition + "_apparati_" + apparatus + " (" +
						"gymnast INTEGER," +
						apparatus + " REAL," +
						"isTeamMember INTEGER DEFAULT 1" +
						");"
		);
	}

	public static boolean removeApparatiFromCompetition(String competition, String apparatus) {
		return Application.database.execute("DROP TABLE IF EXISTS competition_" + competition + "_apparati_" + apparatus + ";");
	}

	public static boolean removeGymnast(String rowid){
		return removeGymnast(rowid, Application.selectedCompetition);
	}

	public static boolean removeGymnast(String rowid, String competition){
		Boolean updateStatus;
		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(rowid));
		updateStatus = Application.database.execute("UPDATE competition_" + competition + " SET active = 0 WHERE ROWID = ?", params);

		DatabaseAnalyse analyse = new DatabaseAnalyse();
		ArrayList<String> apparati = analyse.listApparatiInCompetition(competition);
		for(String apparatus: apparati){
			Application.database.execute("DELETE FROM competition_" + competition + "_apparati_" + apparatus + " WHERE gymnast = ?", params);
		}

		return updateStatus;
	}
}
