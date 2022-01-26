/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Copyright (C) 2009 Eric Lavarde <ewl@users.sourceforge.net>
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
package net.sourceforge.hourglass;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.framework.Prefs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * BaseUtilities for the Swing UI
 * 
 * @author Mike Grant
 */
public class BaseUtilities {

	private static final String MNEMONIC_RESOURCE_SUFFIX = ".mnemonic";
	protected BaseUtilities() {
		initializeResourceBundle();
	}

	/**
	* Initializes the static variable m_resources with the resource bundle necessary for
	* translation strings.
	*/
	private static void initializeResourceBundle() {
		if (m_resources == null) {
			synchronized(BaseUtilities.class) {
				if (m_resources == null) {
					m_resources = ResourceBundle.getBundle(
						gp().getString(Prefs.TRANSLATION_RESOURCE_BUNDLE));
				}
			}
		}
	}

	/**
	 * Returns the sole instance of BaseUtilities.
	 */
	public static BaseUtilities getInstance() {
		if (b_instance == null) {
			b_instance = new BaseUtilities();
		}
		return b_instance;
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

	public char getChar(String resourceKey) {
		String result = null;
		try {
			result = m_resources.getString(resourceKey);
		} catch (MissingResourceException e) {
			// Allow result to remain null, handled below.
			getLogger().debug(e);
		}
		return result == null ? 0 : result.charAt(0);
	}

	/**
	* Copies a file.
	*/
	public static void copy(File source, File target) throws IOException {
		FileInputStream fin = new FileInputStream(source);
		FileOutputStream fout = new FileOutputStream(target);
		copy(fin, fout);
		fin.close();
		fout.close();
	}

	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[2048];
		int read = 0;

		while ((read = in.read(buf)) > 0) {
			out.write(buf, 0, read);
		}
	}

	private
	Logger getLogger() {
		if (m_logger == null) {
			m_logger = LogManager.getLogger(getClass());
		}
		return m_logger;
	}

	private static HourglassPreferences gp() {
		return HourglassPreferences.getInstance();
	}

	private static ResourceBundle m_resources;

	private Logger m_logger;

	private static BaseUtilities b_instance;
}
