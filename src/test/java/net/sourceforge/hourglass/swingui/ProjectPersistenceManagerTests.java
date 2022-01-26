/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
 * Portions Copyright (C) 2003 Mike Grant <mike@acm.jhu.edu>
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
 * Last modified on $Date: 2005/03/06 00:25:34 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.HourglassTestCase;
import net.sourceforge.hourglass.framework.LockManager;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.TimeSpan;
import net.sourceforge.hourglass.framework.Utilities;


/**
 * Unit tests for ProjectPersistenceManager
 *
 * @author Neil Thier
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class ProjectPersistenceManagerTests 
    extends HourglassTestCase {


  public static Test suite() {
    TestSuite result = new TestSuite(ProjectPersistenceManagerTests.class);
    result.setName("ProjectPersistenceManager tests");
    return result;
  }


  public void setUp() throws Exception {
    assertFalse(LockManager.getInstance().isArchiveLocked("testArchive"));
    _pm = new ProjectPersistenceManager("testArchive");
    assertTrue(LockManager.getInstance().isArchiveLocked("testArchive"));
  }


  public void tearDown() {
    File f = _pm.getFile();
    _pm.releaseBackingFile();
    f.delete();    
  }


  /**
   * Tests the checkConsistency() method against resource resName
   */
  public void doTestCheckConsistency(String resName) throws Throwable {
    ProjectGroup g1 = _pm.loadProjects(getClass().getClassLoader()
        .getResourceAsStream(resName));
    ProjectGroup g2 = _pm.loadProjects(getClass().getClassLoader()
        .getResourceAsStream(resName));
    assertTrue(_pm.checkConsistency(g1, g2));
    Project p = (Project) g2.getProjects().iterator().next();
    g2.addTimeSpan(p, new TimeSpan(new Date(System.currentTimeMillis() - 10),
        new Date()));
    assertFalse(_pm.checkConsistency(g1, g2));
  }


  public void testCheckConsistency() throws Throwable {
    doTestCheckConsistency(XML_TEST_RESOURCE);
    doTestCheckConsistency(XML_TEST_RESOURCE_HIER);
  }
  
  public void doTestSaveLoad() throws Exception {
    final String archive = "testArchive2";
    ProjectPersistenceManager persistenceManager = null;
    
    try {
      InputStream sampleHier = getClass().getClassLoader().getResourceAsStream(XML_TEST_RESOURCE_HIER);
      File f = LockManager.getInstance().lockArchive(archive);
      FileOutputStream fout = new FileOutputStream(f);
      Utilities.copy(sampleHier, fout);
      fout.close();
      LockManager.getInstance().unlockArchive(archive);
      String initial = Utilities.getContentsAsString(f);
      
      persistenceManager = new ProjectPersistenceManager(archive);
      ProjectGroup pg = persistenceManager.load();
      persistenceManager.save(pg);
      String resaved = Utilities.getContentsAsString(f);
      
      assertEquals(initial, resaved);
    }
    finally {
      if (persistenceManager != null) {
        persistenceManager.releaseBackingFile();
      }
    }
    
  }

  /**
   * Tests the createBackupFileName() method.
   */
  public void testGetBackupFileName() {
    assertEquals("/home/mike/tmp.txt~", 
                 _pm.getBackupFileName("/home/mike/tmp.txt",0));
  }
  
  public void testAttributePersistence() throws Exception {
      ProjectGroup grp = getSampleDataHier();
      Project p1 = (Project) grp.getRootProject().getChildren().get(0);
      Project p2 = (Project) p1.getChildren().get(0);
      assertNotNull(p1);
      assertNotNull(p2);
      
      p1.setAttribute("domain1", "name1", "value1");
      p1.setAttribute("domain2", "name1", "value1");
      p1.setAttribute("domain2", "name2", "value2");
      
      p2.setAttribute("domain2", "name2", "value2");
      p2.setAttribute("domain2", "name3", "value3");
      p2.setAttribute("domain3", "name3", "value3");
      
      _pm.save(grp);
      
      ProjectGroup newGroup = _pm.load();
      Project p1_new = newGroup.getProject(p1.getId());
      Project p2_new = newGroup.getProject(p2.getId());
      
      assertEquals("value1", p1_new.getAttribute("domain1", "name1"));
      assertEquals("value1", p1_new.getAttribute("domain2", "name1"));
      assertEquals("value2", p1_new.getAttribute("domain2", "name2"));
      
      assertEquals("value2", p2_new.getAttribute("domain2", "name2"));
      assertEquals("value3", p2_new.getAttribute("domain2", "name3"));
      assertEquals("value3", p2_new.getAttribute("domain3", "name3"));
      
      assertNull(p1_new.getAttribute("domain1", "name2"));
      
  }


  public static String XML_TEST_RESOURCE =
    "net/sourceforge/hourglass/framework/sample.xml";

  public static String XML_TEST_RESOURCE_HIER =
    "net/sourceforge/hourglass/framework/sample_hier.xml";

  protected ProjectPersistenceManager _pm;
}
