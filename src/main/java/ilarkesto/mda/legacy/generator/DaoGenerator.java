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
package ilarkesto.mda.legacy.generator;

import ilarkesto.auth.AUserDao;
import ilarkesto.base.Str;
import ilarkesto.mda.legacy.model.DatobModel;
import ilarkesto.mda.legacy.model.DependencyModel;
import ilarkesto.mda.legacy.model.EntityModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.persistence.ADao;

import java.util.LinkedHashSet;
import java.util.Set;

public class DaoGenerator extends ABeanGenerator<EntityModel> {

	public DaoGenerator(EntityModel bean) {
		super(bean);
	}

	@Override
	protected String getName() {
		return "G" + bean.getName() + "Dao";
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected void writeContent() {

		if (!bean.isAbstract()) {
			ln();
			ln("    public final String getEntityName() {");
			ln("        return " + bean.getName() + ".TYPE;");
			ln("    }");

			ln();
			ln("    public final Class getEntityClass() {");
			ln("        return " + bean.getName() + ".class;");
			ln("    }");
		}

		if (bean.isOwnable()) {
			ln();
			s("    public " + bean.getName() + " newEntityInstance(" + getUserClassName() + " user) {").ln();
			s("        " + bean.getName() + " entity = newEntityInstance();").ln();
			s("        entity.setOwner(user);").ln();
			s("        return entity;").ln();
			s("    }").ln();
		}

		if (bean.isViewProtected()) {
			ln();
			s(
				"    public Set<" + bean.getName() + "> getEntitiesVisibleForUser(final " + getUserClassName()
						+ " user) {").ln();
			s("        return getEntities(new Predicate<" + bean.getName() + ">() {").ln();
			s("            public boolean test(" + bean.getName() + " e) {").ln();
			s("                return Auth.isVisible(e, user);").ln();
			s("            }").ln();
			s("        });").ln();
			s("    }").ln();
		}

		ln();
		comment("clear caches");
		ln("    public void clearCaches() {");
		for (PropertyModel p : bean.getProperties()) {
			if (!p.isUnique()) {
				ln("        " + Str.lowercaseFirstLetter(bean.getName()) + "sBy"
						+ Str.uppercaseFirstLetter(p.getNameSingular()) + "Cache.clear();");
			}
			if (!p.isBoolean()) {
				ln("        " + p.getNameSingular() + "sCache = null;");
			}
		}
		ln("    }");

		ln();
		ln("    @Override");
		ln("    public void entityDeleted(EntityEvent event) {");
		ln("        super.entityDeleted(event);");
		ln("        if (event.getEntity() instanceof " + bean.getName() + ") {");
		ln("            clearCaches();");
		ln("        }");
		ln("    }");

		ln();
		ln("    @Override");
		ln("    public void entitySaved(EntityEvent event) {");
		ln("        super.entitySaved(event);");
		ln("        if (event.getEntity() instanceof " + bean.getName() + ") {");
		ln("            clearCaches();");
		ln("        }");
		ln("    }");

		for (PropertyModel property : bean.getProperties()) {
			writeProperty(property);
		}

		ln();
		comment("valueObject classes");
		ln("    @Override");
		ln("    protected Set<Class> getValueObjectClasses() {");
		ln("        Set<Class> ret = new HashSet<Class>(super.getValueObjectClasses());");
		for (PropertyModel property : bean.getProperties()) {
			if (!property.isValueObject()) continue;
			String className = property.getContentType();
			// int idx = className.lastIndexOf('.');
			// className = className.substring(0, idx) + "." + className.substring(idx + 2);
			ln("        ret.add(" + className + ".class);");
		}
		ln("        return ret;");
		ln("    }");

		ln();
		ln("    @Override");
		ln("    public Map<String, Class> getAliases() {");
		ln("        Map<String, Class> aliases = new HashMap<String, Class>(super.getAliases());");
		writeAliases(bean);
		ln("        return aliases;");
		ln("    }");

		ln();
		comment("dependencies");
		for (DependencyModel dependencyModel : bean.getDependencies()) {
			dependency(dependencyModel.getType(), dependencyModel.getName(), false, false);
		}

	}

	private void writeAliases(DatobModel bean) {
		if (bean == null) return;
		for (PropertyModel p : bean.getProperties()) {
			if (!p.isValueObject()) continue;
			ln("        aliases.put(\"" + Str.getLastToken(p.getContentType(), ".") + "\", " + p.getContentType()
					+ ".class);");
		}
		writeAliases((DatobModel) bean.getSuperbean());
	}

	private void writeProperty(PropertyModel p) {
		String pNameUpper = Str.uppercaseFirstLetter(p.getNameSingular());
		String predicateClassNamePrefix = p.isCollection() ? "Contains" : "Is";
		String pType = p.getContentType();

		ln();
		ln("    // -----------------------------------------------------------");
		ln("    // - " + p.getName());
		ln("    // -----------------------------------------------------------");

		ln();
		if (p.isUnique()) {
			ln("    public final " + bean.getName() + " get" + bean.getName() + "By" + pNameUpper + "("
					+ p.getContentType() + " " + p.getNameSingular() + ") {");
			ln("        return getEntity(new " + predicateClassNamePrefix + pNameUpper + "(" + p.getNameSingular()
					+ "));");
			ln("    }");
		} else {
			if (p.isPrimitive()) {
				if (pType.equals("boolean")) pType = "Boolean";
				if (pType.equals("int")) pType = "Integer";
				if (pType.equals("long")) pType = "Long";
			}
			String cacheVarName = Str.lowercaseFirstLetter(bean.getName()) + "sBy" + pNameUpper + "Cache";
			String generic = "<" + pType + ",Set<" + bean.getName() + ">>";
			ln("    private final Cache" + generic + " " + cacheVarName + " = new Cache" + generic + "(");
			ln("            new Cache.Factory" + generic + "() {");
			ln("                public Set<" + bean.getName() + "> create(" + pType + " " + p.getNameSingular() + ") {");
			ln("                    return getEntities(new " + predicateClassNamePrefix + pNameUpper + "("
					+ p.getNameSingular() + "));");
			ln("                }");
			ln("            });");
			ln();

			ln("    public final Set<" + bean.getName() + "> get" + bean.getName() + "sBy" + pNameUpper + "("
					+ p.getContentType() + " " + p.getNameSingular() + ") {");
			ln("        return " + cacheVarName + ".get(" + p.getNameSingular() + ");");
			ln("    }");

			// ln(" public final Set<" + bean.getInterfaceName() + "> get" + bean.getName() + "sBy"
			// + pNameUpper + "(" + p.getContentType() + " " + p.getNameSingular() + ") {");
			// ln(" return getEntities(new " + predicateClassNamePrefix + pNameUpper + "("
			// + p.getNameSingular() + "));");
			// ln(" }");
		}
		if (!p.isBoolean()) {
			String cacheName = p.getNameSingular() + "sCache";
			ln("    private Set<" + pType + "> " + cacheName + ";");
			ln();
			ln("    public final Set<" + pType + "> get" + pNameUpper + "s" + "() {");
			ln("        if (" + cacheName + " == null) {");
			ln("            " + cacheName + " = new HashSet<" + pType + ">();");
			ln("            for (" + bean.getName() + " e : getEntities()) {");
			if (p.isCollection()) {
				ln("                " + cacheName + ".addAll(e.get" + pNameUpper + "s());");
			} else {
				if (p.isPrimitive()) {
					ln("                " + cacheName + ".add(e.get" + pNameUpper + "());");
				} else {
					ln("                if (e.is" + pNameUpper + "Set()) " + cacheName + ".add(e.get" + pNameUpper
							+ "());");
				}
			}
			ln("            }");
			ln("        }");
			ln("        return " + cacheName + ";");
			ln("    }");
		}

		ln();
		ln("    private static class " + predicateClassNamePrefix + pNameUpper + " implements Predicate<"
				+ bean.getName() + "> {");
		ln();
		ln("        private " + p.getContentType() + " value;");
		ln();
		ln("        public " + predicateClassNamePrefix + pNameUpper + "(" + p.getContentType() + " value) {");
		ln("            this.value = value;");
		ln("        }");
		ln();
		ln("        public boolean test(" + bean.getName() + " e) {");
		if (p.isBoolean()) {
			ln("            return value == e." + predicateClassNamePrefix.toLowerCase() + pNameUpper + "();");
		} else {
			ln("            return e." + predicateClassNamePrefix.toLowerCase() + pNameUpper + "(value);");
		}
		ln("        }");
		ln();
		ln("    }");

	}

	protected final String getUserClassName() {
		EntityModel userModel = bean.getUserModel();
		if (userModel == null && bean.getName().equals("User")) userModel = bean;
		return userModel.getPackageName() + "." + userModel.getName();
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

	@Override
	protected String getSuperclass() {
		if ("User".equals(bean.getName())) return AUserDao.class.getName() + "<" + bean.getName() + ">";
		return ADao.class.getName() + "<" + bean.getName() + ">";
	}

	@Override
	protected Set<String> getImports() {
		Set<String> result = new LinkedHashSet<String>();
		result.addAll(super.getImports());
		result.add("ilarkesto.base.*");
		result.add("ilarkesto.base.time.*");
		result.add("ilarkesto.auth.*");
		result.add("ilarkesto.persistence.*");
		result.add("ilarkesto.fp.*");
		return result;
	}

	// --- dependencies ---

}
