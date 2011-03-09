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
// Copyright (c) 2005 Witoslaw Koczewski, http://www.koczewski.de
package ilarkesto.cli;

public class ValueParameter extends AParameter {

	private String	value;

	public ValueParameter(String name, String description) {
		super(name, description);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public boolean isSet() {
		return value != null;
	}

}

// $Log: ValueParameter.java,v $
// Revision 1.2  2006/02/02 17:36:39  wko
// *** empty log message ***
//
// Revision 1.1 2005/11/25 17:07:06 wko
// *** empty log message ***
//
// Revision 1.1 2005/07/07 18:00:17 wko
// *** empty log message ***
//
