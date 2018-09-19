package gothos;

import gothos.DatabaseCore.DatabaseParameter;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigureCertificates {
	private JPanel     panel;
	private JButton    backButton;
	private JTable     certificateTable;
	private JButton    newLineButton;
	private JButton    removeLineButton;
	private JLabel     helpLabel;
	private JLabel     targetLabel;
	private JTextField marginTop;
	private JTextField marginLeft;
	private JTextField marginRight;

	protected String                          tableName;
	protected String                          type;
	protected ConfigureCertificatesTableModel tableModel;

	public JPanel getPanel() {
		return panel;
	}

	public ConfigureCertificates(String tableName, String type) {
		this.tableName = tableName;
		this.type = type;

		tableModel = new ConfigureCertificatesTableModel(tableName, type);
		certificateTable.setModel(tableModel);

		setCellEditors();

		if (tableName.equals("global_certificates")) {
			targetLabel.setText("<html>Sie bearbeiten die Urkunden als Vorlage für neue Wettkämpfe.<br>Urkunden bereits erstellter Wettkämpfe werden nicht verändert.</html>");
		} else {
			targetLabel.setText("<html>Sie bearbeiten die Urkunden für den aktuell ausgewählten Wettkampf.<br>Änderungen wirken sich nicht auf andere Wettkämpfe aus.</html>");
		}

		loadMargins();

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});

		newLineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowIndex = tableModel.addEmptyRow();
				if (certificateTable.editCellAt(rowIndex, 0)) {
					certificateTable.setRowSelectionInterval(rowIndex, rowIndex);
					certificateTable.requestFocus();
					certificateTable.getEditorComponent().requestFocusInWindow();
				}
			}
		});

		removeLineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = certificateTable.getSelectedRows();
				if (selectedRows.length > 0) {
					tableModel.deleteRows(selectedRows);
				} else {
					Common.showMessage("Selektieren Sie zunächst ein Zeile.");
				}
			}
		});

		marginTop.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);

				saveMargin(marginTop.getText(), "Top");
			}
		});

		marginLeft.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);

				saveMargin(marginLeft.getText(), "Left");
			}
		});

		marginRight.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);

				saveMargin(marginRight.getText(), "Right");
			}
		});
	}

	protected void setCellEditors() {
		TableColumn fontColumn = certificateTable.getColumnModel().getColumn(1);
		JComboBox<String> fontBox = new JComboBox<>();
		fontBox.addItem("Arial");
		fontBox.addItem("Times New Roman");
		fontColumn.setCellEditor(new DefaultCellEditor(fontBox));

		TableColumn sizeColumn = certificateTable.getColumnModel().getColumn(2);
		JComboBox<String> sizeBox = new JComboBox<>();
		for (int i = 6; i < 32; i++) {
			sizeBox.addItem(Integer.toString(i));
		}
		sizeColumn.setCellEditor(new DefaultCellEditor(sizeBox));

		TableColumn weightColumn = certificateTable.getColumnModel().getColumn(3);
		JComboBox<String> weightBox = new JComboBox<>();
		weightBox.addItem("normal");
		weightBox.addItem("fett");
		weightColumn.setCellEditor(new DefaultCellEditor(weightBox));

		TableColumn alignColumn = certificateTable.getColumnModel().getColumn(4);
		JComboBox<String> alignBox = new JComboBox<>();
		alignBox.addItem("zentriert");
		alignBox.addItem("links");
		alignBox.addItem("rechts");
		alignColumn.setCellEditor(new DefaultCellEditor(alignBox));
	}

	protected void loadMargins() {

		marginTop.setText("0");
		marginLeft.setText("0");
		marginRight.setText("0");

		ArrayList<DatabaseParameter> parameters = new ArrayList<>();
		parameters.add(new DatabaseParameter(type + "Top"));
		parameters.add(new DatabaseParameter(type + "Left"));
		parameters.add(new DatabaseParameter(type + "Right"));

		ResultSet resultSet = Application.database.query("SELECT line, type FROM " + tableName + " WHERE type IN (?, ?, ?);", parameters);

		try {

			while (resultSet.next()) {
				String marginType = resultSet.getString("type");

				if (marginType.contains("Top")) {
					marginTop.setText(resultSet.getString("line"));
				} else if (marginType.contains("Left")) {
					marginLeft.setText(resultSet.getString("line"));
				} else if (marginType.contains("Right")) {
					marginRight.setText(resultSet.getString("line"));
				}
			}

			resultSet.close();

		} catch (Exception e) {
			Common.printError(e);
		}
	}

	protected void saveMargin(String margin, String orientation) {
		Matcher matcher = Pattern.compile("^\\d+(\\.\\d+|,\\d+)?$").matcher(margin.toString());
		if (matcher.find()) {

			margin = margin.replaceAll(",", ".");

			ArrayList<DatabaseParameter> params = new ArrayList<>();
			params.add(new DatabaseParameter(type + orientation));

			Application.database.execute("DELETE FROM " + tableName + " WHERE type = ?", params);

			params = new ArrayList<>();
			params.add(new DatabaseParameter(margin));
			params.add(new DatabaseParameter(type + orientation));
			Application.database.execute("INSERT INTO " + tableName + " (line, type) VALUES (?, ?);", params);
		} else {
			Common.showMessage("'" + margin + "' ist keine gültige Fließkommazahl zur Abbildung eines metrischen Wertes. Geben Sie eine Zahl ein.");
		}
	}
}
