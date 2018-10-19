package gothos.competitionMainForm.Teams;

import gothos.WindowManager;
import gothos.competitionMainForm.Certificates.PdfCertificate;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewTeams {
	private JPanel    panel;
	private JButton   backButton;
	private JTable    teamsTable;
	private JButton   printPdfProtocol;
	private JButton   printProtocol;
	private JButton   pdfProtocol;
	private JButton   printPdfCertificate;
	private JButton   printCertificate;
	private JButton   pdfCertificate;
	private JLabel    certificateLabel;
	private JLabel    protocolLabel;
	private JCheckBox showGymnasts;
	private JCheckBox showClubs;

	protected ViewTeamsTableModel tableModel;

	public JPanel getPanel() {
		return panel;
	}

	public ViewTeams() {

		tableModel = new ViewTeamsTableModel();
		teamsTable.setModel(tableModel);

		showGymnasts.setSelected(true);
		showClubs.setSelected(true);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowManager.disposeChildFrame();
			}
		});

		printPdfProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfTeamResult result = new PdfTeamResult();

				result.setShowGymnasts(showGymnasts.isSelected());
				result.setShowClubs(showClubs.isSelected());
				result.generatePdf();

				result.saveDialog();
				result.print();
			}
		});

		printProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfTeamResult result = new PdfTeamResult();

				result.setShowGymnasts(showGymnasts.isSelected());
				result.setShowClubs(showClubs.isSelected());
				result.generatePdf();

				result.print();
			}
		});

		pdfProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfTeamResult result = new PdfTeamResult();

				result.setShowGymnasts(showGymnasts.isSelected());
				result.setShowClubs(showClubs.isSelected());
				result.generatePdf();

				result.saveDialog();
			}
		});

		printPdfCertificate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfCertificate certificate = new PdfCertificate();
				certificate.setTeamCertificate(true);
				certificate.generatePdf();

				certificate.saveDialog();
				certificate.print();
			}
		});

		printCertificate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfCertificate certificate = new PdfCertificate();
				certificate.setTeamCertificate(true);
				certificate.generatePdf();

				certificate.print();
			}
		});

		pdfCertificate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PdfCertificate certificate = new PdfCertificate();
				certificate.setTeamCertificate(true);
				certificate.generatePdf();

				certificate.saveDialog();
			}
		});
	}
}
