package gothos.competitionMainForm.Squad;

import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.FormCore.DataFormTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SquadFormTableModel extends DataFormTableModel {

	protected static SquadFormTableModel instance;

	protected JTable table;

	protected String squad;
	protected String apparatus;

	protected SquadForm squadForm;

	protected boolean currentlyInAutoCalculation = false;
	protected int calcMethod = 0;

	final static int NO_CALC_METHOD    = 0;
	final static int INNER_CALC_METHOD = 1;
	final static int OUTER_CALC_METHOD = 2;

	public void setTable(JTable table) {
		this.table = table;
		this.table.setDefaultRenderer(String.class, new CustomRenderer());
	}

	public void setSquadForm(SquadForm squadForm) {
		this.squadForm = squadForm;
	}

	public void setCalcMethod(int calcMethod) {
		this.calcMethod = calcMethod;
	}

	public static SquadFormTableModel getInstance() {
		return instance;
	}

	public SquadFormTableModel(String squad) {
		super();

		this.squad = squad;

		SquadFormTableModel.instance = this;

		iniTable();
	}

	public String getApparatus() {
		return apparatus;
	}

	public void setApparatus(String apparatus) {
		this.apparatus = apparatus;
		iniTable();
		fireTableStructureChanged();
		callListeners();
		autoCalcAll();
	}

	public void iniTable() {

		String                       sql;
		ArrayList<DatabaseParameter> parameters;
		ArrayList<String>            cols        = new ArrayList<>();
		ArrayList<String>            displayCols = new ArrayList<>();

		cols.add("competition_" + Application.selectedCompetition + ".ROWID");
		cols.add("ID");
		cols.add("name");
		cols.add("class");
		cols.add("club");
		cols.add("team");

		displayCols.add("ID");
		displayCols.add("Name");
		displayCols.add("Altersklasse");
		displayCols.add("Verein");
		displayCols.add("Mannschaft");

		CompetitionData competitionData = new CompetitionData();

		competitionData.setSquad(squad);

		if (!Common.emptyString(apparatus)) {
			ArrayList<String> apparatusList = new ArrayList<>();
			apparatusList.add(apparatus);
			competitionData.setApparaties(apparatusList);

			displayCols.add("Wertung für Mannschaft");
			displayCols.add(apparatus);
			displayCols.add("berechneter Wert");
			displayCols.add("D-Wert");
			displayCols.add("E-Wert");
			displayCols.add("E-Wert Kari 1");
			displayCols.add("E-Wert Kari 2");
			displayCols.add("E-Wert Kari 3");
			displayCols.add("E-Wert Kari 4");

			cols.add("IFNULL(isTeamMember, 1) AS isTeamMember");
			cols.add(apparatus);
			cols.add("0 AS calcVal");
			cols.add(apparatus + "_d_value");
			cols.add(apparatus + "_e_value");
			cols.add(apparatus + "_additional_value_one");
			cols.add(apparatus + "_additional_value_two");
			cols.add(apparatus + "_additional_value_three");
			cols.add(apparatus + "_additional_value_four");
		}

		competitionData.setColums(cols.toArray(new String[cols.size()]));

		competitionData.setOrderBy("ID ASC");

		sql = competitionData.getSql();
		parameters = competitionData.getParameters();

		displayColumns = displayCols.toArray(new String[displayCols.size()]);

		buildData(sql, parameters);
	}

	@Override
	protected boolean editableColumn(int row, int col) {
		boolean editable = super.editableColumn(row, col);

		if (col == 8) {
			editable = false;
		} else if (editable && col > 5) {
			editable = true;
		} else {
			editable = false;
		}

		return editable;
	}

	@Override
	protected String getTableForColumn(Integer col) {
		if (col < 5) {
			return baseTable;
		} else {
			return "competition_" + Application.selectedCompetition + "_apparati_" + apparatus;
		}
	}

	@Override
	protected String getPrimaryKeyColumnForColumn(Integer col) {
		if (col < 5) {
			return "ROWID";
		} else {
			return "gymnast";
		}

	}

	@Override
	protected Object handleValuePreInsert(Object value, int row, int col) {

		if (col == 7 || col >= 8) {
			String apparatiValue = value.toString();
			apparatiValue = apparatiValue.replaceAll(",", ".");
			if (apparatiValue.isEmpty()) {
				apparatiValue = null;
			}
			value = apparatiValue;
		}

		return value;
	}

	@Override
	protected boolean checkValue(Object value, int row, int col) {
		boolean status = true;

		if ((col == 7 || col >= 8) && value != null) {
			Matcher matcher = Pattern.compile("^\\d+(\\.\\d+)?$").matcher(value.toString());
			if (!matcher.find()) {
				status = false;
				Common.showError("'" + value.toString() + "' ist keine gültige Wertung.");
			}
		}

		// col == 8 ist berechneter Wert, der darf nicht in die DB
		if (col == 8) {
			status = false;
		}

		return status;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		int updateCol = col;
		super.setValueAt(value, row, col);
		if (rowidSkipped && col >= rowidIndex) {
			col++;
		}

		if (!currentlyInAutoCalculation) {
			currentlyInAutoCalculation = true;
			autoCalcRow(row);
			currentlyInAutoCalculation = false;
		}

		// wenn col == 7 berechneter Wert dann extra Events feuern, da das nicht per DB Update passiert
		if (col == 8) {
			tableData.get(row).get(col).setSelfUpdate(false);
			tableData.get(row).get(col).setValue(value);

			fireTableCellUpdated(row, updateCol);
			callListeners();
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 5) {
			return Boolean.class;
		} else if (columnIndex == 6) {
			return String.class;
		} else {
			return String.class;
		}
	}

	public void autoCalcRow(int row) {
		int startEValueCol        = 10;
		int endEValueCol          = 13;
		int targetEValueCol       = 9;
		int dValueCol             = 8;
		int targetOverallValueCol = 7;

		if (calcMethod != NO_CALC_METHOD) {

			ArrayList<Double> eValues = new ArrayList<>();

			for (int i = startEValueCol; i <= endEValueCol; i++) {
				Object cellValue = getValueAt(row, i);

				if (table.getEditorComponent() != null && table.getEditingColumn() == i) {
					try {
						JTextField textField = (JTextField) table.getEditorComponent();
						cellValue = textField.getText();
					} catch (Exception e) {

					}
				}

				try {
					if (cellValue instanceof Double) {
						eValues.add((Double) cellValue);
					} else {
						eValues.add(Common.parseDouble(cellValue));
					}
				} catch (Exception e) {

				}
			}

			Collections.sort(eValues);

			if (eValues.size() == 3) {
				if (calcMethod == OUTER_CALC_METHOD) {
					eValues.remove(1);
				} else {
					if (eValues.get(1) - eValues.get(0) > eValues.get(2) - eValues.get(1)) {
						eValues.remove(0);
					} else {
						eValues.remove(2);
					}
				}
			} else if (eValues.size() == 4) {
				eValues.remove(3);
				eValues.remove(0);
			}

			setValueAt(average(eValues), row, targetEValueCol);
		}

		Double overAllValue = 0.;
		Double eValue;
		Double dValue;

		try {
			eValue = Double.parseDouble(getValueAt(row, targetEValueCol).toString());
			dValue = Double.parseDouble(getValueAt(row, dValueCol).toString());
			overAllValue = eValue + dValue;
			setValueAt(overAllValue, row, targetOverallValueCol);
		} catch (Exception e) {
		}
	}

	protected Double average(ArrayList<Double> list) {
		Double average = 0.0;

		if (list.size() > 0) {
			for(Double element : list) {
				average += element;
			}

			average = average / list.size();
		}

		return average;
	}

	public void autoCalcAll() {
		for(int i = 0; i < table.getRowCount(); i++) {
			autoCalcRow(i);
		}
	}

	public boolean checkRowApparatiValues(int row) {
		boolean valuesEqual = true;
		Double calcedValue;
		Double enteredValue;

		try {
			if (!Common.emptyString(getValueAt(row, 7).toString()) && !Common.emptyString(getValueAt(row, 6).toString())) {
				calcedValue = Double.parseDouble(getValueAt(row, 7).toString());
				enteredValue = Double.parseDouble(getValueAt(row, 6).toString());

				if (calcedValue > 0. && enteredValue > 0. && calcedValue.compareTo(enteredValue) != 0) {
					valuesEqual = false;
				}
			}
		} catch (Exception e) {}

		return valuesEqual;
	}

	private static class CustomRenderer extends DefaultTableCellRenderer {

		Color backgroundColor = getBackground();

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column == 7 && SquadFormTableModel.getInstance().squadForm.isApparatiSelected() && !SquadFormTableModel.getInstance().checkRowApparatiValues(row)) {
				cell.setBackground(Color.RED);
			} else if (!isSelected) {
				cell.setBackground(backgroundColor);
			}

			return cell;
		}
	}
}
