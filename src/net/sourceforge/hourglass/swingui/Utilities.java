/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * --------------------------------------------------------------------
 *
 * CVS Revision $Revision: 1.14 $
 * Last modified on $Date: 2008/11/13 21:08:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.Component;
import java.awt.Frame;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import net.sourceforge.hourglass.Constants;
import net.sourceforge.hourglass.framework.IHourglassException;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;

import org.apache.log4j.Logger;

/**
 * Utilities for the Swing UI
 * 
 * @author Mike Grant
 */
public class Utilities {

	private static final String MNEMONIC_RESOURCE_SUFFIX = ".mnemonic";
	private Utilities() {
		initializeResourceBundle();
	}

	private void initializeResourceBundle() {
		m_resources = ResourceBundle
				.getBundle(Constants.SWINGUI_RESOURCE_BUNDLE);
	}

	/**
	 * Returns the sole instance of Utilities.
	 */
	public static Utilities getInstance() {
		if (s_instance == null) {
			s_instance = new Utilities();
		}
		return s_instance;
	}

	/**
	 * Returns a test instance of Utilities. This method should never be used
	 * outside the unit tests for Utilities.
	 */
	static Utilities createTestInstance() {
		return new Utilities();
	}

	/**
	 * Returns the icon from the specified raw resource name.
	 */
	public Icon getIconFromResourceName(String resourceName) {
		return getIconFromResourceName(getClass().getClassLoader(),
				resourceName);
	}

	public Icon getIconFromResourceName(ClassLoader cl, String resourceName) {
		URL u = cl.getResource(resourceName);
		if (u == null) {
			getLogger().warn("Can't find icon " + resourceName);
			return null;
		} else {
			Icon result = new ImageIcon(u);
			return result;
		}
	}

	/**
	 * Returns an icon identified by the given resource key
	 */
	public Icon getIcon(String resourceKey) {
		return getIconFromResourceName(m_resources.getString(resourceKey));
	}

	/**
	 * Returns a new Date that has day of the daySource but the time of the
	 * timeSource.
	 * 
	 * @param timeSource
	 *            the Date from which the time is taken
	 * @param daySource
	 *            the Date from which the day is taken
	 * @return a new Date object
	 */
	Date spliceDate(Date daySource, Date timeSource) {
		Calendar c = new GregorianCalendar();
		c.setTime(daySource);

		/*
		 * Get the day and year from daySource.
		 */
		int sourceYear = c.get(Calendar.YEAR);
		int sourceDay = c.get(Calendar.DAY_OF_YEAR);

		/*
		 * Load the timeSource and set the day and year of the daySource.
		 */
		c.setTime(timeSource);
		c.set(Calendar.YEAR, sourceYear);
		c.set(Calendar.DAY_OF_YEAR, sourceDay);

		return c.getTime();
	}

	private JMenu createMenu(String name, Action[] actions) {
		JMenu result = new JMenu(name);
		for (int i = 0; i < actions.length; ++i) {
			result.add(actions[i]);
		}

		return result;

	}
	
	public JMenu createMenuFromResource(String resourceKey) {
		return createMenuFromResource(resourceKey, new Action[0]);
	}

	public JMenu createMenuFromResource(String resourceKey, Action[] actions) {
		String name = getString(resourceKey);
		JMenu result = new JMenu(name);
		for (int i = 0; i < actions.length; ++i) {
			result.add(actions[i]);
		}
		try {
			char mnemonic = getMnemonic(resourceKey);
			result.setMnemonic(mnemonic);
			return result;
		} catch (MissingResourceException e) {
			return result;
		}
	}

	public int getMnemonicAsInt(String resourceKey) {
		return new Integer(getMnemonic(resourceKey));
	}

	private char getMnemonic(String resourceKey) {
		String mnemonicStr = m_resources.getString(resourceKey + MNEMONIC_RESOURCE_SUFFIX);
		char mnemonic = mnemonicStr.charAt(0);
		return mnemonic;
	}

	/**
	 * Returns the localized string with the given key.
	 */
	public String getString(String resourceKey) {
		String result = null;
		try {
			result = m_resources.getString(resourceKey);
		} catch (MissingResourceException e) {
			// Allow result to remain null, handled below.
			getLogger().debug(e);
		}
		return result == null ? "[MISSING: " + resourceKey + "]" : result;
	}

	public String getString(String resourceKey, String[] args) {
		String unformattedMsg = getString(resourceKey);
		return MessageFormat.format(unformattedMsg, (Object[]) args);
	}

	public void showError(Component parent, String key, String[] args) {
		String message = getString(key, args);
		JOptionPane pane = new WrappedOptionPane();
		pane.setMessage(message);
		pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		JDialog dialog = pane.createDialog(parent,
				getString(Strings.ERROR_DIALOG_TITLE));
		dialog.setVisible(true);
	}

	public void showError(Component parent, IHourglassException e) {
		showError(parent, e.getKey(), e.getArgs());
	}

	public int showConfirmation(Frame owner, String key, String[] args,
			int option) {
		JOptionPane pane = new WrappedOptionPane();
		pane.setMessage(getString(key, args));
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptionType(option);
		JDialog dialog = pane.createDialog(owner, "Confirm");
		dialog.setVisible(true);
		return ((Integer) pane.getValue()).intValue();
	}

	public String getFieldLabel(String resourceKey) {
		return getString(resourceKey) + ":";
	}

	private Logger getLogger() {
		if (m_logger == null) {
			m_logger = Logger.getLogger(getClass());
		}
		return m_logger;
	}

	/**
	 * Returns true iff the input string is all whitespace. Zero-length strings
	 * are considered all white space.
	 */
	boolean isAllWhitespace(String str) {
		return str.trim().equals("");
	}

	/**
	 * Returns a string truncated to the given number of characters, and with
	 * the trailer added on. If the string is less than or equal to the given
	 * number of characters, it is returned unmodified.
	 * 
	 * @param string
	 *            the string to format
	 * @param numChars
	 *            the maximum number of characters before truncation occurs.
	 * @param trailer
	 *            appended to the formatted string if truncation occurs.
	 * 
	 * @return the formatted string (if no formatting occurs, the result may be
	 *         either a new string or the original string object.
	 */
	String chopString(String string, int numChars, String trailer) {
		if (string.length() > numChars) {
			String result = string.substring(0, numChars);
			return result + trailer;
		} else {
			return string;
		}
	}

	/**
	 * Creates a path to the child from the parent, in the form of a list.
	 */
	public List createPathTo(Project parent, ProjectGroup group) {
		if (parent == null) {
			return new LinkedList();
		} else {
			List beginning = createPathTo(group.getParent(parent), group);
			beginning.add(parent);
			return beginning;
		}
	}

	private static class WrappedOptionPane extends JOptionPane {

		public int getMaxCharactersPerLineCount() {
			return Constants.ERROR_DIALOG_COLUMN_WIDTH;
		}
	}

	private ResourceBundle m_resources;

	private Logger m_logger;

	private static Utilities s_instance;
}
