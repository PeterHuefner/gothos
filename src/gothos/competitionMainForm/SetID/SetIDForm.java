package gothos.competitionMainForm.SetID;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.SelectboxItem;
import gothos.WindowManager;
import gothos.competitionMainForm.CompetitionMainForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SetIDForm {
	private JButton   backButton;
	private JComboBox sortBox1;
	private JComboBox sortBox2;
	private JComboBox sortBox3;
	private JComboBox sortBox4;
	private JComboBox sortBox5;
	private JButton   setIdButton;
	private JPanel    panel;

	protected JComboBox[] boxes;

	public JPanel getPanel() {
		return panel;
	}

	public SetIDForm() {

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CompetitionMainForm.reloadData();
				WindowManager.disposeChildFrame();
			}
		});

		setIdButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setIds();
				backButton.doClick();
			}
		});
	}

	protected void setIds(){

		String sql = "SELECT ROWID FROM competition_" + Application.selectedCompetition + " WHERE active = 1 ";
		String order = "ORDER BY ";

		for(JComboBox box: boxes) {
			SelectboxItem item = (SelectboxItem) box.getSelectedItem();
			String selectedKey = (String) item.getKey();

			if(!Common.emptyString(selectedKey)){
				sql += order + selectedKey;
				order = ",";
			}
		}

		ResultSet rows = Application.database.query(sql);
		ArrayList<Integer> rowids = new ArrayList<>();
		try {
			while (rows.next()){
				rowids.add(rows.getInt("ROWID"));
			}
			rows.close();
		}catch (SQLException e){}

		for(int i = 0; i < rowids.size(); i++) {
			ArrayList<DatabaseParameter> parameters = new ArrayList<>();
			parameters.add(new DatabaseParameter((i + 1)));
			parameters.add(new DatabaseParameter(rowids.get(i)));
			Application.database.execute("UPDATE competition_" + Application.selectedCompetition + " SET ID = ? WHERE ROWID = ?;", parameters);
		}
	}

	protected Object[] createItems(Integer boxNumber){
		return  new Object[]{
				new SelectboxItem("", boxNumber + ". Sortierung"),
				new SelectboxItem("name", "Name"),
				new SelectboxItem("class", "Altersklasse"),
				new SelectboxItem("club", "Verein"),
				new SelectboxItem("team", "Mannschaft"),
				new SelectboxItem("squad", "Riege"),
		};
	}

	private void createUIComponents() {


		/*for(int i = 0; i < boxes.length; i++){
			boxes[i] = new JComboBox(createItems((i+1)));
		}*/

		sortBox1 = new JComboBox(createItems(1));
		sortBox2 = new JComboBox(createItems(2));
		sortBox3 = new JComboBox(createItems(3));
		sortBox4 = new JComboBox(createItems(4));
		sortBox5 = new JComboBox(createItems(5));

		boxes = new JComboBox[]{
				sortBox1,
				sortBox2,
				sortBox3,
				sortBox4,
				sortBox5
		};
	}
}
