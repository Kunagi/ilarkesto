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
import ilarkesto.base.ToStringComparator;
import ilarkesto.di.BeanProvider;
import ilarkesto.form.Form;
import ilarkesto.form.MultiCheckboxFormField;
import ilarkesto.id.CountingIdGenerator;
import ilarkesto.id.IdGenerator;
import ilarkesto.ui.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MultiOptionAction<T> extends AAction {

	private MultiCheckboxFormField<Option<T>> groupOptionsField;
	private MultiCheckboxFormField<Option<T>> optionsField;

	@Override
	protected void assertPermissions() {}

	@Override
	protected void performAction() throws InterruptedException {
		setAutoShowInfoDone(false);

		List<Option<T>> groupOptions = new ArrayList<Option<T>>();
		List<Option<T>> singleOptions = new ArrayList<Option<T>>();
		for (Option<T> option : getOptions()) {
			if (option.isGroup()) {
				groupOptions.add(option);
			} else {
				singleOptions.add(option);
			}
		}
		Collections.sort(groupOptions, ToStringComparator.INSTANCE_IGNORECASE);
		Collections.sort(singleOptions, ToStringComparator.INSTANCE_IGNORECASE);

		getUi().getUserMessageService().info(message);
		Form form = autowire(new Form());
		form.setStringKeyPrefix(getStringKeyPrefix());
		if (!groupOptions.isEmpty()) {
			groupOptionsField = form.addMultiCheckbox("groupOptions");
			groupOptionsField.setSelectableItems(groupOptions);
			groupOptionsField.setValue(getSelectedOptions());
			groupOptionsField.setItemTooltipProvider(new Option.OptionTooltipStringProvider<T>());
		}
		optionsField = form.addMultiCheckbox("options");
		optionsField.setSelectableItems(singleOptions);
		optionsField.setValue(getSelectedOptions());
		optionsField.setItemTooltipProvider(new Option.OptionTooltipStringProvider<T>());
		form.addSubmitButton("select");
		form.addAbortSubmitButton();
		showFormDialog(form);
		selectedOptions = optionsField.getValue();
		if (groupOptionsField != null) selectedOptions.addAll(groupOptionsField.getValue());
	}

	public void addOption(Option<T> option) {
		if (options == null) options = new ArrayList<Option<T>>();
		options.add(option);
	}

	// --- helper ---

	public Option<T> getOption(String key) {
		if (Option.KEY_CANCEL.equals(key)) return null;
		for (Option<T> option : options) {
			if (option.getKey().equals(key)) return option;
		}
		return null;
	}

	private IdGenerator payloadIdGenerator;

	public void addPayloads(Collection<T> payloads) {
		if (payloadIdGenerator == null) payloadIdGenerator = new CountingIdGenerator("p");
		for (T o : payloads) {
			String icon = o instanceof Iconized ? ((Iconized) o).getIcon() : "item";
			addOption(new Option<T>(payloadIdGenerator.generateId(), o.toString(), icon, o));
		}
	}

	public Set<T> getSelectedPayloads() {
		Set<T> payloads = new HashSet<T>(selectedOptions.size());
		for (Option<T> option : selectedOptions) {
			payloads.add(option.getPayload());
		}
		return payloads;
	}

	public static <T> Set<Option<T>> showDialog(BeanProvider beanProvider, ActionPerformer actionPerformer,
			AAction waitingAction, String message, boolean horizontal, Collection<Option<T>> options) {
		MultiOptionAction<T> action = beanProvider.autowire(new MultiOptionAction());
		action.setOptions(options);
		action.setMessage(message);
		actionPerformer.performSubAction(action, waitingAction);
		return action.getSelectedOptions();
	}

	public static <T> Set<T> showDialog(BeanProvider beanProvider, ActionPerformer actionPerformer,
			AAction waitingAction, String message, Collection<T> payloads) {
		MultiOptionAction<T> action = beanProvider.autowire(new MultiOptionAction());
		action.addPayloads(payloads);
		action.setMessage(message);
		actionPerformer.performSubAction(action, waitingAction);
		return action.getSelectedPayloads();
	}

	public Set<Option<T>> getSelectedOptions() {
		return selectedOptions;
	}

	// --- dependencies ---

	private Set<Option<T>> selectedOptions;

	public void setSelectedOptions(Set<Option<T>> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	private Collection<Option<T>> options = new ArrayList<Option<T>>();

	public final Collection<Option<T>> getOptions() {
		return options;
	}

	public final void setOptions(Collection<Option<T>> options) {
		this.options = options;
	}

	private String message;

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

}
