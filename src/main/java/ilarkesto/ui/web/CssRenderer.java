/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.ui.web;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CssRenderer {

	private PrintWriter out;
	private Style style;

	public CssRenderer() {
		this(new PrintWriter(new StringWriter()));
	}

	public CssRenderer(PrintWriter out) {
		this.out = out;
	}

	public void flush() {
		endStartedStyle();
		out.flush();
	}

	public Style style(String name) {
		endStartedStyle();
		out.print(name);
		out.print(" {");
		style = new Style();
		return style;
	}

	public Style html() {
		return style("html");
	}

	public Style body() {
		return style("body");
	}

	public Style table() {
		return style("table");
	}

	public Style input() {
		return style("input");
	}

	public Style textarea() {
		return style("textarea");
	}

	public Style select() {
		return style("select");
	}

	public Style form() {
		return style("form");
	}

	public Style iframe() {
		return style("iframe");
	}

	public Style h1() {
		return style("h1");
	}

	public Style h2() {
		return style("h2");
	}

	public Style h3() {
		return style("h3");
	}

	public Style h4() {
		return style("h4");
	}

	public Style h5() {
		return style("h5");
	}

	public Style h6() {
		return style("h6");
	}

	public Style blockquote() {
		return style("blockquote");
	}

	public Style pre() {
		return style("pre");
	}

	public Style code() {
		return style("code");
	}

	public Style ul() {
		return style("ul");
	}

	public Style ol() {
		return style("ol");
	}

	public Style td() {
		return style("td");
	}

	public Style div() {
		return style("div");
	}

	public Style a() {
		return style("a");
	}

	public Style aHover() {
		return style("a:hover");
	}

	public Style p() {
		return style("p");
	}

	public Style img() {
		return style("img");
	}

	private void endStartedStyle() {
		if (style == null) return;
		out.println(" }");
		style = null;
	}

	@Override
	public String toString() {
		return out.toString();
	}

	public class Style {

		public Style zIndex(int value) {
			return attr("z-index", value);
		}

		public Style positionAbsolute() {
			return position("absolute");
		}

		public Style positionFixed(int top, int left) {
			positionFixed();
			top(top, "px");
			return left(left, "px");
		}

		public Style top(int value, String unit) {
			return attr("top", value + unit);
		}

		public Style top(int value) {
			return top(value, "px");
		}

		public Style left(int value) {
			return left(value, "px");
		}

		public Style left(int value, String unit) {
			return attr("left", value + unit);
		}

		public Style positionRelative() {
			return position("relative");
		}

		public Style positionFixed() {
			return position("fixed");
		}

		public Style position(String value) {
			return attr("position", value);
		}

		public Style listStyleNone() {
			return listStyle("none");
		}

		public Style listStyle(String value) {
			return attr("list-style", value);
		}

		public Style borderTop(int width, String color) {
			return borderTop(width, "solid", color);
		}

		public Style borderTop(int width, String style, String color) {
			return borderTop(width + "px " + style + " " + color);
		}

		public Style borderTop(String value) {
			return attr("border-top", value);
		}

		public Style borderLeft(String value) {
			return attr("border-left", value);
		}

		public Style borderRight(String value) {
			return attr("border-right", value);
		}

		public Style borderBottom(int width, String color) {
			return borderBottom(width, "solid", color);
		}

		public Style borderBottom(int width, String style, String color) {
			return borderBottom(width + "px " + style + " " + color);
		}

		public Style borderBottom(String value) {
			return attr("border-bottom", value);
		}

		public Style displayNone() {
			return display("none");
		}

		public Style displayInline() {
			return display("inline");
		}

		public Style displayBlock() {
			return display("block");
		}

		public Style display(String value) {
			return attr("display", value);
		}

		public Style whiteSpaceNowrap() {
			return whiteSpace("nowrap");
		}

		public Style whiteSpaceNormal() {
			return whiteSpace("normal");
		}

		public Style whiteSpace(String value) {
			return attr("white-space", value);
		}

		public Style textAlignCenter() {
			return textAlign("center");
		}

		public Style textAlignLeft() {
			return textAlign("left");
		}

		public Style textAlignRight() {
			return textAlign("right");
		}

		public Style textAlignJustify() {
			return textAlignJustify("auto");
		}

		public Style textAlignJustify(String type) {
			return textAlign("justify").attr("text-justify", type);
		}

		public Style textAlign(String value) {
			return attr("text-align", value);
		}

		public Style float_(String value) {
			return attr("float", value);
		}

		public Style floatLeft() {
			return float_("left");
		}

		public Style floatRight() {
			return float_("right");
		}

		public Style clearBoth() {
			return clear("both");
		}

		public Style clear(String value) {
			return attr("clear", value);
		}

		public Style textDecoration(String value) {
			return attr("text-decoration", value);
		}

		public Style textDecorationNone() {
			return textDecoration("none");
		}

		public Style textDecorationBlink() {
			return textDecoration("blink");
		}

		public Style textDecorationLineThrough() {
			return textDecoration("line-through");
		}

		public Style textDecorationUnderline() {
			return textDecoration("underline");
		}

		public Style alignRight() {
			return align("right");
		}

		public Style alignLeft() {
			return align("left");
		}

		public Style alignCenter() {
			return align("center");
		}

		public Style align(String value) {
			return attr("align", value);
		}

		public Style overflowVisible() {
			return overflow("visible");
		}

		public Style overflowHidden() {
			return overflow("hidden");
		}

		public Style overflowAuto() {
			return overflow("auto");
		}

		public Style overflow(String value) {
			return attr("overflow", value);
		}

		public Style maxWidth(int pixels) {
			return maxWidth(pixels, "px");
		}

		public Style maxWidth(int value, String unit) {
			return attr("max-width", value + unit);
		}

		public Style minWidth(int pixels) {
			return minWidth(pixels, "px");
		}

		public Style minWidth(int value, String unit) {
			return attr("min-width", value + unit);
		}

		public Style maxHeight(int pixels) {
			return maxHeight(pixels, "px");
		}

		public Style maxHeight(int value, String unit) {
			return attr("max-height", value + unit);
		}

		public Style minHeight(int pixels) {
			return minHeight(pixels, "px");
		}

		public Style minHeight(int value, String unit) {
			return attr("min-height", value + unit);
		}

		public Style height(int pixels) {
			return height(pixels, "px");
		}

		public Style height(int value, String unit) {
			return attr("height", value + unit);
		}

		public Style height100() {
			return height(100, "%");
		}

		public Style width100() {
			return width(100, "%");
		}

		public Style verticalAlignTop() {
			return verticalAlign("top");
		}

		public Style verticalAlignMiddle() {
			return verticalAlign("middle");
		}

		public Style verticalAlignBottom() {
			return verticalAlign("bottom");
		}

		public Style verticalAlign(String value) {
			return attr("vertical-align", value);
		}

		public Style borderRadius(int value) {
			attr("-webkit-border-radius", value + "px");
			return attr("-moz-border-radius", value + "px");
		}

		public Style columnWidth(int value) {
			attr("-webkit-column-width", value + "px");
			return attr("-moz-column-width", value + "px");
		}

		public Style columnGap(int value) {
			attr("-webkit-column-gap", value + "px");
			return attr("-moz-column-gap", value + "px");
		}

		public Style columnCount(int value) {
			attr("-webkit-column-count", value);
			return attr("-moz-column-count", value);
		}

		public Style borderNone() {
			return border("none");
		}

		public Style border(int width, String color) {
			return border(width, "solid", color);
		}

		public Style border(int width, String style, String color) {
			return border(width + "px " + style + " " + color);
		}

		public Style border(String value) {
			return attr("border", value);
		}

		public Style lineHeight(int pixels) {
			return lineHeight(pixels + "px");
		}

		public Style lineHeight(String value) {
			return attr("line-height", value);
		}

		public Style borderWidth(int value) {
			return attr("border-width", value + "px");
		}

		public Style width(int value) {
			return width(value, "px");
		}

		public Style width(int value, String unit) {
			return attr("width", value + unit);
		}

		public Style colorWhite() {
			return color("white");
		}

		public Style colorBlack() {
			return color("black");
		}

		public Style colorGray() {
			return color("gray");
		}

		public Style color(String value) {
			return attr("color", value);
		}

		public Style borderCollapseCollapse() {
			return borderCollapse("collapse");
		}

		public Style borderCollapse(String value) {
			return attr("border-collapse", value);
		}

		public Style fontWeightNormal() {
			return fontWeight("normal");
		}

		public Style fontWeightBold() {
			return fontWeight("bold");
		}

		public Style fontWeight(String value) {
			return attr("font-weight", value);
		}

		public Style fontStyleNormal() {
			return fontStyle("normal");
		}

		public Style fontStyleItalic() {
			return fontStyle("italic");
		}

		public Style fontStyle(String value) {
			return attr("font-style", value);
		}

		public Style fontFamilyMonospace() {
			return fontFamily("monospace");
		}

		public Style fontFamilySerif() {
			return fontFamily("serif");
		}

		public Style fontFamilySansSerif() {
			return fontFamily("sans-serif");
		}

		public Style fontFamily(String value) {
			return attr("font-family", value);
		}

		public Style fontSize(int value) {
			return fontSize(value + "px");
		}

		public Style fontSize(String value) {
			return attr("font-size", value);
		}

		public Style marginBottom(int value) {
			return attr("margin-bottom", value + "px");
		}

		public Style marginTop(int value) {
			return attr("margin-top", value + "px");
		}

		public Style marginRight(int value) {
			return attr("margin-right", value + "px");
		}

		public Style marginLeft(int value) {
			return attr("margin-left", value + "px");
		}

		public Style margin(int value) {
			return attr("margin", value + "px");
		}

		public Style margin(int top, int right, int bottom, int left) {
			return margin(top + "px " + right + "px " + bottom + "px " + left + "px");
		}

		public Style margin(int topBottom, int leftRight) {
			return margin(topBottom + "px " + leftRight + "px");
		}

		public Style margin(String margin) {
			return attr("margin", margin);
		}

		public Style padding(int value) {
			return attr("padding", value + "px");
		}

		public Style padding(int top, int right, int bottom, int left) {
			return attr("padding", top + "px " + right + "px " + bottom + "px " + left + "px");
		}

		public Style padding(int topBottom, int leftRight) {
			return attr("padding", topBottom + "px " + leftRight + "px");
		}

		public Style paddingLeft(int value) {
			return attr("padding-left", value + "px");
		}

		public Style paddingRight(int value) {
			return attr("padding-right", value + "px");
		}

		public Style paddingTop(int value) {
			return attr("padding-top", value + "px");
		}

		public Style paddingBottom(int value) {
			return attr("padding-bottom", value + "px");
		}

		public Style background(String value) {
			return attr("background", value);
		}

		public Style background(String color, String url) {
			return attr("background", color + " url(" + url + ")");
		}

		public Style background(String color, String url, String repeat) {
			return attr("background", color + " url(" + url + ") " + repeat);
		}

		public Style backgroundUrl(String imageUrl) {
			return background("url(" + imageUrl + ")");
		}

		public Style backgroundNone() {
			return background("none");
		}

		public Style backgroundTransparent() {
			return background("transparent");
		}

		public Style backgroundBlack() {
			return background("black");
		}

		public Style backgroundWhite() {
			return background("white");
		}

		public Style cursorPointer() {
			return cursor("pointer");
		}

		public Style cursorDefault() {
			return cursor("default");
		}

		public Style cursorMove() {
			return cursor("move");
		}

		public Style cursor(String value) {
			return attr("cursor", value);
		}

		private Style attr(String name, int value) {
			return attr(name, String.valueOf(value));
		}

		private Style attr(String name, String value) {
			out.print(" ");
			out.print(name);
			out.print(": ");
			out.print(value);
			out.print(";");
			return this;
		}

	}

}
