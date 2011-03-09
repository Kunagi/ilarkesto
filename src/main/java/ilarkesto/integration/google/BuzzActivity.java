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
package ilarkesto.integration.google;

import ilarkesto.base.Str;
import ilarkesto.base.Tm;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.integration.jdom.JDom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

public class BuzzActivity {

	private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private String id;
	private DateAndTime dateAndTime;
	private String href;
	private String authorName;
	private String authorUri;
	private String authorPhotoUrl;
	private String content;
	private String attachmentHref;
	private String geoPoint;
	private String geoLabel;

	private static BuzzActivity parseActivity(Element eEntry) {
		BuzzActivity a = new BuzzActivity();

		a.setId(JDom.getChildText(eEntry, "id"));
		try {
			String vUpdated = JDom.getChildText(eEntry, "updated");
			DateAndTime dateAndTime = DateAndTime.parse(vUpdated, new SimpleDateFormat(TIME_FORMAT));
			dateAndTime = dateAndTime.toTimezone(Tm.TZ_BERLIN);
			DateAndTime updated = dateAndTime;
			a.setDateAndTime(updated);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
		a.setHref(JDom.getChildByAttribute(eEntry, "link", "rel", "alternate").getAttributeValue("href"));

		a.setGeoPoint(JDom.getChildText(eEntry, "georss:point"));
		a.setGeoLabel(JDom.getChildText(eEntry, "georss:featureName"));

		Element eAuthor = JDom.getChild(eEntry, "author");
		a.setAuthorName(JDom.getChildText(eAuthor, "name"));
		a.setAuthorUri(JDom.getChildText(eAuthor, "uri"));
		a.setAuthorPhotoUrl(JDom.getChildText(eAuthor, "poco:photoUrl"));

		Element eObject = JDom.getChild(eEntry, "activity:object");
		String content = JDom.getChildText(eObject, "content");
		content = Str.html2text(content);
		a.setContent(content);

		Element eAttachment = JDom.getChild(eObject, "buzz:attachment");
		if (eAttachment != null) {
			Element eLink = JDom.getChildByAttribute(eAttachment, "link", "rel", "enclosure");
			if (eLink != null) a.setAttachmentHref(eLink.getAttributeValue("href"));
		}

		return a;
	}

	public static List<BuzzActivity> parseActivities(String xml) {
		Document doc = JDom.createDocument(xml);
		Element eRoot = doc.getRootElement();

		List<Element> eEntrys = JDom.getChildren(eRoot, "entry");
		List<BuzzActivity> ret = new ArrayList<BuzzActivity>(eEntrys.size());
		for (Element eEntry : eEntrys) {
			ret.add(parseActivity(eEntry));
		}
		return ret;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGeoLabel() {
		return geoLabel;
	}

	public void setGeoLabel(String geoLabel) {
		this.geoLabel = geoLabel;
	}

	public String getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(String geoPoint) {
		this.geoPoint = geoPoint;
	}

	public DateAndTime getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(DateAndTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorUri() {
		return authorUri;
	}

	public void setAuthorUri(String authorUri) {
		this.authorUri = authorUri;
	}

	public String getAuthorPhotoUrl() {
		return authorPhotoUrl;
	}

	public void setAuthorPhotoUrl(String authorPhotoUrl) {
		this.authorPhotoUrl = authorPhotoUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttachmentHref() {
		return attachmentHref;
	}

	public void setAttachmentHref(String photoHref) {
		this.attachmentHref = photoHref;
	}

	@Override
	public String toString() {
		return getAuthorName() + ": " + getHref();
	}

}
