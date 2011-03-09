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
package ilarkesto.form.validator;

import ilarkesto.email.EmailAddress;
import ilarkesto.form.ValidationException;

/**
 * @author <A href="mailto:wi@koczewski.de">Witoslaw Koczewski </A>
 * @version 1 $Revision: 1.5 $ $Author: wko $ $Date: 2006/10/26 17:56:53 $
 */
public class EmailValidator implements Validator {

	public static final EmailValidator THIS = new EmailValidator();

	private static final String MSG = "Die Eingabe muss eine g\u00FCltige Email-Adresse sein. Beispiel: \"wi@koczewski.de\" oder \"Witoslaw Koczewski <wi@koczewski.de>\"";

	@Override
	public String validate(String s) throws ValidationException {
		s = s.trim();
		if (s.length() < 5) throw new ValidationException(MSG);
		int idx = s.indexOf('@');
		if (idx < 1) throw new ValidationException(MSG);

		// if (idx >= s.length() - 3) throw new ValidationException(MSG);
		// int idx2 = s.lastIndexOf('.');
		// if (idx2 < 3) throw new ValidationException(MSG);
		// if (idx2 <= idx) throw new ValidationException(MSG);
		// if (idx2 >= s.length() - 1) throw new ValidationException(MSG);

		EmailAddress.parseList(s);
		return s;
	}

}
