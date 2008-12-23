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
 * Last modified on $Date: 2005/05/09 04:02:50 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Action;
import javax.swing.JMenu;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.HourglassTestCase;

/**
 * Unit tests for Utilities
 *
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class UtilitiesTests extends HourglassTestCase {

  public void setUp() {
    _utilities = Utilities.createTestInstance();
    _dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS");
  }


  public static Test suite() {
    TestSuite result = new TestSuite(UtilitiesTests.class);
    result.setName("Utilities tests");
    
    return result;
  }


  /**
   * Tests the <code>spliceDate(Date, Date)</code> method.
   *
   * @see Utilities#spliceDate(Date, Date)
   */
  public void testSpliceDate() throws ParseException {
    Date source = _dateFormat.parse("11/20/2002 11:11:11.000");
    Date target = _dateFormat.parse("12/25/2003 23:34:56.129");
    Date expectedResult = _dateFormat.parse("11/20/2002 23:34:56.129");

    assertEquals(expectedResult, _utilities.spliceDate(source, target));
  }


  /**
   * Tests the isAllWhitespace() method.
   *
   * @see Utilities#isAllWhitespace(String)
   */
  public void testIsAllWhitespace() {
    assertTrue(_utilities.isAllWhitespace(" "));
    assertTrue(_utilities.isAllWhitespace(" \t\n\r "));
    assertTrue(_utilities.isAllWhitespace(""));
    assertFalse(_utilities.isAllWhitespace(" mike "));
    assertFalse(_utilities.isAllWhitespace(" \nmike\n"));
  }


  /**
   * Tests the chopString() method.
   *
   * @see Utilities#chopString(String, int, String)
   */
  public void testChopString() {
    assertEquals("Mike Grant", _utilities.chopString("Mike Grant", 80, ".."));
    assertEquals("Mike...", _utilities.chopString("Mike Grant", 4, "..."));
    assertEquals("Mike is cool", _utilities.chopString
                 ("Mike Grant", 5, "is cool"));
  }
  
  
  /**
   * Tests the getString() method and family.
   * 
   * @see Utilities#getString(String)
   * @see Utilities#getFieldLabel(String)
   */
  public void testGetString() {
    assertEquals("Test Value", _utilities.getString(Strings.UNIT_TEST_STRING));
    assertEquals("Test Value:", _utilities.getFieldLabel(Strings.UNIT_TEST_STRING));
    assertEquals("[MISSING: doesnotexist]", _utilities.getString("doesnotexist"));
  }
  
  public void testCreateMenuFromResource() {
  	JMenu result = _utilities.createMenuFromResource(Strings.UNIT_TEST_MENU1);
  	assertEquals("Menu1", result.getText());
  	assertEquals('M', (char) result.getMnemonic());
  	
  	result = _utilities.createMenuFromResource(Strings.UNIT_TEST_MENU2);
  	assertEquals("Menu2", result.getText());
  	assertEquals(0, result.getMnemonic());
  	
  	// Check our assumption that 0 means no mnemonic.
  	assertEquals(0, new JMenu().getMnemonic());
  }
                 
  private Utilities _utilities;
  private DateFormat _dateFormat;
}
