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

import ilarkesto.base.Str;
import ilarkesto.base.time.Date;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.base.time.Time;
import ilarkesto.core.logging.Log;
import ilarkesto.mda.legacy.model.DatobModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.mda.legacy.model.ReferencePropertyModel;
import ilarkesto.mda.legacy.model.SetPropertyModel;
import ilarkesto.persistence.ADatob;
import ilarkesto.persistence.UniqueFieldConstraintException;

public class DatobGenerator<D extends DatobModel> extends ABeanGenerator<D> {

	public DatobGenerator(D bean) {
		super(bean);
	}

	protected boolean isCopyConstructorEnabled() {
		return true;
	}

	@Override
	protected void writeContent() {

		// nl();
		// comment("default constructor");
		// s(" public G").s(bean.getName()).s("() {").nl();
		// s(" super(null)").nl();
		// s(" }").nl();

		ln();
		ln("    private static final " + Log.class.getName() + " LOG = " + Log.class.getName() + ".get(" + getName()
				+ ".class);");

		if (!bean.isAbstract()) {
			ln();
			ln("    public static final String TYPE = \"" + Str.lowercaseFirstLetter(bean.getName()) + "\";");

			// ln();
			// comment("icon");
			// ln(" public String getIcon() {");
			// ln(" return TYPE;");
			// ln(" };");
		}

		if (isCopyConstructorEnabled()) writeCopyConstructor();

		if (bean.isSearchable()) {
			writeSearchable();
		}

		for (PropertyModel property : bean.getProperties()) {
			writeProperty(property);
		}

		ln();
		ln("    public void updateProperties(Map<?, ?> properties) {");
		ln("        for (Map.Entry entry : properties.entrySet()) {");
		ln("            String property = (String) entry.getKey();");
		ln("            if (property.equals(\"id\")) continue;");
		ln("            Object value = entry.getValue();");
		for (PropertyModel p : bean.getProperties()) {
			String propertyName = p.getName();
			if (p.isReference()) {
				propertyName += "Id";
				if (p.isCollection()) propertyName += "s";
			}
			ln("            if (property.equals(\"" + propertyName + "\")) update"
					+ Str.uppercaseFirstLetter(p.getName()) + "(value);");
		}
		ln("        }");
		ln("    }");

		writeRepairDeadReferences();

		writeEnsureIntegrity();

		super.writeContent();
	}

	protected void writeEnsureIntegrity() {
		ln();
		comment("ensure integrity");
		ln();
		s("    public void ensureIntegrity() {").ln();
		s("        super.ensureIntegrity();").ln();
		for (PropertyModel p : bean.getProperties()) {
			if (p.isCollection()) {
				s("        if (").s(getFieldName(p)).s(" == null) ").s(getFieldName(p)).s(" = new ").s(getFieldImpl(p))
						.s("();").ln();
				if (p.isValueObject()) {
					s("        get" + Str.uppercaseFirstLetter(p.getName()) + "Manager().ensureIntegrityOfStructures(")
							.s(getFieldName(p)).s(");").ln();
				}
				if (p.isReference()) {
					ln("        Set<String> " + p.getName() + " = new HashSet<String>(" + getFieldName(p) + ");");
					ln("        for (String entityId : " + p.getName() + ") {");
					ln("            try {");
					if (p.isAbstract()) {
						ln("                getDaoService().getById(entityId);");
					} else {
						ln("                " + p.getDaoName() + ".getById(entityId);");
					}
					ln("            } catch (EntityDoesNotExistException ex) {");
					ln("                LOG.info(\"Repairing dead " + p.getNameSingular() + " reference\");");
					ln("                repairDead" + Str.uppercaseFirstLetter(p.getNameSingular())
							+ "Reference(entityId);");
					ln("            }");
					ln("        }");
				}
			} else {
				if (p.isReference()) {
					ReferencePropertyModel pRef = (ReferencePropertyModel) p;
					if (pRef.isMaster()) {
						ln("        if (!is" + Str.uppercaseFirstLetter(p.getNameSingular()) + "Set()) {");
						ln("            repairMissingMaster();");
						ln("            return;");
						ln("        }");
					}
					ln("        try {");
					ln("            get" + Str.uppercaseFirstLetter(p.getName()) + "();");
					ln("        } catch (EntityDoesNotExistException ex) {");
					ln("            LOG.info(\"Repairing dead " + p.getNameSingular() + " reference\");");
					ln("            repairDead" + Str.uppercaseFirstLetter(p.getNameSingular()) + "Reference("
							+ getFieldName(p) + ");");
					ln("        }");
				}
			}
		}
		s("    }").ln();
	}

	private void writeCopyConstructor() {
		ln();
		comment("copy constructor");
		s("    public G").s(bean.getName()).s("(G").s(bean.getName()).s(" template) {").ln();
		s("        super(template);").ln();
		s("        if (template==null) return;").ln().ln();
		for (PropertyModel p : bean.getProperties()) {
			String getterMethodPrefix = p.isBoolean() ? "is" : "get";
			s("        set").sU(p.getName()).s("(template.").s(getterMethodPrefix).sU(p.getName()).s("());").ln();
		}
		s("    }").ln();
	}

	private void writeRepairDeadReferences() {
		if (!bean.getProperties().isEmpty()) {
			ln();
			s("    protected void repairDeadReferences(String entityId) {").ln();
			s("        super.repairDeadReferences(entityId);").ln();
			for (PropertyModel p : bean.getProperties()) {
				if (p.isCollection()) {
					s("        if (").s(getFieldName(p)).s(" == null) ").s(getFieldName(p)).s(" = new ")
							.s(getFieldImpl(p)).s("();").ln();
				}
				if (p.isValueObject()) {
					if (p.isCollection()) {
						// ln("        for(ADatob adatob : "+getFieldName(p)+") adatob.repairDeadReferences(entityId);");
						s("        repairDeadReferencesOfValueObjects(").s(getFieldName(p)).s(",entityId);").ln();
					} else {
						s("        if (").s(getFieldName(p)).s(" != null) ").s(getFieldName(p))
								.s(".repairDeadReferences(entityId);");
					}
				}
				if (!p.isReference()) continue;
				String nameUpper = Str.uppercaseFirstLetter(p.getNameSingular());
				s("        repairDead").s(nameUpper).s("Reference(entityId);").ln();
			}
			s("    }").ln();
		}
	}

	private void writeSearchable() {

		ln();
		section("Searchable");

		ln();
		ln("    public boolean matchesKey(String key) {");
		ln("        if (super.matchesKey(key)) return true;");
		for (PropertyModel p : bean.getProperties()) {
			if (!p.isSearchable()) continue;
			ln("        if (matchesKey(get" + Str.uppercaseFirstLetter(p.getName()) + "(), key)) return true;");
		}
		ln("        return false;");
		ln("    }");
	}

	private void writeProperty(PropertyModel p) {
		String pNameUpper = Str.uppercaseFirstLetter(p.getName());

		ln();
		ln("    // -----------------------------------------------------------");
		ln("    // - " + p.getName());
		ln("    // -----------------------------------------------------------");

		// --- property ---
		ln();
		s("    private " + getFieldType(p) + " " + getFieldName(p).substring(5));
		if (p.isCollection()) {
			s(" = new " + getFieldImpl(p) + "()");
		}
		ln(";");
		if (p.isReference()) {
			if (p.isCollection()) {

			} else {
				ln("    private transient " + p.getType() + " " + p.getName() + "Cache;");
			}
		}

		// --- datob manager ---
		String datobGetter = null;
		if (p.isValueObject()) {
			datobGetter = "get" + pNameUpper + "Manager()";
			ln();
			ln("    private transient " + ADatob.StructureManager.class.getName().replace('$', '.') + "<"
					+ p.getContentType() + "> " + p.getName() + "Manager;");
			ln();
			ln("    private " + ADatob.StructureManager.class.getName().replace('$', '.') + "<" + p.getContentType()
					+ "> " + datobGetter + " {");
			ln("        if (" + p.getName() + "Manager == null) " + p.getName() + "Manager = new StructureManager<"
					+ p.getContentType() + ">();");
			ln("        return " + p.getName() + "Manager;");
			ln("    }");
		}

		String getterMethodPrefix = p.isBoolean() ? "is" : "get";

		if (p.isReference() && !p.isCollection()) {
			// --- updateXxxCache ---
			String daoExpr = p.getDaoName();
			if (p.isAbstract()) {
				daoExpr = "getDaoService()";
			}
			ln();
			ln("    private void update" + pNameUpper + "Cache() {");
			ln("        " + p.getName() + "Cache = " + getFieldName(p) + " == null ? null : (" + p.getContentType()
					+ ")" + daoExpr + ".getById(" + getFieldName(p) + ");");
			ln("    }");

			// --- getXxxId ---
			ln();
			ln("    public final String " + getterMethodPrefix + pNameUpper + "Id() {");
			ln("        return " + getFieldName(p) + ";");
			ln("    }");

		}

		// --- getXxx ---
		ln();
		ln("    public final " + p.getType() + " " + getterMethodPrefix + pNameUpper + "() {");
		writeGetXxxContent(p);
		ln("    }");

		// --- setXxx ---
		ln();
		String type = p.getType();
		if (p.isCollection()) type = "Collection<" + p.getContentType() + ">";
		ln("    public final void set" + pNameUpper + "(" + type + " " + p.getName() + ") {");
		ln("        " + p.getName() + " = " + "prepare" + pNameUpper + "(" + p.getName() + ");");
		writeSetXxxContent(p);
		ln("    }");

		ln();
		ln("    protected " + type + " prepare" + pNameUpper + "(" + type + " " + p.getName() + ") {");
		if (p.isString()) {
			ln("        " + p.getName() + " = Str.removeUnreadableChars(" + p.getName() + ");");
		}
		ln("        return " + p.getName() + ";");
		ln("    }");

		if (p.isCollection()) {
			writeCollectionProperty(p);
		} else {
			writeSimpleProperty(p);
		}

	}

	private void writeGetXxxContent(PropertyModel p) {
		if (p.isReference()) {
			String daoExpr = p.getDaoName();
			if (p.isAbstract()) {
				daoExpr = "getDaoService()";
			}
			if (p.isCollection()) {
				String suffix = p instanceof SetPropertyModel ? "AsSet" : "";
				ln("        return (" + p.getCollectionType() + ") " + daoExpr + ".getByIds" + suffix + "("
						+ getFieldName(p) + ");");
			} else {
				ln("        if (" + p.getName() + "Cache == null) update" + Str.uppercaseFirstLetter(p.getName())
						+ "Cache();");
				ln("        return " + p.getName() + "Cache;");
			}
		} else {
			if (p.isCollection()) {
				ln("        return new " + getFieldImpl(p) + "(" + p.getName() + ");");
			} else {
				ln("        return " + p.getName() + ";");
			}
		}
	}

	private void writeSetXxxContent(PropertyModel p) {
		String pNameUpper = Str.uppercaseFirstLetter(p.getName());
		if (p.isReference()) {
			if (p.isCollection()) {
				ln("        if (" + p.getName() + " == null) " + p.getName() + " = Collections.emptyList();");
				String suffix = p instanceof SetPropertyModel ? "AsSet" : "AsList";
				ln("        " + p.getCollectionType() + "<String> ids = getIds" + suffix + "(" + p.getName() + ");");
				ln("        if (" + getFieldName(p) + ".equals(ids)) return;");
				ln("        " + getFieldName(p) + " = ids;");
				ln("        updateLastModified();");
				if (p.isFireModified()) {
					ln("        fireModified(\"" + p.getName() + "=\"+Str.format(" + p.getName() + "));");
				}
			} else {
				ln("        if (is" + pNameUpper + "(" + p.getName() + ")) return;");

				ln("        " + getFieldName(p) + " = " + p.getName() + " == null ? null : " + p.getName()
						+ ".getId();");
				ln("        " + p.getName() + "Cache = " + p.getName() + ";");
				ln("        updateLastModified();");
				if (p.isFireModified()) {
					ln("        fireModified(\"" + p.getName() + "=\"+" + p.getName() + ");");
				}
			}
		} else {
			if (p.isCollection()) {
				ln("        if (" + p.getName() + " == null) " + p.getName() + " = Collections.emptyList();");
				ln("        if (" + getFieldName(p) + ".equals(" + p.getName() + ")) return;");
				if (p.isValueObject()) {
					ln("        " + getFieldName(p) + " = cloneValueObjects(" + p.getName() + ", get" + pNameUpper
							+ "Manager());");
				} else {
					ln("        " + getFieldName(p) + " = new " + getFieldImpl(p) + "(" + p.getName() + ");");
				}
				ln("        updateLastModified();");
				if (p.isFireModified()) {
					ln("        fireModified(\"" + p.getName() + "=\"+Str.format(" + p.getName() + "));");
				}
			} else {
				ln("        if (is" + pNameUpper + "(" + p.getName() + ")) return;");
				if (p.isUnique()) {
					ln("        if (" + p.getName() + " != null && getDao().get" + bean.getName() + "By" + pNameUpper
							+ "(" + p.getName() + ") != null) throw new "
							+ UniqueFieldConstraintException.class.getName() + "(this, \"" + p.getName() + "\", "
							+ p.getName() + ");");
				}
				if (p.isValueObject()) {
					ln("        " + getFieldName(p) + " = " + p.getName() + ".clone(this);");
				} else {
					ln("        " + getFieldName(p) + " = " + p.getName() + ";");
				}
				ln("        updateLastModified();");
				if (p.isFireModified()) {
					ln("        fireModified(\"" + p.getName() + "=\"+" + p.getName() + ");");
				}
			}
		}
	}

	protected final String getFieldName(PropertyModel p) {
		if (p.isReference()) {
			if (p.isCollection()) {
				return "this." + p.getName() + "Ids";
			} else {
				return "this." + p.getName() + "Id";
			}
		} else {
			return "this." + p.getName();
		}
	}

	private String getFieldType(PropertyModel p) {
		if (p.isReference()) {
			if (p.isCollection()) {
				return p.getCollectionType() + "<String>";
			} else {
				return "String";
			}
		} else {
			return p.getType();
		}
	}

	private String getFieldImpl(PropertyModel p) {
		if (p.isReference()) {
			if (p.isCollection()) {
				return p.getCollectionImpl() + "<String>";
			} else {
				return getFieldType(p);
			}
		} else {
			if (p.isCollection()) {
				return p.getCollectionImpl() + "<" + p.getContentType() + ">";
			} else {
				return p.getType();
			}
		}
	}

	private String getImpl(PropertyModel p) {
		if (p.isReference()) {
			if (p.isCollection()) {
				return p.getCollectionImpl() + "<" + p.getContentType() + ">";
			} else {
				return getFieldType(p);
			}
		} else {
			if (p.isCollection()) {
				return p.getCollectionImpl() + "<" + p.getContentType() + ">";
			} else {
				return p.getType();
			}
		}
	}

	protected void writeCollectionProperty(PropertyModel p) {
		String pNameSingularUpper = Str.uppercaseFirstLetter(p.getNameSingular());
		String pNameUpper = Str.uppercaseFirstLetter(p.getName());
		String paramExpr = p.isReference() ? p.getNameSingular() + ".getId()" : p.getNameSingular();

		// --- repairDeadXxxReference ---
		if (p.isReference()) {
			ln();
			ln("    protected void repairDead" + pNameSingularUpper + "Reference(String entityId) {");
			ln("        if (" + getFieldName(p) + ".remove(entityId)) fireModified(\"" + p.getName()
					+ "-=\" + entityId);");
			ln("    }");
		}

		// --- containsXxx ---
		ln();
		ln("    public final boolean contains" + pNameSingularUpper + "(" + p.getContentType() + " "
				+ p.getNameSingular() + ") {");
		ln("        if (" + p.getNameSingular() + " == null) return false;");
		ln("        return " + getFieldName(p) + ".contains(" + paramExpr + ");");
		ln("    }");

		// --- getXxxCount ---
		ln();
		ln("    public final int get" + pNameUpper + "Count() {");
		ln("        return " + getFieldName(p) + ".size();");
		ln("    }");

		// --- isXxxEmpty ---
		ln();
		ln("    public final boolean is" + pNameUpper + "Empty() {");
		ln("        return " + getFieldName(p) + ".isEmpty();");
		ln("    }");

		// --- addXxx ---
		ln();
		ln("    public final boolean add" + pNameSingularUpper + "(" + p.getContentType() + " " + p.getNameSingular()
				+ ") {");
		ln("        if (" + p.getNameSingular() + " == null) throw new IllegalArgumentException(\""
				+ p.getNameSingular() + " == null\");");
		if (p.isValueObject()) {
			ln("        boolean added = " + getFieldName(p) + ".add((" + p.getContentType() + ")" + paramExpr
					+ ".clone(get" + pNameUpper + "Manager()));");
		} else {
			ln("        boolean added = " + getFieldName(p) + ".add(" + paramExpr + ");");
		}
		ln("        if (added) updateLastModified();");
		if (p.isFireModified()) {
			ln("        if (added) fireModified(\"" + p.getName() + "+=\" + " + p.getNameSingular() + ");");
		}
		ln("        return added;");
		ln("    }");

		// --- addXxxs ---
		ln();
		ln("    public final boolean add" + pNameSingularUpper + "s(Collection<" + p.getContentType() + "> "
				+ p.getName() + ") {");
		ln("        if (" + p.getName() + " == null) throw new IllegalArgumentException(\"" + p.getName()
				+ " == null\");");
		if (p.isValueObject()) {
			ln("        boolean added = false;");
			ln("        for (" + p.getContentType() + " " + p.getNameSingular() + " : " + p.getName() + ") {");
			ln("            added = added | " + getFieldName(p) + ".add((" + p.getContentType() + ")"
					+ p.getNameSingular() + ".clone(get" + pNameUpper + "Manager()));");
			ln("        }");
		} else {
			ln("        boolean added = false;");
			ln("        for (" + p.getContentType() + " " + p.getNameSingular() + " : " + p.getName() + ") {");
			ln("            added = added | " + getFieldName(p) + ".add(" + paramExpr + ");");
			ln("        }");
		}
		ln("        return added;");
		ln("    }");

		// --- removeXxx ---
		ln();
		ln("    public final boolean remove" + pNameSingularUpper + "(" + p.getContentType() + " "
				+ p.getNameSingular() + ") {");
		ln("        if (" + p.getNameSingular() + " == null) throw new IllegalArgumentException(\""
				+ p.getNameSingular() + " == null\");");
		ln("        if (" + getFieldName(p) + " == null) return false;");
		ln("        boolean removed = " + getFieldName(p) + ".remove(" + paramExpr + ");");
		ln("        if (removed) updateLastModified();");
		if (p.isFireModified()) {
			ln("        if (removed) fireModified(\"" + p.getName() + "-=\" + " + p.getNameSingular() + ");");
		}
		ln("        return removed;");
		ln("    }");

		// --- removeXxxs ---
		ln();
		ln("    public final boolean remove" + pNameSingularUpper + "s(Collection<" + p.getContentType() + "> "
				+ p.getName() + ") {");
		ln("        if (" + p.getName() + " == null) return false;");
		ln("        if (" + p.getName() + ".isEmpty()) return false;");
		ln("        boolean removed = false;");
		ln("        for (" + p.getContentType() + " _element: " + p.getName() + ") {");
		ln("            removed = removed | remove" + pNameSingularUpper + "(_element);");
		ln("        }");
		ln("        return removed;");
		ln("    }");

		// --- clearXxx ---
		ln();
		ln("    public final boolean clear" + pNameUpper + "() {");
		ln("        if (" + getFieldName(p) + ".isEmpty()) return false;");
		ln("        " + getFieldName(p) + ".clear();");
		ln("        updateLastModified();");
		if (p.isFireModified()) {
			ln("        fireModified(\"" + p.getName() + " cleared\");");
		}
		ln("        return true;");
		ln("    }");

		if (p.getContentType().equals(String.class.getName())) {
			// --- getXxxAsCommaSeperatedString
			ln();
			ln("    public final String get" + pNameUpper + "AsCommaSeparatedString() {");
			ln("        if (" + getFieldName(p) + ".isEmpty()) return null;");
			ln("        return Str.concat(" + getFieldName(p) + ",\", \");");
			ln("    }");

			// --- setXxxAsCommaSeperatedString
			ln();
			ln("    public final void set" + pNameUpper + "AsCommaSeparatedString(String " + p.getName() + ") {");
			ln("        " + getFieldName(p) + " = new " + p.getCollectionImpl() + "(Str.parseCommaSeparatedString("
					+ p.getName() + "));");
			ln("    }");
		}

		// --- updateXxx(Object value) ---
		ln();
		ln("    protected final void update" + pNameUpper + "(Object value) {");
		if (p.isReference()) {
			ln("        Collection<String> ids = (Collection<String>) value;");
			String daoExpr = p.getDaoName();
			if (p.isAbstract()) {
				daoExpr = "getDaoService()";
			}
			String suffix = p instanceof SetPropertyModel ? "AsSet" : "";
			ln("        set" + Str.uppercaseFirstLetter(p.getName()) + "((" + p.getCollectionType() + ") " + daoExpr
					+ ".getByIds" + suffix + "(ids));");
		} else {
			ln("        set" + Str.uppercaseFirstLetter(p.getName()) + "((" + p.getType() + ") value);");
		}
		ln("    }");
	}

	private void writeSimpleProperty(PropertyModel p) {
		String pNameUpper = Str.uppercaseFirstLetter(p.getName());

		// --- repairDeadXxxReference ---
		if (p.isReference()) {
			ReferencePropertyModel pRef = (ReferencePropertyModel) p;
			ln();
			ln("    protected void repairDead" + Str.uppercaseFirstLetter(p.getNameSingular())
					+ "Reference(String entityId) {");
			ln("        if (" + getFieldName(p) + " == null || entityId.equals(" + getFieldName(p) + ")) {");
			if (pRef.isMaster()) {
				ln("            repairMissingMaster();");
			} else {
				ln("            set" + pNameUpper + "(null);");
			}
			ln("        }");
			ln("    }");
		}

		if (!p.isPrimitive()) {
			// --- isXxxSet ---
			ln();
			ln("    public final boolean is" + pNameUpper + "Set() {");
			ln("        return " + getFieldName(p) + " != null;");
			ln("    }");
		}

		// --- isXxx ---
		ln();
		ln("    public final boolean is" + pNameUpper + "(" + p.getType() + " " + p.getName() + ") {");
		if (p.isPrimitive()) {
			ln("        return " + getFieldName(p) + " == " + p.getName() + ";");
		} else {
			ln("        if (" + getFieldName(p) + " == null && " + p.getName() + " == null) return true;");
			if (p.isReference()) {
				ln("        return " + p.getName() + " != null && " + p.getName() + ".getId().equals("
						+ getFieldName(p) + ");");
			} else {
				ln("        return " + getFieldName(p) + " != null && " + getFieldName(p) + ".equals(" + p.getName()
						+ ");");
			}
		}
		ln("    }");

		// --- updateXxx(Object value) ---
		ln();
		ln("    protected final void update" + pNameUpper + "(Object value) {");
		if (p.isReference()) {
			String daoExpr = p.getDaoName();
			if (p.isAbstract()) {
				daoExpr = "getDaoService()";
			}
			ln("        set" + pNameUpper + "(value == null ? null : (" + p.getType() + ")" + daoExpr
					+ ".getById((String)value));");
		} else if (p.isPrimitive()) {
			String type = p.getType();
			if (type.equals("int")) type = Integer.class.getSimpleName();
			if (type.equals("boolean")) type = Boolean.class.getSimpleName();
			ln("        set" + pNameUpper + "((" + type + ")value);");
		} else {
			String type = p.getType();
			if (type.equals(Date.class.getName())) {
				ln("        value = value == null ? null : new " + Date.class.getName() + "((String)value);");
			} else if (type.equals(Time.class.getName())) {
				ln("        value = value == null ? null : new " + Time.class.getName() + "((String)value);");
			} else if (type.equals(DateAndTime.class.getName())) {
				ln("        value = value == null ? null : new " + DateAndTime.class.getName() + "((String)value);");
			}
			ln("        set" + pNameUpper + "((" + p.getType() + ")value);");
		}
		ln("    }");
	}

	@Override
	protected String getSuperclass() {
		return bean.getSuperclass();
	}

	// --- dependencies ---

}
