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
package ilarkesto.form;

import ilarkesto.base.BeanMap;
import ilarkesto.base.Factory;
import ilarkesto.base.Money;
import ilarkesto.base.Str;
import ilarkesto.base.time.Date;
import ilarkesto.base.time.Time;
import ilarkesto.base.time.TimePeriod;
import ilarkesto.core.logging.Log;
import ilarkesto.email.EmailAddress;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeanForm<O> extends Form {

	private static final Log LOG = Log.get(BeanForm.class);

	private O bean;

	private List<FormField> beanFields = new ArrayList<FormField>();

	private BeanMap beanMap;

	public void setBean(O bean) {
		this.bean = bean;
		beanMap = new BeanMap(bean);
	}

	public MultiComplexFormField addMultiComplexProperty(String name, Class<? extends BeanForm> elementFormClass,
			Factory itemFactory) {
		MultiComplexFormField field = addMultiComplex(name, elementFormClass);
		field.setItemFactory(itemFactory);
		beanFields.add(field);

		Collection value = (Collection) beanMap.get(name);
		if (value != null) {
			Set<Object> set = new HashSet<Object>(value);
			field.setValue(set);
		}

		return field;
	}

	public OutputFormField addOutputProperty(String name) {
		OutputFormField field = addOutput(name);

		Object value = beanMap.get(name);
		if (value != null) field.setText(value.toString());

		return field;
	}

	public RadioSelectFormField addRadioSelectProperty(String name, Collection options) {
		RadioSelectFormField field = addRadioSelect(name);
		beanFields.add(field);

		field.setOptions(options);

		Object value = beanMap.get(name);
		if (value != null) field.setValue(value.toString());

		return field;
	}

	public UploadFormField addUploadProperty(String name) {
		UploadFormField field = addUpload(name);
		beanFields.add(field);
		return field;
	}

	public FileFormField addFileProperty(String name) {
		FileFormField field = addFile(name);
		beanFields.add(field);

		field.setValue((File) beanMap.get(name));

		return field;
	}

	public MultiItemFormField addMultiItemProperty(String name, Collection selectableItems) {
		MultiItemFormField field = addMultiItem(name);
		field.setSelectableItems(selectableItems);
		beanFields.add(field);

		Collection value = (Collection) beanMap.get(name);
		if (value != null) {
			Set l = new HashSet(value);
			field.setValue(l);
		}

		return field;
	}

	public ItemFormField addItemProperty(String name, Collection selectableItems) {
		ItemFormField field = addItem(name);
		field.setSelectableItems(selectableItems);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) {
			field.setValue(value);
		}

		return field;
	}

	public DropdownFormField addDropdownProperty(String name, Object[] selectableItems) {
		Collection items = new ArrayList(selectableItems.length);
		for (Object item : selectableItems)
			items.add(item);
		return addDropdownProperty(name, items);
	}

	public DropdownFormField addDropdownProperty(String name, Collection selectableItems) {
		DropdownFormField field = addDropdown(name);
		beanFields.add(field);

		field.setSelectableItems(selectableItems);

		Object value = beanMap.get(name);
		if (value != null) field.setValue(value);

		return field;
	}

	public CheckboxFormField addCheckboxProperty(String name) {
		CheckboxFormField field = addCheckbox(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		field.setChecked((Boolean) value);

		return field;
	}

	public TextFormField addTextProperty(String name) {
		TextFormField field = addText(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue(value.toString());

		return field;
	}

	public FloatFormField addFloatProperty(String name) {
		FloatFormField field = addFloat(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue((Float) value);

		return field;
	}

	public PasswordFormField addPasswordProperty(String name) {
		PasswordFormField field = addPassword(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue(value.toString());

		return field;
	}

	public IntegerFormField addIntegerProperty(String name) {
		IntegerFormField field = addInteger(name);
		beanFields.add(field);

		Integer value = (Integer) beanMap.get(name);
		if (value != null) field.setValue(value);

		return field;
	}

	public TextareaFormField addTextareaProperty(String name, boolean html) {
		TextareaFormField field = addTextarea(name);
		field.setHtml(html);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue(value.toString());

		return field;
	}

	public MoneyFormField addMoneyProperty(String name) {
		MoneyFormField field = addMoney(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue((Money) value);

		return field;
	}

	public EmailAddressFormField addEmailAddressProperty(String name) {
		EmailAddressFormField field = addEmailAddress(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue((EmailAddress) value);

		return field;
	}

	public DateFormField addDateProperty(String name) {
		DateFormField field = addDate(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue((Date) value);

		return field;
	}

	public TimeFormField addTimeProperty(String name) {
		TimeFormField field = addTime(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue((Time) value);

		return field;
	}

	public TimePeriodFormField addTimePeriodProperty(String name) {
		TimePeriodFormField field = addTimePeriod(name);
		beanFields.add(field);

		Object value = beanMap.get(name);
		if (value != null) field.setValue((TimePeriod) value);

		return field;
	}

	@Override
	public void validate() throws ValidationException {
		super.validate();

		for (FormField field : beanFields) {
			if (field instanceof DateFormField) {
				try {
					beanMap.put(field.getName(), ((DateFormField) field).getValueAsDate());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof TimeFormField) {
				try {
					beanMap.put(field.getName(), ((TimeFormField) field).getValueAsTime());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof TimePeriodFormField) {
				try {
					beanMap.put(field.getName(), ((TimePeriodFormField) field).getValueAsTimePeriod());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof MoneyFormField) {
				try {
					beanMap.put(field.getName(), ((MoneyFormField) field).getValueAsMoney());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof ItemFormField) {
				try {
					beanMap.put(field.getName(), ((ItemFormField) field).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Auswahl ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof MultiItemFormField) {
				try {
					beanMap.put(field.getName(), ((MultiItemFormField) field).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Auswahl ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof MultiComplexFormField) {
				try {
					beanMap.put(field.getName(), ((MultiComplexFormField) field).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Auswahl ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof UploadFormField) {
				File file = ((UploadFormField) field).getValue();
				beanMap.put(field.getName(), file);
			} else if (field instanceof FileFormField) {
				beanMap.put(field.getName(), ((FileFormField) field).getValue());
			} else if (field instanceof CheckboxFormField) {
				beanMap.put(field.getName(), ((CheckboxFormField) field).isChecked());
			} else if (field instanceof MultiCheckboxFormField) {
				beanMap.put(field.getName(), ((MultiCheckboxFormField) field).getValue());
			} else if (field instanceof RadioSelectFormField) {
				try {
					beanMap.put(field.getName(), ((RadioSelectFormField) field).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof IntegerFormField) {
				try {
					beanMap.put(field.getName(), ((IntegerFormField) field).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof FloatFormField) {
				try {
					beanMap.put(field.getName(), ((FloatFormField) field).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else if (field instanceof DropdownFormField) {
				try {
					beanMap.put(field.getName(), ((DropdownFormField) field).getValue());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.format(ex));
					throw new ValidationException(ERROR_MSG);
				}
			} else {
				try {
					beanMap.put(field.getName(), field.getValueAsString());
				} catch (Throwable ex) {
					LOG.debug(ex);
					field.setErrorMessage("Eingabe ung\u00FCltig: " + Str.getRootCauseMessage(ex));
					throw new ValidationException(ERROR_MSG);
				}
			}
		}
	}

	public O getBean() {
		return bean;
	}

}
