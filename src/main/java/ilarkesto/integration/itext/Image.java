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

import ilarkesto.pdf.AImage;
import ilarkesto.pdf.APdfBuilder;
import ilarkesto.pdf.APdfElement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.BadElementException;

public class Image extends AImage implements ItextElement {

	private Float height;

	public Image(APdfElement parent, byte[] data) {
		super(parent, data);
	}

	public Image(APdfElement parent, File file) {
		super(parent, file);
	}

	@Override
	public com.lowagie.text.Image getITextElement() {
		com.lowagie.text.Image image;
		try {
			if (data != null) {
				image = com.lowagie.text.Image.getInstance(data);
			} else {
				image = com.lowagie.text.Image.getInstance(file.getPath());
			}
			if (scaleByHeight != null) {
				height = APdfBuilder.mmToPoints(scaleByHeight);
				float width = image.width() * height / image.height();
				image.scaleAbsolute(width, height);
			} else if (scaleByWidth != null) {
				float width = APdfBuilder.mmToPoints(scaleByWidth);
				height = image.height() * width / image.width();
				image.scaleAbsolute(width, height);
			} else {
				height = image.height();
			}
			image.setIndentationLeft(APdfBuilder.mmToPoints(marginLeft));
			image.setIndentationRight(APdfBuilder.mmToPoints(marginRight));
			image.setSpacingBefore(APdfBuilder.mmToPoints(marginTop));
			image.setSpacingAfter(APdfBuilder.mmToPoints(marginBottom));
		} catch (BadElementException ex) {
			throw new RuntimeException(ex);
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		image.setWidthPercentage(0.1f);

		if (align != null) image.setAlignment(convertAlign(align));

		return image;
	}

	public float getHeight() {
		if (height == null) {
			height = getITextElement().height();
		}
		return height;
	}

	public static int convertAlign(Align align) {
		switch (align) {
			case LEFT:
				return com.lowagie.text.Image.ALIGN_RIGHT | com.lowagie.text.Image.ALIGN_TOP;
			case RIGHT:
				return com.lowagie.text.Image.ALIGN_RIGHT | com.lowagie.text.Image.ALIGN_TOP;
		}
		throw new RuntimeException("Unsupported align: " + align);
	}

	// --- dependencies ---

}
