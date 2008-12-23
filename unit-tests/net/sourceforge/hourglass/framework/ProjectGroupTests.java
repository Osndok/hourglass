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
 * CVS Revision $Revision: 1.7 $
 * Last modified on $Date: 2008/10/25 15:22:52 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework;


import java.text.ParseException;

import net.sourceforge.hourglass.HourglassTestCase;

import net.sourceforge.hourglass.framework.local.LocalProjectFactory;

import java.util.UUID;


/**
 * Abstract unit tests for ProjectWriter.  Subclasses must supply an
 * implementation.
 *
 * @author Mike Grant 
 */
public abstract class ProjectGroupTests extends HourglassTestCase {


  protected abstract ProjectGroup createProjectGroup();


  public void setUp() {
    _emptyGroup = createProjectGroup();
    _projectGroup = createProjectGroup();
    ProjectFactory factory = new LocalProjectFactory();
    _projects = new Project[4];
    _ids = new UUID[4];

    for (int i = 0; i < 4; ++i) {
      _projects[i] = factory.createProject(_projectGroup);
      _ids[i] = _projects[i].getId();
    }

    _projectGroup.addProject(_projects[0], ProjectGroup.NO_PARENT);
    _projectGroup.addProject(_projects[1], ProjectGroup.NO_PARENT);
    _projectGroup.addProject(_projects[2], _projects[0]);
    _projectGroup.addProject(_projects[3], _projects[0]);

  }


  public void testGetParent() {
    assertEquals(_projects[0], _projectGroup.getParent(_projects[2]));
    assertEquals(_projectGroup.getRootProject(), 
                 _projectGroup.getParent(_projects[1]));
  }


  public void testInitialState() {
    assertNull(_emptyGroup.getProject(_ids[0]));
    assertNull(_emptyGroup.getProject(_ids[0]));
    assertFalse(_emptyGroup.removeProject(_ids[0]));
    assertFalse(_emptyGroup.removeProject(_ids[1]));
    assertFalse(_emptyGroup.removeProject(_projects[0]));
    assertFalse(_emptyGroup.removeProject(_projects[1]));
  }


  public void testAddRemove() {
    _emptyGroup.addProject(_projects[0], ProjectGroup.NO_PARENT);
    _emptyGroup.addProject(_projects[1], ProjectGroup.NO_PARENT);
    assertEquals(_projects[0], _emptyGroup.getProject(_ids[0]));
    assertEquals(_projects[1], _emptyGroup.getProject(_ids[1]));
    assertTrue(_emptyGroup.removeProject(_projects[0]));
    assertFalse(_emptyGroup.removeProject(_ids[0]));
    assertNull(_emptyGroup.getProject(_ids[0]));
    assertEquals(_projects[1], _emptyGroup.getProject(_ids[1]));
    assertTrue(_emptyGroup.removeProject(_ids[1]));
    assertFalse(_emptyGroup.removeProject(_projects[1]));
    assertNull(_emptyGroup.getProject(_ids[1]));
  }


  public void testParentUpdatedUponRemoval() {
    assertEquals(2, _projects[0].getChildren().size());
    _projectGroup.removeProject(_projects[3]);
    assertEquals(1, _projects[0].getChildren().size());

    assertEquals(2, _projectGroup.getRootProject().getChildren().size());
    _projectGroup.removeProject(_projects[1]);
    assertEquals(1, _projectGroup.getRootProject().getChildren().size());
  }


  public void testRootProject() {
    assertTrue
      (_projectGroup.getRootProject().getChildren().contains(_projects[0]));
    assertTrue
      (_projectGroup.getRootProject().getChildren().contains(_projects[1]));
    assertFalse
      (_projectGroup.getRootProject().getChildren().contains(_projects[2]));
    assertFalse
      (_projectGroup.getRootProject().getChildren().contains(_projects[3]));
  }


  public void testRecursiveRemoval() {
    assertEquals(2, _projects[0].getChildren().size());

    _projectGroup.removeProject(_projects[0]);

    assertFalse
      (_projectGroup.getRootProject().getChildren().contains(_projects[0]));
    assertTrue
      (_projectGroup.getRootProject().getChildren().contains(_projects[1]));
    assertFalse
      (_projectGroup.getRootProject().getChildren().contains(_projects[2]));
    assertFalse
      (_projectGroup.getRootProject().getChildren().contains(_projects[3]));

    assertEquals(0, _projects[0].getChildren().size());
  }

  public void testSetProjectProperties() {
    _projectGroup.setProjectName(_projects[0], "New Name");
    assertEquals("New Name", _projects[0].getName());
    _projectGroup.setProjectDescription(_projects[1], "New Desc.");
    assertEquals("New Desc.", _projects[1].getDescription());
  }


  public void testAddRemoveTimeSpans() throws ParseException {
    long origtime = _projects[0].getTotalTime(false);
    TimeSpan ts = new TimeSpan
      (createDate("2002-11-20T01:00:00.000"),
       createDate("2002-11-20T01:30:00.000"));
    long newtime = origtime + ts.getLength();

    _projectGroup.addTimeSpan(_projects[0], ts);
    assertEquals(newtime, _projects[0].getTotalTime(false));

    _projectGroup.removeTimeSpan(_projects[0], ts);
    assertEquals(origtime, _projects[0].getTotalTime(false));
  }    


  /**
   * Checks that an exception is thrown if we try to create a cycle.
   */
  public void testIllegalParent() {
    try {
      _projectGroup.setParent(_projects[0], _projects[0]);
      fail("Setting self as a parent doesn't throw IllegalParentException.");
    }
    catch (IllegalParentException ipex) {
      // should get here.
    }

    try {
      _projectGroup.setParent(_projects[0], _projects[2]);
      fail("Setting child as a parent doesn't throw IllegalParentException.");
    }
    catch (IllegalParentException ipex) {
      // should get here.
    }    
  }


  protected ProjectGroup _emptyGroup;
  protected ProjectGroup _projectGroup;

  protected Project[] _projects;
  protected UUID[] _ids;

}
