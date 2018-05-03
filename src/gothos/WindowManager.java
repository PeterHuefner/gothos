package gothos;

import gothos.DatabaseCore.DatabaseParameter;
import gothos.competitionMainForm.CompetitionMainForm;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WindowManager {

	public static JFrame mainFrame;

	protected static JPanel startPanel;
	protected static CompetitionMainForm activeCompetition;

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

		if(activeCompetition != null){
			activeCompetition.close();
			Application.selectedCompetition = null;
			activeCompetition = null;
		}

		WindowManager.mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.NORMAL);
		showPanel(WindowManager.startPanel);
		Start.instance.panelShowed();
	}

	public static void showCompetitionPanel(){
		if(activeCompetition == null){
			activeCompetition = new CompetitionMainForm();
		}

		//mainFrame.get
		WindowManager.mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		showPanel(activeCompetition.getPanel());
	}

	public static void closeCompetition(){
		showStartPanel();
	}
}
