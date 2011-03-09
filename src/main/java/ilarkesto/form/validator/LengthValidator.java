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
// Copyright (c) 2003 Witoslaw Koczewski, http://www.koczewski.de
package ilarkesto.form.validator;

import ilarkesto.form.ValidationException;

/**
 * @author <A href="mailto:wi@koczewski.de">Witoslaw Koczewski </A>
 * @version 1 $Revision: 1.2 $ $Author: wko $ $Date: 2006/09/13 19:07:31 $
 */
public class LengthValidator implements Validator {

	private int	min;
	private int	max;

	public LengthValidator(int min, int max) {
		this.min = min;
		this.max = max;
	}

	// overriding
	public String validate(String text) throws ValidationException {
		int len = text.length();
		if (len < min || len > max)
				throw new ValidationException(getMessage());
		return text;
	}

	public String getMessage() {
		return "Die L\u00E4nge Ihrer Eingabe mu\u00DF zwischen " + min + " und " + max
				+ " Zeichen betragen.";
	}

}

// $Log: LengthValidator.java,v $
// Revision 1.2  2006/09/13 19:07:31  wko
// *** empty log message ***
//
// Revision 1.1  2005/11/20 17:42:19  wko
// *** empty log message ***
//
// Revision 1.1  2005/11/10 18:17:56  wko
// initial load
//
// Revision 1.1  2005/06/30 21:37:32  wko
// *** empty log message ***
//
// Revision 1.1 2004/09/01 18:25:22 wko
// repository update
//
// Revision 1.1 2004/08/23 19:38:47 wko
// repository change (revres->v18)
//
// Revision 1.2 2004/06/23 21:48:36 wko
// *** empty log message ***
//
// Revision 1.1 2003/10/23 18:31:08 wko
// core changes
//
// Revision 1.1.1.1 2003/09/20 00:47:47 wko
// no message
//
