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


import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.HourglassTestCase;
import net.sourceforge.hourglass.TestUtilities;
import net.sourceforge.hourglass.framework.local.LocalProjectFactory;

import java.util.UUID;
import org.xml.sax.SAXException;

/**
 * Unit tests for ProjectWriter
 *
 * @author Mike Grant
 */
public class ProjectWriterTests extends HourglassTestCase {

  private static final String UNICODE_DESC = "Mixed String \u4e00\u4e00";

  public static Test suite() {
    TestSuite result = new TestSuite(ProjectWriterTests.class);
    result.setName("ProjectWriter Tests");

    return result;
  }
  

  /**
   * Tests saving and retrieving zero projects.
   */
  public void testWriteNoProjects() throws IOException, SAXException {
    ProjectGroup results = writeAndReadProjects
      (new net.sourceforge.hourglass.framework.local.LocalProjectGroup());
    assertEquals(0, results.getProjects().size());
  }


  /**
   * Tests saving and retrieving several projects.
   */
  public void testWriteProjects() throws IOException, SAXException {

    ProjectGroup results = getSampleData();
    ProjectGroup newResults = writeAndReadProjects(results);

    assertEquals(2, newResults.getProjects().size());
    assertProjectData(newResults, ID1,  "Project 1", 
                      "Project 1 Description", 369);
    
  }


  /**
   * Tests writing the projects hierarchically.  This is important
   * because if parents are not written before children the parser
   * will fail.
   */
  public void testWriteProjectsHier() throws IOException, SAXException {
    
    ProjectGroup results = getSampleDataHier();
    ProjectGroup newResults = writeAndReadProjects(results);

    assertEquals(5, newResults.getProjects().size());
    assertEquals(1, newResults.getRootProject().getChildren().size());
  }

  /**
   * Tests writing out projects containing '&', ';', etc.
   */
  public void testWriteReservedCharacters() 
    throws IOException, SAXException {

    doTestWriteReservedCharacters('&');
    doTestWriteReservedCharacters(';');
    doTestWriteReservedCharacters('\'');
    doTestWriteReservedCharacters('\"');
  }

  public void testReadWriteUnicode() throws IOException, SAXException {
  	ProjectGroup results = getSampleDataHier();
  	MutableProject p1 = (MutableProject) results.getProjects().iterator().next();
  	p1.setDescription(UNICODE_DESC);

  	results = writeAndReadProjects(results);
  	p1 = (MutableProject) results.getProjects().iterator().next();
  	assertEquals(UNICODE_DESC, p1.getDescription());
  }

  /**
   * Tests writing out projects containing ch.
   */
  public void doTestWriteReservedCharacters(char ch) 
    throws IOException, SAXException {

    ProjectGroup results = TestUtilities.getInstance().createNewSampleData();
    Project p1 = results.getProject(ID1);
    String name = "text" + ch + "text";
    String desc = name + "desc";

    results.setProjectName(p1, name);               
    results.setProjectDescription(p1, desc);

    ProjectGroup newResults = writeAndReadProjects(results);
    assertEquals(2, newResults.getProjects().size());
    assertProjectData(newResults, ID1, name, desc, 369);
  }
    


  /**
   * Writes the given projects to a pipe, and parses them back in.
   */
  private ProjectGroup writeAndReadProjects(ProjectGroup group)
    throws IOException, SAXException {

    PipedOutputStream out = new PipedOutputStream();
    final PipedInputStream in = new PipedInputStream(out);

    ProjectWriter writer = new ProjectWriter(out);
    _parsedGroup = null;

    /*
     * Start a reader thread so the pipe doesn't fill up.
     */
    Thread t = new Thread() {
        public void run() {
          try {
            ProjectParser parser = 
              new ProjectParser(new LocalProjectFactory(), true);
            _parsedGroup = parser.parse(in);
          }
          catch (Exception e) {
            getLogger().error("Error in reader thread.", e);
            fail("Exception in reader thread" + e);
          }
        }
      };
    t.start();

    writer.write(group);
    writer.close();

    try {
      t.join();
    }
    catch (InterruptedException iex) {
      fail("Reader thread interrupted.");
    }

    return _parsedGroup;
  }


  private ProjectGroup _parsedGroup;

  private static final UUID ID1 = 
    UUID.fromString("66a04623-056e-11d7-b289-e0cd170f00c2");
}
