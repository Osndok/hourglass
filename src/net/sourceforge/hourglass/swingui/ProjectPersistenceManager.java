/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
 * Portions Copyright (C) 2003 Mike Grant <mike@acm.jhu.edu>
 * Copyright (C) 2008 Eric Lavarde <ewl@users.sourceforge.net>
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
 * CVS Revision $Revision: 1.10 $
 * Last modified on $Date: 2005/05/07 18:36:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import net.sourceforge.hourglass.Constants;
import net.sourceforge.hourglass.framework.HourglassException;
import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.framework.LockManager;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.ProjectParser;
import net.sourceforge.hourglass.framework.ProjectWriter;
import net.sourceforge.hourglass.framework.Utilities;
import net.sourceforge.hourglass.framework.local.LocalProjectFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * This class handles the saving and loading of the project file.
 *
 * @author Neil Thier
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class ProjectPersistenceManager 
  implements Constants, TimerListener, HourglassPreferences.Listener {


  /**
   * Creates a new persistence manager for the given archive.  The persistence
   * manager will be backed by the file HOURGLASS_DIR/&lt;archive&gt;.xml.  A
   * lock is obtained for the archive, and when the persistence manager is no
   * longer being used, {@link #releaseBackingFile()} should be called to release
   * the lock on the backing file.
   * 
   * @param archiveName the name of the archive
   * @throws HourglassException if there is a problem locking or creating the archive
   */
  public ProjectPersistenceManager(String archiveName) throws HourglassException {
    _archiveName = archiveName;
    _file = LockManager.getInstance().lockArchive(_archiveName);
    ClientState.getInstance().getTimer().addTimerListener(this);
    HourglassPreferences.getInstance().listenAutosavingEnable(this);
    HourglassPreferences.getInstance().listenAutosavingIntervalMinutes(this);
    HourglassPreferences.getInstance().listenBackupsNumber(this);
    refreshPreferences();
  }

  public void releaseBackingFile() {
    LockManager.getInstance().unlockArchive(_archiveName);
  }

  /**
   * Loads the persistent project data into the client state.
   */
  public ProjectGroup load() throws SAXException, IOException {
    return loadProjects(getFile());
  }


  /**
   * Loads data from the given file.
   *
   * @param file the file from which to load data.
   */
  protected ProjectGroup loadProjects(File file) 
    throws SAXException, IOException {

    return loadProjects(new FileInputStream(file));
  }


  /**
   * Loads the persistent project data.
   */
  protected ProjectGroup loadProjects(InputStream is) 
    throws SAXException, IOException {

    return getParser().parse(is);
  }
  

  /**
   * Saves the project data from the client state to a file.
   *
   * Checks that the file written to disk is consistent with the data
   * in memory before overwriting the project file
   *
   * @param group the ProjectGroup to save.
   */
  public synchronized void save(ProjectGroup group)
    throws SAXException, IOException {
    
    backup(getFile());

    /*
     * Begin by writing the ProjectGroup to a temp file.
     */
    File tmpFile = File.createTempFile("hourglass", "xml");
    writeProjectFile(tmpFile, group);

    /*
     * Ensure that the temp file's signature matched that of the
     * projects in memory.  This validates that nothing went wrong
     * when we were writing the file.
     */
    ProjectGroup written = loadProjects(tmpFile);
    ProjectGroup existing = group;
    if (checkConsistency(written, existing)) {
      File old = getFile();
      if (old.exists()) {
        old.delete();
      }
      move(tmpFile, old);
    }
    else {
      throw new ProjectWriteException
        ("Unable to properly write the project file to disk.");
    }
  }


  /**
   * Checks if two projects maps are consistent.  Projects are defined
   * to be consistent if and only if their signatures match.
   *
   * @return true iff they are consistent
   */
  protected boolean checkConsistency(ProjectGroup g1, ProjectGroup g2) 
    throws IOException, SAXException {

    return generateSignature(g1) == generateSignature(g2);
  }

  
  /**
   * Generates a signature for a project group.
   *
   * The signature of a set of project is the sum of the hash codes of
   * the projects added to the sum of the total times of all the
   * projects.
   *
   * @return the project set signature
   */
  private long generateSignature(ProjectGroup projectGroup) {
    long sig = 0;
    Iterator i = projectGroup.getProjects().iterator();
    while (i.hasNext()) {
      Project p = (Project) i.next();
      getLogger().debug(p.getTotalTime(true) + "  " + p.hashCode());
      sig += p.getTotalTime(true);
      sig += p.hashCode();
    }
    getLogger().debug("Signature: " + sig);
    return sig;
  }


  /**
   * Writes the current set of projects (from ClientState) to the
   * given file.
   */
  protected void writeProjectFile(File file, ProjectGroup group) throws IOException {
    ProjectWriter writer = new ProjectWriter(new FileOutputStream(file));
    writer.write(group);
    writer.close();
  }


  protected void backup(File file) throws IOException {
	backup(file, 0);
  }

  /**
   * Backs up the current data file to getBackupFileName(filename).
   * 
   * @param file the backup file
   */
  protected void backup(File file, int num) throws IOException {
    File backup = getBackupFile(file, num);

    // if it already exists, move it to a backup file or delete it
    if (file.exists()) {
      File tmp = new File(file.getAbsolutePath());
      if ( num < _backupsNumber ) { // not enough backups created
        // if the backup file already exists, delete it
        if (backup.exists()) {
	  backup(backup, num+1);
        }
        move(tmp, backup);
      } else {
        tmp.delete();
      }
    }
  }


  /**
   * Returns the project parser.
   */
  private ProjectParser getParser() throws SAXException {
    if (_parser == null) {
      /*
       * Validation off for performance reasons.
       */
      _parser = new ProjectParser(new LocalProjectFactory(), 
                                  ProjectParser.VALIDATION_OFF);
    }
    return _parser;
  }


  /**
   * Returns a backup filename given the name of the real file.
   */
  protected String getBackupFileName(String fileName, int num) {
    if (num == 0) {
      return fileName + "~";
    } else {
      if (fileName.lastIndexOf('~') > 0) { // the filename contains a tilde
        return fileName.substring(0, fileName.lastIndexOf('~')) + "~" + num;
      } else {
        return fileName + "~" + num;
      }
    }
  }


  /**
   * Returns a backup file given the real file.
   */
  protected File getBackupFile(File file, int num) {
    return new File(getBackupFileName(file.getAbsolutePath(), num));
  }


  private Logger getLogger() {
    if (_logger == null) {
      _logger = Logger.getLogger(getClass());
    }
    return _logger;
  }


  /**
   * Moves a file.  If renameTo() doesn't work, fails over to copy and
   * delete.
   */
  private void move(File source, File target) throws IOException {
    boolean renameSucceeded = source.renameTo(target);
    if (!renameSucceeded) {
      Utilities.copy(source, target);
      source.delete();
    }
  }

  public synchronized void preferenceChanged(String preferenceId) {
    refreshPreferences();
  }

  private void refreshPreferences() {
    _isAutosaveEnabled = 
      HourglassPreferences.getInstance().getAutosavingEnable();
    _autosaveInterval = 
      HourglassPreferences.getInstance().getAutosavingIntervalMinutes();
    _minutesRemainingUntilAutosave = _autosaveInterval;
    _backupsNumber = 
      HourglassPreferences.getInstance().getBackupsNumber();
    getLogger().debug("isAutosaveEnabled: " + _isAutosaveEnabled);
  }

  public void minuteChanged() {
    // TODO: This functionality probably belongs somewhere else.
    if (_isAutosaveEnabled) {
      --_minutesRemainingUntilAutosave;
      getLogger().debug(
          _minutesRemainingUntilAutosave + " minutes until next autosave.");
      if (_minutesRemainingUntilAutosave <= 0) {
        try {
          save(ClientState.getInstance().getProjectGroup());
        }
        catch (Exception e) {
          getLogger().error("Could not autosave", e);
        }
        _minutesRemainingUntilAutosave = _autosaveInterval;
      }
    }
    else {
      getLogger().debug("autosave not enabled.");
    }
  }

  protected File getFile() {
    return _file;
  }
  
  public void secondChanged() { }
  public void hourChanged() { }
  public void dayChanged() { }


  private Logger _logger;
  private ProjectParser _parser;
  private File _file;
  private String _archiveName;

  private boolean _isAutosaveEnabled;
  private int _autosaveInterval;
  private int _backupsNumber;
  private int _minutesRemainingUntilAutosave;

}
