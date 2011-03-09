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
package ilarkesto.ui.usermessage;

import ilarkesto.base.Str;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserMessageService {

	private List<UserMessage> messages = new ArrayList<UserMessage>();
	private List<UserMessageListener> listeners;

	public void addListener(UserMessageListener listener) {
		if (listeners == null) listeners = new ArrayList<UserMessageListener>(1);
		listeners.add(listener);
	}

	public void info(String message) {
		addMessage(new UserMessage(UserMessage.INFO, message));
	}

	public void warn(String message) {
		addMessage(new UserMessage(UserMessage.WARN, message));
	}

	public void error(String message) {
		addMessage(new UserMessage(UserMessage.ERROR, message));
	}

	public synchronized void addMessage(UserMessage message) {
		this.messages.add(message);
		if (listeners == null) return;
		List<UserMessage> messages = new ArrayList<UserMessage>(1);
		messages.add(message);
		for (UserMessageListener listener : listeners) {
			listener.onUserMessages(messages);
		}
	}

	public synchronized void addMessages(List<UserMessage> messages) {
		this.messages.addAll(messages);
		if (listeners == null) return;
		for (UserMessageListener listener : listeners) {
			listener.onUserMessages(messages);
		}
	}

	public synchronized List<UserMessage> getMessages() {
		return new ArrayList<UserMessage>(messages);
	}

	public synchronized void removeMessages(Collection<UserMessage> messages) {
		this.messages.removeAll(messages);
	}

	@Override
	public String toString() {
		return Str.format(messages);
	}

}
