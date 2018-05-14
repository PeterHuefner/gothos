package gothos.competitionMainForm;

import com.opencsv.CSVWriter;
import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseAnalyse;
import gothos.WindowManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExportForm {

	protected String mode;
	protected String csvFilePath;
	private JButton  backButton;
	private JButton  exportButton;
	private JPanel panel;

	public JPanel getPanel() {
		return panel;
	}

	public ExportForm(String mode) {
		this.mode = mode;

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});


		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser            chooser = new JFileChooser();
				FileNameExtensionFilter filter  = new FileNameExtensionFilter("CSV-Dateien", "csv");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);

				int chooserState = chooser.showSaveDialog(WindowManager.childFrame);
				if (chooserState == JFileChooser.APPROVE_OPTION) {
					csvFilePath = chooser.getSelectedFile().getAbsolutePath();
					if(!csvFilePath.matches("\\.csv$")){
						csvFilePath += ".csv";
					}
					File file   = new File(csvFilePath);

					if(file.exists()){

						Integer overrideStatus = JOptionPane.showConfirmDialog(WindowManager.childFrame, file.getName()+ " existiert bereits. Soll die Datei überschrieben werden?");
						if(overrideStatus == JOptionPane.YES_OPTION){
							export();
						}

					}else{
						export();
					}
				}
			}
		});
	}

	protected void export(){

		DatabaseAnalyse analyse = new DatabaseAnalyse();
		String sql;

		if(mode == "extended"){
			sql = analyse.baseCompetitionSelect(true, analyse.listApparatiInCompetition(), true);
		}else{
			sql = analyse.baseCompetitionSelect(true, new ArrayList<String>(), false);
		}

		ResultSet result = Application.database.query(sql);
		try{
			ResultSetMetaData metaData = result.getMetaData();

			ArrayList<String> head = new ArrayList<>();
			for(Integer i = 0; i < metaData.getColumnCount(); i++){
				String columnName = metaData.getColumnLabel(i + 1);
				if(Common.emptyString(columnName)){
					columnName = metaData.getColumnName(i + 1);
				}

				if(!Common.emptyString(columnName) && !columnName.toLowerCase().equals("rowid")){
					head.add(columnName);
				}
			}

			CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath), ';', '"', '\\', "\n");
			writer.writeNext(head.toArray(new String[0]));

			while(result.next()){
				ArrayList<String> entry = new ArrayList<>();
				for(String col: head){
					entry.add(result.getString(col));
				}
				writer.writeNext(entry.toArray(new String[0]));
			}

			writer.close();
			result.close();
		}catch (Exception e){
			Common.printError(e);
		}

		backButton.doClick();
	}

}
