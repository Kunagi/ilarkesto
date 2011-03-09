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
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Utility methods for Swing. Dialogs, frames, positioning.
 */
public class Swing {

	public static void main(String[] args) throws Throwable {
		Log.setDebugEnabled(true);
		Log.DEBUG(UIManager.getInstalledLookAndFeels());
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		JOptionPane.showMessageDialog(null, createMessageComponent("Nachricht"));
		showMessageDialog(
			null,
			" Geschafft, obwohl der Text so scheiss lang ist.\n Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheissGeschafft, obwohl der Text so scheiss lang ist.\\n Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheissGeschafft, obwohl der Text so scheiss lang ist.\\n Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheissGeschafft, obwohl der Text so scheiss lang ist.\\n Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss lang ist. Geschafft, obwohl der Text so scheiss");
	}

	public static Dimension getFractionFromScreen(double xFactor, double yFactor) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension((int) (screen.getWidth() * xFactor), (int) (screen.getHeight() * yFactor));
	}

	public static void invokeInEventDispatchThreadLater(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	public static void invokeInEventDispatchThread(Runnable runnable) {
		if (isEventDispatchThread()) {
			runnable.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException ex) {
				return;
			} catch (InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static void assertEventDispatchThread() {
		if (!isEventDispatchThread()) throw new RuntimeException("Thread is not the EventDispatchThread");
	}

	public static boolean isEventDispatchThread() {
		return EventQueue.isDispatchThread();
	}

	public static boolean isBlank(JTextField field) {
		if (field == null) return true;
		return Str.isBlank(field.getText());
	}

	public static JDialog showModalDialogWithoutBlocking(Component parent, String title, Component content) {
		final JDialog dialog = new JDialog(getWindow(parent), title);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setModal(true);
		dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
		dialog.add(content);
		dialog.pack();
		// dialog.setMinimumSize(dialog.getPreferredSize());
		dialog.setResizable(false);
		placeBest(dialog, parent);
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				dialog.setVisible(true);
				synchronized (dialog) {
					dialog.notifyAll();
				}
			}
		};
		t.setDaemon(true);
		t.start();
		while (!dialog.isVisible()) {
			synchronized (dialog) {
				try {
					dialog.wait(10);
				} catch (InterruptedException ex) {}
			}
		}
		return dialog;
	}

	public static void showMessageDialog(Component parent, String message) {
		JFrame frame = null;
		if (parent == null) {
			frame = new JFrame("Nachricht");
			center(frame);
			frame.setVisible(true);
			parent = frame;
		}
		JOptionPane.showMessageDialog(parent, createMessageComponent(message));
		if (frame != null) {
			frame.dispose();
		}
	}

	public static JComponent createMessageComponent(String message) {
		return createMessageComponent(message, 600, null);
	}

	public static JComponent createMessageComponent(String message, int preferredWidth, Color color) {
		if (message != null && !message.startsWith("<html")) message = "<html>" + Str.replaceForHtml(message);
		JEditorPane editor = new JEditorPane("text/html", message);
		if (color != null) editor.setForeground(color);
		editor.setOpaque(false);
		editor.setEditable(false);
		int lines = editor.getPreferredSize().width / preferredWidth;
		if (lines == 0) {
			return editor;
		} else {
			int height = Math.min(editor.getPreferredSize().height + (lines * 25), 300);
			JScrollPane scroller = new JScrollPane(editor);
			scroller.setPreferredSize(new Dimension(preferredWidth, height));
			scroller.setBorder(null);
			return scroller;
		}
	}

	public static String showTextEditorDialog(Component parent, String text, String title) {
		JTextArea textArea = new JTextArea(text, 25, 80);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(640, 480));
		if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(parent, scrollPane, title,
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null)) return null;
		return textArea.getText();
	}

	public static int getWidth(String s, Font font) {
		JLabel l = new JLabel(s);
		l.setFont(font);
		return l.getPreferredSize().width;
	}

	public static JTextField activateSelectAllOnFocusGained(final JTextField field) {
		field.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				field.selectAll();
			}

		});
		return field;
	}

	public static JSplitPane createVerticalSplit(Component upper, Component lower, int dividerLocation) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.add(upper);
		splitPane.add(lower);
		splitPane.setDividerLocation(dividerLocation);
		return splitPane;
	}

	public static JSplitPane createHorizontalSplit(Component left, Component right) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(left);
		splitPane.add(right);
		return splitPane;
	}

	/**
	 * Takes a screenshot of the default graphics device.
	 * 
	 * @param windowToHide If this parameter is provided, the window will be hidden before the screen is
	 *            captured and then showed again. This causes that the window does not appear on the
	 *            screenshot.
	 */
	public static BufferedImage captureScreen(Window windowToHide) {
		return captureScreen(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(), windowToHide);
	}

	/**
	 * Takes a screenshot of a given device.
	 * 
	 * @param screen The graphics device to capture.
	 * @param windowToHide If this parameter is provided, the window will be hidden before the screen is
	 *            captured and then showed again. This causes that the window does not appear on the
	 *            screenshot.
	 */
	public static BufferedImage captureScreen(GraphicsDevice screen, Window windowToHide) {
		DisplayMode mode = screen.getDisplayMode();
		if (windowToHide != null) windowToHide.setVisible(false);
		try {
			return new Robot().createScreenCapture(new Rectangle(mode.getWidth(), mode.getHeight()));
		} catch (AWTException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (windowToHide != null) windowToHide.setVisible(true);
		}
	}

	/**
	 * Shows a component in a JFrame. The frame will be packed and centered on the screen.
	 * 
	 * @param component The Component to show.
	 * @param title The title of the frame.
	 * @param icon TODO
	 * @param exitOnClose Specifies if the Application quits, when the frame is closed.
	 * @return The packed and visible frame.
	 */
	public static JFrame showInJFrame(final Component component, final String title, final Image icon,
			final boolean exitOnClose) {
		final JFrame frame = new JFrame(title);
		if (icon != null) frame.setIconImage(icon);
		frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.HIDE_ON_CLOSE);
		frame.add(component);
		frame.pack();
		center(frame);
		frame.setVisible(true);
		return frame;
	}

	public static JFrame showInJFrame(Component component, double percentScreenWidth) {
		component.setPreferredSize(getFractionFromScreen(percentScreenWidth, percentScreenWidth));
		return showInJFrame(component);
	}

	public static JFrame showInJFrame(Component component) {
		return showInJFrame(component, component.getClass().getSimpleName(), null, true);
	}

	public static Window getWindow(Component component) {
		if (component == null) return null;
		if (component instanceof Window) return (Window) component;
		return SwingUtilities.windowForComponent(component);
	}

	/**
	 * Creates a default modal dialog for a component.
	 * 
	 * @param component The component, to show in a dialog.
	 * @param parent The component, whichs frame to be the parent of the modal dialog.
	 * @return The created dialog.
	 */
	public static JDialog createDialog(Component component, Component parent, String title) {
		Window window = getWindow(parent);
		JDialog dialog = new JDialog((Frame) window); // TODO remove cast!
		dialog.setTitle(title);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.getContentPane().add(component);
		dialog.setModal(true);
		dialog.pack();
		placeBest(dialog, window);
		return dialog;
	}

	public static JFrame createFrame(Component component, Component parent, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(component);
		frame.pack();
		placeBest(frame, parent);
		return frame;
	}

	private static Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

	/**
	 * Determines and loads a 16x16 icon. The icon hast to be placed in the classpath as the following file:
	 * <code>img/16/{name}.png</code> The icon will be cached.
	 * 
	 * @param name The name of the icon (Filename without extension).
	 */
	public static ImageIcon getIcon16(String name) {
		return getIcon("img/16/" + name + ".png", 16);
	}

	public static ImageIcon getIcon128(String name) {
		return getIcon("img/128/" + name + ".png", 128);
	}

	public static ImageIcon getIcon(String path) {
		return getIcon(path, null);
	}

	public static ImageIcon getIcon(String path, Integer size) {
		if (icons.containsKey(path)) return icons.get(path);
		ImageIcon result = new ImageIcon(getImage(path, size));
		icons.put(path, result);
		return result;
	}

	public static int getTrayIconSize() {
		Dimension dim = SystemTray.getSystemTray().getTrayIconSize();
		int size = (int) Math.min(dim.getWidth(), dim.getHeight());
		return size;
	}

	public static Image getImage(String path, Integer size) {
		BufferedImage im = IO.loadImage(path);
		if (size == null) return im;
		return IO.getScaled(im, size, size);
	}

	public static void addTrayIcon(TrayIcon trayIcon) {
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Centers a window on the screen.
	 */
	public static void center(Window window) {
		Dimension w = window.getSize();
		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((s.width - w.width) / 2, (s.height - w.height) / 2);
	}

	/**
	 * Places a window at the best (centered) position relative to another window.
	 */
	public static void placeBest(Window window, Window parent) {
		if (parent == null) {
			center(window);
			return;
		}
		placeBest(window, parent.getLocation(), parent.getSize());
	}

	/**
	 * Places a window at the best (centered) position relative to another window.
	 */
	public static void placeBest(Window window, Component parent) {
		Window parentWindow = getWindow(parent);
		if (parentWindow == null) {
			center(window);
			return;
		}
		placeBest(window, parentWindow.getLocation(), parent.getSize());
	}

	/**
	 * Places a window at the best (centered) position relative to another window.
	 */
	public static void placeBest(Window window, Point parentPosition, Dimension parentSize) {
		window.setLocation(getBestWindowPosition(window.getSize(), parentPosition, parentSize));
	}

	/**
	 * Determines the best (centered) position for a window relative to another window.
	 */
	public static Point getBestWindowPosition(Dimension dim, Point parentPos, Dimension parentDim) {
		return getBestWindowPosition(dim, getCenter(parentPos, parentDim));
	}

	/**
	 * Determines the best (centered) position for a window relative to another window.
	 */
	public static Point getBestWindowPosition(Dimension dim, Point parentPos) {
		int x = parentPos.x - (dim.width / 2);
		int y = parentPos.y - (dim.height / 2);
		boolean xOk = false;
		boolean yOk = false;
		if (x < 0) {
			x = 0;
			xOk = true;
		}
		if (y < 0) {
			y = 0;
			yOk = true;
		}
		if (!xOk) {
			if (x + dim.width > Toolkit.getDefaultToolkit().getScreenSize().width) {
				x = Toolkit.getDefaultToolkit().getScreenSize().width - dim.width;
			}
		}
		if (!yOk) {
			if (y + dim.height > Toolkit.getDefaultToolkit().getScreenSize().height - 20) {
				y = Toolkit.getDefaultToolkit().getScreenSize().height - dim.height - 20;
			}
		}
		return new Point(x, y);
	}

	/**
	 * Determines the center of an window.
	 */
	public static Point getCenter(Point parentPos, Dimension parentDim) {
		return new Point(parentPos.x + (parentDim.width / 2), parentPos.y + (parentDim.height / 2));
	}

	public static MenuItem createMenuItem(String label, ActionListener actionListener) {
		MenuItem item = new MenuItem(label);
		item.addActionListener(actionListener);
		return item;
	}

	private Swing() {}

}
