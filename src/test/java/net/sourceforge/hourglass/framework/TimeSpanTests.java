/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Portions Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.HourglassTestCase;

/**
 * Unit tests for TimeSpan
 *
 * @author Mike Grant
 * @author Neil Thier
 */
public class TimeSpanTests extends HourglassTestCase {

  public static Test suite() {
    TestSuite result = new TestSuite(TimeSpanTests.class);
    result.setName("TimeSpan tests");

    return result;
  }


  public void setUp() throws Exception {

    _dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSS");
   
    _ts1 = new TimeSpan(_dateFormat.parse("11/20/2002 23:34:56.129"),
                        _dateFormat.parse("11/21/2002 01:12:31.999"));
    _ts2 = new TimeSpan(_dateFormat.parse("11/20/2002 23:34:56.129"),
                        _dateFormat.parse("11/21/2002 01:12:31.998"));
    _ts3 = new TimeSpan(_dateFormat.parse("11/20/2002 23:34:56.128"),
                        _dateFormat.parse("11/21/2002 01:12:31.999"));
    _ts4 = new TimeSpan(_dateFormat.parse("11/20/2002 23:34:56.128"),
                        _dateFormat.parse("11/21/2002 01:12:31.999"));    
  }


  public void tearDown() {
    _ts1 = null;
    _ts2 = null;
    _ts3 = null;
    _ts4 = null;
  }

  /**
   * Tests that null dates can't be passed to the constructor.
   */
  public void testConstructorArguments() {
    
    try {
      new TimeSpan(new Date(), null);
      fail("Didn't throw IllegalArgumentException for null dates.");
    }
    catch (IllegalArgumentException iae) {
      /* Pass */
    }

    try {
      new TimeSpan(null, new Date());
      fail("Didn't throw IllegalArgumentException for null dates.");
    }
    catch (IllegalArgumentException iae) {
      /* Pass */
    }
  } 


  /**
   * Tests that the end time must be >= than the start time.
   */
  public void testPositiveInterval() throws Exception {

    try {
      new TimeSpan(_dateFormat.parse("11/20/2002 12:34:56.012"),
                   _dateFormat.parse("11/20/2002 12:34:56.011"));
      fail("Didn't throw IllegalArgumentException for negative interval");
    }
    catch (IllegalArgumentException iae) {
      /* Pass */
    }
  }

  /**
   * Tests the ability to compute the span length.
   */
  public void testLength() {
    assertEquals(5855870, _ts1.getLength());
  }

  /**
   * Tests the equals() method.
   */
  public void testEquals() {
    assertTrue(_ts1.equals(_ts1));
    assertTrue(_ts3.equals(_ts4));
    assertTrue(_ts4.equals(_ts3));
    assertFalse(_ts1.equals(_ts2));
    assertFalse(_ts2.equals(_ts3));
    assertFalse(_ts1.equals("Mike"));
    assertFalse("Mike".equals(_ts1));
  }

  /**
   * Tests the compareTo() method.
   */
  public void testCompareTo() {
    assertTrue(_ts1.compareTo(_ts1) == 0);
    assertTrue(_ts3.compareTo(_ts4) == 0);
    assertTrue(_ts4.compareTo(_ts3) == 0);

    assertTrue(_ts1.compareTo(_ts2) > 0);
    assertTrue(_ts2.compareTo(_ts1) < 0);

    assertTrue(_ts2.compareTo(_ts3) > 0);
    assertTrue(_ts3.compareTo(_ts2) < 0);
  }

  /**
   * Tests the isFromDate() method
   */
  public void testIsFromDate() throws Exception {
    assertTrue(_ts1.isFromDate(_dateFormat.parse("11/20/2002 23:34:55.129")));
    assertTrue(_ts1.isFromDate(_dateFormat.parse("11/20/2002 11:34:56.129")));
    assertTrue(_ts1.isFromDate(_dateFormat.parse("11/21/2002 03:34:57.129")));
    assertFalse(_ts1.isFromDate(_dateFormat.parse("11/19/2002 03:34:57.129")));
    assertFalse(_ts1.isFromDate(_dateFormat.parse("11/22/2002 03:34:57.129")));
  }

  /**
   * Tests the hashcode() method.
   */
  public void testHashCode() {
    assertTrue(_ts3.hashCode() == _ts4.hashCode());
    assertTrue(_ts2.hashCode() + " != " + _ts3.hashCode(),
               _ts2.hashCode() != _ts3.hashCode());
  }

  private DateFormat _dateFormat;

  private TimeSpan _ts1;
  private TimeSpan _ts2;
  private TimeSpan _ts3;
  private TimeSpan _ts4;
}
