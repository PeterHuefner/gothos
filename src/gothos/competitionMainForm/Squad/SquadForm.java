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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
	private JLabel    helpTextLabel;
	private JButton   calcValuesOuter;
	private JButton   calcValuesInner;

	final static int INNER_CALC_METHOD = 1;
	final static int OUTER_CALC_METHOD = 2;

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

		calcValuesOuter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent actionEvent) {
				String selectedApparatus = apparatiSelect.getSelectedItem().toString();

			}
		});

		calcValuesInner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent actionEvent) {
				String selectedApparatus = apparatiSelect.getSelectedItem().toString();
			}
		});

		generateStatistics();
	}

	protected void generateStatistics() {
        competitionData.setSquad(squad);

        CompetitionStatistics stats = new CompetitionStatistics(competitionData);
        LinkedHashMap<String, Integer> counts = stats.getCountsForCols(new String[]{"club", "class"});

        squadInfoLabel.setText("Anzahl Aktive: " + stats.getGymnastCountInCurrentQuery() + " | Anzahl Vereine: " + counts.get("club") + " | Anzahl Alterklassen: " + counts.get("class"));
    }

    void calcValues (int calcMethod, String apparatus) {
		String sql =
				"SELECT " +
				"competition_" + Application.selectedCompetition + ".ROWID, " +
				apparatus + ", " +
				apparatus + "_d_value, " +
				apparatus + "_e_value, " +
				apparatus + "_additional_value_one, " +
				apparatus + "_additional_value_two, " +
				apparatus + "_additional_value_three, " +
				apparatus + "_additional_value_four " +
				"FROM competition_" + Application.selectedCompetition + " " +
				"LEFT JOIN competition_" + Application.selectedCompetition + "_apparati_" + apparatus +
				"ON competition_" + Application.selectedCompetition + ".ROWID = competition_" + Application.selectedCompetition + "_apparati_" + apparatus + ".gymnast " +
				"WHERE squad = ?;"
		;

		ArrayList<DatabaseParameter> params = new ArrayList<>();

		params.add(new DatabaseParameter(squad));

	    ResultSet resultSet = Application.database.query(sql, params);

	    try {

		    while (resultSet.next()) {
		    	String baseCol = apparatus + "_additional_value_";
				String[] cols = new String[]{"one", "two", "three", "four"};
		    	ArrayList<Float> eValues = new ArrayList<>();

				for (String col : cols) {
					if (!Common.emptyString(resultSet.getString(baseCol + col))) {
						eValues.add(resultSet.getFloat(baseCol + col));
					}
				}

				Collections.sort(eValues);

				if (eValues.size() == 3) {
					if (calcMethod == INNER_CALC_METHOD) {

					} else if (calcMethod == OUTER_CALC_METHOD) {

					}
				} else if (eValues.size() == 4) {

				}

		    }

		    resultSet.close();
	    } catch (SQLException e) {
	    	Common.printError(e);
	    }
    }
}
