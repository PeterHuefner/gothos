package gothos.competitionMainForm.Squad;

import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
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

	public void generatePdf(ArrayList<String> apparati, boolean showTeamCheckbox) {

		CompetitionData               competitionData = new CompetitionData();
		LinkedHashMap<String, String> competitionInfo = competitionData.getCompetitionData();

		setFont(PDType1Font.TIMES_ROMAN);

		float idColWidth                 = 12;
		float nameColWidth               = 44;
		float apparatiValueColWidth      = 44;
		float teamCheckBoxColWidth       = 0;
		float spacerColWidth             = 0;
		float headerTeamCheckBoxColWidth = 0;

		if (showTeamCheckbox) {
			apparatiValueColWidth = 30;
			teamCheckBoxColWidth = 10;
			spacerColWidth = 4;
			headerTeamCheckBoxColWidth = 25;
		}

		document = new PDDocument();

		try {

			competitionData.setSquad(squad);
			competitionData.setOrderBy("ID ASC, ROWID ASC");

			String                       sql        = competitionData.getSql();
			ArrayList<DatabaseParameter> parameters = competitionData.getParameters();

			LinkedHashMap<Integer, LinkedHashMap<String, String>> gymnasts = Application.database.fetchAll(sql, parameters);

			LineStyle borderStyle = new LineStyle(new Color(0), 0.5f);

			for (String apparatus : apparati) {
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

				styleHeaderCell(headerRow.createCell(idColWidth, "Startnr."));
				styleHeaderCell(headerRow.createCell(nameColWidth, "Name"));

				if (showTeamCheckbox) {
					styleHeaderCell(headerRow.createCell(apparatiValueColWidth - (headerTeamCheckBoxColWidth - teamCheckBoxColWidth), "Wertung"));
					//styleHeaderCell(headerRow.createCell(spacerColWidth, ""));
					styleHeaderCell(headerRow.createCell(headerTeamCheckBoxColWidth + spacerColWidth, "in Mannschaftswertung")).setAlign(HorizontalAlignment.RIGHT);
				} else {
					styleHeaderCell(headerRow.createCell(apparatiValueColWidth, "Wertung"));
				}

				table.addHeaderRow(headerRow);

				for (Map.Entry<Integer, LinkedHashMap<String, String>> gymnastEntry : gymnasts.entrySet()) {

					Row<PDPage> emptyRow = table.createRow(12);
					noBorder(emptyRow.createCell(idColWidth, ""));
					noBorder(emptyRow.createCell(nameColWidth, ""));
					noBorder(emptyRow.createCell(apparatiValueColWidth, "")).setBottomBorderStyle(borderStyle);

					if (showTeamCheckbox) {
						noBorder(emptyRow.createCell(spacerColWidth, ""));
						noBorder(emptyRow.createCell(teamCheckBoxColWidth, "")).setBottomBorderStyle(borderStyle);
					}

					LinkedHashMap<String, String> gymnast = gymnastEntry.getValue();
					Row<PDPage>                   row     = table.createRow(24);

					String name = gymnast.getOrDefault("name", "") + "<br>" + gymnast.getOrDefault("club", "");
					/*if (!Common.emptyString(gymnast.getOrDefault("team", "")) && !gymnast.getOrDefault("club", "").equals(gymnast.getOrDefault("team", ""))) {
						name += "<br>" + gymnast.getOrDefault("team", "");
					}*/
					//name += "</i>";

					styleCell(row.createCell(idColWidth, gymnast.getOrDefault("ID", "")));
					styleCell(row.createCell(nameColWidth, name));

					Cell<PDPage> apparatusValueCell = row.createCell(apparatiValueColWidth, "");
					apparatusValueCell.setBottomBorderStyle(borderStyle);
					apparatusValueCell.setTopBorderStyle(borderStyle);
					apparatusValueCell.setLeftBorderStyle(borderStyle);
					apparatusValueCell.setRightBorderStyle(borderStyle);

					if (showTeamCheckbox) {

						styleCell(row.createCell(spacerColWidth, ""));

						Cell<PDPage> teamCheckBoxCell = row.createCell(teamCheckBoxColWidth, "");
						teamCheckBoxCell.setBottomBorderStyle(borderStyle);
						teamCheckBoxCell.setTopBorderStyle(borderStyle);
						teamCheckBoxCell.setLeftBorderStyle(borderStyle);
						teamCheckBoxCell.setRightBorderStyle(borderStyle);
					}
				}

				table.draw();

				stream.close();

				//defaultFooter();
			}

		} catch (Exception e) {
			Common.printError(e);
		}

	}

	protected Cell<PDPage> styleHeaderCell(Cell<PDPage> cell) {
		noBorder(cell);
		cell.setFont(PDType1Font.TIMES_BOLD);
		cell.setFontSize(14f);

		return cell;
	}

	protected Cell<PDPage> styleCell(Cell<PDPage> cell) {
		noBorder(cell);
		cell.setFont(PDType1Font.TIMES_ROMAN);
		cell.setFontSize(12f);

		return cell;
	}

	public void print() {
		this.print(PageFormat.PORTRAIT);
	}
}
