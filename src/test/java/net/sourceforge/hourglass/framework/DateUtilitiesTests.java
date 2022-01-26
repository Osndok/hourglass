/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
 * Portions Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
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
 * Last modified on $Date: 2004/06/27 07:51:44 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.HourglassTestCase;

/**
 * Unit tests for DateUtilities
 *
 * @author Neil Thier
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class DateUtilitiesTests extends HourglassTestCase {

  public static Test suite() {
    TestSuite result = new TestSuite(DateUtilitiesTests.class);
    result.setName("DateUtilities tests");
    return result;
  }


  public void setUp() throws Exception {
    _dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    _longDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
  }

  public void testGetBeginningOfDate() throws Exception {
    // normal test
    Date d1 = _longDateFormat.parse("2/26/2003 23:34");
    Date d1Result = _longDateFormat.parse("2/26/2003 00:00");
    // test passing the desired date
    Date d2 = _longDateFormat.parse("2/26/2003 00:00");
    Date d2Result = _longDateFormat.parse("2/26/2003 00:00");

    Date result = DateUtilities.getBeginningOfDay(d1);
    assertEquals(d1Result, result);
    result = DateUtilities.getBeginningOfDay(d2);
    assertEquals(d2Result, result);
  }
  
  public void testGetEndOfDate() throws Exception {
  	// Normal Test
  	Date d1 = _longDateFormat.parse("2/26/2003 23:34");
  	Date d1Result = getDateFormat().parse("2003-02-26T23:59:59.999");
  	
  	// Already have desired date
  	Date d2 = getDateFormat().parse("2003-02-26T23:59:59.999");
  	Date d2Result = getDateFormat().parse("2003-02-26T23:59:59.999");
  	
  	Date result = DateUtilities.getEndOfDay(d1);
  	assertEquals(d1Result, result);
  	result = DateUtilities.getEndOfDay(d2);
  	assertEquals(d2Result, result);
  }

  public void testGetLastOccurringDayOfWeek() throws Exception {
    Date d1 = _dateFormat.parse("10/21/2003");
    Date d1Result = _dateFormat.parse("10/19/2003");
    // test passing the desired date
    Date d2 = _dateFormat.parse("10/19/2003");
    Date d2Result = _dateFormat.parse("10/19/2003");
    // test crossing the day of week boundary (asking for last monday
    // when it's sunday)
    Date d3 = _dateFormat.parse("10/19/2003");
    Date d3Result = _dateFormat.parse("10/13/2003");

    Date result = DateUtilities.getMostRecentlyOccuringDay(d1, Calendar.SUNDAY);
    assertEquals(d1Result, result);
    result = DateUtilities.getMostRecentlyOccuringDay(d2, Calendar.SUNDAY);
    assertEquals(d2Result, result);
    result = DateUtilities.getMostRecentlyOccuringDay(d3, Calendar.MONDAY);
    assertEquals(d3Result, result);
  }

  public void testGetBeginningOfMonth() throws Exception {
    // normal test
    Date d1 = _dateFormat.parse("2/26/2003");
    Date d1Result = _dateFormat.parse("2/1/2003");
    // test passing the desired date
    Date d2 = _dateFormat.parse("2/1/2003");
    Date d2Result = _dateFormat.parse("2/1/2003");

    Date result = DateUtilities.getBeginningOfMonth(d1);
    assertTrue(d1Result.equals(result));
    result = DateUtilities.getBeginningOfMonth(d2);
    assertTrue(d2Result.equals(result));
  }

  public void testGetBeginningOfYear() throws Exception {
    // normal test
    Date d1 = _dateFormat.parse("2/26/2003");
    Date d1Result = _dateFormat.parse("1/1/2003");
    // test passing the desired date
    Date d2 = _dateFormat.parse("1/1/2003");
    Date d2Result = _dateFormat.parse("1/1/2003");

    Date result = DateUtilities.getBeginningOfYear(d1);
    assertEquals(d1Result, result);
    result = DateUtilities.getBeginningOfYear(d2);
    assertEquals(d2Result, result);
  }

  private DateFormat _dateFormat;
  private DateFormat _longDateFormat;
}
