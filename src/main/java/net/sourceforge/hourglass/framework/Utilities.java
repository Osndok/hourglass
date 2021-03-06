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
 * CVS Revision $Revision: 1.8 $
 * Last modified on $Date: 2005/05/07 18:36:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Comparator;

import net.sourceforge.hourglass.BaseUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Utilities for the Hourglass framework package.
 *
 * @author Mike Grant
 */
public class Utilities extends BaseUtilities {


  private Utilities() { super(); }


  /**
   * Returns the single instance of Utilities
   */
  public static Utilities getInstance() {
    if (s_instance == null) {
      synchronized(Utilities.class) {
        if (s_instance == null) {
          s_instance = new Utilities();
        }
      }
    }
    return s_instance;
  }


  /**
   * Creates an instance of the date format used by Hourglass
   */
  public DateFormat createDateFormat() {
    return new ISO8601DateFormat();
  }


  /**
   * Returns a comparator that orders projects by name.  Null is
   * considered the least value.
   */
  public Comparator getProjectNameComparator() {
    if (m_comparator == null) {
      m_comparator = new Comparator() {
          public int compare(Object o1, Object o2) {
          	
          	Project p1 = (Project) o1;
          	Project p2 = (Project) o2;

            if (p1.getName() == null) {
              return (p2.getName() == null) ? 0 : -1;
            }
            else if (p2.getName() == null) {
              return 1;
            }
            else {
              return p1.getName().compareTo(p2.getName());
            }
          }
        };
    }
    return m_comparator;
  }          
  
  public static File createTempDirectory() throws IOException {
    File file = File.createTempFile("hourglass", null);
    s_logger.debug("Creating temporary directory " + file.getAbsolutePath());
    file.delete();
    file.mkdirs();
    file.deleteOnExit();
    return file;    
  }
  
  public static void deleteRecursive(File f) {
    if (f.isDirectory()) {
      File[] files = f.listFiles();
      for (int i = 0; i < files.length; ++i) {
        deleteRecursive(files[i]);
      }
    }
    f.delete();
  }
  
  public static String join(String[] strings, String joinString) {
    StringBuffer result = new StringBuffer(strings[0]);
    for (int i = 1; i < strings.length; ++i) {
      result.append(joinString).append(strings[i]);
    }
    return result.toString();
  }


  public File getHourglassDir() {
  	if (m_hourglassDir == null) {
		m_hourglassDir = new File(gp().getPath(Prefs.HOURGLASS_DIR));
	}

  	return m_hourglassDir;
  }
  
  
  
  public static String getContentsAsString(File f) throws IOException {
    StringBuffer result = new StringBuffer();
    BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
    String s;
    while ((s = b.readLine()) != null) {
      result.append(s).append(System.getProperty("line.separator"));
    }
    return result.toString();
  }

  private static HourglassPreferences gp() {
	  return HourglassPreferences.getInstance();
  }

  private Comparator m_comparator;
  private File m_hourglassDir;

  private static Utilities s_instance;
  private static final Logger s_logger = LogManager.getLogger(Utilities.class);

}
