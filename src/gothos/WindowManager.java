package gothos;

import gothos.DatabaseCore.DatabaseParameter;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WindowManager {

	public static JFrame mainFrame;

	protected static JPanel startPanel;

	public static void initiateVisuals(JPanel startPanel){
		WindowManager.startPanel = startPanel;
		WindowManager.mainFrame = new JFrame("Gothos - dev state");

		WindowManager.mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		showStartPanel();
	}

	public static void showCreateCompetition(){
		CompetitionForm competitionForm = new CompetitionForm();
		showPanel(competitionForm.getPanel());
	}

	public static void showCreateCompetition(String competition){
		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(competition));
		ResultSet result = Application.database.query("SELECT ROWID FROM competitions WHERE name = ?", params);

		String primaryKey = "";

		try{
			while (result.next()){
				primaryKey = result.getString("ROWID");
			}
			result.close();
		}catch (SQLException e){
			Common.printError(e);
		}

		CompetitionForm competitionForm = new CompetitionForm(primaryKey);
		showPanel(competitionForm.getPanel());
	}

	public static void showGlobalClasses(){
		ConfigureClasses classes = new ConfigureClasses("global_classes");
		showPanel(classes.getPanel());
	}

	public static void showPanel(JPanel panel){
		WindowManager.mainFrame.setContentPane(panel);
		WindowManager.mainFrame.pack();
		WindowManager.mainFrame.setVisible(true);
		WindowManager.mainFrame.setLocationRelativeTo(null);
	}

	public static void showStartPanel(){
		showPanel(WindowManager.startPanel);
		Start.instance.panelShowed();
	}

}
