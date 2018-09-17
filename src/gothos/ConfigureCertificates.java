package gothos;

import javax.swing.*;

public class ConfigureCertificates {
	private JPanel  panel;
	private JButton backButton;
	private JTable  certificateTable;
	private JButton newLineButton;
	private JButton removeLineButton;
	private JLabel helpLabel;

	protected String tableName;
	protected ConfigureCertificatesTableModel tableModel;

	public ConfigureCertificates(String tableName) {
		this.tableName = tableName;

		tableModel = new ConfigureCertificatesTableModel();
		certificateTable.setModel(tableModel);
	}
}
