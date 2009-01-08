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
 * CVS Revision $Revision: 1.7 $
 * Last modified on $Date: 2008/11/13 21:08:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.Properties;

import net.sourceforge.hourglass.swingui.Strings;

import org.apache.log4j.Logger;


/**
 * Encapsulates setting and persisting preferences.
 */
public class HourglassPreferences {

  private HourglassPreferences() {
    _prefs = Preferences.userNodeForPackage(getClass());
    _listeners = new HashMap();

    // load the properties file containing preferences definitions
    _props = new Properties();
    URL propURL = getClass().getClassLoader().getResource(PREFERENCES_RESOURCE);
    try {
      _props.load(propURL.openStream());
    } catch (java.io.IOException e) {
	getLogger().error("Preferences couldn't be loaded from '"
                        + PREFERENCES_RESOURCE + "'.", e);
    }
  }


  public void setSaveAllChanges(boolean b) {
    putBoolean(Prefs.SAVE_ALL_CHANGES, b);
  }
  
  public boolean getSaveAllChanges() {
    return getBoolean(Prefs.SAVE_ALL_CHANGES);
  }

  public void listenSaveAllChanges(Listener listener) {
    addListener(listener, Prefs.SAVE_ALL_CHANGES);
  }


  public void setAutosavingEnable(boolean b) {
    putBoolean(Prefs.AUTOSAVING_ENABLE, b);
  }
  
  public boolean getAutosavingEnable() {
    return getBoolean(Prefs.AUTOSAVING_ENABLE);
  }

  public void listenAutosavingEnable(Listener listener) {
    addListener(listener, Prefs.AUTOSAVING_ENABLE);
  }


  public void setAutosavingIntervalMinutes(int interval) {
    putInt(Prefs.AUTOSAVING_INTERVAL_MINUTES, interval);
  }

  public int getAutosavingIntervalMinutes() {
    return getInt(Prefs.AUTOSAVING_INTERVAL_MINUTES);
  }

  public void listenAutosavingIntervalMinutes(Listener listener) {
    addListener(listener, Prefs.AUTOSAVING_INTERVAL_MINUTES);
  }


  public void setBackupsNumber(int backups) {
    putInt(Prefs.BACKUPS_NUMBER, backups);
  }

  public int getBackupsNumber() {
    return getInt(Prefs.BACKUPS_NUMBER);
  }

  public void listenBackupsNumber(Listener listener) {
    addListener(listener, Prefs.BACKUPS_NUMBER);
  }


  public void setTimezoneUseDefault(boolean b) {
    putBoolean(Prefs.TIMEZONE_USE_DEFAULT, b);
  }

  public boolean getTimezoneUseDefault() {
    return getBoolean(Prefs.TIMEZONE_USE_DEFAULT);
  }

  public void listenTimezoneUseDefault(Listener listener) {
    addListener(listener, Prefs.TIMEZONE_USE_DEFAULT);
  }


  public void setTimezone(String timeZone) {
    if (timeZone == null) {
      remove(Prefs.TIMEZONE);
    }
    else {
      putString(Prefs.TIMEZONE, timeZone);
    }
  }

  public String getTimezone() {
    if (getTimezoneUseDefault()) {
      return null;
    }
    else {
      return getString(Strings.PREFS_TIMEZONE);
    }
  }

  public void listenTimezone(Listener listener) {
    addListener(listener, Prefs.TIMEZONE);
  }


  public String getTimeFormatType() {
	String type = getString(Prefs.TIME_FORMAT_TYPE);
	if (type == null) { // we use the language's default format
		return gu().getString(Strings.TIME_FORMAT_DEFAULT);
	} else {
		return type;
	}
  }
  
  public void setTimeFormatType(String timeFormatType) {
  	putString(Prefs.TIME_FORMAT_TYPE, timeFormatType);
  }

  public void listenTimeFormatType(Listener listener) {
    addListener(listener, Prefs.TIME_FORMAT_TYPE);
  }


  public DateFormat createTimeFormat() {
      return new SimpleDateFormat(getTimeFormatString()); 
  }
  
  public String getTimeFormatString() {
    return Strings.TIME_FORMAT_24_HOUR.equals(getTimeFormatType()) ?
	"HH:mm" : "h:mm a";
  }

  public void putBoolean(String preferenceId, boolean value) {
    _prefs.putBoolean(preferenceId, value);
    firePreferenceChanged(preferenceId);
  }
  
 /** Gets a (mutable or immutable) preference value using the
 * {@link #getString(String) getString()} method and casting the result
 * to a boolean.
 * @param preferenceId the name of the preference to fetch.
 * @see #getString(String)
 */
  public boolean getBoolean(String preferenceId) {
	return (new Boolean(getString(preferenceId))).booleanValue();
  }
  
  public void putInt(String preferenceId, int value) {
    _prefs.putInt(preferenceId, value);
    firePreferenceChanged(preferenceId);
  }
  
 /** Gets a (mutable or immutable) preference value using the
 * {@link #getString(String) getString()} method and casting the result
 * to an int.
 * @param preferenceId the name of the preference to fetch.
 * @see #getString(String)
 */
  public int getInt(String preferenceId) {
	return (new Integer(getString(preferenceId))).intValue();
  }

  public void putString(String preferenceId, String value) {
    _prefs.put(preferenceId, value);
    firePreferenceChanged(preferenceId);
  }

 /** Gets a (mutable or immutable) preference value by searching
 * first in the System properties (adding the prefix "hg."), then in the
 * actual preferences, and finally in the default properties file.
 * @param preferenceId the name of the preference to fetch.
 * @return the string value corresponding to the given preference.
 * 	A value of null would point to a bug in the code.
 */
  public String getString(String preferenceId) {
	// the following call makes use of the default value of each get
	// resp. getProperty call.
	String val = System.getProperty("hg." + preferenceId,
		_prefs.get(preferenceId,
		_props.getProperty(preferenceId)
		));
	if (val == null) {
		getLogger().warn("Preference '" + preferenceId
			+ "' couldn't be retrieved.");
	}
	return val;
  }
  
 /** Gets a (mutable or immutable) preference value using the
 * {@link #getString(String) getString()} method replacing recursively
 * variables of the type ${pref} through their value either as a System
 * property or as a preference.
 * @param preferenceId the name of the preference to fetch.
 * @see #getString(String)
 */
  public String getPath(String preferenceId) {
	String path = getString(preferenceId);
	getLogger().debug("getPath - retrieved '" + path + "' for ID = '"
		+ preferenceId + "'.");
	if (path == null) return null;
	int indirect = getInt(Prefs.MAX_INDIRECTIONS); // avoid infinite loops
	// as long as we find variables in the string, we replace them through
	// their preference value, or through the corresponding System property.
	while (indirect >= 0 && path.matches(".*\\$\\{[\\w.]+}.*")) {
		indirect--; // avoid infinite loops
		String pref = path.substring(path.indexOf("${")+2,
					path.indexOf("}"));
		String val = getString(pref);
		if (val == null) val = System.getProperty(pref);
		if (val == null) {
			getLogger().warn("Variable '${" + pref
				+ "}' couldn't be resolved in preference '"
				+ preferenceId + "' = '" + path + "'.");
			return null;
		} else {
			getLogger().debug("getPath - All occurences of ${"
				+ pref + "} will be replaced through '"
				+ val + "'.");
			// the replace() construct is a ugly hack to avoid
			// issues with swallowed backslashes under Windows.
			path = path.replaceAll("\\$\\{" + pref + "}",
					val.replace(File.separatorChar,'/')
				).replace('/',File.separatorChar);
			getLogger().debug("getPath - Resulting path is '"
					+ path + "'.");
		}
	} // as long as we find variables in the string
	getLogger().debug("Preference '" + preferenceId + "' resolved to '" + path + "'.");
	return path;
  }

  public void remove(String preferenceId) {
      _prefs.remove(preferenceId);
      firePreferenceChanged(preferenceId);
  }

  public static HourglassPreferences getInstance() {
    return __instance;
  }

  private Logger getLogger() {
    if (_logger == null) {
      _logger = Logger.getLogger(getClass());
    }
    return _logger;
  }
  

  /**
   * Sets up the given HourglassPreferences.Listener to receive preference
   * change events for the specified preference IDs.
   */
  public void addListener(Listener listener, String[] preferenceIds) {
    for (int i = 0; i < preferenceIds.length; ++i) {
      addListener(listener, preferenceIds[ i ]);
    }
  }

  public void addListener(Listener listener, String preferenceId) {
      if (!_listeners.containsKey(preferenceId)) {
        _listeners.put(preferenceId, new LinkedList());
      }
      List list = (List) _listeners.get(preferenceId);
      list.add(listener);
  }

  
  public void firePreferenceChanged(String preferenceId) {
    List l = (List) _listeners.get(preferenceId);
    if (l != null) {
      Iterator i = l.iterator();
      while (i.hasNext()) {
        ((Listener) i.next()).preferenceChanged(preferenceId);
      }
    }
  }

  public Preferences getRootPreferencesNode() {
    return _prefs;
  }
  
  
  /**
   * Listener that receives events for preference changes.
   */
  public interface Listener {
    public void preferenceChanged(String preferenceId);
  }

  private static Utilities gu() {
	  return Utilities.getInstance();
  }

  private Preferences _prefs;
  private Logger _logger;
  private Map _listeners;
  private Properties _props;
  
  private final static String PREFERENCES_RESOURCE =
	"net/sourceforge/hourglass/framework/HourglassPreferences.properties";

  private static HourglassPreferences __instance =
    new HourglassPreferences();

}
