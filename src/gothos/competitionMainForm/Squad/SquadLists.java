package gothos.competitionMainForm.Squad;

import gothos.Common;
import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.Notification;
import gothos.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SquadLists {
	private JPanel  panel;
	private JButton backButton;
	private JPanel  apparatiPanel;
	private JButton printButton;
	private JButton menOlympic;
	private JButton womenOlympic;
	private JButton pdfButton;

	protected String squad;
	protected LinkedHashMap<String, JCheckBox> apparatiCheckboxes = new LinkedHashMap<>();

	public JPanel getPanel() {
		return panel;
	}

	public SquadLists(String squad) {
		this.squad = squad;

		Notification.addOnce("frameShowed", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addApparati();
				JFrame frame = (JFrame) e.getSource();
				frame.pack();
			}
		});

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});

		pdfButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfSquadLists lists = prepareList();
				if (lists != null) {
					lists.saveDialog();
				}
			}
		});

		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfSquadLists lists = prepareList();
				if (lists != null) {
					lists.print();
				}
			}
		});

		menOlympic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Map.Entry<String, JCheckBox> apparatus: apparatiCheckboxes.entrySet()) {
					switch (apparatus.getKey().toLowerCase()) {
						case "boden" :
						case "pauschenpferd" :
						case "pferd" :
						case "ringe" :
						case "sprung" :
						case "barren" :
						case "reck" :
							apparatus.getValue().setSelected(true);
							break;
						default:
							apparatus.getValue().setSelected(false);
					}
				}
			}
		});

		womenOlympic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Map.Entry<String, JCheckBox> apparatus: apparatiCheckboxes.entrySet()) {
					switch (apparatus.getKey().toLowerCase()) {
						case "boden" :
						case "sprung" :
						case "balken" :
						case "stufenbarren" :
						case "stuba" :
							apparatus.getValue().setSelected(true);
							break;
						default:
							apparatus.getValue().setSelected(false);
					}
				}
			}
		});
	}

	protected void addApparati() {

		ArrayList<String> apparatiList = DatabaseAnalyse.listApparatiInCompetition();

		apparatiPanel.setLayout(new BoxLayout(apparatiPanel, BoxLayout.Y_AXIS));

		for (String apparatus: apparatiList) {
			Common.systemoutprintln(apparatus);
			JCheckBox checkBox = new JCheckBox(apparatus);

			apparatiPanel.add(checkBox);

			apparatiCheckboxes.put(apparatus, checkBox);
		}

		apparatiPanel.revalidate();
		apparatiPanel.repaint();

	}

	protected PdfSquadLists prepareList() {
		PdfSquadLists list = new PdfSquadLists(squad);

		ArrayList<String> apparati = new ArrayList<>();

		for (Map.Entry<String, JCheckBox> apparatus: apparatiCheckboxes.entrySet()) {
			if (apparatus.getValue().isSelected()) {
				apparati.add(apparatus.getKey());
			}
		}

		if (apparati.size() == 0) {
			Common.showError("Sie haben keine Geräte ausgewählt");

			return null;
		} else {
			list.generatePdf(apparati);
			return list;
		}
	}
}
