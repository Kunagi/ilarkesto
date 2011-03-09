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
package ilarkesto.concurrent;

import java.util.Collection;
import java.util.Collections;

public abstract class ACollectionTask<E> extends ATask {

	private int count;
	private int index;
	private E element;

	protected abstract Collection<E> prepare() throws InterruptedException;

	protected abstract void perform(E element) throws InterruptedException;

	@Override
	protected final void perform() throws InterruptedException {
		Collection<E> elements = prepare();
		if (elements == null) elements = Collections.emptyList();
		count = elements.size();
		index = 0;
		for (E element : elements) {
			this.element = element;
			try {
				perform(element);
			} catch (Throwable ex) {
				onElementError(element, ex);
			}
			if (isAbortRequested()) break;
			index++;
		}
		cleanup();
	}

	protected void onElementError(E element, Throwable ex) {
		throw new RuntimeException("Processing element failed: " + element, ex);
	}

	public final int getIndex() {
		return index;
	}

	protected void cleanup() throws InterruptedException {}

	protected String getProgressMessage(E element) {
		return element.toString();
	}

	@Override
	public final String getProgressMessage() {
		return getProgressMessage(element);
	}

	@Override
	public final float getProgress() {
		if (count == 0) return 1;
		if (index == 0) return 0;
		return (float) index / (float) count;
	}

}
