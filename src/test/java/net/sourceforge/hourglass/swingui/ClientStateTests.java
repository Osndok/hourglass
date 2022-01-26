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
 * Last modified on $Date: 2005/05/08 00:16:31 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import net.sourceforge.hourglass.HourglassTestCase;

import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.TimeSpan;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Unit tests for ClientState
 *
 * @author Mike Grant
 */
public class ClientStateTests 
  extends HourglassTestCase 
  implements ClientStateListener {

  public static Test suite() {
    TestSuite result = new TestSuite(ClientStateTests.class);
    result.setName("ClientState tests");

    return result;
  }

  public void setUp() throws Exception {
    _clientState = ClientState.createTestInstance();
    _clientState.addClientStateListener(this);
    _sampleData = getSampleData();
  }


  /**
   * Tests that setProjects works and fires the correct events.
   */
  public void testSetGetProjects() {
    assertFalse(_projectGroupChangedFired);
    _clientState.setProjectGroup(_sampleData);
    assertTrue(_projectGroupChangedFired);

    assertEquals(_sampleData, _clientState.getProjectGroup());
  }


  public void projectGroupChanged(ProjectGroup group) {
    _projectGroupChangedFired = true;
  }


  public void selectedProjectChanged(Project p) { }
  public void newTimeSpanStarted(Date d) { }
  public void currentTimeSpanStopped(TimeSpan ts) { }
  public void currentTimeSpanAborted() { }


  private ClientState _clientState;
  private boolean _projectGroupChangedFired;
  private ProjectGroup _sampleData;
}
