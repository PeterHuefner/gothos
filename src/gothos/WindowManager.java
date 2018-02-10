package gothos;

import javax.swing.*;

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
