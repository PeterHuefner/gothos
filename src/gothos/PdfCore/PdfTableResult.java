package gothos.PdfCore;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.*;

public class PdfTableResult extends Pdf {

	protected BaseTable           table;

	protected BaseTable prepareTable(float margin, float bottomMargin, float yPosition) throws java.io.IOException {

		if (margin < 0) {
			margin = 30;
		}

		if (bottomMargin < 0) {
			bottomMargin = 70;
		}

		if (yPosition < 0) {
			// y position is your coordinate of top left corner of the table
			yPosition = 550;
		}

		// starting y position is whole page height subtracted by top and bottom margin
		float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
		// we want table across whole page width (subtracted by left and right margin ofcourse)
		float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

		boolean drawContent  = true;
		float   yStart       = yStartNewPage;

		this.table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true, drawContent);

		return this.table;
	}

	protected BaseTable prepareTable() throws java.io.IOException {
		return prepareTable(30, 70, 540);
	}

	protected void handleBorders() {

		LineStyle borderStyle   = new LineStyle(new Color(0), 0.5f);
		LineStyle noBorderStyle = new LineStyle(new Color(0xFFFFFF), 0);

		for (Row<PDPage> row : table.getRows()) {
			int i = 0;
			for (Cell<PDPage> cell : row.getCells()) {
				cell.setBottomBorderStyle(borderStyle);

				cell.setTopBorderStyle(noBorderStyle);
				cell.setRightBorderStyle(noBorderStyle);
				cell.setLeftBorderStyle(noBorderStyle);

				if (i == 0) {
					//cell.setLeftBorderStyle(borderStyle);
				}
				i++;
			}
		}
	}

	protected void noBorders() {

		LineStyle noBorderStyle = new LineStyle(new Color(0xFFFFFF), 0);

		for (Row<PDPage> row : table.getRows()) {
			for (Cell<PDPage> cell : row.getCells()) {
				cell.setBottomBorderStyle(noBorderStyle);
				cell.setTopBorderStyle(noBorderStyle);
				cell.setRightBorderStyle(noBorderStyle);
				cell.setLeftBorderStyle(noBorderStyle);
			}
		}
	}

	protected Cell<PDPage> noBorder (Cell <PDPage> cell) {
		LineStyle noBorderStyle = new LineStyle(new Color(0xFFFFFF), 0);
		cell.setBottomBorderStyle(noBorderStyle);
		cell.setTopBorderStyle(noBorderStyle);
		cell.setRightBorderStyle(noBorderStyle);
		cell.setLeftBorderStyle(noBorderStyle);
		return cell;
	}
}
