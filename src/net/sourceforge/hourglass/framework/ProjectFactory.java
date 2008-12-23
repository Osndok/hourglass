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
 * CVS Revision $Revision: 1.5 $
 * Last modified on $Date: 2008/10/25 15:22:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework;

import java.util.UUID;

/**
 * A factory for creating projects, and project-related entities.
 *
 * @author Mike Grant
 */
public interface ProjectFactory {
  
  /**
   * Creates a new project and assigns it an ID.
   */
  Project createProject(ProjectGroup projectGroup);


  /**
   * Creates a new project with the given name and description.
   */
  Project createProject(ProjectGroup projectGroup, String name, String description);


  /**
   * Creates a new project with the given ID.
   */
  Project createProject(ProjectGroup projectGroup, UUID id);


  /**
   * Creates a new project with the given ID, name, and description.
   */
  Project createProject(ProjectGroup projectGroup, UUID id, String name, String description);


  /**
   * Creates a new project group.
   */
  ProjectGroup createProjectGroup();

}
