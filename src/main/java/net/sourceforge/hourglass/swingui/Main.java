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
 * CVS Revision $Revision: 1.11 $
 * Last modified on $Date: 2005/05/07 18:36:29 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.io.File;
import java.util.TimeZone;

import net.sourceforge.hourglass.framework.HourglassException;
import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.framework.Prefs;
import net.sourceforge.hourglass.framework.Utilities;
import net.sourceforge.hourglass.plugins.PluginManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bootstraps the application.
 *
 * @author Mike Grant
 */
public class Main {

  /**
   * Runs the application.
   */
  public void run() {
    ensureHourglassDirExists();
    bootstrapGlobalPreferences();
    try {
      ProjectPersistenceManager persistenceManager = new ProjectPersistenceManager(
                                                           gp().getPath(Prefs.ARCHIVE_NAME));
      ClientState.getInstance().setPersistenceManager(persistenceManager);
      SummaryFrame sf = new SummaryFrame();
      ClientState.getInstance().setSummaryFrame(sf);

      PluginManager.initializePluginManager();
      sf.setVisible(true);
    }
    catch (HourglassException e) {
    	getLogger().debug("Error initializing hourglass.", e);
    	net.sourceforge.hourglass.swingui.Utilities.getInstance().showError(null, e);
    	System.exit(0);
    }
  }



/**
   * Bootstraps any preferences that are globally needed.
   */
  private void bootstrapGlobalPreferences() {
    if (!gp().getTimezoneUseDefault() && gp().getTimezone() != null) {
      TimeZone.setDefault(TimeZone.getTimeZone(gp().getTimezone()));
    }
  }


  /**
   * Ensures that the HOURGLASS_DIR directory exists.  If it does not
   * exist, it is created.
   */
  private void ensureHourglassDirExists() {
    File dir = gu().getHourglassDir();
    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  private
  Logger getLogger() {
    if (_logger == null) {
      _logger = LogManager.getLogger(getClass());
    }
    return _logger;
  }

  private static Utilities gu() {
	  return Utilities.getInstance();
  }

  private static HourglassPreferences gp() {
	  return HourglassPreferences.getInstance();
  }


  public static void main(String[] args) {
    new Main().run();
  }
  
  private Logger _logger;

}
