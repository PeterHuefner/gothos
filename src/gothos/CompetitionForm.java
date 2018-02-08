package gothos;

import gothos.FormCore.DataForm;
import gothos.FormCore.DataFormElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompetitionForm extends DataForm{
	private JPanel competitionFormPanel;
	private JTextField name;
	private JLabel competitionNameLabel;
	private JButton saveDataButton;
	private JButton cancelButton;
	private JTextField longname;
	private JLabel competitionLongnameLabel;
	private JTextField description;
	private JLabel competitionDescriptionLabel;
	private JTextField competitionDay;
	private JLabel competitionDayLabel;

	public CompetitionForm(){
		super("competitions");

		connectPanel();
		defineColumns();

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Start.showStartPanel();
			}
		});
		saveDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
	}

	protected void connectPanel(){
		this.panel = this.competitionFormPanel;
	}

	protected void defineColumns(){
		columns.add(new DataFormElement(this.name, "name"));
		columns.add(new DataFormElement(this.longname, "longname"));
		columns.add(new DataFormElement(this.description, "description"));
		columns.add(new DataFormElement(this.competitionDay, "competitionDay"));
	}
}
