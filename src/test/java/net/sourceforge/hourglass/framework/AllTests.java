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
 * CVS Revision $Revision: 1.3 $
 * Last modified on $Date: 2004/02/27 05:31:05 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import net.sourceforge.hourglass.HourglassTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All tests for the framework package.
 */
public class AllTests extends HourglassTestCase {
  
  public static Test suite() {
    TestSuite result = new TestSuite("Framework tests");
    
    addSubpackageTests(result);
    addUnitTests(result);

    return result;
  }

  private static void addSubpackageTests(TestSuite suite) {
    suite.addTest(net.sourceforge.hourglass.framework.local.AllTests.suite());
  }

  private static void addUnitTests(TestSuite suite) {
    suite.addTest(ISO8601DateFormatTests.suite());
    suite.addTest(LockManagerTests.suite());
    suite.addTest(ProjectParserTests.suite());
    suite.addTest(ProjectWriterTests.suite());
    suite.addTest(TimeSpanTests.suite());
    suite.addTest(DateUtilitiesTests.suite());
  }
  
}
