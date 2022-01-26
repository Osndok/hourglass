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
 * CVS Revision $Revision: 1.2 $
 * Last modified on $Date: 2003/04/16 02:54:55 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework.local;

import junit.framework.Test;
import junit.framework.TestSuite;

import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.ProjectGroupTests;


/**
 * Unit tests for LocalProjectGroup
 *
 * @author Mike Grant 
 */
public class LocalProjectGroupTests extends ProjectGroupTests {


  protected ProjectGroup createProjectGroup() {
    return new LocalProjectGroup();
  }


  public static Test suite() {
    TestSuite result = new TestSuite(LocalProjectGroupTests.class);
    result.setName("LocalProjectGroup Tests");

    return result;
  }
    

    /**
   * Tests the isSubproject() method.
   */
  public void testIsSubproject() {
    LocalProjectGroup lpg = (LocalProjectGroup) _projectGroup;
    for (int i = 0; i < 4; ++i) {
      assertTrue(lpg.isSubproject(_projects[i], _projects[i]));
      assertTrue(lpg.isSubproject(_projects[i], 
                                             lpg.getRootProject()));
      assertFalse(lpg.isSubproject(lpg.getRootProject(),
                                             _projects[i]));
    }

    assertTrue(lpg.isSubproject(_projects[2], _projects[0]));
    assertTrue(lpg.isSubproject(_projects[3], _projects[0]));

    assertFalse(lpg.isSubproject(_projects[1], _projects[0]));
    assertFalse(lpg.isSubproject(_projects[0], _projects[1]));
    assertFalse(lpg.isSubproject(_projects[2], _projects[1]));
    assertFalse(lpg.isSubproject(_projects[1], _projects[2]));
    assertFalse(lpg.isSubproject(_projects[3], _projects[1]));
    assertFalse(lpg.isSubproject(_projects[1], _projects[3]));
    assertFalse(lpg.isSubproject(_projects[3], _projects[2]));
    assertFalse(lpg.isSubproject(_projects[2], _projects[3]));

    assertTrue(lpg.isSubproject
                (lpg.getRootProject(), 
                 lpg.getRootProject()));

  }                 

}
