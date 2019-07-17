package gothos.competitionMainForm.Classes;

import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.CompetitionStatistics;
import gothos.WindowManager;
import gothos.competitionMainForm.Certificates.PdfCertificate;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

public class ViewClass {
	private JButton backButton;
	private JTable  classTable;
	private JButton printAndPdfProtocol;
	private JButton printCertificates;
	private JPanel  panel;
	private JButton printProtocol;
	private JButton pdfProtocol;
	private JButton printAndPdfCertificate;
	private JButton pdfCertificate;
    private JLabel classInfoLabel;

    protected String className;
	protected ViewClassTableModel tableModel;

	public JPanel getPanel() {
		return panel;
	}

	public ViewClass (String className) {
		this.className = className;


		tableModel = new ViewClassTableModel(className);
		classTable.setModel(tableModel);


		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});

		printAndPdfProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfClassResult classResult = new PdfClassResult(className);
				classResult.generatePdf();

				classResult.saveDialog();
				classResult.print();
			}
		});

		printProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfClassResult classResult = new PdfClassResult(className);
				classResult.generatePdf();

				classResult.print();
			}
		});

		pdfProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfClassResult classResult = new PdfClassResult(className);
				classResult.generatePdf();

				classResult.saveDialog();
			}
		});

		printAndPdfCertificate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfCertificate certificate = new PdfCertificate();
				certificate.setClassName(className);
				certificate.generatePdf();

				certificate.saveDialog();
				certificate.print();
			}
		});

		printCertificates.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfCertificate certificate = new PdfCertificate();
				certificate.setClassName(className);
				certificate.generatePdf();

				certificate.print();
			}
		});

		pdfCertificate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfCertificate certificate = new PdfCertificate();
				certificate.setClassName(className);
				certificate.generatePdf();

				certificate.saveDialog();
			}
		});

		generateStatistics();
	}

	protected void generateStatistics() {
		CompetitionData competitionData = new CompetitionData();
		competitionData.setClassName(className);

		CompetitionStatistics          stats  = new CompetitionStatistics(competitionData);
		LinkedHashMap<String, Integer> counts = stats.getCountsForCols(new String[]{"club"});

		classInfoLabel.setText("Anzahl Aktive: " + stats.getGymnastCountInCurrentQuery() + " | Anzahl Vereine: " + counts.get("club"));
	}
}
