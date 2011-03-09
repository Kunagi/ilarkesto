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
import ilarkesto.gwt.client.AGwtDao;
import ilarkesto.gwt.client.AGwtEntity;
import ilarkesto.gwt.client.Gwt;
import ilarkesto.gwt.client.editor.ABooleanEditorModel;
import ilarkesto.gwt.client.editor.ADateAndTimeEditorModel;
import ilarkesto.gwt.client.editor.ADateEditorModel;
import ilarkesto.gwt.client.editor.AFloatEditorModel;
import ilarkesto.gwt.client.editor.AIntegerEditorModel;
import ilarkesto.gwt.client.editor.AOptionEditorModel;
import ilarkesto.gwt.client.editor.ATextEditorModel;
import ilarkesto.gwt.client.editor.ATimeEditorModel;
import ilarkesto.mda.legacy.model.ApplicationModel;
import ilarkesto.mda.legacy.model.BackReferenceModel;
import ilarkesto.mda.legacy.model.BeanModel;
import ilarkesto.mda.legacy.model.EntityModel;
import ilarkesto.mda.legacy.model.PredicateModel;
import ilarkesto.mda.legacy.model.PropertyModel;
import ilarkesto.mda.legacy.model.StringPropertyModel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class GwtEntityGenerator extends ABeanGenerator<EntityModel> {

	private ApplicationModel application;

	public GwtEntityGenerator(EntityModel datobModel, ApplicationModel application) {
		super(datobModel);
		this.application = application;
	}

	@Override
	protected void writeContent() {
		dao();
		predicates();
		constructors();
		type();
		properties();
		updatePropertiesMethod();
		storePropertiesMethod();
		backReferences();
		if (bean.isSearchable()) searchable();

	}

	private void backReferences() {
		Set<String> backRefs = new HashSet<String>();
		for (BackReferenceModel br : bean.getBackReferences()) {
			if (backRefs.contains(br.getName())) continue;
			backRefs.add(br.getName());
			backReference(br);
		}
	}

	private void backReference(BackReferenceModel br) {
		ln();
		PropertyModel ref = br.getReference();
		BeanModel refEntity = ref.getBean();
		if (refEntity instanceof EntityModel) {
			if (!((EntityModel) refEntity).isGwtSupport()) return;
		}
		String by = Str.uppercaseFirstLetter(ref.getName());
		if (ref.isCollection()) by = Str.removeSuffix(by, "s");
		if (ref.isUnique()) {
			ln("    public final " + refEntity.getBeanClass().replace(".server.", ".client.") + " get"
					+ Str.uppercaseFirstLetter(br.getName()) + "() {");
			ln("        return getDao().get" + Str.uppercaseFirstLetter(br.getName()) + "By" + by + "(("
					+ bean.getName() + ")this);");
			ln("    }");
		} else {
			ln("    public final java.util.List<" + refEntity.getBeanClass().replace(".server.", ".client.") + "> get"
					+ Str.uppercaseFirstLetter(br.getName()) + "s() {");
			ln("        return getDao().get" + refEntity.getName() + "sBy" + by + "((" + bean.getName() + ")this);");
			ln("    }");
		}
	}

	private void searchable() {
		ln();
		ln("    @Override");
		ln("    public boolean matchesKey(String key) {");
		ln("        if (super.matchesKey(key)) return true;");
		for (PropertyModel p : bean.getProperties()) {
			if (!p.isSearchable()) continue;
			ln("        if (matchesKey(get" + Str.uppercaseFirstLetter(p.getName()) + "(), key)) return true;");
		}
		ln("        return false;");
		ln("    }");
	}

	private void editor(PropertyModel property) {
		if (property.isCollection()) return;
		if (property.isReference()) return;
		String modelProperty = property.getName() + "Model";
		String nameUpper = Str.uppercaseFirstLetter(property.getName());
		String baseClassName = null;
		String type = property.getType();
		if (property.isOptionRestricted()) {
			if (type.equals(int.class.getName())) type = Integer.class.getName();
			baseClassName = AOptionEditorModel.class.getName() + "<" + type + ">";
		} else if (type.equals(int.class.getName()) || type.equals(Integer.class.getName())) {
			baseClassName = AIntegerEditorModel.class.getName();
			type = Integer.class.getName();
		} else if (type.equals(float.class.getName()) || type.equals(Float.class.getName())) {
			baseClassName = AFloatEditorModel.class.getName();
			type = Float.class.getName();
		} else if (type.equals(Date.class.getName())) {
			baseClassName = ADateEditorModel.class.getName();
			type = ilarkesto.gwt.client.Date.class.getName();
		} else if (type.equals(Time.class.getName())) {
			baseClassName = ATimeEditorModel.class.getName();
			type = ilarkesto.gwt.client.Time.class.getName();
		} else if (type.equals(DateAndTime.class.getName())) {
			baseClassName = ADateAndTimeEditorModel.class.getName();
			type = ilarkesto.gwt.client.DateAndTime.class.getName();
		} else if (type.equals(String.class.getName())) {
			baseClassName = ATextEditorModel.class.getName();
		} else if (type.equals(boolean.class.getName())) {
			baseClassName = ABooleanEditorModel.class.getName();
			type = Boolean.class.getName();
		} else if (type.equals(Boolean.class.getName())) {
			baseClassName = ABooleanEditorModel.class.getName();
		}
		if (baseClassName == null) return;

		ln();
		ln("    private transient " + nameUpper + "Model " + modelProperty + ";");
		ln();
		ln("    public " + nameUpper + "Model get" + nameUpper + "Model() {");
		ln("        if (" + modelProperty + " == null) " + modelProperty + " = create"
				+ Str.uppercaseFirstLetter(modelProperty) + "();");
		ln("        return " + modelProperty + ";");
		ln("    }");
		ln();
		ln("    protected " + Str.uppercaseFirstLetter(nameUpper) + "Model create" + nameUpper
				+ "Model() { return new " + nameUpper + "Model(); }");
		ln();
		ln("    protected class " + nameUpper + "Model extends " + baseClassName + " {");
		ln();
		ln("        @Override");
		ln("        public String getId() {");
		ln("            return \"" + bean.getName() + "_" + property.getName() + "\";");
		ln("        }");
		ln();
		ln("        @Override");
		ln("        public " + type + " getValue() {");
		if (type.equals(Boolean.class.getName())) {
			ln("            return is" + nameUpper + "();");
		} else {
			ln("            return get" + nameUpper + "();");
		}
		ln("        }");
		ln();
		ln("        @Override");
		ln("        public void setValue(" + type + " value) {");
		ln("            set" + nameUpper + "(value);");
		ln("        }");
		if (baseClassName.equals(AIntegerEditorModel.class.getName())) {
			ln();
			ln("            @Override");
			ln("            public void increment() {");
			ln("                set" + nameUpper + "(get" + nameUpper + "() + 1);");
			ln("            }");
			ln();
			ln("            @Override");
			ln("            public void decrement() {");
			ln("                set" + nameUpper + "(get" + nameUpper + "() - 1);");
			ln("            }");
		}
		if (property.isOptionRestricted()) {
			ln();
			ln("        @Override");
			ln("        public List<" + type + "> getOptions() {");
			ln("            return get" + nameUpper + "Options();");
			ln("        }");
		}
		if (property.isMandatory()) {
			ln();
			ln("        @Override");
			ln("        public boolean isMandatory() { return true; }");
		}
		String editablePredicate = property.getEditablePredicate();
		if (editablePredicate != null) {
			String returnValue = editablePredicate.equals("false") ? "false" : getName() + ".this.is"
					+ Str.uppercaseFirstLetter(editablePredicate) + "()";
			ln();
			ln("        @Override");
			ln("        public boolean isEditable() { return " + returnValue + "; }");
		}
		if (property instanceof StringPropertyModel) {
			StringPropertyModel sProperty = (StringPropertyModel) property;
			if (sProperty.isRichtext()) {
				ln();
				ln("        @Override");
				ln("        public boolean isRichtext() { return true; }");
			}
			if (sProperty.isMasked()) {
				ln();
				ln("        @Override");
				ln("        public boolean isMasked() { return true; }");
			}
			if (sProperty.isMaxLengthSet()) {
				ln();
				ln("        @Override");
				ln("        public int getMaxLength() { return " + sProperty.getMaxLenght() + "; }");
			}
			if (sProperty.isTemplateAvailable()) {
				ln();
				ln("        @Override");
				ln("        public String getTemplate() { return get" + nameUpper + "Template(); }");
			}
		}
		if (property.getTooltip() != null) {
			ln("        @Override");
			ln("        public String getTooltip() { return \"" + property.getTooltip().replace("\\", "\\\\") + "\"; }");
		}
		ln();
		ln("        @Override");
		ln("        protected void onChangeValue(" + type + " oldValue, " + type + " newValue) {");
		ln("            super.onChangeValue(oldValue, newValue);");
		ln("            addUndo(this, oldValue);");
		ln("        }");
		ln();
		ln("    }");
	}

	private void dao() {
		ln();
		String daoClass = application.getPackageName().replace(".server", ".client") + ".Dao";
		ln("    protected " + daoClass + " getDao() {");
		ln("        return " + daoClass + ".get();");
		ln("    }");
	}

	private void constructors() {
		ln();
		ln("    public", "G" + bean.getName() + "() {");
		ln("    }");
		ln();
		ln("    public", "G" + bean.getName() + "(Map data) {");
		ln("        super(data);");
		ln("        updateProperties(data);");
		ln("    }");
	}

	private void type() {
		ln();
		ln("    public static final String ENTITY_TYPE = \"" + Str.lowercaseFirstLetter(bean.getName()) + "\";");
		ln();
		ln("    @Override");
		ln("    public final String getEntityType() {");
		ln("        return ENTITY_TYPE;");
		ln("    }");
	}

	private void predicates() {
		for (PredicateModel p : bean.getPredicates()) {
			ln();
			ln("    public abstract boolean is" + Str.uppercaseFirstLetter(p.getName()) + "();");
		}
	}

	private void properties() {
		for (PropertyModel p : bean.getProperties()) {
			property(p);
		}
	}

	private void property(PropertyModel p) {
		String name = p.getName();
		String nameUpper = Str.uppercaseFirstLetter(p.getName());
		ln();
		comment(p.getName());
		ln();
		if (p.isCollection()) {
			if (p.isReference()) {
				// reference collection
				String type = p.getType().replace(".server.", ".client.");
				String typeName = type.substring(type.lastIndexOf('.') + 1, type.length() - 1);
				String contentType = p.getContentType().replace(".server.", ".client.");
				String varName = name + "Ids";
				String nameSingular = p.getNameSingular();
				String nameSingularUpper = Str.uppercaseFirstLetter(nameSingular);
				ln("    private Set<String>", varName + " = new HashSet<String>();");
				ln();
				ln("    public final", type, "get" + nameUpper + "() {");
				ln("        if (", varName + ".isEmpty()) return Collections.emptySet();");
				ln("        return getDao().get" + typeName + "s(this." + varName + ");");
				ln("    }");
				ln();
				ln("    public final void set" + nameUpper + "(Collection<" + contentType + "> values) {");
				ln("        " + varName + " = " + Gwt.class.getName() + ".getIdsAsSet(values);");
				ln("        propertyChanged(\"" + p.getName() + "Ids\", this." + p.getName() + "Ids);");
				ln("    }");
				ln();
				ln("    public final void add" + nameSingularUpper + "(" + contentType, nameSingular + ") {");
				ln("        String id = " + nameSingular + ".getId();");
				ln("        if (" + varName + ".contains(id)) return;");
				ln("        " + varName + ".add(id);");
				ln("        propertyChanged(\"" + p.getName() + "Ids\", this." + p.getName() + "Ids);");
				ln("    }");
				ln();
				ln("    public final void remove" + nameSingularUpper + "(" + contentType, nameSingular + ") {");
				ln("        String id = " + nameSingular + ".getId();");
				ln("        if (!" + varName + ".contains(id)) return;");
				ln("        " + varName + ".remove(id);");
				ln("        propertyChanged(\"" + p.getName() + "Ids\", this." + p.getName() + "Ids);");
				ln("    }");
				ln();
				ln("    public final boolean contains" + nameSingularUpper + "(" + contentType + " " + nameSingular
						+ ") {");
				ln("        return " + varName + ".contains(" + nameSingular + ".getId());");
				ln("    }");
				ln();
			} else {
				// data collection
				String type = p.getType().replace(".server.", ".client.");
				String typeName = type.substring(type.lastIndexOf('.') + 1, type.length() - 1);
				String contentType = p.getContentType().replace(".server.", ".client.");
				String nameSingular = p.getNameSingular();
				String nameSingularUpper = Str.uppercaseFirstLetter(nameSingular);
				ln("    private " + p.getType() + " " + p.getName() + " = new " + p.getCollectionImpl() + "<"
						+ p.getContentType() + ">();");
				ln();
				ln("    public final " + p.getType() + " get" + nameUpper + "() {");
				ln("        return new " + p.getCollectionImpl() + "<" + p.getContentType() + ">(" + p.getName() + ");");
				ln("    }");
				ln();
				ln("    public final void set" + nameUpper + "(" + p.getType() + " " + p.getName() + ") {");
				ln("        if (" + p.getName()
						+ " == null) throw new IllegalArgumentException(\"null is not allowed\");");
				ln("        if (this." + p.getName() + ".equals(" + p.getName() + ")) return;");
				ln("        this." + p.getName() + " = new " + p.getCollectionImpl() + "<" + p.getContentType() + ">("
						+ p.getName() + ");");
				ln("        propertyChanged(\"" + p.getName() + "\", this." + p.getName() + ");");
				ln("    }");
				ln();
				ln("    public final boolean contains" + nameSingularUpper + "(" + contentType + " " + nameSingular
						+ ") {");
				ln("        return " + name + ".contains(" + nameSingular + ");");
				ln("    }");
				ln();
			}
		} else {
			// simple (not collection)
			String type = p.getType();
			if (type.equals(Date.class.getName())) type = ilarkesto.gwt.client.Date.class.getName();
			if (type.equals(Time.class.getName())) type = ilarkesto.gwt.client.Time.class.getName();
			if (type.equals(DateAndTime.class.getName())) type = ilarkesto.gwt.client.DateAndTime.class.getName();
			if (p.isReference()) {
				// simple reference
				String typeName;
				if (type.equals("ilarkesto.persistence.AEntity")) {
					type = AGwtEntity.class.getName();
					typeName = "Entity";
				} else {
					type = type.replace(".server.", ".client.");
					typeName = type.substring(type.lastIndexOf('.') + 1);
				}
				ln("    private String", p.getName() + "Id;");
				ln();
				ln("    public final", type, "get" + nameUpper + "() {");
				ln("        if (" + p.getName() + "Id == null) return null;");
				ln("        return getDao().get" + typeName + "(this." + p.getName() + "Id);");
				ln("    }");
				ln();
				ln("    public final boolean is" + nameUpper + "Set() {");
				ln("        return " + p.getName() + "Id != null;");
				ln("    }");
				ln();
				ln("    public final", bean.getName(), "set" + nameUpper + "(" + type, p.getName() + ") {");
				ln("        String id = " + p.getName() + " == null ? null : " + p.getName() + ".getId();");
				ln("        if (equals(this." + p.getName() + "Id, id)) return (" + bean.getName() + ") this;");
				ln("        this." + p.getName() + "Id = id;");
				ln("        propertyChanged(\"" + p.getName() + "Id\", this." + p.getName() + "Id);");
				ln("        return (" + bean.getName() + ")this;");
				ln("    }");
			} else {
				// simple property
				ln("    private", type, p.getName(), ";");
				ln();
				ln("    public final", type, (p.isBoolean() ? "is" : "get") + nameUpper + "() {");
				ln("        return this." + p.getName(), ";");
				ln("    }");
				ln("");
				ln("    public final", bean.getName(), "set" + nameUpper + "(" + type, p.getName() + ") {");
				ln("        if (is" + nameUpper + "(" + p.getName() + ")) return (" + bean.getName() + ")this;");
				if (p.isUnique()) {
					ln("        if (" + p.getName() + " != null && getDao().get" + bean.getName() + "By" + nameUpper
							+ "(" + p.getName() + ") != null) throw new RuntimeException(\"\\\"\" + " + p.getName()
							+ " + \"\\\" already exists.\");");
				}
				ln("        this." + p.getName(), "=", p.getName(), ";");
				ln("        propertyChanged(\"" + p.getName() + "\", this." + p.getName() + ");");
				ln("        return (" + bean.getName() + ")this;");
				ln("    }");
				if (p.isOptionRestricted()) {
					String optionType = type;
					if (optionType.equals(int.class.getName())) optionType = Integer.class.getName();
					ln();
					ln("    public abstract List<" + optionType + "> get" + nameUpper + "Options();");
				}
				if (p instanceof StringPropertyModel) {
					StringPropertyModel sp = (StringPropertyModel) p;
					if (sp.isTemplateAvailable()) {
						ln();
						ln("    public abstract String get" + nameUpper + "Template();");
					}
				}
			}
			ln();
			ln("    public final boolean is" + nameUpper + "(" + type.replace(".server.", ".client."), p.getName()
					+ ") {");
			ln("        return equals(this." + (p.isReference() ? p.getName() + "Id" : p.getName()) + ", "
					+ p.getName() + ");");
			ln("    }");
		}
		editor(p);
	}

	private void updatePropertiesMethod() {
		ln();
		comment("update properties by map");
		ln();
		ln("    public void updateProperties(Map props) {");
		for (PropertyModel p : bean.getProperties()) {
			if (p.isCollection()) {
				if (p.isReference()) {
					ln("        " + p.getName() + "Ids = (Set<String>) props.get(\"" + p.getName() + "Ids\");");
				} else {
					ln("        " + p.getName(), " = (" + p.getType() + ") props.get(\"" + p.getName() + "\");");
				}
			} else {
				if (p.isReference()) {
					ln("        " + p.getName() + "Id = (String) props.get(\"" + p.getName() + "Id\");");
				} else {
					String type = p.getType();
					if (type.equals(Date.class.getName())) {
						ln("        String " + p.getName() + "AsString = (String) props.get(\"" + p.getName() + "\");");
						ln("        " + p.getName(), " =  " + p.getName() + "AsString == null ? null : new "
								+ ilarkesto.gwt.client.Date.class.getName() + "(" + p.getName() + "AsString);");
					} else if (type.equals(Time.class.getName())) {
						ln("        String " + p.getName() + "AsString = (String) props.get(\"" + p.getName() + "\");");
						ln("        " + p.getName(), " =  " + p.getName() + "AsString == null ? null : new "
								+ ilarkesto.gwt.client.Time.class.getName() + "(" + p.getName() + "AsString);");
					} else if (type.equals(DateAndTime.class.getName())) {
						ln("        String " + p.getName() + "AsString = (String) props.get(\"" + p.getName() + "\");");
						ln("        " + p.getName(), " =  " + p.getName() + "AsString == null ? null : new "
								+ ilarkesto.gwt.client.DateAndTime.class.getName() + "(" + p.getName() + "AsString);");
					} else {
						if (type.equals("boolean")) type = "Boolean";
						if (type.equals("int")) type = "Integer";
						ln("        " + p.getName(), " = (" + type + ") props.get(\"" + p.getName() + "\");");
					}
				}
			}
		}
		ln("        updateLocalModificationTime();");
		ln("    }");
	}

	private void storePropertiesMethod() {
		ln();
		ln("    @Override");
		ln("    public void storeProperties(Map properties) {");
		ln("        super.storeProperties(properties);");
		for (PropertyModel p : bean.getProperties()) {
			if (p.isCollection()) {
				String propertyVar = p.isReference() ? p.getName() + "Ids" : p.getName();
				ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar + ");");
			} else {
				String type = p.getType();
				if (type.equals(Date.class.getName())) {
					String propertyVar = p.getName();
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
							+ " == null ? null : this." + propertyVar + ".toString());");
				} else if (type.equals(Time.class.getName())) {
					String propertyVar = p.getName();
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
							+ " == null ? null : this." + propertyVar + ".toString());");
				} else if (type.equals(DateAndTime.class.getName())) {
					String propertyVar = p.getName();
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar
							+ " == null ? null : this." + propertyVar + ".toString());");
				} else {
					String propertyVar = p.isReference() ? p.getName() + "Id" : p.getName();
					ln("        properties.put(\"" + propertyVar + "\", this." + propertyVar + ");");
				}
			}
		}
		ln("    }");

	}

	@Override
	protected String getName() {
		return "G" + bean.getName();
	}

	@Override
	protected String getPackage() {
		return bean.getPackageName().replace(".server.", ".client.");
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

	@Override
	protected String getSuperclass() {
		return "scrum.client.common.AScrumGwtEntity";
	}

	@Override
	protected Set<String> getImports() {
		Set<String> ret = new LinkedHashSet<String>(super.getImports());
		ret.add("scrum.client.common.*");
		ret.add(AGwtDao.class.getPackage().getName() + ".*");
		return ret;
	}
}
