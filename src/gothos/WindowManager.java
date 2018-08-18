package gothos;

import gothos.DatabaseCore.DatabaseParameter;
import gothos.Test.Pdftest;
import gothos.competitionMainForm.*;
import gothos.competitionMainForm.Classes.ViewClass;
import gothos.competitionMainForm.ConfigureApparaties.ConfigureApparaties;
import gothos.competitionMainForm.Export.ExportForm;
import gothos.competitionMainForm.Import.ImportForm;
import gothos.competitionMainForm.SetID.SetIDForm;
import gothos.competitionMainForm.Squad.SquadForm;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WindowManager {

	public static JFrame mainFrame;
	public static JFrame childFrame;

	protected static JPanel              startPanel;
	protected static CompetitionMainForm activeCompetition;


	public static void initiateVisuals(JPanel startPanel) {
		WindowManager.startPanel = startPanel;
		WindowManager.mainFrame = new JFrame("Gothos - dev state");

		WindowManager.mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		showStartPanel();
	}

	public static void createChildFrame(String title) {
		disposeChildFrame();

		childFrame = new JFrame(title);
		childFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static void disposeChildFrame() {
		if (childFrame != null) {
			childFrame.dispose();
		}
		childFrame = null;
	}

	public static void showCreateCompetition() {
		CompetitionForm competitionForm = new CompetitionForm();
		showPanel(competitionForm.getPanel());
	}

	public static void showCreateCompetition(String competition) {
		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(competition));
		ResultSet result = Application.database.query("SELECT ROWID FROM competitions WHERE name = ?", params);

		String primaryKey = "";

		try {
			while (result.next()) {
				primaryKey = result.getString("ROWID");
			}
			result.close();
		} catch (SQLException e) {
			Common.printError(e);
		}

		CompetitionForm competitionForm = new CompetitionForm(primaryKey);
		showPanel(competitionForm.getPanel());
	}

	public static void showGlobalClasses() {
		ConfigureClasses classes = new ConfigureClasses("global_classes");
		showPanel(classes.getPanel());
	}

	public static void showPanel(JPanel panel) {
		showPanelInFrame(panel, WindowManager.mainFrame);
	}

	public static void showPanelInFrame(JPanel panel, JFrame frame) {
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public static void showStartPanel() {

		if (activeCompetition != null) {
			activeCompetition.close();
			Application.selectedCompetition = null;
			activeCompetition = null;
		}

		WindowManager.mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.NORMAL);
		showPanel(WindowManager.startPanel);
		Start.instance.panelShowed();
	}

	public static void showCompetitionPanel() {
		if (activeCompetition == null) {
			activeCompetition = new CompetitionMainForm();
		}

		//mainFrame.get
		WindowManager.mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		showPanel(activeCompetition.getPanel());
	}

	public static void closeCompetition() {
		showStartPanel();
	}

	public static void showImport() {
		if (!Common.emptyString(Application.selectedCompetition)) {
			createChildFrame("Importieren");

			ImportForm importForm = new ImportForm();
			showPanelInFrame(importForm.getPanel(), childFrame);
		}
	}

	public static void shwoSetIds() {
		if (!Common.emptyString(Application.selectedCompetition)) {
			createChildFrame("IDs vergeben");

			SetIDForm form = new SetIDForm();
			showPanelInFrame(form.getPanel(), childFrame);
		}
	}

	public static void showExportForm(String mode) {
		if (!Common.emptyString(Application.selectedCompetition)) {
			createChildFrame("Exportieren");

			ExportForm form = new ExportForm(mode);
			showPanelInFrame(form.getPanel(), childFrame);
		}
	}

	public static void showLocalClasses() {
		if (!Common.emptyString(Application.selectedCompetition)) {
			createChildFrame("Altersklassen verwalten");
			ConfigureClasses classes = new ConfigureClasses("competition_" + Application.selectedCompetition + "_classes");
			classes.setOpenAsChildPanel(true);
			showPanelInFrame(classes.getPanel(), childFrame);
		}
	}

	public static void showConfigureApparati() {
		if (!Common.emptyString(Application.selectedCompetition)) {
			createChildFrame("Geräte verwalten");
			ConfigureApparaties apparati = new ConfigureApparaties();
			showPanelInFrame(apparati.getPanel(), childFrame);
		}
	}

	public static void showSquadForm(String squad) {
		if (!Common.emptyString(Application.selectedCompetition)) {
			createChildFrame("Riege " + squad + " ansehen/Wertungen eintragen");
			SquadForm squadForm = new SquadForm(squad);
			showPanelInFrame(squadForm.getPanel(), childFrame);
		}
	}

	public static void showViewClasses(String className) {
		if (!Common.emptyString(Application.selectedCompetition)) {
			createChildFrame("Alterklasse " + className + " ansehen");
			ViewClass viewClass = new ViewClass(className);
			showPanelInFrame(viewClass.getPanel(), childFrame);
		}
	}





	public static void showTest(){
		createChildFrame("TEST");
		Pdftest pdftest = new Pdftest();
		showPanelInFrame(pdftest.getPanel(), childFrame);
	}
}
