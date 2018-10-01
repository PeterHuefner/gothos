package gothos.competitionMainForm.Certificates;

import be.quodlibet.boxable.Paragraph;
import gothos.Application;
import gothos.Common;
import gothos.DatabaseCore.CompetitionData;
import gothos.DatabaseCore.DatabaseParameter;
import gothos.PdfCore.Pdf;
import gothos.competitionMainForm.Gymnast;
import gothos.competitionMainForm.Team;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.print.PageFormat;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfCertificate extends Pdf {

	protected String                                                className;
	protected boolean                                               teamCertificate  = false;
	protected String                                                certType         = "single";
	protected String                                                table;
	protected LinkedHashMap<Integer, LinkedHashMap<String, String>> certificateLines;
	protected float                                                 verticalMargin   = 4;
	protected float                                                 horizontalMargin = 2;
	protected CompetitionData                                       competitionData;
	protected LinkedHashMap<String, String>                         competitionInfo;
	protected int                                                   counter = 0;

	protected ArrayList<Team>    teamResult;
	protected ArrayList<Gymnast> classResult;

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

		competitionData = new CompetitionData();
		competitionInfo = competitionData.getCompetitionData();

		loadMargins();
		loadCertificateLines();

		document = new PDDocument();


		try {

			/*page = new PDPage(
					new PDRectangle(-millimeterToPoints(100), millimeterToPoints(40), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight())
			);
			document.addPage(page);
			stream = new PDPageContentStream(document, page);

			drawText("Linke Welt", PDType1Font.TIMES_ROMAN, 12, "left", 1);
			drawText("Linke Welt", PDType1Font.TIMES_ROMAN, 12, "left", 1);
			drawText("Linke Welt", PDType1Font.TIMES_ROMAN, 18, "left", 1.2f);
			drawText("Linke Welt", PDType1Font.TIMES_ROMAN, 12, "left", 1.5f);
			drawText("Linke Welt", PDType1Font.TIMES_ROMAN, 12, "left", 2f);

			stream.close();/**/


			setFont(PDType1Font.TIMES_ROMAN);
			setFontSize(14);

			loadGymnastData();

			boolean pageStatus;
			do {
				pageStatus = printPage();
			} while (pageStatus);

			/*drawText("Hallo Welt");
			drawText("Linke Welt");
			drawCenteredText("Zentrierte Welt");
			drawText("Linke Welt");
			drawRightText("Rechte Welt");
			drawCenteredText("Zentrierte Welt");
			drawCenteredText("Zentrierte Welt");
			drawRightText("Rechte Welt");
			drawText("Linke Welt");*/

		} catch (Exception e) {
			Common.printError(e);
		}
	}

	protected void loadMargins() {
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
	}

	protected void loadCertificateLines() {
		ArrayList<DatabaseParameter> params = new ArrayList<>();
		params.add(new DatabaseParameter(certType));
		certificateLines = Application.database.fetchAllIndexed("SELECT ROWID, * FROM " + table + " WHERE type = ? ORDER BY ROWID;", params, "ROWID");

		if (certificateLines.size() == 0) {
			Common.showError("Es konnte keine Urkunden-Konfiguration geladen werden. Die erstellten Seiten werden vermutlich leer sein.\nLegen Sie vor dem Erstellen von Urkunden erst die Inhalte unter 'Urkunden verwalten' in einem geladenen Wettkampf an.");
		}
	}

	protected void loadGymnastData() {
		if (certType.equals("single")) {

			if (!Common.emptyString(className)) {
				competitionData.setClassName(className);
				classResult = competitionData.calculateClassResult();
			} else {
				Common.showMessage("Keine Alterklasse zur Berechnung erhalten");
			}

		} else {
			teamResult = competitionData.calculateTeamResult();
		}
	}

	protected boolean printPage() {
		boolean status;

		LinkedHashMap<String, String> placeholder = new LinkedHashMap<>();

		placeholder.put("COMPETITION_NAME", competitionInfo.getOrDefault("longname", ""));
		placeholder.put("COMPETITION_DAY", competitionInfo.getOrDefault("competitionDay", ""));
		placeholder.put("COMPETITION_DESCRIPTION", competitionInfo.getOrDefault("description", ""));

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat();

		dateFormat.applyPattern("dd");
		placeholder.put("DAY", dateFormat.format(now));

		dateFormat.applyPattern("MM");
		placeholder.put("MONTH", dateFormat.format(now));

		dateFormat.applyPattern("yyyy");
		placeholder.put("YEAR", dateFormat.format(now));

		/*for (Map.Entry<String, String> competitionInfoBlock: competitionInfo.entrySet()) {
			placeholder.put("COMPETITION_" + competitionInfoBlock.getKey().toUpperCase(), competitionInfoBlock.getValue());
		}*/

		if (certType.equals("single")) {
			if (counter < classResult.size()) {
				status = true;

				Gymnast gymnast = classResult.get(counter);

				counter++;

				for (Map.Entry<String, String> gymnastEntry: gymnast.getMetaData().entrySet()) {
					placeholder.put(gymnastEntry.getKey().toUpperCase(), gymnastEntry.getValue());
				}

				placeholder.put("RANKING", gymnast.getRanking().toString());
				placeholder.put("POINTS", String.format("%2.3f", gymnast.getSum()));

			} else {
				status = false;
			}
		} else {
			if (counter < teamResult.size()) {
				status = true;

				Team team = teamResult.get(counter);

				counter++;

				placeholder.put("NAME", team.getName());
				placeholder.put("RANKING", team.getRanking().toString());
				placeholder.put("POINTS", String.format("%2.3f", team.getSum()));

			} else {
				status = false;
			}
		}

		if (status) {
			addPage(-millimeterToPoints(horizontalMargin * 10), millimeterToPoints(verticalMargin * 10), PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());

			try {
				stream = new PDPageContentStream(document, page);

				for (Map.Entry<Integer, LinkedHashMap<String, String>> line: certificateLines.entrySet()) {
					printLine(line.getValue(), placeholder);
				}

				stream.close();
			} catch (Exception e) {
				Common.printError(e);
			}
		}

		return status;
	}

	protected void printLine(LinkedHashMap<String, String> lineConfig, LinkedHashMap<String, String> placeholders) {

		String line = lineConfig.get("line");
		String font = lineConfig.get("font");
		String size = lineConfig.get("size");
		String height = lineConfig.get("height");
		String weight = lineConfig.get("weight");
		String align = lineConfig.get("align");

		if (Common.emptyString(font)) {
			font = "Times New Roman";
		}

		if (Common.emptyString(size)) {
			size = "12";
		}

		if (Common.emptyString(weight)) {
			weight = "normal";
		}

		if (Common.emptyString(align)) {
			align = "zentriert";
		}

		if (Common.emptyString(height)) {
			height = "1.0";
		}

		if (!Common.emptyString(line)) {

			Matcher matches = Pattern.compile("\\$\\$([^\\$]+)\\$\\$").matcher(line);
			while (matches.find()) {
				String placeholder = matches.group(1);
				if (placeholders.get(placeholder) != null) {
					line = line.replaceAll("\\$\\$" + placeholder + "\\$\\$", placeholders.get(placeholder));
				}
			}

			size = size.replaceAll(",", ".");
			float fontSize = Float.parseFloat(size);
			if (fontSize <= 0) {
				fontSize = 12;
			}
			setFontSize(fontSize);

			switch (font) {
				case "Times New Roman" :
					setFont(PDType1Font.TIMES_ROMAN);

					switch (weight) {
						case "fett" :
							setFont(PDType1Font.TIMES_BOLD);
							break;
						case "kursiv" :
							setFont(PDType1Font.TIMES_ITALIC);
							break;
						case "fett-kursiv" :
							setFont(PDType1Font.TIMES_BOLD_ITALIC);
							break;
					}

					break;
				case "Courier" :
					setFont(PDType1Font.COURIER);

					switch (weight) {
						case "fett" :
							setFont(PDType1Font.COURIER_BOLD);
							break;
						case "kursiv" :
							setFont(PDType1Font.COURIER_OBLIQUE);
							break;
						case "fett-kursiv" :
							setFont(PDType1Font.COURIER_BOLD_OBLIQUE);
							break;
					}

					break;
				case "Helvetica" :
					setFont(PDType1Font.HELVETICA);

					switch (weight) {
						case "fett" :
							setFont(PDType1Font.HELVETICA_BOLD);
							break;
						case "kursiv" :
							setFont(PDType1Font.HELVETICA_OBLIQUE);
							break;
						case "fett-kursiv" :
							setFont(PDType1Font.HELVETICA_BOLD_OBLIQUE);
							break;
					}

					break;
				default:
					setFont(PDType1Font.TIMES_ROMAN);
			}

			try {

				height = height.replaceAll(",", ".");
				float lineHeight = Float.parseFloat(height);
				if (lineHeight <= 0) {
					lineHeight = 1f;
				}

				switch (align) {
					case "links":
						drawText(line, currentFont, currentFontsize, "left", lineHeight);
						break;
					case "rechts":
						drawText(line, currentFont, currentFontsize, "right", lineHeight);
						break;
					default:
						drawText(line, currentFont, currentFontsize, "center", lineHeight);
				}
			} catch (Exception e) {
				Common.printError(e);
			}
		}
	}

	public void print() {
		this.print(PageFormat.PORTRAIT);
	}
}
