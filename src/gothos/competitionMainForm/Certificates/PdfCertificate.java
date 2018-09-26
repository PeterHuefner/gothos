package gothos.competitionMainForm.Certificates;

import be.quodlibet.boxable.Paragraph;
import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.PdfCore.Pdf;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.print.PageFormat;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PdfCertificate extends Pdf {

	protected String  className;
	protected boolean teamCertificate = false;
	protected String  certType        = "single";
	protected String  table;
	protected LinkedHashMap<Integer, LinkedHashMap<String, String>> certificateLines;

	public void setClassName(String className) {
		this.className = className;
	}

	public void setTeamCertificate(boolean teamCertificate) {
		this.teamCertificate = teamCertificate;

		if (this.teamCertificate) {
			certType = "team";
		} else {
			certType = "single";
		}
	}

	public PdfCertificate() {
		this.table = "competition_" + Application.selectedCompetition + "_certificates";
	}

	public void generatePdf() {

		float verticalMargin   = 4;
		float horizontalMargin = 2;

		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(certType + "Top"));
		params.add(new DatabaseParameter(certType + "Left"));

		ResultSet resultSet = Application.database.query("SELECT line, type FROM " + table + " WHERE type IN (?, ?)", params);
		try {
			while (resultSet.next()) {
				String type = resultSet.getString("type");
				if (type.contains("Top")) {
					verticalMargin = Float.parseFloat(resultSet.getString("line"));
				} else {
					horizontalMargin = Float.parseFloat(resultSet.getString("line"));
				}
			}

			resultSet.close();
		} catch (Exception e) {
			Common.printError(e);
		}

		document = new PDDocument();

		page = new PDPage(
				new PDRectangle(-millimeterToPoints(verticalMargin), millimeterToPoints(horizontalMargin), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight())
				//new PDRectangle(-millimeterToPoints(0), millimeterToPoints(0), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight())
		);

		document.addPage(page);

		try {

			params = new ArrayList<>();
			params.add(new DatabaseParameter(certType));

			certificateLines = Application.database.fetchAllIndexed("SELECT ROWID, * FROM " + table + " WHERE type = ?;", params, "ROWID");

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

	protected void printPage() {

	}

	public void print() {
		this.print(PageFormat.PORTRAIT);
	}
}
