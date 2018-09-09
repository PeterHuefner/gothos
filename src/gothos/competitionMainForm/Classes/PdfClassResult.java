package gothos.competitionMainForm.Classes;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.PdfCore.PdfTableResult;
import gothos.WindowManager;
import gothos.competitionMainForm.Gymnast;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PdfClassResult extends PdfTableResult {

	protected String                        className;
	protected String                        classDisplayName;
	protected CompetitionData               competitionData;
	protected LinkedHashMap<String, String> classConfig;

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

		/*ArrayList<Gymnast> copy = new ArrayList<>();
		for (Gymnast gymnast : gymnasts) {
			copy.add(gymnast);
		}
		for (Gymnast gymnast : copy) {
			gymnasts.add(gymnast);
			gymnasts.add(gymnast);
			gymnasts.add(gymnast);
			gymnasts.add(gymnast);
		}*/

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

		this.page = new PDPage(
				new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth())
		);

		document.addPage(page);

		try {
			stream = new PDPageContentStream(document, page);

			//Titel
			stream.beginText();
			stream.setFont(PDType1Font.TIMES_ROMAN, 14);
			stream.newLineAtOffset(30, page.getMediaBox().getHeight() - 40);
			stream.setLeading(14.5f);

			stream.showText(classDisplayName + " - " + competitionInfo.get("longname"));

			suggestedFileName = classDisplayName + "_" + competitionInfo.get("longname");
			suggestedFileName = suggestedFileName.replaceAll("\\s", "_") + ".pdf";

			stream.endText();


			if (gymnasts.size() > 0) {

				int allApparatiWidth = 65;
				int apparatiWidth    = (allApparatiWidth / gymnasts.get(0).getApparatiValues().size());
				int diffSpace        = allApparatiWidth - (gymnasts.get(0).getApparatiValues().size() * apparatiWidth);

				prepareTable();

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

				handleBorders();

				table.draw();
			}

			stream.close();

			defaultFooter();


		} catch (Exception e) {
			Common.printError(e);
		}
	}

	public void print() {
		this.print(PageFormat.LANDSCAPE);
	}
}
