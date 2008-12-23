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
 * Last modified on $Date: 2004/08/18 23:49:15 $ by $Author: mgrant79 $
 *
 */


package net.sourceforge.hourglass.acceptance.gui.framework;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;



/**
 *
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class BaseScreen {
	
	private JFCTestCase m_testCase;

	public BaseScreen(JFCTestCase testCase) {
		m_testCase = testCase;
	}
	
	protected TestHelper getHelper() {
		return getTestCase().getHelper();
	}
	
	protected JFCTestCase getTestCase() {
		return m_testCase;
	}
	
}
