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
package ilarkesto.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileField extends JPanel {

	public static void main(String[] args) {
		FileField ff = createForDirectory();
		Swing.showInJFrame(ff);
	}

	private JFileChooser fileChooser;
	private JTextField textField;
	private FileSelectionListener fileSelectionListener;

	public FileField() {
		super(new BorderLayout());

		textField = new JTextField(30);
		JButton selectFileButton = new JButton("...");
		selectFileButton.addActionListener(new SelectActionListener());

		add(textField, BorderLayout.CENTER);
		add(selectFileButton, BorderLayout.EAST);

		fileChooser = new JFileChooser();
	}

	public void addFileSelectionListener(FileSelectionListener listener) {
		if (fileSelectionListener != null) throw new IllegalStateException("fileSelectionListener already set");
		fileSelectionListener = listener;
	}

	public static FileField createForDirectory() {
		FileField f = new FileField();
		f.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		return f;
	}

	public static FileField createForFile() {
		FileField f = new FileField();
		f.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		return f;
	}

	public void select() {
		File file = getFile();
		if (file != null) fileChooser.setSelectedFile(file);
		if (JFileChooser.APPROVE_OPTION == fileChooser.showDialog(getFileChooser(), "OK")) {
			File selectedFile = fileChooser.getSelectedFile();
			setFile(selectedFile);
			if (selectedFile != null && fileSelectionListener != null)
				fileSelectionListener.onFileSelected(selectedFile);
		}
	}

	public void setFile(File file) {
		textField.setText(file == null ? null : file.getPath());
	}

	public File getFile() {
		String path = getPath();
		return path == null ? null : new File(path);
	}

	public String getPath() {
		String path = textField.getText().trim();
		if (path.length() == 0) return null;
		return path;
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	class SelectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			select();
		}
	}

	public static interface FileSelectionListener {

		void onFileSelected(File file);

	}

}
