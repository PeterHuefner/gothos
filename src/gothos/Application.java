package gothos;

import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.DatabaseCore.SqliteConnection;

import javax.swing.*;
import java.io.File;
import java.util.prefs.Preferences;

public class Application {

	public static final String name    = "Gothos";
	public static final String version = "0.9";

	public static String dbVersion = "";

	public static SqliteConnection database;
	public static String           selectedCompetition = "";

	public static void initiate() {
		Application.database = null;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Application.disconnectDatabase();
			}
		});
	}

	public static boolean connectToDatabase(String file) {
		try {
			File f = new File(file);
			if(!f.exists() && !f.isDirectory()) {
				f.createNewFile();
			}

			Application.database = new SqliteConnection(file);
			DatabaseAnalyse.checkDatabase();
		} catch (Exception e) {
			System.out.println("keine Verbindung hergestellt");
			JOptionPane.showConfirmDialog(WindowManager.mainFrame, "Datenbank könnte nicht geöffnet werden");
			JOptionPane.showMessageDialog(WindowManager.mainFrame, "Datenbank könnte nicht geöffnet werden");
		}

		if (Application.database != null) {
			Preferences preferences = Preferences.userNodeForPackage(Start.class);
			preferences.put("database", file);
		}

		return (Application.database != null);
	}

	public static void disconnectDatabase() {
		if (Application.database != null) {
			Application.database.close();
		}
	}

	public static void exitApplication() {
		WindowManager.disposeChildFrame();
		WindowManager.mainFrame.dispose();

		Application.disconnectDatabase();

		Runtime.getRuntime().exit(0);
	}
}
