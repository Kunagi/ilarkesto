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
package ilarkesto.form;

import ilarkesto.form.validator.Validator;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public class TextFormField extends AFormField {

	private String value;
	private boolean trim = true;
	private boolean emptyIsNull = true;
	private Validator validator;
	private int width = 40;
	private String suffix;
	private InputAssistant inputAssistant;
	private InputAssistantButton inputAssistantButton;

	public TextFormField(String name) {
		super(name);
		inputAssistantButton = (InputAssistantButton) new InputAssistantButton(Form.BUTTON_PREFIX
				+ Form.INPUTASSISTANT_BUTTON_NAME_PREFIX + name).setValidateForm(false).setLabel("Auswahl...").setIcon(

		"inputAssistant");
	}

	public InputAssistantButton getInputAssistantButton() {
		return inputAssistantButton;
	}

	public TextFormField setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public TextFormField setInputAssistant(InputAssistant inputAssistant) {
		this.inputAssistant = inputAssistant;
		return this;
	}

	public boolean isInputAssistantSet() {
		return inputAssistant != null;
	}

	public InputAssistant getInputAssistant() {
		return inputAssistant;
	}

	public String getSuffix() {
		return suffix;
	}

	public TextFormField setTrim(boolean trim) {
		this.trim = trim;
		return this;
	}

	public TextFormField setWidth(int value) {
		this.width = value;
		return this;
	}

	public TextFormField setValidator(Validator validator) {
		this.validator = validator;
		return this;
	}

	public TextFormField setValue(String value) {
		this.value = value;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
		value = preProcessValue(data.get(getName()));
	}

	protected String preProcessValue(String s) {
		if (s == null) return null;
		if (trim) {
			s = s.trim();
		}
		if (emptyIsNull && s.length() == 0) { return null; }
		return s;
	}

	public void validate() throws ValidationException {
		if (value == null) {
			if (isRequired()) { throw new ValidationException("Eingabe erforderlich"); }
		} else {
			if (validator != null) {
				validator.validate(value);
			}
		}
	}

	public String getValueAsString() {
		return value;
	}

	public class InputAssistantButton extends FormButton {

		public InputAssistantButton(String name) {
			super(name);
		}

		public TextFormField getField() {
			return TextFormField.this;
		}

	}

}
