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
 * CVS Revision $Revision: 1.7 $
 * Last modified on $Date: 2008/11/13 21:08:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import net.sourceforge.hourglass.swingui.Strings;

import org.apache.log4j.Logger;


/**
 * Encapsulates setting and persisting preferences.
 * 
 * TODO [mkg 8/15/2003]: Remove custom accessors where possible.
 * TODO [mkg 8/15/2003]: Centralize default values.
 */
public class HourglassPreferences {

  private HourglassPreferences() {
    _prefs = Preferences.userNodeForPackage(getClass());
    _listeners = new HashMap();
  }


  public void setSaveAllChanges(boolean b) {
    putBoolean(Prefs.SAVE_ALL_CHANGES, b);
  }
  
  public boolean getSaveAllChanges() {
    return getBoolean(Prefs.SAVE_ALL_CHANGES, false);
  }

  public void listenSaveAllChanges(Listener listener) {
    addListener(listener, Prefs.SAVE_ALL_CHANGES);
  }


  public void setAutosavingEnable(boolean b) {
    putBoolean(Prefs.AUTOSAVING_ENABLE, b);
  }
  
  public boolean getAutosavingEnable() {
    return getBoolean(Prefs.AUTOSAVING_ENABLE, false);
  }

  public void listenAutosavingEnable(Listener listener) {
    addListener(listener, Prefs.AUTOSAVING_ENABLE);
  }


  public void setAutosavingIntervalMinutes(int interval) {
    putInt(Prefs.AUTOSAVING_INTERVAL_MINUTES, interval);
  }

  public int getAutosavingIntervalMinutes() {
    return getInt(Prefs.AUTOSAVING_INTERVAL_MINUTES, 30);
  }

  public void listenAutosavingIntervalMinutes(Listener listener) {
    addListener(listener, Prefs.AUTOSAVING_INTERVAL_MINUTES);
  }


  public void setTimezoneUseDefault(boolean b) {
    putBoolean(Prefs.TIMEZONE_USE_DEFAULT, b);
  }

  public boolean getTimezoneUseDefault() {
    return getBoolean(Prefs.TIMEZONE_USE_DEFAULT, true);
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
      return getString(Strings.PREFS_TIMEZONE, null);
    }
  }

  public void listenTimezone(Listener listener) {
    addListener(listener, Prefs.TIMEZONE);
  }


  public String getTimeFormatType() {
	  // TODO: use TIME_FORMAT_DEFAULT from localized properties
  	return getString(Prefs.TIME_FORMAT_TYPE, Strings.TIME_FORMAT_12_HOUR);
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
  
  public boolean getBoolean(String preferenceId, boolean defaultValue) {
    return _prefs.getBoolean(preferenceId, defaultValue);
  }
  
  public void putInt(String preferenceId, int value) {
    _prefs.putInt(preferenceId, value);
    firePreferenceChanged(preferenceId);
  }
  
  public int getInt(String preferenceId, int defaultValue) {
    return _prefs.getInt(preferenceId, defaultValue);
  }
  
  public void putString(String preferenceId, String value) {
    _prefs.put(preferenceId, value);
    firePreferenceChanged(preferenceId);
  }

  public String getString(String preferenceId, String defaultValue) {
    return _prefs.get(preferenceId, defaultValue);
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


  private Preferences _prefs;
  private Logger _logger;
  private Map _listeners;
  
  private static HourglassPreferences __instance =
    new HourglassPreferences();

}
