package gothos.competitionMainForm.Squad;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.CompetitionStatistics;
import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.TableNavigator;
import gothos.WindowManager;
import gothos.competitionMainForm.CompetitionMainForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SquadForm {
	protected String              squad;
	protected SquadFormTableModel tableModel;
	protected TableNavigator      navigator;
	protected CompetitionData     competitionData;

	private JButton   backButton;
	private JPanel    panel;
	private JTable    squadTable;
	private JComboBox apparatiSelect;
    private JLabel    squadInfoLabel;

    public JPanel getPanel() {
		return panel;
	}

	public SquadForm(String squad) {

		this.squad = squad;

        competitionData = new CompetitionData();

		tableModel = new SquadFormTableModel(this.squad);
		squadTable.setModel(tableModel);
		navigator = new TableNavigator(squadTable);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
				//CompetitionMainForm.reloadData();
			}
		});

		apparatiSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedApparatus = apparatiSelect.getSelectedItem().toString();

				if (!Common.emptyString(selectedApparatus) && DatabaseAnalyse.listApparatiInCompetition().contains(selectedApparatus)) {
					tableModel.setApparatus(selectedApparatus);
				}
			}
		});

		apparatiSelect.removeAllItems();
		apparatiSelect.addItem("Gerät auswählen");
		for (String squadName : DatabaseAnalyse.listApparatiInCompetition()) {
			apparatiSelect.addItem(squadName);
		}

		generateStatistics();
	}

	protected void generateStatistics() {
        competitionData.setSquad(squad);

        CompetitionStatistics stats = new CompetitionStatistics(competitionData);
        LinkedHashMap<String, Integer> counts = stats.getCountsForCols(new String[]{"club", "class"});

        squadInfoLabel.setText("Anzahl Aktive: " + stats.getGymnastCountInCurrentQuery() + " | Anzahl Vereine: " + counts.get("club") + " | Anzahl Alterklassen: " + counts.get("class"));
    }
}
