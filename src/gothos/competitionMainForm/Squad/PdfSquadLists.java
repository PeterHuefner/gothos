package gothos.competitionMainForm.Squad;

import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.PdfCore.Pdf;
import gothos.PdfCore.PdfTableResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.awt.print.PageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PdfSquadLists extends PdfTableResult {

	protected String squad;

	public PdfSquadLists(String squad) {
		this.squad = squad;
	}

	public void generatePdf(ArrayList<String> apparati) {

		CompetitionData competitionData = new CompetitionData();
		LinkedHashMap<String, String> competitionInfo = competitionData.getCompetitionData();

		setFont(PDType1Font.TIMES_ROMAN);


		document = new PDDocument();

		try {

			competitionData.setSquad(squad);
			competitionData.setOrderBy("ID ASC, ROWID ASC");

			String sql = competitionData.getSql();
			ArrayList<DatabaseParameter> parameters = competitionData.getParameters();

			LinkedHashMap<Integer, LinkedHashMap<String, String>> gymnasts = Application.database.fetchAll(sql, parameters);

			LineStyle borderStyle = new LineStyle(new Color(0), 0.5f);

			for (String apparatus: apparati) {
				addPage(-millimeterToPoints(12), millimeterToPoints(12), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());

				stream = new PDPageContentStream(document, page);

				String headline;
				if (squad.contains("Riege")) {
					headline = squad + " - " + apparatus;
				} else {
					headline = "Riege " + squad + " - " + apparatus;
				}

				drawText(headline, PDType1Font.TIMES_ROMAN, 18, "left", 1);
				drawText(competitionInfo.getOrDefault("longname", ""), PDType1Font.TIMES_ITALIC, 14, "left", 1.5f);

				prepareTable(0, 75, lastY - 25);

				Row<PDPage> headerRow = table.createRow(24);

				styleHeaderCell(headerRow.createCell(12, "Startnr."));
				styleHeaderCell(headerRow.createCell(44, "Name"));
				styleHeaderCell(headerRow.createCell(44, "Wertung"));
				
				table.addHeaderRow(headerRow);

				for (Map.Entry<Integer, LinkedHashMap<String, String>> gymnastEntry: gymnasts.entrySet()) {

					Row<PDPage> emptyRow = table.createRow(12);
					noBorder(emptyRow.createCell(12, ""));
					noBorder(emptyRow.createCell(44, ""));
					noBorder(emptyRow.createCell(44, "")).setBottomBorderStyle(borderStyle);

					LinkedHashMap<String, String> gymnast = gymnastEntry.getValue();
					Row<PDPage> row = table.createRow(24);

					String name = gymnast.getOrDefault("name", "") + "<br><br><i>" + gymnast.getOrDefault("club", "");
					if (!Common.emptyString(gymnast.getOrDefault("team", "")) && !gymnast.getOrDefault("club", "").equals(gymnast.getOrDefault("team", ""))) {
						name += "<br>" + gymnast.getOrDefault("team", "");
					}
					name += "</i>";
					
					styleCell(row.createCell(12, gymnast.getOrDefault("ID", "")));
					styleCell(row.createCell(44, name));

					Cell<PDPage> apparatusValueCell = row.createCell(44, "");
					apparatusValueCell.setBottomBorderStyle(borderStyle);
					apparatusValueCell.setTopBorderStyle(borderStyle);
					apparatusValueCell.setLeftBorderStyle(borderStyle);
					apparatusValueCell.setRightBorderStyle(borderStyle);
				}

				table.draw();

				stream.close();

				//defaultFooter();
			}

		} catch (Exception e) {
			Common.printError(e);
		}

	}

	protected void styleHeaderCell(Cell<PDPage> cell) {
		noBorder(cell);
		cell.setFont(PDType1Font.TIMES_BOLD);
		cell.setFontSize(14f);
	}

	protected void styleCell(Cell<PDPage> cell) {
		noBorder(cell);
		cell.setFont(PDType1Font.TIMES_ROMAN);
		cell.setFontSize(12f);
	}
	
	public void print() {
		this.print(PageFormat.PORTRAIT);
	}
}
