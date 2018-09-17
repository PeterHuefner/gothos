package gothos.DatabaseCore;

import gothos.Common;
import gothos.Application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseAnalyse {

	public static void checkDatabase() {

		/*boolean st0 = Application.database.execute("INSERT INTO test(col1, col2) values ('one', 'two');");
		long id = Application.database.getLastInsertId();
		boolean st1 = Application.database.execute("INSERT OR IGNORE INTO test(ROWID,col1)values(10,\"asd\");");

		long id2 = Application.database.getLastInsertId();*/

		ArrayList<String> tables = listTables();

		//Check if settings table exists
		if (!tables.contains("global_classes")) {
			if (!DatabaseStructure.createGlobalClasses()) {
				Common.printError("Fehler beim Anlegen der global_classes");
			}
		}

		if (!tables.contains("settings")) {
			if (!DatabaseStructure.createSettings()) {
				Common.printError("Fehler beim Anlegen der settings");
			}
		}

		if (!tables.contains("competitions")) {
			if (!DatabaseStructure.createCompetitions()) {
				Common.printError("Fehler beim Anlegen der competitions");
			}
		}

		if (!tables.contains("global_certificates")) {
			if (!DatabaseStructure.createGlobalCertificate()) {
				Common.printError("Fehler beim Anlegen der global_certificates");
			}
		}

	}

	public static void checkCompetition(String competition) {

	}

	public static ArrayList<String> listTables() {
		return listTables("");
	}

	public static ArrayList<String> listTables(String matchRegex) {
		ArrayList<String> tables = new ArrayList<String>();

		ResultSet rs = Application.database.query("SELECT name FROM sqlite_master WHERE type = 'table';");

		try {
			while (rs.next()) {
				if (matchRegex.isEmpty()) {
					tables.add(rs.getString("name"));
				} else if (rs.getString("name").matches(matchRegex)) {
					tables.add(rs.getString("name"));
				}
			}
			rs.close();
		} catch (SQLException e) {
			Common.printError(e);
		}

		return tables;
	}

	public static ArrayList<String> listApparatiInCompetition() {
		return listApparatiInCompetition(Application.selectedCompetition);
	}

	public static ArrayList<String> listApparatiInCompetition(String competition) {
		ArrayList<String> tables   = listTables("^competition_" + competition + "_apparati_.+$");
		ArrayList<String> apparati = new ArrayList<String>();

		for (String table : tables) {
			Matcher matcher = Pattern.compile("^.+_apparati_(.+)$").matcher(table);
			if (matcher.find()) {
				apparati.add(matcher.group(1));
			}
		}

		return apparati;
	}
}
