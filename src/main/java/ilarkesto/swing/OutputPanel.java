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
import java.awt.Dimension;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class OutputPanel extends JPanel {

	public static void main(String[] args) throws Throwable {
		OutputPanel op = new OutputPanel();
		Swing.showInJFrame(op);
		op.append("message 1");
		op.append("message 2");
		Thread.sleep(2000);
		op.clear();
		op.append("message 3");
		op.append("message 4");
	}

	private BlockingQueue<String> strings;
	private JEditorPane outputPane;
	private JScrollPane scroller;

	public OutputPanel() {
		super(new BorderLayout());

		strings = new LinkedBlockingDeque<String>();

		outputPane = new JEditorPane("text/html", "");
		outputPane.setEditable(false);

		scroller = new JScrollPane(outputPane);
		scroller.setPreferredSize(new Dimension(600, 200));

		add(scroller, BorderLayout.CENTER);
	}

	public void append(String text) {
		strings.add("<p>" + Str.replaceForHtml(text) + "</p>");
		updateOutputPane();
	}

	public void clear() {
		strings.clear();
		updateOutputPane();
	}

	private void updateOutputPane() {
		Swing.invokeInEventDispatchThreadLater(new Runnable() {

			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				for (String s : strings)
					sb.append(s);
				outputPane.setText(sb.toString());
			}
		});
	}
}
