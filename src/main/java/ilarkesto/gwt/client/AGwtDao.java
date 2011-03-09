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
package ilarkesto.gwt.client;

import ilarkesto.core.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AGwtDao extends AComponent {

	private String entityIdBase;
	private int entityIdCounter;

	protected abstract Collection<Map<String, ? extends AGwtEntity>> getEntityMaps();

	protected abstract AGwtEntity updateLocalEntity(String type, Map data);

	protected abstract void onEntityModifiedRemotely(AGwtEntity entity);

	protected abstract void onEntityDeletedRemotely(AGwtEntity entity);

	protected abstract void onEntityCreatedLocaly(AGwtEntity entity, Runnable successAction);

	protected abstract void onEntityDeletedLocaly(AGwtEntity entity);

	protected abstract void onEntityPropertyChangedLocaly(AGwtEntity entity, String property, Object value);

	public abstract Map<String, Integer> getEntityCounts();

	public String getEntityIdBase() {
		return entityIdBase;
	}

	public int getEntityIdCounter() {
		return entityIdCounter;
	}

	String getNewEntityId() {
		if (entityIdBase == null) throw new RuntimeException("No entityIdBase received yet.");
		return entityIdBase + "-" + ++entityIdCounter;
	}

	public void handleDataFromServer(ADataTransferObject data) {
		if (data.entityIdBase != null) {
			entityIdBase = data.entityIdBase;
			log.debug("entityIdBase received:", data.entityIdBase);
		}
		List<AGwtEntity> modifiedEntities = null;
		if (data.containsEntities()) {
			modifiedEntities = new ArrayList<AGwtEntity>(data.getEntities().size());
			for (Map entityData : data.getEntities()) {
				AGwtEntity entity = updateLocalEntity((String) entityData.get("@type"), entityData);
				modifiedEntities.add(entity);
			}
		}
		if (data.containsDeletedEntities()) {
			List<String> deletedEntities = new ArrayList<String>(data.getDeletedEntities());
			for (Map<String, ? extends AGwtEntity> map : getEntityMaps()) {
				for (String entityId : new ArrayList<String>(deletedEntities)) {
					AGwtEntity entity = map.remove(entityId);
					if (entity != null) {
						deletedEntities.remove(entityId);
						Log.DEBUG("deleted:", entity.getEntityType() + ":", entity);
						onEntityDeletedRemotely(entity);
					}
				}
			}
		}
		if (modifiedEntities != null) {
			for (AGwtEntity entity : modifiedEntities) {
				onEntityModifiedRemotely(entity);
			}
		}
	}

	protected final void entityCreated(AGwtEntity entity, Runnable successAction) {
		entity.setCreated();
		onEntityCreatedLocaly(entity, successAction);
	}

	protected final void entityDeleted(AGwtEntity entity) {
		onEntityDeletedLocaly(entity);
	}

	public final void entityPropertyChanged(AGwtEntity entity, String property, Object value) {
		onEntityPropertyChangedLocaly(entity, property, value);
	}

	public final AGwtEntity getEntity(String id) throws EntityDoesNotExistException {
		for (Map<String, ? extends AGwtEntity> entityMap : getEntityMaps()) {
			AGwtEntity entity = entityMap.get(id);
			if (entity != null) return entity;
		}
		throw new EntityDoesNotExistException(id);
	}

}
