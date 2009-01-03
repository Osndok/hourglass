/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004 Michael K. Grant <mike@acm.jhu.edu>
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
 * CVS Revision $Revision: 1.4 $
 * Last modified on $Date: 2005/05/07 18:36:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.io.File;
import java.io.IOException;

import net.sourceforge.hourglass.swingui.Strings;

/**
 * Manages locks for the application.
 * 
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class LockManager {
  
  private LockManager() {
    _hgDir = Utilities.getInstance().getHourglassDir();
  }
  
  public static LockManager getInstance() {
    return __instance;
  }
  
  public static LockManager createTestInstance(final File dir) {
    return new LockManager() {
      {
        this._hgDir = dir;
      }
    };
  }

  /**
   * Locks an archive.  The archive name corresponds to a file in HOURGLASS_DIR.
   * The filename used for the archive is of the form HOURGLASS_DIR/archive.DATAFILE_EXTENSION. 
   *  
   * @param archiveName the name of the arhive
   * @return the file corresponding to the archive.
   * @see #unlockArchive(String)
   * @see #isArchiveLocked(String)
   */
  public synchronized File lockArchive(String archiveName) throws HourglassException {
    File lockfile = getLockFileFor(archiveName);
    try {
      if (!lockfile.createNewFile()) {
        throw new HourglassException(Strings.ERROR_KEY_CANNOT_LOCK_ARCHIVE, 
            new String[] { archiveName, lockfile.getAbsolutePath() }); 
      }
    }
    catch (IOException e) {
      throw new HourglassException(e, Strings.ERROR_KEY_CANNOT_LOCK_ARCHIVE, 
          new String[] { archiveName, lockfile.getAbsolutePath() }); 
    }

    File result = new File(getHourglassDir(), getDataFileName(archiveName));
    try {
      if (!result.exists()) {
        result.createNewFile();
      }
      return result;
    } 
    catch (IOException e) {
      lockfile.delete();
      throw new HourglassException(e, Strings.ERROR_KEY_CANNOT_CREATE_NEW_ARCHIVE,
          new String[] { archiveName, result.getAbsolutePath() });
    }
  }
  
  /**
   * Unlocks the given archive
   * 
   * @param archiveName the name of the archive
   * @see #lockArchive(String)
   * @see #isArchiveLocked(String)
   */
  public synchronized void unlockArchive(String archiveName) {
    File lockfile = getLockFileFor(archiveName);
    lockfile.delete();
  }
  
  
  /**
   * Unlocks the given archive
   * 
   * @param archiveName the name of the archive
   * @see #lockArchive(String)
   * @see #unlockArchive(String)
   */
  public synchronized boolean isArchiveLocked(String archiveName) {
    File lockfile = getLockFileFor(archiveName);
    return lockfile.exists();
  }
  
  private File getLockFileFor(String archiveName) {
    return new File(getHourglassDir(), getLockFileName(archiveName));
  }

  private String getLockFileName(String archiveName) {
    return archiveName + LOCK_EXTENSION;
  }
  
  private String getDataFileName(String archiveName) {
    return archiveName + DATAFILE_EXTENSION;
  }
  
  private File getHourglassDir() {
  	return _hgDir;
  }
  
  File _hgDir;
  
  private static final LockManager __instance = new LockManager();
  private static final String DATAFILE_EXTENSION = ".xml";
  private static final String LOCK_EXTENSION = ".lock";

}
