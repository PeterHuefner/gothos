package gothos;

import gothos.FormCore.DataForm;
import gothos.FormCore.DataFormElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompetitionForm extends DataForm{
	private JPanel competitionFormPanel;
	private JTextField competitionName;
	private JLabel competitionNameLabel;
	private JButton saveDataButton;
	private JButton cancelButton;

	public CompetitionForm(){
		super();

		connectPanel();
		defineColumns();

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Start.showStartPanel();
			}
		});
	}

	protected void connectPanel(){
		this.panel = this.competitionFormPanel;
	}

	protected void defineColumns(){
		columns.add(new DataFormElement(this.competitionName, "competitionName"));
	}
}
