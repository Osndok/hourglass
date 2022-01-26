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
 * Last modified on $Date: 2008/10/25 15:22:52 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import net.sourceforge.hourglass.framework.ISO8601DateFormat;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Base test class for all Hourglass tests.
 *
 * @author Mike Grant
 */
public abstract class HourglassTestCase extends TestCase {
  
  public HourglassTestCase() {
    // Do nothing 
  }
  
  public HourglassTestCase(String name) {
    super(name);
  }

  
  public Date createDate(String date) throws ParseException {
    return getDateFormat().parse(date);
  }
  
  public Date createDate(String date, String timeZonePortion) throws ParseException {
  	return _isoDateFormat.parse(date + timeZonePortion);
  }

  public DateFormat getDateFormat() {
    if (_dateFormat == null) {
      _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }
    return _dateFormat;
  }
  
  /**
   * Returns the Log4J logger for this test case.
   */
  protected
  Logger getLogger() {
    if (_logger == null) {
      _logger = LogManager.getLogger(getClass());
    }
    return _logger;
  }


  /**
   * Asserts that the specified project exists and has the given properties.
   *
   * @param group a project group
   * @param id the project ID to check
   * @param name the expected project name
   * @param description the expected project description
   * @param totaltime the expected total time of the project in milliseconds
   */
  protected void assertProjectData(ProjectGroup group, UUID id, 
                                   String name, String description,
                                   long totaltime) {

    Project p = group.getProject(id);
    assertNotNull(p);

    assertEquals(name, p.getName());
    assertEquals(description, p.getDescription());
    assertEquals(totaltime, p.getTotalTime(false));
  }


  /**
   * Returns a set of sample data.
   *
   * Equivalent to TestUtilities.getInstance().getSampleData()
   */
  public ProjectGroup getSampleData() throws IOException, SAXException {
    return TestUtilities.getInstance().getSampleData();
  }


  /**
   * Returns a hierarchical set of sample data.
   *
   * Equivalent to TestUtilities.getInstance().getSampleDataHier()
   */
  public ProjectGroup getSampleDataHier() throws IOException, SAXException {
    return TestUtilities.getInstance().getSampleDataHier();
  }

  private Logger _logger;
  private DateFormat _dateFormat;
  private DateFormat _isoDateFormat = new ISO8601DateFormat();

}
