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

import ilarkesto.base.Iconized;
import ilarkesto.base.MissingDependencyException;
import ilarkesto.base.Str;
import ilarkesto.base.StringProvider;
import ilarkesto.core.logging.Log;
import ilarkesto.di.BeanProvider;
import ilarkesto.form.BeanForm;
import ilarkesto.form.Form;
import ilarkesto.form.FormButton;
import ilarkesto.form.FormField;
import ilarkesto.form.InputAssistant;
import ilarkesto.form.ItemFormField;
import ilarkesto.form.MultiComplexFormField;
import ilarkesto.form.MultiItem;
import ilarkesto.form.MultiItemFormField;
import ilarkesto.form.TextFormField;
import ilarkesto.form.ValidationException;
import ilarkesto.id.CountingIdGenerator;
import ilarkesto.id.IdGenerator;
import ilarkesto.ui.Option;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;

public final class FormAction<F extends Form> extends AAction {

	private static final Log LOG = Log.get(FormAction.class);

	@Override
	protected void assertPermissions() {}

	@Override
	protected void performAction() throws InterruptedException {
		setAutoShowInfoDone(false);
		if (form == null) throw new MissingDependencyException("form");
		form.addHidden("actionId").setValue(getActionId());

		for (FormField field : form.getAutoTriggerFields()) {
			if (field instanceof ItemFormField) {
				showSelectItemDialog((ItemFormField) field);
			} else {
				throw new RuntimeException("AutoTrigger not supported for field: " + field.getClass().getSimpleName());
			}
		}
		do {
			LOG.debug("Handling form:", form.getName());
			showDialog(form);
		} while (handleSubmit());
	}

	/**
	 * @return true, if submit handeled internally (show form again)
	 */
	private boolean handleSubmit() {
		// inject form with current request properties
		autowire(form);

		// determine clicked button
		for (String parameter : webRequestParametersBeanProvider.beanNames()) {
			if (parameter.startsWith(Form.BUTTON_PREFIX)) {
				String buttonName = parameter.substring(Form.BUTTON_PREFIX.length());
				clickedButton = form.getButton(buttonName);
				break;
			}
		}
		LOG.debug("clickedButton:", clickedButton);

		if (clickedButton != null) {
			if (clickedButton.isUpdateFields()) {
				updateForm(form);
			}

			// check if form validation active for this button
			if (clickedButton.isValidateForm()) {
				try {
					form.validate();
				} catch (ValidationException ex) {
					LOG.debug("Form is not valid:", ex);
					String message = ex.getMessage();
					Throwable cause = ex.getCause();
					if (cause != null) message += " " + Str.format(cause);
					form.setErrorMessage(message);
					return true;
				}
			}

			try {
				return handleButtonClick(clickedButton);
			} catch (ValidationException ex) {
				LOG.debug("Form is not valid:", ex);
				form.setErrorMessage(ex.getMessage());
				return true;
			}

		}

		return true;
	}

	private void showSelectItemDialog(ItemFormField field) {
		try {
			field.setValue(showOptionDialog(form.getStringKeyPrefix() + "." + field.getName() + ".select.message",
				field.getSelectableItems()));
		} catch (ActionAbortedException ex) {}
	}

	private boolean handleButtonClick(FormButton button) throws ValidationException {

		if (button instanceof ItemFormField.SelectButton) {
			showSelectItemDialog(((ItemFormField.SelectButton) button).getField());
			return true;
		}

		if (button instanceof ItemFormField.ClearButton) {
			ItemFormField field = ((ItemFormField.ClearButton) button).getField();
			field.setValue(null);
			return true;
		}

		if (button instanceof MultiItemFormField.AddButton) {
			MultiItemFormField field = ((MultiItemFormField.AddButton) button).getField();

			MultiOptionAction action = autowire(new MultiOptionAction());
			action.setMessage(string(form.getStringKeyPrefix() + "." + field.getName() + ".select.message"));
			IdGenerator itemIdGenerator = new CountingIdGenerator("item");
			for (Object o : field.getSelectableMultiItems()) {
				MultiItem item = (MultiItem) o;
				Option option = new Option(itemIdGenerator.generateId(), item.toString(), "multiItem", item);
				option.setGroup(true);
				option.setTooltip(item.getTooltip());
				action.addOption(option);
			}
			StringProvider itemTooltipProvider = field.getItemTooltipProvider();
			StringProvider itemLabelProvider = field.getItemLabelProvider();
			for (Object item : field.getSelectableItems()) {
				String icon = item instanceof Iconized ? ((Iconized) item).getIcon() : "item";
				String label = itemLabelProvider == null ? item.toString() : itemLabelProvider.getString(item);
				Option option = new Option(itemIdGenerator.generateId(), label, icon, item);
				if (itemTooltipProvider != null) {
					option.setTooltip(itemTooltipProvider.getString(item));
				}
				action.addOption(option);
			}

			try {
				actionPerformer.performSubAction(action, this);
			} catch (ActionAbortedException ex) {
				return true;
			}
			Set items = action.getSelectedPayloads();
			for (Object item : items) {
				if (item instanceof MultiItem) {
					for (Object o : ((MultiItem) item).getItems()) {
						field.addValueItem(o);
					}
				} else {
					field.addValueItem(item);
				}
			}
			return true;
		}

		if (button instanceof MultiItemFormField.RemoveButton) {
			MultiItemFormField field = ((MultiItemFormField.RemoveButton) button).getField();
			Object item = ((MultiItemFormField.RemoveButton) button).getItem();
			field.removeValueItem(item);
			return true;
		}

		if (button instanceof MultiItemFormField.RemoveAllButton) {
			MultiItemFormField field = ((MultiItemFormField.RemoveAllButton) button).getField();
			field.removeAllItems();
			return true;
		}

		if (button instanceof MultiComplexFormField.AddButton) {
			MultiComplexFormField.AddButton b = (MultiComplexFormField.AddButton) button;
			final MultiComplexFormField field = b.getField();
			final BeanForm form = field.createSubform();
			autowire(form);
			form.setBean(field.getItemFactory().getBean());
			FormAction action = new FormAction();
			action.setForm(form);
			try {
				actionPerformer.performSubAction(action, this);
			} catch (ActionAbortedException ex) {
				return true;
			}
			if (!action.isClickedButtonAbort()) {
				field.addValueItem(form.getBean());
			}
			return true;
		}

		if (button instanceof MultiComplexFormField.EditButton) {
			MultiComplexFormField.EditButton b = (MultiComplexFormField.EditButton) button;
			final MultiComplexFormField field = b.getField();
			final BeanForm form = field.createSubform();
			autowire(form);
			form.setBean(b.getItem());
			FormAction action = new FormAction();
			action.setForm(form);
			try {
				actionPerformer.performSubAction(action, this);
			} catch (ActionAbortedException ex) {}
			return true;

		}

		if (button instanceof MultiComplexFormField.RemoveButton) {
			MultiComplexFormField field = ((MultiComplexFormField.RemoveButton) button).getField();
			Object item = ((MultiComplexFormField.RemoveButton) button).getItem();
			field.removeValueItem(item);
			return true;
		}

		if (button instanceof TextFormField.InputAssistantButton) {
			TextFormField field = ((TextFormField.InputAssistantButton) button).getField();
			InputAssistant inputAssistant = field.getInputAssistant();
			Object option;
			try {
				option = showOptionDialog("inputAssistant.message", inputAssistant.getOptions());
			} catch (ActionAbortedException ex) {
				return true;
			}
			field.setValue(inputAssistant.applyToInput(field.getValueAsString(), option));
			return true;
		}

		if (button.isAbort()) throw new ActionAbortedException();

		// user defined button
		return false;
	}

	private void updateForm(Form form) {
		Map<String, String> data = new HashMap<String, String>();
		for (String parameter : webRequestParametersBeanProvider.beanNames()) {
			if (!parameter.startsWith("_")) {
				data.put(parameter, (String) webRequestParametersBeanProvider.getBean(parameter));
			}
		}
		form.update(data, uploadedFiles);
	}

	private FormButton clickedButton;

	public FormButton getClickedButton() {
		return clickedButton;
	}

	public F getForm() {
		return form;
	}

	@Override
	public String toString() {
		return "FormAction:" + (form == null ? null : form.getName());
	}

	// --- helper ---

	public boolean isClickedButtonAbort() {
		return clickedButton == null ? false : clickedButton.isAbort();
	}

	// --- dependencies ---

	private F form;

	public void setForm(F form) {
		this.form = form;
	}

	private Collection<FileItem> uploadedFiles;

	public void setUploadedFiles(Collection<FileItem> uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}

	private BeanProvider webRequestParametersBeanProvider;

	public void setWebRequestParametersBeanProvider(BeanProvider webRequestParametersBeanProvider) {
		this.webRequestParametersBeanProvider = webRequestParametersBeanProvider;
	}
}
