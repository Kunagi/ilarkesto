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
package ilarkesto.rss;

import ilarkesto.base.Utl;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.integration.jdom.JDom;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

public class Rss20Builder {

	private String title;
	private String link;
	private String description;
	private String language;
	private DateAndTime pubDate;
	private String image;

	private List<Item> items = new ArrayList<Item>();

	public Rss20Builder sortItems() {
		Collections.sort(items);
		return this;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Item addItem() {
		Item item = new Item();
		items.add(item);
		return item;
	}

	public Document createXmlDocument() {
		Document doc = new Document();

		Element eRss = new Element("rss");
		doc.setRootElement(eRss);
		eRss.setAttribute("version", "2.0");

		Element eChannel = JDom.addElement(eRss, "channel");
		if (title != null) JDom.addTextElement(eChannel, "title", title);
		if (link != null) JDom.addTextElement(eChannel, "link", link);
		if (description != null) JDom.addTextElement(eChannel, "description", description);
		if (language != null) JDom.addTextElement(eChannel, "language", language);
		if (pubDate != null) JDom.addTextElement(eChannel, "pubDate", pubDate.toString(DateAndTime.FORMAT_RFC822));
		if (image != null) {
			Element eImage = JDom.addElement(eChannel, "image");
			JDom.addTextElement(eImage, "url", image);
			JDom.addTextElement(eImage, "title", "Logo");
		}

		for (Item item : items) {
			Element eItem = JDom.addElement(eChannel, "item");
			item.appendTo(eItem);
		}

		return doc;
	}

	public void removeDuplicates(Item item) {
		for (Item i : new ArrayList<Item>(items)) {
			if (i == item) continue;
			if (Utl.equals(i.guid, item.guid) || (i.enclosure != null && Utl.equals(i.enclosure, item.enclosure))
					|| Utl.equals(i.title, item.title)) {
				items.remove(item);
			}
		}
	}

	public void write(OutputStream out, String encoding) {
		JDom.write(createXmlDocument(), out, encoding);
	}

	@Override
	public String toString() {
		return createXmlDocument().toString();
	}

	public class Item implements Comparable<Item> {

		private String title;
		private String description;
		private String link;
		private String guid;
		private DateAndTime pubDate;
		private String enclosure;

		private void appendTo(Element eItem) {
			if (title != null) JDom.addTextElement(eItem, "title", title);
			if (description != null) JDom.addTextElement(eItem, "description", description);
			if (link != null) JDom.addTextElement(eItem, "link", link);
			if (guid != null) JDom.addTextElement(eItem, "guid", guid);
			if (pubDate != null) JDom.addTextElement(eItem, "pubDate", pubDate.toString(DateAndTime.FORMAT_RFC822));
			if (enclosure != null) {
				Element eEnclosure = JDom.addElement(eItem, "enclosure");
				eEnclosure.setAttribute("url", enclosure);
			}
		}

		public void setLink(String link) {
			this.link = link;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public void setPubDate(DateAndTime pubDate) {
			this.pubDate = pubDate;
		}

		public void setEnclosure(String enclosure) {
			this.enclosure = enclosure;
		}

		@Override
		public int compareTo(Item o) {
			return o.pubDate.compareTo(pubDate);
		}

	}

}
