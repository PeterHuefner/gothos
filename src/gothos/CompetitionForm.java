package gothos;

import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.DatabaseCore.DatabaseStructure;
import gothos.DatabaseCore.TransferDataForFinalCompetition;
import gothos.FormCore.DataForm;
import gothos.FormCore.DataFormElement;
import gothos.FormCore.SelectboxItem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CompetitionForm extends DataForm{
	private JPanel     competitionFormPanel;
	private JTextField name;
	private JLabel     competitionNameLabel;
	private JButton    saveDataButton;
	private JButton    cancelButton;
	private JTextField longname;
	private JLabel     competitionLongnameLabel;
	private JTextField description;
	private JLabel     competitionDescriptionLabel;
	private JTextField competitionDay;
	private JLabel     competitionDayLabel;
	//private JComboBox  teamCalculateMode;
	private JLabel     numberOfMaxTeamMembersLabel;
	private JTextField numberOfMaxTeamMembers;
	private JLabel     isFinalLabel;
	private JComboBox  referenceCompetitions;
	private JLabel     isFinalHelp;
	private JTextField numberOfFinalists;
	private JTextField numberOfReplacers;

	private final String referenceCompetitionPlaceholder = "Kein Finalwettkampf erstellen";

	public CompetitionForm(){
		super("competitions");

		ini();
	}

	public CompetitionForm(String competition){
		super("competitions", competition);

		ini();

		load();
	}

	private void createUIComponents() {
		/*teamCalculateMode = new JComboBox(new Object[]{
				new SelectboxItem(0, "Summe aller Wertungen"),
				new SelectboxItem(1, "Summe aller Ergebnisse")
		});*/


	}

	protected void ini(){
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

		listCompetitions();

		if(!Common.emptyString(primaryKey)){
			name.setEnabled(false);
		} else {
			referenceCompetitions.setEnabled(false);
			numberOfFinalists.setEnabled(false);
			numberOfReplacers.setEnabled(false);
		}
	}

	protected void connectPanel(){
		this.panel = this.competitionFormPanel;
	}

	protected void defineColumns(){
		columns.add(new DataFormElement(this.name, "name"));
		columns.add(new DataFormElement(this.longname, "longname"));
		columns.add(new DataFormElement(this.description, "description"));
		columns.add(new DataFormElement(this.competitionDay, "competitionDay"));
		//columns.add(new DataFormElement(this.teamCalculateMode, "teamCalculateMode"));
		columns.add(new DataFormElement(this.numberOfMaxTeamMembers, "numberOfMaxTeamMembers"));
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

		if(!this.name.getText().matches(Common.tableNameReqex)){
			Common.showError("Der Wettkampfkenner muss mit einem Buchstaben beginnen und darf nur aus Buchstaben, Zahlen und Unterstrichen bestehen.\nUmlaute und ß sind nicht verwendbar.");
			status = false;
		}

		return status;
	}

	@Override
	public boolean save() {
		Boolean addCompetition = false;

		if(Common.emptyString(primaryKey)){
			addCompetition = true;
		}

		Boolean status = super.save();

		if(!Common.emptyString(primaryKey)){
			name.setEnabled(false);
		}

		if(addCompetition && status){
			DatabaseStructure.createCompetition(name.getText());

			String referenceCompetition = (String) referenceCompetitions.getSelectedItem();
			if (Common.emptyString(referenceCompetition) || referenceCompetition.equals(referenceCompetitionPlaceholder)) {
				TransferDataForFinalCompetition transfer = new TransferDataForFinalCompetition(name.getText(), referenceCompetition);
				//transfer.setFinalistsCount();
				//transfer.setReplacersCount();
			}
		}

		return status;
	}

	private void listCompetitions() {
		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(1));

		referenceCompetitions.addItem(referenceCompetitionPlaceholder);

		ResultSet         result       = Application.database.query("SELECT name FROM competitions WHERE final != ?;", params);
		try {
			while (result.next()) {
				referenceCompetitions.addItem(result.getString("name"));
			}
			result.close();
		} catch (SQLException e) {
			Common.printError(e);
		}
	}
}
