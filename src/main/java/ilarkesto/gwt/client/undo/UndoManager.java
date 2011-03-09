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
package ilarkesto.gwt.client.undo;

import ilarkesto.core.logging.Log;

import java.util.LinkedList;
import java.util.List;

public class UndoManager {

	private static final Log LOG = Log.get(UndoManager.class);

	private int maxOperations = 5;
	private List<AUndoOperation> operations = new LinkedList<AUndoOperation>();

	public void add(AUndoOperation operation) {
		operations.add(0, operation);
		while (operations.size() > maxOperations) {
			operations.remove(operations.size() - 1);
		}
	}

	public void undo(AUndoOperation operation) {
		LOG.info("Undo:", operation);
		operation.undo();
		operations.remove(operation);
	}

	public List<AUndoOperation> getOperations() {
		return operations;
	}

	public void clear() {
		operations.clear();
	}
}
