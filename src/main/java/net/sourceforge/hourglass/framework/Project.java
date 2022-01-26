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
 * CVS Revision $Revision: 1.12 $
 * Last modified on $Date: 2008/10/25 15:22:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.util.UUID;

/**
 * A project.  This interface defines an immutable project.
 * MutableProject extends this by adding methods for mutation.  Only
 * immutable projects should be exposed outside the framework.
 * Mutation to projects outside the framework should be done through
 * the ProjectGroup interface.
 *
 * @author Mike Grant 
 */
public interface Project {

  /**
   * Returns the unique ID of this project.
   */
  UUID getId();

  
  /**
   * Returns the cumulative time that has been put into the project.
   *
   * @param isRecursive true if calculation should recurse into
   *          subprojects, false if not.
   * @return the cumulative time in milliseconds
   */
  long getTotalTime(boolean isRecursive);

  
  /**
   * Returns the time put into the project since the specified date/time.
   *
   * @param isRecursive true if calculation should recurse into
   *          subprojects, false if not.
   * @return the time in milliseconds elapsed since the given date/time.
   */
  long getTimeSince(Date d, boolean isRecursive);
  
  long getTimeSince(long time, boolean isRecursive);


  /**
   * Returns the time put into the project between the two given
   * intervals (inclusive).
   *
   * @param isRecursive true if calculation should recurse into
   *          subprojects, false if not.
   * @return the time in milliseconds.
   */
  long getTimeBetween(Date start, Date end, boolean isRecursive);


  /**
   * Returns all the timespan objects in this project.
   *
   * @return an Set of timespans
   */
  Set<TimeSpan> getTimeSpans();


  /**
   * Returns the name of this project.  This may not be null.
   */
  String getName();


  /**
   * Returns the description of this project.  This may not be null.
   */
  String getDescription();


  /**
   * Returns a read-only list of the child projects.  
   */
  List<Project> getChildren();
  
  /**
   * Returns the group with which this project is associated.
   */
  ProjectGroup getProjectGroup();
  
  /**
   * Sets a string attribute on the project.
   * 
   * @param name the attribute name
   * @param domain the attribute domain
   * @param value the attribute value
   */
  void setAttribute(String domain, String name, String value);
  
  /**
   * Gets a string attribute from the project
   * 
   * @param domain the attribute domain
   * @param name the attribute name
   */
  String getAttribute(String domain, String name);
  
  /**
   * Get all attribute domains for this project.
   */
  Iterator getAttributeDomains();
  
  /**
   * Get all attributes for this domain.
   */
  Iterator getAttributeKeys(String domain);
  
  
  /**
   * Removes an attribute from the project
   * 
   * @param domain the attribute domain
   * @param name the attribute name
   */
  boolean removeAttribute(String domain, String name);
  
  
}
