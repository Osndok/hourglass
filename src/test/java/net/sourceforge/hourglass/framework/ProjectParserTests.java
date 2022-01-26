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
package net.sourceforge.hourglass.framework;

import net.sourceforge.hourglass.HourglassTestCase;
import net.sourceforge.hourglass.TestUtilities;

import net.sourceforge.hourglass.framework.local.LocalProjectFactory;

import java.io.InputStream;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.UUID;

/**
 * Unit tests for ProjectParser
 *
 * @author Mike Grant
 */
public class ProjectParserTests extends HourglassTestCase {


  public static Test suite() {
    TestSuite result = new TestSuite(ProjectParserTests.class);
    result.setName("ProjectParser Tests");

    return result;
  }


  public void setUp() throws Exception {
    _parser = new ProjectParser(new LocalProjectFactory(), true);
    InputStream is = TestUtilities.getInstance().getSampleStream();
    assertNotNull(is);
    InputStream isHier = TestUtilities.getInstance().getSampleStreamHier();
    assertNotNull(isHier);
    _results = _parser.parse(is);
    _resultsHier = _parser.parse(isHier);
  }


  public void testParseCount() {
    assertEquals(2, _results.getProjects().size());
  }


  public void testNoNullProjects() {
    Iterator i = _results.getProjects().iterator();
    while (i.hasNext()) {
      Object each = i.next();
      assertNotNull(each);
      assertTrue("Non-project element",
                 each instanceof Project);
    }
  }
    

  public void testData() {
    assertProjectData(_results, ID1, "Project 1", 
                      "Project 1 Description", 369);
  }


  /**
   * Tests that the parents are assigned correctly.
   */
  public void testChildren() {
    Project p1 = _resultsHier.getProject(ID_HIER_1);
    Project p2 = _resultsHier.getProject(ID_HIER_2);
    Project p3 = _resultsHier.getProject(ID_HIER_3);
    Project p4 = _resultsHier.getProject(ID_HIER_4);
    Project p5 = _resultsHier.getProject(ID_HIER_5);

    assertEquals(2, p1.getChildren().size());
    assertTrue(p1.getChildren().contains(p2));
    assertTrue(p1.getChildren().contains(p3));
    assertEquals(0, p2.getChildren().size());
    assertEquals(2, p3.getChildren().size());
    assertTrue(p3.getChildren().contains(p4));
    assertTrue(p3.getChildren().contains(p5));
    assertEquals(0, p4.getChildren().size());
    assertEquals(0, p5.getChildren().size());
  }


  /**
   * Tests that the description field is never null, even if it's not
   * specified by the XML.
   */
  public void testNoDescription() throws Exception {

    InputStream is = getClass().getClassLoader().getResourceAsStream
      ("net/sourceforge/hourglass/framework/sample_no_desc.xml");
    ProjectGroup results = _parser.parse(is);

    assertEquals(1, results.getProjects().size());
    Project p = (Project) results.getProjects().iterator().next();

    assertEquals("", p.getDescription());
  }


  private ProjectGroup _results;
  private ProjectGroup _resultsHier;
  private ProjectParser _parser;

  private static final UUID ID1 = 
    UUID.fromString("66a04623-056e-11d7-b289-e0cd170f00c2");

  private static final UUID ID_HIER_1 =
    UUID.fromString("66a04623-056e-11d7-b289-e0cd170f00c2");
  private static final UUID ID_HIER_2 =
    UUID.fromString("07502364-056f-11d7-b289-e0cd170f00c2");
  private static final UUID ID_HIER_3 =
    UUID.fromString("6cfdea2b-5d9c-11d7-8b65-9ca753b1b6a8");
  private static final UUID ID_HIER_4 =
    UUID.fromString("6d00341c-5d9c-11d7-8b65-9ca753b1b6a8");
  private static final UUID ID_HIER_5 =
    UUID.fromString("6d00f76d-5d9c-11d7-8b65-9ca753b1b6a8");
}
