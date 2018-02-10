package gothos;

import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.DatabaseCore.SqliteConnection;

import javax.swing.*;

public class Application {

	public static SqliteConnection database;
	public static String selectedCompetition = "";

	public static void initiate(){
		Application.database = null;

		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override public void run(){
				if(Application.database != null){
					Application.database.close();
				}
			}
		});
	}

	public static boolean connectToDatabase(String file){
		try {
			Application.database = new SqliteConnection(file);
			DatabaseAnalyse analyse = new DatabaseAnalyse();
			analyse.checkDatabase();
		}catch (Exception e){
			System.out.println("keine Verbindung hergestellt");
			JOptionPane.showConfirmDialog(WindowManager.mainFrame, "Datenbank könnte nicht geöffnet werden");
			JOptionPane.showMessageDialog(WindowManager.mainFrame, "Datenbank könnte nicht geöffnet werden");
		}

		return (Application.database != null);
	}
}
