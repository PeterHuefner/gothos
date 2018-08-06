package gothos.competitionMainForm;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.DatabaseCore.DatabaseStructure;
import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConfigureApparaties {
	private JButton backButton;
	private JList   apparatiList;
	private JButton createApparatus;
	private JButton deleteApparatus;
	private JPanel panel;

	public JPanel getPanel() {
		return panel;
	}

	public ConfigureApparaties() {
		listApparati();

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
				CompetitionMainForm.reloadData();
			}
		});

		createApparatus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String apparatus = JOptionPane.showInputDialog(WindowManager.childFrame, "Benennen Sie das neue Gerät.");

				if (apparatus.matches(Common.tableNameReqex)) {
					DatabaseStructure.addApparatiToCompetition(Application.selectedCompetition, apparatus);
					listApparati();
				} else {
					Common.showError("Gerätenamen müssen mit einem Buchstaben beginnen und dürfen nur aus Buchstaben, Zahlen und Unterstrichen bestehen.\nUmlaute und ß sind nicht verwendbar.");
				}
			}
		});

		deleteApparatus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedApparatus = "";
				if(apparatiList.getSelectedValue() != null){
					selectedApparatus = apparatiList.getSelectedValue().toString();
				}

				if(!Common.emptyString(selectedApparatus)){
					Integer userChoice = JOptionPane.showConfirmDialog(WindowManager.childFrame, "Soll das Gerät " + selectedApparatus + " gelöscht werden? \nAlle Wertungen aller Teilnehmer für dieses Gerät werden ebenfalls gelöscht. \nDie Altersklassen die dieses Gerät verwenden müssen angepasst werden.", "Löschen bestätigen", JOptionPane.YES_NO_OPTION);

					if(userChoice == JOptionPane.YES_OPTION){
						DatabaseStructure.removeApparatiFromCompetition(Application.selectedCompetition, selectedApparatus);
						listApparati();
					}
				}
			}
		});
	}

	protected void listApparati() {
		apparatiList.clearSelection();
		apparatiList.setListData(DatabaseAnalyse.listApparatiInCompetition().toArray());
	}
}
