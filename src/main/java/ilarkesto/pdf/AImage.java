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
package ilarkesto.pdf;

import java.io.File;

public abstract class AImage extends AParagraphElement {

	public enum Align {
		LEFT, RIGHT
	}

	protected byte[] data;
	protected File file;
	protected Float scaleByHeight;
	protected Float scaleByWidth;
	protected Align align;
	protected float marginTop = 0f;
	protected float marginRight = 0f;
	protected float marginBottom = 0f;
	protected float marginLeft = 0f;

	public AImage(APdfElement parent, byte[] data) {
		super(parent);
		this.data = data;
	}

	public AImage(APdfElement parent, File file) {
		super(parent);
		this.file = file;
	}

	public Align getAlign() {
		return align;
	}

	// --- helper ---

	public AImage setAlignLeft() {
		return setAlign(Align.LEFT);
	}

	public AImage setAlignRight() {
		return setAlign(Align.RIGHT);
	}

	public AImage setMargin(float top, float right, float bottom, float left) {
		setMarginTop(top);
		setMarginRight(right);
		setMarginBottom(bottom);
		setMarginLeft(left);
		return this;
	}

	public AImage setMargin(float topBottom, float leftRight) {
		return setMargin(topBottom, leftRight, topBottom, leftRight);
	}

	public AImage setMargin(float margin) {
		setMarginTop(margin);
		setMarginRight(margin);
		setMarginBottom(margin);
		setMarginLeft(margin);
		return this;
	}

	// --- dependencies ---

	public AImage setMarginTop(float marginTop) {
		this.marginTop = marginTop;
		return this;
	}

	public AImage setMarginRight(float marginRight) {
		this.marginRight = marginRight;
		return this;
	}

	public AImage setMarginBottom(float marginBottom) {
		this.marginBottom = marginBottom;
		return this;
	}

	public AImage setMarginLeft(float marginLeft) {
		this.marginLeft = marginLeft;
		return this;
	}

	public AImage setScaleByHeight(Float scaleByHeight) {
		this.scaleByHeight = scaleByHeight;
		return this;
	}

	public AImage setScaleByWidth(Float scaleByWidth) {
		this.scaleByWidth = scaleByWidth;
		return this;
	}

	public AImage setAlign(Align align) {
		this.align = align;
		return this;
	}

}
