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

	public static boolean createGlobalCertificate() {
		return Application.database.execute(
				"CREATE TABLE IF NOT EXISTS global_certificates ( " +
						"line TEXT, " +
						"font TEXT, " +
						"size REAL, " +
						"height REAL, " +
						"weight TEXT, " +
						"align TEXT, " +
						"type TEXT " +
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
						"final INTEGER, " +
						"numberOfFinalists INTEGER DEFAULT 6, " +
						"teamCalculateMode INTEGER DEFAULT 0, " +
						"numberOfMaxTeamMembers INTEGER DEFAULT 0 " +
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

		Boolean certificateStatus =  Application.database.execute(
				"CREATE TABLE IF NOT EXISTS competition_" + name + "_certificates ( " +
						"line TEXT, " +
						"font TEXT, " +
						"size REAL, " +
						"height REAL, " +
						"weight TEXT, " +
						"align TEXT, " +
						"type TEXT " +
						");");

		Boolean certificateCopyStatus = Application.database.execute("INSERT INTO competition_" + name + "_certificates (line, font, size, height, weight, align, type) SELECT line, font, size, height, weight, align, type FROM global_certificates;");

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

		return competitionStatus && allApparatiStatus && classesStatus && classesCopyStatus && certificateStatus && certificateCopyStatus;
	}

	public static boolean insertDefaultCertificates() {
		return Application.database.execute(
				"INSERT INTO global_certificates\n" +
				"(line, font, \"size\", height, weight, align, \"type\")\n" +
				"VALUES\n" +
				"('8', NULL, NULL, NULL, NULL, NULL, 'teamTop'),\n" +
				"('4', NULL, NULL, NULL, NULL, NULL, 'teamLeft'),\n" +
				"('8', NULL, NULL, NULL, NULL, NULL, 'singleTop'),\n" +
				"('4', NULL, NULL, NULL, NULL, NULL, 'singleLeft'),\n" +
				"('$$NAME$$', 'Times New Roman', 28, 1, 'fett', 'zentriert', 'single'),\n" +
				"('vom $$CLUB$$', 'Times New Roman', 24, 3.5, 'normal', 'zentriert', 'single'),\n" +
				"('belegte mit $$POINTS$$ Punkten', 'Times New Roman', 24, 2.5, 'normal', 'zentriert', 'single'),\n" +
				"('in der $$CLASS$$', 'Times New Roman', 24, 2.5, 'normal', 'zentriert', 'single'),\n" +
				"('den $$RANKING$$. Platz', 'Times New Roman', 28, 3, 'fett', 'zentriert', 'single'),\n" +
				"('$$NAME$$', 'Times New Roman', 28, 1, 'fett', 'zentriert', 'team'),\n" +
				"('belegte mit $$POINTS$$ Punkten', 'Times New Roman', 24, 2.5, 'normal', 'zentriert', 'team'),\n" +
				"('den $$RANKING$$. Platz', 'Times New Roman', 28, 3, 'fett', 'zentriert', 'team');\n"
		);
	}

	public static boolean removeCompetition(String name) {
		Boolean allApparatiStatus = true;
		ArrayList<String> apparati = DatabaseAnalyse.listApparatiInCompetition(name);
		for(String apparatus: apparati){
			Boolean apparatusStatus = removeApparatiFromCompetition(name, apparatus);

			if(!apparatusStatus){
				allApparatiStatus = false;
			}
		}

		Boolean classesStatus = Application.database.execute("DROP TABLE IF EXISTS competition_" + name + "_classes;");
		Boolean certificatesStatus = Application.database.execute("DROP TABLE IF EXISTS competition_" + name + "_certificates;");
		Boolean competitionStatus = Application.database.execute("DROP TABLE IF EXISTS competition_" + name + ";");

		ArrayList<DatabaseParameter> parameters = new ArrayList<>();
		parameters.add(new DatabaseParameter(name));
		Boolean competitionEntryStatus = Application.database.execute("DELETE FROM competitions WHERE name = ?", parameters);


		return competitionStatus && allApparatiStatus && classesStatus && competitionEntryStatus && certificatesStatus;
	}

	public static boolean addApparatiToCompetition(String competition, String apparatus) {
		return Application.database.execute(
				"CREATE TABLE IF NOT EXISTS competition_" + competition + "_apparati_" + apparatus + " (" +
						"gymnast INTEGER PRIMARY KEY NOT NULL," +
						apparatus + " REAL," +
						apparatus + "_d_value REAL, " +
						apparatus + "_e_value REAL, " +
						apparatus + "_additional_value_one REAL, " +
						apparatus + "_additional_value_two REAL, " +
						apparatus + "_additional_value_three REAL, " +
						apparatus + "_additional_value_four REAL, " +
						"isTeamMember INTEGER DEFAULT 1," +
						"isFinalist INTEGER DEFAULT 0," +
						"isReplacer INTEGER DEFAULT 0" +
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

		ArrayList<String> apparati = DatabaseAnalyse.listApparatiInCompetition(competition);
		for(String apparatus: apparati){
			Application.database.execute("DELETE FROM competition_" + competition + "_apparati_" + apparatus + " WHERE gymnast = ?", params);
		}

		return updateStatus;
	}
}
