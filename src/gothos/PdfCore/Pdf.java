package gothos.PdfCore;

import gothos.Common;
import gothos.WindowManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pdf {

	public final float MM_TO_POINT = 0.352778f;

	protected PDDocument          document;
	protected PDPage              page;
	protected PDPageContentStream stream;
	protected String              suggestedFileName;
	protected boolean             openFileAfterSave = true;

	protected PDType1Font currentFont;
	protected float       currentFontsize;
	protected float       lastX = -1;
	protected float       lastY = -1;

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

	public float millimeterToPoints(float millimeter) {
		return this.millimeterToPoints(millimeter, 0);
	}

	public float millimeterToPoints(float millimeter, int places) {
		float points = millimeter / MM_TO_POINT;
		points = (float) Common.round((double) points, places);
		return points;
	}

	public float pointsToMillimeters(float point) {
		return this.pointsToMillimeters(point, 0);
	}

	public float pointsToMillimeters(float point, int places) {
		float points = point * MM_TO_POINT;
		points = (float) Common.round((double) points, places);
		return points;
	}

	protected void setFont(PDType1Font font) {
		currentFont = font;
	}

	protected void setFontSize(float size) {
		currentFontsize = size;
	}

	protected void drawText(String text) throws Exception {
		drawText(text, currentFont, currentFontsize, "left", 1.0f);
	}

	protected void drawCenteredText(String text) throws Exception {
		drawText(text, currentFont, currentFontsize, "center", 1.0f);
	}

	protected void drawRightText(String text) throws Exception {
		drawText(text, currentFont, currentFontsize, "right", 1.0f);
	}

	protected void drawText(String text, PDType1Font font, float size, String alignment, float lineHeight) throws Exception {

		if (font == null) {
			font = PDType1Font.TIMES_ROMAN;
			size = 12;
		}

		stream.beginText();

		float x          = 0, y = 0;
		float fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * size;
		float textWidth  = font.getStringWidth(text) / 1000 * size;

		stream.setFont(font, size);

		if (lastX != -1) {
			//x -= Math.abs(lastX);
		}

		if (lastY == -1) {
			y = page.getMediaBox().getHeight() - fontHeight;
		} else {
			y = lastY - (fontHeight * lineHeight);
		}

		if (alignment.equals("center")) {
			//x += (page.getMediaBox().getWidth() - textWidth) / 2; // margin des Dokuments wird nicht beachtet
			x += (Math.abs(page.getMediaBox().getUpperRightX()) - Math.abs(page.getMediaBox().getLowerLeftX()) - textWidth) / 2;
		} else if (alignment.equals("right")) {
			x += (Math.abs(page.getMediaBox().getUpperRightX()) - Math.abs(page.getMediaBox().getLowerLeftX()) - textWidth);
		}

		stream.newLineAtOffset(x, y);

		stream.showText(text);

		lastY = y;
		lastX = x;

		stream.endText();
	}

	protected PDPage addPage() {
		return addPage(-millimeterToPoints(25), millimeterToPoints(25), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
	}

	protected PDPage addPage(float x, float y, float width, float height) {
		page = new PDPage(
				new PDRectangle(x, y, width, height)
		);
		document.addPage(page);
		lastY = -1;
		lastX = -1;

		return page;
	}

	protected void defaultFooter() throws java.io.IOException {
		PDPageTree pages = document.getPages();
		for (int i = 0, length = pages.getCount(); i < length; i++) {
			PDPage              thisPage     = pages.get(i);
			PDPageContentStream footerStream = new PDPageContentStream(document, thisPage, PDPageContentStream.AppendMode.APPEND, true);
			PDType1Font         font         = PDType1Font.TIMES_ROMAN;

			footerStream.beginText();
			footerStream.setFont(PDType1Font.TIMES_ROMAN, 10);
			footerStream.newLineAtOffset(30, 20);
			footerStream.setLeading(14.5f);

			footerStream.showText("Seite " + (i + 1));

			Date             now        = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat();
			dateFormat.applyPattern("dd.MM.YYYY HH:mm");
			String dateText = dateFormat.format(now);

			float textWidth = font.getStringWidth(dateText) / 1000 * 10;
			float x         = (Math.abs(page.getMediaBox().getUpperRightX()) - Math.abs(page.getMediaBox().getLowerLeftX()) - textWidth) - 60;

			footerStream.newLineAtOffset(x, 0);
			footerStream.showText(dateText);

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

			String filename = chooser.getSelectedFile().getAbsolutePath();

			if (!Common.regexMatch("\\.pdf$", filename)) {
				filename += ".pdf";
			}

			this.save(filename);
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
