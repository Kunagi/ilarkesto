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
package ilarkesto.ui;

import ilarkesto.base.StringProvider;

public final class Option<T> {

	public static final String KEY_CANCEL = "_cancel";

	public String getKey() {
		return key;
	}

	public String getIcon() {
		return icon;
	}

	public String getLabel() {
		return label;
	}

	public T getPayload() {
		return payload;
	}

	public boolean isGroup() {
		return group;
	}

	public String getTooltip() {
		return tooltip;
	}

	@Override
	public boolean equals(Object obj) {
		return key.equals(((Option) obj).getKey());
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public String toString() {
		return label;
	}

	// --- dependencies ---

	private String key;
	private String label;
	private String icon;
	private String tooltip;
	private T payload;
	private boolean group;

	public Option(String key, String label, String icon, T payload) {
		this.key = key;
		this.label = label;
		this.icon = icon;
		this.payload = payload;
	}

	public Option(String key, String label, String icon) {
		this(key, label, icon, null);
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	// --- tooltip StringProvider ---

	public static final OptionTooltipStringProvider OPTION_TOOLTIP_STRING_PROVIDER = new OptionTooltipStringProvider();

	public static class OptionTooltipStringProvider<T> implements StringProvider<Option<T>> {

		@Override
		public String getString(Option<T> o) {
			return o.getTooltip();
		}
	}

}
