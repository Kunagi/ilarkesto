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
import ilarkesto.di.BeanProvider;
import ilarkesto.id.CountingIdGenerator;
import ilarkesto.id.IdGenerator;
import ilarkesto.ui.Option;

import java.util.ArrayList;
import java.util.Collection;

public final class OptionAction<T> extends AAction {

	@Override
	protected void assertPermissions() {}

	@Override
	protected void performAction() throws InterruptedException {
		setAutoShowInfoDone(false);
		showDialog(getOptions(), getMessage());
		if (selectedOption == null) throw new ActionAbortedException();
	}

	public void addOption(Option<T> option) {
		if (options == null) options = new ArrayList<Option<T>>();
		options.add(option);
	}

	public Option<T> getSelectedOption() {
		return selectedOption;
	}

	// --- helper ---

	public boolean isOption(String key) {
		return key.equals(selectedOption.getKey());
	}

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
		horizontal = false;
	}

	public T getSelectedPayload() {
		return getSelectedOption().getPayload();
	}

	public static <T> T showDialog(BeanProvider beanProvider, ActionPerformer actionPerformer, AAction waitingAction,
			String message, Collection<T> payloads) {
		OptionAction<T> action = beanProvider.autowire(new OptionAction());
		action.setHorizontal(false);
		action.addPayloads(payloads);
		action.setMessage(message);
		actionPerformer.performSubAction(action, waitingAction);
		Option<T> option = action.getSelectedOption();
		return option == null ? null : option.getPayload();
	}

	// --- dependencies ---

	private Option<T> selectedOption;

	public final void setSelectedOptionKey(String option) {
		this.selectedOption = getOption(option);
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

	private boolean horizontal = true;

	public boolean isHorizontal() {
		return horizontal;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

}
