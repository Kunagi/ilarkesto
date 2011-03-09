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

import ilarkesto.core.logging.Log;
import ilarkesto.fp.Predicate;
import ilarkesto.id.IdentifiableResolver;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TransactionService implements IdentifiableResolver<AEntity> {

	private static final Log LOG = Log.get(TransactionService.class);

	public TransactionService() {}

	private Transaction createTransaction() {
		Transaction t = new Transaction(entityStore);
		LOG.debug("Transaction created: " + t);
		return t;
	}

	private ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<Transaction>();

	private Transaction getCurrentTransaction(boolean autocreate) {
		Transaction t = threadLocalTransaction.get();
		if (t == null && autocreate) {
			t = createTransaction();
			threadLocalTransaction.set(t);
		}
		return t;
	}

	private Transaction getCurrentTransaction() {
		return getCurrentTransaction(true);
	}

	public synchronized void commit() {
		Transaction t = getCurrentTransaction(false);
		if (t == null) {
			// LOG.debug("No transaction to commit.");
			return;
		}
		try {
			t.commit();
		} finally {
			threadLocalTransaction.set(null);
		}
	}

	public synchronized void cancel() {
		Transaction t = getCurrentTransaction(false);
		if (t == null) return;
		LOG.debug("Cancelling transaction:", t);
		threadLocalTransaction.set(null);
	}

	public boolean isPersistent(String id) {
		return entityStore.getById(id) != null;
	}

	// --- delegations ---

	@Override
	public AEntity getById(String id) {
		Transaction transaction = getCurrentTransaction(false);
		if (transaction == null) {
			return entityStore.getById(id);
		} else {
			return transaction.getById(id);
		}
	}

	public AEntity getEntity(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		Transaction transaction = getCurrentTransaction(false);
		if (transaction == null) {
			return entityStore.getEntity(typeFilter, entityFilter);
		} else {
			return transaction.getEntity(typeFilter, entityFilter);
		}
	}

	@Override
	public List<AEntity> getByIds(Collection<String> ids) {
		Transaction transaction = getCurrentTransaction(false);
		if (transaction == null) {
			return entityStore.getByIds(ids);
		} else {
			return transaction.getByIds(ids);
		}
	}

	public Set<AEntity> getEntities(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		Transaction transaction = getCurrentTransaction(false);
		if (transaction == null) {
			return entityStore.getEntities(typeFilter, entityFilter);
		} else {
			return transaction.getEntities(typeFilter, entityFilter);
		}
	}

	public int getEntitiesCount(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		Transaction transaction = getCurrentTransaction(false);
		if (transaction == null) {
			return entityStore.getEntitiesCount(typeFilter, entityFilter);
		} else {
			return transaction.getEntitiesCount(typeFilter, entityFilter);
		}
	}

	public void deleteEntity(AEntity entity) {
		getCurrentTransaction().deleteEntity(entity);
	}

	public void saveEntity(AEntity entity) {
		getCurrentTransaction().saveEntity(entity);
	}

	public void registerEntity(AEntity entity) {
		getCurrentTransaction().registerEntity(entity);
	}

	// --- dependencies ---

	private EntityStore entityStore;

	public void setEntityStore(EntityStore entityStore) {
		this.entityStore = entityStore;
	}

}
