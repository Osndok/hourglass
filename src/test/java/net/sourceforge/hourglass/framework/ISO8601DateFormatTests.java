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
 * Last modified on $Date: 2003/10/27 06:17:46 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import net.sourceforge.hourglass.HourglassTestCase;

import java.text.ParseException;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Unit tests for the ISO8601DateFormat
 *
 * @author Mike Grant
 */
public class ISO8601DateFormatTests extends HourglassTestCase {

  public static Test suite() {
    TestSuite result = new TestSuite(ISO8601DateFormatTests.class);
    result.setName("ISO8601DateFormat Tests");

    return result;
  }


  public void setUp() {
    _dateFormat = new ISO8601DateFormat();
  }


  /**
   * Formats a date, then parses it back, checking it with the
   * original.  We don't want to test formatting directly, because it
   * would be timezone-specific.  This is a good compromise.
   */
  public void testFormatAndParse() throws ParseException {
    Date d = new Date();
    assertEquals(d, _dateFormat.parse(_dateFormat.format(d)));
  }

  /**
   * Tests parsing a date in several timezones.
   */
  public void testParse() throws ParseException {
    assertEquals(DATE_OBJ, _dateFormat.parse(DATE_STRING_CEN));
    assertEquals(DATE_OBJ, _dateFormat.parse(DATE_STRING_MTN));
    assertEquals(DATE_OBJ, _dateFormat.parse(DATE_STRING_PAC));
    assertEquals(DATE_OBJ, _dateFormat.parse(DATE_STRING_GMT));
    assertEquals(DATE_OBJ, _dateFormat.parse(DATE_STRING_GMT2));
  }

  private ISO8601DateFormat _dateFormat;

  private static final long DATE_MSEC = 1041967860767L;
  private static final String DATE_STRING_CEN = "2003-01-07T13:31:00.767-06:00";
  private static final String DATE_STRING_MTN = "2003-01-07T12:31:00.767-07:00";
  private static final String DATE_STRING_PAC = "2003-01-07T11:31:00.767-08:00";
  private static final String DATE_STRING_GMT = "2003-01-07T19:31:00.767+00:00";
  private static final String DATE_STRING_GMT2 = "2003-01-07T19:31:00.767-00:00";
  private static final Date DATE_OBJ = new Date(DATE_MSEC);
}
