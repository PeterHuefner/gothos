package gothos.competitionMainForm.Certificates;

import be.quodlibet.boxable.Paragraph;
import gothos.Common;
import gothos.PdfCore.Pdf;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.print.PageFormat;

public class PdfCertificate extends Pdf {

	protected String className;
	protected boolean teamCertificate = false;

	public void setClassName(String className) {
		this.className = className;
	}

	public void setTeamCertificate(boolean teamCertificate) {
		this.teamCertificate = teamCertificate;
	}

	public PdfCertificate() {

	}

	public void generatePdf() {

		document = new PDDocument();

		page = new PDPage(
				new PDRectangle(-millimeterToPoints(10), millimeterToPoints(10), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight())
				//new PDRectangle(-millimeterToPoints(0), millimeterToPoints(0), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight())
		);

		document.addPage(page);

		try {

			PDFont font = PDType1Font.TIMES_ROMAN;

			stream = new PDPageContentStream(document, page);

			//stream.beginText();

			setFont(PDType1Font.TIMES_ROMAN);
			setFontSize(14);

			/*drawRightText("Rechte Welt");
			drawRightText("Rechte Welt");
			drawText("Linke Welt");*/

			drawText("Hallo Welt");
			drawText("Linke Welt");
			drawCenteredText("Zentrierte Welt");
			drawText("Linke Welt");
			drawRightText("Rechte Welt");
			drawCenteredText("Zentrierte Welt");
			drawCenteredText("Zentrierte Welt");
			drawRightText("Rechte Welt");
			drawText("Linke Welt");

			/*stream.setFont(PDType1Font.TIMES_ROMAN, 14);
			stream.newLineAtOffset(0, page.getMediaBox().getHeight() - (font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 14));
			stream.showText("Hallo Welt");

			stream.newLineAtOffset(0, -50);
			stream.showText("Tschüss Welt");

			stream.newLineAtOffset(50, -50);
			stream.showText("Tschüss Welt");

			stream.newLineAtOffset(-50, -50);
			stream.showText("Tschüss Welt");*/

			//stream.endText();

			stream.close();

		} catch (Exception e) {
			Common.printError(e);
		}
	}

	public void print() {
		this.print(PageFormat.PORTRAIT);
	}
}
