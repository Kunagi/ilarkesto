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
package ilarkesto.ui.action;

import ilarkesto.form.Form;
import ilarkesto.form.TextFormField;
import ilarkesto.form.validator.Validator;

public class InputAction extends AAction {

	private TextFormField inputField;

	@Override
	protected void performAction() {
		setAutoShowInfoDone(false);
		Form form = autowire(new Form());
		form.setStringKeyPrefix(getStringKeyPrefix());
		inputField = form.addText("input");
		inputField.setRequired(true);
		if (validator != null) inputField.setValidator(validator);
		form.addSubmitButton("ok");
		form.addAbortSubmitButton();
		showFormDialog(form);
	}

	public String getInputString() {
		return inputField.getValueAsString();
	}

	@Override
	protected void assertPermissions() {}

	// --- dependencies ---

	private Validator validator;

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
