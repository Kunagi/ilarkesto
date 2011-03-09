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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is for transporting data from the scrum server to the GWT client.
 */
public abstract class ADataTransferObject implements Serializable, IsSerializable {

	public String entityIdBase;
	public Boolean developmentMode;
	private List<String> errors;
	public Integer conversationNumber;

	private String userId;
	private Set<String> deletedEntities;
	private Map<String, Map<String, Serializable>> entities;

	// dummys required for gwt-serialization
	private int dummyI;
	private Integer dummyInteger;
	private float dummyF;
	private Float dummyFloat;

	public void clear() {
		entities = null;
		deletedEntities = null;
	}

	public void addError(String error) {
		if (errors == null) errors = new ArrayList<String>(1);
		errors.add(error);
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setUserId(String user) {
		this.userId = user;
	}

	public String getUserId() {
		return userId;
	}

	public boolean isUserSet() {
		return userId != null;
	}

	public final boolean containsEntities() {
		return entities != null && !entities.isEmpty();
	}

	public final boolean containsEntity(String entityId) {
		return entities.containsKey(entityId);
	}

	public final void addEntity(Map data) {
		if (entities == null) entities = new HashMap<String, Map<String, Serializable>>();
		entities.put((String) data.get("id"), data);
	}

	public final Collection<Map<String, Serializable>> getEntities() {
		return entities.values();
	}

	public final boolean containsDeletedEntities() {
		return deletedEntities != null && !deletedEntities.isEmpty();
	}

	public final void addDeletedEntity(String entityId) {
		if (deletedEntities == null) deletedEntities = new HashSet<String>();
		deletedEntities.add(entityId);
	}

	public final Set<String> getDeletedEntities() {
		return deletedEntities;
	}

}
