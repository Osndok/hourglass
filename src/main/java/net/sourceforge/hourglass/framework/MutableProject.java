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
 * CVS Revision $Revision: 1.3 $
 * Last modified on $Date: 2003/04/15 05:37:31 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;


/**
 * Mutable extensions for projects.  Mutable projects should only be
 * used within the framework, and should not be returned to callers
 * outside the framework package.
 *
 * @author Mike Grant 
 */
public interface MutableProject extends Project {

  /**
   * Adds the given time span to the project.
   *
   * @param timeSpan the time span to add
   * @throws TimeSpanOverlapException if the timespan being added
   *         overlaps one already in the project.
   */
  void addTimeSpan(TimeSpan timeSpan);


  /**
   * Sets the name of this project.  May not be null.
   *
   * @throws IllegalArgumentException if name is null.
   */
  void setName(String name);
  

  /**
   * Sets the description of this project.  May not be null.
   *
   * @throws IllegalArgumentException if name is null.
   */
  void setDescription(String desc);


  /**
   * Removes the given timespan from the project.
   *
   * @param target the timespan to be removed.
   * @return true if the timespan was found, false otherwise.
   */
  boolean removeTimeSpan(TimeSpan target);


  /**
   * Adds a child project.
   */
  void addChildProject(Project p);


  /**
   * Removes a child project.
   */
  boolean removeChildProject(Project p);


  /**
   * Removes all child projects.
   */
  void clearChildren();

  
  /**
   * Sorts the children of the project by name.
   */
  void sortChildren();

}
