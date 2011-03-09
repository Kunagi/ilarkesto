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
package ilarkesto.integration.imdb;

import ilarkesto.core.base.Str;

public class ImdbRecord {

	private String id;
	private String title;
	private String titleDe;
	private Integer year;
	private String coverId;
	private String trailerId;

	public ImdbRecord(String id, String title, String titleDe, Integer year, String coverId, String trailerId) {
		super();
		this.id = id;
		this.title = title;
		this.titleDe = titleDe;
		this.year = year;
		this.coverId = coverId;
		this.trailerId = trailerId;
	}

	public String getId() {
		return id;
	}

	public String getTrailerId() {
		return trailerId;
	}

	public String getTitle() {
		return title;
	}

	public boolean isTitleSet() {
		return !Str.isBlank(title);
	}

	public String getTitleDe() {
		return titleDe;
	}

	public boolean isTitleDeSet() {
		return !Str.isBlank(titleDe);
	}

	public Integer getYear() {
		return year;
	}

	public boolean isYearSet() {
		return year != null;
	}

	public String getCoverId() {
		return coverId;
	}

	public boolean isCoverIdSet() {
		return !Str.isBlank(coverId);
	}

	@Override
	public String toString() {
		return title + " (" + year + ")";
	}

}
