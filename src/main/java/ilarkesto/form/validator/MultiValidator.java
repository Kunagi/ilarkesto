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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * TODO Comment type MultiValidator
 * 
 * @author <A href="mailto:wi@koczewski.de">Witoslaw Koczewski </A> <BR>
 *         <code>
 * 			$Author: wko $
 * 		</code>
 * @version <code>
 * 			$Revision: 1.2 $
 *			$Date: 2006/09/13 19:07:31 $
 *		</code>
 */
public class MultiValidator implements Validator {

	private Collection	validators	= new ArrayList(3);

	public MultiValidator() {};

	public MultiValidator(Validator v1, Validator v2) {
		add(v1);
		add(v2);
	}

	public void add(Validator validator) {
		validators.add(validator);
	}

	// overriding
	public String validate(String text) throws ValidationException {
		for (Iterator iter = validators.iterator(); iter.hasNext();) {
			Validator validator = (Validator) iter.next();
			validator.validate(text);
		}
		return text;
	}

}

//$Log: MultiValidator.java,v $
//Revision 1.2  2006/09/13 19:07:31  wko
//*** empty log message ***
//
//Revision 1.1  2005/11/20 17:42:19  wko
//*** empty log message ***
//
//Revision 1.1  2005/11/10 18:17:56  wko
//initial load
//
//Revision 1.1  2005/06/30 21:37:32  wko
//*** empty log message ***
//
