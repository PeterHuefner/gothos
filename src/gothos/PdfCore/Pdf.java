package gothos.PdfCore;

import gothos.Common;
import gothos.WindowManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;

public class Pdf {

	protected PDDocument          document;
	protected PDPage              page;
	protected PDPageContentStream stream;
	protected String              suggestedFileName;
	protected boolean             openFileAfterSave = true;

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

	protected void defaultFooter() throws java.io.IOException {
		PDPageTree pages = document.getPages();
		for (int i = 0, length = pages.getCount(); i < length; i++) {
			PDPage              thisPage     = pages.get(i);
			PDPageContentStream footerStream = new PDPageContentStream(document, thisPage, PDPageContentStream.AppendMode.APPEND, true);

			footerStream.beginText();
			footerStream.setFont(PDType1Font.TIMES_ROMAN, 10);
			footerStream.newLineAtOffset(30, 20);
			footerStream.setLeading(14.5f);

			footerStream.showText("Seite " + (i + 1));

			footerStream.endText();
			footerStream.close();
		}
	}

	public void saveDialog() {
		JFileChooser            chooser = new JFileChooser();
		FileNameExtensionFilter filter  = new FileNameExtensionFilter("PDF", "pdf");
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);

		if (!Common.emptyString(suggestedFileName)) {
			chooser.setSelectedFile(new File(suggestedFileName));
		}

		int chooserState = chooser.showSaveDialog(WindowManager.childFrame);
		if (chooserState == JFileChooser.APPROVE_OPTION) {
			this.save(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void save(String filename) {
		try {
			document.save(filename);

			if (openFileAfterSave) {
				File file = new File(filename);
				Desktop.getDesktop().open(file);
			}
		} catch (Exception e) {
			Common.printError(e);
		}
	}

	public void print(int orientation) {
		try {
			PrinterJob job = PrinterJob.getPrinterJob();

			job.setPageable(new PDFPageable(document));

			PageFormat format = new PageFormat();
			format.setOrientation(orientation);

			Paper paper = job.defaultPage().getPaper(); // A4

			format.setPaper(paper);

			job.setPrintable(new PDFPrintable(document, Scaling.ACTUAL_SIZE), format);

			if (job.printDialog()) {
				job.print();
			}
		} catch (Exception e) {
			Common.printError(e);
		}
	}
}
