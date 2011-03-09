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
import ilarkesto.auth.AUserDao;
import ilarkesto.auth.Auth;
import ilarkesto.base.Iconized;
import ilarkesto.base.Reflect;
import ilarkesto.base.Utl;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.core.logging.Log;
import ilarkesto.di.Context;
import ilarkesto.fp.Predicate;
import ilarkesto.id.IdentifiableResolver;
import ilarkesto.search.SearchResultsConsumer;
import ilarkesto.search.Searchable;
import ilarkesto.search.Searcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ADao<E extends AEntity> extends ADatobManager<E> implements IdentifiableResolver<E>, Searcher,
		DaoListener, Iconized {

	private static final Log LOG = Log.get(ADao.class);

	private Predicate<Class> entityTypeFilter;
	private String icon;

	// --- ---

	@Override
	public void onDatobModified(E entity, String comment) {
		// don's save new entities
		if (!isPersistent(entity)) return;

		LOG.info("Entity modified:", Utl.toStringWithType(entity), "->", comment);
		saveEntity(entity);
	}

	@Override
	public void updateLastModified(E entity) {
		entity.updateLastModified();
	}

	@Override
	public void onMissingMaster(E entity) {
		deleteEntity(entity);
		throw new EnsureIntegrityCompletedException();
	}

	// --- basic ---

	public abstract String getEntityName();

	public abstract Class getEntityClass();

	public Map<String, Class> getAliases() {
		return Collections.emptyMap();
	}

	private boolean isPersistent(E entity) {
		return transactionService.isPersistent(entity.getId());
	}

	public final Predicate<Class> getEntityTypeFilter() {
		if (entityTypeFilter == null) {
			entityTypeFilter = new Predicate<Class>() {

				@Override
				public boolean test(Class parameter) {
					return parameter.isAssignableFrom(getEntityClass());
				}

			};
		}
		return entityTypeFilter;
	}

	@Override
	public String getIcon() {
		if (icon == null) {
			icon = (String) Reflect.getFieldValue(getEntityClass(), "ICON");
			if (icon == null) icon = getEntityName();
		}
		return icon;
	}

	public int getEntitiesCount(Predicate<E> predicate) {
		return transactionService.getEntitiesCount(getEntityTypeFilter(), (Predicate<AEntity>) predicate);
	}

	public E getEntity(Predicate<E> predicate) {
		return (E) transactionService.getEntity(getEntityTypeFilter(), (Predicate<AEntity>) predicate);
	}

	public final Set<E> getEntities(Predicate<E> filter) {
		// long start = System.currentTimeMillis();
		Set<E> result = (Set<E>) transactionService.getEntities(getEntityTypeFilter(), (Predicate<AEntity>) filter);
		// long time = System.currentTimeMillis() - start;
		// if (time > 2000) throw new RuntimeException("getEntities took too
		// long. fix it!");
		return result;
	}

	@Override
	public E getById(String id) {
		if (id == null) throw new RuntimeException("id must not be null");
		E entity = (E) transactionService.getById(id);
		if (entity == null) throw new EntityDoesNotExistException(id);
		return entity;
	}

	@Deprecated
	public E getEntityById(String id) {
		return getById(id);
	}

	@Override
	public List<E> getByIds(Collection<String> entitiesIds) {
		Set<String> ids = new HashSet<String>(entitiesIds);
		List<E> result = (List<E>) transactionService.getByIds(entitiesIds);
		if (result.size() != ids.size()) {
			result = new ArrayList<E>();
			for (String id : ids) {
				E entity = (E) transactionService.getById(id);
				result.add(entity);
			}
		}
		return result;
	}

	public Set<E> getByIdsAsSet(Collection<String> entitiesIds) {
		return new HashSet<E>(getByIds(entitiesIds));
	}

	@Deprecated
	public List<E> getEntitiesByIds(Collection<String> entitiesIds) {
		return getByIds(entitiesIds);
	}

	public Set<E> getEntitiesVisibleForUser(final AUser user) {
		return getEntities(new Predicate<E>() {

			@Override
			public boolean test(E e) {
				return Auth.isVisible(e, user);
			}

		});
	}

	public Set<E> getEntities() {
		return (Set<E>) transactionService.getEntities(getEntityTypeFilter(), null);
	}

	public void deleteEntity(E entity) {
		transactionService.deleteEntity(entity);
		daoService.fireEntityDeleted(entity);
	}

	public void saveEntity(E entity) {
		transactionService.saveEntity(entity);
		daoService.fireEntitySaved(entity);
	}

	public E newEntityInstance() {
		E entity;
		try {
			entity = (E) getEntityClass().newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		entity.setLastModified(DateAndTime.now());
		transactionService.registerEntity(entity);
		return entity;
	}

	public E newEntityInstance(String id) {
		E entity = newEntityInstance();
		entity.setId(id);
		return entity;
	}

	public void ensureIntegrity() {
		if (!initialized) throw new RuntimeException("Not initialized!");
		Class clazz = getEntityClass();
		LOG.info("Ensuring integrity:", clazz.getSimpleName());
		for (E entity : getEntities()) {
			try {
				entity.ensureIntegrity();
			} catch (EnsureIntegrityCompletedException ex) {
				continue;
			} catch (Throwable ex) {
				throw new RuntimeException("Ensuring integrity for " + clazz.getSimpleName() + ":" + entity.getId()
						+ " failed.", ex);
			}
		}
	}

	@Override
	public void entityDeleted(EntityEvent event) {
		AEntity entity = event.getEntity();
		for (AEntity e : getEntities()) {
			try {
				e.repairDeadReferences(entity.getId());
			} catch (EnsureIntegrityCompletedException ex) {
				continue;
			}
		}
	}

	@Override
	public void entitySaved(EntityEvent event) {}

	@Override
	public void feed(final SearchResultsConsumer searchBox) {
		if (!Searchable.class.isAssignableFrom(getEntityClass())) return;

		for (AEntity entity : getEntities(new Predicate<E>() {

			@Override
			public boolean test(E e) {
				return Auth.isVisible(e, searchBox.getSearcher()) && e instanceof Searchable
						&& Persist.matchesKeys(e, searchBox.getKeys());
			}

		})) {
			searchBox.addEntity(entity);
		}

	}

	protected final TransactionService getTransactionService() {
		return transactionService;
	}

	// ---

	protected Set<Class> getValueObjectClasses() {
		return Collections.emptySet();
	}

	@Override
	public String toString() {
		String entityName = getEntityName();
		if (entityName == null) return entityName + "Dao";
		return getClass().getName();
	}

	// --------------------
	// --- dependencies ---
	// --------------------

	private volatile boolean initialized;

	public synchronized final void initialize(Context context) {
		if (initialized) throw new RuntimeException("Already initialized!");

		Class entityClass = getEntityClass();
		context.autowireClass(entityClass);
		for (Class c : getValueObjectClasses())
			context.autowireClass(c);
		Field daoField;
		try {
			daoField = entityClass.getDeclaredField("dao");
			boolean accessible = daoField.isAccessible();
			if (!accessible) daoField.setAccessible(true);
			try {
				daoField.set(null, this);
			} catch (IllegalArgumentException ex) {
				throw new RuntimeException(ex);
			} catch (IllegalAccessException ex) {
				throw new RuntimeException(ex);
			} catch (NullPointerException ex) {
				throw new RuntimeException("Setting dao field failed. Is it static?", ex);
			}
			if (!accessible) daoField.setAccessible(false);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchFieldException ex) {
			// nop
		}

		initialized = true;
	}

	private DaoService daoService;

	public final void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	public final DaoService getDaoService() {
		return daoService;
	}

	private TransactionService transactionService;

	public final void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	protected AUserDao userDao;

	public final void setUserDao(AUserDao userDao) {
		this.userDao = userDao;
	}

}
