package gothos;

import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataForm;
import gothos.FormCore.DataFormElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
				WindowManager.showStartPanel();
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

	public boolean check(){
		boolean status = true;

		if(this.name.getText().isEmpty()){
			status = false;
			Common.showFormError("Der Wettkampfkenner darf nicht leer sein.");
		}else if(this.primaryKey == null || this.primaryKey.isEmpty()){
			ArrayList<DatabaseParameter> param = new ArrayList<>();
			param.add(new DatabaseParameter(this.name.getText()));
			String res = Application.database.fetchFirstColumn("SELECT COUNT(*) FROM competitions WHERE name = ?;", param);

			Integer count = Integer.parseInt(res);

			if(count != 0){
				status = false;
				Common.showFormError("Der Wettkampfkenner darf nicht mehrfach vergeben werden.");
			}
		}

		return status;
	}
}
