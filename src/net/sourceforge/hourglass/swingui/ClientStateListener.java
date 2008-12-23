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
 * CVS Revision $Revision: 1.6 $
 * Last modified on $Date: 2005/05/08 00:16:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.util.Date;

import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.TimeSpan;

/**
 * A listener that reacts to changes in the ClientState
 *
 * @author Mike Grant
 */
public interface ClientStateListener {

  /**
   * Indicates that the set of projects has changed.
   *
   * @param projectGroup a reference to the updated project group.
   */
  void projectGroupChanged(ProjectGroup projectGroup);


  /**
   * Indicates that the currently active project has changed.
   *
   * @param activeProject the new active project
   */
  void selectedProjectChanged(Project activeProject);


  /**
   * Indicates that a new timespan has been started.
   */
  void newTimeSpanStarted(Date d);


  /**
   * Indicates that the current timespan has been stopped.
   * 
   * @param timeSpan the time span that has just been stopped.
   */
  void currentTimeSpanStopped(TimeSpan timeSpan);
  
  /**
   * Indicates that the current timespan has been aborted.
   */
  void currentTimeSpanAborted();

}
