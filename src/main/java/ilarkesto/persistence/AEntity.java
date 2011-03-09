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
package ilarkesto.persistence;

import ilarkesto.auth.AUser;
import ilarkesto.base.Iconized;
import ilarkesto.base.Utl;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.id.Identifiable;

import java.util.Map;
import java.util.UUID;

public abstract class AEntity extends ADatob implements Identifiable, Iconized {

	private static final long serialVersionUID = 1L;

	private static DaoService daoService;

	private String id;
	private DateAndTime lastModified;
	private String lastEditorId;

	public abstract ADao getDao();

	// --- dependencies ---

	public static DaoService getDaoService() {
		return daoService;
	}

	public final static void setDaoService(DaoService daoService) {
		AEntity.daoService = daoService;
	}

	// --- ---

	@Override
	protected final ADao getManager() {
		return getDao();
	}

	@Override
	public String getIcon() {
		return getDao().getIcon();
	}

	@Override
	public final String getId() {
		if (id == null) id = UUID.randomUUID().toString();
		return id;
	}

	final void setId(String id) {
		this.id = id;
	}

	public final DateAndTime getLastModified() {
		return lastModified;
	}

	final void setLastModified(DateAndTime value) {
		this.lastModified = value;
	}

	public final AUser getLastEditor() {
		if (this.lastEditorId == null) return null;
		return (AUser) userDao.getById(this.lastEditorId);
	}

	public final void setLastEditor(AUser lastEditor) {
		if (isLastEditor(lastEditor)) return;
		this.lastEditorId = lastEditor == null ? null : lastEditor.getId();
		fireModified("lastEditor=" + lastEditor);
	}

	public final boolean isLastEditor(AUser user) {
		if (this.lastEditorId == null && user == null) return true;
		return user != null && user.getId().equals(this.lastEditorId);
	}

	public final boolean isLastEditorSet() {
		return lastEditorId != null;
	}

	@Override
	protected void fireModified(String comment) {
		super.fireModified(comment);
	}

	protected void updateLastModified() {
		setLastModified(DateAndTime.now());
	}

	@Override
	public void ensureIntegrity() {
		super.ensureIntegrity();
		if (lastModified == null) fireModified("lastModified!=null");
	}

	@Override
	protected void storeProperties(Map properties) {
		properties.put("@type", getDao().getEntityName());
		properties.put("id", getId());
	}

	@Override
	public final boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof AEntity)) return false;
		return Utl.equals(getId(), ((AEntity) o).getId());
	}

	@Override
	public final int hashCode() {
		if (id == null) return 0;
		return id.hashCode();
	}

}
