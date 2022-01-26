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
 * CVS Revision $Revision: 1.10 $
 * Last modified on $Date: 2008/10/25 15:22:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework;

import java.util.Collection;
import java.util.Iterator;

import java.util.UUID;


/**
 * Represents a group of projects.
 *
 * @author Mike Grant
 */
public interface ProjectGroup {


  /**
   * Adds a project to the group with the given parent.
   *
   * @param parent the parent project (null if p is to be a top-level
   *          project).  
   */
  void addProject(Project p, Project parent);


  /**
   * Adds a project to the group with the given parent.
   *
   * @param parentId the parent project ID (null if p is to be a
   *          top-level project). 
   */
  void addProject(Project p, UUID parentId);


  /**
   * Removes a project from the group.  This will also remove any
   * child projects.
   *
   * @return true if the project was remove, false if the project was
   *           not found in the group.  
   */
  boolean removeProject(Project p);


  /**
   * Removes the project with the given ID from the group.
   *
   * @return true if the project was remove, false if the project was
   *           not found in the group.
   */
  boolean removeProject(UUID projectId);


  /**
   * Returns the project with the given ID.
   */
  Project getProject(UUID projectId);


  /**
   * Return a Collection of all the projects.
   */
  Collection<Project> getProjects();


  /**
   * Sets the name of the given project.
   */
  void setProjectName(Project p, String name);


  /**
   * Sets the description of the given project.
   */
  void setProjectDescription(Project p, String description);


  /**
   * Adds a timespan to the given project.
   */
  void addTimeSpan(Project p, TimeSpan timespan);


  /**
   * Removes a timespan from the given project.
   */
  void removeTimeSpan(Project p, TimeSpan timespan);


  /**
   * Returns a ``meta-project'' representing the single root project
   * (the parent of the top-level projects).
   */
  Project getRootProject();


  /**
   * Returns the parent of the given project.  Returns the hidden
   * root project if called on a top-level proejct.
   */
  Project getParent(Project child);


  /**
   * Sets the parent of a project.
   *
   * @throws IllegalParentException if project's parent is set to an
   *           illegal value.  Examples of illegal values include
   *           making the project its own parent, and setting the
   *           parent to a child (i.e. creating any cycles).  
   */
  void setParent(Project child, Project parent) 
    throws IllegalParentException;
  
  /**
   * Returns whether child is a subproject of parent.
   */
  boolean isSubproject(Project child, Project parent);


  /**
   * Adds a ProjectGroupListener */
  void addProjectGroupListener(ProjectGroupListener l);


  /**
   * Removes a ProjectGroupListener
   */
  void removeProjectGroupListener(ProjectGroupListener l);
  
  /**
   * Returns the string attribute associated with a particular project.
   * 
   * @param project the project for which to retrieve the attribute
   * @param domain the attribute domain
   * @param name the attribute name
   */
  String getAttribute(Project project, String domain, String name);
  
  /**
   * Removes the string attribute associated with a particular project.
   * 
   * @param project the project for which to retrieve the attribute
   * @param domain the attribute domain
   * @param name the attribute name
   */
  boolean removeAttribute(Project project, String domain, String name);

  /**
   * Sets a string attribute associated with a particular project.
   * 
   * @param project the project for which to retrieve the attribute
   * @param domain the attribute domain
   * @param name the attribute name
   * @param value the attribute value
   */
  void setAttribute(Project project, String domain, String name, String value);
  
  /**
   * Returns all attribute domains for the given project.
   */
  Iterator getAttributeDomains(Project project);
  
  /**
   * Returns all attribute keys for the given project and domain.
   */
  Iterator getAttributeKeys(Project project, String domain);

  /**
   * Represents the lack of a parent project.  
   */
  public static final Project NO_PARENT = null;
  
}
