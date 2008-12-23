/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Portions Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
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
 * CVS Revision $Revision: 1.13 $
 * Last modified on $Date: 2005/05/08 00:16:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectFactory;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.TimeSpan;
import net.sourceforge.hourglass.framework.TimeSpanOverlapException;
import net.sourceforge.hourglass.framework.local.LocalProjectFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * Represents the swing client's current state.
 *
 * @author Mike Grant
 */
public class ClientState implements HourglassPreferences.Listener {

  private ClientState() {
    _listeners = new ArrayList();
    _projectGroup = getProjectFactory().createProjectGroup();
    _timer = new Timer();
    _timer.start();
    HourglassPreferences.getInstance().addListener(this, new String[] {
      Strings.PREFS_PERSIST_ALL_CHANGES
    });
    refreshPreferences();
  }


  /**
   * Returns the single instance of ClientState.
   */
  public static ClientState getInstance() {
    if (__instance == null) {
      synchronized (ClientState.class) {
        if (__instance == null) {
          __instance = new ClientState();
        }
      }
    }
    return __instance;
  }


  /**
   * Creates a new instance of ClientState to be used for testing.
   *
   * This method should never be used outside of the unit tests for
   * ClientState
   */
  protected static ClientState createTestInstance() {
    return new ClientState();
  }  


  /**
   * Sets the current project group.
   */
  public void setProjectGroup(ProjectGroup group) {
    _projectGroup = group;
    fireProjectGroupChanged();
  } 

  
  /**
   * Returns the current set of projects.
   *
   * @return the current set of projects as a map keyed on ID.
   */
  public Collection getProjects() {
    return getProjectGroup().getProjects();
  }


  public ProjectGroup getProjectGroup() {
    return _projectGroup;
  }


  /**
   * Fires projectsChanged() in all listeners.
   */
  protected void fireProjectGroupChanged() {
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      ((ClientStateListener) i.next()).projectGroupChanged(_projectGroup);
    }
  }


  /**
   * Fires newTimeSpanStarted() in all listeners.
   */
  protected void fireNewTimeSpanStarted(Date d) {
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      ((ClientStateListener) i.next()).newTimeSpanStarted(d);
    }
  }


  /**
   * Fires currentTimeSpanStopped() in all listeners.
   */
  protected void fireCurrentTimeSpanStopped(TimeSpan current) {
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      ((ClientStateListener) i.next()).currentTimeSpanStopped(current);
    }
  }
  
  /**
   * Fires currentTimeSpanStopped() in all listeners.
   */
  protected void fireCurrentTimeSpanAborted() {
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      ((ClientStateListener) i.next()).currentTimeSpanAborted();
    }
  }

  /**
   * Fires activeProjectChanged() on all listeners.
   *
   * @param selectedProject the new active project.
   */
  protected void fireSelectedProjectChanged(Project selectedProject) {
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      ((ClientStateListener) i.next()).selectedProjectChanged(selectedProject);
    }
  }
  
  /**
   * Adds a ClientStateListener
   */
  public void addClientStateListener(ClientStateListener l) {
    _listeners.add(l);
  }


  /**
   * Removed a ClientStateListener
   */
  public void removeClientStateListener(ClientStateListener l) {
    _listeners.remove(l);
  }

  /**
   * Gets the project factory associated with the application.
   */
  public ProjectFactory getProjectFactory() {
    if (_projectFactory == null) {
      _projectFactory = new LocalProjectFactory();
    }
    return _projectFactory;
  }


  public Timer getTimer() {
    return _timer;
  }

  
  public void stopCleanly() {
    getLogger().debug("Stopping ClientState");
    getTimer().scheduleCleanStop();
    try {
      getTimer().join();
    }
    catch (InterruptedException e) {
      getLogger().warn("Timer interrupted while joining.", e);
    }
    getLogger().debug("Successfully joined Timer thread.");
    
    getPersistenceManager().releaseBackingFile();
    getLogger().debug("ClientState stopped cleanly.");
  }


  /**
   * Changed the currently selected project.
   */
  public void setSelectedProject(Project p) {
    m_selectedProject = p;
    fireSelectedProjectChanged(m_selectedProject);
  }


  /**
   * Returns the currently selected project.
   */
  public Project getSelectedProject() {
    return m_selectedProject;
  }


  /**
   * Begins a new timespan starting at the given time.
   */
  public void startNewTimeSpan(Date d) {
  	setRunningProject(getSelectedProject());
    m_openTimeSpanStart = d;
    fireNewTimeSpanStarted(m_openTimeSpanStart);
  }    


  /**
   * Begins a new timespan at the current time.
   */
  public void startNewTimeSpan() {
    startNewTimeSpan(new Date());
  }


  /**
   * Closes the current timespan at current time; enters it into the project.
   */
  public void stopCurrentTimeSpan() { 
    stopCurrentTimeSpan(new Date());
  }


  /**
	 * Closes the current timespan at given time; enters it into the project.
	 */
	public void stopCurrentTimeSpan(Date d) {
		Date start = m_openTimeSpanStart;
		m_openTimeSpanStart = null;
		TimeSpan ts = new TimeSpan(start, d);
		fireCurrentTimeSpanStopped(ts);
		try {
			getProjectGroup().addTimeSpan(getRunningProject(), ts);
			if (m_persistAllChanges) {
				getPersistenceManager().save(ClientState.getInstance().getProjectGroup());
			}
		}
		catch (TimeSpanOverlapException tsoex) {
			Utilities.getInstance().showError(getSummaryFrame(), tsoex);
			fireSelectedProjectChanged(getSelectedProject());
		}
		catch (SAXException e) {
			// TODO
			getLogger().error(e);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			getLogger().debug(e);
		}
		finally {
			setRunningProject(null);
		}
	}
  


	/**
	 * Resets the current running time span
	 */
	public void resetCurrentTimeSpan() {
		m_openTimeSpanStart = null;
	}


  /**
   * Returns whether or not the application is currently tracking time.
   */
  public boolean isRunning() {
    return m_openTimeSpanStart != null;
  }
  
  private void abortCurrentTimeSpan() {
  	m_openTimeSpanStart = null;
  	fireCurrentTimeSpanAborted();
  }


  /**
   * Toggles timing on or off.
   */
  public void toggleTimer() {
    if (isRunning()) {
      setSelectedProject(getRunningProject());
      stopCurrentTimeSpan();
    }
    else {
      startNewTimeSpan();
    }
  }
  
  public void removeProject(Project p) {
  	if (isRunning() && getProjectGroup().isSubproject(getRunningProject(), p)) {
  		// If we're removing a parent of the running project, abort the timer
  		// and clear the selection.
  		abortCurrentTimeSpan();
  		setSelectedProject(null);
  	}
  	else if (getProjectGroup().isSubproject(getSelectedProject(), p)) {
  		// If we're removing a parent of the selected project, change the selection
  		// to the running project
  		setSelectedProject(getRunningProject());
  	}
    getProjectGroup().removeProject(p);
  }
  
  public boolean isRunningProjectSelected() {
  	return getRunningProject().equals(getSelectedProject());
  }


  /**
   * Returns the time of the open time span, if any.
   *
   * Returns zero if no open time span.
   *
   * @return the time in ms
   */
  public long getOpenTimeSpanStartMillis() {
    return m_openTimeSpanStart.getTime();
  }
  
  public Date getOpenTimeSpanStart() {
  	return m_openTimeSpanStart;
  }

  private Logger getLogger() {
    if (_logger == null) {
      _logger = Logger.getLogger(getClass());
    }
    return _logger;
  }


  /**
   * Sets edit mode on or off.
   */
  public void setEditMode(boolean editMode) {
    _isEditMode = editMode;
  }


  /**
   * Returns whether or not we're in edit mode.
   */
  public boolean isEditMode() {
    return _isEditMode;
  }


  /**
   * Sets the SummaryFrame for the application.  This method should
   * generally be called once by Main at initialization-time.
   *
   * @param summaryFrame the SummaryFrame for the application
   */
  public void setSummaryFrame(SummaryFrame summaryFrame) {
    _summaryFrame = summaryFrame;
  }


  /**
   * Returns the SummaryFrame for the application.
   */
  public SummaryFrame getSummaryFrame() {
    return _summaryFrame;
  }


  /**
   * Replaces target TimeSpan with replacement in the active project.
   *
   * If the target is null, the replacement is appended.  If the
   * replacement is null, the target is deleted.  If both parameters
   * are null, an IllegalArgumentException is thrown.
   *
   * @param target  the timespan that is being replaced, or null if the
   *        replacement is to be appended.
   * @param replacement  the replacement timespan, or null if there is no
   *        replacement and the target is to be deleted.  
   * @throws IllegalArgumentException if target and replacement are
   *         both null.
   */
  public void replaceTimeSpan(TimeSpan target, TimeSpan replacement) {
    if (replacement == null && target == null) {
      throw new IllegalArgumentException
        ("Target and replacement are null.");
    }
    if (target != null) {
      getProjectGroup().removeTimeSpan(getSelectedProject(), target);
      getLogger().debug("Removing " + target);
    }
    if (replacement != null) {
      getProjectGroup().addTimeSpan(getSelectedProject(), replacement);
      getLogger().debug("Adding " + replacement);
    }
    fireSelectedProjectChanged(getSelectedProject());
  }
  
  
  public void preferenceChanged(String preferenceId) {
    refreshPreferences();
  }
  
  public void setRunningProject(Project p) {
  	m_runningProject = p;
  }
  
  public Project getRunningProject() {
  	return m_runningProject;
  }
  
  private void refreshPreferences() {
    m_persistAllChanges =
      HourglassPreferences.getInstance().getBoolean
	(Strings.PREFS_PERSIST_ALL_CHANGES,
	 false);
  }
  
  public ProjectPersistenceManager getPersistenceManager() {
    return _persistenceManager;
  }
  
  public void setPersistenceManager(ProjectPersistenceManager mgr) {
    _persistenceManager = mgr;
  }

  private SummaryFrame _summaryFrame;

  private ProjectGroup _projectGroup;
  private List _listeners;
  private ProjectFactory _projectFactory;
  private ProjectPersistenceManager _persistenceManager;
  private Timer _timer;
  private Project m_selectedProject;
  private Project m_runningProject;
  private Logger _logger;

  private boolean _isEditMode;
  private boolean m_persistAllChanges;
  private Date m_openTimeSpanStart;

  private static ClientState __instance;

}
