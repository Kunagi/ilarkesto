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

import ilarkesto.base.Str;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ExceptionPanel extends JPanel {

    public static void main(String[] args) {
        showErrorDialog(
            null,
            new RuntimeException(
                    new RuntimeException(
                            new RuntimeException(
                                    new RuntimeException(
                                            new RuntimeException(
                                                    new RuntimeException(
                                                            new RuntimeException(
                                                                    "Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!Unrecoverable error in your brain! Unrecoverable error in your brain! Unrecoverable error in your brain!"))))))));
    }

    private JComponent messageComponent;
    private JTextArea stackTraceField;
    private JPanel expandButtonPanel;

    public ExceptionPanel(Throwable exception) {

        // messageField = new JEditorPane();
        // messageField = new JTextArea(3, 50);
        // messageField = new JLabel();
        // messageField.setOpaque(false);
        // Font font = messageField.getFont();
        // messageField.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
        // String msg = Str.getRootCauseMessage(exception);
        // messageField.setText("<html>" + Str.replaceForHtml(msg));
        messageComponent = Swing.createMessageComponent(Str.getRootCauseMessage(exception));

        stackTraceField = new JTextArea(10, 50);
        stackTraceField.setOpaque(false);
        // stackTraceField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, font.getSize() - 1));
        stackTraceField.setText(Str.getStackTrace(exception));

        setLayout(new BorderLayout(10, 10));
        add(messageComponent, BorderLayout.NORTH);

        JButton expandButton = new JButton("?");
        // expandButton.setFont(new Font(font.getFamily(), Font.PLAIN, font.getSize() - 5));
        expandButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                expand();
            }

        });
        expandButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        expandButtonPanel.add(expandButton);
        add(expandButtonPanel, BorderLayout.EAST);

        setOpaque(false);
    }

    private void expand() {
        JScrollPane stackTraceFieldScrollPane = new JScrollPane(stackTraceField);
        add(stackTraceFieldScrollPane, BorderLayout.CENTER);
        remove(expandButtonPanel);
        Window window = Swing.getWindow(messageComponent);
        window.pack();
        Swing.center(window);
    }

    public static void showErrorDialog(Component parent, Throwable ex) {
        JOptionPane.showMessageDialog(parent, new ExceptionPanel(ex), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showDialog(Component parent, Throwable ex, String title) {
        JOptionPane.showMessageDialog(parent, new ExceptionPanel(ex), title, JOptionPane.ERROR_MESSAGE);
    }

}
