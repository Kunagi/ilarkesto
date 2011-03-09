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

import ilarkesto.fp.Predicate;
import ilarkesto.id.IdentifiableResolver;

import java.util.Set;

public interface EntityStore extends IdentifiableResolver<AEntity> {

	void setVersion(long version);

	void setAlias(String alias, Class cls);

	void load(Class<? extends AEntity> cls, String alias);

	AEntity getEntity(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter);

	int getEntitiesCount(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter);

	Set<AEntity> getEntities(Predicate<Class> typeFilter, Predicate<AEntity> entityFilter);

	void save(AEntity entity);

	void delete(AEntity entity);

}
