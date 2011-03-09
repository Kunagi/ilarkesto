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
package ilarkesto.form.swing;

import ilarkesto.core.logging.Log;
import ilarkesto.form.FileFormField;
import ilarkesto.form.Form;
import ilarkesto.form.FormButton;
import ilarkesto.form.FormField;
import ilarkesto.form.TextFormField;
import ilarkesto.form.ValidationException;
import ilarkesto.swing.FileField;
import ilarkesto.swing.PanelBuilder;
import ilarkesto.swing.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FormDialog {

	private static final Log LOG = Log.get(FormDialog.class);
	private static final FormButton WINDOW_CLOSED_BUTTON = new FormButton(Form.ABORT_BUTTON_NAME);

	private Form form;
	private PanelBuilder pb;
	private JDialog dialog;
	private FormButton clickedButton;
	private boolean loop;
	private Map<String, JComponent> fieldComponents;

	public FormDialog(Form form) {
		this.form = form;
	}

	public static final FormButton showDialog(Window owner, Form form) {
		FormDialog fd = new FormDialog(form);
		return fd.showDialog(owner);
	}

	public FormButton showDialog(Window owner) {
		loop = true;
		while (loop) {
			loop = false;
			dialog = new JDialog(owner);
			dialog.setTitle(form.getFormTitle());
			dialog.setModal(true);
			dialog.add(createPanel());
			dialog.pack();
			if (owner == null) {
				Swing.center(dialog);
			} else {
				Swing.placeBest(dialog, owner);
			}
			dialog.setVisible(true);
		}
		return clickedButton != null ? clickedButton : WINDOW_CLOSED_BUTTON;
	}

	private void updateForm() {
		Map<String, String> data = new HashMap<String, String>();
		for (String name : fieldComponents.keySet()) {
			if (!name.startsWith("_")) {
				data.put(name, getComponentValue(fieldComponents.get(name)));
			}
		}
		form.update(data, Collections.EMPTY_LIST);
	}

	private String getComponentValue(JComponent c) {
		if (c instanceof JTextField) return ((JTextField) c).getText();
		if (c instanceof FileField) return ((FileField) c).getPath();
		throw new RuntimeException("Unsupported component: " + c.getClass().getName());
	}

	public JPanel createPanel() {
		pb = new PanelBuilder();
		pb.setDefaultPadding(2, 2, 5, 5);

		form.initialize();

		// TODO hidden fields

		String errorMessage = form.getErrorMessage();
		if (errorMessage != null) {
			if (!errorMessage.startsWith("<html>")) {
				errorMessage = "<html><font color='red'>" + errorMessage + "</font>";
			}
			pb.add(Swing.createMessageComponent(errorMessage)).setFillToHorizontal();
			pb.nl();
		}

		String sideImage = form.getSideImage();
		// TODO sideImage

		fieldComponents = new HashMap<String, JComponent>();
		for (FormField field : form.getVisibleFields()) {
			addField(field);
		}

		// buttons
		pb.addEmpty();
		for (FormButton button : form.getSubmitButtons()) {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(createButton(button));
			pb.add(panel);
		}
		pb.nl();

		return pb.toPanel();
	}

	private JButton createButton(FormButton button) {
		JButton b = new JButton(button.getLabel());
		b.addActionListener(new ButtonActionListener(button));
		b.setIcon(Swing.getIcon16(button.getIcon()));
		return b;
	}

	private void addField(FormField field) {
		String label = field.getLabel();
		String hintText = field.getHintText();
		if (field.isRequired()) label += "*";

		JLabel l = new JLabel(label);
		l.setToolTipText(hintText);
		Font font = l.getFont();
		l.setFont(new Font(font.getFamily(), Font.PLAIN, font.getSize()));
		l.setForeground(Color.GRAY);
		pb.add(l).setAnchorToNorthEast();

		JComponent f;
		if (field instanceof TextFormField) {
			f = createTextField((TextFormField) field);
		} else if (field instanceof FileFormField) {
			f = createFileField((FileFormField) field);
		} else {
			throw new RuntimeException("Unsupported field: " + field.getClass());
		}
		fieldComponents.put(field.getName(), f);

		String errorMessage = field.getErrorMessage();
		if (errorMessage != null) {
			if (!errorMessage.startsWith("<html>")) {
				errorMessage = "<html><font color='red'>" + errorMessage + "</font>";
			}
			JPanel panel = new JPanel(new BorderLayout(2, 2));
			panel.add(Swing.createMessageComponent(errorMessage), BorderLayout.NORTH);
			panel.add(f);
			pb.add(panel).setAnchorToWest();
		} else {
			pb.add(f).setAnchorToWest();
		}

		pb.nl();
	}

	private JComponent createTextField(TextFormField field) {
		JTextField f = new JTextField(field.getWidth());
		f.setText(field.getValueAsString());
		return f;
	}

	private JComponent createFileField(FileFormField field) {
		FileField f = field.isFolder() ? FileField.createForDirectory() : FileField.createForFile();
		f.setFile(field.getValue());
		return f;
	}

	private boolean handleButtonClick(FormButton button) throws ValidationException {

		// if (button instanceof ItemFormField.SelectButton) {
		// showSelectItemDialog(((ItemFormField.SelectButton) button).getField());
		// return true;
		// }
		//
		// if (button instanceof ItemFormField.ClearButton) {
		// ItemFormField field = ((ItemFormField.ClearButton) button).getField();
		// field.setValue(null);
		// return true;
		// }
		//
		// if (button instanceof MultiItemFormField.AddButton) {
		// MultiItemFormField field = ((MultiItemFormField.AddButton) button).getField();
		// Collection items = field.getSelectableItems();
		// Collection multiItems = field.getSelectableMultiItems();
		// Collection payloads;
		//
		// OptionAction action = beanProvider.autowire(new OptionAction());
		// action.setHorizontal(false);
		// action.setMessage(getString(form.getStringKeyPrefix() + "." + field.getName() +
		// ".select.message"));
		// IdGenerator itemIdGenerator = new CountingIdGenerator("item");
		// for (Object o : field.getSelectableMultiItems()) {
		// MultiItem item = (MultiItem) o;
		// IOption option = new Option(itemIdGenerator.generateId(), item.toString(), "multiItem", item);
		// option.setGroup(true);
		// action.addOption(option);
		// }
		// for (Object item : field.getSelectableItems()) {
		// action.addOption(new Option(itemIdGenerator.generateId(), item.toString(),
		// iconResolver.getIcon(item),
		// item));
		// }
		//
		// try {
		// actionPerformer.perform(action, this);
		// } catch (ActionAbortedException ex) {
		// return true;
		// }
		// Object item = action.getSelectedOption().getPayload();
		// if (item instanceof MultiItem) {
		// for (Object o : ((MultiItem) item).getItems()) {
		// field.addValueItem(o);
		// }
		// } else {
		// field.addValueItem(item);
		// }
		// return true;
		// }
		//
		// if (button instanceof MultiItemFormField.RemoveButton) {
		// MultiItemFormField field = ((MultiItemFormField.RemoveButton) button).getField();
		// Object item = ((MultiItemFormField.RemoveButton) button).getItem();
		// field.removeValueItem(item);
		// return true;
		// }
		//
		// if (button instanceof MultiItemFormField.RemoveAllButton) {
		// MultiItemFormField field = ((MultiItemFormField.RemoveAllButton) button).getField();
		// field.removeAllItems();
		// return true;
		// }
		//
		// if (button instanceof MultiComplexFormField.AddButton) {
		// MultiComplexFormField.AddButton b = (MultiComplexFormField.AddButton) button;
		// final MultiComplexFormField field = b.getField();
		// final BeanForm form = field.createSubform();
		// beanProvider.autowire(form);
		// form.setBean(field.getItemFactory().getBean());
		// FormAction action = new FormAction();
		// action.setForm(form);
		// try {
		// actionPerformer.perform(action, this);
		// } catch (ActionAbortedException ex) {
		// return true;
		// }
		// if (!action.isClickedButtonAbort()) {
		// field.addValueItem(form.getBean());
		// }
		// return true;
		// }
		//
		// if (button instanceof MultiComplexFormField.EditButton) {
		// MultiComplexFormField.EditButton b = (MultiComplexFormField.EditButton) button;
		// final MultiComplexFormField field = b.getField();
		// final BeanForm form = field.createSubform();
		// beanProvider.autowire(form);
		// form.setBean(b.getItem());
		// FormAction action = new FormAction();
		// action.setForm(form);
		// try {
		// actionPerformer.perform(action, this);
		// } catch (ActionAbortedException ex) {}
		// return true;
		//
		// }
		//
		// if (button instanceof MultiComplexFormField.RemoveButton) {
		// MultiComplexFormField field = ((MultiComplexFormField.RemoveButton) button).getField();
		// Object item = ((MultiComplexFormField.RemoveButton) button).getItem();
		// field.removeValueItem(item);
		// return true;
		// }

		if (button.isAbort()) return false;

		// user defined button
		return false;
	}

	class ButtonActionListener implements ActionListener {

		private FormButton button;

		public ButtonActionListener(FormButton button) {
			this.button = button;
		}

		public void actionPerformed(ActionEvent e) {
			clickedButton = button;
			if (clickedButton.isUpdateFields()) {
				updateForm();
			}
			if (clickedButton.isValidateForm()) {
				try {
					form.validate();
				} catch (ValidationException ex) {
					LOG.debug("Form is not valid:", ex);
					form.setErrorMessage(ex.getMessage());
					loop = true;
					dialog.dispose();
					return;
				}
			}

			try {
				loop = handleButtonClick(clickedButton);
			} catch (ValidationException ex) {
				LOG.debug("Form is not valid:", ex);
				form.setErrorMessage(ex.getMessage());
				loop = true;
			}

			dialog.dispose();
			return;
		}

	}

}
