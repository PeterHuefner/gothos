package gothos.Test;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import gothos.Common;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;

import javax.swing.*;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.GregorianCalendar;

public class Pdftest {
	private JPanel panel;

	public JPanel getPanel() {
		return panel;
	}

	public Pdftest() {

		PDDocument document = new PDDocument();
		PDDocumentInformation information = document.getDocumentInformation();

		information.setAuthor("gothos - Wettkampfverwaltung");
		information.setCreator("gothos - Wettkampfverwaltung");
		information.setTitle("Test PDF");

		GregorianCalendar date = new GregorianCalendar();
		information.setCreationDate(date);


		//Portrait
		//PDPage page = new PDPage();

		// Landscape
		PDPage page = new PDPage(
				new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth())
		);

		document.addPage(page);


		try{
			PDPageContentStream stream = new PDPageContentStream(document, page);

			stream.beginText();
			stream.setFont( PDType1Font.TIMES_ROMAN, 12 );

			//Direkter anfang des Dokuments ohne Rand
			//stream.newLineAtOffset(0,page.getMediaBox().getHeight() - 10);

			//Anfang des Dokuments mit etwas Rand
			stream.newLineAtOffset(20,page.getMediaBox().getHeight() - 25);

			stream.setLeading(10);


			stream.showText("Hallo Welt!");
			stream.newLine();
			stream.showText("Text in einer neuen Zeile");

			stream.endText();

			//Tabelle

			//Dummy Table
			float margin = 50;
			// starting y position is whole page height subtracted by top and bottom margin
			float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
			// we want table across whole page width (subtracted by left and right margin ofcourse)
			float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

			boolean drawContent = true;
			float yStart = yStartNewPage;
			float bottomMargin = 70;
			// y position is your coordinate of top left corner of the table
			float yPosition = 550;

			BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true, drawContent);

			Row<PDPage>  headerRow = table.createRow(15f);
			Cell<PDPage> cell      = headerRow.createCell(100, "Header");
			table.addHeaderRow(headerRow);


			Row<PDPage> row = table.createRow(12);
			cell = row.createCell(30, "Data 1");
			cell = row.createCell(70, "Some value");

			table.draw();


			stream.close();
		}catch (Exception e){
			Common.printError(e);
		}

		try {

			document.save(System.getProperty("user.home") + File.separator + "pdftest.pdf");

			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPageable(new PDFPageable(document));
			if (job.printDialog()) {
				job.print();
			}

			document.close();
		}catch (Exception e){
			Common.printError(e);
		}


	}
}
