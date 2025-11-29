package gothos.competitionMainForm.Squad;

import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.ImageCell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.line.LineStyle;
import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.PdfCore.Pdf;
import gothos.PdfCore.PdfTableResult;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class PdfSquadLists extends PdfTableResult {

	protected String squad;

	public PdfSquadLists (String squad) {
		this.squad = squad;
	}

	float                         idColWidth                 = 12;
	float                         nameColWidth               = 44;
	float                         apparatiValueColWidth      = 34;
	float                         teamCheckBoxColWidth       = 0;
	float                         spacerColWidth             = 0;
	float                         headerTeamCheckBoxColWidth = 0;
	float                         classColWidth              = 10;
	boolean                       showTeamCheckbox;
	CompetitionData               competitionData;
	LinkedHashMap<String, String> competitionInfo;

	public void generatePdf (ArrayList<String> apparati, boolean showTeamCheckbox) {
		this.showTeamCheckbox = showTeamCheckbox;
		competitionData = new CompetitionData();
		competitionInfo = competitionData.getCompetitionData();

		suggestedFileName = "Riege_" + squad + "_" + competitionInfo.get("longname");
		suggestedFileName = suggestedFileName.replaceAll("\\s", "_") + ".pdf";

		setFont(PDType1Font.TIMES_ROMAN);
		setFontSize(12f);

		idColWidth = 12;
		nameColWidth = 44;
		apparatiValueColWidth = 34;
		teamCheckBoxColWidth = 0;
		spacerColWidth = 0;
		headerTeamCheckBoxColWidth = 0;
		classColWidth = 10;

		if (showTeamCheckbox) {
			apparatiValueColWidth = 25;
			nameColWidth = 39;
			teamCheckBoxColWidth = 10;
			spacerColWidth = 4;
			headerTeamCheckBoxColWidth = 25;
		}

		document = new PDDocument();

		PDDocumentInformation information = document.getDocumentInformation();

		information.setAuthor("gothos - Wettkampfverwaltung");
		information.setCreator("gothos - Wettkampfverwaltung");
		information.setTitle("Riege - " + squad);
		information.setCreationDate(new GregorianCalendar());

		try {

			competitionData.setSquad(squad);
			competitionData.setOrderBy("ID ASC, ROWID ASC");

			String                       sql        = competitionData.getSql();
			ArrayList<DatabaseParameter> parameters = competitionData.getParameters();

			LinkedHashMap<Integer, LinkedHashMap<String, String>> gymnasts = Application.database.fetchAll(sql, parameters);

			LineStyle borderStyle = new LineStyle(new Color(0), 0.5f);

			/*addPage(-millimeterToPoints(12), millimeterToPoints(12), PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());

			stream = new PDPageContentStream(document, page);

			drawText("Hallo Welt");

			stream.close();*/


			Integer pageCount;
			for (String apparatus : apparati) {
				pageCount = document.getNumberOfPages();
				ArrayList<Integer> apparatusPageIndexes = new ArrayList<>();

				addPage(-millimeterToPoints(12), millimeterToPoints(12), PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());

				stream = new PDPageContentStream(document, page);

				printPageHeader(apparatus);

				for (Map.Entry<Integer, LinkedHashMap<String, String>> gymnastEntry : gymnasts.entrySet()) {

					LinkedHashMap<String, String> gymnast = gymnastEntry.getValue();
					Row<PDPage>                   row     = table.createRow(30);
					row.setLineSpacing(1.4f);

					String name      = gymnast.getOrDefault("name", "") + "<br>" + gymnast.getOrDefault("club", "");
					String className = gymnast.getOrDefault("class", "");

					styleCell(row.createCell(idColWidth, gymnast.getOrDefault("ID", "")));
					styleCell(row.createCell(nameColWidth, name));
					styleCell(row.createCell(classColWidth, className));

					if (showTeamCheckbox) {

						/*styleCell(row.createCell(spacerColWidth, ""));

						Cell<PDPage> teamCheckBoxCell = row.createCell(teamCheckBoxColWidth, "");
						teamCheckBoxCell.setBottomBorderStyle(borderStyle);
						teamCheckBoxCell.setTopBorderStyle(borderStyle);
						teamCheckBoxCell.setLeftBorderStyle(borderStyle);
						teamCheckBoxCell.setRightBorderStyle(borderStyle);*/

						ImageCell<PDPage> imgCell = row.createImageCell(apparatiValueColWidth, new Image(ImageIO.read(getClass().getResourceAsStream("/images/valueRectSmall.png"))));
						noBorder(imgCell);

						ImageCell<PDPage> imgTeamCell = row.createImageCell(teamCheckBoxColWidth, new Image(ImageIO.read(getClass().getResourceAsStream("/images/teamCheckBox.png"))));
						noBorder(imgTeamCell);

					} else {
						ImageCell<PDPage> imgCell = row.createImageCell(apparatiValueColWidth, new Image(ImageIO.read(getClass().getResourceAsStream("/images/valueRectBig.png"))));
						noBorder(imgCell);
					}
				}

				table.draw();

				stream.close();

				for (int i = pageCount; i < document.getNumberOfPages(); i++) {
					apparatusPageIndexes.add(i);
				}

				printApparatiFooter(apparatus, apparatusPageIndexes);
			}

		} catch (Exception e) {
			Common.printError(e);
		}

	}

	void printPageHeader (String apparatus) {
		try {
			String headline;
			if (squad.contains("Riege")) {
				headline = squad + " - " + apparatus;
			} else {
				headline = "Riege " + squad + " - " + apparatus;
			}

			drawText(headline, PDType1Font.TIMES_ROMAN, 18, "left", 1);
			drawText(competitionInfo.getOrDefault("longname", ""), PDType1Font.TIMES_ITALIC, 14, "left", 1.5f);

			prepareTable(0, 70, lastY - 25);

			Row<PDPage> headerRow = table.createRow(25);

			styleHeaderCell(headerRow.createCell(idColWidth, "Startnr."));
			styleHeaderCell(headerRow.createCell(nameColWidth, "Name"));
			styleHeaderCell(headerRow.createCell(classColWidth, "AK"));

			if (showTeamCheckbox) {
				styleHeaderCell(headerRow.createCell(apparatiValueColWidth - (headerTeamCheckBoxColWidth - teamCheckBoxColWidth), "Wertung"));
				//styleHeaderCell(headerRow.createCell(spacerColWidth, ""));
				styleHeaderCell(headerRow.createCell(headerTeamCheckBoxColWidth + spacerColWidth, "in Mannschaftswertung")).setAlign(HorizontalAlignment.RIGHT);
			} else {
				styleHeaderCell(headerRow.createCell(apparatiValueColWidth, "Wertung"));
			}

			table.addHeaderRow(headerRow);
		} catch (Exception e) {
			Common.printError(e);
		}
	}

	void printApparatiFooter (String apparatus, ArrayList<Integer> pageIndexes) throws java.io.IOException {
		int counter = 0;
		for (Integer pageIndex : pageIndexes) {
			counter++;

			PDPage              thisPage     = document.getPage(pageIndex);
			PDPageContentStream footerStream = new PDPageContentStream(document, thisPage, PDPageContentStream.AppendMode.APPEND, true);
			PDType1Font         font         = PDType1Font.TIMES_ROMAN;

			footerStream.beginText();
			footerStream.setFont(PDType1Font.TIMES_ROMAN, 10);
			footerStream.newLineAtOffset(0, 60);
			//footerStream.setLeading(14.5f);

			String squadNumberAndApparatus = "Riege " + squad + " - " + apparatus;

			if (pageIndexes.size() > 1) {
				squadNumberAndApparatus += " - Blatt " + counter;
			}

			footerStream.showText(squadNumberAndApparatus);

			String pageNumber = "Seite " + (pageIndex + 1);
			float  textWidth  = font.getStringWidth(pageNumber) / 1000 * 10;
			float  x          = (Math.abs(page.getMediaBox().getUpperRightX()) - Math.abs(page.getMediaBox().getLowerLeftX()) - textWidth);

			footerStream.newLineAtOffset(x, 0);
			footerStream.showText(pageNumber);

			footerStream.endText();
			footerStream.close();
		}
	}

	protected void defaultFooter () throws java.io.IOException {
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

	protected Cell<PDPage> styleHeaderCell (Cell<PDPage> cell) {
		noBorder(cell);
		cell.setFont(PDType1Font.TIMES_BOLD);
		cell.setFontSize(14f);

		return cell;
	}

	protected Cell<PDPage> styleCell (Cell<PDPage> cell) {
		noBorder(cell);
		cell.setFont(PDType1Font.TIMES_ROMAN);
		cell.setFontSize(12f);

		return cell;
	}

	public void print () {
		this.print(PageFormat.LANDSCAPE);
	}
}
