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
 * Last modified on $Date: 2008/10/25 15:22:52 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework.local;

import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectFactory;
import net.sourceforge.hourglass.framework.ProjectGroup;

import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * Creates local project objects and entities.
 *
 * @author Mike Grant
 */
public class LocalProjectFactory implements ProjectFactory {
  
  public Project createProject(ProjectGroup projectGroup) {
    return createProject(projectGroup, null, null);
  }

  public Project createProject(ProjectGroup projectGroup, String name, String description) {
    UUID id = UUID.randomUUID();
    return createProject(projectGroup, id, name, description);
  }

  public Project createProject(ProjectGroup projectGroup, UUID id) {
    return createProject(projectGroup, id, null, null);
  }


  public Project createProject(ProjectGroup projectGroup, UUID id, String name, String description) {
    _logger.debug("Creating project with id " + id);
    LocalProject result = new LocalProject(id);    
    result.setName((name == null) ? "" : name);
    result.setDescription((description == null) ? "" : description);
    result.setProjectGroup(projectGroup);

    return result;
  }


  public ProjectGroup createProjectGroup() {
    return new LocalProjectGroup();
  }


  private Logger _logger = Logger.getLogger(getClass());

}
