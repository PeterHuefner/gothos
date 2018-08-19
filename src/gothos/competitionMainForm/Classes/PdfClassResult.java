package gothos.competitionMainForm.Classes;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.WindowManager;
import gothos.competitionMainForm.Gymnast;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.print.PrinterJob;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PdfClassResult {

	protected String                        className;
	protected String                        classDisplayName;
	protected CompetitionData               competitionData;
	protected LinkedHashMap<String, String> classConfig;
	protected PDDocument                    document;
	protected PDPageContentStream           stream;

	public PdfClassResult(String className) {
		this.className = className;
		competitionData = new CompetitionData();
		competitionData.setClassName(className);
		classConfig = competitionData.getClassConfig(className);
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();

		try {
			if (document != null) {
				document.close();
			}
		} catch (Exception e) {

		}
	}

	public void generatePdf() {

		LinkedHashMap<String, String> competitionInfo = competitionData.getCompetitionData();
		ArrayList<Gymnast>            gymnasts        = competitionData.calculateClassResult();

		classDisplayName = className;

		if (!Common.emptyString(classConfig.get("displayName"))) {
			classDisplayName = classConfig.get("displayName");
		}

		if (Common.emptyString(competitionInfo.get("longname"))) {
			competitionInfo.put("logname", competitionInfo.get("name"));
		}

		document = new PDDocument();
		PDDocumentInformation information = document.getDocumentInformation();

		information.setAuthor("gothos - Wettkampfverwaltung");
		information.setCreator("gothos - Wettkampfverwaltung");
		information.setTitle("Protokoll_" + classDisplayName);
		information.setCreationDate(new GregorianCalendar());

		PDPage page = new PDPage(
				new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth())
		);

		document.addPage(page);

		try {
			stream = new PDPageContentStream(document, page);

			//Titel
			stream.beginText();
			stream.setFont(PDType1Font.TIMES_ROMAN, 14);
			stream.newLineAtOffset(20, page.getMediaBox().getHeight() - 25);
			stream.setLeading(14.5f);

			stream.showText(classDisplayName + " - " + competitionInfo.get("longname"));

			stream.endText();


			if (gymnasts.size() > 0) {

				int allApparatiWidth = 65;
				int apparatiWidth    = (allApparatiWidth / gymnasts.get(0).getApparatiValues().size());
				int diffSpace        = allApparatiWidth - (gymnasts.get(0).getApparatiValues().size() * apparatiWidth);

				float margin = 15;
				// starting y position is whole page height subtracted by top and bottom margin
				float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
				// we want table across whole page width (subtracted by left and right margin ofcourse)
				float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

				boolean drawContent  = true;
				float   yStart       = yStartNewPage;
				float   bottomMargin = 70;
				// y position is your coordinate of top left corner of the table
				float yPosition = 550;

				LineStyle borderStyle   = new LineStyle(new Color(0), 0.5f);
				LineStyle noBorderStyle = new LineStyle(new Color(0xFFFFFF), 0);

				BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true, drawContent);
				//table.removeAllBorders(true);

				Row<PDPage> headerRow = table.createRow(15f);

				headerRow.createCell(5, "Startnr.");
				headerRow.createCell(20 + diffSpace, "Name");


				for (Map.Entry<String, Double> apparatus : gymnasts.get(0).getApparatiValues().entrySet()) {
					headerRow.createCell(apparatiWidth, apparatus.getKey());
				}

				headerRow.createCell(5, "<b>Punkte</b>");
				headerRow.createCell(5, "<b>Platz</b>");

				table.addHeaderRow(headerRow);


				for (Gymnast gymnast : gymnasts) {

					Row<PDPage> row = table.createRow(24);
					row.createCell(5, gymnast.getMetaData().get("ID"));
					row.createCell(20 + diffSpace, gymnast.getMetaData().get("name") + "<br><i>" + gymnast.getMetaData().get("club") + "</i>");

					for (Map.Entry<String, Double> apparatus : gymnast.getApparatiValues().entrySet()) {
						//row.createCell(apparatiWidth, apparatus.getValue().toString());
						row.createCell(apparatiWidth, String.format("%2.3f", apparatus.getValue()));
					}

					row.createCell(5, "<b>" + String.format("%2.3f", gymnast.getSum()) + "</b>");
					row.createCell(5, "<b>" + gymnast.getRanking().toString() + "</b>");

				}

				for (Row<PDPage> row : table.getRows()) {
					int i = 0;
					for (Cell<PDPage> cell : row.getCells()) {
						cell.setBottomBorderStyle(borderStyle);

						cell.setTopBorderStyle(noBorderStyle);
						cell.setRightBorderStyle(noBorderStyle);
						cell.setLeftBorderStyle(noBorderStyle);

						if (i == 0) {
							//cell.setLeftBorderStyle(borderStyle);
						}
						i++;
					}
				}

				table.draw();
			}


			stream.close();
		} catch (Exception e) {
			Common.printError(e);
		}
	}

	public void saveDialog() {
		JFileChooser            chooser = new JFileChooser();
		FileNameExtensionFilter filter  = new FileNameExtensionFilter("PDF", "pdf");
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);

		int chooserState = chooser.showSaveDialog(WindowManager.childFrame);
		if (chooserState == JFileChooser.APPROVE_OPTION) {
			this.save(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void save(String filename) {
		try {
			document.save(filename);
		} catch (Exception e) {
			Common.printError(e);
		}
	}

	public void print() {
		try {
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPageable(new PDFPageable(document));
			if (job.printDialog()) {
				job.print();
			}
		} catch (Exception e) {
			Common.printError(e);
		}
	}
}
