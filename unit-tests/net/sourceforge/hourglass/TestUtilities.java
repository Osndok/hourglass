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
 * CVS Revision $Revision: 1.4 $
 * Last modified on $Date: 2003/04/06 08:47:50 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass;

import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.ProjectParser;

import net.sourceforge.hourglass.framework.local.LocalProjectFactory;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;


/**
 * Common utility functions shared among the tests.
 *
 * @author Mike Grant
 */
public class TestUtilities {

  private TestUtilities() {
  }


  /**
   * Returns the single instance of TestUtilities.
   */
  public static TestUtilities getInstance() {
    if (__instance == null) {
      __instance = new TestUtilities();
    }
    return __instance;
  }


  /**
   * Returns an InputStream for XML_TEST_RESOURCE
   */
  public InputStream getSampleStream() {
    return getSampleStream(XML_TEST_RESOURCE);
  }


  /**
   * Returns an InputStream for resName.
   */
  private InputStream getSampleStream(String resName) {
    return getClass().getClassLoader().getResourceAsStream(resName);
  }


  /**
   * Returns an InputStream for XML_TEST_RESOURCE_HIER
   */
  public InputStream getSampleStreamHier() {
    return getSampleStream(XML_TEST_RESOURCE_HIER);
  }



  /**
   * Returns the sample data contained in XML_TEST_RESOURCE.
   *
   * Uses LocalProjectFactory to create projects.
   */
  public ProjectGroup getSampleData() throws IOException, SAXException {
    if (_sampleData == null) {
      _sampleData = createNewSampleData();
    }
    return _sampleData;
  }


  /**
   * Returns the sample data contained in XML_TEST_RESOURCE_HIER.
   *
   * Uses LocalProjectFactory to create projects.
   */
  public ProjectGroup getSampleDataHier() 
    throws IOException, SAXException {

    if (_sampleDataHier == null) {
      _sampleDataHier = createNewSampleDataHier();
    }
    return _sampleDataHier;
  }


  /**
   * Creates a new set of sample data from XML_TEST_RESOURCE;
   */
  public ProjectGroup createNewSampleData() 
    throws IOException, SAXException {

    ProjectParser parser = 
      new ProjectParser(new LocalProjectFactory(), false);
    return parser.parse(getSampleStream());
  }


  /**
   * Creates a new set of sample data from XML_TEST_RESOURCE_HIER.
   */
  public ProjectGroup createNewSampleDataHier() 
    throws IOException, SAXException {

    ProjectParser parser = 
      new ProjectParser(new LocalProjectFactory(), false);
    return parser.parse(getSampleStreamHier());
  }


  private static TestUtilities __instance;
  
  private ProjectGroup _sampleData;
  private ProjectGroup _sampleDataHier;

  public static final String XML_TEST_RESOURCE =
    "net/sourceforge/hourglass/framework/sample.xml";

  public static final String XML_TEST_RESOURCE_HIER =
    "net/sourceforge/hourglass/framework/sample_hier.xml";

}
