/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.itext;

import ilarkesto.pdf.ACell;
import ilarkesto.pdf.AImage;
import ilarkesto.pdf.AParagraph;
import ilarkesto.pdf.APdfBuilder;
import ilarkesto.pdf.APdfElement;
import ilarkesto.pdf.ATable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;

public class Cell extends ACell implements ItextElement {

	private Collection<ItextElement> elements = new ArrayList<ItextElement>();

	public Cell(APdfElement parent) {
		super(parent);
	}

	@Override
	public AParagraph paragraph() {
		Paragraph p = new Paragraph(this);
		p.setDefaultFontStyle(getFontStyle());
		elements.add(p);
		return p;
	}

	@Override
	public AImage image(File file) {
		Image i = new Image(this, file);
		elements.add(i);
		return i;
	}

	@Override
	public ATable table(float... cellWidths) {
		Table t = new Table(this);
		t.setCellWidths(cellWidths);
		elements.add(t);
		return t;
	}

	@Override
	public ATable table(int columnCount) {
		Table t = new Table(this);
		t.setColumnCount(columnCount);
		elements.add(t);
		return t;
	}

	@Override
	public AImage image(byte[] data) {
		Image i = new Image(this, data);
		elements.add(i);
		return i;
	}

	@Override
	public Element getITextElement() {
		PdfPCell cell = new PdfPCell();
		cell.setBorderColorTop(getBorderTopColor());
		cell.setBorderColorBottom(getBorderBottomColor());
		cell.setBorderColorLeft(getBorderLeftColor());
		cell.setBorderColorRight(getBorderRightColor());
		cell.setBorderWidthTop(APdfBuilder.mmToPoints(getBorderTopWidth()));
		cell.setBorderWidthBottom(APdfBuilder.mmToPoints(getBorderBottomWidth()));
		cell.setBorderWidthLeft(APdfBuilder.mmToPoints(getBorderLeftWidth()));
		cell.setBorderWidthRight(APdfBuilder.mmToPoints(getBorderRightWidth()));
		cell.setPadding(0);
		cell.setPaddingTop(APdfBuilder.mmToPoints(getPaddingTop()));
		cell.setPaddingBottom(APdfBuilder.mmToPoints(getPaddingBottom()));
		cell.setPaddingLeft(APdfBuilder.mmToPoints(getPaddingLeft()));
		cell.setPaddingRight(APdfBuilder.mmToPoints(getPaddingRight()));
		cell.setExtraParagraphSpace(0);
		cell.setIndent(0);
		cell.setUseBorderPadding(false);
		cell.setBackgroundColor(getBackgroundColor());
		cell.setColspan(getColspan());
		for (ItextElement element : elements)
			cell.addElement(element.getITextElement());
		return cell;
	}

	// --- dependencies ---

}
