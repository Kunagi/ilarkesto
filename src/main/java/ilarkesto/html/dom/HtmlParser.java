/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.html.dom;

import ilarkesto.core.base.Parser;
import ilarkesto.core.base.Parser.ParseException;
import ilarkesto.core.logging.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class HtmlParser {

	private static final boolean debug = true;
	private static final Log log = Log.get(HtmlParser.class);

	private HtmlPage page;

	private HtmlDataContainer currentContainer;
	private HtmlTag currentTag;
	private Parser parser;

	public HtmlPage parse(String html) throws ParseException {
		page = new HtmlPage();
		currentContainer = page;

		parser = new Parser(html);
		String prefix = parser.getUntilIf("<html");
		if (prefix != null) {
			parser.gotoAfter(prefix);
			onText(prefix);
		}
		while (!parser.isEnd()) {
			parseNext();
		}

		return page;
	}

	private void parseNext() throws ParseException {
		String text = parser.getUntil("<");
		if (text == null) {
			text = parser.getRemaining();
			onText(text);
			parser.gotoEnd();
			return;
		}

		parser.gotoAfter(text);
		onText(text);

		if (parser.isNext("</")) {
			parseClosingTag();
		} else {
			parseOpeningTag();
		}
	}

	private void parseOpeningTag() throws ParseException {
		parser.gotoAfter("<");
		String tagName = parser.getUntil(" ", "/>", ">");
		parser.gotoAfter(tagName);

		Map<String, String> attributes = null;

		if (parser.isNext(" ")) {
			attributes = parseAttributes();
		}

		boolean closed = false;
		if (parser.isNext("/>")) {
			closed = true;
			parser.gotoAfter("/");
		}

		// next must be ">"
		parser.gotoAfter(">");

		HtmlTag tag = new HtmlTag(currentTag, tagName, attributes, closed);
		onTagStarted(tag);
	}

	private void parseClosingTag() throws ParseException {
		parser.gotoAfter("</");
		String tagName = parser.getUntilAndGotoAfter(">");
		onTagClosed(tagName);
	}

	private Map<String, String> parseAttributes() throws ParseException {
		String s = parser.getUntil(">");
		parser.gotoAfter(s);
		return new LinkedHashMap<String, String>();
	}

	private void onTagStarted(HtmlTag tag) {
		if (debug) log.debug("Start tag:", tag.getName());
		currentContainer.add(tag);
		if (tag.isShort()) {

		} else {
			currentTag = tag;
			currentContainer = tag;
		}
	}

	private void onTagClosed(String tagName) {
		if (debug) log.debug("End tag:", tagName);

		// TODO auto correction here
		currentTag = currentTag.getParent();
		currentContainer = currentTag == null ? page : currentTag;
	}

	private void onText(String text) {
		if (debug) log.debug("Text:", text);
		if (text == null) return;
		if (text.isEmpty()) return;
		currentContainer.add(new HtmlText(text));
	}
}