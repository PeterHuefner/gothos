package gothos.competitionMainForm.Teams;

import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.PdfCore.PdfTableResult;
import gothos.competitionMainForm.Classes.PdfClassResult;
import gothos.competitionMainForm.Gymnast;
import gothos.competitionMainForm.Team;
import gothos.competitionMainForm.TeamGymnastApparatusInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.awt.print.PageFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class PdfTeamResult extends PdfTableResult {

	protected CompetitionData competitionData;
	protected boolean         showGymnasts = true;
	protected boolean         showClubs    = true;

	public void setShowClubs(boolean showClubs) {
		this.showClubs = showClubs;
	}

	public void setShowGymnasts(boolean showGymnasts) {
		this.showGymnasts = showGymnasts;
	}

	public PdfTeamResult() {
		competitionData = new CompetitionData();
	}

	public void generatePdf() {

		LinkedHashMap<String, String> competitionInfo = competitionData.getCompetitionData();
		ArrayList<Team>               teams           = competitionData.calculateTeamResult();

		if (Common.emptyString(competitionInfo.get("longname"))) {
			competitionInfo.put("logname", competitionInfo.get("name"));
		}

		document = new PDDocument();
		PDDocumentInformation information = document.getDocumentInformation();

		information.setAuthor("gothos - Wettkampfverwaltung");
		information.setCreator("gothos - Wettkampfverwaltung");
		information.setTitle("Protokoll - Mannschaftswertung");
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

			stream.showText(competitionInfo.get("longname"));

			suggestedFileName = "Mannschaftfswertung_" + competitionInfo.get("longname");
			suggestedFileName = suggestedFileName.replaceAll("\\s", "_") + ".pdf";

			stream.endText();

			if (teams.size() > 0) {

				int   allApparatiWidth = 65;
				int   apparatiWidth    = (allApparatiWidth / teams.get(0).getApparatiValues().size());
				int   diffSpace        = allApparatiWidth - (teams.get(0).getApparatiValues().size() * apparatiWidth);
				float rowHeight        = 15;
				float gymnastRowHeight = 24;

				if (!showClubs) {
					gymnastRowHeight = rowHeight;
				}

				LineStyle borderStyle = new LineStyle(new Color(0), 0.5f);
				prepareTable();

				Row<PDPage> headerRow = table.createRow(24);

				noBorder(headerRow.createCell(5, "Startnr."));
				noBorder(headerRow.createCell(20 + diffSpace, ""));


				for (Map.Entry<String, Double> apparatus : teams.get(0).getApparatiValues().entrySet()) {
					noBorder(headerRow.createCell(apparatiWidth, apparatus.getKey()));
				}

				noBorder(headerRow.createCell(5, "<b>Punkte</b>"));
				noBorder(headerRow.createCell(5, "<b>Platz</b>"));

				table.addHeaderRow(headerRow);

				for (Team team : teams) {

					Row<PDPage> teamHeaderRow = table.createRow(rowHeight);

					noBorder(teamHeaderRow.createCell(5 + 20 + diffSpace, "<b>" + team.getName() + "</b>"));
					noBorder(teamHeaderRow.createCell(65, ""));
					noBorder(teamHeaderRow.createCell(5, ""));

					noBorder(teamHeaderRow.createCell(5, ""));
					//teamHeaderRow.createCell(5, "<b>" + team.getRanking().toString() + "</b>");

					if (showGymnasts) {

						for (Gymnast gymnast : team.getGymnasts()) {
							Row<PDPage> gymnastRow = table.createRow(gymnastRowHeight);

							noBorder(gymnastRow.createCell(5, gymnast.getMetaData().get("ID")));
							noBorder(gymnastRow.createCell(20 + diffSpace, gymnast.getMetaData().get("name") + (showClubs ? "<br><i>" + gymnast.getMetaData().get("club") + "</i>" : "")));

							for (Map.Entry<String, Double> apparatus : team.getApparatiValues().entrySet()) {
								String apparatusValue = "";
								if (team.getGymnastsApparatiInfo().containsKey(apparatus.getKey())) {
									ArrayList<TeamGymnastApparatusInfo> infos = team.getGymnastsApparatiInfo().get(apparatus.getKey());
									for (TeamGymnastApparatusInfo info : infos) {
										if (info.gymnast == gymnast.getROWID()) {
											if (gymnast.getApparatiValues().get(apparatus.getKey()) != null) {
												if (!info.isTeamValue) {
													apparatusValue = "<i>-" + String.format("%2.3f", gymnast.getApparatiValues().get(apparatus.getKey())) + "-</i>";
												} else {
													apparatusValue = String.format("%2.3f", gymnast.getApparatiValues().get(apparatus.getKey()));
												}
											}

											break;
										}
									}
								}

								gymnast.getApparatiValues().putIfAbsent(apparatus.getKey(), 0.0);
								noBorder(gymnastRow.createCell(apparatiWidth, apparatusValue));
							}

							noBorder(gymnastRow.createCell(7, String.format("%2.3f", gymnast.getSum())));
							noBorder(gymnastRow.createCell(3, ""));

							if (team.getGymnasts().indexOf(gymnast) == team.getGymnasts().size() - 1) {
								LineStyle border = new LineStyle(new Color(0), 0.5f);
								int       i      = 0;
								for (Cell<PDPage> cell : gymnastRow.getCells()) {
									if (i > 1) {
										cell.setBottomBorderStyle(border);
									}
									i++;
								}
							}
						}

					}

					Row<PDPage> teamFooterRow = table.createRow(rowHeight);

					noBorder(teamFooterRow.createCell(5 + 20 + diffSpace, ""));

					for (Map.Entry<String, Double> apparatus : team.getApparatiValues().entrySet()) {
						noBorder(teamFooterRow.createCell(apparatiWidth, String.format("%2.3f", apparatus.getValue())));
					}

					noBorder(teamFooterRow.createCell(7, "<b>" + String.format("%2.3f", team.getSum()) + "</b>"));
					noBorder(teamFooterRow.createCell(3, "<b>" + team.getRanking() + "</b>"));

					if (teams.indexOf(team) != teams.size() - 1) {
						table.createRow(rowHeight);
					}
				}

				//handleBorders();
				//noBorders();

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
