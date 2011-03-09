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
package ilarkesto.form.validator;

import ilarkesto.form.ValidationException;

public class SuffixValidator implements Validator {

	private String	suffix;
	private boolean	ignoreCase;
	private String	failureMessage;

	public SuffixValidator(String suffix, boolean ignoreCase) {
		this(suffix, ignoreCase,
				"Ung\u00FCltige Endung der Eingabe. Erforderlich ist: \"" + suffix
						+ "\"");
	}

	public SuffixValidator(String suffix, boolean ignoreCase,
			String failureMessage) {
		this.suffix = suffix;
		this.ignoreCase = ignoreCase;
		if (ignoreCase) {
			this.suffix = this.suffix.toLowerCase();
		}
		this.failureMessage = failureMessage;
	}

	public String validate(String text) throws ValidationException {
		if (ignoreCase) {
			if (!text.toLowerCase().endsWith(suffix))
				throw new ValidationException(failureMessage);
		} else {
			if (!text.endsWith(suffix))
				throw new ValidationException(failureMessage);
		}
		return text;
	}

}

// $Log: SuffixValidator.java,v $
// Revision 1.2  2006/09/13 19:07:31  wko
// *** empty log message ***
//
// Revision 1.1  2005/11/20 17:42:19  wko
// *** empty log message ***
//
// Revision 1.1  2005/11/10 18:17:56  wko
// initial load
//
// Revision 1.1  2005/09/22 17:12:24  wko
// *** empty log message ***
//
