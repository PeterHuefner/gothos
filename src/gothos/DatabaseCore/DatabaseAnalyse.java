package gothos.DatabaseCore;

import gothos.Common;
import gothos.Application;
import gothos.Start;
import gothos.WindowManager;
import gothos.competitionMainForm.Certificates.PdfCertificate;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
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
			} else {
				insertCreatedSettings();
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
			} else if(!DatabaseStructure.insertDefaultCertificates()) {
				Common.printError("Fehler beim Einfügen der Default-Certificates");
			}
		}


		String version = Application.database.fetchFirstColumn("SELECT value FROM settings WHERE name = 'version'");
		if (Common.emptyString(version)) {
			version = "unbekannt";
		}

		if (!version.equals(Application.version)) {

			String dbCreated = Application.database.fetchFirstColumn("SELECT value FROM settings WHERE name = 'database created'");

			String[] options = new String[]{
					"ignorieren",
					"versuchen die Datenbank zu aktualisieren, Warnung wird bei Erfolg nicht mehr angezeigt",
					"in der Datenbank die aktuelle Version vermerken und somit diese Warnung nicht mehr anzeigen",
			};
			int selectedOption = JOptionPane.showOptionDialog(
					WindowManager.mainFrame,
					"Diese Datenbank wurde mit einer anderen Version von " + Application.name + " erstellt.\n\n" +
					"Es wird empfohlen eine neue Datenbank zu erstellen, vor allem wenn Sie einen Wettkampf durchführen.\n\n" +
					"Wenn Sie keine Daten ändern oder eingeben wollen, können Sie zunächst fortfahren. \n\n"+
					"Der Funktionsumfang von " + Application.name + " in so einem Fall, \n" +
					"hängt vom Unterschied der Versionen ab, die die Datenbank erstellt hat und der aktuell verwendeteten.\n\n" +
					"Die Datenbank wurde am " + dbCreated + " mit der Version " + version + " erstellt. Sie verwenden momentan " + Application.version + ".\n\n" +
					"Sie haben nun folgende Möglichkeiten:",
					"Versionskonflikt festgestellt",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					new ImageIcon(""),
					options,
					options[1]);


			switch (selectedOption) {
				case 0:

					break;
				case 1:

					if (upgradeDatabase(version)) {
						updateVersionInfo();
					}

					break;
				case 2:
					updateVersionInfo();
					break;
			}

		}
	}

	protected static void insertCreatedSettings() {
		LinkedHashMap<String, DatabaseParameter> values = new LinkedHashMap<>();
		values.put("name", new DatabaseParameter("version"));
		values.put("value", new DatabaseParameter(Application.version));
		Application.database.insertData("settings", values);

		values = new LinkedHashMap<>();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		Date today = Calendar.getInstance().getTime();
		values.put("name", new DatabaseParameter("database created"));
		values.put("value", new DatabaseParameter(df.format(today)));

		long insertCount = Application.database.insertData("settings", values);

		if (insertCount > 0) {
			Common.showMessage("Versionsnummer in der Datenbank erfolgreich gespeichert.");
		} else {
			Common.showError("Versionsnummer konnte nicht in der Datenbank gespeichert werden.");
		}
	}

	protected static void updateVersionInfo() {
		Application.database.execute("DELETE FROM settings WHERE name = 'version'");

		LinkedHashMap<String, DatabaseParameter> values = new LinkedHashMap<>();
		values.put("name", new DatabaseParameter("version"));
		values.put("value", new DatabaseParameter(Application.version));
		Application.database.insertData("settings", values);
	}

	protected static boolean upgradeDatabase(String oldVersion) {
		boolean status = true;

		return true;
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
